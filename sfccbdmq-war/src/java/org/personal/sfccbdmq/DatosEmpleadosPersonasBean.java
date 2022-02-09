/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import org.talento.sfccbdmq.DatosEmpleadosBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "datosEmpleadosPersonas")
@ViewScoped
public class DatosEmpleadosPersonasBean extends DatosEmpleadosBean{

    /**
     * Creates a new instance of DatosEmpleadosActivosBean
     */
    public DatosEmpleadosPersonasBean() {
        super.campo="personal";
    }
    @PostConstruct
    private void interno(){
        super.modificarInterno();
    }
}
