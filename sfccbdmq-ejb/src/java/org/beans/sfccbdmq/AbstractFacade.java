/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Maestros;

/**
 *
 * @author edwin
 * @param <T>
 */
public abstract class AbstractFacade<T> {
    

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
//    @EJB private EventosFacade ejbLogs;

    protected abstract EntityManager getEntityManager();

    protected abstract String modificarObjetos(T nuevo);
    @Resource(mappedName = "jms/EventosFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/Eventos")
    private Queue queue;
//

    public void create(T entity, String usuario) throws InsertarException {
        try {

            getEntityManager().persist(entity);
//            log(modificarObjetos(entity), "I", entityClass.getSimpleName(), usuario);
//            ejbEventos.crearAuditoria(entityClass.getName(), insertarObjetos(entity), usuario, "I");
            //sendJMSMessageToColadeNotificacion(entity);
        } catch (Exception e) {
            throw new InsertarException(entity.toString(), e);
        } finally {
            Logger.getLogger(entityClass.getName()).log(Level.INFO, "Entidad Creada:{0}", entity.toString());
        }
        log(modificarObjetos(entity), "I", entityClass.getSimpleName(), usuario);
    }

    public void edit(T entity, String usuario) throws GrabarException {
        try {
//            ejbEventos.crearAuditoria(entityClass.getName(), modificarObjetos(entity), usuario, "M");
            log(modificarObjetos(entity), "M", entityClass.getSimpleName(), usuario);
            getEntityManager().merge(entity);

            //sendJMSMessageToColadeNotificacion(entity);
        } catch (Exception e) {
            throw new GrabarException(entity.toString(), e);
        } finally {
            Logger.getLogger(entityClass.getName()).log(Level.INFO, "Entidad modificada:{0}", entity.toString());
        }
    }

    public void remove(T entity, String usuario) throws BorrarException {
        try {
            //  ejbEventos.crearAuditoria(entityClass.getName(), borrarObjetos(entity), usuario, "B");
            log(modificarObjetos(entity), "R", entityClass.getSimpleName(), usuario);
            getEntityManager().remove(getEntityManager().merge(entity));
            //sendJMSMessageToColadeNotificacion(entity);
        } catch (Exception e) {
            throw new BorrarException(entity.toString(), e);
        } finally {
            Logger.getLogger(entityClass.getName()).log(Level.INFO, "Entidad borrada:{0}", entity.toString());
        }
    }

    public T find(Object id) throws ConsultarException {
        try {
            return getEntityManager().find(entityClass, id);
        } catch (Exception e) {
            throw new ConsultarException(id.toString(), e);
        }
    }

