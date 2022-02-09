/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.talento.sfccbdmq.CargosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Cargos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Cargos.class)
public class Cargosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Cargos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            CargosBean bean = (CargosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "cargos");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Cargosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Cargos codigo=(Cargos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Cargos) value).getId().toString();
    }
}