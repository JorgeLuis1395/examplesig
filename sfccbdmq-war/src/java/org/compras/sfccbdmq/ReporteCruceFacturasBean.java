/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
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
import org.auxiliares.sfccbdmq.AtsSri;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabdocelectFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallescomprasFacade;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallescompras;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cruceFacturasSfccbdmq")
@ViewScoped
public class ReporteCruceFacturasBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ReporteCruceFacturasBean() {
        Calendar c = Calendar.getInstance();

        cabeceraDocumentos = new LazyDataModel<Cabdocelect>() {

            @Override
            public List<Cabdocelect> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Cabdocelect> cabeceraDocumentos;
    private List<Cabdocelect> listaCabeceraDocumentos;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private Resource reporteXls;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;

    @EJB
    private CabdocelectFacade ejbCabdocelect;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private DocumentoselectronicosFacade ejbDocumentoselectronicos;
    @EJB
    private DetallescomprasFacade ejbDetallescompras;
    @EJB
    private RenglonesFacade ejbRenglones;

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String perfilInterno = (String) params.get("p");
        String nombreForma = "PagoLoteVista";
        if (perfilInterno == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilInterno));
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
        cabeceraDocumentos = new LazyDataModel<Cabdocelect>() {
            @Override
            public List<Cabdocelect> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha asc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }

                String where = "o.fecha between :desde and :hasta ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }

                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());

                if (getProveedorBean().getProveedor() != null) {
                    where += " and o.compromiso.proveedor=:proveedor";
                    parametros.put("proveedor", getProveedorBean().getProveedor());
                }
                if (getEntidadesBean().getEntidad() != null) {
                    where += " and o.compromiso.beneficiario=:beneficiario";
                    parametros.put("beneficiario", getEntidadesBean().getEntidad().getEmpleados());
                }

                int total = 0;
                try {
                    parametros.put(";where", where);
                    listaCabeceraDocumentos = ejbCabdocelect.encontarParametros(parametros);
                    total = ejbCabdocelect.contar(parametros);
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
                getCabeceraDocumentos().setRowCount(total);
                try {
                    return ejbCabdocelect.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        return null;
    }

    public double traerValorCompromiso(Cabdocelect cab) {
        double valor = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabeceradoc=:cabeceradoc");
            parametros.put("cabeceradoc", cab);
            List<Detallescompras> lista = ejbDetallescompras.encontarParametros(parametros);
            for (Detallescompras dc : lista) {
                valor += dc.getValor().doubleValue();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCruceFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;

    }

    public double traerValorRenglones(Cabdocelect cab) {
        double valor = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion='OBLIGACIONES_LOTE' and o.valor>0");
            parametros.put("idmodulo", cab.getId());
            List<Renglones> lista = ejbRenglones.encontarParametros(parametros);
            for (Renglones o : lista) {
                valor += o.getValor().doubleValue() * o.getSigno();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCruceFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;

    }

    public double traerValorDetalles(Cabdocelect cab) {
        double valor = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabeccera=:cabeccera and o.cabeccera.anulado is null");
            parametros.put("cabeccera", cab);
            List<Documentoselectronicos> lista = ejbDocumentoselectronicos.encontarParametros(parametros);
            for (Documentoselectronicos o : lista) {
                valor += o.getValortotal().doubleValue();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCruceFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;

    }

    public String exportar() {
        try {
            buscar();
            DocumentoXLS xls = new DocumentoXLS("Compras");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "PROVEEDOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NRO. PAGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "COMPROMISO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR COMPROMISO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "DEVENGADO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR DETALLE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "DIFERENCIA"));

            xls.agregarFila(campos, true);
            for (Cabdocelect e : listaCabeceraDocumentos) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String nombre = "";
                if (e.getCompromiso().getProveedor() != null) {
                    nombre = e.getCompromiso().getProveedor().toString();
                } else {
                    if (e.getCompromiso().getBeneficiario() != null) {
                        nombre = e.getCompromiso().getBeneficiario().toString();
                    } else {
                        nombre = "";
                    }

                }
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", e.getFecha() != null ? (sdf.format(e.getFecha())) : ""));
                campos.add(new AuxiliarReporte("String", nombre));
                campos.add(new AuxiliarReporte("Integer", e.getId()));
                campos.add(new AuxiliarReporte("String", e.getCompromiso().toString()));
                campos.add(new AuxiliarReporte("double", traerValorCompromiso(e)));
                campos.add(new AuxiliarReporte("double", traerValorRenglones(e)));
                campos.add(new AuxiliarReporte("double", traerValorDetalles(e)));
                campos.add(new AuxiliarReporte("double", traerValorRenglones(e) - traerValorDetalles(e)));
                xls.agregarFila(campos, false);
            }
            setReporteXls(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCruceFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the cabeceraDocumentos
     */
    public LazyDataModel<Cabdocelect> getCabeceraDocumentos() {
        return cabeceraDocumentos;
    }

    /**
     * @param cabeceraDocumentos the cabeceraDocumentos to set
     */
    public void setCabeceraDocumentos(LazyDataModel<Cabdocelect> cabeceraDocumentos) {
        this.cabeceraDocumentos = cabeceraDocumentos;
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

}
