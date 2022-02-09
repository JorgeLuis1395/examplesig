/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.PuntoEmisionBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Puntoemision;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Puntoemision.class)
public class Puntosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Puntoemision codigo = null;
        try {
            if (value == null) {
                return null;
            }
            PuntoEmisionBean bean = (PuntoEmisionBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "puntosemisionSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Puntosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Puntoemision codigo=(Puntoemision) value;
        if (codigo.getId()==null)
                return "0";
        return ((Puntoemision) value).getId().toString();
    }
}