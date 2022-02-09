/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.faces.application.Resource;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.auxiliares.sfccbdmq.AuxiliarAfectacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.ReporteAsientoBean;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "consultarAsientosSfccbdmq")
@ViewScoped
public class ConsultarAsientosBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ConsultarAsientosBean() {

        cabeceras = new LazyDataModel<Cabeceras>() {

            @Override
            public List<Cabeceras> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Cabeceras> cabeceras;
    private List<Renglones> renglones;
    private List<Renglones> renglonesResumido;
    private Detallecertificaciones detalle;
    private List<AuxiliarCarga> totales;
    private Cabeceras cabecera;
    private int anio;
    private boolean imprimirNuevo;
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private RenglonesFacade ejbRenglones;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private Tipoasiento tipoAsiento;
    private Codigos modulo;
    private Integer numero;
    private Integer numeroModulo;
    private Codigos equivalencia;
    private boolean conAuxiliares = true;
    private String descripcion;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{utilitarioAsientoSfccbdmq}")
    private ReporteAsientoBean imprimeBean;
    private Resource reporte;
    private Resource reporteXls;
    private Resource reporteResumido;
    private Resource reporteResumidoXls;

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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "ConsultaAsientosVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
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

    public String buscar() {

        cabeceras = new LazyDataModel<Cabeceras>() {
            @Override
            public List<Cabeceras> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc,o.tipo.nombre asc,o.numero asc");
//                    parametros.put(";orden", "o.fecha desc,o.tipo.nombre asc,o.numero asc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                if (tipoAsiento != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipoAsiento);
                }
                if (equivalencia != null) {
                    where += " and o.tipo.equivalencia=:equivalencia";
                    parametros.put("equivalencia", equivalencia);
                }
                if (modulo != null) {
                    where += " and o.modulo=:modulo";
                    parametros.put("modulo", modulo);

                }
                if (!((numeroModulo == null) || (numeroModulo <= 0))) {
                    where += " and  o.idmodulo=:idmodulo";
                    parametros.put("idmodulo", numeroModulo);
                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", numero);
                }
                if (!((descripcion == null) || (descripcion.isEmpty()))) {
                    where += " and upper(o.descripcion) like :descripcion";
                    parametros.put("descripcion", "%" + descripcion.toUpperCase() + "%");
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCabeceras.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }
                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                cabeceras.setRowCount(total);
                try {
                    return ejbCabeceras.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        return null;
    }

    // nuevo imprimir
    public String imprimir(Cabeceras cabecera) {
        reporte = null;
        reporteXls = null;
        reporteResumido = null;
        reporteResumidoXls = null;
        this.cabecera = cabecera;
        List<AuxiliarAfectacion> listaAuxiliar = new LinkedList<>();
        try {
            renglones = new LinkedList<>();

            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put(";orden", "o.cuenta asc");
//            parametros.put(";orden", "o.valor desc,o.cuenta asc");
            parametros.put("cabecera", cabecera);
            if (conAuxiliares) {
                renglones = ejbRenglones.encontarParametros(parametros);
                // auxiliar y la cuenta
                for (Renglones r : renglones) {
                    Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                    r.setNombre(c.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        double valor = Math.abs(r.getValor().doubleValue());
                        if (r.getSigno() == -1) {
                            r.setCreditos(valor);
                        } else {
                            r.setDebitos(valor);
                        }

                    } else {
                        double valor = Math.abs(r.getValor().doubleValue());
                        if (r.getSigno() == -1) {
                            r.setDebitos(valor * r.getSigno());
                        } else {
                            r.setCreditos(valor);
                        }
                    }
                    if (c.getAuxiliares() != null) {

                        VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                        r.setAuxiliarNombre(v == null ? "" : v.getNombre());

                    } else {
                        if (r.getAuxiliar() != null) {
                            VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                            r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                        }
                    }
                    // ver el compromiso
                }
            } else {
                List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);

                for (Renglones r : listaRenglones) {
                    colocaRasImpresion(r);
                    // ver el compromiso
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsultarAsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getImprimeBean().setCabecera(cabecera);
        getImprimeBean().setEmpresa(configuracionBean.getConfiguracion());
        getImprimeBean().setUsuario(seguridadbean.getLogueado().getUserid());
        getImprimeBean().setRenglones(renglones);
        getImprimeBean().setIncluyeAux(conAuxiliares);
        getImprimeBean().imprimirReporte();
        getImprimeBean().exportar();
        reporte = getImprimeBean().getAsiento();
        reporteXls = getImprimeBean().getAsientoXls();
        formulario.insertar();

        return null;
    }

    // fin nuevo
    public String imprimir1(Cabeceras cabecera) {
        this.cabecera = cabecera;
        try {
            renglones = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put(";orden", "o.cuenta asc");
//            parametros.put(";orden", "o.valor desc,o.cuenta asc");
            parametros.put("cabecera", cabecera);
            if (conAuxiliares) {
                renglones = ejbRenglones.encontarParametros(parametros);
                // auxiliar y la cuenta
                for (Renglones r : renglones) {
                    Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                    r.setNombre(c.getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        double valor = Math.abs(r.getValor().doubleValue());
                        r.setDebitos(valor);

                    } else {
                        double valor = Math.abs(r.getValor().doubleValue());
                        r.setCreditos(valor);
                    }
                    if (c.getAuxiliares() != null) {
                        VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                        r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                    } else {
                        if (r.getAuxiliar() != null) {
                            VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                            r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                        }
                    }
                }
            } else {
                List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);

                for (Renglones r : listaRenglones) {
                    colocaRasImpresion(r);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsultarAsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map parametros = new HashMap();

        parametros.put(
                "empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put(
                "expresado", "Asiento contable : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put(
                "nombrelogo", "logo-new.png");
        parametros.put(
                "usuario", seguridadbean.getLogueado().getUserid());
        parametros.put(
                "fecha", cabecera.getFecha());
        parametros.put(
                "documento", cabecera.getTipo().getNombre() + " - " + cabecera.getNumero());
        parametros.put(
                //                "modulo", cabecera.getModulo().getNombre() );
                "modulo", cabecera.getModulo() == null ? "" : cabecera.getModulo().getNombre()
                + " - " + (cabecera.getIdmodulo() == null ? "" : cabecera.getIdmodulo().toString()));
        parametros.put(
                "descripcion", cabecera.getDescripcion());
        Calendar c = Calendar.getInstance();
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Diario.jasper"),
                renglones, "Diario" + String.valueOf(c.getTimeInMillis()) + ".pdf");

        formulario.insertar();
//        calculaTotales();
//        Renglones r = new Renglones();
//        r.setCuenta("TOTALES");
//        for (AuxiliarCarga a : totales) {
//            r.setCreditos(a.getIngresos().doubleValue());
//            r.setDebitos(a.getEgresos().doubleValue());
//        }
//        renglones.add(r);

        return null;
    }

    private void colocaRasImpresion(Renglones r) {
        for (Renglones r1 : renglones) {
            if (r1.getCuenta().equals(r.getCuenta())) {
                if (r.getValor().doubleValue() > 0) {
                    double valor = r1.getDebitos() + Math.abs(r.getValor().doubleValue()) * r.getSigno();
                    r1.setDebitos(valor);

                } else {
                    double valor = r1.getCreditos() + Math.abs(r.getValor().doubleValue()) * r.getSigno();
                    r1.setCreditos(valor);
                }
                return;
            }
        }
        // ver nombre dde la cuenta
        r.setNombre(cuentasBean.traerNombre(r.getCuenta()));
        if (r.getValor().doubleValue() > 0) {
            double valor = r.getValor().doubleValue();
            r.setDebitos(Math.abs(valor) * r.getSigno());
            r.setCreditos(0.0);
        } else {
            double valor = r.getValor().doubleValue();
            r.setCreditos(Math.abs(valor) * r.getSigno());
            r.setDebitos(0.0);
        }
        renglones.add(r);
    }

    public String imprimirResumido(Cabeceras cabecera) {
        reporte = null;
        reporteXls = null;
        reporteResumido = null;
        reporteResumidoXls = null;
        try {
            renglonesResumido = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabecera);
            parametros.put(";orden", "o.id asc");
            List<Renglones> renglonesDetallados = ejbRenglones.encontarParametros(parametros);
            if (!renglonesDetallados.isEmpty()) {
                for (Renglones r : renglonesDetallados) {
                    estaEnRenglones(r);
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("Asiento contable expresado en : " + configuracionBean.getConfiguracion().getExpresado());
            pdf.agregaParrafo("\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha de emisión: "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf.format(cabecera.getFecha())));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Documento: "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, cabecera.getTipo() == null ? "" : cabecera.getTipo().getNombre() + " - " + cabecera.getNumero()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Módulo: "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, cabecera.getModulo() == null ? "" : cabecera.getModulo().getNombre()
                    + " - " + (cabecera.getIdmodulo() == null ? "" : cabecera.getIdmodulo().toString())));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaParrafo("Descripción : " + cabecera.getDescripcion() + "\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglonesResumido != null) {
                if (!renglonesResumido.isEmpty()) {
                    for (Renglones r : renglonesResumido) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                            sumaCreditos += valor * -1;
                        }
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");
                    pdf.agregaParrafo("\n");
                }
            }

            reporteResumido = pdf.traerRecurso();

            DocumentoXLS xls = new DocumentoXLS("Asiento Resumido");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cuenta"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre Cuenta"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Referencia"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cent. Costo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Debe"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Haber"));
            xls.agregarFila(campos, true);

            if (renglonesResumido != null) {
                if (!renglonesResumido.isEmpty()) {
                    for (Renglones r : renglonesResumido) {
                        campos = new LinkedList<>();
                        campos.add(new AuxiliarReporte("String", r.getCuenta()));
                        campos.add(new AuxiliarReporte("String", cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                        campos.add(new AuxiliarReporte("String", r.getReferencia()));
                        campos.add(new AuxiliarReporte("String", r.getCentrocosto()));

                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            campos.add(new AuxiliarReporte("String", df.format(valor)));
                            campos.add(new AuxiliarReporte("String", "0"));
                            sumaDebitos += valor;
                        } else {
                            campos.add(new AuxiliarReporte("String", ""));
                            campos.add(new AuxiliarReporte("String", df.format(valor * -1)));
                            sumaCreditos += valor * -1;
                        }
                        xls.agregarFila(campos, false);
                    }
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", "TOTAL"));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", df.format(sumaDebitos)));
                    campos.add(new AuxiliarReporte("String", df.format(sumaCreditos)));
                }
            }
            reporteResumidoXls = xls.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsultarAsientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    private boolean estaEnRenglones(Renglones r1) {

        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglonesResumido) {
            if ((r.getCuenta().equals(r1.getCuenta()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }
        Renglones r = new Renglones();
        r.setCentrocosto(r1.getCentrocosto());
        r.setCuenta(r1.getCuenta());
        r.setCreditos(r1.getCreditos());
        r.setDebitos(r1.getDebitos());
        r.setNombre(r1.getNombre());
        r.setValor(r1.getValor());
        r.setReferencia(r1.getReferencia());
        renglonesResumido.add(r);
        return false;
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
     * @return the detalle
     */
    public Detallecertificaciones getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecertificaciones detalle) {
        this.detalle = detalle;
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
     * @return the cabeceras
     */
    public LazyDataModel<Cabeceras> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(LazyDataModel<Cabeceras> cabeceras) {
        this.cabeceras = cabeceras;
    }

    /**
     * @return the cabecera
     */
    public Cabeceras getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
    }

    /**
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
     * @return the formularioImpresion
     */
    public Formulario getFormularioImpresion() {
        return formularioImpresion;
    }

    /**
     * @param formularioImpresion the formularioImpresion to set
     */
    public void setFormularioImpresion(Formulario formularioImpresion) {
        this.formularioImpresion = formularioImpresion;
    }

    /**
     * @return the imprimirNuevo
     */
    public boolean isImprimirNuevo() {
        return imprimirNuevo;
    }

    /**
     * @param imprimirNuevo the imprimirNuevo to set
     */
    public void setImprimirNuevo(boolean imprimirNuevo) {
        this.imprimirNuevo = imprimirNuevo;
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
     * @return the tipoAsiento
     */
    public Tipoasiento getTipoAsiento() {
        return tipoAsiento;
    }

    /**
     * @param tipoAsiento the tipoAsiento to set
     */
    public void setTipoAsiento(Tipoasiento tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }

    /**
     * @return the modulo
     */
    public Codigos getModulo() {
        return modulo;
    }

    /**
     * @param modulo the modulo to set
     */
    public void setModulo(Codigos modulo) {
        this.modulo = modulo;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the numeroModulo
     */
    public Integer getNumeroModulo() {
        return numeroModulo;
    }

    /**
     * @param numeroModulo the numeroModulo to set
     */
    public void setNumeroModulo(Integer numeroModulo) {
        this.numeroModulo = numeroModulo;
    }

    /**
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
    }

    /**
     * @return the totales
     */
    public List<AuxiliarCarga> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCarga> totales) {
        this.totales = totales;
    }

    private void calculaTotales() {
        double creditos = 0;
        double debitos = 0;
        for (Renglones r : renglones) {
            creditos += r.getCreditos();
            debitos += r.getDebitos();
        }
        totales = new LinkedList<>();
        AuxiliarCarga a = new AuxiliarCarga();
        a.setTotal("Totales");
        a.setIngresos(new BigDecimal(Math.abs(creditos)));
        a.setEgresos(new BigDecimal(Math.abs(debitos)));
        totales.add(a);
    }

    private void calculaTotalesAnt() {
        double creditos = 0;
        double debitos = 0;
        for (Renglones r : renglones) {
            double valor = r.getValor().doubleValue();
            if (valor > 0) {
                creditos += valor;
            } else {
                debitos += valor;
            }
        }
        totales = new LinkedList<>();
        AuxiliarCarga a = new AuxiliarCarga();
        a.setTotal("Totales");
        a.setIngresos(new BigDecimal(Math.abs(creditos)));
        a.setEgresos(new BigDecimal(Math.abs(debitos)));
        totales.add(a);
    }

    public double getDebitos() {
        Cabeceras c = (Cabeceras) cabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera and o.valor>0");
        parametros.put("cabecera", c);
        parametros.put(";campo", "o.valor*o.signo");
        try {
            return ejbRenglones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(AsientosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getCreditos() {
        Cabeceras c = (Cabeceras) cabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera and o.valor<0");
        parametros.put("cabecera", c);
        parametros.put(";campo", "o.valor*o.signo");
        try {
            return ejbRenglones.sumarCampo(parametros).doubleValue() * -1;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(AsientosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the conAuxiliares
     */
    public boolean isConAuxiliares() {
        return conAuxiliares;
    }

    /**
     * @param conAuxiliares the conAuxiliares to set
     */
    public void setConAuxiliares(boolean conAuxiliares) {
        this.conAuxiliares = conAuxiliares;
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
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
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
     * @return the equivalencia
     */
    public Codigos getEquivalencia() {
        return equivalencia;
    }

    /**
     * @param equivalencia the equivalencia to set
     */
    public void setEquivalencia(Codigos equivalencia) {
        this.equivalencia = equivalencia;
    }

    /**
     * @return the imprimeBean
     */
    public ReporteAsientoBean getImprimeBean() {
        return imprimeBean;
    }

    /**
     * @param imprimeBean the imprimeBean to set
     */
    public void setImprimeBean(ReporteAsientoBean imprimeBean) {
        this.imprimeBean = imprimeBean;
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

    /**
     * @return the reporteResumido
     */
    public Resource getReporteResumido() {
        return reporteResumido;
    }

    /**
     * @param reporteResumido the reporteResumido to set
     */
    public void setReporteResumido(Resource reporteResumido) {
        this.reporteResumido = reporteResumido;
    }

    /**
     * @return the reporteResumidoXls
     */
    public Resource getReporteResumidoXls() {
        return reporteResumidoXls;
    }

    /**
     * @param reporteResumidoXls the reporteResumidoXls to set
     */
    public void setReporteResumidoXls(Resource reporteResumidoXls) {
        this.reporteResumidoXls = reporteResumidoXls;
    }

    /**
     * @return the renglonesResumido
     */
    public List<Renglones> getRenglonesResumido() {
        return renglonesResumido;
    }

    /**
     * @param renglonesResumido the renglonesResumido to set
     */
    public void setRenglonesResumido(List<Renglones> renglonesResumido) {
        this.renglonesResumido = renglonesResumido;
    }
}
