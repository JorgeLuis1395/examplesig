/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
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
import java.util.Calendar;
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
@ManagedBean(name = "balanceMensualSfccbdmq")
@ViewScoped
public class BalanceMensualBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public BalanceMensualBean() {
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
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoreBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    private String auxiliar;
    private String centrocosto;
    private String cuenta;
    private String nivel;
    private double saldoInicial;
    private boolean ceros;
    private boolean analisis;
    private int mes;
    private int anio;
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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        mes = c.get(Calendar.MONTH) + 1;
        anio = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        String nombreForma = "BalanceMensualVista";
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
//        parametros.put(";where", "o.tipo=1");
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
            Logger.getLogger(BalanceMensualBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (mes <= 0) {
            MensajesErrores.advertencia("Es necesario mes");
            return null;
        }
        if (anio <= 0) {
            MensajesErrores.advertencia("Es necesario anio");
            return null;
        }
        auxiliar = null;

        balance = new LinkedList<>();
        // buscar formatos
        double totalSi = 0;
        double totalIngresos = 0;
        double totalEgresos = 0;
        double totalSf = 0;
        String cuentaResutados = configuracionBean.getConfiguracion().getCtaresultado();
        cuentasBean.traerNivel(cuentaResutados);
        Formatos nivelFormatos = cuentasBean.getNivel();
        Map parametros = new HashMap();

        Calendar cInicioMes = Calendar.getInstance();
        cInicioMes.set(anio, mes - 1, 1);
        Calendar cFinMes = Calendar.getInstance();
        cFinMes.set(anio, mes, 1);
        cFinMes.add(Calendar.DATE, -1);
        Calendar cFinMesAnterior = Calendar.getInstance();
        cFinMesAnterior.set(anio, mes - 1, 1);
        cFinMesAnterior.add(Calendar.DATE, -1);
//        parametros.put(";where", "o.tipo=1");
        parametros.put(";orden", "o.inicial asc");
        try {
            // utilidad o perdida 
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            double utilidad = 0;
            AuxiliarCarga aTotales = new AuxiliarCarga();
            aTotales.setIngresos(new BigDecimal(0));
            aTotales.setEgresos(new BigDecimal(0));
            aTotales.setSaldoAcreedor(new BigDecimal(0));
            aTotales.setSaldoDeudor(new BigDecimal(0));
            aTotales.setSaldoInicial(new BigDecimal(0));
            aTotales.setSaldoFinal(new BigDecimal(0));
            aTotales.setTotal("Totales");
            for (Formatos f : lFormatos) {
                parametros = new HashMap();
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
                parametros.put(";orden", "o.codigo");

                List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
                for (Cuentas c : lCuentas) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setTotal(c.getNombre());
                    a.setCuenta(c.getCodigo());

                    // creditos Mes
                    String where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor>0 ";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", cInicioMes.getTime());
                    parametros.put("hasta", cFinMes.getTime());
                    parametros.put(";campo", "o.valor");
                    double ingresos = ejbRenglones.sumarCampo(parametros).doubleValue();
                    a.setIngresos(new BigDecimal(ingresos));
                    // creditos Acumulados
                    where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor>0 ";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                    parametros.put("hasta", cFinMes.getTime());
                    parametros.put(";campo", "o.valor");
                    double ingresosAcumulado = ejbRenglones.sumarCampo(parametros).doubleValue();
                    a.setSaldoDeudor(new BigDecimal(ingresosAcumulado));
                    // mes egresos
                    // debitos Mes
                    where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor<0 ";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", cInicioMes.getTime());
                    parametros.put("hasta", cFinMes.getTime());
                    parametros.put(";campo", "o.valor");
                    double egresos = ejbRenglones.sumarCampo(parametros).doubleValue() * -1;
                    a.setEgresos(new BigDecimal(egresos));
                    // creditos Acumulados
                    where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor<0 ";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                    parametros.put("hasta", cFinMes.getTime());
                    parametros.put(";campo", "o.valor");
                    double egresosAcumulados = ejbRenglones.sumarCampo(parametros).doubleValue() * -1;
                    a.setSaldoAcreedor(new BigDecimal(egresosAcumulados));

                    double saldoMes = ingresos - egresos;
                    double saldoAcumulado = ingresosAcumulado - egresosAcumulados;
                    a.setSaldoInicial(new BigDecimal(saldoMes));
                    a.setSaldoFinal(new BigDecimal(saldoAcumulado));
//                    a.setSaldoFinal(new BigDecimal(a.getSaldoInicial().doubleValue() + a.getIngresos().doubleValue() - egresos));
                    if (ceros) {
                        balance.add(a);
                        if (!((this.nivel == null) || (this.nivel.isEmpty()))) {
                            aTotales.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + aTotales.getIngresos().doubleValue()));
                            aTotales.setEgresos(new BigDecimal(a.getEgresos().doubleValue() + aTotales.getEgresos().doubleValue()));
                            aTotales.setSaldoAcreedor(new BigDecimal(a.getSaldoAcreedor().doubleValue() + aTotales.getSaldoAcreedor().doubleValue()));
                            aTotales.setSaldoDeudor(new BigDecimal(a.getSaldoDeudor().doubleValue() + aTotales.getSaldoDeudor().doubleValue()));
                            aTotales.setSaldoInicial(new BigDecimal(a.getSaldoInicial().doubleValue() + aTotales.getSaldoInicial().doubleValue()));
                            aTotales.setSaldoFinal(new BigDecimal(a.getSaldoFinal().doubleValue() + aTotales.getSaldoFinal().doubleValue()));
                        } else {
                            if (c.getImputable()) {
                                aTotales.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + aTotales.getIngresos().doubleValue()));
                                aTotales.setEgresos(new BigDecimal(a.getEgresos().doubleValue() + aTotales.getEgresos().doubleValue()));
                                aTotales.setSaldoAcreedor(new BigDecimal(a.getSaldoAcreedor().doubleValue() + aTotales.getSaldoAcreedor().doubleValue()));
                                aTotales.setSaldoDeudor(new BigDecimal(a.getSaldoDeudor().doubleValue() + aTotales.getSaldoDeudor().doubleValue()));
                                aTotales.setSaldoInicial(new BigDecimal(a.getSaldoInicial().doubleValue() + aTotales.getSaldoInicial().doubleValue()));
                                aTotales.setSaldoFinal(new BigDecimal(a.getSaldoFinal().doubleValue() + aTotales.getSaldoFinal().doubleValue()));
                            }
                        }

                    } else {
                        if ((a.getEgresos().doubleValue() == 0)
                                && (a.getIngresos().doubleValue() == 0)
                                && (a.getSaldoDeudor().doubleValue() == 0)
                                && (a.getSaldoAcreedor().doubleValue() == 0)) {

                        } else {
                            balance.add(a);
                            // deberia sumar totales
                            if (!((this.nivel == null) || (this.nivel.isEmpty()))) {
                                aTotales.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + aTotales.getIngresos().doubleValue()));
                                aTotales.setEgresos(new BigDecimal(a.getEgresos().doubleValue() + aTotales.getEgresos().doubleValue()));
                                aTotales.setSaldoAcreedor(new BigDecimal(a.getSaldoAcreedor().doubleValue() + aTotales.getSaldoAcreedor().doubleValue()));
                                aTotales.setSaldoDeudor(new BigDecimal(a.getSaldoDeudor().doubleValue() + aTotales.getSaldoDeudor().doubleValue()));
                                aTotales.setSaldoInicial(new BigDecimal(a.getSaldoInicial().doubleValue() + aTotales.getSaldoInicial().doubleValue()));
                                aTotales.setSaldoFinal(new BigDecimal(a.getSaldoFinal().doubleValue() + aTotales.getSaldoFinal().doubleValue()));
                            } else {
                                if (c.getImputable()) {
                                    aTotales.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + aTotales.getIngresos().doubleValue()));
                                    aTotales.setEgresos(new BigDecimal(a.getEgresos().doubleValue() + aTotales.getEgresos().doubleValue()));
                                    aTotales.setSaldoAcreedor(new BigDecimal(a.getSaldoAcreedor().doubleValue() + aTotales.getSaldoAcreedor().doubleValue()));
                                    aTotales.setSaldoDeudor(new BigDecimal(a.getSaldoDeudor().doubleValue() + aTotales.getSaldoDeudor().doubleValue()));
                                    aTotales.setSaldoInicial(new BigDecimal(a.getSaldoInicial().doubleValue() + aTotales.getSaldoInicial().doubleValue()));
                                    aTotales.setSaldoFinal(new BigDecimal(a.getSaldoFinal().doubleValue() + aTotales.getSaldoFinal().doubleValue()));
                                }
                            }
                        }
                    }
                } // fin for

            } // fin formatos
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            try {
                DocumentoPDF pdf;
                String usuario = seguridadbean.getLogueado().getUserid();
                String empresa = configuracionBean.getConfiguracion().getNombre();
                String titulo = "BALANCE DE SUMAS Y SALDOS";
                String parTitulos = "AL:" + sdf.format(cFinMes.getTime())
                        + "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado();
                pdf = new DocumentoPDF(titulo, empresa, parTitulos, usuario, false);
//                pdf = new DocumentoPDF("BALANCE DE SUMAS Y SALDOS\n AL " + sdf.format(cFinMes.getTime()) + "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
                List<AuxiliarReporte> columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CUENTA"));
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "MES DEBITO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "MES CREDITO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ACUMULADO DEBITO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ACUMULADO CREDITO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO"));

                for (AuxiliarCarga b : balance) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, b.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, b.getTotal()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getIngresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getEgresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoInicial().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoDeudor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoAcreedor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoFinal().doubleValue()));
                }
                if (!((nivel == null) || (nivel.isEmpty()))) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getIngresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getEgresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getSaldoInicial().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getSaldoDeudor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getSaldoAcreedor().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getSaldoFinal().doubleValue()));
                }
                pdf.agregarTablaReporte(titulos, columnas, 8, 100, null);
                reporte = pdf.traerRecurso();
            } catch (IOException | DocumentException ex) {
                Logger.getLogger(SituacionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!((nivel == null) || (nivel.isEmpty()))) {
                balance.add(aTotales);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BalanceMensualBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     *
     *
     * /
     *
     **
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
     * @return the analisis
     */
    public boolean isAnalisis() {
        return analisis;
    }

    /**
     * @param analisis the analisis to set
     */
    public void setAnalisis(boolean analisis) {
        this.analisis = analisis;
    }

    /**
     * @return the proveedoreBean
     */
    public ProveedoresBean getProveedoreBean() {
        return proveedoreBean;
    }

    /**
     * @param proveedoreBean the proveedoreBean to set
     */
    public void setProveedoreBean(ProveedoresBean proveedoreBean) {
        this.proveedoreBean = proveedoreBean;
    }

    /**
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
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
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
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
