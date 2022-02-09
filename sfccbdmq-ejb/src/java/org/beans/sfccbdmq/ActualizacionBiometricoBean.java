/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import org.entidades.sfccbdmq.Biometrico;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luis
 */
@Singleton
@LocalBean
public class ActualizacionBiometricoBean {

    private Date fechaDesde;
    private Date fechaHasta;

    @EJB
    private MarcacionesbiometricoFacade ejbMarcaciones;
    @EJB
    private BiometricoFacade ejbBiometrico;

    @Schedule(minute = "59", second = "59", dayOfMonth = "*", month = "*", year = "*", hour = "23", dayOfWeek = "*")
    public void actualizarBiometrico() {

        try {
            Calendar desdeCalendario = Calendar.getInstance();
            Calendar hastaCalendario = Calendar.getInstance();
            desdeCalendario.set(Calendar.HOUR_OF_DAY, 0);
            desdeCalendario.set(Calendar.MINUTE, 0);
            desdeCalendario.set(Calendar.SECOND, 0);
            hastaCalendario.set(Calendar.HOUR_OF_DAY, 23);
            hastaCalendario.set(Calendar.MINUTE, 59);
            hastaCalendario.set(Calendar.SECOND, 59);

            Map parametros = new HashMap();

            String where = " o.cedula is not null ";
            where += " and o.fechahora between :desde and :hasta ";
            parametros.put("desde", desdeCalendario.getTime());
            parametros.put("hasta", hastaCalendario.getTime());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechahora asc");
            List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.cedula is not null and o.fecha between :desde and :hasta ");
            parametros.put("desde", desdeCalendario.getTime());
            parametros.put("hasta", hastaCalendario.getTime());
            List<Biometrico> listabiometrico = ejbBiometrico.encontarParametros(parametros);
            if (listabiometrico.isEmpty()) {
                actualizar();
            } else {
                 for (Marcacionesbiometrico b : blista) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.idmarcacion=:idbiometrico");
                    parametros.put("idbiometrico", b.getId());
                    System.out.println("Biometrico: "+b.getId());
                    List<Biometrico> listaCompararBiometrico = ejbBiometrico.encontarParametros(parametros);
                    if (listaCompararBiometrico.isEmpty()) {
                        Biometrico bio = new Biometrico();
                        bio.setCedula(b.getCedula());
                        bio.setIdmarcacion(b.getId());
                        bio.setDispositivo(b.getDispositivo());
                        bio.setSitio(b.getSitio());
                        bio.setFecha(b.getFechahora());
                        bio.setFechaproceso(b.getFechahoraproceso());
                        bio.setEntrada(Boolean.TRUE);
                        ejbBiometrico.create(bio, "");

                    } else {
                        System.out.println("Marcaciones biométrico " + b.getId() + " y " + "Biométrico " + listaCompararBiometrico.get(0).getId());
                    }
                }
            }
        } catch (ConsultarException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(ActualizacionBiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InsertarException ex) {
            Logger.getLogger(ActualizacionBiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String actualizar() {
        try {

            Map parametros = new HashMap();

//            String where = " o.cedula is not null and o.fechahora between :desde and :hasta";
            String where = " o.cedula is not null ";
            parametros.put(";where", where);
            parametros.put(";inicial", 0);
            parametros.put(";final", 500);
            //parametros.put("codigo", e.getEntidad().getPin());
            parametros.put(";orden", "o.fechahora asc");
            List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);
            Biometrico biome = new Biometrico();
            for (Marcacionesbiometrico b : blista) {
                biome.setIdmarcacion(b.getId());
                biome.setCedula(b.getCedula());
                biome.setDispositivo(b.getDispositivo());
                biome.setSitio(b.getSitio());
                biome.setFecha(b.getFechahora());
                biome.setFechaproceso(b.getFechahoraproceso());
                biome.setEntrada(Boolean.TRUE);
                ejbBiometrico.create(biome, "");
            }

        } catch (ConsultarException | InsertarException ex) {
            Logger.getLogger(ActualizacionBiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the fechaDesde
     */
    public Date getFechaDesde() {
        return fechaDesde;
    }

    /**
     * @param fechaDesde the fechaDesde to set
     */
    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    /**
     * @return the fechaHasta
     */
    public Date getFechaHasta() {
        return fechaHasta;
    }

    /**
     * @param fechaHasta the fechaHasta to set
     */
    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
}