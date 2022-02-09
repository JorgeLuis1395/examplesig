/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.presupuestos.sfccbdmq.ProyectosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Proyectos.class)
public class Proyectosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Proyectos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ProyectosBean bean = (ProyectosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "proyectosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Proyectosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Proyectos codigo=(Proyectos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Proyectos) value).getId().toString();
    }
}