/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.entidades.sfccbdmq.Asignacionespoa;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.errores.sfccbdmq.ConsultarException;
import org.pacpoa.sfccbdmq.AsignacionesPoaBean;

/**
 *
 * @author edison
 */
@FacesConverter(forClass = Asignacionespoa.class)
public class AsignacionesPoac implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Asignacionespoa codigo = null;//ENTIDAD
        try {
            AsignacionesPoaBean bean = (AsignacionesPoaBean) 
                    context.getELContext().getELResolver().getValue(context.getELContext(), null, "asignacionesPoa");
            Integer id = Integer.parseInt(value);
            codigo = bean.traer(id);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("Convertidor").log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Asignacionespoa codigo = (Asignacionespoa) value;//ENTIDAD
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }
}
