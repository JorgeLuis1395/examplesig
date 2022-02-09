/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.TrackingcontratoFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Trackingcontrato;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.RolEmpleadoBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteContrFechas")
@ViewScoped
public class ReporteContratosFechasBean implements Serializable {

    /**
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the tipocontrato
     */
    public Codigos getTipocontrato() {
        return tipocontrato;
    }

    /**
     * @param tipocontrato the tipocontrato to set
     */
    public void setTipocontrato(Codigos tipocontrato) {
        this.tipocontrato = tipocontrato;
    }

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Contratos> listadoContratos;
    private List<Contratos> listaContratos;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private String tipoFecha = "o.inicio";
    private Date desde;
    private Date hasta;
    @EJB
    private ContratosFacade ejbContratos;
    @EJB
    private DetallecompromisoFacade ejbDetalleCompromisos;
    @EJB
    private CompromisosFacade ejbCompromiso;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private TrackingcontratoFacade ejbTrackingcontrato;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;

    private Resource reporte;

    //PARAMETROS DE BUSQUEDA 
    private String titulo = "";
    private String proveedor = "";
    private String formapago = "";
    private String certificacion = "";
    private Integer nrocertificacion;
    private String valor = "";
    private String anticipo = "";
    private Organigrama direccion;
    private Codigos tipocontrato;

    private Resource reporteXls;
    private Formulario formularioReporte = new Formulario();

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

