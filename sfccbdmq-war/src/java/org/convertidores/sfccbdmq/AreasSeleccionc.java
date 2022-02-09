/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.talento.sfccbdmq.AreasSeleccionBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Areasseleccion;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edison
 */
@FacesConverter(forClass = Areasseleccion.class)
public class AreasSeleccionc implements Converter {
     @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Areasseleccion codigo = null;
        try {
            AreasSeleccionBean bean = 
                    (AreasSeleccionBean) context.getELContext().getELResolver().getValue(context.getELContext(), null, "areasSeleccion");
            Integer id = Integer.parseInt(value);
            codigo = bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(AreasSeleccionc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Areasseleccion codigo = (Areasseleccion) value;
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }
}