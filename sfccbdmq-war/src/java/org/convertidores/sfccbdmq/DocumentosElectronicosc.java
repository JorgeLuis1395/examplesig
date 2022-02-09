/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.compras.sfccbdmq.DocumentosElectronicosBean;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Documentoselectronicos.class)
public class DocumentosElectronicosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Documentoselectronicos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            DocumentosElectronicosBean bean = (DocumentosElectronicosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "documentosElectronicosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(DocumentosElectronicosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Documentoselectronicos codigo=(Documentoselectronicos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Documentoselectronicos) value).getId().toString();
    }
}