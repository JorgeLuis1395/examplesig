/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Proyectos;

/**
 *
 * @author edwin
 */
public class AuxiliarMeses {
    private String codigo;
    private String codigoClasificador;
    private String nombre;
    private String nombreClasificador;
    private String estilo;
    private Proyectos proyecto;
    private double enero;
    private double febrero;
    private double marzo;
    private double abril;
    private double mayo;
    private double junio;
    private double julio;
    private double agosto;
    private double septiempbre;
    private double octubre;
    private double noviembre;
    private double diciembre;
    private double total;
    
    private Cajas caja;
    private Fondos fondo;
    private int id;
    private double valor;

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the enero
     */
    public double getEnero() {
        return enero;
    }

    /**
     * @param enero the enero to set
     */
    public void setEnero(double enero) {
        this.enero = enero;
    }

    /**
     * @return the febrero
     */
    public double getFebrero() {
        return febrero;
    }

    /**
     * @param febrero the febrero to set
     */
    public void setFebrero(double febrero) {
        this.febrero = febrero;
    }

    /**
     * @return the marzo
     */
    public double getMarzo() {
        return marzo;
    }

    /**
     * @param marzo the marzo to set
     */
    public void setMarzo(double marzo) {
        this.marzo = marzo;
    }

    /**
     * @return the abril
     */
    public double getAbril() {
        return abril;
    }

    /**
     * @param abril the abril to set
     */
    public void setAbril(double abril) {
        this.abril = abril;
    }

    /**
     * @return the mayo
     */
    public double getMayo() {
        return mayo;
    }

    /**
     * @param mayo the mayo to set
     */
    public void setMayo(double mayo) {
        this.mayo = mayo;
    }

    /**
     * @return the junio
     */
    public double getJunio() {
        return junio;
    }

    /**
     * @param junio the junio to set
     */
    public void setJunio(double junio) {
        this.junio = junio;
    }

    /**
     * @return the julio
     */
    public double getJulio() {
        return julio;
    }

    /**
     * @param julio the julio to set
     */
    public void setJulio(double julio) {
        this.julio = julio;
    }

    /**
     * @return the agosto
     */
    public double getAgosto() {
        return agosto;
    }

    /**
     * @param agosto the agosto to set
     */
    public void setAgosto(double agosto) {
        this.agosto = agosto;
    }

    /**
     * @return the septiempbre
     */
    public double getSeptiempbre() {
        return septiempbre;
    }

    /**
     * @param septiempbre the septiempbre to set
     */
    public void setSeptiempbre(double septiempbre) {
        this.septiempbre = septiempbre;
    }

    /**
     * @return the octubre
     */
    public double getOctubre() {
        return octubre;
    }

    /**
     * @param octubre the octubre to set
     */
    public void setOctubre(double octubre) {
        this.octubre = octubre;
    }

    /**
     * @return the noviembre
     */
    public double getNoviembre() {
        return noviembre;
    }

    /**
     * @param noviembre the noviembre to set
     */
    public void setNoviembre(double noviembre) {
        this.noviembre = noviembre;
    }

    /**
     * @return the diciembre
     */
    public double getDiciembre() {
        return diciembre;
    }

    /**
     * @param diciembre the diciembre to set
     */
    public void setDiciembre(double diciembre) {
        this.diciembre = diciembre;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }
    public void calculaTotal(){
        total=enero+febrero+marzo+abril+mayo+junio+julio+agosto+septiempbre+octubre+noviembre+diciembre;
    }
    public boolean isDistintoCero(){
        
        if ((enero!=0) || (febrero!=0) ||(marzo!=0) ||(abril!=0) ||(mayo!=0)
                ||(junio!=0) ||(julio!=0) ||(agosto!=0) ||
                (septiempbre!=0) || (octubre!=0) ||
                (noviembre!=0) || (diciembre!=0)){
            return true;
        }
        return false;
    }
    /**
     * @return the estilo
     */
    public String getEstilo() {
        return estilo;
    }

    /**
     * @param estilo the estilo to set
     */
    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    /**
     * @return the codigoClasificador
     */
    public String getCodigoClasificador() {
        return codigoClasificador;
    }

    /**
     * @param codigoClasificador the codigoClasificador to set
     */
    public void setCodigoClasificador(String codigoClasificador) {
        this.codigoClasificador = codigoClasificador;
    }

    /**
     * @return the nombreClasificador
     */
    public String getNombreClasificador() {
        return nombreClasificador;
    }

    /**
     * @param nombreClasificador the nombreClasificador to set
     */
    public void setNombreClasificador(String nombreClasificador) {
        this.nombreClasificador = nombreClasificador;
    }

    /**
     * @return the proyecto
     */
    public Proyectos getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(Proyectos proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the caja
     */
    public Cajas getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(Cajas caja) {
        this.caja = caja;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the fondo
     */
    public Fondos getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
    }
}
