/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStoreException;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabdocelectFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallescomprasFacade;
import org.beans.sfccbdmq.EjecutadoFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.ModificacionesFacade;
import org.beans.sfccbdmq.OrdenesdecompraFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallescompras;
import org.entidades.sfccbdmq.Ejecutado;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Ordenesdecompra;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.utilitarios.firma.FileUtils;
import org.utilitarios.firma.FirmadorPDF;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "compromisosAdmSfccbdmq")
@ViewScoped
public class CompromisosAdministradorBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public CompromisosAdministradorBean() {

        compromisos = new LazyDataModel<Compromisos>() {

            @Override
            public List<Compromisos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Compromisos> compromisos;
    private List<Detallecompromiso> detallees;
    private Detallecompromiso detalle;
    private List<Detallecompromiso> detalleesb;
    private List<Ejecutado> ejecutados;
    private Certificaciones certificacion;
    private Cabecerainventario cabeceraInventario;
    private Compromisos compromiso;
    private double valorAnt;
    private double saldoLiberar;
    private Date fechaLiberar;
    private int anio;
    private boolean imprimirNuevo;
    private boolean tieneCertificacion;
    private String partida;
    private String clave;
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioCertificacion = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioFirma = new Formulario();
    private Formulario formularioCompromiso = new Formulario();
    private Formulario formularioAjuste = new Formulario();
    private Formulario formularioLiberarSaldo = new Formulario();
    private Formulario formularioAnular = new Formulario();
    private File archivoFirmar;

    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private DetallecertificacionesFacade ejbDetalles;
    @EJB
    private CertificacionesFacade ejbCert;
    @EJB
    private ModificacionesFacade ejbModificaciones;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private CabecerainventarioFacade ejbCabInv;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private OrdenesdecompraFacade ejbOrdenes;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private ContratosFacade ejbContratos;
    @EJB
    private EjecutadoFacade ejbEjecutado;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private DetallescomprasFacade ejbDetallescompras;
    @EJB
    private CabdocelectFacade ejbCabdocelect;
    //Pacpoa
    @EJB
    private DetallecertificacionespoaFacade ejbDetallecertificacionespoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;
    @EJB
    private CodigosFacade ejbCodigos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectoBean;
    @ManagedProperty(value = "#{asignacionesSfccbdmq}")
    private AsignacionesBean asignacionesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;

    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }
    private Perfiles perfil;
    private Date desde;
    private Codigos fuente;
    private Integer numeroCertificacion;
    private Integer numero;
    private Date hasta;
    private Boolean muchos;
    private Boolean muchosBuscar;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Recurso reportepdf;
    private boolean esProveedor = true;
    private DocumentoPDF pdf;

    public boolean isEsProveedor() {
        return esProveedor;
    }

