/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.activos.sfccbdmq.TiposActivosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tipoactivo;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Tipoactivo.class)
public class TiposActivosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tipoactivo codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TiposActivosBean bean = (TiposActivosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "tiposActivosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(TiposActivosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tipoactivo codigo=(Tipoactivo) value;
        if (codigo.getId()==null)
                return "0";
        return ((Tipoactivo) value).getId().toString();
    }
}