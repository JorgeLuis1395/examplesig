/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author edwin
 */

public class MensajesErrores implements Serializable {

    private static final long serialVersionUID = 1L;
   
    private  boolean si;

    public static void mensaje(String clase, String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        message.setSummary(mensaje);
        message.setDetail(mensaje);
        context.addMessage(clase, message);
        

    }
     public static void error( String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        message.setSummary(mensaje);
        message.setDetail(mensaje);
        context.addMessage("ERROR", message);

    }
    public static void informacion( String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary(mensaje);
        message.setDetail(mensaje);
        context.addMessage("ADVERTENCIA", message);

    } 
    public static void fatal( String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_FATAL);
        message.setSummary(mensaje);
        message.setDetail(mensaje);
        context.addMessage("ADVERTENCIA", message);

    }
    public static void advertencia( String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_WARN);
        message.setSummary(mensaje);
        message.setDetail(mensaje);
        context.addMessage("ADVERTENCIA", message);

    }

    public String cancelar() {
       
        return null;
    }
    public void cancelarAccion(ActionEvent event){
       
       si=false;
    }

    /**
     * @return the mostrar
     */
    private FacesContext getCurrentContext() {
        return FacesContext.getCurrentInstance();
    }

    public boolean isMostrar() {
        
        return !getCurrentContext().getMessageList().isEmpty();
    }

   
    public MensajesErrores() {
    }

    /**
     * @return the si
     */
    public boolean isSi() {
        return si;
    }

    /**
     * @param si the si to set
     */
    public void setSi(boolean si) {
        this.si = si;
    }

    
}
