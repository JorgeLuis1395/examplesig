/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.activos.sfccbdmq.EdificiosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Edificios;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Edificios.class)
public class Edificiosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Edificios codigo = null;
        try {
            if (value == null) {
                return null;
            }
            EdificiosBean bean = (EdificiosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "edificiosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Edificiosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Edificios codigo=(Edificios) value;
        if (codigo.getId()==null)
                return "0";
        return ((Edificios) value).getId().toString();
    }
}