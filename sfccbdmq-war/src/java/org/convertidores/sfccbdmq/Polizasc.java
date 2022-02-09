/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.activos.sfccbdmq.PolizasBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Polizas;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Polizas.class)
public class Polizasc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Polizas codigo = null;
        try {
            if (value == null) {
                return null;
            }
            PolizasBean bean = (PolizasBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "polizasSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Polizasc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Polizas codigo=(Polizas) value;
        if (codigo.getId()==null)
                return "0";
        return ((Polizas) value).getId().toString();
    }
}