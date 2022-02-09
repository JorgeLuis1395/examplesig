/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.presupuestos.sfccbdmq.AsignacionesBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Asignaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Asignaciones.class)
public class Asignacionesc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Asignaciones codigo = null;
        try {
            if (value == null) {
                return null;
            }
            AsignacionesBean bean = (AsignacionesBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "asignacionesSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Asignacionesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Asignaciones codigo=(Asignaciones) value;
        if (codigo.getId()==null)
                return "0";
        return ((Asignaciones) value).getId().toString();
    }
}