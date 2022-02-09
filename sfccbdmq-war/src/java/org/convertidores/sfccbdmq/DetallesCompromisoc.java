/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.presupuestos.sfccbdmq.CompromisosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Detallecompromiso.class)
public class DetallesCompromisoc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Detallecompromiso codigo = null;
        try {
            if (value == null) {
                return null;
            }
            CompromisosBean bean = (CompromisosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "compromisosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(DetallesCompromisoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Detallecompromiso codigo=(Detallecompromiso) value;
        if (codigo.getId()==null)
                return "0";
        return ((Detallecompromiso) value).getId().toString();
    }
}