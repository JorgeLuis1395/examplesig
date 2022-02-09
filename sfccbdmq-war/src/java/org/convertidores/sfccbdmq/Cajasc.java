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
import org.contabilidad.sfccbdmq.AperturaCajasBean;
import org.entidades.sfccbdmq.Cajas;

import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Cajas.class)
public class Cajasc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Cajas codigo = null;
        try {
            if (value == null) {
                return null;
            }
            AperturaCajasBean bean = (AperturaCajasBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "aperturasCajasSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Cajasc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Cajas codigo=(Cajas) value;
        if (codigo.getId()==null)
                return "0";
        return ((Cajas) value).getId().toString();
    }
}