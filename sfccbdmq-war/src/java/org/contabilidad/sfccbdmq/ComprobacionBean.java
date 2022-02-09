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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "comprobacionSfccbdmq")
@ViewScoped
public class ComprobacionBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ComprobacionBean() {
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
    private String auxiliar;
    private String centrocosto;
    private String cuenta;
    private String nivel;
    private double saldoInicial;
    private boolean ceros;
    private boolean analisis;
    private Resource reporte;
    private Resource reporteXls;

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
        String nombreForma = "ComprobacionVista";
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
        auxiliar = null;
//        if (proveedoreBean.getProveedor() != null) {
//            auxiliar = proveedoreBean.getProveedor().getEmpresa().getRuc();
//        }
//        if (empleadosBean.getEmpleadoSeleccionado() != null) {
//            auxiliar = empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin();
//        }
//Para los asientos iniciales buscar los asientos iniciales + movimientos desde el inicio del año
        Calendar cal = Calendar.getInstance();
        cal.setTime(desde);
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        Calendar fechaApertura = Calendar.getInstance();
        fechaApertura.set(anio, 0, 1);

        Calendar fechaDesdeAntes = Calendar.getInstance();
        fechaDesdeAntes.set(anio, mes, dia);
        fechaDesdeAntes.add(Calendar.DATE, -1);

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

//        Calendar cAnioPedido = Calendar.getInstance();
//        cAnioPedido.setTime(desde);
//        Calendar cAnioPeriodo = Calendar.getInstance();
//        cAnioPeriodo.setTime(configuracionBean.getConfiguracion().getPinicial());
//        boolean esEsteAnio = cAnioPedido.get(Calendar.YEAR) == cAnioPeriodo.get(Calendar.YEAR);
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
            aTotales.setActivos(new BigDecimal(0));
            aTotales.setPasivos(new BigDecimal(0));
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
                    a.setIngresos(new BigDecimal(0));
                    a.setEgresos(new BigDecimal(0));
                    a.setSaldoAcreedor(new BigDecimal(0));
                    a.setSaldoDeudor(new BigDecimal(0));
                    a.setSaldoInicial(new BigDecimal(0));
                    a.setSaldoFinal(new BigDecimal(0));
                    a.setActivos(new BigDecimal(0));
                    a.setPasivos(new BigDecimal(0));
                    // busca las sumas
                    // saldo Inicial
                    
                    parametros = new HashMap();
                    parametros.put(";where", "o.cuenta like :codigo and o.cabecera.tipo.equivalencia.codigo='I' and o.fecha=:inicio");
                    parametros.put("inicio", fechaApertura.getTime());
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put(";campo", "(o.valor * o.signo)");
                    double valorInicio = ejbRenglones.sumarCampo(parametros).doubleValue();

                    parametros = new HashMap();
                    parametros.put(";where", "o.cuenta like :codigo and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha between :inicio and :desde");
                    parametros.put("inicio", fechaApertura.getTime());
                    parametros.put("desde", fechaDesdeAntes.getTime());
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put(";campo", "(o.valor * o.signo)");
                    double valorMovimientos = ejbRenglones.sumarCampo(parametros).doubleValue();
                    double totalInicio = valorInicio + valorMovimientos;
                    a.setSaldoInicial(new BigDecimal(totalInicio));

                    // debitos
                    parametros = new HashMap();
                    String where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor>0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.signo = 1";

//                    if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
//                        where += " and o.centrocosto=:centro";
//                        parametros.put("centro", centrocosto);
//                    }
//                    if (!((auxiliar == null) || (auxiliar.isEmpty()))) {
//                        where += " and o.auxiliar=:auxiliar";
//                        parametros.put("auxiliar", auxiliar);
//                    }
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
//                    a.setIngresos(ejbRenglones.sumarCampo(parametros));
                    double valor1 = ejbRenglones.sumarCampo(parametros).doubleValue();

                    where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor>0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.signo = -1";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
//                    a.setIngresos(ejbRenglones.sumarCampo(parametros));
                    double valor2 = ejbRenglones.sumarCampo(parametros).doubleValue();
                    double total = valor1 - valor2;
                    a.setIngresos(new BigDecimal(total));

//Creditos
                    where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.signo = 1";
                    parametros = new HashMap();
//                    if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
//                        where += " and o.centrocosto=:centro";
//                        parametros.put("centro", centrocosto);
//                    }
//                    if (!((auxiliar == null) || (auxiliar.isEmpty()))) {
//                        where += " and o.auxiliar=:auxiliar";
//                        parametros.put("auxiliar", auxiliar);
//                    }
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
//                    double egresos = ejbRenglones.sumarCampo(parametros).doubleValue() * -1;
                    double valor3 = ejbRenglones.sumarCampo(parametros).doubleValue();

//                    a.setEgresos(new BigDecimal(egresos));
                    where = "o.cuenta like :codigo and o.fecha between :desde and :hasta and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.signo = -1";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
//                    a.setIngresos(ejbRenglones.sumarCampo(parametros));
                    double valor4 = ejbRenglones.sumarCampo(parametros).doubleValue();
                    double total2 = valor3 - valor4;
                    double total3 = total2 * -1;
                    a.setEgresos(new BigDecimal(total3));

