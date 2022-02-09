/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.talento.sfccbdmq.DatosEmpleadosBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "datosEmpleadosActivosSfccbdmq")
@ViewScoped
public class DatosEmpleadosActivosBean extends DatosEmpleadosBean{

    /**
     * Creates a new instance of DatosEmpleadosActivosBean
     */
    public DatosEmpleadosActivosBean() {
        super.campo="activos";
    }
    
}
