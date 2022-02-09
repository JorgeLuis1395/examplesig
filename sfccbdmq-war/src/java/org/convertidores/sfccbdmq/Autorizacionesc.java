/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.compras.sfccbdmq.AutorizacionesBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Autorizaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Autorizaciones.class)
public class Autorizacionesc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Autorizaciones codigo = null;
        try {
            if (value == null) {
                return null;
            }
            AutorizacionesBean bean = (AutorizacionesBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "autorizacionesSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Autorizacionesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Autorizaciones codigo=(Autorizaciones) value;
        if (codigo.getId()==null)
                return "0";
        return ((Autorizaciones) value).getId().toString();
    }
}