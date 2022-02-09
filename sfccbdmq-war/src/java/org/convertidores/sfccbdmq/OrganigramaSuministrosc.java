/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.inventarios.sfccbdmq.SolicitudSuministrosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Organigramasuministros;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Organigramasuministros.class)
public class OrganigramaSuministrosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Organigramasuministros codigo = null;
        try {
            if (value == null) {
                return null;
            }
            SolicitudSuministrosBean bean = (SolicitudSuministrosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "solicitudTxSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traerSuministro(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(OrganigramaSuministrosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Organigramasuministros codigo=(Organigramasuministros) value;
        if (codigo.getId()==null)
                return "0";
        return ((Organigramasuministros) value).getId().toString();
    }
}