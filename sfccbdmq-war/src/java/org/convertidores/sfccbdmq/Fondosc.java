/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.contabilidad.sfccbdmq.AperturaFondosBean;
import org.entidades.sfccbdmq.Fondos;

import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Fondos.class)
public class Fondosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Fondos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            AperturaFondosBean bean = (AperturaFondosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "aperturasFondosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Fondosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Fondos codigo=(Fondos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Fondos) value).getId().toString();
    }
}