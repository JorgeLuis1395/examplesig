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
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.ConsultarException;
import org.personal.sfccbdmq.ViaticosEmpleadoBean;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Viaticosempleado.class)
public class ViaticosEmpleadosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Viaticosempleado codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ViaticosEmpleadoBean bean = (ViaticosEmpleadoBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "viaticosEmpleadoSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Viaticosempleado codigo=(Viaticosempleado) value;
        if (codigo.getId()==null)
                return "0";
        return ((Viaticosempleado) value).getId().toString();
    }
}