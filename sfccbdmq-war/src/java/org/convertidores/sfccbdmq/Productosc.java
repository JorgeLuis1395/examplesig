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
import org.pagos.sfccbdmq.ProductosBean;
import org.entidades.sfccbdmq.Productos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Productos.class)
public class Productosc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Productos codigo = null;
        try {
            if (value == null) {
                return null;
            }
            ProductosBean bean = (ProductosBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "productosSfcbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Productosc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Productos codigo=(Productos) value;
        if (codigo.getId()==null)
                return "0";
        return ((Productos) value).getId().toString();
    }
}