    /* @return the parametrosSeguridad
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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteContrFechaVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of ContratosEmpleadoBean
     */
    public ReporteContratosFechasBean() {
        listadoContratos = new LazyDataModel<Contratos>() {

            @Override
            public List<Contratos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {

        listadoContratos = new LazyDataModel<Contratos>() {

            @Override
            public List<Contratos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };

        return null;
    }

//    public String buscar() {
//        // traer la certificacion de los contratos en base 
//        listadoContratos = new LazyDataModel<Contratos>() {
//
//            @Override
//            public List<Contratos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
    public List<Contratos> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.proceso asc");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = "" + tipoFecha + " between :desde and :hasta and o.padre is null";
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();
            if ((clave.equalsIgnoreCase("notificado")) || (clave.equalsIgnoreCase("conformado")) || (clave.equalsIgnoreCase("auditado"))) {
                if (valor != null) {
                    if (valor.length() == 2) {
                        if (valor.toUpperCase().equals("SI")) {
                            where += " and o." + clave + "=true";
                        } else {
                            where += " and o." + clave + "=false";
                        }
                    }
                }
            } else {

                String palabra = "palabra";
                where += " and upper(o." + clave + ") like :" + palabra;
                parametros.put(palabra, "%" + valor.toUpperCase() + "%");
            }

        }
        if (!(titulo == null || titulo.trim().isEmpty())) {
            where += " and upper(o.titulo) like :titulo";
            parametros.put("titulo", "%" + titulo.toUpperCase() + "%");
        }
        if (proveedorBean.getProveedor() != null) {
            where += " and o.proveedor=:proveedor ";
            parametros.put("proveedor", proveedorBean.getProveedor());
        }
//                if (!(proveedor == null || proveedor.trim().isEmpty())) {
//                    where += " and upper(o.proveedor.empresa.nombrecomercial) like :proveedor";
//                    parametros.put("proveedor", "%" + proveedor.toUpperCase() + "%");
//                }
        if (!(formapago == null || formapago.trim().isEmpty())) {
            where += " and upper(o.formapago) like :formapago";
            parametros.put("formapago", "%" + formapago.toUpperCase() + "%");
        }
        if (!(certificacion == null || certificacion.trim().isEmpty())) {
            where += " and upper(o.certificacion.motivo) like :certificacion";
            parametros.put("certificacion", "%" + certificacion.toUpperCase() + "%");
        }
        if (direccion != null) {
            where += " and o.direccion=:direccion";
            parametros.put("direccion", direccion);
        }
        if (tipocontrato != null) {
            where += " and o.tipocontrato=:tipocontrato";
            parametros.put("tipocontrato", tipocontrato);
        }
        if (entidadesBean.getEntidad() != null) {
            where += " and o.administrador=:administrador";
            parametros.put("administrador", entidadesBean.getEntidad());
        }
//                if (nrocertificacion != null) {
//                    where += " and o.certificacion.id=:nrocertificacion";
//                    parametros.put("nrocertificacion", nrocertificacion);
//                }

        int total;
        try {
            parametros.put(";where", where);
//                    parametros.put(";orden", "o.proceso");
//                    parametros.put(";orden", tipoFecha + " desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            total = ejbContratos.contar(parametros);
            int endIndex = i + pageSize;
            if (endIndex > total) {
                endIndex = total;
            }
            parametros.put(";inicial", i);
            parametros.put(";final", endIndex);
            listadoContratos.setRowCount(total);
            List<Contratos> lista = ejbContratos.encontarParametros(parametros);
//                    listaContratos = ejbContratos.encontarParametros(parametros);
            return ejbContratos.encontarParametros(parametros);
//                    for (Contratos c : lista) {
//                        //ponerCertificacion(c);
//                        ponerDetalleCertificacion(c);
//                    }
//                    return lista;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
//            }
//        };
//        return null;
    }

    private void ponerCertificacion(Contratos c) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.compromiso.contrato=:contrato");
            parametros.put("contrato", c);
            List<Detallecompromiso> lista = ejbDetalleCompromisos.encontarParametros(parametros);
            for (Detallecompromiso d : lista) {
                c.setCertificacion(d.getCompromiso().getCertificacion());
                return;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String ponerDetalleCertificacion(Contratos c) {
        String retorno = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
//            List<Detallecompromiso> detallescompromisos = new LinkedList<>();
//            for (Compromisos lc : compromisos) {
//                parametros = new HashMap();
//                parametros.put(";where", "o.compromiso=:compromiso");
//                parametros.put("compromiso", lc);
//                List<Detallecompromiso> aux = ejbDetalleCompromisos.encontarParametros(parametros);
//                for(Detallecompromiso dc : aux){
//                    detallescompromisos.add(dc);
//                }
//            }
//            for (Detallecompromiso dtcpm : detallescompromisos) {
//                if (dtcpm.getDetallecertificacion() != null && dtcpm.getDetallecertificacion().getCertificacion() != null) {
//                    retorno +="<p><ul><strong>N. Certificación : " + dtcpm.getDetallecertificacion().getCertificacion().getId()+"</strong>";
//                    retorno += "<li><strong> Motivo : </strong>" + dtcpm.getDetallecertificacion().getCertificacion().getMotivo() + 
//                            "</li><li><strong>Fecha : </strong>" + sdf.format(dtcpm.getDetallecertificacion().getCertificacion().getFecha())+"</li>";
//                    retorno +="</p></ul>";
//                }
//            }
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    retorno += "<p><ul><strong>N. Certificación : " + dtcpm.getCertificacion().getNumerocert() + "</strong>";
                    retorno += "<li><strong> Motivo : </strong>" + dtcpm.getCertificacion().getMotivo()
                            + "</li><li><strong>Fecha : </strong>" + sdf.format(dtcpm.getCertificacion().getFecha()) + "</li>";
                    retorno += "</p></ul>";
                }
            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String ponerNumeroCertificacion(Contratos c) {
        String retorno = "";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    retorno += dtcpm.getCertificacion().getNumerocert();
                }
            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String ponerFechaCertificacion(Contratos c) {
        String retorno = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    retorno += sdf.format(dtcpm.getCertificacion().getFecha());
                }
            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double ponerValorCertificacion(Contratos c) {
        try {
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
//            double numero = 0;
            double numero = 0;
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.certificacion=:certificacion");
                    parametros.put("certificacion", dtcpm.getCertificacion());
                    List<Detallecertificaciones> certificaciones = ejbDetCert.encontarParametros(parametros);
                    for (Detallecertificaciones dc : certificaciones) {
                        numero += dc.getValor().doubleValue();
                    }
                }
            }
            return numero;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String ponerNumeroCompromiso(Contratos c) {
        String retorno = "";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    retorno += dtcpm.getNumerocomp();
                }
            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Integer ponerIdCompromiso(Contratos c) {
        Integer retorno = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            for (Compromisos dtcpm : compromisos) {
                retorno = dtcpm.getId();
            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String ponerFechaCompromiso(Contratos c) {
        String retorno = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    retorno += sdf.format(dtcpm.getFecha());
                }
            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double ponerValorCompromiso(Contratos c) {
        String retorno = null;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            double numero = 0;
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.compromiso=:compromiso");
                    parametros.put("compromiso", dtcpm);
                    List<Detallecompromiso> detalleCompromisos = ejbDetComp.encontarParametros(parametros);
                    for (Detallecompromiso dc : detalleCompromisos) {
                        numero += dc.getValor().doubleValue();
                    }
                }
            }
            return numero;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double ponerValorCompromisoDoble(Contratos c) {
        String retorno = null;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            double numero = 0;
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.compromiso=:compromiso");
                    parametros.put("compromiso", dtcpm);
                    List<Detallecompromiso> detalleCompromisos = ejbDetComp.encontarParametros(parametros);
                    for (Detallecompromiso dc : detalleCompromisos) {
                        numero += dc.getValor().doubleValue();
                    }
                }
            }
            return numero;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String ponerPartidaCompromiso(Contratos c) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            String numero = "";
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.compromiso=:compromiso");
                    parametros.put("compromiso", dtcpm);
                    parametros.put(";orden", "o.id desc");
                    List<Detallecompromiso> detalleCompromisos = ejbDetComp.encontarParametros(parametros);
                    for (Detallecompromiso dc : detalleCompromisos) {
                        numero += dc.getAsignacion().getClasificador().getCodigo() + "\n";
                    }
                }
            }
            return numero;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String ponerPartidaNombreCompromiso(Contratos c) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            String numero = "";
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.compromiso=:compromiso");
                    parametros.put("compromiso", dtcpm);
                    parametros.put(";orden", "o.id desc");
                    List<Detallecompromiso> detalleCompromisos = ejbDetComp.encontarParametros(parametros);
                    for (Detallecompromiso dc : detalleCompromisos) {
                        numero += dc.getAsignacion().getClasificador().getNombre() + "\n";
                    }
                }
            }
            return numero;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String ponerValorPartidaCompromiso(Contratos c) {
        try {
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);
            String numero = "";
            for (Compromisos dtcpm : compromisos) {
                if (dtcpm.getCertificacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.compromiso=:compromiso");
                    parametros.put("compromiso", dtcpm);
                    parametros.put(";orden", "o.id desc");
                    List<Detallecompromiso> detalleCompromisos = ejbDetComp.encontarParametros(parametros);
                    for (Detallecompromiso dc : detalleCompromisos) {
                        numero += df.format(dc.getValor().doubleValue()) + "\n";
                    }
                }
            }
            return numero;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String ponerDetalleCompromiso(Contratos c) {
        String retorno = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", c);
            List<Compromisos> compromisos = ejbCompromiso.encontarParametros(parametros);

            for (Compromisos com : compromisos) {

                if (com.getNumerocomp() != null) {
                    retorno += "<p><ul><strong>N. Compromiso : " + com.getNumerocomp() + "</strong>";
                    retorno += "<li><strong> Motivo : </strong>" + com.getMotivo()
                            + "</li><li><strong>Fecha : </strong>" + sdf.format(com.getFecha()) + "</li>";
                    retorno += "</p></ul>";
                }
            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        String where = "" + tipoFecha + " between :desde and :hasta and o.padre is null";
        Map parametros = new HashMap();
        try {
            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<Contratos> lista = ejbContratos.encontarParametros(parametros);
            for (Contratos c : lista) {
                ponerCertificacion(c);
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Reporte de Contratos");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Contratos.jasper"),
                    lista, "Contratos" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
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
     * @return the listadoFamiliares
     */
    public LazyDataModel<Contratos> getListadoContratos() {
        return listadoContratos;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Contratos> listadoFamiliares) {
        this.listadoContratos = listadoFamiliares;
    }

    /**
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
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
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @return the formapago
     */
    public String getFormapago() {
        return formapago;
    }

    /**
     * @param formapago the formapago to set
     */
    public void setFormapago(String formapago) {
        this.formapago = formapago;
    }

    /**
     * @return the certificacion
     */
    public String getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(String certificacion) {
        this.certificacion = certificacion;
    }

    /**
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }

    /**
     * @return the anticipo
     */
    public String getAnticipo() {
        return anticipo;
    }

    /**
     * @param anticipo the anticipo to set
     */
    public void setAnticipo(String anticipo) {
        this.anticipo = anticipo;
    }

    /**
     * @return the nrocertificacion
     */
    public Integer getNrocertificacion() {
        return nrocertificacion;
    }

    /**
     * @param nrocertificacion the nrocertificacion to set
     */
    public void setNrocertificacion(Integer nrocertificacion) {
        this.nrocertificacion = nrocertificacion;
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
     * @return the direccion
     */
    public Organigrama getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    public String exportar() {
        try {
            if (listaContratos == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("Contratos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TIPO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "No. PROCESO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NÚMERO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TITULO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "PROVEEDOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "BASE IMPONIBLE 0"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "BASE IMPONIBLE IVA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "IVA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ANTICIPO"));

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA DE INICIO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA DE FIN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA DE FIRMA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA DE ANTICIPO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "F. PAGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FORMA DE PAGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nro. CERT."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA CERT."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR CERT."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nro. COMP."));

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA COMP."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR COMP."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "DIRECCIÓN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ES OBRA?"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ADMINISTRADOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "URL COMPRAS PÚBLICAS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ESTADO"));

            xls.agregarFila(campos, true);
            for (Contratos e : listaContratos) {
                if (e != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    double valorT = e.getValor().doubleValue() - (e.getSubtotal().doubleValue() + e.getIva().doubleValue());
                    String numeroCert = ponerNumeroCertificacion(e);
                    String fechaCert = ponerFechaCertificacion(e);
                    double valorCert = ponerValorCertificacion(e);
//                    double valoCert = Double.parseDouble(valorCert);
                    String numeroComp = ponerNumeroCompromiso(e);
                    String fechaComp = ponerFechaCompromiso(e);
                    double valorComp = ponerValorCompromiso(e);
//                    double valoComp = Double.parseDouble(valorComp);

                    String estado = null;
                    if (e.getEstado() != null) {
                        if (e.getEstado() == 0) {
                            estado = "INGRESADO";
                        }
                        if (e.getEstado() == 1) {
                            estado = "ENTREGA ANTICIPO";
                        }
                        if (e.getEstado() == 2) {
                            estado = "EN PROCESO";
                        }
                        if (e.getEstado() == 3) {
                            estado = "LIQUIDADO";
                        }
                        if (e.getEstado() == 4) {
                            estado = "T. MUTUO ACUERDO";
                        }
                        if (e.getEstado() == 5) {
                            estado = "T. UNILATERAL";
                        }
                    }
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", e.getTipocontrato() != null ? e.getTipocontrato().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", e.getProceso() != null ? e.getProceso() : ""));
                    campos.add(new AuxiliarReporte("String", e.getNumero() != null ? e.getNumero() : ""));
                    campos.add(new AuxiliarReporte("String", e.getTitulo() != null ? e.getTitulo() : ""));
                    campos.add(new AuxiliarReporte("String", e.getProveedor() != null ? e.getProveedor().getEmpresa().toString() : ""));
                    campos.add(new AuxiliarReporte("String", e.getSubtotal() != null ? df.format(e.getSubtotal()) : ""));
                    campos.add(new AuxiliarReporte("String", e.getIva() != null ? df.format(e.getIva()) : ""));
                    campos.add(new AuxiliarReporte("String", df.format(valorT)));
                    campos.add(new AuxiliarReporte("String", e.getValor() != null ? df.format(e.getValor()) : ""));
                    campos.add(new AuxiliarReporte("String", e.getAnticipo() != null ? df.format(e.getAnticipo()) : ""));

                    campos.add(new AuxiliarReporte("String", e.getInicio() != null ? sdf.format(e.getInicio()) : ""));
                    campos.add(new AuxiliarReporte("String", e.getFin() != null ? sdf.format(e.getFin()) : ""));
                    campos.add(new AuxiliarReporte("String", e.getFirma() != null ? sdf.format(e.getFirma()) : ""));
                    campos.add(new AuxiliarReporte("String", e.getFechaanticipo() != null ? sdf.format(e.getFechaanticipo()) : ""));
                    campos.add(new AuxiliarReporte("String", e.getFpago() != null ? e.getFpago().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", e.getFormapago() != null ? e.getFormapago() : ""));
                    campos.add(new AuxiliarReporte("String", numeroCert != null ? numeroCert : ""));
                    campos.add(new AuxiliarReporte("String", fechaCert != null ? fechaCert : ""));
//                    campos.add(new AuxiliarReporte("String", valorCert != null ? df.format(valorCert) : ""));
                    campos.add(new AuxiliarReporte("String", df.format(valorCert)));
                    campos.add(new AuxiliarReporte("String", numeroComp != null ? numeroComp : ""));

                    campos.add(new AuxiliarReporte("String", fechaComp != null ? fechaComp : ""));
//                    campos.add(new AuxiliarReporte("String", valorComp != null ? df.format(valorComp) : ""));
                    campos.add(new AuxiliarReporte("String", df.format(valorComp)));
                    campos.add(new AuxiliarReporte("String", e.getDireccion() != null ? e.getDireccion().toString() : ""));
                    campos.add(new AuxiliarReporte("String", e.getObra() ? "SI" : "NO"));
                    campos.add(new AuxiliarReporte("String", e.getAdministrador() != null ? e.getAdministrador().toString() : ""));
                    campos.add(new AuxiliarReporte("String", e.getUrlcompras() != null ? e.getUrlcompras() : ""));
                    campos.add(new AuxiliarReporte("String", estado != null ? estado : ""));
                    xls.agregarFila(campos, false);
                }
            }
            setReporte(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the listaContratos
     */
    public List<Contratos> getListaContratos() {
        return listaContratos;
    }

    /**
     * @param listaContratos the listaContratos to set
     */
    public void setListaContratos(List<Contratos> listaContratos) {
        this.listaContratos = listaContratos;
    }

    public String traerCreacion(Contratos c) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
        parametros.put("contrato", c);
        parametros.put(";inicial", 0);
        parametros.put(";final", 1);
        parametros.put(";orden", "o.fecha asc");
        try {
            List<Trackingcontrato> listaTk = ejbTrackingcontrato.encontarParametros(parametros);
            for (Trackingcontrato t : listaTk) {
                return t.getUsuario();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
