/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ejb.sfcarchivos;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
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
     * @return - lista de objetos
     * @Param Map de parametos
     */
    public List<T> encontarParametros(Map parametros) {
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
                par.put(clave, e.getValue());
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

    }

    public int contar(Map parametros) {
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

    }

    public BigDecimal sumarCampo(Map parametros) {
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

    }

    public Double sumarCampoDoble(Map parametros) {
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

    }

    public List<Object[]> sumar(Map parametros) {

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
    public List<T> encontarSqlArmado(Map parametros) {
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

    }
}
