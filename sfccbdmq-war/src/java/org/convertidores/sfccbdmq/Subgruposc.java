/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.activos.sfccbdmq.SubGruposBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Subgruposactivos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Subgruposactivos.class)
public class Subgruposc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Subgruposactivos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            SubGruposBean bean = (SubGruposBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "subGruposActivosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Subgruposc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Subgruposactivos codigo=(Subgruposactivos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Subgruposactivos) value).getId().toString();
    }
}