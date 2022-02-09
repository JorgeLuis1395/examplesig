/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.talento.sfccbdmq.DatosEmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "datosEmpleadosTesoreria")
@ViewScoped
public class DatosEmpleadosTesoreriaBean extends DatosEmpleadosBean{

    /**
     * Creates a new instance of DatosEmpleadosActivosBean
     */
    public DatosEmpleadosTesoreriaBean() {
        super.campo="tesoreria";
    }
    
}