    public List<T> findAll() throws ConsultarException {
        try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public List<T> findRange(int[] range) throws ConsultarException {
        try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            javax.persistence.Query q = getEntityManager().createQuery(cq);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public int count() throws ConsultarException {
        try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
            cq.select(getEntityManager().getCriteriaBuilder().count(rt));
            javax.persistence.Query q = getEntityManager().createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    /**
     * <p>
     * Función genérica para toda consulta: Necesita un paràmetro cuyo KEY sea
     * igual a where con la cadena de where</p>
     * <p>
     * Necesita un parámetro ;orden on la cadena order by</p>
     *
     * <p>
     * Necesita un paràmetro llamado ;inicial para rango inicial Necesita un
     * parámetro llamado ;final indicar el rango final</p>
     *
     * @param parametros
     * @return - lista de objetos
     * @Param Map de parametos
     */
    public List<T> encontarParametros(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains(";where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else {
                    par.put(clave.trim(), e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select object(o) from " + tabla + " as o " + where + orden;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            if (all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public int contar(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select count(o) from " + tabla + " as o " + where;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }

            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public Integer maximoNumero(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            String campo = "";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else if (clave.contains(";campo")) {
                    campo = (String) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select max(" + campo + ") from " + tabla + " as o " + where;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            Integer respuesta = (Integer) q.getSingleResult();
            if (respuesta == null) {
                return 0;
            }
            return (respuesta);
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public Date maximaFecha(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            String campo = "";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else if (clave.contains(";campo")) {
                    campo = (String) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select max(" + campo + ") from " + tabla + " as o " + where;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            Date respuesta = (Date) q.getSingleResult();
            if (respuesta == null) {
                return null;
            }
            return (respuesta);
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public String maximoNumeroStr(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            String campo = "";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else if (clave.contains(";campo")) {
                    campo = (String) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select max(" + campo + ") from " + tabla + " as o " + where;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            String respuesta = (String) q.getSingleResult();
            if (respuesta == null) {
                return "0";
            }
            return (respuesta);
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public BigDecimal sumarCampo(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            String campo = "o.valor";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else if (clave.contains(";campo")) {
                    campo = (String) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select sum(" + campo + ") from " + tabla + " as o " + where;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            BigDecimal r = (BigDecimal) q.getSingleResult();
            return r == null ? new BigDecimal(BigInteger.ZERO) : r;
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public Double sumarCampoDoble(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            String campo = "o.valor";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else if (clave.contains(";campo")) {
                    campo = (String) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select sum(" + campo + ") from " + tabla + " as o " + where;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            Double r = (Double) q.getSingleResult();
            return r == null ? new Double(0) : r;
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public Float sumarCampoFloat(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String where = "";
            String orden = "";
            String campo = "o.valor";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("where")) {
                    where = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else if (clave.contains(";campo")) {
                    campo = (String) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }
            if (!where.isEmpty()) {
                where = " where " + where;
            }

            String sql = "Select sum(" + campo + ") from " + tabla + " as o " + where;
            Query q = getEntityManager().createQuery(sql);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            Float r = (Float) q.getSingleResult();
            return r == null ? new Float(0) : r;
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    public List<Object[]> sumar(Map parametros) throws ConsultarException {

        List<Object[]> retorno = null;
        Iterator it = parametros.entrySet().iterator();
        String where = "";
        String grupo = "";
        String campo = "";
        String suma = "";
        String orden = "";
        boolean all = false;
        int maxResults = -1;
        int firstResult = -1;
        String tabla = entityClass.getSimpleName();
        Map par = new HashMap();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();
            if (clave.contains(";where")) {
                where = (String) e.getValue();
            } else if (clave.contains(";inicial")) {
                firstResult = (Integer) e.getValue();
                all = true;
            } else if (clave.contains(";campo")) {
                grupo = " group by " + (String) e.getValue();
                campo = (String) e.getValue();
            } else if (clave.contains(";suma")) {
                suma = (String) e.getValue();
            } else if (clave.contains(";orden")) {
                orden = " order by " + (String) e.getValue();
            } else if (clave.contains(";final")) {
                all = true;
                maxResults = (Integer) e.getValue();
            } else {
                par.put(clave, e.getValue());
            }

        }
        if (!where.isEmpty()) {
            where = " where " + where;
        }
        String coma = ((campo == null || campo.isEmpty()) ? "" : ",");
        String sql = "Select " + campo + coma + suma + " from " + tabla + " as o " + where + grupo + " " + orden;
        Query q = getEntityManager().createQuery(sql);
        if (all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        it = par.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();

            q.setParameter(clave, e.getValue());
        }
        retorno = q.getResultList();

        return retorno;
    }

    public List<T> sumarClase(Map parametros) throws ConsultarException {

        List<T> retorno = null;
        Iterator it = parametros.entrySet().iterator();
        String where = "";
        String grupo = "";
        String campo = "";
        String suma = "";
        String orden = "";
        boolean all = false;
        int maxResults = -1;
        int firstResult = -1;
        String tabla = entityClass.getSimpleName();
        Map par = new HashMap();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();
            if (clave.contains(";where")) {
                where = (String) e.getValue();
            } else if (clave.contains(";inicial")) {
                firstResult = (Integer) e.getValue();
                all = true;
            } else if (clave.contains(";campo")) {
                grupo = " group by " + (String) e.getValue();
                campo = (String) e.getValue();
            } else if (clave.contains(";suma")) {
                suma = (String) e.getValue();
            } else if (clave.contains(";orden")) {
                orden = " order by " + (String) e.getValue();
            } else if (clave.contains(";final")) {
                all = true;
                maxResults = (Integer) e.getValue();
            } else {
                par.put(clave, e.getValue());
            }

        }
        if (!where.isEmpty()) {
            where = " where " + where;
        }
        String coma = ((campo == null || campo.isEmpty()) ? "" : ",");
        String sql = "Select " + campo + coma + suma + " from " + tabla + " as o " + where + grupo + " " + orden;
        Query q = getEntityManager().createQuery(sql, entityClass);
//        if (all) {
//            q.setMaxResults(maxResults);
//            q.setFirstResult(firstResult);
//        }
        it = par.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String clave = (String) e.getKey();

            q.setParameter(clave, e.getValue());
        }
        retorno = q.getResultList();

        return retorno;
    }

    /**
     * <p>
     * Función genérica para toda consulta: Necesita un paràmetro cuyo KEY sea
     * igual a al nombre de una sentencia preparada</p>
     * <p>
     * Necesita un parámetro ;orden on la cadena order by</p>
     *
     * <p>
     * Necesita un paràmetro llamado ;inicial para rango inicial Necesita un
     * parámetro llamado ;final indicar el rango final</p>
     *
     * @return - lista de objetos
     * @Param Map de parametos
     */
    public List<T> encontarSqlArmado(Map parametros) throws ConsultarException {
        try {
            Iterator it = parametros.entrySet().iterator();
            String consulta = "";
            String orden = "";
            boolean all = false;
            int maxResults = -1;
            int firstResult = -1;
            String tabla = entityClass.getSimpleName();
            Map par = new HashMap();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains(";consulta")) {
                    consulta = (String) e.getValue();
                } else if (clave.contains(";inicial")) {
                    firstResult = (Integer) e.getValue();
                    all = true;
                } else if (clave.contains(";orden")) {
                    orden = " order by " + (String) e.getValue();

                } else if (clave.contains(";final")) {
                    all = true;
                    maxResults = (Integer) e.getValue();
                } else {
                    par.put(clave, e.getValue());
                }

            }

            Query q = getEntityManager().createNamedQuery(consulta);

            it = par.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();

                q.setParameter(clave, e.getValue());
            }
            if (all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } catch (Exception e) {
            throw new ConsultarException(entityClass.toString(), e);
        }
    }

    /**
     * *
     *
     *
     * @param desde
     * @param hasta
     * @return 0 iguales, 1 mayor hasta -1 mayor desde
     */
    public int comparaFechas(Date desde, Date hasta) {
        Calendar cDesde = Calendar.getInstance();
        Calendar cHasta = Calendar.getInstance();
        cDesde.setTime(desde);
        cHasta.setTime(hasta);
        int aDesde = cDesde.get(Calendar.YEAR);
        int aHasta = cHasta.get(Calendar.YEAR);
        int mDesde = cDesde.get(Calendar.MONTH);
        int mHasta = cHasta.get(Calendar.MONTH);
        int dDesde = cDesde.get(Calendar.DATE);
        int dHasta = cHasta.get(Calendar.DATE);
        // comparo anios
        if (aDesde > aHasta) {
            return -1;
        } else if (aDesde < aHasta) {
            return 1;
        }
        if (mDesde > mHasta) {
            return -1;
        } else if (mDesde < mHasta) {
            return 1;
        }
        if (dDesde > dHasta) {
            return -1;
        } else if (dDesde < dHasta) {
            return 1;
        }
        return 0;
    }

    /**
     * *
     *
     *
     * @param desde
     * @param hasta
     * @return 0 iguales, 1 mayor hasta -1 mayor desde
     */
    public int comparaFechasHHMM(Date desde, Date hasta) {
        Calendar cDesde = Calendar.getInstance();
        Calendar cHasta = Calendar.getInstance();
        cDesde.setTime(desde);
        cHasta.setTime(hasta);
        int aDesde = cDesde.get(Calendar.YEAR);
        int aHasta = cHasta.get(Calendar.YEAR);
        int mDesde = cDesde.get(Calendar.MONTH);
        int mHasta = cHasta.get(Calendar.MONTH);
        int dDesde = cDesde.get(Calendar.DATE);
        int dHasta = cHasta.get(Calendar.DATE);
        int hDesde = cDesde.get(Calendar.HOUR_OF_DAY);
        int hHasta = cHasta.get(Calendar.HOUR_OF_DAY);
        int minDesde = cDesde.get(Calendar.MINUTE);
        int minHasta = cHasta.get(Calendar.MINUTE);
        // comparo anios
        if (aDesde > aHasta) {
            return -1;
        } else if (aDesde < aHasta) {
            return 1;
        }
        if (mDesde > mHasta) {
            return -1;
        } else if (mDesde < mHasta) {
            return 1;
        }
        if (dDesde > dHasta) {
            return -1;
        } else if (dDesde < dHasta) {
            return 1;
        }
        if (hDesde > hHasta) {
            return -1;
        } else if (hDesde < hHasta) {
            return 1;
        }
        if (minDesde > minHasta) {
            return -1;
        } else if (minDesde < minHasta) {
            return 1;
        }
        return 0;
    }

    public Date convertXMLCalenarIntoDate(XMLGregorianCalendar calendar) {

        return calendar.toGregorianCalendar().getTime();

    }

    public XMLGregorianCalendar convertDateToXMLCalenar(Date calendar) {
        try {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(calendar);
            XMLGregorianCalendar cal = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gcal);
            return cal;
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void log(String objeto, String operacion, String clase, String usuario) {
//        Eventos e = new Eventos();
        String evento = clase + "#" + operacion + "#" + usuario + "#" + objeto;
//        e.setBean(clase);
//        e.setOperacion(operacion);
//        e.setUserid(usuario);
//        e.setObjeto(objeto);
//        e.setFecha(new Date());
//        e.setHora(new Date());
        try {
            javax.jms.Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(queue);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(evento);
            messageProducer.send(message);
            messageProducer.close();
            connection.close();
        } catch (JMSException ex) {
        }
    }

    public String saldoDias(Long minutosParametro, boolean operativo, boolean utilizado) {
        DecimalFormat df = new DecimalFormat("0");
        double hora = minutosParametro.doubleValue() / 60;
        double enteroHora = (int) (hora);
        double minutos = (hora - enteroHora) * 60;
        int ocho = 8;
        if (utilizado && operativo) {
            ocho = 8;
//            ocho=24;
        }
        double dias = enteroHora / ocho;
        double enteroDias = (int) dias;

        double saldoHoras = (dias - enteroDias) * ocho;
        return "" + df.format(enteroDias) + " días, "
                + df.format(saldoHoras) + " horas,"
                + df.format(minutos) + " minutos";
    }

    public double cuatosDias(Long minutosParametro, boolean operativo, boolean utilizado) {
        DecimalFormat df = new DecimalFormat("0");
        double hora = minutosParametro.doubleValue() / 60;
        double enteroHora = (int) (hora);
        double minutos = (hora - enteroHora) * 60;
        int ocho = 8;
        if (utilizado && operativo) {
            ocho = 8;
//            ocho=24;
        }
        double dias = enteroHora / ocho;
        double enteroDias = (int) dias;

        double saldoHoras = (dias - enteroDias) * ocho;
        return enteroDias + saldoHoras / 24 + minutos / (24 * 60);
    }

    public Maestros traerMaestroCodigo(String codigo,
            String modulo) throws ConsultarException {
        Query q = getEntityManager().createQuery("Select object(o) from Maestros as o"
                + " where o.codigo=:maestroParametro and o.modulo=:modulo");
        q.setParameter("maestroParametro", codigo);
        q.setParameter("modulo", modulo);
        List<Maestros> lista = q.getResultList();
        if ((lista == null) || (lista.isEmpty())) {
            return null;
        }
        return lista.get(0);
    }

    public Maestros traerMaestroCodigo(String codigo) throws ConsultarException {
        Query q = getEntityManager().createQuery("Select object(o) from Maestros as o"
                + " where o.codigo=:maestroParametro");
        q.setParameter("maestroParametro", codigo);
        List<Maestros> lista = q.getResultList();
        if ((lista == null) || (lista.isEmpty())) {
            return null;
        }
        return lista.get(0);
    }

    public Codigos traerCodigo(String maestro, String codigo, String modulo) throws ConsultarException {
        String sql = "Select object(o) from Codigos as o"
                + " where o.maestro.codigo=:maestroParametro and "
                + " o.codigo=:codigo " + (modulo == null ? "" : " and o.maestro.modulo=:modulo ");
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("maestroParametro", maestro);
        if (modulo != null) {
            q.setParameter("modulo", modulo);
        }
        q.setParameter("codigo", codigo);
        List<Codigos> lista = q.getResultList();
        if ((lista == null) || (lista.isEmpty())) {
            return null;
        }
        return lista.get(0);
    }

    public List<Codigos> traerCodigos(String maestro, String modulo) throws ConsultarException {
        String sql = "Select object(o) from Codigos as o"
                + " where o.maestro.codigo=:maestroParametro ";
//                + " " + (modulo == null ? "" : " and o.maestro.modulo=:modulo ");
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("maestroParametro", maestro);
//        if (modulo != null) {
//            q.setParameter("modulo", modulo);
//        }
        return q.getResultList();
    }

    /**
     * Función que elimina acentos y caracteres especiales de una cadena de
     * texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public String reemplazarCaracteresRaros(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ/%&,.#+-¡?¿()!°|[]º";
//        System.out.println("Original"+original.length());
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC              N    ";
//        System.out.println("Ascii"+ascii.length());
        String output = input;
        if (output == null) {
            return "";
        }
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        output = output.replace("\n", "");
        output = output.replace("\t", "");
        return output;
    }

    public int diferenciaDosFechas(Calendar fechaHasta, Calendar fechaDesde) {
        int diaDesde = fechaDesde.get(Calendar.DATE);
        int mesDesde = fechaDesde.get(Calendar.MONTH) + 1;
        int anioDesde = fechaDesde.get(Calendar.YEAR);
        // hasta
        int diaHasta = fechaHasta.get(Calendar.DATE);
        int mesHasta = fechaHasta.get(Calendar.MONTH) + 1;
        int anioHasta = fechaHasta.get(Calendar.YEAR);

        if (mesDesde == mesHasta) {
            if ((mesDesde == 1) || (mesDesde == 3) || (mesDesde == 5)
                    || (mesDesde == 7) || (mesDesde == 8)
                    || (mesDesde == 10) || (mesDesde == 12)) {
                // tiene 31 días
                if (diaHasta == 31) {
                    diaHasta = 30;
                }
                if (diaDesde == 31) {
                    diaDesde = 30;
                }
                return diaHasta - diaDesde + 1;
            } else if (mesDesde == 2) {
                // febrero 
                if ((diaHasta == 28) || (diaHasta == 29)) {
                    diaHasta = 30;
                }
                return diaHasta - diaDesde + 1;
            } else {
                // meses de 30 días 
                return diaHasta - diaDesde + 1;
            }
        } else if (mesDesde < mesHasta) {
            int difDias = 0;
            for (int imes = mesDesde; imes < mesHasta; imes++) {
                difDias += 30;
            } // fin del for
            return difDias - diaDesde + 1;
        } // fin del else if

        return 0;
    }

    public double redondeo(double valor) {
        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal bd = new BigDecimal(dividido);
        return bd.setScale(2, RoundingMode.CEILING).doubleValue();
    }

}
