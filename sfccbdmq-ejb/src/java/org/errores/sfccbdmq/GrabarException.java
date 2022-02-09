/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.errores.sfccbdmq;

import javax.ejb.ApplicationException;

/**
 *
 * @author maquina01
 */
@ApplicationException(rollback = true)
public class GrabarException extends Exception{
    public GrabarException(String message, Throwable cause) {
        super("Error al ejecutar la grebación del Registro  :\n" + message, cause);
    }

    public GrabarException(String message) {
        super("Error al ejecutar la grebación del Registro :\n" + message);
    }
    
}
