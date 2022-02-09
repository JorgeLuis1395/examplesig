/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;




import org.inventarios.sfccbdmq.UnidadesBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Unidades;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@FacesConverter(forClass = Unidades.class)
public class Unidadesc implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Unidades codigo = null;
        try {
            if (value == null) {
                return null;
            }
            UnidadesBean bean = (UnidadesBean)
                    context.getELContext().getELResolver().
                    getValue(context.getELContext(), null, "unidadesSfccbdmq");
            Integer id = Integer.parseInt(value);
            codigo= bean.traer(id);
        } catch (ConsultarException ex) {
            Logger.getLogger(Unidadesc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Unidades codigo=(Unidades) value;
        if (codigo.getId()==null)
                return "0";
        return ((Unidades) value).getId().toString();
    }
}