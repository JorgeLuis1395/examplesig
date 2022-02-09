/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.talento.sfccbdmq.EmpleadosBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Empleados;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Empleados.class)
public class Empleadosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Empleados codigo = null;
        if (value == null) {
            return null;
        }
        EmpleadosBean bean = (EmpleadosBean)
                context.getELContext().getELResolver().
                        getValue(context.getELContext(), null, "empleados");
        Integer id = Integer.parseInt(value);
        codigo= bean.traer(id);
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Empleados codigo=(Empleados) value;
        if (codigo.getId()==null)
                return "0";
        return ((Empleados) value).getId().toString();
    }
}