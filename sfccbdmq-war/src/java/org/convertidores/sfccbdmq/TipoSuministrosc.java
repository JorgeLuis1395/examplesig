/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.inventarios.sfccbdmq.TiposSuministrosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Tiposuministros.class)
public class TipoSuministrosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tiposuministros codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TiposSuministrosBean bean = (TiposSuministrosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "tiposSuministrosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(TipoSuministrosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tiposuministros codigo=(Tiposuministros) value;
        if (codigo.getId()==null)
                return "0";
        return ((Tiposuministros) value).getId().toString();
    }
}