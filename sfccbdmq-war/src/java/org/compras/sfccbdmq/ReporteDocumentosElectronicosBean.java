/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.util.Calendar;
import java.util.Date;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.util.HashMap;
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
import javax.xml.bind.JAXBException;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteDocElectSfccbdmq")
@ViewScoped
public class ReporteDocumentosElectronicosBean {

    /**
     * Creates a new instance of TipoajusteBean
     */
    public ReporteDocumentosElectronicosBean() {
        listadoDocumentos = new LazyDataModel<Documentoselectronicos>() {

            @Override
            public List<Documentoselectronicos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, pageSize, scs, map);
            }
        };
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{facturaSri}")
    private FacturaSriBean facturaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @EJB
    private DocumentoselectronicosFacade ejbDocumentos;
    private LazyDataModel<Documentoselectronicos> listadoDocumentos;
    private Perfiles perfil;
    private Formulario formulario=new Formulario();
    private Date desde;
    private Date hasta;

    /**
     * @return the listadoDocumentos
     */
    public LazyDataModel<Documentoselectronicos> getListadoDocumentos() {
        return listadoDocumentos;
    }

    /**
     * @param listadoDocumentos the listadoDocumentos to set
     */
    public void setListadoDocumentos(LazyDataModel<Documentoselectronicos> listadoDocumentos) {
        this.listadoDocumentos = listadoDocumentos;
    }

    public List<Documentoselectronicos> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fechaemision asc");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }

        String where = "o.tipo=0 and o.fechaemision between  :desde and :hasta ";
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();
            if (clave.equalsIgnoreCase("fechaemision")) {
                if (valor != null) {
                    if (valor.length() == 10) {
                        where += " and o." + clave + "=:" + clave;
                        String[] aux = ((String) e.getValue()).split("/");
                        Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(aux[2]), Integer.parseInt(aux[1]), Integer.parseInt(aux[0]));
//                Date valor = (Date) e.getValue();
                        parametros.put(clave, cal.getTime());
                    }
                }
            } else {

                where += " and upper(o." + clave + ") like :" + clave;
                parametros.put(clave, valor.toUpperCase() + "%");
            }
        }

        int total = 0;
        try {
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            total = ejbDocumentos.contar(parametros);
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
        listadoDocumentos.setRowCount(total);
        try {
            return ejbDocumentos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @PostConstruct
    private void activar() {
        desde=configuracionBean.getConfiguracion().getPinicial();
        hasta=configuracionBean.getConfiguracion().getPfinal();
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
        String nombreForma = "ReporteElectronicasVista";
        if (perfilInterno == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfilInterno));
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
    public String ver(Documentoselectronicos doc){
        try {

            getFacturaBean().cargarMarshal(facturaBean.grabarXml("PRUEBA", doc.getXml()));

        } catch (JAXBException ex) {
            getFacturaBean().cargar(doc.getXml());
//            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    /**
     * @return the facturaBean
     */
    public FacturaSriBean getFacturaBean() {
        return facturaBean;
    }

    /**
     * @param facturaBean the facturaBean to set
     */
    public void setFacturaBean(FacturaSriBean facturaBean) {
        this.facturaBean = facturaBean;
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
    public String buscar(){
        listadoDocumentos = new LazyDataModel<Documentoselectronicos>() {

            @Override
            public List<Documentoselectronicos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, pageSize, scs, map);
            }
        };
        return null;
    }
}
