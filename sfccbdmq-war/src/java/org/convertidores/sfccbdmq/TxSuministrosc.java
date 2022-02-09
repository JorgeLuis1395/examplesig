/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.inventarios.sfccbdmq.TxInventariosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Txinventarios.class)
public class TxSuministrosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Txinventarios codigo = null;
        try {
            if (value == null) {
                return null;
            }
            TxInventariosBean bean = (TxInventariosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "txSuministrosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(TxSuministrosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Txinventarios codigo=(Txinventarios) value;
        if (codigo.getId()==null)
                return "0";
        return ((Txinventarios) value).getId().toString();
    }
}