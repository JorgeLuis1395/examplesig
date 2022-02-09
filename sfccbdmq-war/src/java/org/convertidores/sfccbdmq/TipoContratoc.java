/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.talento.sfccbdmq.TipoContratoBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edison
 */
@FacesConverter(forClass = Tiposcontratos.class)
public class TipoContratoc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Tiposcontratos codigo = null;
        try {
            TipoContratoBean bean = (TipoContratoBean) 
                    context.getELContext().getELResolver().getValue(context.getELContext(), null, "tipoContrato");
            Integer id = Integer.parseInt(value);
            codigo = bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(TipoContratoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Tiposcontratos codigo = (Tiposcontratos) value;
        if (codigo.getId() == null) {
            return "0";
        }
        return codigo.getId().toString();
    }
}
