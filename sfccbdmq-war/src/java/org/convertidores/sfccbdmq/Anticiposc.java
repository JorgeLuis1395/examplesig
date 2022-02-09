/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.pagos.sfccbdmq.AnticiposProveedoresBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Anticipos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Anticipos.class)
public class Anticiposc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Anticipos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            AnticiposProveedoresBean bean = (AnticiposProveedoresBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "anticiposProveedoresSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Anticiposc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Anticipos codigo=(Anticipos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Anticipos) value).getId().toString();
    }
}