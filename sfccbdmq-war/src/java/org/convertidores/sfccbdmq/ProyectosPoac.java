/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.entidades.sfccbdmq.Proyectospoa;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.errores.sfccbdmq.ConsultarException;
import org.pacpoa.sfccbdmq.ProyectosPoaBean;

/**
 *
 * @author edison
 */
@FacesConverter(forClass = Proyectospoa.class)
public class ProyectosPoac implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Proyectospoa codigo = null;//ENTIDAD
        try {
            ProyectosPoaBean bean = (ProyectosPoaBean) context.getELContext().getELResolver().getValue(context.getELContext(), 
                    null, "proyectosPoa");
            Integer id = Integer.parseInt(value);
            codigo = bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger("Convertidor").log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Proyectospoa codigo = (Proyectospoa) value;//ENTIDAD
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }
}
