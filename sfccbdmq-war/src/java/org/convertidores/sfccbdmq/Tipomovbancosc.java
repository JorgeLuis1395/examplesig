/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.pagos.sfccbdmq.TipoMovBancosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tipomovbancos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Tipomovbancos.class)
public class Tipomovbancosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tipomovbancos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TipoMovBancosBean bean = (TipoMovBancosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "tipoMovBancosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Tipomovbancosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tipomovbancos codigo=(Tipomovbancos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Tipomovbancos) value).getId().toString();
    }
}