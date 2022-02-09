/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.talento.sfccbdmq.OrganigramaBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Organigrama;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Organigrama.class)
public class Organigramac implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Organigrama codigo = null;
        try {
            if (value == null) {
                return null;
            }
            OrganigramaBean bean = (OrganigramaBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "organigrama");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Organigramac.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Organigrama codigo=(Organigrama) value;
        if (codigo.getId()==null)
                return "0";
        return ((Organigrama) value).getId().toString();
    }
}