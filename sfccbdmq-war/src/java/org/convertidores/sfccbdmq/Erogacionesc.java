/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.activos.sfccbdmq.ErogacionesBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Erogaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Erogaciones.class)
public class Erogacionesc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Erogaciones codigo = null;
        try {
            if ((value == null) || (value.isEmpty())) {
                return null;
            }
            ErogacionesBean bean = (ErogacionesBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "erogacionesSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Erogacionesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Erogaciones codigo=(Erogaciones) value;
        if (codigo.getId()==null)
                return "0";
        return ((Erogaciones) value).getId().toString();
    }
}