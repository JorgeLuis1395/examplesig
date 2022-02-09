/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.talento.sfccbdmq.ConceptosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Conceptos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Conceptos.class)
public class Conceptosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Conceptos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ConceptosBean bean = (ConceptosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "conceptosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Conceptosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Conceptos codigo=(Conceptos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Conceptos) value).getId().toString();
    }
}