/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import org.contabilidad.sfccbdmq.CuentasBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCalendario;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.GarantiasFacade;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "vencimientosGarantiasSfccbdmq")
@ViewScoped
public class VencimientosGarantiasBean {

    /**
     * Creates a new instance of AuxiliarCalendarioBean
     */
    public VencimientosGarantiasBean() {
    }
    private AuxiliarCalendario calendario;
    private List<AuxiliarCalendario> calendarioMes;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private GarantiasFacade ejbGarantias;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private int mes;
    private int anio;

    /**
     * @return the calendario
     */
    public AuxiliarCalendario getAuxiliarCalendario() {
        return calendario;
    }

    /**
     * @param calendario the calendario to set
     */
    public void setAuxiliarCalendario(AuxiliarCalendario calendario) {
        this.calendario = calendario;
    }

    /**
     * @return the calendarioMes
     */
    public List<AuxiliarCalendario> getCalendarioMes() {
        return calendarioMes;
    }

    /**
     * @param calendarioMes the calendarioMes to set
     */
    public void setCalendarioMes(List<AuxiliarCalendario> calendarioMes) {
        this.calendarioMes = calendarioMes;
    }

    public String buscar() {
        Calendar c = Calendar.getInstance();
        int ultimo_dia = 0;
        int hoy = c.get(Calendar.DATE);
        int mesHoy = c.get(Calendar.MONTH)+1;
        if (mesHoy!=mes){
            hoy=-1;
        }
        if (mes == 12) {
            ultimo_dia = 31;
        } else {
            c.set(anio, mes, 1);
            c.add(Calendar.DATE, -1);
            ultimo_dia = c.get(Calendar.DATE);

        }
        c.set(anio, mes - 1, 1);
        calendarioMes = new LinkedList<>();
        calendario = new AuxiliarCalendario();
        int j = 1;
        int hoyMasSiete = 10;
        boolean guarda=false;
        
        for (int i = 0; i < ultimo_dia; i++) {
            String estilo="background-color: #D20005;color: white";
            int dia = c.get(Calendar.DAY_OF_WEEK);
            Integer diaMostrar = c.get(Calendar.DATE);
            if (hoy==i){
                hoyMasSiete=0;
            }
            if (hoyMasSiete<8){
                estilo="background-color: #D20005;color: white";
                hoyMasSiete++;
            } else {
                estilo="";
            }
            // buscar garatias de la fecha
            String garantias="";
            Map parmetros=new HashMap();
            parmetros.put(";where", "o.vencimiento=:vencimiento");
            parmetros.put("vencimiento", c.getTime());
            try {
                List<Garantias> lista=ejbGarantias.encontarParametros(parmetros);
                 garantias="";
                for (Garantias g:lista){
                    garantias+="<p><Strong>Grarantía :</Strong>"+g.getObjeto()+""
                            + "</p><p><Strong>Contrato : </Strong>"+g.getContrato().toString()+
                            "</p><Strong>Proveedor : </Strong>"+g.getContrato().getProveedor().getEmpresa().toString()+"</p>";
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(VencimientosGarantiasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (dia == Calendar.SUNDAY) {
                calendario = new AuxiliarCalendario();
                calendario.setDiaDomingo(diaMostrar);
                calendario.setFechaDomingo(c.getTime());
                calendario.setContenidoDomingo(garantias);
                calendario.setEstilo(estilo);
                guarda=false;
            } else if (dia == Calendar.MONDAY) {
                calendario.setDiaLunes(diaMostrar);
                calendario.setFechaLunes(c.getTime());
                calendario.setContenidoLunes(garantias);
                calendario.setEstilo(estilo);
            } else if (dia == Calendar.TUESDAY) {
                calendario.setDiaMartes(diaMostrar);
                calendario.setFechaMartes(c.getTime());
                calendario.setContenidoMartes(garantias);
                calendario.setEstilo(estilo);
            } else if (dia == Calendar.WEDNESDAY) {
                calendario.setDiaMiercoles(diaMostrar);
                calendario.setFechaMiercoles(c.getTime());
                calendario.setContenidoMiercoles(garantias);
                calendario.setEstilo(estilo);
            } else if (dia == Calendar.THURSDAY) {
                calendario.setDiaJueves(diaMostrar);
                calendario.setFechaJueves(c.getTime());
                calendario.setContenidoJueves(garantias);
                calendario.setEstilo(estilo);
            } else if (dia == Calendar.FRIDAY) {
                calendario.setDiaViernes(diaMostrar);
                calendario.setFechaViernes(c.getTime());
                calendario.setContenidoViernes(garantias);
                calendario.setEstilo(estilo);
            } else if (dia == Calendar.SATURDAY) {
                calendario.setDiaSabado(diaMostrar);
                calendario.setFechaSabado(c.getTime());
                calendario.setContenidoSabado(garantias);
                calendarioMes.add(calendario);
                calendario.setEstilo(estilo);
                guarda=true;
            }
            c.add(Calendar.DATE, 1);
            j++;
        }
        if (!guarda){
             calendarioMes.add(calendario);
        }
        return null;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH) + 1;
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "VencimientosGarantiaVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
    }

    /**
     * @return the perfil
     */
    public Perfiles getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(Perfiles perfil) {
        this.perfil = perfil;
    }

    /**
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
    }

    /**
     * @param configuracionBean the configuracionBean to set
     */
    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

}
