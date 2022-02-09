/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarMeses;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.DetallesvalesFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Cajas;
import org.errores.sfccbdmq.ConsultarException;
import org.presupuestos.sfccbdmq.CalulosPresupuestoBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteCajasSfccbdmq")
@ViewScoped
public class ReporteCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public ReporteCajasBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    private Formulario formulario = new Formulario();

    private Cajas caja;
    private AuxiliarMeses aSuperTotal;
    private List<AuxiliarMeses> listaMeses;
//    private Date desde;
//    private Date hasta;
    // todo para perfiles 
    private Perfiles perfil;
    private int anio;

    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private RenglonesFacade ejbRenglones;

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ReporteReposicionesCajasVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
    }

    public String buscar() {
        llenarCajas();
        return null;
    }

    private void llenarCajas() {
        try {
            aSuperTotal = new AuxiliarMeses();
            aSuperTotal.setEstilo("total");

            listaMeses = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.apertura is null");
            List<Cajas> listaCajas = ejbCajas.encontarParametros(parametros);
            for (Cajas ca : listaCajas) {
                parametros = new HashMap();
                parametros.put(";where", "o.apertura=:apertura");
                parametros.put("apertura", ca);
                parametros.put(";orden", "o.departamento.codigo");
                List<Cajas> lista = ejbCajas.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.apertura=:apertura");
                    parametros.put("apertura", ca);
                    parametros.put(";campo", "o.apertura,o.id,o.apertura.empleado.entidad.pin,o.apertura.valor");
                    parametros.put(";suma", "sum(o.valor)");
                    List<Object[]> listaAgrupados = ejbCajas.sumar(parametros);
                    AuxiliarMeses aMeses = new AuxiliarMeses();
                    double valorE = 0;
                    double valorF = 0;
                    double valorM = 0;
                    double valorA = 0;
                    double valorMa = 0;
                    double valorJ = 0;
                    double valorJu = 0;
                    double valorAg = 0;
                    double valorS = 0;
                    double valorO = 0;
                    double valorN = 0;
                    double valorD = 0;

                    for (Object[] o : listaAgrupados) {

                        Cajas cajaApertura = (Cajas) o[0];
                        int id = (int) o[1];
                        String pin = (String) o[2];
                        BigDecimal valorApertura = (BigDecimal) o[3];
                        int kardex;

                        parametros = new HashMap();
                        parametros.put(";where", "o.id=:id");
                        parametros.put("id", id);
                        List<Cajas> listaId = ejbCajas.encontarParametros(parametros);
                        Cajas c = listaId.get(0);

                        aMeses.setCaja(cajaApertura);
                        aMeses.setId(id);
                        aMeses.setCodigo(pin);
                        aMeses.setValor(valorApertura.doubleValue());

                        Calendar cDesde = Calendar.getInstance();
                        cDesde.set(anio, 0, 1);
                        Calendar cHasta = Calendar.getInstance();
                        cHasta.set(anio, 0, 31);
                        double valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorE += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 2, 1);
                        cHasta.add(Calendar.DATE, -1);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorF += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 2, 31);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorM += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 3, 30);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorA += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 4, 31);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorMa += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 5, 30);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorJ += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 6, 31);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorJu += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 7, 31);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorAg += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 8, 30);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorS += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 9, 31);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorO += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 10, 30);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorN += valor;

                        cDesde.add(Calendar.MONTH, 1);
                        cHasta.set(anio, 11, 31);
                        valor = traerValor(cDesde.getTime(), cHasta.getTime(), id);
                        if (valor == 0) {
                            if (c.getKardexbanco() != null) {
                                kardex = c.getKardexbanco().getId();
                                valor = traerValorKardex(cDesde.getTime(), cHasta.getTime(), kardex);
                            }
                        }
                        valorD += valor;

                        aMeses.calculaTotal();
                    }

                    aMeses.setEnero(valorE);
                    aMeses.setFebrero(valorF);
                    aMeses.setMarzo(valorM);
                    aMeses.setAbril(valorA);
                    aMeses.setMayo(valorMa);
                    aMeses.setJunio(valorJ);
                    aMeses.setJulio(valorJu);
                    aMeses.setAgosto(valorAg);
                    aMeses.setSeptiempbre(valorS);
                    aMeses.setOctubre(valorO);
                    aMeses.setNoviembre(valorN);
                    aMeses.setDiciembre(valorD);
                    aMeses.calculaTotal();
                    listaMeses.add(aMeses);

                    aSuperTotal.setCodigo("Total");
                    aSuperTotal.setValor(aMeses.getValor() + aSuperTotal.getValor());
                    aSuperTotal.setEnero(aMeses.getEnero() + aSuperTotal.getEnero());
                    aSuperTotal.setFebrero(aMeses.getFebrero() + aSuperTotal.getFebrero());
                    aSuperTotal.setMarzo(aMeses.getMarzo() + aSuperTotal.getMarzo());
                    aSuperTotal.setAbril(aMeses.getAbril() + aSuperTotal.getAbril());
                    aSuperTotal.setMayo(aMeses.getMayo() + aSuperTotal.getMayo());
                    aSuperTotal.setJunio(aMeses.getJunio() + aSuperTotal.getJunio());
                    aSuperTotal.setJulio(aMeses.getJulio() + aSuperTotal.getJulio());
                    aSuperTotal.setAgosto(aMeses.getAgosto() + aSuperTotal.getAgosto());
                    aSuperTotal.setSeptiempbre(aMeses.getSeptiempbre() + aSuperTotal.getSeptiempbre());
                    aSuperTotal.setOctubre(aMeses.getOctubre() + aSuperTotal.getOctubre());
                    aSuperTotal.setNoviembre(aMeses.getNoviembre() + aSuperTotal.getNoviembre());
                    aSuperTotal.setDiciembre(aMeses.getDiciembre() + aSuperTotal.getDiciembre());
                }
            }
            aSuperTotal.calculaTotal();
            listaMeses.add(aSuperTotal);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double traerValor(Date desde, Date hasta, int idmodulo) {
        Map parametros = new HashMap();

        String where = "o.cabecera.fecha between :desde and :hasta"
                + " and o.cabecera.opcion in ('REPOSICION CAJA CHICA','LIQUIDACION_CAJAS')"
                + " and o.cabecera.idmodulo=:idmodulo and o.valor>0"
                + " and o.cabecera.tipo.nombre='CAJAS CHICAS'";
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put("idmodulo", idmodulo);

        parametros.put(";campo", "o.valor");

        try {
            parametros.put(";where", where);
            return ejbRenglones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerValorKardex(Date desde, Date hasta, int idmodulo) {
        Map parametros = new HashMap();

        String where = "o.cabecera.fecha between :desde and :hasta"
                + " and o.cabecera.opcion in ('PAGO_CAJAS_CHICAS_REEMBOLSO' , 'PAGO_CAJAS_CHICAS')"
                + " and o.cabecera.idmodulo=:idmodulo and o.valor>0";
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put("idmodulo", idmodulo);

        parametros.put(";campo", "o.valor");

        try {
            parametros.put(";where", where);
            return ejbRenglones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the caja
     */
    public Cajas getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(Cajas caja) {
        this.caja = caja;
    }

    /**
     * @return the aSuperTotal
     */
    public AuxiliarMeses getaSuperTotal() {
        return aSuperTotal;
    }

    /**
     * @param aSuperTotal the aSuperTotal to set
     */
    public void setaSuperTotal(AuxiliarMeses aSuperTotal) {
        this.aSuperTotal = aSuperTotal;
    }

    /**
     * @return the listaMeses
     */
    public List<AuxiliarMeses> getListaMeses() {
        return listaMeses;
    }

    /**
     * @param listaMeses the listaMeses to set
     */
    public void setListaMeses(List<AuxiliarMeses> listaMeses) {
        this.listaMeses = listaMeses;
    }

    /**
     * @return the perfil
     */
    public Perfiles getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(Perfiles perfil) {
        this.perfil = perfil;
    }

    /**
     * @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    /**
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
    }

    /**
     * @param configuracionBean the configuracionBean to set
     */
    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

}
