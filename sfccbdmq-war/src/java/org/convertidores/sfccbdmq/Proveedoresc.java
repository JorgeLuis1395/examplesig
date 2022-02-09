/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.compras.sfccbdmq.ProveedoresBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Proveedores;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Proveedores.class)
public class Proveedoresc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Proveedores codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ProveedoresBean bean = (ProveedoresBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "proveedoresSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traerProveedor(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Proveedoresc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Proveedores codigo=(Proveedores) value;
        if (codigo.getId()==null)
                return "0";
        return ((Proveedores) value).getId().toString();
    }
}