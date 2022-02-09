/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.TipoAsientoBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Tipoasiento.class)
public class Tipoasientoc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tipoasiento codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TipoAsientoBean bean = (TipoAsientoBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "tipoAsientoSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Tipoasientoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tipoasiento codigo=(Tipoasiento) value;
        if (codigo.getId()==null)
                return "0";
        return ((Tipoasiento) value).getId().toString();
    }
}