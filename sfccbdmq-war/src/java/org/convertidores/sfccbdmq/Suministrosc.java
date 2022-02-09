/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.inventarios.sfccbdmq.SuministrosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Suministros;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Suministros.class)
public class Suministrosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Suministros codigo = null;
        try {
            if (value == null) {
                return null;
            }
            SuministrosBean bean = (SuministrosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "suministrosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Suministrosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Suministros codigo=(Suministros) value;
        if (codigo.getId()==null)
                return "0";
        return ((Suministros) value).getId().toString();
    }
}