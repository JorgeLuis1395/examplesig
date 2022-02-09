/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.presupuestos.sfccbdmq.CertificacionesBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Detallecertificaciones.class)
public class Detallecertificacionesc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Detallecertificaciones codigo = null;
        try {
            if (value == null) {
                return null;
            }
            CertificacionesBean bean = (CertificacionesBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "certificacionesSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Detallecertificacionesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Detallecertificaciones codigo=(Detallecertificaciones) value;
        if (codigo.getId()==null)
                return "0";
        return ((Detallecertificaciones) value).getId().toString();
    }
}