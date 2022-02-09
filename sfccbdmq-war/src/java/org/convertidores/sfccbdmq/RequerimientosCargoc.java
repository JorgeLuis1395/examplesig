/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.talento.sfccbdmq.RequerimientosCargoBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Requerimientoscargo;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edison
 */
@FacesConverter(forClass = Requerimientoscargo.class)
public class RequerimientosCargoc implements Converter {
     @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Requerimientoscargo codigo = null;
        try {
            RequerimientosCargoBean bean = (RequerimientosCargoBean) 
                    context.getELContext().getELResolver().getValue(context.getELContext(), null, "requerimientosCargo");
            Integer id = Integer.parseInt(value);
            codigo = bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(RequerimientosCargoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Requerimientoscargo codigo = (Requerimientoscargo) value;
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }
}