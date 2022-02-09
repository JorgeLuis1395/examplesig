/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.text.DecimalFormat;
import java.util.Calendar;
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
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ContratosFacade;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "modificacionesSfccbdmq")
@ViewScoped
public class ModificacionBean {

    /**
     * Creates a new instance of ContratosBean
     */
    public ModificacionBean() {
    }
    private Contratos modificacion;
    private Contratos contrato;
    private List<Contratos> contratos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    private Boolean tieneAnticipo;
    @EJB
    private ContratosFacade ejbContratos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    /**
     * @return the modificacion
     */
    public Contratos getModificacion() {
        return modificacion;
    }

    /**
     * @param modificacion the modificacion to set
     */
    public void setModificacion(Contratos modificacion) {
        this.modificacion = modificacion;
    }

    /**
     * @return the contratos
     */
    public List<Contratos> getContratos() {
        return contratos;
    }

    /**
     * @param contratos the contratos to set
     */
    public void setContratos(List<Contratos> contratos) {
        this.contratos = contratos;
    }

    public String buscar() {
        if (contrato == null) {
//            contratos = null;
            MensajesErrores.advertencia("Seleccione un contrato  primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:contrato");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.fin desc");
        try {
            contratos = ejbContratos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ModificacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (contrato == null) {

            MensajesErrores.advertencia("Seleccione un contrato primero");
            return null;
        }
        formulario.insertar();
        modificacion = new Contratos();
        modificacion.setPadre(contrato);
        modificacion.setProveedor(contrato.getProveedor());
        modificacion.setAdministrador(contrato.getAdministrador());
        modificacion.setTipocontrato(contrato.getTipocontrato());
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("MODIFICATORIOS");
        imagenesBean.Buscar();
        return null;
    }

    public String modificar(Contratos modificacion) {

        this.modificacion = modificacion;
        contrato = modificacion.getPadre();
        modificacion.setTipocontrato(contrato.getTipocontrato());
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("MODIFICATORIOS");
        imagenesBean.Buscar();
        Calendar fAnticipo = Calendar.getInstance();
        Calendar fFirma = Calendar.getInstance();
        Calendar fFin = Calendar.getInstance();

        fFirma.setTime(modificacion.getFirma());
        if (modificacion.getFechaanticipo() != null) {
            fAnticipo.setTime(modificacion.getFechaanticipo());
        }
        fFin.setTime(modificacion.getFin());
        if (modificacion.getEsfirma()) {
//            fAnticipo.setTime(contrato.getFirma());
            int dias = (int) ((fFin.getTimeInMillis() - fFirma.getTimeInMillis()) / 86400000) - 1;
            modificacion.setNumeroDias(dias);
        } else {
            int dias = (int) ((fFin.getTimeInMillis() - fAnticipo.getTimeInMillis()) / 86400000) - 1;
            modificacion.setNumeroDias(dias);
        }
        formulario.editar();

        return null;
    }

    public String eliminar(Contratos modificacion) {
        this.modificacion = modificacion;
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

//    private boolean validar() {
//
//        if ((modificacion.getObjeto() == null) || (modificacion.getObjeto().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario objeto de modificacion");
//            return true;
//        }
//        if (!getMostrarInicio()) {
//            if ((modificacion.getFecha() == null)) {
//                MensajesErrores.advertencia("Es necesario Fecha de inicio");
//                return true;
//            }
//            if ((modificacion.getFecha().before(configuracionBean.getConfiguracion().getPvigente()))) {
//                MensajesErrores.advertencia("Es necesario Fecha de inicio mayor  a periodo vigente");
//                return true;
//            }
//        }
//        if (!getMostrarFin()) {
//            if ((modificacion.getFechafin() == null)) {
//                MensajesErrores.advertencia("Es necesario Fecha de fin");
//                return true;
//            }
//            if ((modificacion.getFecha().after(modificacion.getFechafin()))) {
//                MensajesErrores.advertencia("Fecha de inicio de de ser antes de fecha de fin");
//                return true;
//            }
//        }
//        if (getMostrarMonto()) {
//            if (modificacion.getPadre().getValor().doubleValue() <= modificacion.getValor().doubleValue()) {
//                MensajesErrores.advertencia("Monto sobrepasa el valor del contrato");
//                return true;
//            }
//        }
//        if (getMostrarNumero()) {
//            if ((modificacion.getNumero()== null) || (modificacion.getNumero().isEmpty())) {
//                MensajesErrores.advertencia("Es necesario Número de modificacion");
//                return true;
//            }
//        } else {
//            modificacion.setNumero(contrato.getNumero());
//        }
//        return false;
//    }
    private boolean validar() {
//        if (getMostrarFin()) {
//            modificacion.setFin(contrato.getFin());
//        }
        try {
            if (getEsRubro()) {
                DecimalFormat df = new DecimalFormat("0000");
                // traer el numero que corresponde 
                Map parametros = new HashMap();
                parametros.put(";where", "o.padre=:padre");
                parametros.put("padre", contrato);
                int total = ejbContratos.contar(parametros);
                modificacion.setNumero(contrato.getNumero() + "-" + df.format(total));
                modificacion.setInicio(contrato.getInicio());
                modificacion.setFin(contrato.getFin());
            } else {
                if ((modificacion.getNumero() == null) || (modificacion.getNumero().isEmpty())) {
                    MensajesErrores.advertencia("Es necesario número de contrato");
                    return true;
                }

            }
            if (getEsAdendum()) {

            }
            if ((modificacion.getObjeto() == null) || (modificacion.getObjeto().isEmpty())) {
                MensajesErrores.advertencia("Es necesario objeto de contrato");
                return true;
            }
            if ((modificacion.getTitulo() == null) || (modificacion.getTitulo().isEmpty())) {
                MensajesErrores.advertencia("Es necesario objeto de contrato");
                return true;
            }
            if ((modificacion.getAdministrador() == null)) {
                MensajesErrores.advertencia("Es necesario administrador de contrato");
                return true;
            }
            if (!getMostrarInicio()) {
                if ((modificacion.getInicio() == null)) {
                    MensajesErrores.advertencia("Es necesario Fecha de inicio");
                    return true;
                }
                if ((modificacion.getInicio().before(configuracionBean.getConfiguracion().getPvigente()))) {
                    MensajesErrores.advertencia("Es necesario Fecha de inicio mayor que periodo vigente");
                    return true;
                }
//            if ((modificacion.getInicio() == null)) {
//                MensajesErrores.advertencia("Es necesario Fecha de inicio");
//                return true;
//            }
            }
//        if ((contrato.getFin() == null)) {
//            MensajesErrores.advertencia("Es necesario Fecha de fin");
//            return true;
//        }
            if ((modificacion.getFirma() == null)) {
                MensajesErrores.advertencia("Es necesario fecha de firma");
                return true;
            }

            if (!getEsRubro()) {
                if ((modificacion.getNumeroDias() == null) || (modificacion.getNumeroDias() <= 0)) {
                    MensajesErrores.advertencia("Es necesario Número de días de contrato");
                    return true;
                }
                Integer valor = modificacion.getNumeroDias();
                Calendar c = Calendar.getInstance();
                if (tieneAnticipo) {
                    if (modificacion.getEsfirma()) {
                        c.setTime(modificacion.getFirma());
                        c.add(Calendar.DATE, valor + 1);
                        modificacion.setFin(c.getTime());
                    } else {
                        c.setTime(modificacion.getFechaanticipo());
                        c.add(Calendar.DATE, valor + 1);
                        modificacion.setFin(c.getTime());
                    }
                } else {
                    c.setTime(modificacion.getFirma());
                    c.add(Calendar.DATE, valor + 1);
                    modificacion.setFin(c.getTime());
                }
                if ((modificacion.getInicio().after(modificacion.getFin()))) {
                    MensajesErrores.advertencia("Fecha de inicio de de ser antes de fecha de fin");
                    return true;
                }
            }

//        if ((contrato.getInicio().after(contrato.getFirma()))) {
//            MensajesErrores.advertencia("Fecha de inicio de de ser antes de fecha de firma");
//            return true;
//        }
//        if ((contrato.getFirma().after(contrato.getFin()))) {
//            MensajesErrores.advertencia("Fecha de firma de de ser antes de fecha de fin");
//            return true;
//        }
//        if ((contrato.getCertificacion()==null) ){
//            MensajesErrores.advertencia("Es necesario certificación de contrato");
//            return true;
//        }
            if ((modificacion.getAdministrador() == null)) {
                MensajesErrores.advertencia("Es necesario administrador de contrato");
                return true;
            }

            if (!getEsAdendum()) {
                if (modificacion.getPadre().getValor().doubleValue() <= modificacion.getValor().doubleValue()) {
                    MensajesErrores.advertencia("Monto sobrepasa el valor del contrato");
                    return true;
                }

                if (tieneAnticipo) {
                    if ((modificacion.getAnticipo() == null)) {
                        MensajesErrores.advertencia("Neceario fecha de anticipo");
                        return true;
                    }
                    if (modificacion.getValor().doubleValue() <= modificacion.getAnticipo().doubleValue()) {
                        MensajesErrores.advertencia("Valor debe ser mayor al valor del anticipo");
                        return true;
                    }
                }
            }
            // numero de be seer unico
            if (formulario.isNuevo()) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.numero=:numero");
                parametros.put("numero", modificacion.getNumero());

                int total = ejbContratos.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Número de contrato ya utilizado");
                    return true;
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
//            if (modificacion.getNumero() == null) {
//                modificacion.setNumero(contrato.getNumero() + "-" + modificacion.getTipo().getCodigo());
//            }
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            modificacion.setEstado(2);
            modificacion.setDireccion(modificacion.getPadre().getDireccion());
            modificacion.setProceso(modificacion.getPadre().getProceso());
            ejbContratos.create(modificacion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ModificacionBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbContratos.edit(modificacion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ModificacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbContratos.remove(modificacion, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ModificacionBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Contratos traer(Integer id) throws ConsultarException {
        return ejbContratos.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ModificacionesVista";
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
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
    }

    public boolean getEsRubro() {
        if (modificacion == null) {
            return false;
        }
        if (modificacion.getTipo() == null) {
            return false;
        }
        String nombre = modificacion.getTipo().getNombre().toLowerCase();
        return nombre.startsWith("rubros");
    }

    public boolean getMostrarInicio() {
        if (modificacion == null) {
            return false;
        }
        if (modificacion.getTipo() == null) {
            return false;
        }
        String nombre = modificacion.getTipo().getNombre().toLowerCase();
        return nombre.startsWith("rubros");
    }

    public boolean getMostrarFin() {
        if (modificacion == null) {
            return false;
        }
        if (modificacion.getTipo() == null) {
            return false;
        }
        String nombre = modificacion.getTipo().getNombre().toLowerCase();
        return nombre.startsWith("rubros");
    }

    public boolean getEsAdendum() {
        if (modificacion == null) {
            return false;
        }
        if (modificacion.getTipo() == null) {
            return false;
        }
        String nombre = modificacion.getTipo().getNombre().toLowerCase();
        return nombre.startsWith("ademdum");
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
     * @return the tieneAnticipo
     */
    public Boolean getTieneAnticipo() {
        return tieneAnticipo;
    }

    /**
     * @param tieneAnticipo the tieneAnticipo to set
     */
    public void setTieneAnticipo(Boolean tieneAnticipo) {
        this.tieneAnticipo = tieneAnticipo;
    }

    public void cambiaDias(ValueChangeEvent ve) {
        if (modificacion.getFirma() != null) {
            Integer valor = (Integer) ve.getNewValue();
            Calendar c = Calendar.getInstance();
            if (tieneAnticipo) {
                if (modificacion.getEsfirma()) {
                    c.setTime(modificacion.getFirma());
                    c.add(Calendar.DATE, valor + 1);
                    modificacion.setFin(c.getTime());
                } else {
                    c.setTime(modificacion.getFechaanticipo());
                    c.add(Calendar.DATE, valor + 1);
                    modificacion.setFin(c.getTime());
                }
            } else {
                c.setTime(modificacion.getFirma());
                c.add(Calendar.DATE, valor + 1);
                modificacion.setFin(c.getTime());
            }
        }
    }
}
