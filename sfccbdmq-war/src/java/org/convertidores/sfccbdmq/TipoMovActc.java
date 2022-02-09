/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.activos.sfccbdmq.TipoMovActBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tipomovactivos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Tipomovactivos.class)
public class TipoMovActc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tipomovactivos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TipoMovActBean bean = (TipoMovActBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "tipoMovActivosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(TipoMovActc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tipomovactivos codigo=(Tipomovactivos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Tipomovactivos) value).getId().toString();
    }
}