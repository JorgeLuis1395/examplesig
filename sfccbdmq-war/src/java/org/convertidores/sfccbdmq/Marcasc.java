/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.activos.sfccbdmq.MarcasBean;
import org.entidades.sfccbdmq.Marcas;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author luisfernando
 */
@FacesConverter(forClass = Marcas.class)
public class Marcasc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Marcas codigo = null;
        try {
            
            MarcasBean bean = (MarcasBean) context.getELContext().getELResolver().getValue(context.getELContext(), null, "marcasActivos");
            Integer id = Integer.parseInt(value);
            codigo = bean.traerc(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Marcasc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Marcas codigo = (Marcas) value;
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }

}
