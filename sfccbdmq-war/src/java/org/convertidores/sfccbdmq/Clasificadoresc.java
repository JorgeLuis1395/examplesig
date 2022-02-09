/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.presupuestos.sfccbdmq.ClasificadorBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Clasificadores;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Clasificadores.class)
public class Clasificadoresc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Clasificadores codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ClasificadorBean bean = (ClasificadorBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "clasificadorSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Clasificadoresc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Clasificadores codigo=(Clasificadores) value;
        if (codigo.getId()==null)
                return "0";
        return ((Clasificadores) value).getId().toString();
    }
}