                    a.setImputable(c.getImputable());
                    a.setSaldoFinal(new BigDecimal(a.getSaldoInicial().doubleValue() + a.getIngresos().doubleValue() - total3));
                    if (ceros) {
                        balance.add(a);
                        aTotales.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + aTotales.getIngresos().doubleValue()));
                        aTotales.setEgresos(new BigDecimal(a.getEgresos().doubleValue() + aTotales.getEgresos().doubleValue()));
                        aTotales.setSaldoAcreedor(new BigDecimal(a.getSaldoAcreedor().doubleValue() + aTotales.getSaldoAcreedor().doubleValue()));
                        aTotales.setSaldoDeudor(new BigDecimal(a.getSaldoDeudor().doubleValue() + aTotales.getSaldoDeudor().doubleValue()));
                        aTotales.setSaldoInicial(new BigDecimal(a.getSaldoInicial().doubleValue() + aTotales.getSaldoInicial().doubleValue()));
                        aTotales.setSaldoFinal(new BigDecimal(a.getSaldoFinal().doubleValue() + aTotales.getSaldoFinal().doubleValue()));
                        aTotales.setActivos(new BigDecimal(a.getActivos().doubleValue() + aTotales.getActivos().doubleValue()));
                        aTotales.setPasivos(new BigDecimal(a.getPasivos().doubleValue() + aTotales.getPasivos().doubleValue()));
                    } else {
                        if ((a.getEgresos().doubleValue() == 0)
                                && (a.getIngresos().doubleValue() == 0)
                                && (a.getEgresos().doubleValue() == 0)
                                && (a.getSaldoInicial().doubleValue() == 0)
                                && (a.getSaldoFinal().doubleValue() == 0)
                                && (a.getActivos().doubleValue() == 0)
                                && (a.getPasivos().doubleValue() == 0)
                                && (a.getSaldoDeudor().doubleValue() == 0)
                                && (a.getSaldoAcreedor().doubleValue() == 0)) {

                        } else {
                            balance.add(a);
                            aTotales.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + aTotales.getIngresos().doubleValue()));
                            aTotales.setEgresos(new BigDecimal(a.getEgresos().doubleValue() + aTotales.getEgresos().doubleValue()));
                            aTotales.setSaldoAcreedor(new BigDecimal(a.getSaldoAcreedor().doubleValue() + aTotales.getSaldoAcreedor().doubleValue()));
                            aTotales.setSaldoDeudor(new BigDecimal(a.getSaldoDeudor().doubleValue() + aTotales.getSaldoDeudor().doubleValue()));
                            aTotales.setSaldoInicial(new BigDecimal(a.getSaldoInicial().doubleValue() + aTotales.getSaldoInicial().doubleValue()));
                            aTotales.setSaldoFinal(new BigDecimal(a.getSaldoFinal().doubleValue() + aTotales.getSaldoFinal().doubleValue()));
                            aTotales.setActivos(new BigDecimal(a.getActivos().doubleValue() + aTotales.getActivos().doubleValue()));
                            aTotales.setPasivos(new BigDecimal(a.getPasivos().doubleValue() + aTotales.getPasivos().doubleValue()));
                        }
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");

            try {
                DocumentoPDF pdf;
                DocumentoXLS xls;

                pdf = new DocumentoPDF("BALANCE DE COMPROBACION\n AL " + sdf.format(hasta) + "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
                xls = new DocumentoXLS("BALANCE DE COMPROBACION");

                List<AuxiliarReporte> columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
                List<AuxiliarReporte> camposXls = new LinkedList<>();

                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CUENTA"));
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO INICIAL"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEBITOS"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CREDITOS"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO FINAL"));

                /*Para el XLS*/
                camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CUENTA"));
                camposXls.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO INICIAL"));
                camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEBITOS"));
                camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CREDITOS"));
                camposXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO FINAL"));
                xls.agregarFila(camposXls, true);

                for (AuxiliarCarga b : balance) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, b.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, b.getTotal()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoInicial().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getIngresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getEgresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, b.getSaldoFinal().doubleValue()));

                    //                    /*Para documernto XLS*/
                    camposXls = new LinkedList<>();
                    camposXls.add(new AuxiliarReporte("String", b.getCuenta() != null ? b.getCuenta() : ""));
                    camposXls.add(new AuxiliarReporte("String", b.getTotal() != null ? b.getTotal() : ""));
                    camposXls.add(new AuxiliarReporte("String", b.getSaldoInicial() != null ? df.format(b.getSaldoInicial().doubleValue()) : ""));
                    camposXls.add(new AuxiliarReporte("String", b.getIngresos() != null ? df.format(b.getIngresos().doubleValue()) : ""));
                    camposXls.add(new AuxiliarReporte("String", b.getEgresos() != null ? df.format(b.getEgresos().doubleValue()) : ""));
                    camposXls.add(new AuxiliarReporte("String", b.getSaldoFinal() != null ? df.format(b.getSaldoFinal().doubleValue()) : ""));
                    xls.agregarFila(camposXls, false);

                    if (analisis) {
                        // pide analisis
                        List<AuxiliarAsignacion> lanalisis = getListaAnalisis(b);
                        if (lanalisis != null) {
//                            pdf.agregaParrafo("\nANALISIS\n");
                            for (AuxiliarAsignacion au : lanalisis) {
                                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, au.getCodigo()));
                                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, au.getNombre()));
                                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, au.getValor()));

                                camposXls = new LinkedList<>();
                                camposXls.add(new AuxiliarReporte("String", au.getCodigo() != null ? au.getCodigo() : ""));
                                camposXls.add(new AuxiliarReporte("String", au.getNombre() != null ? au.getNombre() : ""));
                                camposXls.add(new AuxiliarReporte("String", ""));
                                camposXls.add(new AuxiliarReporte("String", ""));
                                camposXls.add(new AuxiliarReporte("String", ""));
                                camposXls.add(new AuxiliarReporte("Double", au.getValor()));
                                xls.agregarFila(camposXls, false);

                            }
