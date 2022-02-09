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
import org.activos.sfccbdmq.ActivosBean;
import org.entidades.sfccbdmq.Activos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Activos.class)
public class Activosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Activos codigo = null;
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            ActivosBean bean = (ActivosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "activosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Activosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Activos codigo=(Activos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Activos) value).getId().toString();
    }
}