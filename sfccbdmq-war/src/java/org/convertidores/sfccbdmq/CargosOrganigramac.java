/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.talento.sfccbdmq.CargoxOrganigramaBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Cargosxorganigrama.class)
public class CargosOrganigramac implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Cargosxorganigrama codigo = null;
        try {
            if (value == null) {
                return null;
            }
            CargoxOrganigramaBean bean = (CargoxOrganigramaBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "cargoxorganigrama");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(CargosOrganigramac.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Cargosxorganigrama codigo=(Cargosxorganigrama) value;
        if (codigo.getId()==null)
                return "0";
        return ((Cargosxorganigrama) value).getId().toString();
    }
}