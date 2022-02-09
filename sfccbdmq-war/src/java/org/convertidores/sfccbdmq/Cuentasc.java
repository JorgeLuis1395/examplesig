/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.CuentasBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Cuentas;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Cuentas.class)
public class Cuentasc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Cuentas codigo = null;
        try {
            if (value == null) {
                return null;
            }
            CuentasBean bean = (CuentasBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "cuentasSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Cuentasc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Cuentas codigo=(Cuentas) value;
        if (codigo.getId()==null)
                return "0";
        return ((Cuentas) value).getId().toString();
    }
}