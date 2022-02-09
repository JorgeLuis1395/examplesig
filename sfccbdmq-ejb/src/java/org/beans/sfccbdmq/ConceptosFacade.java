/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.entidades.sfccbdmq.Conceptos;
import org.errores.sfccbdmq.ConsultarException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Configuracion;

/**
 *
 * @author edwin
 */
@Stateless
public class ConceptosFacade extends AbstractFacade<Conceptos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConceptosFacade() {
        super(Conceptos.class);
    }

    @Override
    protected String modificarObjetos(Conceptos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";
        retorno += "<esporcentaje>" + nuevo.getEsporcentaje() + "</esporcentaje>";
        retorno += "<sobre>" + nuevo.getSobre() + "</sobre>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<provision>" + nuevo.getProvision() + "</provision>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<formula>" + nuevo.getFormula() + "</formula>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<novedad>" + nuevo.getNovedad() + "</novedad>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";
        retorno += "<fechapago>" + nuevo.getFechapago() + "</fechapago>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<esvalor>" + nuevo.getEsvalor() + "</esvalor>";
        retorno += "<deque>" + nuevo.getDeque() + "</deque>";
        retorno += "<periodico>" + nuevo.getPeriodico() + "</periodico>";
        retorno += "<vacaciones>" + nuevo.getVacaciones() + "</vacaciones>";
        retorno += "<liquidacion>" + nuevo.getLiquidacion() + "</liquidacion>";

        return retorno;
    }

    /**
     *
     * @param formula
     * @param nombreFormula
     * @return Resultado de la inserción valor si hay errores
     * @throws org.errores.sfccbdmq.ConsultarException
     */
    public String crearSp(String formula, String nombreFormula) throws ConsultarException {
        int x;
        try {
            String formulaAGrabar = "CREATE OR REPLACE FUNCTION " + nombreFormula + ""
                    + "(real,\n"
                    + "    real,\n"
                    + "    real,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer) \n"
                    + " RETURNS real AS\n"
                    + "$BODY$\n"
                    + "DECLARE\n"
                    + " rmu ALIAS FOR $1;\n"
                    + " subrogacion ALIAS FOR $2;\n"
                    + " ingresos ALIAS FOR $3;\n"
                    + " diasTrabajados ALIAS FOR $4;\n"
                    + " diasTrabajadosSubrogados ALIAS FOR $5;\n"
                    + " idEmpleado ALIAS FOR $6;\n "
                    + " diaActual ALIAS FOR $7;\n "
                    + " mesActual ALIAS FOR $8;\n "
                    + " anio_Actual ALIAS FOR $9;\n "
                    + "resultado real;\n"
                    + formula + "\n"
                    + "RETURN resultado;\n"
                    + "END;\n"
                    + "$BODY$\n"
                    + " LANGUAGE plpgsql;";
            Query q = em.createNativeQuery(formulaAGrabar);
            q.setParameter(1, formulaAGrabar);

//            em.getTransaction().begin();
            x = q.executeUpdate();
//            em.getTransaction().commit();

        } catch (Exception ex) {
            throw new ConsultarException("ERROR EJECUNTADO SP :\n", ex);
        }
        return null;

    }

   public String crearSuperProceos() throws ConsultarException {

        try {
            Query qConf = em.createQuery("SELECT OBJECT(o) FROM Configuracion as o");
            List<Configuracion> lconfig = qConf.getResultList();
            Configuracion conf = new Configuracion();
            for (Configuracion cf : lconfig) {
                conf = cf;
            }
            Query qIngresos = em.createQuery("SELECT OBJECT(o) FROM Conceptos as o "
                    + " WHERE o.activo=TRUE and o.ingreso=TRUE and o.vacaciones=false "
                    + "and o.liquidacion=false order by o.orden");
            List<Conceptos> listaIngresos = qIngresos.getResultList();
            Query qEgresos = em.createQuery("SELECT OBJECT(o) FROM Conceptos as o "
                    + " WHERE o.activo=true and o.ingreso=FALSE and o.vacaciones=false and o.liquidacion=false "
                    + " order by o.orden");
            List<Conceptos> listaEgresos = qEgresos.getResultList();
            String formulaAGrabar = "CREATE OR REPLACE FUNCTION todorol"
                    + "(real,\n"
                    + "    real,\n"
                    + "    real,\n"
                    //                    + "    numeric,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    integer,\n"
                    + "    timestamp,\n"
                    + "    real)\n"
                    + "     \n"
                    + " RETURNS real AS\n"
                    + "$BODY$\n"
                    + "DECLARE\n"
                    + " rmu ALIAS FOR $1;\n"
                    + " valorsubrogacion ALIAS FOR $2;\n"
                    + " basico ALIAS FOR $3;\n"
                    + " diasTrabajados ALIAS FOR $4;\n"
                    + " diasTrabajadosSubrogados ALIAS FOR $5;\n"
                    + " idEmpleado ALIAS FOR $6;\n "
                    + " diaActual ALIAS FOR $7;\n "
                    + " mesActual ALIAS FOR $8;\n "
                    + " anio_Actual ALIAS FOR $9;\n "
                    + " diaUtilizada ALIAS FOR $10;\n "
                    + " valorencargo ALIAS FOR $11;\n "
                    + " ingresos real;\n "
                    + " idpagos int;\n "
                    + " idprueba int;\n "
                    + "resultado real;\n"
                    + "subrogacion real;\n"
                    + "yaCargado boolean;\n"
                    + "prestamosRegistro RECORD;\n"
                    + "sancionesRegistro RECORD;\n"
                    + "-- Version del Sistema 1FEB2019\n";
            String declaraciones = "";
            String formula = "subrogacion:=valorencargo+valorsubrogacion;";
            formula += "idpagos:=(Select id from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado "
                    + "and concepto is null and prestamo is null and sancion is null);\n";
            formula += "yaCargado:=(Select cargado from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado "
                    + "and concepto is null and prestamo is null and sancion is null);\n";
            formula += "if yaCargado is null or yaCargado=false then\n";
            formula += "if idpagos is null then\n";

            formula += "insert into pagosempleados (empleado, anio, mes, valor,cantidad,encargo, pagado, cargado, dia) values ("
                    + "idEmpleado,anio_Actual,mesActual,rmu,valorsubrogacion,valorencargo,false,false,diaUtilizada);\n";
            formula += "else\n";
            formula += "update pagosempleados set valor=rmu,encargo=valorencargo,cantidad=valorsubrogacion,dia=diaUtilizada,pagado=false where id=idpagos;\n";
            formula += "end if;\n";
            formula += "end if;\n";
            formula += "ingresos:=rmu+valorencargo+valorsubrogacion;\n";
            String sumatorio = "";
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
            for (Conceptos c : listaIngresos) {
                declaraciones += "valor" + c.getCodigo() + " real;\n";
                if (c.getEsporcentaje()) {
                    if (null != c.getDeque())//                            tomar el valor
                    // de que
                    {
                        switch (c.getDeque()) {
                            case 0: {
                                //                                return "SMV";
                                double v = (c.getValor() == null ? 0 : c.getValor());
                                formula += "valor" + c.getCodigo() + ":=(" + df.format(conf.getSmv() * v / 100) + ")::numeric(10,2);\n";
                                break;
                            }
                            case 1: {
                                //                                return "RMU";
                                float v = (c.getValor() == null ? 0 : c.getValor());
                                formula += "valor" + c.getCodigo() + ":=((rmu+subrogacion)*(" + df.format(v) + "/100))::numeric(10,2);\n";
                                break;
                            }
                            case 2: {
                                //                                return "Ingresos";
                                // toca ver despues de que esten todos los ingresos comolo almaceno no se
                                float v = (c.getValor() == null ? 0 : c.getValor());
                                formula += "valor" + c.getCodigo() + ":=(ingresos)*(" + df.format(v) + "/100)::numeric(10,2);\n";
                                break;
                            }
                            default:
                                break;
                        }
                    }

                } else if (c.getEsvalor()) {
                    formula += "valor" + c.getCodigo() + ":=(" + df.format(c.getValor()) + ")::numeric(10,2);\n";
                } else if (!((c.getFormula() == null) || (c.getFormula().isEmpty()))) {
                    formula += "valor" + c.getCodigo() + ":=(Select " + c.getCodigo() + "(rmu,valorencargo+valorsubrogacion,ingresos,diasTrabajados,"
                            + "diasTrabajadosSubrogados,idEmpleado,diaActual,mesActual,anio_Actual)::numeric(10,2));\n";
                }

                if (c.getNovedad()) {
//                    formula += "valor" + c.getCodigo() + ":=" + "valor" + c.getCodigo() + "* "
                    formula += "valor" + c.getCodigo() + ":=" 
                            + "(select novedadesempleado(idEmpleado," + c.getId() + ",mesActual,anio_Actual)::numeric(10,2));\n";
                }
                if (c.getIngreso()) {
                    formula += "\t\tIF valor" + c.getCodigo() + "=0 THEN\n";
                    formula += "\t\t\tingresos:=ingresos+valor" + c.getCodigo() + ";\n";
                    formula += "\t\tend if;\n";
                }
                formula += "idpagos:=(Select id from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado "
                        + "and concepto=" + c.getId() + ");\n";
                formula += "yaCargado:=(Select cargado from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado "
                        + "and concepto=" + c.getId() + ");\n";
                formula += "if yaCargado is null or yaCargado=false then\n";
                formula += "if idpagos is null then\n";
                formula += "\tif valor" + c.getCodigo() + " is not null then\n";
                formula += "\t\t insert into pagosempleados (concepto, empleado, anio, mes, valor, pagado, cargado, dia) values ("
                        + c.getId() + ",idEmpleado,anio_Actual,mesActual,valor" + c.getCodigo() + ",false,false,diaUtilizada);\n";
                formula += "\tend if;\n";
                formula += "else\n";
                formula += "if valor" + c.getCodigo() + " is not null then\n";
                formula += "update pagosempleados set valor=valor" + c.getCodigo() + ",dia=diaUtilizada,pagado=false where id=idpagos;\n";
                formula += "end if;\n";
                formula += "end if;\n";
                formula += "end if;\n";
                sumatorio += "retorno:=retorno+valor" + c.getCodigo() + ";\n";

            }
            for (Conceptos c : listaEgresos) {
                declaraciones += "valor" + c.getCodigo() + " real;\n";
                if (c.getEsporcentaje()) {
                    if (null != c.getDeque())//                            tomar el valor
                    // de que
                    {
                        switch (c.getDeque()) {
                            case 0: {
                                //                                return "SMV";
                                double v = (c.getValor() == null ? 0 : c.getValor());
                                formula += "valor" + c.getCodigo() + ":=(" + df.format(conf.getSmv() * v / 100) + ")::numeric(10,2);\n";
                                break;
                            }
                            case 1: {
                                //                                return "RMU";
                                float v = (c.getValor() == null ? 0 : c.getValor());
                                formula += "valor" + c.getCodigo() + ":=((rmu+subrogacion)*(" + df.format(v) + "/100))::numeric(10,2);\n";
                                break;
                            }
                            case 2: {
                                //                                return "Ingresos";
                                // toca ver despues de que esten todos los ingresos comolo almaceno no se
                                float v = (c.getValor() == null ? 0 : c.getValor());
                                formula += "valor" + c.getCodigo() + ":=((ingresos)*(" + df.format(v) + "/100))::numeric(10,2);\n";
                                break;
                            }
                            default:
                                break;
                        }
                    }

                } else if (c.getEsvalor()) {
                    formula += "valor" + c.getCodigo() + ":=(" + df.format(c.getValor()) + ")::numeric(10,2);\n";
                } else if (!((c.getFormula() == null) || (c.getFormula().isEmpty()))) {
                    formula += "valor" + c.getCodigo() + ":=(Select " + c.getCodigo() + "(rmu,valorencargo+valorsubrogacion,ingresos,diasTrabajados,"
                            + "diasTrabajadosSubrogados,idEmpleado,diaActual,mesActual,anio_Actual)::numeric(10,2) );\n";
                }

                if (c.getNovedad()) {
                    formula += "valor" + c.getCodigo() + ":=" 
//                    formula += "valor" + c.getCodigo() + ":=" + "valor" + c.getCodigo() + "* "
                            + "(select novedadesempleado(idEmpleado," + c.getId() + ",mesActual,anio_Actual))::numeric(10,2);\n";
                }

                formula += "idpagos:=(Select id from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado "
                        + "and concepto=" + c.getId() + ");\n";
                formula += "yaCargado:=(Select cargado from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado "
                        + "and concepto=" + c.getId() + ");\n";
                formula += "if yaCargado is null or yaCargado=false then\n";
                formula += "if idpagos is null then\n";
                formula += "if valor" + c.getCodigo() + " is not null then\n";
                formula += "insert into pagosempleados (concepto, empleado, anio, mes, valor, pagado, cargado, dia) values ("
                        + c.getId() + ",idEmpleado,anio_Actual,mesActual,valor" + c.getCodigo() + ",false,false,diaUtilizada);\n";
                formula += "end if;\n";
                formula += "else\n";
                formula += "if valor" + c.getCodigo() + " is not null then\n";
                formula += "update pagosempleados set valor=valor" + c.getCodigo() + ",dia=diaUtilizada,pagado=false where id=idpagos;\n";
                formula += "end if;\n";
                formula += "end if;\n";
                formula += "end if;\n";

                sumatorio += "retorno:=retorno-valor" + c.getCodigo() + ";\n";

            }
            String prestamos = "-- pongo lo de prestamos\n"
                    + " FOR prestamosRegistro IN \n"
                    + "    select prestamo,valorpagado,amortizaciones.id,cuota,numero \n"
                    + "from amortizaciones left join prestamos on amortizaciones.prestamo=prestamos.id\n"
                    + "where\n"
                    + "prestamos.empleado=idEmpleado and amortizaciones.mes=mesActual and amortizaciones.anio=anio_actual\n"
                    + "and prestamos.pagado=true and prestamos.aprobado=true \n"
                    + "and (valorpagado is null or valorpagado=0)\n"
                    + " LOOP\n"
                    + "idpagos:=(Select id from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado and prestamo=prestamosRegistro.prestamo);\n"
                    + "if idpagos is null then\n"
                    + "insert into pagosempleados (prestamo, empleado, anio, mes, valor, pagado, cargado, dia) values (prestamosRegistro.prestamo,idEmpleado,anio_Actual,mesActual,prestamosRegistro.cuota,false,false,diaUtilizada);\n"
                    + "else\n"
                    + "update pagosempleados set valor=prestamosRegistro.cuota,dia=diaUtilizada where id=idpagos;\n"
                    + "end if;\n"
                    + " END LOOP;                                              \n"
                    + "--    \n";
            String sanciones = "-- pongo lo de sanciones\n"
                    + " FOR sancionesRegistro IN \n"
                    + "    select * from historialsanciones\n"
                    + "where \n"
                    + "empleado=idempleado  and faplicacion is null and especunaria=true\n"
                    + " LOOP\n"
                    + "idpagos:=(Select id from pagosempleados where mes=mesActual and anio=anio_actual and empleado=idEmpleado and sancion=sancionesRegistro.id);\n"
                    + "if idpagos is null then\n"
                    + "insert into pagosempleados (sancion, empleado, anio, mes, valor, pagado, cargado, dia) values (sancionesRegistro.id,idEmpleado,anio_Actual,mesActual,sancionesRegistro.valor,false,false,diaUtilizada);\n"
                    + "else\n"
                    + "update pagosempleados set valor=sancionesRegistro.valor,dia=diaUtilizada where id=idpagos;\n"
                    + "end if;\n"
                    + " END LOOP\n;                                              \n"
                    + "--    \n";
            formulaAGrabar += declaraciones + "\nBEGIN\n" + formula + "\n" + prestamos + sanciones;
//            formulaAGrabar += declaraciones+"\nBEGIN\n"+formula+"\n"+sumatorio;
            formulaAGrabar += "\nRETURN resultado;\n"
                    + "END;\n"
                    + "$BODY$\n"
                    + " LANGUAGE plpgsql;";
            Query q = em.createNativeQuery(formulaAGrabar);
            q.setParameter(1, formulaAGrabar);
//            em.getTransaction().begin();
            int x = q.executeUpdate();
//            em.getTransaction().commit();

        } catch (Exception ex) {
            throw new ConsultarException("ERROR EJECUNTADO SP :\n", ex);
        }
        return null;

    }

    public float ejecutaSp(float rmu, float subrogacion, float basico,
            int diasTrabajados, int diasSubTrabajados,
            Integer idEmpleado, Date fechaFormula, Date dia, float encargo) {
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        Calendar c = Calendar.getInstance();
        c.setTime(fechaFormula);
        int mes = c.get(Calendar.MONTH) + 1;
//        String sql = "select todorol" + "(" + df.format(rmu) + "," + df.format(subrogacion) + "," + df.format(basico)
//                + "," + diasTrabajados + "," + diasSubTrabajados + "," + idEmpleado + "," + c.get(Calendar.DATE) + "," + mes + "," + c.get(Calendar.YEAR)
//                + "):: numeric(10,2)";
        Query q = em.createNativeQuery("select todorol (?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11):: numeric(10,2)");
//        Query q = em.createNativeQuery(sql);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        q.setParameter(1, rmu);
        q.setParameter(2, subrogacion);
        q.setParameter(3, basico);
        q.setParameter(4, diasTrabajados);
        q.setParameter(5, diasSubTrabajados);
        q.setParameter(6, idEmpleado);
        q.setParameter(7, c.get(Calendar.DATE));
        q.setParameter(8, c.get(Calendar.MONTH) + 1);
        q.setParameter(9, c.get(Calendar.YEAR));
        q.setParameter(10, dia);
        q.setParameter(11, encargo);
        Object retorno = q.getSingleResult();
        if (retorno == null) {
            return 0;
        }
        return ((BigDecimal) retorno).floatValue();

//        return 0;
    }
}