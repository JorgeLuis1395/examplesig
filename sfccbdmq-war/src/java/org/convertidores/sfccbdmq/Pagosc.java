/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.pagos.sfccbdmq.KardexPagosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Pagosvencimientos.class)
public class Pagosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Pagosvencimientos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            KardexPagosBean bean = (KardexPagosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "kardexPagosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Pagosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Pagosvencimientos codigo=(Pagosvencimientos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Pagosvencimientos) value).getId().toString();
    }
}