/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.BodegasBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Bodegas;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Bodegas.class)
public class Bodegasc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Bodegas codigo = null;
        try {
            if ((value == null) || (value.isEmpty())) {
                return null;
            }
            BodegasBean bean = (BodegasBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "bodegasSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Bodegasc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Bodegas codigo=(Bodegas) value;
        if (codigo.getId()==null)
                return "0";
        return ((Bodegas) value).getId().toString();
    }
}