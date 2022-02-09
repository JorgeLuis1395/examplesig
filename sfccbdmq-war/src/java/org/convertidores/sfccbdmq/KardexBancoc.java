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
import org.entidades.sfccbdmq.Kardexbanco;
import org.errores.sfccbdmq.ConsultarException;
import org.pagos.sfccbdmq.KardexBean;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Kardexbanco.class)
public class KardexBancoc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Kardexbanco codigo = null;
        try {
            if ((value == null) || (value.isEmpty())) {
                return null;
            }
            KardexBean bean = (KardexBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "kardexBancosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(KardexBancoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Kardexbanco codigo=(Kardexbanco) value;
        if (codigo.getId()==null)
                return "0";
        return ((Kardexbanco) value).getId().toString();
    }
}