/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.talento.sfccbdmq.EscalaSalarialBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Escalassalariales;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edisonfabsac
 */
@FacesConverter(forClass = Escalassalariales.class)
public class EscalasSalarialesc implements Converter {
     @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Escalassalariales codigo = null;
        try {
            EscalaSalarialBean bean =
                    (EscalaSalarialBean) context.getELContext().getELResolver().getValue(context.getELContext(), null, "escala");
            Integer id = Integer.parseInt(value);
            codigo = bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(EscalasSalarialesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Escalassalariales codigo = (Escalassalariales) value;
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }
}