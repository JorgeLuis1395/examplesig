/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.ConsultarException;
import org.pagos.sfccbdmq.PagoSaldosBean;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Renglones.class)
public class Renglonesc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Renglones codigo = null;
        try {
            if (value == null) {
                return null;
            }
            PagoSaldosBean bean = (PagoSaldosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "pagosSaldosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Renglonesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Renglones codigo=(Renglones) value;
        if (codigo.getId()==null)
                return "0";
        return ((Renglones) value).getId().toString();
    }
}