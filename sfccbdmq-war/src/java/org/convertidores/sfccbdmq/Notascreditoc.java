/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.compras.sfccbdmq.NotasCreditoBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Notascredito;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Notascredito.class)
public class Notascreditoc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Notascredito codigo = null;
        try {
            if (value == null) {
                return null;
            }
            NotasCreditoBean bean = (NotasCreditoBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "notasCreditoSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Notascreditoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Notascredito codigo=(Notascredito) value;
        if (codigo.getId()==null)
                return "0";
        return ((Notascredito) value).getId().toString();
    }
}