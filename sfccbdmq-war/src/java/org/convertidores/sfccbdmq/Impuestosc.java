/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.ImpuestosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Impuestos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Impuestos.class)
public class Impuestosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Impuestos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ImpuestosBean bean = (ImpuestosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "impuestosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Impuestosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Impuestos codigo=(Impuestos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Impuestos) value).getId().toString();
    }
}