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
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
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
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "saldosSfccbdmq")
@ViewScoped
public class SaldosBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public SaldosBean() {
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
    private Date desde;
    private Date hasta;
    private String cuenta;
    private String nivel;
    private double saldoInicial;
    private boolean ceros;
    private boolean analisis;
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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        String nombreForma = "SaldosVista";
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
            Codigos codigo = codigosBean.traerCodigo(Constantes.NOMBRE_NIVEL_CUENTAS, "01");
            if (codigo == null) {
                MensajesErrores.advertencia("Configure el nombre de los niveles de cuentas Maestro:" + Constantes.NOMBRE_NIVEL_CUENTAS + " Código :01");
                return null;
            }
            String[] auxCodigo = codigo.getParametros().split("#");
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
            int sizeNombres = auxCodigo.length;
            if (size != sizeNombres) {
                MensajesErrores.advertencia("Configure el nombre de los niveles de cuentas  con los niveles del formato");
                return null;
            }
            SelectItem[] items = new SelectItem[size + 1];

            int i = 0;
            int j = 0;
            items[i++] = new SelectItem("", "--- Seleccione uno ---");
            String que = "";
            String totalL = "";
            for (String x : sinpuntos) {
                String longitud = "";
                que += x;
                longitud = que.replace("X", "_");
                items[i++] = new SelectItem(longitud, auxCodigo[j++]);

            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BalanceComprobacionBean.class.getName()).log(Level.SEVERE, null, ex);
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
        balance = new LinkedList<>();
        // buscar formatos

        double totalSf = 0;
        Map parametros = new HashMap();

        if (!((this.nivel == null) || (this.nivel.isEmpty()))) {
            parametros.put(";where", "o.codigo like :codigo");
            parametros.put("codigo", nivel);
        } else {
            if (!((cuenta == null) || (cuenta.isEmpty()))) {
                parametros.put(";where", "o.codigo like :codigo");
                parametros.put("codigo", cuenta + "%");
            }
        }
        parametros.put(";orden", "o.codigo");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
//            DocumentoPDF pdf;
//            pdf = new DocumentoPDF("BALANCE DE SALDOS  \n DESDE :" + sdf.format(desde) + "HASTA :" + sdf.format(hasta) + "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());            pdf = new DocumentoPDF();
            String usuario = seguridadbean.getLogueado().getUserid();
            String empresa = configuracionBean.getConfiguracion().getNombre();
            String titulo = "BALANCE DE SALDOS";
            String parTitulos = "DESDE :" + sdf.format(desde)
                    + " HASTA : " + sdf.format(hasta)
                    + "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado();
            DocumentoPDF pdf;
            pdf = new DocumentoPDF(titulo, empresa, parTitulos, usuario, true);
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CUENTA"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO FINAL"));
            List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : lCuentas) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setTotal(c.getNombre());
                a.setCuenta(c.getCodigo());
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :codigo");
                parametros.put("codigo", c.getCodigo() + "%");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put(";campo", "o.valor");
                double saldo = ejbRenglones.sumarCampo(parametros).doubleValue();
                totalSf += saldo;
                a.setImputable(c.getImputable());
//                if (c.getImputable()) {
//                    a.setImputable(c.getAuxiliares() != null);
//                }
                a.setSaldoFinal(new BigDecimal(saldo));
                balance.add(a);
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTotal()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, saldo));
                if (analisis) {
                    if (c.getImputable()) {
                        // movimientos
                        List<AuxiliarAsignacion> listaAux = getListaAnalisis(a);
                        if ((listaAux != null) && (listaAux.size() > 0)) {
//                            pdf.agregarTablaReporte(titulos, columnas, 3, 100, null);
//                            columnas = new LinkedList<>();
//                            titulos = new LinkedList<>();
                            List<AuxiliarReporte> columnas1 = new LinkedList<>();
                            List<AuxiliarReporte> titulos1 = new LinkedList<>();
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO FINAL"));
                            pdf.agregarLinea();
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "________"));
                            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "____________________________________________"));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "_________________"));
//                            pdf.agregarFila(3, columnas1, true);
                            for (AuxiliarAsignacion au : listaAux) {
                                if (au.getCodigo().contains("TOTAL")) {
                                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, au.getCodigo()));
                                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, au.getNombre()));
                                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, au.getValor()));
                                } else {
                                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, au.getCodigo()));
                                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, au.getNombre()));
                                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, au.getValor()));
                                }
                            }
//                            pdf.agregarLinea();
//                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "________"));
//                            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "____________________________________________"));
//                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "_________________"));
//                            pdf.agregarTablaReporte(titulos1, columnas1, 3, 100, "Auxiliar Cuenta : " + c.getCodigo());
                        }
                    }
                }
            }
            AuxiliarCarga a = new AuxiliarCarga();
            a.setTotal("******** Total *********");
            a.setCuenta("");
            a.setImputable(false);
            a.setSaldoFinal(new BigDecimal(totalSf));
            balance.add(a);
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCuenta()));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTotal()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoFinal().doubleValue()));
            pdf.agregarTablaReporte(titulos, columnas, 3, 100, null);
            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(SaldosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public List<AuxiliarAsignacion> getListaAnalisis(AuxiliarCarga a) {
        List<AuxiliarAsignacion> laux = null;
        if (a == null) {
            return null;
        }
        if (analisis) {
            if (a.isImputable()) {
                // ver si cuenta tiene auxiliar
                Cuentas c = cuentasBean.traerCodigo(a.getCuenta());
                if (c.getAuxiliares() != null) {
                    try {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.cuenta = :codigo and o.fecha between :desde and :hasta");
                        parametros.put("codigo", a.getCuenta());
                        parametros.put("desde", desde);
                        parametros.put("hasta", hasta);
                        parametros.put(";campo", "o.auxiliar");
                        parametros.put(";suma", "sum(o.valor)");
                        List<Object[]> listaObjetos = ejbRenglones.sumar(parametros);
                        laux = new LinkedList<>();
                        double total = 0;
                        for (Object[] o : listaObjetos) {
                            String aux = (String) o[0];
                            double valor = ((BigDecimal) o[1]).doubleValue();
                            total += valor;
                            AuxiliarAsignacion ax = new AuxiliarAsignacion();
                            ax.setCodigo(aux);

                            ax.setValor(valor);
                            if (!((aux == null) || (aux.isEmpty()))) {
                                VistaAuxiliares v = seguridadbean.traerAuxiliar(aux);
                                ax.setNombre(v == null ? "" : v.getNombre());
                                laux.add(ax);
                            }
                        }
                        if (laux.isEmpty()) {
                            laux = null;
                        } else {
                            AuxiliarAsignacion ax = new AuxiliarAsignacion();
                            ax.setCodigo("TOTAL");
                            ax.setValor(total);
                            laux.add(ax);
                        }
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(SaldosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return laux;
    }

    public boolean getVer(AuxiliarCarga a) {
        if (analisis) {
            if (a.isImputable()) {
                return true;
            }
        }
        return false;
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
