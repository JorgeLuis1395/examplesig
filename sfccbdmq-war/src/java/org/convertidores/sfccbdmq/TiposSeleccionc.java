/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.talento.sfccbdmq.TiposSeleccionesBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tiposseleccion;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edison
 */
@FacesConverter(forClass = Tiposseleccion.class)
public class TiposSeleccionc implements Converter {
     @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tiposseleccion codigo = null;
        try {
            TiposSeleccionesBean bean = (TiposSeleccionesBean) context.getELContext().getELResolver().getValue(context.getELContext(), null, "tiposSelecciones");
            Integer id = Integer.parseInt(value);
            codigo = bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(TiposSeleccionc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tiposseleccion codigo = (Tiposseleccion) value;
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }
}