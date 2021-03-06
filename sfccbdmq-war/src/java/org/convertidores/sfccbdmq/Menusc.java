/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.seguridad.sfccbdmq.MantenimientoMenusBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Menusistema;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Menusistema.class)
public class Menusc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Menusistema codigo = null;
        try {
            if (value == null) {
                return null;
            }
            MantenimientoMenusBean bean = (MantenimientoMenusBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "mantenimientoMenus");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Menusc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Menusistema codigo=(Menusistema) value;
        if (codigo.getId()==null)
                return "0";
        return ((Menusistema) value).getId().toString();
    }
}