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
import org.compras.sfccbdmq.InformesBean;
import org.entidades.sfccbdmq.Informes;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Informes.class)
public class Informesc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Informes codigo = null;
        try {
            if (value == null) {
                return null;
            }
            InformesBean bean = (InformesBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "informesSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Informesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Informes codigo=(Informes) value;
        if (codigo.getId()==null)
                return "0";
        return ((Informes) value).getId().toString();
    }
}