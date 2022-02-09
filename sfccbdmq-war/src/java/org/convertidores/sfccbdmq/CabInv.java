/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.presupuestos.sfccbdmq.CompromisosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Cabecerainventario.class)
public class CabInv implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Cabecerainventario codigo = null;
        try {
            if (value == null) {
                return null;
            }
            CompromisosBean bean = (CompromisosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "compromisosSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traerInv(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(CabInv.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Cabecerainventario codigo=(Cabecerainventario) value;
        if (codigo.getId()==null)
                return "0";
        return ((Cabecerainventario) value).getId().toString();
    }
}