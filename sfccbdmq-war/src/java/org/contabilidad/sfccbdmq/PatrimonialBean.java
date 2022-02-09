/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "patrimonialSfccbdmq")
@ViewScoped
public class PatrimonialBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public PatrimonialBean() {
    }
    private List<AuxiliarCarga> balance;
    private Formulario formulario = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private FormatosFacade ejbFormatos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String auxiliar;
    private String centrocosto;
    private String cuenta;
    private String nivel;
    private double saldoInicial;
    private boolean ceros;
    private Resource reporte;

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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPvigente();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        String nombreForma = "PatrimonialVista";
        if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilStr));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }

    }

    public SelectItem[] getComboNiveles() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.tipo=0");
        parametros.put(";orden", "o.inicial asc");
        try {
            List<Formatos> lf = ejbFormatos.encontarParametros(parametros);
            String largo = "";
            for (Formatos f : lf) {
                if (f.getFormato().length() > largo.length()) {
                    largo = f.getFormato();
                }
            }
            String formato = largo.replace(".", "#");
            String[] sinpuntos = formato.split("#");
            int size = sinpuntos.length;
            SelectItem[] items = new SelectItem[size + 1];

            int i = 0;
            items[i++] = new SelectItem("", "--- Seleccione uno ---");
            String que = "";
            String totalL = "";
            for (String x : sinpuntos) {
                String longitud = "";
                que += x;
                longitud = que.replace("X", "_");
                items[i++] = new SelectItem(longitud, que);

            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PatrimonialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (desde == null) {
            MensajesErrores.advertencia("Es necesario fecha desde");
            return null;
        }
        if (hasta == null) {
            MensajesErrores.advertencia("Es necesario fecha hasta");
            return null;
        }
        balance = new LinkedList<>();
        // buscar formatos
        double totalSi = 0;
        double totalIngresos = 0;
        double totalEgresos = 0;
        double totalSf = 0;
        String cuentaResutados = configuracionBean.getConfiguracion().getCtaresultado();
        cuentasBean.traerNivel(cuentaResutados);
        Formatos nivelFormato = cuentasBean.getNivel();
        Map parametros = new HashMap();
        parametros.put(";where", "o.tipo=1");

        parametros.put(";orden", "o.inicial asc");
        try {
            // utilidad o perdida 
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            double utilidad = 0;
            for (Formatos f : lFormatos) {
                // saldo inicial
                parametros = new HashMap();
//                if ((centrocosto == null) || (centrocosto.isEmpty())) {
//                    parametros.put(";where", "o.cuenta like :codigo and o.fecha<=:desde");
//                } else {
//                    parametros.put(";where", "o.cuenta like :codigo "
//                            + "and o.fecha<=:desde and upper(o.centrocosto) like :centrocosto");
//                    parametros.put("centrocosto", centrocosto.toUpperCase() + "%");
//                }
//                parametros.put("codigo", f.getInicial() + "%");
//                parametros.put("desde", desde);
//                parametros.put(";campo", "o.valor");
//                utilidad += ejbRenglones.sumarCampo(parametros).doubleValue();
                // ingresos egresos
                if ((f.getDescripcion().equals("I") || f.getDescripcion().equals("G"))) {
                    parametros = new HashMap();

                    if ((centrocosto == null) || (centrocosto.isEmpty())) {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha between :desde and :hasta ");
                    } else {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha between :desde and :hasta and upper(o.centrocosto) like :centrocosto");
                        parametros.put("centrocosto", centrocosto.toUpperCase() + "%");
                    }
                    parametros.put("codigo", f.getInicial() + "%");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
                    utilidad += ejbRenglones.sumarCampo(parametros).doubleValue();
                }
            }
            // calculada la utilidad
            parametros = new HashMap();
            parametros.put(";where", "o.tipo=0");
            parametros.put(";orden", "o.inicial asc");
            lFormatos = ejbFormatos.encontarParametros(parametros);
            for (Formatos f : lFormatos) {
                parametros = new HashMap();

                parametros.put(";orden", "o.codigo");
                if (!((this.nivel == null) || (this.nivel.isEmpty()))) {
                    parametros.put(";where", "o.codigo like :codigo");
                    if (f.getInicial().length() >= this.nivel.length()) {
                        parametros.put("codigo", f.getInicial());
                    } else {
                        int cuantos = this.nivel.length() - f.getInicial().length();
                        String que = f.getInicial();
                        for (int i = 0; i < cuantos; i++) {
                            que += "_";
                        }
                        parametros.put("codigo", que);
                    }
                } else {
                    parametros.put(";where", "o.codigo like :codigo");
                    parametros.put("codigo", f.getInicial() + "%");
                }
                List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
                for (Cuentas c : lCuentas) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setTotal(c.getNombre());
                    a.setCuenta(c.getCodigo());
                    // busca las sumas
                    // saldo Inicial
                    parametros = new HashMap();
                    if ((centrocosto == null) || (centrocosto.isEmpty())) {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha<=:desde "
                                + "and o.cabecera.tipo.equivalencia.codigo ='I'");
                    } else {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha<:desde and upper(o.centrocosto) "
                                + "like :centrocosto and o.cabecera.tipo.equivalencia.codigo = 'I'");
                        parametros.put("centrocosto", centrocosto.toUpperCase() + "%");
                    }
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", desde);
                    parametros.put(";campo", "o.valor");
                    a.setSaldoInicial(ejbRenglones.sumarCampo(parametros));

                    // creditos
                    parametros = new HashMap();
                    if ((centrocosto == null) || (centrocosto.isEmpty())) {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha between :desde "
                                + "and :hasta and o.valor>0 and  o.cabecera.tipo.equivalencia.codigo not in ('I','C')");
                    } else {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha between :desde and :hasta "
                                + "and o.valor>0 and upper(o.centrocosto) like :centrocosto and "
                                + " o.cabecera.tipo.equivalencia.codigo not in ('I','C')");
                        parametros.put("centrocosto", centrocosto.toUpperCase() + "%");
                    }
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
                    
                    a.setEgresos(ejbRenglones.sumarCampo(parametros));

                    parametros = new HashMap();
                    if ((centrocosto == null) || (centrocosto.isEmpty())) {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha between :desde and "
                                + ":hasta and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C')");
                    } else {
                        parametros.put(";where", "o.cuenta like :codigo and o.fecha between :desde and :hasta "
                                + " and o.valor<0 and upper(o.centrocosto) like :centrocosto and "
                                + " o.cabecera.tipo.equivalencia.codigo not in ('I','C')");
                        parametros.put("centrocosto", centrocosto.toUpperCase() + "%");
                    }
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
                    double egresos = ejbRenglones.sumarCampo(parametros).doubleValue() * -1;

                    a.setIngresos(new BigDecimal(egresos));
                    a.setImputable(c.getImputable());
                    // ver la utilidad o perdida del ejercicio
                    if (nivelFormato.equals(f)) {
                        // ver si es cuenta del mismo tipo
                        // posible error cuanto la longitud de la cuenta sea menor a la del codigo
                        if (cuentaResutados.length() >= c.getCodigo().length()) {
                            String resultado = cuentaResutados.substring(0, c.getCodigo().length());
                            if (resultado.equals(c.getCodigo())) {
                                if (utilidad > 0) {
                                    a.setEgresos(new BigDecimal(a.getEgresos().doubleValue() + utilidad));
                                } else {
                                    a.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + Math.abs(utilidad)));
                                }
                            }
                        }
                    }
                    a.setSaldoFinal(new BigDecimal(a.getSaldoInicial().doubleValue() 
                            + a.getEgresos().doubleValue() - a.getIngresos().doubleValue()));
                    if (!((this.nivel == null) || (this.nivel.isEmpty()))) {
                        totalSi += a.getSaldoInicial().doubleValue();
                        totalEgresos += a.getEgresos().doubleValue();
                        totalIngresos += a.getIngresos().doubleValue();
                        totalSf += a.getSaldoFinal().doubleValue();
                    } else {
                        if (c.getImputable()) {
                            totalSi += a.getSaldoInicial().doubleValue();
                            totalEgresos += a.getEgresos().doubleValue();
                            totalIngresos += a.getIngresos().doubleValue();
                            totalSf += a.getSaldoFinal().doubleValue();
                        }
                    }
                    if (ceros) {
                        balance.add(a);
                    } else {
                        if ((a.getEgresos().doubleValue() == 0)
                                && (a.getIngresos().doubleValue() == 0)
                                && (a.getSaldoFinal().doubleValue() == 0)
                                && (a.getSaldoInicial().doubleValue() == 0)) {

                        } else {
                            balance.add(a);
                        }
                    }
                }
