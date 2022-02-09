/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.compras.sfccbdmq.TiposEgresoBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tipoegreso;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Tipoegreso.class)
public class Tipoegresoc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tipoegreso codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TiposEgresoBean bean = (TiposEgresoBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "tiposEgresoSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Tipoegresoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tipoegreso codigo=(Tipoegreso) value;
        if (codigo.getId()==null)
                return "0";
        return ((Tipoegreso) value).getId().toString();
    }
}