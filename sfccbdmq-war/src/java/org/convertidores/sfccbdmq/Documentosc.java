/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.contabilidad.sfccbdmq.DocumentosEmisionBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Documentos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Documentos.class)
public class Documentosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Documentos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            DocumentosEmisionBean bean = (DocumentosEmisionBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "documentosEmisionSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Documentosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Documentos codigo=(Documentos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Documentos) value).getId().toString();
    }
}