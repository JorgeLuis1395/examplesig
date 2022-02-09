/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.convertidores.sfccbdmq;


import org.talento.sfccbdmq.CabecerasEmpleadosBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@FacesConverter(forClass = Cabecerasrrhh.class)
public class CamposCabecerasc implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Cabecerasrrhh codigo=null;
        CabecerasEmpleadosBean bean=(CabecerasEmpleadosBean)
                context.getELContext().getELResolver().getValue(context.getELContext(), null, "cabecerasEmpleados");
        Integer id = Integer.parseInt(value);
        try {
            codigo=bean.traerCabecera(id);
        } catch (ConsultarException ex) {
            Logger.getLogger("Convertidor").log(Level.SEVERE, null, ex);
        }
        return codigo;
    
    
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
      Cabecerasrrhh codigo = (Cabecerasrrhh)value;
        if(codigo.getId()==null){
            return "0";
        }
        return codigo.getId().toString();
    }
    
}