    public void setEsProveedor(boolean esProveedor) {
        this.esProveedor = esProveedor;
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

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "CompromisosVista";
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
//        if (numero == null) {
//            if (asignacionesBean.getFuente() == null) {
//                MensajesErrores.advertencia("Seleccione una fuente de financiamiento");
//                return null;
//            }
//            if (asignacionesBean.getPartida() == null) {
//                MensajesErrores.advertencia("Seleccione una partida primero");
//                return null;
//            }
//        }

        compromisos = new LazyDataModel<Compromisos>() {
            @Override
            public List<Compromisos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.id desc,o.fecha desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                if (numero == null) {
                    String where = "o.fecha between :desde and :hasta ";
//                    String where = "o.fecha between :desde and :hasta and o.nomina is null";
                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
//                    if (asignacionesBean.getFuente() != null) {
//                        where += " and o.detallecertificacion.asignacion.fuente=:fuente";
//                        parametros.put("fuente", asignacionesBean.getFuente());
//                    }
                    if (muchosBuscar == null) {
                        muchosBuscar = false;
                    }
//                    if (muchosBuscar) {
//                        where += " and o.proveedor is null ";
//                    } else {
//                        where += " and o.proveedor is not null ";
//                    }
                    if (proveedorBean.getProveedor() != null) {
                        where += " and o.proveedor=:proveedor";
                        parametros.put("proveedor", proveedorBean.getProveedor());
                    }
                    if (entidadesBean.getEntidad() != null) {
                        where += " and o.beneficiario=:beneficiario";
                        parametros.put("beneficiario", entidadesBean.getEntidad().getEmpleados());
                    }
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);

                    if (numeroCertificacion != null) {
                        where = "  o.certificacion.numerocert=:numero and  "
                                //                                + " o.estado=0  and  "
                                + " o.certificacion.impreso=true ";
                        parametros = new HashMap();
                        parametros.put("numero", numeroCertificacion);
                    }
                    int total = 0;
                    try {
                        parametros.put(";where", where);
                        total = ejbCompromisos.contar(parametros);
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
                    compromisos.setRowCount(total);
                    try {
                        return ejbCompromisos.encontarParametros(parametros);
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                        Logger.getLogger("").log(Level.SEVERE, null, ex);
                    }
                } else {
                    String where = "  o.numerocomp=:numero ";
//                    String where = "  o.id=:numero and o.nomina is null";
                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                    parametros.put("numero", numero);
                    int total = 0;
                    try {
                        parametros.put(";where", where);
                        total = ejbCompromisos.contar(parametros);
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
                    compromisos.setRowCount(total);
                    try {
                        List<Compromisos> lf = ejbCompromisos.encontarParametros(parametros);
                        return lf;
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                        Logger.getLogger("").log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        };

        return null;
    }

    public String crear() {
        try {
            Codigos cod;
            cod = ejbCodigos.traerCodigo("NUM", "05");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para certificaciones");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
            Integer num = Integer.parseInt(cod.getParametros());
            Integer nume = num + 1;
            tieneCertificacion = true;
            compromiso = new Compromisos();
            compromiso.setFecha(new Date());
            compromiso.setNomina(null);
            compromiso.setDevengado(0);
            compromiso.setNumerocomp(nume);
            detallees = new LinkedList<>();
            detalleesb = new LinkedList<>();
            muchos = false;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.insertar();
        cabeceraInventario = null;
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);

//        getFormularioCertificacion().insertar();
        return null;
    }

    public String seleccionaCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
        formularioCertificacion.cancelar();
        formularioDetalle.insertar();
        if ((compromiso.getMotivo() == null) || (compromiso.getMotivo().isEmpty())) {
            compromiso.setMotivo(certificacion.getMotivo());
        }
        compromiso.setCertificacion(certificacion);
        //ES-2018-03-22
//        if (compromiso.getDevengado() == 0) {
//            muchos = false;
//        }

//        formulario.insertar();
        return null;
    }

    public String modificar(Compromisos compromiso) {
        this.compromiso = compromiso;

//        if (compromiso.getImpresion() != null) {
//            MensajesErrores.advertencia("Compromiso ya impreso no es posible modificar");
//            return null;
//        }
        getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
        compromiso.setNomina(null);
        try {
            detallees = ejbDetallecompromiso.encontarParametros(parametros);
//            muchos = poneMuchos();
            muchos = false;
//            esProveedor = compromiso.getProveedor() == null;
            if (compromiso.getProveedor() == null) {
                esProveedor = false;
            } else {
                esProveedor = true;
            }
            cabeceraInventario = null;
            if (compromiso.getCertificacion() != null) {
                tieneCertificacion = true;
            } else {
                tieneCertificacion = false;
            }
            // traer la cabera de inventario del compromiso
            List<Cabecerainventario> lista = ejbCabInv.encontarParametros(parametros);
            for (Cabecerainventario ci : lista) {
                cabeceraInventario = ci;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        if (!muchos) {
            if (compromiso.getProveedor() != null) {
                proveedorBean.setProveedor(compromiso.getProveedor());
                proveedorBean.setEmpresa(compromiso.getProveedor() == null ? null : compromiso.getProveedor().getEmpresa());
                if (compromiso.getProveedor().getEmpresa() != null) {
                    proveedorBean.setRuc(compromiso.getProveedor() == null ? null : compromiso.getProveedor().getEmpresa().getNombre());
                }
            }
        }
        // traer la cabera de inventario del compromiso

        detalleesb = new LinkedList<>();
        return null;
    }

    private boolean poneMuchos() {
        for (Detallecompromiso d : detallees) {
            if (d.getProveedor() != null) {
                return true;
            }
        }
        return false;
    }

    public String imprimir(Compromisos compromiso) {
        this.compromiso = compromiso;
        setImprimirNuevo(compromiso.getImpresion() == null);
        //*******************************************************************//
        compromiso.setImpresion(compromiso.getFecha());
//        compromiso.setImpresion(new Date());
        List<Auxiliar> titulos = proyectoBean.getTitulos();
        try {

            Map parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            detallees = ejbDetallecompromiso.encontarParametros(parametros);
            String direccion = "";
            for (Detallecompromiso d : detallees) {
                if (d.getCompromiso() != null) {
                    if (d.getCompromiso().getCertificacion() != null) {
                        if (d.getCompromiso().getCertificacion().getDireccion() != null) {
                            direccion = d.getCompromiso().getCertificacion().getDireccion().toString();
                        }
                    }

                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
            pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("INFORME DE COMPROMISO\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Compromiso :"));
//            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, compromiso.getId().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, compromiso.getNumerocomp() + ""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf1.format(compromiso.getFecha()).toUpperCase()));
            pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + anio
                    + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaTitulo("\n");
            String proveedor = "";
            String contrato = "";
            if (compromiso.getProveedor() != null) {
                proveedor = compromiso.getProveedor().getEmpresa().toString();
                if (compromiso.getContrato() != null) {
                    contrato = compromiso.getContrato().toString();
                }
            } else if (compromiso.getBeneficiario() != null) {
                proveedor = compromiso.getBeneficiario().getEntidad().toString();

            }
            List<AuxiliarReporte> titulosReporte = new LinkedList<>();
            int totalCol = 6;
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CBTE. CTBL"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulosReporte.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulosReporte.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            double total = 0;
            for (Detallecompromiso d : detallees) {
                String que = "";
                for (Auxiliar a : titulos) {
                    que += a.getTitulo1() + " : ";
                    que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";

                }
                que += " Programa : " + d.getAsignacion().getProyecto().toString();
                d.setArbolProyectos(que);
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(d.getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, que));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                total += d.getValor().doubleValue();
            }

            DecimalFormat df = new DecimalFormat("###,##0.00");
            pdf.agregarTablaReporte(titulosReporte, columnas, totalCol, 100, null);
            pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + contrato + " " + compromiso.getMotivo());
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\nEl Monto del compromiso asciende a: " + df.format(total) + "\n");
            pdf.agregaParrafo("\nTotal compromiso: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");
            pdf.agregaParrafo("\n\n");
//            FM14SEP2018
            if (compromiso.getCertificacion() != null) {
                if (compromiso.getCertificacion().getNumerocert() != null) {
                    pdf.agregaParrafo("\nCertificación Nro: " + compromiso.getCertificacion().getNumerocert() + " - " + compromiso.getCertificacion().getMotivo() + "\n");
                    pdf.agregaParrafo("\n\n");
                }
            }
//            FM14SEP2018
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista de Presupuesto"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Jefe de Presupuesto/Director Financiero"));

            pdf.agregarTabla(null, columnas, 2, 100, null);
            reportepdf = pdf.traerRecurso();
            archivoFirmar = pdf.traerArchivo();
            ejbCompromisos.edit(compromiso, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.insertar();

        return null;
    }

    public String firma() {
        formularioFirma.insertar();
        return null;

    }

    public String grabarFirma() {
        try {
            FirmadorPDF firmador = new FirmadorPDF("compromiso-" + this.compromiso.getId(), clave, seguridadbean.getLogueado().getPin(), configuracionBean.getConfiguracion().getDirectorio(), archivoFirmar, pdf.getNumeroPaginas());
            boolean archivoOk = false;
            if (compromiso.getArchivo() != null) {
                File existente = new File(compromiso.getArchivo());
                if (existente.exists()) {
                    byte[] docByteArry = FileUtils.fileConvertToByteArray(existente);
                    if (docByteArry.length != 0) {
                        archivoOk = true;
                    }
                }
            }

            if (!archivoOk) {
                firmador.firmar();
                compromiso.setArchivo(configuracionBean.getConfiguracion().getDirectorio() + "/firmados/" + compromiso.getId() + ".pdf");

                ejbCompromisos.edit(compromiso, seguridadbean.getLogueado().getUserid());

            } else {
                firmador.editarFirma();
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        clave = null;
        formularioImpresion.cancelar();
        formularioFirma.cancelar();
        return null;
    }

    public String eliminar(Compromisos compromiso) {
        this.compromiso = compromiso;

//       getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
        try {
            detallees = ejbDetallecompromiso.encontarParametros(parametros);
            muchos = poneMuchos();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!muchos) {
            if (compromiso.getProveedor() != null) {
                proveedorBean.setEmpresa(compromiso.getProveedor().getEmpresa());
                proveedorBean.setRuc(compromiso.getProveedor().getEmpresa().getNombre());
            }
        }
        formulario.eliminar();
        return null;
    }

    public String liberarSaldo(Compromisos comp) {
        compromiso = comp;
        fechaLiberar = compromiso.getFecha();
        detalle = new Detallecompromiso();
        saldoLiberar = 0;
        formularioLiberarSaldo.insertar();
        return null;
    }

    public String grabarLiberarSaldo() {
//        Asignaciones a = detalle.getAsignacion();
        try {
            if (saldoLiberar > detalle.getSaldo().doubleValue()) {
                MensajesErrores.advertencia("El saldo a liberar es mayor que el saldo");
                return null;
            }
            Detallecompromiso dNuevo = new Detallecompromiso();
            dNuevo.setCompromiso(compromiso);
            dNuevo.setFecha(fechaLiberar);
            dNuevo.setValor(new BigDecimal(-saldoLiberar));
            dNuevo.setAsignacion(detalle.getAsignacion());
            dNuevo.setSaldo(new BigDecimal(-saldoLiberar));
            ejbDetallecompromiso.create(dNuevo, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioLiberarSaldo.cancelar();
        return null;
    }

    public double getSaldoCompromiso() {
        if (detalle == null) {
            return 0;
        }
        if (detalle.getAsignacion() == null) {
            return 0;
        }
        double total = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion and o.compromiso=:compromiso");
            parametros.put("asignacion", detalle.getAsignacion());
            parametros.put("compromiso", compromiso);
            List<Detallecompromiso> listac = ejbDetallecompromiso.encontarParametros(parametros);
            if (!listac.isEmpty()) {
                detalle = listac.get(0);
                total = detalle.getSaldo().doubleValue();
                saldoLiberar = total;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public String ajusteSaldo(Compromisos comp) {
        compromiso = comp;
        fechaLiberar = compromiso.getFecha();
        detalle = new Detallecompromiso();
        saldoLiberar = 0;
        formularioAjuste.insertar();
        return null;
    }

    public String grabarAjusteSaldo() {
//        Asignaciones a = detalle.getAsignacion();
        try {
            if (saldoLiberar > getSaldoAjuste()) {
                MensajesErrores.advertencia("El saldo a liberar es mayor que el saldo");
                return null;
            }
            Detallecompromiso dNuevo = new Detallecompromiso();
            dNuevo.setCompromiso(compromiso);
            dNuevo.setFecha(fechaLiberar);
            dNuevo.setValor(new BigDecimal(saldoLiberar));
            dNuevo.setAsignacion(detalle.getAsignacion());
            dNuevo.setSaldo(new BigDecimal(saldoLiberar));
            ejbDetallecompromiso.create(dNuevo, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAjuste.cancelar();
        return null;
    }

    public double getSaldoAjuste() {
        if (detalle == null) {
            return 0;
        }
        if (detalle.getAsignacion() == null) {
            return 0;
        }
        double total = 0;
        double valorCerti = 0;
        double valorCompromisos = 0;
        try {
            if (compromiso.getCertificacion() != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
                parametros.put("asignacion", detalle.getAsignacion());
                parametros.put("certificacion", compromiso.getCertificacion());
                List<Detallecertificaciones> lista = ejbDetalles.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Detallecertificaciones dc = lista.get(0);
                    valorCerti = dc.getValor().doubleValue();
                }
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.asignacion=:asignacion and o.compromiso=:compromiso");
                parametros.put("asignacion", detalle.getAsignacion());
                parametros.put("compromiso", compromiso);
                valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                total = valorCerti - valorCompromisos;
                return total;
            } else {
                double asignacion = detalle.getAsignacion().getValor().doubleValue();
                Map parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.asignacion=:asignacion");
                parametros.put("asignacion", detalle.getAsignacion());
                valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                valorCerti = ejbDetalles.sumarCampo(parametros).doubleValue();
                double valorReformas = ejbReformas.sumarCampo(parametros).doubleValue();
                total = asignacion + valorReformas - (valorCerti + valorCompromisos);
                return total;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public SelectItem[] getComboAsignacionCompromiso() {
        if (compromiso == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
        try {
            List<Detallecompromiso> lDetalles = ejbDetallecompromiso.encontarParametros(parametros);
            int size = lDetalles.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Detallecompromiso x : lDetalles) {
                items[i++] = new SelectItem(x.getAsignacion(), x.getAsignacion().toString());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public double getValorDetallecompromiso() {
        if (detallees == null) {
            return 0;
        }
        double retorno = 0;
        for (Detallecompromiso o : detallees) {
            retorno += o.getValor().doubleValue();
        }
        return retorno;
    }

    public double getValorTotalCompromiso() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", c);
        parametros.put(";campo", "o.valor");
        try {
            retorno = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String getValorTotalCompromisoStr() {
        double retorno = 0;
        for (Detallecompromiso d : detallees) {
            retorno += d.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public double getSaldoTotalCompromiso() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", c);
        parametros.put(";campo", "o.saldo");
        try {
            retorno = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getSaldoCertificacion() {
        if (detalle == null) {
            return 0;
        }
        if (detalle.getAsignacion() == null) {
            return 0;
        }
        // ver la formula en detalle debde estar la asignacion buscar las reformas y restar de los certificaciones y los compromisos
        return valorSumado(detalle.getAsignacion());
    }

    private double valorSumado(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.asignacion=:asignacion");
        parametros.put("asignacion", a);

        try {

            double reformas = ejbReformas.sumarCampo(parametros).doubleValue();
            double asignacion = a.getValor().doubleValue();
            double certificaciones = ejbDetalles.sumarCampo(parametros).doubleValue();

            if (certificacion != null) {
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
                parametros.put("asignacion", a);
                parametros.put("certificacion", certificacion);
                certificaciones = ejbDetalles.sumarCampo(parametros).doubleValue();
            }
            double valorCompromisos;
            if (detalle.getId() == null) {
                parametros = new HashMap();
                parametros.put(";where", "o.asignacion=:asignacion and o.compromiso=:compromiso");
                parametros.put("asignacion", a);
                parametros.put("compromiso", compromiso);
                List<Detallecompromiso> listac = ejbDetallecompromiso.encontarParametros(parametros);
                if (listac.isEmpty()) {

                } else {

                    Detallecompromiso dcom = listac.get(0);
                    if (compromiso.getCertificacion() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
                        parametros.put("asignacion", a);
                        parametros.put("certificacion", compromiso.getCertificacion());
                        List<Detallecertificaciones> lista = ejbDetalles.encontarParametros(parametros);
                        Detallecertificaciones dcert = lista.get(0);
                        dcom.setDetallecertificacion(dcert);
                        ejbDetallecompromiso.edit(dcom, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            if (detalle.getId() != null) {
                if (compromiso.getCertificacion() == null) {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion is null");
                    parametros.put("asignacion", detalle.getAsignacion());
                    parametros.put("id", detalle.getId());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                } else {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is not null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion=:certificacion");
                    parametros.put("asignacion", detalle.getAsignacion());
                    parametros.put("certificacion", compromiso.getCertificacion());
                    parametros.put("id", detalle.getId());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                }
            } else {
                if (compromiso.getCertificacion() == null) {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.detallecertificacion is null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.compromiso.certificacion is null");
                    parametros.put("asignacion", detalle.getAsignacion());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                } else {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.detallecertificacion is not null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.compromiso.certificacion=:certificacion");
                    parametros.put("asignacion", detalle.getAsignacion());
                    parametros.put("certificacion", compromiso.getCertificacion());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                }
            }
//            return asignacion + reformas - (certificaciones + valorCompromisos);
            if (!tieneCertificacion) {
                double totals = asignacion + reformas - (certificaciones + valorCompromisos);
                double cuadre = Math.round((totals) * 100);
                double dividido = cuadre / 100;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double total = valortotal.doubleValue();
                if (saldoLiberar == 0) {
                    saldoLiberar = total;
                }
                return total;
            } else {
                double totals = certificaciones - valorCompromisos;
                double cuadre = Math.round((totals) * 100);
                double dividido = cuadre / 100;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double t = valortotal.doubleValue();
                if (saldoLiberar == 0) {
                    saldoLiberar = t;
                }
                return t;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double valorSumadoAsigDeta(Detallecompromiso d) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", d.getAsignacion());
        parametros.put(";where", "o.asignacion=:asignacion");

        try {
            double reformas = ejbReformas.sumarCampo(parametros).doubleValue();
            double asignacion = d.getAsignacion().getValor().doubleValue();
            double certificaciones = ejbDetalles.sumarCampo(parametros).doubleValue();
            double valorCompromisos;
            if (d.getId() != null) {
                if (!tieneCertificacion) {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is null");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion is null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.compromiso!=:id and o.compromiso.certificacion is null");
                    parametros.put("asignacion", d.getAsignacion());
                    parametros.put("id", d.getCompromiso());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                } else {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is not null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.compromiso!=:id and o.compromiso.certificacion is not null");
                    parametros.put("asignacion", d.getAsignacion());
                    parametros.put("id", d.getCompromiso());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                }
            } else {
                if (!tieneCertificacion) {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.compromiso!=:id and o.compromiso.certificacion is null");
                    parametros.put("asignacion", d.getAsignacion());
                    parametros.put("id", d.getCompromiso());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                } else {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
//                    parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is not null");
                    parametros.put(";where", "o.asignacion=:asignacion and o.compromiso!=:id and o.compromiso.certificacion is not null");
                    parametros.put("asignacion", d.getAsignacion());
                    parametros.put("id", d.getCompromiso());
                    valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                }
            }
//            return asignacion + reformas - (certificaciones + valorCompromisos);
            if (compromiso.getDevengado() == null) {
                compromiso.setDevengado(0);
            }
            if (!tieneCertificacion) {
                double total = asignacion + reformas - (certificaciones + valorCompromisos);
                return total;
            } else {
                return certificaciones - valorCompromisos;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean validar() {
//        if ((compromiso.getValorcertificacion() == null) || (compromiso.getValorcertificacion().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Es necesario valor de compromiso");
//            return true;
//        }
        if (esProveedor) {
            if (proveedorBean.getProveedor() == null) {
                MensajesErrores.advertencia("Es necesario proveeror");
                return true;
            }

        } else {
            if (entidadesBean.getEntidad() == null) {
                MensajesErrores.advertencia("Es necesario empleado");
                return true;
            }

        }
        if ((compromiso.getMotivo() == null) || (compromiso.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de compromiso");
            return true;
        }
        if (compromiso.getFecha() == null) {
            MensajesErrores.advertencia("Es necesario fecha de compromiso");
            return true;
        }
        if (compromiso.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Es necesario fecha de compromiso mayor o igual a periodo vigente");
            return true;
        }
//        if ((compromiso.getDetallecertificacion() == null)) {
//            MensajesErrores.advertencia("Es necesario partida ");
//            return true;
//        }
//        if (!muchos) {
//            if (proveedorBean.getEmpresa() == null) {
//                MensajesErrores.advertencia("Seleccione un proveedor primero");
//                return true;
//            }
//        }
        if ((detallees == null) || (detallees.isEmpty())) {
            MensajesErrores.advertencia("Seleccione al menos una partida");
            return true;
        }
        double totalCompromisos = 0;
        if (formulario.isNuevo()) {
            // sumar los para cada detalle
            for (Detallecompromiso d : detallees) {
                if (muchos) {
                    if (d.getProveedor() == null) {
                        MensajesErrores.advertencia("Necesario proveedor en la partida :" + d.getAsignacion().toString());
                        return true;
                    }
                }
                if (d.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
                    MensajesErrores.advertencia("Es necesario fecha de detalle compromiso mayor o igual a periodo vigente : " + d.getAsignacion().toString());
                    return true;
                }
                if (tieneCertificacion) {
//                    double saldo = Math.round((valorSumado(d.getAsignacion()) - d.getValor().doubleValue()) * 100) / 100;
//                    if (detalle != null) {
                    if (d != null) {
                        double detalleV = d.getValor().doubleValue();
                        double total = valorSumadoAsigDeta(d);
                        double cuadre4 = Math.round((total) * 100);
                        double dividido4 = cuadre4 / 100;
                        BigDecimal valortotal4 = new BigDecimal(dividido4).setScale(2, RoundingMode.HALF_UP);
                        double saldo = valortotal4.doubleValue();
//                        if (saldo < 0) {
//                            MensajesErrores.advertencia("El valor del compromiso sobrepasa el valor certificado en la partida:" + d.getAsignacion().toString());
//                            return true;
//                        }
                    }
                }
                totalCompromisos += d.getValor().doubleValue();
            }
        } else {
            for (Detallecompromiso d : detallees) {
                if (muchos) {
                    if (d.getProveedor() == null) {
                        MensajesErrores.advertencia("Necesario proveedor en la partida :" + d.getAsignacion().toString());
                        return true;
                    }
                }
                if (d.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
                    MensajesErrores.advertencia("Es necesario fecha de detalle compromiso mayor o igual a periodo vigente : " + d.getAsignacion().toString());
                    return true;
                }
                if (tieneCertificacion) {
//                if (detalle != null) {
                    if (d != null) {
                        double detalleV = d.getValor().doubleValue();
                        double total = valorSumadoAsigDeta(d);
                        double cuadre3 = Math.round((total) * 100);
                        double dividido3 = cuadre3 / 100;
                        BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
                        double saldo = (valortotal3.doubleValue());

//                        if (saldo < 0) {
//                            MensajesErrores.advertencia("El valor del compromiso sobrepasa el valor certificado en la partida: " + d.getAsignacion().toString());
//                            return true;
//                        }
                    }
                }
                totalCompromisos += d.getValor().doubleValue();
            }
        }
        if (compromiso.getContrato() != null) {
            double cuadre2 = Math.round(compromiso.getContrato().getValor().doubleValue() * 100);
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double contrato = valortotal2.doubleValue();

            double cuadre3 = Math.round(totalCompromisos);
            double dividido3 = cuadre3 / 100;
            BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
            double tCompromisos = (valortotal3.doubleValue());

            if (contrato < tCompromisos) {
                MensajesErrores.advertencia("Valor del contrato menor a los compromisos");
                return true;
            }
            Map parametros = new HashMap();
            if (compromiso.getId() != null) {
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.compromiso.contrato=:contrato and o.compromiso!=:compromiso");
//                parametros.put(";where", "o.compromiso.contrato=:contrato and o.compromiso.impresion is not null and o.compromiso!=:compromiso");
                parametros.put("contrato", compromiso.getContrato());
                parametros.put("compromiso", compromiso);
            } else {
                parametros.put(";campo", "o.valor");
//                parametros.put(";where", "o.compromiso.contrato=:contrato and o.compromiso.impresion is not null");
                parametros.put(";where", "o.compromiso.contrato=:contrato");
                parametros.put("contrato", compromiso.getContrato());
//                parametros.put("compromiso", compromiso);
            }
            try {
//                double sumaCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue() + totalCompromisos;

                double cuadre = Math.round(compromiso.getContrato().getValor().doubleValue() * 100);
                double dividido = cuadre / 100;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double valorContrato = valortotal.doubleValue();

                double cuadre5 = Math.round(ejbDetallecompromiso.sumarCampo(parametros).doubleValue() + totalCompromisos);
                double dividido5 = cuadre5 / 100;
                BigDecimal valortotal5 = new BigDecimal(dividido5).setScale(2, RoundingMode.HALF_UP);

                parametros = new HashMap();
                parametros.put(";campo", "o.monto");
                parametros.put(";where", "o.contrato=:contrato");
                parametros.put("contrato", compromiso.getContrato());
                double valorModificatorios = ejbModificaciones.sumarCampo(parametros).doubleValue();

                double sumaCompromisos = (valortotal5.doubleValue());

//                double totalContratos = valorContrato + valorModificatorios;
                double cuadre6 = Math.round(valorContrato + valorModificatorios);
                double dividido6 = cuadre6 / 100;
                BigDecimal valortotal6 = new BigDecimal(dividido6).setScale(2, RoundingMode.HALF_UP);
                double totalContratos = (valortotal6.doubleValue());

                if (totalContratos < sumaCompromisos) {
                    MensajesErrores.advertencia("Valor del contrato menor a los compromisos 2");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (tieneCertificacion) {
            compromiso.setDireccion(compromiso.getCertificacion().getDireccion());
        } else {
//            if (compromiso.getDireccion())
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            compromiso.setNomina(null);
//            compromiso.setFecha(new Date());
//            if (!muchos) {
            if (esProveedor) {
                compromiso.setProveedor(proveedorBean.getProveedor());
            } else {
                compromiso.setBeneficiario(entidadesBean.getEntidad().getEmpleados());
                compromiso.setProveedor(null);
                compromiso.setContrato(null);
            }
//        }else {
//                compromiso.setProveedor(null);
//                compromiso.setContrato(null);
//            }
            Contratos contrato = compromiso.getContrato();
            compromiso.setAnulado(Boolean.FALSE);
            ejbCompromisos.create(compromiso, seguridadbean.getLogueado().getUserid());
            Organigrama direccion = null;
            for (Detallecompromiso o : detallees) {
                o.setCompromiso(compromiso);
                o.setSaldo(o.getValor());
                if (!muchos) {
                    o.setProveedor(null);
                }
                if (o.getValor().doubleValue() > 0) {
                    o.setFecha(compromiso.getFecha());
                }
                if (compromiso.getDevengado() > 1) {
                    o.setSaldo(BigDecimal.ZERO);
                }
                ejbDetallecompromiso.create(o, seguridadbean.getLogueado().getUserid());
                Ejecutado e = new Ejecutado();
                if (compromiso.getDevengado() > 1) {
                    e.setEjecutado(false);
                    e.setValor(o.getValor());
                    e.setFecha(compromiso.getFecha());
                    e.setDetallecomrpomiso(o);
                    ejbEjecutado.create(e, seguridadbean.getLogueado().getUserid());
                }
                if (compromiso.getDevengado() == 3) {
                    e = new Ejecutado();
                    e.setEjecutado(true);
                    e.setValor(o.getValor());
                    e.setFecha(compromiso.getFecha());
                    e.setDetallecomrpomiso(o);
                    ejbEjecutado.create(e, seguridadbean.getLogueado().getUserid());
                }
            }
            if (contrato != null) {
                contrato.setEstado(1);
                contrato.setDireccion(compromiso.getCertificacion() == null ? null : compromiso.getCertificacion().getDireccion());
                ejbContratos.edit(contrato, seguridadbean.getLogueado().getUserid());
            }
            if (cabeceraInventario != null) {
                cabeceraInventario.setCompromiso(compromiso);
                ejbCabInv.edit(cabeceraInventario, seguridadbean.getLogueado().getUserid());
                Ordenesdecompra orden = cabeceraInventario.getOrdencompra();
                orden.setCompromiso(compromiso);
                ejbOrdenes.edit(orden, seguridadbean.getLogueado().getUserid());
            }
//            ES-2018-03-22 CAMBIO DE CREATE A EDIT, PARA EVITAR LA DUPLICACION DEL REGISTRO DE CERTIFICACION
            if (certificacion != null) {

                certificacion.setImpreso(true);
//            ejbCert.create(certificacion, seguridadbean.getLogueado().getUserid());
                ejbCert.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            Codigos cod = ejbCodigos.traerCodigo("NUM", "05");
            cod.setParametros(compromiso.getNumerocomp() + "");
            ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();

        buscar();

        return null;
    }

    public String grabar() {
        if (compromiso.getBeneficiario() != null) {
            entidadesBean.setEntidad(compromiso.getBeneficiario().getEntidad());
            entidadesBean.getEntidad().setEmpleados(compromiso.getBeneficiario());
        }

        if (validar()) {
            return null;
        }
        try {
            if (esProveedor) {
                compromiso.setProveedor(proveedorBean.getProveedor());

            } else {
                compromiso.setBeneficiario(entidadesBean.getEntidad().getEmpleados());
                compromiso.setProveedor(null);
                compromiso.setContrato(null);
            }
//            if (muchos) {
//                compromiso.setProveedor(null);
//                compromiso.setContrato(null);
//            } else {
//                compromiso.setProveedor(proveedorBean.getProveedor());
//            }
            compromiso.setNomina(null);
//            compromiso.setImprimecertificacion(Boolean.FALSE);
            ejbCompromisos.edit(compromiso, seguridadbean.getLogueado().getUserid());
            for (Detallecompromiso o : detallees) {
                o.setCompromiso(compromiso);
                // TRAE EL SALDO

                if (!muchos) {
                    o.setProveedor(null);
                }
                if (o.getId() == null) {
                    o.setSaldo(o.getValor());
                    if (o.getValor().doubleValue() > 0) {
                        o.setFecha(compromiso.getFecha());
                    }
                    ejbDetallecompromiso.create(o, seguridadbean.getLogueado().getUserid());
                } else {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso=:compromiso");
                    parametros.put("compromiso", o);
                    parametros.put(";campo", "o.valor");
                    double sumado = ejbRenglones.sumarCampo(parametros).doubleValue();
                    double saldo = o.getValor().doubleValue() - sumado;
                    o.setSaldo(new BigDecimal(saldo));
                    if (o.getValor().doubleValue() > 0) {
                        o.setFecha(compromiso.getFecha());
                    }
                    ejbDetallecompromiso.edit(o, seguridadbean.getLogueado().getUserid());
                }
                Map parametros = new HashMap();
                parametros.put(";where", "o.detallecomrpomiso=:detalle");
                parametros.put("detalle", o);
                List<Ejecutado> el = ejbEjecutado.encontarParametros(parametros);
                for (Ejecutado e : el) {
                    ejbEjecutado.remove(e, seguridadbean.getLogueado().getUserid());
                }
                Ejecutado e = new Ejecutado();
                if (compromiso.getDevengado() > 0) {
                    e.setEjecutado(false);
                    e.setValor(o.getValor());
                    e.setFecha(compromiso.getFecha());
                    e.setDetallecomrpomiso(o);
                    ejbEjecutado.create(e, seguridadbean.getLogueado().getUserid());
                }
                if (compromiso.getDevengado() == 2) {
                    e = new Ejecutado();
                    e.setEjecutado(true);
                    e.setFecha(compromiso.getFecha());
                    e.setValor(o.getValor());
                    e.setDetallecomrpomiso(o);
                    ejbEjecutado.create(e, seguridadbean.getLogueado().getUserid());
                }

            }
            for (Detallecompromiso o : detalleesb) {
                if (o.getId() != null) {
                    ejbDetallecompromiso.remove(o, seguridadbean.getLogueado().getUserid());
                }
            }
            if (cabeceraInventario != null) {
                cabeceraInventario.setCompromiso(compromiso);
                ejbCabInv.edit(cabeceraInventario, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | InsertarException | BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
//            compromiso.setEstado(-1);
            Map parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            List<Cabdocelect> listCabdocelects = ejbCabdocelect.encontarParametros(parametros);
            if (!listCabdocelects.isEmpty()) {
                MensajesErrores.advertencia("Compromiso tiene Documentos Electronicos no es posible borrar");
                return null;
            }
            List<Detallecompromiso> listaDetalleCompromiso = ejbDetallecompromiso.encontarParametros(parametros);
            for (Detallecompromiso d : listaDetalleCompromiso) {
                parametros = new HashMap();
                parametros.put(";where", "o.detallecompromiso=:detalle");
                parametros.put("detalle", d);
                List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
                List<Detallescompras> listDetalleCompras = ejbDetallescompras.encontarParametros(parametros);
                if (!listaRenglones.isEmpty()) {
                    MensajesErrores.advertencia("Compromiso tiene Renglones no es posible borrar");
                    return null;
                }
                if (!listDetalleCompras.isEmpty()) {
                    MensajesErrores.advertencia("Compromiso tiene Compras no es posible borrar");
                    return null;
                }
                parametros = new HashMap();
                parametros.put(";where", "o.detallecomrpomiso=:detalle");
                parametros.put("detalle", d);
                List<Ejecutado> el = ejbEjecutado.encontarParametros(parametros);
                for (Ejecutado e : el) {
                    ejbEjecutado.remove(e, seguridadbean.getLogueado().getUserid());
                }
                ejbDetallecompromiso.remove(d, seguridadbean.getLogueado().getUserid());
            }
            ejbCompromisos.remove(compromiso, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CompromisosAdministradorBean.class
                            .getName()).log(Level.SEVERE, null, ex);

        } catch (ConsultarException ex) {
            Logger.getLogger(CompromisosAdministradorBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
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
     * @return the proyectoBean
     */
    public ProyectosBean getProyectoBean() {
        return proyectoBean;
    }

    /**
     * @param proyectoBean the proyectoBean to set
     */
    public void setProyectoBean(ProyectosBean proyectoBean) {
        this.proyectoBean = proyectoBean;
    }

    /**
     * @return the asignacionesBean
     */
    public AsignacionesBean getAsignacionesBean() {
        return asignacionesBean;
    }

    /**
     * @param asignacionesBean the asignacionesBean to set
     */
    public void setAsignacionesBean(AsignacionesBean asignacionesBean) {
        this.asignacionesBean = asignacionesBean;
    }

    /**
     * @return the compromisos
     */
    public LazyDataModel<Compromisos> getCompromisos() {
        return compromisos;
    }

    /**
     * @param compromisos the compromisos to set
     */
    public void setCompromisos(LazyDataModel<Compromisos> compromisos) {
        this.compromisos = compromisos;
    }

    /**
     * @return the compromiso
     */
    public Compromisos getCompromiso() {
        return compromiso;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
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

//   
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
     * @return the numeroCertificacion
     */
    public Integer getNumeroCertificacion() {
        return numeroCertificacion;
    }

    /**
     * @param numeroCertificacion the numeroCertificacion to set
     */
    public void setNumeroCertificacion(Integer numeroCertificacion) {
        this.numeroCertificacion = numeroCertificacion;
    }

    /**
     * @return the detallees
     */
    public List<Detallecompromiso> getDetalles() {
        return detallees;
    }

    /**
     * @param detallees the detallees to set
     */
    public void setDetalles(List<Detallecompromiso> detallees) {
        this.detallees = detallees;
    }

    /**
     * @return the detalle
     */
    public Detallecompromiso getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecompromiso detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the certificacion
     */
    public Certificaciones getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
    }

    /**
     * @return the formularioCertificacion
     */
    public Formulario getFormularioCertificacion() {
        return formularioCertificacion;
    }

    /**
     * @param formularioCertificacion the formularioCertificacion to set
     */
    public void setFormularioCertificacion(Formulario formularioCertificacion) {
        this.formularioCertificacion = formularioCertificacion;
    }

    public SelectItem[] getComboPartidas() {

        if (certificacion == null) {
            return null;
        }
        Map parametros = new HashMap();
        try {
            parametros.put(";where", "o.certificacion=:certificacion and "
                    + " o.certificacion.anulado=false");
            parametros.put("certificacion", certificacion);
            return Combos.getSelectItems(ejbDetalles.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevaDetalle() {
        detalle = new Detallecompromiso();
        detalle.setFecha(compromiso.getFecha());
        if (tieneCertificacion) {
            if (compromiso.getCertificacion() == null) {
                getFormularioCertificacion().insertar();
            } else {
                formularioDetalle.insertar();
                certificacion = compromiso.getCertificacion();
            }
            if (muchos) {
                proveedorBean.setEmpresa(null);
                proveedorBean.setProveedor(null);
                proveedorBean.setRuc(null);
            }
            valorAnt = 0;
//            formularioCertificacion.cancelar();

        } else {
            certificacion = null;
            compromiso.setCertificacion(certificacion);
            formularioDetalle.insertar();
        }
        return null;
    }

    public double getSumaComplementarios() {
        if (compromiso == null) {
            return 0;
        }
        if (compromiso.getContrato() == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
        parametros.put(";campo", "o.monto");
        parametros.put("contrato", compromiso.getContrato());
        double retorno;
        try {
            retorno = ejbModificaciones.sumarCampo(parametros).doubleValue();
//            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            return retorno;

        } catch (ConsultarException ex) {
            Logger.getLogger(CompromisosAdministradorBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return 0.00;
    }

    public String modificaDetalle() {
        int fila = formularioDetalle.getFila().getRowIndex();
        formularioDetalle.setIndiceFila(fila);
        this.detalle = detallees.get(fila);
        formularioDetalle.editar();
        if (compromiso.getDevengado() == 0) {
            certificacion = compromiso.getCertificacion();
        }
        if (muchos) {
            proveedorBean.setEmpresa(detalle.getProveedor().getEmpresa());
            proveedorBean.setProveedor(detalle.getProveedor());
            proveedorBean.setRuc(detalle.getProveedor().getEmpresa().getNombre());
        }
        fuente = detalle.getAsignacion().getFuente();
        partida = detalle.getAsignacion().getClasificador().getCodigo();

        valorAnt = detalle.getValor().doubleValue();
        return null;
    }

    public String borraDetalle() {
        int fila = formularioDetalle.getFila().getRowIndex();
        formularioDetalle.setIndiceFila(fila);
        this.detalle = detallees.get(fila);
        if (compromiso.getDevengado() == 0) {
            certificacion = compromiso.getCertificacion();
        }
        formularioDetalle.eliminar();
        if (muchos) {
            proveedorBean.setEmpresa(detalle.getProveedor().getEmpresa());
            proveedorBean.setRuc(detalle.getProveedor().getEmpresa().getNombre());
        }
        valorAnt = detalle.getValor().doubleValue();
        return null;
    }

    public boolean validaDetalle() {

        if ((detalle.getAsignacion() == null)) {
            MensajesErrores.advertencia("Es necesario una partida");
            return true;
        }
        if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Es necesario valor de documento");
            return true;
        }
        if (muchos) {
            if (proveedorBean.getEmpresa() == null) {
                MensajesErrores.advertencia("Es necesario Proveedor");
                return true;
            }
            if ((detalle.getMotivo() == null) || (detalle.getMotivo().isEmpty())) {
                MensajesErrores.advertencia("Es necesario motivo del documento");
                return true;
            }
        }
        // comprobar que los saldos
        // sumar los ya usados esta asignaciones + reformas -(certificaciones+compromisos)

//        double saldoS = getSaldoCertificacion();
//        double saldo = Math.round((saldoS * 100) / 100);
        double cuadre = Math.round(getSaldoCertificacion() * 100);
        double dividido = cuadre / 100;
        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        double saldo = valortotal.doubleValue();

//        double saldo = getSaldoCertificacion();
        // ver linea por línea?
        double nuevo = detalle.getValor().doubleValue();
        double suma = 0;
        for (Detallecompromiso d : detallees) {
            if (d.getAsignacion().equals(detalle.getAsignacion())) {
                if (detalle.getId() != null) {
                    if (d.getId() != detalle.getId()) {
                        suma += d.getValor().doubleValue();
                    }
                } else {
                    suma += d.getValor().doubleValue();
                }

            }
        }
        double total = suma + nuevo;
//        suma += resta;
//        double valorActual = detalle.getAsignacion().getValor().doubleValue();
//        if (saldo - total < 0) {
//            MensajesErrores.advertencia("Valor sobrepasa ceritificación");
////            detalle.setValor(new BigDecimal(valorActual));
//            return true;
//        }
        return false;
    }

    public String insetarDetalle() {

        if (validaDetalle()) {
            return null;
        }

        if (muchos) {
            detalle.setProveedor(proveedorBean.getProveedor());
        }
        detallees.add(detalle);
        formularioDetalle.cancelar();
        return null;
    }

    public String grabarDetalle() {
        if (validaDetalle()) {
            return null;
        }
        if (muchos) {
            detalle.setProveedor(proveedorBean.getProveedor());
        }
        detallees.set(formularioDetalle.getIndiceFila(), detalle);
        formularioDetalle.cancelar();
        return null;
    }

    public String borrarDetalle() {
        detalleesb.add(detalle);
        detallees.remove(formularioDetalle.getIndiceFila());
        formularioDetalle.cancelar();
        // colocar el saldo del compromiso sino se jode
        return null;
    }

    /**
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
    }

    private boolean esta(Asignaciones a) {
        for (Detallecompromiso d : detallees) {
            if (d.getAsignacion().equals(a)) {
                return true;
            }
        }
        return false;
    }

    public Detallecompromiso traer(Integer id) throws ConsultarException {
        return ejbDetallecompromiso.find(id);
    }

    public Compromisos traerCompromiso(Integer id) throws ConsultarException {
        return ejbCompromisos.find(id);
    }

    /**
     * @return the muchos
     */
    public Boolean getMuchos() {
        return muchos;
    }

    /**
     * @param muchos the muchos to set
     */
    public void setMuchos(Boolean muchos) {
        this.muchos = muchos;
    }

    public boolean isEditar() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        if (c.getNomina() != null) {
            if (c.getNomina()) {
                return true;
            }
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.detallecompromiso.compromiso=:compromiso "
                + " and o.detallecompromiso.compromiso.nomina is null");
        parametros.put("compromiso", c);
        try {
            int total = ejbRasCompras.contar(parametros);
            return total != 0;

        } catch (ConsultarException ex) {
            Logger.getLogger(CompromisosAdministradorBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
     * @return the cabeceraInventario
     */
    public Cabecerainventario getCabeceraInventario() {
        return cabeceraInventario;
    }

    public Cabecerainventario traerInv(Integer id) throws ConsultarException {
        return ejbCabInv.find(id);
    }

    /**
     * @param cabeceraInventario the cabeceraInventario to set
     */
    public void setCabeceraInventario(Cabecerainventario cabeceraInventario) {
        this.cabeceraInventario = cabeceraInventario;
    }

    public SelectItem[] getComboInventario() {

        if (proveedorBean.getProveedor() == null) {
            return null;
        }

        Map parametros = new HashMap();
        try {
            parametros.put(";where", "o.proveedor=:proveedor and o.txid.ingreso=true"
                    + " and o.compromiso is null and o.estado=0");
            parametros.put("proveedor", proveedorBean.getProveedor());
            List<Cabecerainventario> lista = ejbCabInv.encontarParametros(parametros);
            int size = lista.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Cabecerainventario x : lista) {
                parametros = new HashMap();
                parametros.put(";where", ""
                        + " o.cabecerainventario=:cabecerainventario");
                parametros.put("cabecerainventario", x);
                List<Kardexinventario> kardex = ejbKardex.encontarParametros(parametros);
                double valor = 0;
                for (Kardexinventario k : kardex) {
                    valor += k.getCantidad() * k.getCosto();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                items[i++] = new SelectItem(x, "Bodega :" + x.getBodega().getNombre() + " Fecha Mov: " + sdf.format(x.getFecha()) + " NO: " + x.getNumero());
//                items[i++] = new SelectItem(x, "Bodega :" + x.getBodega().getNombre() + " Fecha Mov: " + sdf.format(x.getFecha()) + " NO: " + x.getNumero() + " Valor : " + df.format(valor));
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CertificacionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getValoreInventario() {
        if (cabeceraInventario == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cab");
        parametros.put(";campo", "o.cantidad*o.costo");
        parametros.put("cab", cabeceraInventario);
        try {
            double valor = ejbKardex.sumarCampoDoble(parametros);
            DecimalFormat df = new DecimalFormat("###,##0.00");
            String retorno = "Valor consumo : " + df.format(valor);
            parametros = new HashMap();
            parametros.put(";where", "o.cabecerainventario=:cab");
            parametros.put(";campo", "o.cantidadinversion*o.costo");
            parametros.put("cab", cabeceraInventario);
            double valori = ejbKardex.sumarCampoDoble(parametros);
            retorno += " Valor inversión : " + df.format(valori);
            retorno += " Total : " + df.format(valor + valori);
            return retorno;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CompromisosAdministradorBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
//    public double getModificatorios() {
//        if (compromiso.getContrato() == null) {
//            return 0;
//        }
//        if (compromiso == null) {
//            return 0;
//        }
//        Map parametros = new HashMap();
//        parametros.put(";campo", "o.monto");
//        parametros.put(";where", "o.contrato=:contrato");
//        parametros.put("contrato", compromiso.getContrato());
//        try {
//            return ejbModificaciones.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }

    /**
     * @return the muchosBuscar
     */
    public Boolean getMuchosBuscar() {
        return muchosBuscar;
    }

    /**
     * @param muchosBuscar the muchosBuscar to set
     */
    public void setMuchosBuscar(Boolean muchosBuscar) {
        this.muchosBuscar = muchosBuscar;
    }

    public List<Clasificadores> getListaClasificadores() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.proyecto.anio=:anio");
        parametros.put("anio", getAnio());
        parametros.put(";orden", "o.clasificador.codigo");
        try {
//            if ((compromiso.getDevengado() != 0)) {
            if (!tieneCertificacion) {
                List<Asignaciones> lAsignaciones = ejbAsignaciones.encontarParametros(parametros);
                List<Clasificadores> listaClasificadores = new LinkedList<>();

                for (Asignaciones x : lAsignaciones) {

                    double saldo = valorSumado(x);
                    if (saldo > 0) {
                        // ver si el clasificador esta ya en la lista
                        estaYaclasificador(x.getClasificador(), listaClasificadores);
                    }
                }
                return listaClasificadores;
            } else {
                List<Clasificadores> listaClasificadores = new LinkedList<>();
                parametros = new HashMap();
                parametros.put(";where", " o.certificacion=:certificacion");
//                parametros.put("anio", getAnio());
                parametros.put("certificacion", compromiso.getCertificacion());
                parametros.put(";orden", "o.asignacion.clasificador.codigo");
                List<Detallecertificaciones> dCertificaciones = ejbDetalles.encontarParametros(parametros);
                for (Detallecertificaciones x : dCertificaciones) {

                    double saldo = valorSumado(x.getAsignacion());
                    if (saldo > 0) {
                        // ver si el clasificador esta ya en la lista
                        estaYaclasificador(x.getAsignacion().getClasificador(), listaClasificadores);
                    }
                }
                return listaClasificadores;

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CompromisosAdministradorBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void estaYaclasificador(Clasificadores c, List<Clasificadores> listaClasificadores) {
        for (Clasificadores c1 : listaClasificadores) {
            if (c1.equals(c)) {
                return;
            }
        }
        listaClasificadores.add(c);
    }

    public SelectItem[] getComboClasificadorLiberarCompromiso() {
        if (compromiso == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
//        parametros.put(";orden", "o.asignacion.proyecto.codigo,o.fuente.nombre");
        try {
            List<Detallecompromiso> lDetalles = ejbDetallecompromiso.encontarParametros(parametros);
            int size = lDetalles.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Detallecompromiso x : lDetalles) {
                items[i++] = new SelectItem(x.getAsignacion(), x.getAsignacion().toString());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
    }

    /**
     * @return the fuente
     */
    public Codigos getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(Codigos fuente) {
        this.fuente = fuente;
    }

    public SelectItem[] getComboFuenteClasificador() {
        if (partida == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador.codigo=:clasificador  and o.proyecto.anio=:anio");
        parametros.put("clasificador", partida);
        parametros.put("anio", getAnio());
        parametros.put(";orden", "o.fuente.nombre");
        try {
            List<Asignaciones> lAsignaciones = ejbAsignaciones.encontarParametros(parametros);
            List<Codigos> listaFuentes = new LinkedList<>();

            for (Asignaciones x : lAsignaciones) {

                double saldo = valorSumado(x);
                if (saldo > 0) {
                    // ver si el clasificador esta ya en la lista
                    esta(x.getFuente(), listaFuentes);
                }

            }
            return Combos.getSelectItems(listaFuentes, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CompromisosAdministradorBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void esta(Codigos f, List<Codigos> listaFuentes) {
        for (Codigos f1 : listaFuentes) {
            if (f.equals(f1)) {
                return;
            }
        }
        listaFuentes.add(f);
    }

    public SelectItem[] getComboClasificadorFuente() {
        if (getFuente() == null) {
            return null;
        }
        if (partida == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador.codigo=:clasificador and o.fuente=:fuente and o.proyecto.anio=:anio");
        parametros.put("clasificador", partida);
        parametros.put("fuente", getFuente());
        parametros.put("anio", getAnio());
        parametros.put(";orden", "o.proyecto.codigo,o.fuente.nombre");
        try {
            List<Asignaciones> lAsignaciones = ejbAsignaciones.encontarParametros(parametros);
            int size = lAsignaciones.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Asignaciones x : lAsignaciones) {
                items[i++] = new SelectItem(x, x.getProyecto().getCodigo() + " - " + x.getProyecto().getNombre());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(AsignacionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPartidaCertificacion() {
        if (compromiso == null) {
            return null;
        }
        if (compromiso.getCertificacion() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", compromiso.getCertificacion());
//        parametros.put(";orden", "o.asignacion.proyecto.codigo,o.fuente.nombre");
        try {
            List<Detallecertificaciones> lDetalles = ejbDetalles.encontarParametros(parametros);
            int size = lDetalles.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Detallecertificaciones x : lDetalles) {
                items[i++] = new SelectItem(x.getAsignacion(), x.getAsignacion().toString());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public SelectItem[] getComboPartidaCertificacionConSaldo() {
        if (compromiso == null) {
            return null;
        }
        if (compromiso.getCertificacion() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", compromiso.getCertificacion());
//        parametros.put(";orden", "o.asignacion.proyecto.codigo,o.fuente.nombre");
        try {
            List<Detallecertificaciones> lDetalles = ejbDetalles.encontarParametros(parametros);
            int size = lDetalles.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Detallecertificaciones x : lDetalles) {
                items[i++] = new SelectItem(x.getAsignacion(), x.getAsignacion().toString());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @return the formularioFirma
     */
    public Formulario getFormularioFirma() {
        return formularioFirma;
    }

    /**
     * @param formularioFirma the formularioFirma to set
     */
    public void setFormularioFirma(Formulario formularioFirma) {
        this.formularioFirma = formularioFirma;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the formularioCompromiso
     */
    public Formulario getFormularioCompromiso() {
        return formularioCompromiso;
    }

    /**
     * @param formularioCompromiso the formularioCompromiso to set
     */
    public void setFormularioCompromiso(Formulario formularioCompromiso) {
        this.formularioCompromiso = formularioCompromiso;
    }

//    public double getValorOcupadoCertificacion() {
//        if (detallees == null) {
//            return 0;
//        }
//        if (detalle == null) {
//            return 0;
//        }
//        double suma = 0;
//        for (Detallecompromiso d : detallees) {
////            if (d.getDetallecertificacion().equals(detalle.getDetallecertificacion())) {
//            if (d.getAsignacion().equals(detalle.getAsignacion())) {
//                if (detalle.getId() != null) {
//                    if (d.getId() != detalle.getId()) {
//                        suma += d.getValor().doubleValue();
//                    }
//                } else {
//                    suma += d.getValor().doubleValue();
//                }
//            }
//        }
//        return suma;
//    }
    public double getValorOcupadoCertificacion() {
        if (detallees == null) {
            return 0;
        }
        if (detalle == null) {
            return 0;
        }
        double valorCompromisos = 0;
        try {
            for (Detallecompromiso d : detallees) {
                Map parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.asignacion=:asignacion");
                parametros.put("asignacion", d.getAsignacion());
                if (d.getId() != null) {
                    if (compromiso.getCertificacion() == null) {
                        parametros = new HashMap();
                        parametros.put(";campo", "o.valor");
//                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is null");
                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion  is null");
                        parametros.put("asignacion", d.getAsignacion());
                        parametros.put("id", detalle.getId());
                        valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                    } else {
                        parametros = new HashMap();
                        parametros.put(";campo", "o.valor");
//                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is not null");
                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion=:certificacion");
                        parametros.put("asignacion", d.getAsignacion());
                        parametros.put("certificacion", certificacion);
                        parametros.put("id", detalle.getId());
                        valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                    }
                } else {
                    if (compromiso.getCertificacion() == null) {
                        parametros = new HashMap();
                        parametros.put(";campo", "o.valor");
//                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is null");
                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion is null");
                        parametros.put("asignacion", d.getAsignacion());
                        parametros.put("id", detalle.getId());
                        valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
                    } else {
                        parametros = new HashMap();
                        parametros.put(";campo", "o.valor");
//                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.detallecertificacion is not null");
                        parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id and o.compromiso.certificacion=:certificacion");
                        parametros.put("asignacion", d.getAsignacion());
                        parametros.put("certificacion", compromiso.getCertificacion());
                        parametros.put("id", detalle.getId());
                        valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();

                    }
                }

            }
        } catch (ConsultarException ex) {
            Logger.getLogger(CompromisosAdministradorBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return valorCompromisos;
    }

    public String anular(Compromisos compromiso) {
        this.compromiso = compromiso;
        if ((compromiso.getImpresion() == null)) {
            MensajesErrores.advertencia("Compromiso no está aprobado");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
        try {
            detallees = ejbDetallecompromiso.encontarParametros(parametros);
            List<Cabdocelect> listCabdocelects = ejbCabdocelect.encontarParametros(parametros);
            if (!listCabdocelects.isEmpty()) {
                MensajesErrores.advertencia("Compromiso tiene Documentos Electronicos no es posible anular");
                return null;
            }
            List<Detallecompromiso> listaDetalleCompromiso = ejbDetallecompromiso.encontarParametros(parametros);
            for (Detallecompromiso d : listaDetalleCompromiso) {
                parametros = new HashMap();
                parametros.put(";where", "o.detallecompromiso=:detalle");
                parametros.put("detalle", d);
                List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
                List<Detallescompras> listDetalleCompras = ejbDetallescompras.encontarParametros(parametros);
                if (!listaRenglones.isEmpty()) {
                    MensajesErrores.advertencia("Compromiso tiene Renglones no es posible anular");
                    return null;
                }
                if (!listDetalleCompras.isEmpty()) {
                    MensajesErrores.advertencia("Compromiso tiene Compras no es posible anular");
                    return null;
                }
                parametros = new HashMap();
                parametros.put(";where", "o.detallecomrpomiso=:detalle");
                parametros.put("detalle", d);
                List<Ejecutado> el = ejbEjecutado.encontarParametros(parametros);
                if (!el.isEmpty()) {
                    MensajesErrores.advertencia("Compromiso tiene Ejecutado no es posible anular");
                    return null;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnular.editar();
        return null;
    }

    public String grabarAnular() {
        if ((compromiso.getObservacionanulado() == null) || (compromiso.getObservacionanulado().isEmpty())) {
            MensajesErrores.advertencia("Es necesario observación");
            return null;
        }
        if (compromiso.getFechaanulado() == null) {
            MensajesErrores.advertencia("Es necesario observación");
            return null;
        }
        compromiso.setUsuarioanulado(seguridadbean.getLogueado().getUserid());
        compromiso.setAnulado(Boolean.TRUE);
        try {
            for (Detallecompromiso dc : detallees) {
                Detallecompromiso dNuevo = new Detallecompromiso();
                double total = -dc.getValor().doubleValue();
                dNuevo.setCompromiso(compromiso);
                dNuevo.setDetallecertificacion(dc.getDetallecertificacion());
                dNuevo.setValor(new BigDecimal(total));
                dNuevo.setAsignacion(dc.getAsignacion());
                dNuevo.setSaldo(new BigDecimal(total));
                dNuevo.setFecha(compromiso.getFechaanulado());
                ejbDetallecompromiso.create(dNuevo, seguridadbean.getLogueado().getUserid());
            }
            ejbCompromisos.edit(compromiso, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosAdministradorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnular.cancelar();
        return null;
    }

    /**
     * @return the formularioAnular
     */
    public Formulario getFormularioAnular() {
        return formularioAnular;
    }

    /**
     * @param formularioAnular the formularioAnular to set
     */
    public void setFormularioAnular(Formulario formularioAnular) {
        this.formularioAnular = formularioAnular;
    }

    /**
     * @return the fechaLiberar
     */
    public Date getFechaLiberar() {
        return fechaLiberar;
    }

    /**
     * @param fechaLiberar the fechaLiberar to set
     */
    public void setFechaLiberar(Date fechaLiberar) {
        this.fechaLiberar = fechaLiberar;
    }

    /**
     * @return the saldoLiberar
     */
    public double getSaldoLiberar() {
        return saldoLiberar;
    }

    /**
     * @param saldoLiberar the saldoLiberar to set
     */
    public void setSaldoLiberar(double saldoLiberar) {
        this.saldoLiberar = saldoLiberar;
    }

    /**
     * @return the formularioAjuste
     */
    public Formulario getFormularioAjuste() {
        return formularioAjuste;
    }

    /**
     * @param formularioAjuste the formularioAjuste to set
     */
    public void setFormularioAjuste(Formulario formularioAjuste) {
        this.formularioAjuste = formularioAjuste;
    }

    /**
     * @return the formularioLiberarSaldo
     */
    public Formulario getFormularioLiberarSaldo() {
        return formularioLiberarSaldo;
    }

    /**
     * @param formularioLiberarSaldo the formularioLiberarSaldo to set
     */
    public void setFormularioLiberarSaldo(Formulario formularioLiberarSaldo) {
        this.formularioLiberarSaldo = formularioLiberarSaldo;
    }

    /**
     * @return the tieneCertificacion
     */
    public boolean isTieneCertificacion() {
        return tieneCertificacion;
    }

    /**
     * @param tieneCertificacion the tieneCertificacion to set
     */
    public void setTieneCertificacion(boolean tieneCertificacion) {
        this.tieneCertificacion = tieneCertificacion;
    }

}
