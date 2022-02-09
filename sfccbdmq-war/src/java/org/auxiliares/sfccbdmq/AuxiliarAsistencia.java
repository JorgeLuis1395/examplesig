/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Transient;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Organigrama;

/**
 *
 * @author edwin
 */
public class AuxiliarAsistencia implements Serializable {

    private Empleados empleado;
    private Date entrada;
    private Date entradaLeida;
    private Date salidaUno;
    private Date salidaLeidaUno;
    private Date entradaDos;
    private Date entradaLeidaDos;
    private Date salida;
    private Date salidaLeida;
    private String dondeUno;
    private String dondeDos;
    private String dondeTres;
    private String dondeCuatro;
    private String observaciones;
    private Organigrama organigrama;
    private int cuantoUno;
    private int cuantoDos;
    private int cuantoTres;
    private Date horaIngreso;
    private Date horaSalidaLunch;
    private Date horaIngresoLunch;
    private Date horaSalida;
     private Date fecha;
    private String fechaNormal;
     
     @Transient
     private String colorIngreso;
     private String colorAlmuerzo;
     private String colorSalida;

    /**
     * @return the entrada
     */
    public Date getEntrada() {
        return entrada;
    }

    /**
     * @param entrada the entrada to set
     */
    public void setEntrada(Date entrada) {
        this.entrada = entrada;
    }

    /**
     * @return the entradaLeida
     */
    public Date getEntradaLeida() {
        return entradaLeida;
    }

    /**
     * @param entradaLeida the entradaLeida to set
     */
    public void setEntradaLeida(Date entradaLeida) {
        this.entradaLeida = entradaLeida;
    }

    /**
     * @return the salidaUno
     */
    public Date getSalidaUno() {
        return salidaUno;
    }

    /**
     * @param salidaUno the salidaUno to set
     */
    public void setSalidaUno(Date salidaUno) {
        this.salidaUno = salidaUno;
    }

    /**
     * @return the salidaLeidaUno
     */
    public Date getSalidaLeidaUno() {
        return salidaLeidaUno;
    }

    /**
     * @param salidaLeidaUno the salidaLeidaUno to set
     */
    public void setSalidaLeidaUno(Date salidaLeidaUno) {
        this.salidaLeidaUno = salidaLeidaUno;
    }

    /**
     * @return the entradaDos
     */
    public Date getEntradaDos() {
        return entradaDos;
    }

    /**
     * @param entradaDos the entradaDos to set
     */
    public void setEntradaDos(Date entradaDos) {
        this.entradaDos = entradaDos;
    }

    /**
     * @return the entradaLeidaDos
     */
    public Date getEntradaLeidaDos() {
        return entradaLeidaDos;
    }

    /**
     * @param entradaLeidaDos the entradaLeidaDos to set
     */
    public void setEntradaLeidaDos(Date entradaLeidaDos) {
        this.entradaLeidaDos = entradaLeidaDos;
    }

    /**
     * @return the salida
     */
    public Date getSalida() {
        return salida;
    }

    /**
     * @param salida the salida to set
     */
    public void setSalida(Date salida) {
        this.salida = salida;
    }

    /**
     * @return the salidaLeida
     */
    public Date getSalidaLeida() {
        return salidaLeida;
    }

    /**
     * @param salidaLeida the salidaLeida to set
     */
    public void setSalidaLeida(Date salidaLeida) {
        this.salidaLeida = salidaLeida;
    }

    /**
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the dondeUno
     */
    public String getDondeUno() {
        return dondeUno;
    }

    /**
     * @param dondeUno the dondeUno to set
     */
    public void setDondeUno(String dondeUno) {
        this.dondeUno = dondeUno;
    }

    /**
     * @return the dondeDos
     */
    public String getDondeDos() {
        return dondeDos;
    }

    /**
     * @param dondeDos the dondeDos to set
     */
    public void setDondeDos(String dondeDos) {
        this.dondeDos = dondeDos;
    }

