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
import org.entidades.sfccbdmq.Viaticos;
import org.errores.sfccbdmq.ConsultarException;
import org.presupuestos.sfccbdmq.ViaticosBean;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Viaticos.class)
public class Viaticosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Viaticos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ViaticosBean bean = (ViaticosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "viaticosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Viaticosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Viaticos codigo=(Viaticos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Viaticos) value).getId().toString();
    }
}