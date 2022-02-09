/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
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
import org.auxiliares.sfccbdmq.AuxiliarCompras;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.beans.sfccbdmq.ValesfondosFacade;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Valescajas;
import org.entidades.sfccbdmq.Valesfondos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteCompras")
@ViewScoped
public class ReporteComprasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AuxiliarCompras> listaObligaciones;

    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Date desde;
    private Date hasta;
    private Resource reporteXls;
    private Formulario formularioReporte = new Formulario();
    @EJB
    private DocumentoselectronicosFacade ejbDocElec;
    @EJB
    private ValescajasFacade ejbValescajas;

    @EJB
    private ValesfondosFacade ejbValesfondos;

    @EJB
    private ObligacionesFacade ejbObligaciones;

//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
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
        String nombreForma = "ReporteComprasVista";
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
    public ReporteComprasBean() {

    }

    public String buscar() {

        Map parametros = new HashMap();
        String where = "(o.fechaemision between :desde and :hasta) and o.obligacion is not null";

        parametros.put(";where", where);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        try {
            listaObligaciones = new LinkedList<>();
            List<Documentoselectronicos> listaDocumentos = ejbDocElec.encontarParametros(parametros);

            parametros = new HashMap();
            where = "(o.fecha between :desde and :hasta) and o.estado=2";
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            List<Valesfondos> listaFondos = ejbValesfondos.encontarParametros(parametros);
            parametros = new HashMap();
            where = "(o.fecha between :desde and :hasta) and o.estado=2";
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            List<Valescajas> listaCaja = ejbValescajas.encontarParametros(parametros);
            parametros = new HashMap();
            where = "(o.fecha between :desde and :hasta) and o.estado=2";
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

//            List<Valescajas> listaCaja = ejbValescajas.encontarParametros(parametros);
            for (Documentoselectronicos de : listaDocumentos) {

                String nombre = "";
                if (de.getCabeccera().getCompromiso().getProveedor() != null) {
                    nombre = de.getCabeccera().getCompromiso().getProveedor().getEmpresa().toString();
                } else {
                    if (de.getCabeccera().getCompromiso().getBeneficiario() != null) {
                        nombre = de.getCabeccera().getCompromiso().getBeneficiario().getEntidad().toString();
                    }
                }

                AuxiliarCompras comp = new AuxiliarCompras();
                comp.setBaseImponible0(de.getBaseimponible0() == null ? 0 : de.getBaseimponible0().doubleValue());
                comp.setBaseImponibleIva(de.getBaseimponible() == null ? 0 : de.getBaseimponible().doubleValue());
                comp.setRetencion(de.getNumero());
                comp.setIva(de.getIva() == null ? 0 : de.getIva().doubleValue());
                comp.setProveedor(nombre);
                comp.setValor(de.getValortotal() == null ? 0 : de.getValortotal().doubleValue());
                comp.setFechaInicio(de.getFechaemision());
                listaObligaciones.add(comp);
            }

            for (Valesfondos vf : listaFondos) {

                String nombre = "";
                if (vf.getProveedor() != null) {
                    nombre = vf.getProveedor().getEmpresa().toString();
                }

                AuxiliarCompras compvf = new AuxiliarCompras();
                compvf.setBaseImponible0(vf.getBaseimponiblecero() == null ? 0 : vf.getBaseimponiblecero().doubleValue());
                compvf.setBaseImponibleIva(vf.getBaseimponible() == null ? 0 : vf.getBaseimponible().doubleValue());
                compvf.setIva(vf.getIva() == null ? 0 : vf.getIva().doubleValue());
                compvf.setRetencion(vf.getNumero().toString());
                compvf.setProveedor(nombre);
                compvf.setValor(vf.getValor() == null ? 0 : vf.getValor().doubleValue());
                compvf.setFechaInicio(vf.getFecha());
                listaObligaciones.add(compvf);
            }

            for (Valescajas vc : listaCaja) {
                String nombre = "";
                if (vc.getProveedor() != null) {
                    nombre = vc.getProveedor().getEmpresa().toString();
                }

                AuxiliarCompras compvc = new AuxiliarCompras();
                compvc.setBaseImponible0(vc.getBaseimponiblecero() == null ? 0 : vc.getBaseimponiblecero().doubleValue());
                compvc.setBaseImponibleIva(vc.getBaseimponible() == null ? 0 : vc.getBaseimponible().doubleValue());
                compvc.setIva(vc.getIva() == null ? 0 : vc.getIva().doubleValue());
                compvc.setProveedor(nombre);
                compvc.setRetencion(vc.getNumero().toString());
                compvc.setValor(vc.getValor() == null ? 0 : vc.getValor().doubleValue());
                compvc.setFechaInicio(vc.getFecha());
                listaObligaciones.add(compvc);
            }
            Collections.sort(listaObligaciones, new valorComparator());
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String exportar() {
        try {
            if (getListaObligaciones() == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("Contratos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "PROVEEDOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA EMISIÓN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "RETENCIÓN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "BASE IMPONIBLE 0"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "BASE IMPONIBLE IVA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "IVA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR"));

            xls.agregarFila(campos, true);
            for (AuxiliarCompras de : listaObligaciones) {
                Double imponible = 0.0;
                if (de != null) {
                    
                  
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", de.getProveedor()));
                    campos.add(new AuxiliarReporte("String", sdf.format(de.getFechaInicio())));
                    campos.add(new AuxiliarReporte("String", de.getRetencion()));
                    campos.add(new AuxiliarReporte("Double", de.getBaseImponible0()));
                    campos.add(new AuxiliarReporte("Double", de.getBaseImponibleIva()));
                    campos.add(new AuxiliarReporte("Double", de.getIva()));
                    campos.add(new AuxiliarReporte("Double", de.getValor()));
                    xls.agregarFila(campos, false);

                }
            }
            setReporte(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteComprasBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listaObligaciones
     */
    public List<AuxiliarCompras> getListaObligaciones() {
        return listaObligaciones;
    }

    /**
     * @param listaObligaciones the listaObligaciones to set
     */
    public void setListaObligaciones(List<AuxiliarCompras> listaObligaciones) {
        this.listaObligaciones = listaObligaciones;
    }

    public double getTotalBase0() {
        double total = 0;
        if (listaObligaciones == null) {
            return 0;
        }
        for (AuxiliarCompras com : listaObligaciones) {
            total += com.getBaseImponible0();
        }
        return total;
    }

    public double getTotalBaseIva() {
        double total = 0;
        if (listaObligaciones == null) {
            return 0;
        }
        for (AuxiliarCompras com : listaObligaciones) {
            total += com.getBaseImponibleIva();
        }
        return total;
    }

    public double getTotalValor() {
        double total = 0;
        if (listaObligaciones == null) {
            return 0;
        }
        for (AuxiliarCompras com : listaObligaciones) {
            total += com.getValor();
        }
        return total;
    }

    public double getTotalIva() {
        double total = 0;
        if (listaObligaciones == null) {
            return 0;
        }
        for (AuxiliarCompras com : listaObligaciones) {
            total += com.getIva();
        }
        return total;
    }

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            AuxiliarCompras r = (AuxiliarCompras) o1;
            AuxiliarCompras r1 = (AuxiliarCompras) o2;
            return r1.getFechaInicio().
                    compareTo(r.getFechaInicio());

        }
    }

}
