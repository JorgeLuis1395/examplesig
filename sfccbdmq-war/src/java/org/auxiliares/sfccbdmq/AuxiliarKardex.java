/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Suministros;

/**
 *
 * @author edwin
 */
public class AuxiliarKardex implements Serializable {

    /**
     * @return the txIngresosInv
     */
    public double getTxIngresosInv() {
        return txIngresosInv;
    }

    /**
     * @param txIngresosInv the txIngresosInv to set
     */
    public void setTxIngresosInv(double txIngresosInv) {
        this.txIngresosInv = txIngresosInv;
    }

    /**
     * @return the txEgresosInv
     */
    public double getTxEgresosInv() {
        return txEgresosInv;
    }

    /**
     * @param txEgresosInv the txEgresosInv to set
     */
    public void setTxEgresosInv(double txEgresosInv) {
        this.txEgresosInv = txEgresosInv;
    }

    /**
     * @return the txCostoIngresosInv
     */
    public double getTxCostoIngresosInv() {
        return txCostoIngresosInv;
    }

    /**
     * @param txCostoIngresosInv the txCostoIngresosInv to set
     */
    public void setTxCostoIngresosInv(double txCostoIngresosInv) {
        this.txCostoIngresosInv = txCostoIngresosInv;
    }

    /**
     * @return the txCostoEgresosInv
     */
    public double getTxCostoEgresosInv() {
        return txCostoEgresosInv;
    }

    /**
     * @param txCostoEgresosInv the txCostoEgresosInv to set
     */
    public void setTxCostoEgresosInv(double txCostoEgresosInv) {
        this.txCostoEgresosInv = txCostoEgresosInv;
    }

    /**
     * @return the txIngresos
     */
    public double getTxIngresos() {
        return txIngresos;
    }

    /**
     * @param txIngresos the txIngresos to set
     */
    public void setTxIngresos(double txIngresos) {
        this.txIngresos = txIngresos;
    }

    /**
     * @return the txEgresos
     */
    public double getTxEgresos() {
        return txEgresos;
    }

    /**
     * @param txEgresos the txEgresos to set
     */
    public void setTxEgresos(double txEgresos) {
        this.txEgresos = txEgresos;
    }

    /**
     * @return the txCostoIngresos
     */
    public double getTxCostoIngresos() {
        return txCostoIngresos;
    }

    /**
     * @param txCostoIngresos the txCostoIngresos to set
     */
    public void setTxCostoIngresos(double txCostoIngresos) {
        this.txCostoIngresos = txCostoIngresos;
    }

    /**
     * @return the txCostoEgresos
     */
    public double getTxCostoEgresos() {
        return txCostoEgresos;
    }

    /**
     * @param txCostoEgresos the txCostoEgresos to set
     */
    public void setTxCostoEgresos(double txCostoEgresos) {
        this.txCostoEgresos = txCostoEgresos;
    }

    /**
     * @return the fuente
     */
    public String getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    /**
     * @return the suministroEntidad
     */
    public Suministros getSuministroEntidad() {
        return suministroEntidad;
    }

    /**
     * @param suministroEntidad the suministroEntidad to set
     */
    public void setSuministroEntidad(Suministros suministroEntidad) {
        this.suministroEntidad = suministroEntidad;
    }

    private String suministro;
    private Date fecha;
    private String transaccion;
    private Integer numero;
    private String observaciones;
    private String fuente;
    private double saldoinicial;
    private double saldoinicialinv;
    private double saldoinicialtotal;
    private double cantidadingreso;
    private double cantidadingresoinv;
    private double ingreso;
    private double costounitarioing;
    private double costounitarioInving;
    private double totalingreso;
    private double cantidadegreso;
    private double cantidadegresoinv;
    private double egreso;
    private double costounitarioegreso;
    private double costounitarioInvegreso;
    private double totalegreso;
    private double cantidadsaldo;
    private double cantidadsaldoinv;
    private double costopromediosaldo;
    private double costopromediosaldoInv;
    private double txIngresos;
    private double txEgresos;
    private double txCostoIngresos;
    private double txCostoEgresos;
    private double txIngresosInv;
    private double txEgresosInv;
    private double txCostoIngresosInv;
    private double txCostoEgresosInv;
    private double saldo;
    private String unidades;
    private Suministros suministroEntidad;
    private Codigos familia;

    /**
     * @return the suministro
     */
    public String getSuministro() {
        return suministro;
    }