    /**
     * @return the dondeTres
     */
    public String getDondeTres() {
        return dondeTres;
    }

    /**
     * @param dondeTres the dondeTres to set
     */
    public void setDondeTres(String dondeTres) {
        this.dondeTres = dondeTres;
    }

    /**
     * @return the dondeCuatro
     */
    public String getDondeCuatro() {
        return dondeCuatro;
    }

    /**
     * @param dondeCuatro the dondeCuatro to set
     */
    public void setDondeCuatro(String dondeCuatro) {
        this.dondeCuatro = dondeCuatro;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the organigrama
     */
    public Organigrama getOrganigrama() {
        return organigrama;
    }

    /**
     * @param organigrama the organigrama to set
     */
    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    /**
     * @return the cuantoUno
     */
    public int getCuantoUno() {
        return cuantoUno;
    }

    /**
     * @param cuantoUno the cuantoUno to set
     */
    public void setCuantoUno(int cuantoUno) {
        this.cuantoUno = cuantoUno;
    }

    /**
     * @return the cuantoDos
     */
    public int getCuantoDos() {
        return cuantoDos;
    }

    /**
     * @param cuantoDos the cuantoDos to set
     */
    public void setCuantoDos(int cuantoDos) {
        this.cuantoDos = cuantoDos;
    }

    /**
     * @return the horaIngreso
     */
    public Date getHoraIngreso() {
        return horaIngreso;
    }

    /**
     * @param horaIngreso the horaIngreso to set
     */
    public void setHoraIngreso(Date horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    /**
     * @return the horaSalidaLunch
     */
    public Date getHoraSalidaLunch() {
        return horaSalidaLunch;
    }

    /**
     * @param horaSalidaLunch the horaSalidaLunch to set
     */
    public void setHoraSalidaLunch(Date horaSalidaLunch) {
        this.horaSalidaLunch = horaSalidaLunch;
    }

    /**
     * @return the horaIngresoLunch
     */
    public Date getHoraIngresoLunch() {
        return horaIngresoLunch;
    }

    /**
     * @param horaIngresoLunch the horaIngresoLunch to set
     */
    public void setHoraIngresoLunch(Date horaIngresoLunch) {
        this.horaIngresoLunch = horaIngresoLunch;
    }

    /**
     * @return the horaSalida
     */
    public Date getHoraSalida() {
        return horaSalida;
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(Date horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * @return the cuantoTres
     */
    public int getCuantoTres() {
        return cuantoTres;
    }

    /**
     * @param cuantoTres the cuantoTres to set
     */
    public void setCuantoTres(int cuantoTres) {
        this.cuantoTres = cuantoTres;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the colorIngreso
     */
    public String getColorIngreso() {
        return colorIngreso;
    }

    /**
     * @param colorIngreso the colorIngreso to set
     */
    public void setColorIngreso(String colorIngreso) {
        this.colorIngreso = colorIngreso;
    }

    /**
     * @return the colorAlmuerzo
     */
    public String getColorAlmuerzo() {
        return colorAlmuerzo;
    }

    /**
     * @param colorAlmuerzo the colorAlmuerzo to set
     */
    public void setColorAlmuerzo(String colorAlmuerzo) {
        this.colorAlmuerzo = colorAlmuerzo;
    }

    /**
     * @return the colorSalida
     */
    public String getColorSalida() {
        return colorSalida;
    }

    /**
     * @param colorSalida the colorSalida to set
     */
    public void setColorSalida(String colorSalida) {
        this.colorSalida = colorSalida;
    }

    /**
     * @return the fechaNormal
     */
    public String getFechaNormal() {
        return fechaNormal;
    }

    /**
     * @param fechaNormal the fechaNormal to set
     */
    public void setFechaNormal(String fechaNormal) {
        this.fechaNormal = fechaNormal;
    }

    
 

}
