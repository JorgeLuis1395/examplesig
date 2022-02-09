/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AmortizcontablesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetalleamortizFacade;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.entidades.sfccbdmq.Amortizcontables;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detalleamortiz;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "amortizContabSfccbdmq")
@ViewScoped
public class AmortizContabBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public AmortizContabBean() {
        amortizaciones = new LazyDataModel<Amortizcontables>() {

            @Override
            public List<Amortizcontables> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Amortizcontables> amortizaciones;
    private List<Detalleamortiz> detallees;
    private Detalleamortiz detalle;
    private List<Detalleamortiz> detalleesb;
    private Amortizcontables amortizacion;
    private Obligaciones obligacion;
    private Formulario formulario = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    @EJB
    private AmortizcontablesFacade ejbAmortizcontables;
    @EJB
    private DetalleamortizFacade ejbDetalleamortiz;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private DocumentoselectronicosFacade ejbDocElec;
    @EJB
    private RascomprasFacade ejbDetalles;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private Perfiles perfil;
    private Date desde;
    private String motivo;
    private Cabeceras cabecera;
    private boolean activos = true;
    private Date hasta;
    private Double valor;
    private Double valorAnterior;
    private List<Renglones> renglones;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{obligacionesSfccbdmq}")
    private ObligacionesBean obligacionesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
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
        desde = configuracionBean.getConfiguracion().getPinicial();
        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "AmortizcontablesVista";
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
        amortizaciones = new LazyDataModel<Amortizcontables>() {
            @Override
            public List<Amortizcontables> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc,o.id");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }

                String where = "o.fecha between :desde and :hasta ";
//                    String where = "o.fecha between :desde and :hasta and o.nomina is null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }

                parametros.put("desde", desde);
                parametros.put("hasta", hasta);

                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo ";
                    parametros.put("motivo", motivo + "%");
                }
                if (activos) {
                    where += " and o.finalizado is null";
                } else {
                    where += " and o.finalizado is not null";
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbAmortizcontables.contar(parametros);
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
                amortizaciones.setRowCount(total);
                try {
                    return ejbAmortizcontables.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String crear() {

        amortizacion = new Amortizcontables();
        amortizacion.setFecha(new Date());
        detallees = new LinkedList<>();
        detalleesb = new LinkedList<>();
        obligacionesBean.setTieneAmortizaciones(true);
        obligacionesBean.setEstado(2);
        formularioObligacion.insertar();
        valor = new Double(0);
        return null;
    }

    public String seleccionaObligacion(Obligaciones obligacion) {
        this.setObligacion(obligacion);
        formularioObligacion.cancelar();
        formulario.insertar();
        return null;
    }

    public double getTraerValor() {
        if (obligacion == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        parametros.put(";campo", "o.valortotal");
        try {
            return ejbDocElec.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String modificar(Amortizcontables amortizacion) {
        this.amortizacion = amortizacion;
        getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.amortizcontab=:amortizacion");
        parametros.put("amortizacion", amortizacion);
        try {
            valor = new Double(0);
            detallees = ejbDetalleamortiz.encontarParametros(parametros);
            for (Detalleamortiz d : detallees) {
                if (d.getValor().doubleValue() == 0) {
                    ejbDetalleamortiz.remove(d, seguridadbean.getLogueado().getUserid());
                }
                valor += d.getValor().doubleValue();
            }
            valor = new Double(Math.round(valor * 100) / 100);
            parametros = new HashMap();
            parametros.put(";where", "o.amortizacion=:amortizacion");
            parametros.put("amortizacion", amortizacion);
            List<Obligaciones> obl = ejbObligaciones.encontarParametros(parametros);
            for (Obligaciones o : obl) {
                obligacion = o;
            }
        } catch (ConsultarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        detalleesb = new LinkedList<>();
        return null;
    }

    public String contabilizar(Amortizcontables amortizacion) {
        this.amortizacion = amortizacion;
        cabecera = new Cabeceras();
        Map parametros = new HashMap();
        parametros.put(";where", "o.amortizcontab=:amortizacion "
                + "and o.contabilizado is null and o.fecha between :desde and :hasta");
        parametros.put(";orden", "o.fecha asc");
        parametros.put("desde", configuracionBean.getConfiguracion().getPvigente());
        parametros.put("hasta", new Date());
        parametros.put("amortizacion", amortizacion);
        try {
            detalle = null;
            valor = new Double(0);
            detallees = ejbDetalleamortiz.encontarParametros(parametros);
            if (!detallees.isEmpty()) {
                detalle = detallees.get(0);
            }
            if (detalle == null) {
                MensajesErrores.advertencia("No existe nada por contabilizar");
                return null;
            }
            renglones = new LinkedList<>();
            cabecera.setFecha(detalle.getFecha());
            String vale = ejbCabecera.validarCierre(cabecera.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.amortizacion=:amortizacion");
            parametros.put("amortizacion", amortizacion);
            List<Obligaciones> obl = ejbObligaciones.encontarParametros(parametros);
            for (Obligaciones o : obl) {
                obligacion = o;
            }
            Renglones r = new Renglones();
            Cuentas ctaUno = cuentasBean.traerCodigo(amortizacion.getCtadebito());
            r.setCuenta(amortizacion.getCtadebito());
            r.setFecha(detalle.getFecha());
            r.setValor(detalle.getValor());
            if (ctaUno.getAuxiliares() != null) {
                r.setAuxiliar(obligacion.getProveedor().getEmpresa().getRuc());
            }
            renglones.add(r);
            r = new Renglones();
            Cuentas cta = cuentasBean.traerCodigo(amortizacion.getCtacredito());
            r.setCuenta(amortizacion.getCtacredito());
            r.setFecha(detalle.getFecha());
            r.setValor(detalle.getValor().negate());
            if (cta.getAuxiliares() != null) {
                if (obligacion.getProveedor() != null) {
                    r.setAuxiliar(obligacion.getProveedor().getEmpresa().getRuc());
                } else {
                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion");
                    parametros.put("obligacion", obligacion);
                    List<Documentoselectronicos> listaDocument = ejbDocElec.encontarParametros(parametros);
                    if (!listaDocument.isEmpty()) {
                        Cabdocelect cab = listaDocument.get(0).getCabeccera();
                        if (cab != null) {
                            if (cab.getCompromiso() != null) {
                                if (cab.getCompromiso().getBeneficiario() != null) {
                                    r.setAuxiliar(cab.getCompromiso().getBeneficiario().getEntidad().getPin());
                                }
                            }
                        }
                    }
                }
            }
            renglones.add(r);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioContabilizar.editar();
        detalleesb = new LinkedList<>();
        return null;
    }

    public String grabarContabilizar() {
        if (cabecera.getTipo() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de asiento");
            return null;
        }
        if ((cabecera.getDescripcion() == null) || (cabecera.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Descripción necesaria");
            return null;
        }
        String vale = ejbCabecera.validarCierre(cabecera.getFecha());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        Tipoasiento ta = cabecera.getTipo();
        int numeroAsiento = ta.getUltimo();
        numeroAsiento++;
        ta.setUltimo(numeroAsiento);
        try {
            ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
            cabecera.setNumero(numeroAsiento);
            cabecera.setDia(new Date());
            cabecera.setNumero(numeroAsiento);
            cabecera.setIdmodulo(amortizacion.getId());
            cabecera.setUsuario(seguridadbean.getLogueado().getUserid());
            cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabecera.setOpcion("AMORTIZACIONES");
            ejbCabecera.create(cabecera, seguridadbean.getLogueado().getUserid());
            for (Renglones r : renglones) {
                r.setCabecera(cabecera);
                r.setReferencia(cabecera.getDescripcion());
                r.setFecha(cabecera.getFecha());
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            }
            detalle.setContabilizado(detalle.getFecha());
            ejbDetalleamortiz.edit(detalle, seguridadbean.getLogueado().getUserid());
            // ver si se acabo
            Map parametros = new HashMap();
            parametros.put(";where", "o.amortizcontab=:amortizacion and o.contabilizado is null");
            parametros.put(";orden", "o.fecha desc");
            parametros.put("amortizacion", amortizacion);
            int total = ejbDetalleamortiz.contar(parametros);
            if (total == 0) {
                amortizacion.setFinalizado(new Date());
                ejbAmortizcontables.edit(amortizacion, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioContabilizar.cancelar();
        return null;

    }

    public String imprimir(Amortizcontables amortizacion) {
//        this.amortizacion = amortizacion;
//        setImprimirNuevo(amortizacion.getImpresion() == null);
//        //*******************************************************************//
//        amortizacion.setImpresion(amortizacion.getFecha());
////        amortizacion.setImpresion(new Date());
//        List<Auxiliar> titulos = proyectoBean.getTitulos();
//        try {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.amortizacion=:amortizacion");
//            parametros.put("amortizacion", amortizacion);
//            detallees = ejbDetalleamortiz.encontarParametros(parametros);
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
//            DocumentoPDF pdf = new DocumentoPDF("COMPROMISO PRESUPUESTARIO No :" + amortizacion.getId().toString(), seguridadbean.getLogueado().getUserid());
//            List<AuxiliarReporte> columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("String", "Fecha :"));
//            columnas.add(new AuxiliarReporte("Date", amortizacion.getFecha()));
//            columnas.add(new AuxiliarReporte("String", ""));
//            columnas.add(new AuxiliarReporte("String", ""));
//            if (amortizacion.getProveedor() != null) {
//                columnas.add(new AuxiliarReporte("String", "Proveedor :"));
//                columnas.add(new AuxiliarReporte("String", amortizacion.getProveedor().getEmpresa().toString()));
//                columnas.add(new AuxiliarReporte("String", "Contrato :"));
//                if (amortizacion.getContrato() != null) {
//                    columnas.add(new AuxiliarReporte("String", amortizacion.getContrato().toString()));
//                } else {
//                    columnas.add(new AuxiliarReporte("String", "SIN CONTRATO"));
//                }
//            }
//
//            pdf.agregarTabla(null, columnas, 4, 100, null);
//            pdf.agregaParrafo("Concepto :" + amortizacion.getMotivo());
//            pdf.agregaParrafo("\n\n");
//            List<AuxiliarReporte> titulosReporte = new LinkedList<>();
//            int totalCol = 5;
//            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
//            titulosReporte.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
//            titulosReporte.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
//            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
//            if (amortizacion.getProveedor() == null) {
//                titulosReporte.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
//                totalCol++;
//            }
//            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
//            columnas = new LinkedList<>();
//            double total = 0;
//            for (Detalleamortiz d : detallees) {
//                String que = "";
//                for (Auxiliar a : titulos) {
//                    que += a.getTitulo1() + " : ";
//                    que += proyectoBean.dividir(a, d.getDetallecertificacion().getAsignacion().getProyecto()) + " ";
//
//                }
//                d.setArbolProyectos(que);
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getDetallecertificacion().getAsignacion().getClasificador().getCodigo()));
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getDetallecertificacion().getAsignacion().getClasificador().getNombre()));
//                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, que));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getDetallecertificacion().getAsignacion().getFuente().getNombre()));
//                if (amortizacion.getProveedor() == null) {
//                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getProveedor().getEmpresa().toString()));
//                }
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
//                total += d.getValor().doubleValue();
//            }
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            if (amortizacion.getProveedor() == null) {
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//            }
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, total));
//            pdf.agregarTablaReporte(titulosReporte, columnas, totalCol, 100, null);
//            pdf.agregaParrafo("\n\n");
//            pdf.agregaParrafo("\n\n");
//            pdf.agregaParrafo("\n\n");
//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", "Analista de Presupuesto"));
//            columnas.add(new AuxiliarReporte("String", "Director(a) Financiera"));
//            pdf.agregarTabla(null, columnas, 2, 100, null);
//            reporte = pdf.traerRecurso();
//            ejbAmortizcontables.edit(amortizacion, seguridadbean.getLogueado().getUserid());
//
////            parametros = new HashMap();
////            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
////            parametros.put("expresado", "Amortizacion presupuestaria No  : " + amortizacion.getId());
////            parametros.put("nombrelogo", "logo-new.png");
////            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
////            parametros.put("fecha", amortizacion.getFecha());
////            parametros.put("contrato", (amortizacion.getContrato() == null ? "" : amortizacion.getContrato().toString()));
////            parametros.put("contratista", (amortizacion.getProveedor() == null ? "" : amortizacion.getProveedor().getEmpresa().toString()));
////            parametros.put("concepto", amortizacion.getMotivo());
////            Calendar c = Calendar.getInstance();
////            reporte = new Reportesds(parametros,
////                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Amortizacion1.jasper"),
////                    detallees, "Amortizacion" + String.valueOf(c.getTimeInMillis()) + ".pdf");
//        } catch (GrabarException | ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException | DocumentException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formularioImpresion.insertar();

        return null;
    }

    public String eliminar(Amortizcontables amortizacion) {
        this.amortizacion = amortizacion;
        if (amortizacion.getFinalizado() != null) {
            MensajesErrores.advertencia("No se puede eliminar amortización ya finalizada");
            return null;
        }
//       getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.amortizcontab=:amortizacion");
        parametros.put("amortizacion", amortizacion);
        try {
            valor = new Double(0);
            detallees = ejbDetalleamortiz.encontarParametros(parametros);
            for (Detalleamortiz d : detallees) {
                valor += d.getValor().doubleValue();
                if (d.getContabilizado() != null) {
                    MensajesErrores.advertencia("No se puede eliminar amortización ya contabilizada un mes");
                    return null;
                }
            }

            valor = new Double(Math.round(valor * 100) / 100);
            parametros = new HashMap();
            parametros.put(";where", "o.amortizacion=:amortizacion");
            parametros.put("amortizacion", amortizacion);
            List<Obligaciones> obl = ejbObligaciones.encontarParametros(parametros);
            for (Obligaciones o : obl) {
                obligacion = o;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
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

    public boolean validar() {
//        if ((amortizacion.getValorcertificacion() == null) || (amortizacion.getValorcertificacion().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Es necesario valor de amortizacion");
//            return true;
//        }
        if ((amortizacion.getMotivo() == null) || (amortizacion.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de amortizacion");
            return true;
        }
        if (amortizacion.getFecha() == null) {
            MensajesErrores.advertencia("Es necesario fecha de amortizacion");
            return true;
        }
        if (amortizacion.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Es necesario fecha de amortizacion mayor o igual a periodo vigente");
            return true;
        }
        double valOb = getTraerValor();
        double suma = Math.round((valOb - valor) * 100);
        if (suma < 0) {
            MensajesErrores.advertencia("El valor de la amortización debe ser mayor o igual a la factura");
            return true;
        }
        suma = Math.round((getTotalRenglones() - valor) * 100);
        if (suma != 0) {
            MensajesErrores.advertencia("El valor de la tabla debe ser igual al  valor de la amortización");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {

            ejbAmortizcontables.create(amortizacion, seguridadbean.getLogueado().getUserid());
            for (Detalleamortiz o : detallees) {
                o.setAmortizcontab(amortizacion);
                ejbDetalleamortiz.create(o, seguridadbean.getLogueado().getUserid());
            }
            getObligacion().setAmortizacion(amortizacion);
            ejbObligaciones.edit(getObligacion(), seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String crearAmortizacion() {
        if ((amortizacion.getDias() == null) || (amortizacion.getDias() <= 0)) {
            MensajesErrores.fatal("Número de días debe ser mayor a cero");
            return null;
        }
        if (amortizacion.getFecha() == null) {
            MensajesErrores.fatal("Fecha debe ser válida");
            return null;
        }
        if ((valor == null) || (valor == 0)) {
            MensajesErrores.fatal("Valor debe ser mayor a cero");
            return null;
        }
        detallees = new LinkedList<>();
        boolean salir = true;
        Calendar mes = Calendar.getInstance();
        mes.setTime(amortizacion.getFecha());
        double valorDiario = valor / amortizacion.getDias();
        // fecha final tomando en cuenta los dias
        Calendar finPeriodo = Calendar.getInstance();
        finPeriodo.setTime(amortizacion.getFecha());
        finPeriodo.add(Calendar.DATE, amortizacion.getDias());
        // calcular el numero de días para fin de mes
        int mesAcual = mes.get(Calendar.MONTH);
        int anioAcual = mes.get(Calendar.YEAR);
        int diaActual = mes.get(Calendar.DATE);
        double valorTotal = valor;
        while (salir) {
            Calendar finDeMes = Calendar.getInstance();
            // mes siguinete
            finDeMes.set(anioAcual, mesAcual + 1, 1);
            // dia fin de mes
            finDeMes.add(Calendar.DATE, -1);
            int ultimmoDia = finDeMes.get(Calendar.DATE);
//            int diasDelMes = ultimmoDia - diaActual;
            int diasDelMes = ultimmoDia - diaActual + 1;
            double cuanto = valorDiario * diasDelMes;
            double queda = valorTotal - cuanto;
            Detalleamortiz d = new Detalleamortiz();
            d.setFecha(finDeMes.getTime());
            if (queda < 0) {
                d.setValor(new BigDecimal(valorTotal));
            } else {
                d.setValor(new BigDecimal(cuanto));
            }
            detallees.add(d);
            int valorQueda = (int) queda * 100;
            if (valorQueda / 100 <= 0) {
                salir = false;
            } else {
                valorTotal = queda;
                diaActual = 1;
                mesAcual++;
                if (mesAcual == 12) {
                    anioAcual++;
                    mesAcual = 0;
                }
            }

        }
        return null;
    }

    private long diferenciaDias(Calendar desde, Calendar hasta) {
        return (desde.getTimeInMillis() - hasta.getTimeInMillis()) / Constantes.MILLSECS_PER_DAY;
    }

    public String insertarSinTabla() {
        if (validar()) {
            return null;
        }
        try {
            ejbAmortizcontables.create(amortizacion, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        if (validar()) {
            return null;
        }
        try {

//            amortizacion.setImprimecertificacion(Boolean.FALSE);
            ejbAmortizcontables.edit(amortizacion, seguridadbean.getLogueado().getUserid());
            for (Detalleamortiz o : detallees) {
                o.setAmortizcontab(amortizacion);
                if (o.getId() == null) {
                    ejbDetalleamortiz.create(o, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalleamortiz.edit(o, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Detalleamortiz o : detalleesb) {
                if (o.getId() != null) {
                    ejbDetalleamortiz.remove(o, seguridadbean.getLogueado().getUserid());
                }
            }

        } catch (GrabarException | InsertarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrarTabla() {
        try {
//            amortizacion.setEstado(-1);
            for (Detalleamortiz d : detallees) {
                if (d.getContabilizado() != null) {
                    MensajesErrores.advertencia("No es posible borrar ya fue contabilizado un mes");
                    return null;
                }
            }
            for (Detalleamortiz d : detallees) {
                if (d.getId() != null) {
                    ejbDetalleamortiz.remove(d, seguridadbean.getLogueado().getUserid());
                }
            }

            detallees = new LinkedList<>();
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        buscar();
//        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
//            amortizacion.setEstado(-1);

            for (Detalleamortiz d : detallees) {
                ejbDetalleamortiz.remove(d, seguridadbean.getLogueado().getUserid());
            }
            obligacion.setAmortizacion(null);
            ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            ejbAmortizcontables.remove(amortizacion, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the amortizaciones
     */
    public LazyDataModel<Amortizcontables> getAmortizcontables() {
        return amortizaciones;
    }

    /**
     * @param amortizaciones the amortizaciones to set
     */
    public void setAmortizcontables(LazyDataModel<Amortizcontables> amortizaciones) {
        this.amortizaciones = amortizaciones;
    }

    /**
     * @return the amortizacion
     */
    public Amortizcontables getAmortizacion() {
        return amortizacion;
    }

    /**
     * @param amortizacion the amortizacion to set
     */
    public void setAmortizacion(Amortizcontables amortizacion) {
        this.amortizacion = amortizacion;
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
     * @return the detallees
     */
    public List<Detalleamortiz> getDetalles() {
        return detallees;
    }

    /**
     * @param detallees the detallees to set
     */
    public void setDetalles(List<Detalleamortiz> detallees) {
        this.detallees = detallees;
    }

    /**
     * @return the detalle
     */
    public Detalleamortiz getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detalleamortiz detalle) {
        this.detalle = detalle;
    }

    public String nuevaDetalle() {
        detalle = new Detalleamortiz();
        detalle.setFecha(new Date());
        valorAnterior = 0.0;
//        formularioDetalle.insertar();
        getFormularioDetalle().insertar();

        return null;
    }

    public String modificaDetalle(Detalleamortiz detalle) {
        getFormularioDetalle().setIndiceFila(getFormularioDetalle().getFila().getRowIndex());
        this.detalle = detalle;
        valorAnterior = detalle.getValor().doubleValue();
        getFormularioDetalle().editar();
        return null;
    }

    public String borraDetalle(Detalleamortiz detalle) {
        getFormularioDetalle().setIndiceFila(getFormularioDetalle().getFila().getRowIndex());
        this.detalle = detalle;
        return null;
    }

    public boolean validaDetalle() {
//        if ((detalle.getDetallecertificacion() == null)) {
//            MensajesErrores.advertencia("Es necesario una partida");
//            return true;
//        }
//        if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Es necesario valor de documento");
//            return true;
//        }
//        if (muchos) {
//            if (proveedorBean.getEmpresa() == null) {
//                MensajesErrores.advertencia("Es necesario Proveedor");
//                return true;
//            }
//            if ((detalle.getMotivo() == null) || (detalle.getMotivo().isEmpty())) {
//                MensajesErrores.advertencia("Es necesario motivo del documento");
//                return true;
//            }
//        }
//        // comprobar que los saldos
//        // sumar los ya usados
//        double usados = 0;
//        Map parametros = new HashMap();
//        if (detalle.getId() != null) {
//            parametros.put(";where", "o.detallecertificacion=:detallacertificacion and o.id!=:id and o.detallecertificacion.certificacion!=:certificacion");
//            parametros.put("id", detalle.getId());
//            parametros.put("certificacion", detalle.getDetallecertificacion().getCertificacion());
//        } else {
//            parametros.put(";where", "o.detallecertificacion=:detallacertificacion");
//        }
//
//        parametros.put("detallacertificacion", detalle.getDetallecertificacion());
//        parametros.put(";campo", "o.valor");
//        try {
//            usados = ejbDetalleamortiz.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AmortizContabBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        // ver linea por línea?
//        double nuevo = detalle.getValor().doubleValue();
//        double suma = 0;
//        for (Detalleamortiz d : detallees) {
//            if (d.getDetallecertificacion().equals(detalle.getDetallecertificacion())) {
//                suma += d.getValor().doubleValue();
//            }
//        }
//        double resta = nuevo - valorAnt;
////        suma += resta;
//        double valorActual = detalle.getDetallecertificacion().getValor().doubleValue();
//        if (suma + usados > valorActual) {
//            MensajesErrores.advertencia("Valor sobrepasa ceritificación");
//            return true;
//        }
        return false;
    }

    public String insetarDetalle() {

//        if (esta(detalle.getDetallecertificacion())) {
//            MensajesErrores.advertencia("Partida ya usada en este amortizacion");
//            return null;
//        }
        if (validaDetalle()) {
            return null;
        }

        detallees.add(detalle);
        getFormularioDetalle().cancelar();
        return null;
    }

    public String grabarDetalle() {
        if (validaDetalle()) {
            return null;
        }
        detallees.set(getFormularioDetalle().getIndiceFila(), detalle);
        // Calcular pa aabajo lo que toca
        int indice_desde = getFormularioDetalle().getIndiceFila();
        indice_desde++;
        int total = detallees.size();
        int numeroDeCuotas = total - (indice_desde);
        double valora = (valorAnterior - detalle.getValor().doubleValue()) / numeroDeCuotas;
        for (int i = indice_desde; i < total; i++) {
            Detalleamortiz d = detallees.get(i);
            double valorActual = d.getValor().doubleValue();
            d.setValor(new BigDecimal(valorActual + valora));
        }

        //Traer el valor de la amortizacion
//        double contabilizado = 0;
//        int numero = 0;
//        for (Detalleamortiz dt : detallees) {
//            if (dt.getContabilizado() != null) {
//                contabilizado += dt.getValor().doubleValue();
//            } else {
//                if (dt == detalle) {
//                    contabilizado += dt.getValor().doubleValue();
//                } else {
//                    numero++;
//                }
//            }
//        }
//        double valorDividir = valor - contabilizado;
//        double valorParaDividir = valorDividir / numero;
//        for (Detalleamortiz dt : detallees) {
//            if (dt.getContabilizado() == null) {
//                if (dt != detalle) {
//                    dt.setValor(new BigDecimal(valorParaDividir));
//                }
//            }
//        }
        getFormularioDetalle().cancelar();

        return null;
    }

    public String borrarDetalle() {
        detalleesb.add(detalle);
        detallees.remove(getFormularioDetalle().getIndiceFila());
        getFormularioDetalle().cancelar();
        // colocar el saldo del amortizacion sino se jode
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

    public Detalleamortiz traer(Integer id) throws ConsultarException {
        return ejbDetalleamortiz.find(id);
    }

    public Amortizcontables traerAmortizacion(Integer id) throws ConsultarException {
        return ejbAmortizcontables.find(id);
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
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the activos
     */
    public boolean isActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(boolean activos) {
        this.activos = activos;
    }

    /**
     * @return the formularioObligacion
     */
    public Formulario getFormularioObligacion() {
        return formularioObligacion;
    }

    /**
     * @param formularioObligacion the formularioObligacion to set
     */
    public void setFormularioObligacion(Formulario formularioObligacion) {
        this.formularioObligacion = formularioObligacion;
    }

    /**
     * @return the obligacionesBean
     */
    public ObligacionesBean getObligacionesBean() {
        return obligacionesBean;
    }

    /**
     * @param obligacionesBean the obligacionesBean to set
     */
    public void setObligacionesBean(ObligacionesBean obligacionesBean) {
        this.obligacionesBean = obligacionesBean;
    }

    /**
     * @return the valor
     */
    public Double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(Double valor) {
        this.valor = valor;
    }

    /**
     * @return the obligacion
     */
    public Obligaciones getObligacion() {
        return obligacion;
    }

    /**
     * @param obligacion the obligacion to set
     */
    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    public double getTotalRenglones() {
        if (detallees == null) {
            return 0;
        }
        double total = 0;
        for (Detalleamortiz d : detallees) {
            total += d.getValor().doubleValue();
        }
        return total;
    }

    /**
     * @return the formularioContabilizar
     */
    public Formulario getFormularioContabilizar() {
        return formularioContabilizar;
    }

    /**
     * @param formularioContabilizar the formularioContabilizar to set
     */
    public void setFormularioContabilizar(Formulario formularioContabilizar) {
        this.formularioContabilizar = formularioContabilizar;
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

    public SelectItem[] getComboTipoasientoContab() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre desc");
        parametros.put(";where", "o.modulo=:modulo and o.editable=true");
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
        try {
            return Combos.getSelectItems(ejbTipos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double calculaValor(Obligaciones o) {
        if (o == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valortotal");
        try {
            return ejbDocElec.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerTotal(Amortizcontables amortizacion) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.amortizcontab=:amortizacion");
        parametros.put("amortizacion", amortizacion);
        double valor = 0;
        try {
            List<Detalleamortiz> lista = ejbDetalleamortiz.encontarParametros(parametros);
            for (Detalleamortiz d : lista) {
                valor += d.getValor().doubleValue();
            }
            return valor;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
