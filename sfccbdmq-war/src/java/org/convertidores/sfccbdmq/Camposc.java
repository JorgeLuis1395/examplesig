/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.CamposBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Campos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Campos.class)
public class Camposc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Campos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            CamposBean bean = (CamposBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "camposSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Camposc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Campos codigo=(Campos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Campos) value).getId().toString();
    }
}