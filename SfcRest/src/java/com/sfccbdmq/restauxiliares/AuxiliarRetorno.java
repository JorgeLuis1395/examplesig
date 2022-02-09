/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.restauxiliares;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class AuxiliarRetorno implements Serializable{
     private static final long serialVersionUID = 1L;
    private String ok;

    /**
     * @return the ok
     */
    public String getOk() {
        return ok;
    }

    /**
     * @param ok the ok to set
     */
    public void setOk(String ok) {
        this.ok = ok;
    }
    
}