//                } //else de nivel
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PatrimonialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        if (ceros) {
//            int i = 0;
//            for (AuxiliarCarga a1 : balance) {
//                if ((a1.getEgresos().doubleValue() == 0)
//                        && (a1.getIngresos().doubleValue() == 0)
//                        && (a1.getSaldoFinal().doubleValue() == 0)
//                        && (a1.getSaldoInicial().doubleValue() == 0)) {
//                    balance.remove(i);
//                }
//                i++;
//            }
//        }
        AuxiliarCarga a = new AuxiliarCarga();

        a.setTotal(
                "******** Total *********");
        a.setCuenta(
                "");
        a.setSaldoInicial(
                new BigDecimal(totalSi));
        a.setSaldoFinal(
                new BigDecimal(totalSf));
        a.setIngresos(
                new BigDecimal(totalIngresos));
        a.setEgresos(
                new BigDecimal(totalEgresos));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        try {
            DocumentoPDF pdf;
            pdf = new DocumentoPDF("SITUACION  PATRIMONIAL\n AL " + sdf.format(hasta)+ "\n VALORES EXPRESADOS EN "+configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CUENTA"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEBITOS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CREDITOS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO FINAL"));

            for (AuxiliarCarga b : balance) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, b.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, b.getTotal()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoInicial().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getIngresos().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getEgresos().doubleValue()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoFinal().doubleValue()));
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoInicial().doubleValue()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getIngresos().doubleValue()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEgresos().doubleValue()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoFinal().doubleValue()));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, null);
            reporte=pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(SituacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        balance.add(a);

        return null;
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
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    /**
     *
     *
     * /
     *
     **
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
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
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the centrocosto
     */
    public String getCentrocosto() {
        return centrocosto;
    }

    /**
     * @param centrocosto the centrocosto to set
     */
    public void setCentrocosto(String centrocosto) {
        this.centrocosto = centrocosto;
    }

    /**
     * @return the cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    /**
     * @return the balance
     */
    public List<AuxiliarCarga> getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(List<AuxiliarCarga> balance) {
        this.balance = balance;
    }

    /**
     * @return the ceros
     */
    public boolean isCeros() {
        return ceros;
    }

    /**
     * @param ceros the ceros to set
     */
    public void setCeros(boolean ceros) {
        this.ceros = ceros;
    }

    /**
     * @return the nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
    }
}
