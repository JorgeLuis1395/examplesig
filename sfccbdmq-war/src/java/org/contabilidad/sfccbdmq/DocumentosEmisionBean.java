/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.DocumentosFacade;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "documentosEmisionSfccbdmq")
@ViewScoped
public class DocumentosEmisionBean {

    /**
     * Creates a new instance of DocumentosBean
     */
    public DocumentosEmisionBean() {
    }
    private Documentos documento;
    private Puntoemision punto;
    private List<Documentos> documentos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private DocumentosFacade ejbDocumentos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the documento
     */
    public Documentos getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(Documentos documento) {
        this.documento = documento;
    }

    /**
     * @return the documentos
     */
    public List<Documentos> getDocumentos() {
        return documentos;
    }

    /**
     * @param documentos the documentos to set
     */
    public void setDocumentos(List<Documentos> documentos) {
        this.documentos = documentos;
    }

    public String buscar() {
        if (punto == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.final1 desc");
        parametros.put(";where", "o.punto=:punto");
        parametros.put("punto", punto);
        try {
            documentos = ejbDocumentos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (punto == null) {
            MensajesErrores.advertencia("Seleccione una punto primero");
            return null;
        }
        formulario.insertar();
        
        documento = new Documentos();
        documento.setPunto(punto);
        
        return null;
    }

    public String modificar(Documentos documento) {

        this.documento = documento;
        formulario.editar();

        return null;
    }

    public String eliminar(Documentos documento) {

        this.documento = documento;
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

    private boolean validar() {

        if ((documento.getAutorizacion() == null) || (documento.getAutorizacion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Autorización de documento");
            return true;
        }
        if ((documento.getDesde() == null)) {
            MensajesErrores.advertencia("Es necesario fecha desde ");
            return true;
        }
        if ((documento.getHasta() == null)) {
            MensajesErrores.advertencia("Es necesario fecha hasta");
            return true;
        }
        if ((documento.getDesde().after(documento.getHasta()))) {
            MensajesErrores.advertencia("Debe ser desde mayor a hasta");
            return true;
        }
        if ((documento.getInicial() == null) || (documento.getInicial() <= 0)) {
            MensajesErrores.advertencia("Es necesario  documento inicial");
            return true;
        }
        if ((documento.getFinal1() == null) || (documento.getFinal1() <= 0)) {
            MensajesErrores.advertencia("Es necesario  documento final");
            return true;
        }
        if ((documento.getFinal1() <= documento.getInicial())) {
            MensajesErrores.advertencia("Documento final debe ser mayor a documento inicail");
            return true;
        }
//        if ((documento.getRelacioncobranza()== null) || (documento.getRelacioncobranza()<=0)) {
//            MensajesErrores.advertencia("Es necesario Relación de cobranza válido en documento");
//            return true;
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            documento.setNumeroactual(documento.getInicial());
            ejbDocumentos.create(documento, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
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
            if (documento.getNumeroactual() == null) {
                documento.setNumeroactual(documento.getInicial());
            } else if (documento.getNumeroactual()<documento.getInicial()){
                documento.setNumeroactual(documento.getInicial());
            }
            ejbDocumentos.edit(documento, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbDocumentos.remove(documento, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Documentos traer(Integer id) throws ConsultarException {
        return ejbDocumentos.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "DocumentosVista";
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

    public SelectItem[] getComboDocumentos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        try {
            return Combos.getSelectItems(ejbDocumentos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the punto
     */
    public Puntoemision getPunto() {
        return punto;
    }

    /**
     * @param punto the punto to set
     */
    public void setPunto(Puntoemision punto) {
        this.punto = punto;
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
}