//                            pdf.agregaParrafo("\n_______________________________________________________________\n");
                        }
                    }
                }
                if (!((nivel == null) || (nivel.isEmpty()))) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getSaldoInicial().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getIngresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getEgresos().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, aTotales.getSaldoFinal().doubleValue()));

                    /*Para documento XLS*/
                    camposXls = new LinkedList<>();
                    camposXls.add(new AuxiliarReporte("String", ""));
                    camposXls.add(new AuxiliarReporte("String", "TOTAL"));
                    camposXls.add(new AuxiliarReporte("String", aTotales.getSaldoInicial() != null ? df.format(aTotales.getSaldoInicial().doubleValue()) : ""));
                    camposXls.add(new AuxiliarReporte("String", aTotales.getIngresos() != null ? df.format(aTotales.getIngresos().doubleValue()) : ""));
                    camposXls.add(new AuxiliarReporte("String", aTotales.getEgresos() != null ? df.format(aTotales.getEgresos().doubleValue()) : ""));
                    camposXls.add(new AuxiliarReporte("String", aTotales.getSaldoFinal() != null ? df.format(aTotales.getSaldoFinal().doubleValue()) : ""));
                    xls.agregarFila(camposXls, false);
                }
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, null);

                reporte = pdf.traerRecurso();
                reporteXls = xls.traerRecurso();
            } catch (IOException | DocumentException ex) {
                Logger.getLogger(SituacionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!((nivel == null) || (nivel.isEmpty()))) {
                balance.add(aTotales);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ComprobacionBean.class.getName()).log(Level.SEVERE, null, ex);
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
                        parametros.put(";suma", "sum(o.valor*o.signo)");
                        List<Object[]> listaObjetos = ejbRenglones.sumar(parametros);
                        laux = new LinkedList<>();
                        for (Object[] o : listaObjetos) {
                            String aux = (String) o[0];
                            double valor = ((BigDecimal) o[1]).doubleValue();
                            AuxiliarAsignacion ax = new AuxiliarAsignacion();
                            ax.setCodigo(aux);

                            ax.setValor(valor);
                            if (!((aux == null) || (aux.isEmpty()))) {
                                if (valor != 0) {
                                    // ver el tipo de auxiliar
                                    VistaAuxiliares v = seguridadbean.traerAuxiliar(aux);
                                    ax.setNombre(v == null ? "" : v.getNombre());
                                    laux.add(ax);
                                }
                            }
                        }
                        if (laux.isEmpty()) {
                            laux = null;
                        }
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ComprobacionBean.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return laux;
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

    /**
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }
}
