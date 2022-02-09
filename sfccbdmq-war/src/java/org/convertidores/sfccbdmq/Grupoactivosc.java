/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.activos.sfccbdmq.GrupoActivosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Grupoactivos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Grupoactivos.class)
public class Grupoactivosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Grupoactivos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            GrupoActivosBean bean = (GrupoActivosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "gruposActivosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Grupoactivosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Grupoactivos codigo=(Grupoactivos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Grupoactivos) value).getId().toString();
    }
}