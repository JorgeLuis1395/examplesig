/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarActivos;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.GrupoactivosFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.talento.sfccbdmq.RolEmpleadoBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteGruposSumadoSfccbdmq")
@ViewScoped
public class ReporteGruposSumadoBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReporteGruposSumadoBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Perfiles perfil;
    private LazyDataModel<Activos> activos;
    private List<Activos> listaActivos;
    private List<AuxiliarActivos> listaAux;
    private Date desde;
    private Date hasta;
    private Resource reporteXls;
    private double totalSumadoInicio;
    private double totalSumadoInicioControl;
    private double totalSumadoIngreso;
    private double totalSumadoEgreso;
    private double totalSumadoIngresoControl;
    private double totalSumadoEgresoControl;

    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private GrupoactivosFacade ejbGrupoactivos;

    @PostConstruct
    private void activar() {
        hasta = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicial());
        desde = c.getTime();
        c.setTime(new Date());
        c.set(Calendar.DATE, 31);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        hasta = c.getTime();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteTrackingActivosVista";
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
//    }
    }

    public String buscar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id is not null");
            parametros.put(";orden", "o.codigo");
            List<Grupoactivos> lista = ejbGrupoactivos.encontarParametros(parametros);
            listaAux = new LinkedList<>();
            double vi1t = 0;
            double vi2t = 0;
            double vi3t = 0;
            double vi4t = 0;
            double vic1t = 0;
            double vic2t = 0;
            double vic3t = 0;
            double vic4t = 0;

            for (Grupoactivos ga : lista) {
                //Activos Fijos
                //Saldos iniciales //Ingresos
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechaingreso<:desde and o.control=false and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("grupo", ga);
                double vi1 = ejbActivos.sumarCampo(parametros).doubleValue();
                vi1t += vi1;
                //Egresos
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechasolicitud<:desde and o.control=false and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("grupo", ga);
                double vi2 = ejbActivos.sumarCampo(parametros).doubleValue();
                vi2t += vi2;

                //Control
                //Saldos iniciales //Ingresos
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechaingreso<:desde and o.control=true and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("grupo", ga);
                double vic1 = ejbActivos.sumarCampo(parametros).doubleValue();
                vic1t += vic1;
                //Egresos
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechasolicitud<:desde and o.control=true and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("grupo", ga);
                double vic2 = ejbActivos.sumarCampo(parametros).doubleValue();
                vic2t += vic2;

                //Movimientos de Ingresos, Activos Fijos
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechaingreso between :desde and :hasta and o.control=false and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                parametros.put("grupo", ga);
                double vi3 = ejbActivos.sumarCampo(parametros).doubleValue();
                vi3t += vi3;
                //Movimientos de Ingresos, Control
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechaingreso between :desde and :hasta and o.control=true and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                parametros.put("grupo", ga);
                double vic3 = ejbActivos.sumarCampo(parametros).doubleValue();
                vic3t += vic3;

                //Movimientos de Egresos, Activos Fijos
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechasolicitud  between :desde and :hasta and o.control=false and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                parametros.put("grupo", ga);
                double vi4 = ejbActivos.sumarCampo(parametros).doubleValue();
                vi4t += vi4;
                //Movimientos de Egresoss, Control
                parametros = new HashMap();
                parametros.put(";campo", "o.valoralta");
                parametros.put(";where", "o.fechasolicitud  between :desde and :hasta and o.control=true and o.grupo=:grupo");
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                parametros.put("grupo", ga);
                double vic4 = ejbActivos.sumarCampo(parametros).doubleValue();
                vic4t += vic4;

                AuxiliarActivos aux = new AuxiliarActivos();
                aux.setCodigo(ga.getNombre());
                aux.setValorInicio(vi1 - vi2);
                aux.setValorInicioControl(vic1 - vic2);
                aux.setValorIngreso(vi3);
                aux.setValorIngresoControl(vic3);
                aux.setValorEgreso(vi4);
                aux.setValorEgresoControl(vic4);
                listaAux.add(aux);
            }
            AuxiliarActivos aux = new AuxiliarActivos();
            aux.setCodigo("Total");
            aux.setValorInicio(vi1t - vi2t);
            aux.setValorInicioControl(vic1t - vic2t);
            aux.setValorIngreso(vi3t);
            aux.setValorIngresoControl(vic3t);
            aux.setValorEgreso(vi4t);
            aux.setValorEgresoControl(vic4t);
            listaAux.add(aux);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String exportar() {
        try {
            if (listaActivos == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("ActivosGrupos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA INGRESO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NOMBRE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TIPO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GRUPO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CÓDIGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CÓDIGO PADRE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "OBSERVACIONES"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "C. COSTO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MARCA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MODELO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NO. SERIE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ESTADO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CUSTODIO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "OFICINA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "EMPRESA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR ALTA"));
            xls.agregarFila(campos, true);
            for (Activos g1 : listaActivos) {
                if (g1 != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", g1.getFechaingreso() != null ? (sdf.format(g1.getFechaingreso())) : ""));
                    campos.add(new AuxiliarReporte("String", g1.getNombre() != null ? g1.getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getGrupo() != null ? g1.getGrupo().getTipo().toString() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getGrupo() != null ? g1.getGrupo().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getCodigo() != null ? g1.getCodigo() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getPadre() != null ? g1.getPadre().getCodigo() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getObservaciones() != null ? g1.getObservaciones() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getCcosto() != null ? g1.getCcosto() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getActivomarca() != null ? g1.getActivomarca().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getModelo() != null ? g1.getModelo() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getNumeroserie() != null ? g1.getNumeroserie() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getEstado() != null ? g1.getEstado().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getCustodio() != null ? g1.getCustodio().getEntidad().toString() : ""));
                    campos.add(new AuxiliarReporte("String", g1.getLocalizacion() != null ? (g1.getLocalizacion().getEdificio() + "-" + g1.getLocalizacion().getNombre()) : ""));
                    campos.add(new AuxiliarReporte("String", g1.getProveedor() != null ? g1.getProveedor().getEmpresa().toString() : ""));
                    campos.add(new AuxiliarReporte("double", g1.getValoralta().doubleValue()));
                    xls.agregarFila(campos, false);
                }
            }

            setReporteXls(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    /**
     * @return the subgrupoBean
     */
    public SubGruposBean getSubgrupoBean() {
        return subgrupoBean;
    }

    /**
     * @param subgrupoBean the subgrupoBean to set
     */
    public void setSubgrupoBean(SubGruposBean subgrupoBean) {
        this.subgrupoBean = subgrupoBean;
    }

    /**
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
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
     * @return the activos
     */
    public LazyDataModel<Activos> getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(LazyDataModel<Activos> activos) {
        this.activos = activos;
    }

    /**
     * @return the listaActivos
     */
    public List<Activos> getListaActivos() {
        return listaActivos;
    }

    /**
     * @param listaActivos the listaActivos to set
     */
    public void setListaActivos(List<Activos> listaActivos) {
        this.listaActivos = listaActivos;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
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
     * @return the listaAux
     */
    public List<AuxiliarActivos> getListaAux() {
        return listaAux;
    }

    /**
     * @param listaAux the listaAux to set
     */
    public void setListaAux(List<AuxiliarActivos> listaAux) {
        this.listaAux = listaAux;
    }

    /**
     * @return the totalSumadoIngreso
     */
    public double getTotalSumadoIngreso() {
        return totalSumadoIngreso;
    }

    /**
     * @param totalSumadoIngreso the totalSumadoIngreso to set
     */
    public void setTotalSumadoIngreso(double totalSumadoIngreso) {
        this.totalSumadoIngreso = totalSumadoIngreso;
    }

    /**
     * @return the totalSumadoEgreso
     */
    public double getTotalSumadoEgreso() {
        return totalSumadoEgreso;
    }

    /**
     * @param totalSumadoEgreso the totalSumadoEgreso to set
     */
    public void setTotalSumadoEgreso(double totalSumadoEgreso) {
        this.totalSumadoEgreso = totalSumadoEgreso;
    }

    /**
     * @return the totalSumadoIngresoControl
     */
    public double getTotalSumadoIngresoControl() {
        return totalSumadoIngresoControl;
    }

    /**
     * @param totalSumadoIngresoControl the totalSumadoIngresoControl to set
     */
    public void setTotalSumadoIngresoControl(double totalSumadoIngresoControl) {
        this.totalSumadoIngresoControl = totalSumadoIngresoControl;
    }

    /**
     * @return the totalSumadoEgresoControl
     */
    public double getTotalSumadoEgresoControl() {
        return totalSumadoEgresoControl;
    }

    /**
     * @param totalSumadoEgresoControl the totalSumadoEgresoControl to set
     */
    public void setTotalSumadoEgresoControl(double totalSumadoEgresoControl) {
        this.totalSumadoEgresoControl = totalSumadoEgresoControl;
    }

    /**
     * @return the totalSumadoInicio
     */
    public double getTotalSumadoInicio() {
        return totalSumadoInicio;
    }

    /**
     * @param totalSumadoInicio the totalSumadoInicio to set
     */
    public void setTotalSumadoInicio(double totalSumadoInicio) {
        this.totalSumadoInicio = totalSumadoInicio;
    }

    /**
     * @return the totalSumadoInicioControl
     */
    public double getTotalSumadoInicioControl() {
        return totalSumadoInicioControl;
    }

    /**
     * @param totalSumadoInicioControl the totalSumadoInicioControl to set
     */
    public void setTotalSumadoInicioControl(double totalSumadoInicioControl) {
        this.totalSumadoInicioControl = totalSumadoInicioControl;
    }
}
