/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.TipoAjusteBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tipoajuste;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Tipoajuste.class)
public class Tipoajustec implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tipoajuste codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TipoAjusteBean bean = (TipoAjusteBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "tipoAjusteSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Tipoajustec.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tipoajuste codigo=(Tipoajuste) value;
        if (codigo.getId()==null)
                return "0";
        return ((Tipoajuste) value).getId().toString();
    }
}