    /**
     * @param suministro the suministro to set
     */
    public void setSuministro(String suministro) {
        this.suministro = suministro;
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
     * @return the transaccion
     */
    public String getTransaccion() {
        return transaccion;
    }

    /**
     * @param transaccion the transaccion to set
     */
    public void setTransaccion(String transaccion) {
        this.transaccion = transaccion;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
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
     * @return the saldoinicial
     */
    public double getSaldoinicial() {
        return saldoinicial;
    }

    /**
     * @param saldoinicial the saldoinicial to set
     */
    public void setSaldoinicial(double saldoinicial) {
        this.saldoinicial = saldoinicial;
    }

    /**
     * @return the saldoinicialinv
     */
    public double getSaldoinicialinv() {
        return saldoinicialinv;
    }

    /**
     * @param saldoinicialinv the saldoinicialinv to set
     */
    public void setSaldoinicialinv(double saldoinicialinv) {
        this.saldoinicialinv = saldoinicialinv;
    }

    /**
     * @return the saldoinicialtotal
     */
    public double getSaldoinicialtotal() {
        return saldoinicialtotal;
    }

    /**
     * @param saldoinicialtotal the saldoinicialtotal to set
     */
    public void setSaldoinicialtotal(double saldoinicialtotal) {
        this.saldoinicialtotal = saldoinicialtotal;
    }

    /**
     * @return the cantidadingreso
     */
    public double getCantidadingreso() {
        return cantidadingreso;
    }

    /**
     * @param cantidadingreso the cantidadingreso to set
     */
    public void setCantidadingreso(double cantidadingreso) {
        this.cantidadingreso = cantidadingreso;
    }

    /**
     * @return the cantidadingresoinv
     */
    public double getCantidadingresoinv() {
        return cantidadingresoinv;
    }

    /**
     * @param cantidadingresoinv the cantidadingresoinv to set
     */
    public void setCantidadingresoinv(double cantidadingresoinv) {
        this.cantidadingresoinv = cantidadingresoinv;
    }

    /**
     * @return the ingreso
     */
    public double getIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(double ingreso) {
        this.ingreso = ingreso;
    }

    /**
     * @return the costounitarioing
     */
    public double getCostounitarioing() {
        return costounitarioing;
    }

    /**
     * @param costounitarioing the costounitarioingreso to set
     */
    public void setCostounitarioing(double costounitarioing) {
        this.costounitarioing = costounitarioing;
    }

    /**
     * @return the totalingreso
     */
    public double getTotalingreso() {
        return totalingreso;
    }

    /**
     * @param totalingreso the totalingreso to set
     */
    public void setTotalingreso(double totalingreso) {
        this.totalingreso = totalingreso;
    }

    /**
     * @return the cantidadegreso
     */
    public double getCantidadegreso() {
        return cantidadegreso;
    }

    /**
     * @param cantidadegreso the cantidadegreso to set
     */
    public void setCantidadegreso(double cantidadegreso) {
        this.cantidadegreso = cantidadegreso;
    }

    /**
     * @return the cantidadegresoinv
     */
    public double getCantidadegresoinv() {
        return cantidadegresoinv;
    }

    /**
     * @param cantidadegresoinv the cantidadegresoinv to set
     */
    public void setCantidadegresoinv(double cantidadegresoinv) {
        this.cantidadegresoinv = cantidadegresoinv;
    }

    /**
     * @return the egreso
     */
    public double getEgreso() {
        return egreso;
    }

    /**
     * @param egreso the egreso to set
     */
    public void setEgreso(double egreso) {
        this.egreso = egreso;
    }

    /**
     * @return the costounitarioegreso
     */
    public double getCostounitarioegreso() {
        return costounitarioegreso;
    }

    /**
     * @param costounitarioegreso the costounitarioegreso to set
     */
    public void setCostounitarioegreso(double costounitarioegreso) {
        this.costounitarioegreso = costounitarioegreso;
    }

    /**
     * @return the totalegreso
     */
    public double getTotalegreso() {
        return totalegreso;
    }

    /**
     * @param totalegreso the totalegreso to set
     */
    public void setTotalegreso(double totalegreso) {
        this.totalegreso = totalegreso;
    }

    /**
     * @return the cantidadsaldo
     */
    public double getCantidadsaldo() {
        return cantidadsaldo;
    }

    /**
     * @param cantidadsaldo the cantidadsaldo to set
     */
    public void setCantidadsaldo(double cantidadsaldo) {
        this.cantidadsaldo = cantidadsaldo;
    }

    /**
     * @return the cantidadsaldoinv
     */
    public double getCantidadsaldoinv() {
        return cantidadsaldoinv;
    }

    /**
     * @param cantidadsaldoinv the cantidadsaldoinv to set
     */
    public void setCantidadsaldoinv(double cantidadsaldoinv) {
        this.cantidadsaldoinv = cantidadsaldoinv;
    }

    /**
     * @return the costopromediosaldo
     */
    public double getCostopromediosaldo() {
        return costopromediosaldo;
    }

    /**
     * @param costopromediosaldo the costopromediosaldo to set
     */
    public void setCostopromediosaldo(double costopromediosaldo) {
        this.costopromediosaldo = costopromediosaldo;
    }

    /**
     * @return the saldo
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    /**
     * @return the unidades
     */
    public String getUnidades() {
        return unidades;
    }

    /**
     * @param unidades the unidades to set
     */
    public void setUnidades(String unidades) {
        this.unidades = unidades;
    }

    /**
     * @return the costopromediosaldoInv
     */
    public double getCostopromediosaldoInv() {
        return costopromediosaldoInv;
    }

    /**
     * @param costopromediosaldoInv the costopromediosaldoInv to set
     */
    public void setCostopromediosaldoInv(double costopromediosaldoInv) {
        this.costopromediosaldoInv = costopromediosaldoInv;
    }

    /**
     * @return the costounitarioInvegreso
     */
    public double getCostounitarioInvegreso() {
        return costounitarioInvegreso;
    }

    /**
     * @param costounitarioInvegreso the costounitarioInvegreso to set
     */
    public void setCostounitarioInvegreso(double costounitarioInvegreso) {
        this.costounitarioInvegreso = costounitarioInvegreso;
    }

    /**
     * @return the costounitarioInving
     */
    public double getCostounitarioInving() {
        return costounitarioInving;
    }

    /**
     * @param costounitarioInving the costounitarioInving to set
     */
    public void setCostounitarioInving(double costounitarioInving) {
        this.costounitarioInving = costounitarioInving;
    }

    /**
     * @return the familia
     */
    public Codigos getFamilia() {
        return familia;
    }

    /**
     * @param familia the familia to set
     */
    public void setFamilia(Codigos familia) {
        this.familia = familia;
    }
}