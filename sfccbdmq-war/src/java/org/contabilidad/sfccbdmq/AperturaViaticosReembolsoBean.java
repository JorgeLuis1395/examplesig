/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ViaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */
@ManagedBean(name = "aperturaViaticosReembolsoSfccbdmq")
@ViewScoped
public class AperturaViaticosReembolsoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Viaticos> listadoViaticos;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Date desde;
    private Date hasta;
    private Viaticosempleado empleado;
    private String motivo;
    private Viaticos viatico;
    private Recurso reportepdf;

    @EJB
    private ViaticosFacade ejbViaticos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosEmpleados;

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
    }

    public AperturaViaticosReembolsoBean() {
        listadoViaticos = new LazyDataModel<Viaticos>() {

            @Override
            public List<Viaticos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoViaticos = new LazyDataModel<Viaticos>() {
            @Override
            public List<Viaticos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                String where = "o.id is not null and o.fecha between :desde and :hasta and o.reembolso=true";
                parametros.put(";orden", "o.motivo asc");

                int total;
                try {
                    if (!((motivo == null) || (motivo.isEmpty()))) {
                        where += " o.motivo like :motivo";
                        parametros.put("motivo", motivo + "%");
                    }
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbViaticos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoViaticos.setRowCount(total);
                    return ejbViaticos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String nuevo() {
        viatico = new Viaticos();
        viatico.setVigente(Boolean.TRUE);
        viatico.setEstado(0);
        viatico.setReembolso(Boolean.TRUE);
        viatico.setFecha(new Date());
        empleado = new Viaticosempleado();
        empleado.setEstado(0);
        empleado.setContrafactura(Boolean.FALSE);
        formulario.insertar();
        return null;
    }

    public String modificar() {
        try {
            viatico = (Viaticos) listadoViaticos.getRowData();
            Map parametros = new HashMap();
            parametros.put(";where", "o.viatico=:viatico");
            parametros.put("viatico", viatico);
            List<Viaticosempleado> lista = ejbViaticosEmpleados.encontarParametros(parametros);
            if (lista.isEmpty()) {
                empleado = lista.get(0);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AperturaViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String eliminar() {
        try {
            viatico = (Viaticos) listadoViaticos.getRowData();
            Map parametros = new HashMap();
            parametros.put(";where", "o.viatico=:viatico");
            parametros.put("viatico", viatico);
            List<Viaticosempleado> lista = ejbViaticosEmpleados.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                empleado = lista.get(0);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AperturaViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if (viatico.getMotivo().isEmpty() || viatico.getMotivo().trim().isEmpty()) {
            MensajesErrores.advertencia("Indique el motivo");
            return true;
        }
        if (empleadosBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbViaticos.create(viatico, seguridadbean.getLogueado().getUserid());
            empleado.setEmpleado(empleadosBean.getEmpleadoSeleccionado());
            empleado.setViatico(viatico);
            empleado.setEstado(0);
            ejbViaticosEmpleados.create(empleado, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AperturaViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbViaticos.edit(viatico, seguridadbean.getLogueado().getUserid());
            empleado.setViatico(viatico);
            empleado.setEstado(0);
            empleado.setEmpleado(empleadosBean.getEmpleadoSeleccionado());
            ejbViaticosEmpleados.edit(empleado, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AperturaViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            if (empleado != null) {
                ejbViaticosEmpleados.remove(empleado, seguridadbean.getLogueado().getUserid());
            }
            ejbViaticos.remove(viatico, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AperturaViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Viaticos traer(Integer id) throws ConsultarException {
        return ejbViaticos.find(id);
    }

    public String imprimir() {
        try {
            viatico = null;
            List<Viaticosempleado> listaEmpleados = new LinkedList<>();
            if (viatico == null) {
                viatico = (Viaticos) listadoViaticos.getRowData();
                Map parametros = new HashMap();
                parametros.put(";where", "o.viatico=:viatico");
                parametros.put("viatico", viatico);
                listaEmpleados = ejbViaticosEmpleados.encontarParametros(parametros);
            }
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafoCompleto(viatico.getMotivo(), AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafo("\n");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pdf.agregaParrafoCompleto("LUGAR DE COMISIÓN: " + viatico.getLugar(), AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafoCompleto("FECHA DE SALIDA: " + sdf.format(viatico.getDesde()), AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafoCompleto("FECHA DE LLEGADA: " + sdf.format(viatico.getHasta()), AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "CÉDULA"));
            titulos.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE DE SERVIDORES QUE INTEGRA LA COMISIÓN"));
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "CARGO"));
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "REMUNERACIÓN"));

            DecimalFormat df = new DecimalFormat("###,##0.00");
            for (Viaticosempleado p : listaEmpleados) {
                double salario = p.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
                BigDecimal sueldo = new BigDecimal(salario);
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado() != null ? p.getEmpleado().getEntidad().getPin() : ""));
                columnas.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado() != null ? p.getEmpleado().toString() : ""));
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado() != null ? p.getEmpleado().getCargoactual().getCargo().getNombre() : ""));
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, sueldo != null ? df.format(sueldo) : ""));
            }
            pdf.agregarTablaReporte(titulos, columnas, 11, 100, null);
            pdf.agregaParrafo("\n\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            reportepdf = pdf.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AperturaViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.insertar();
        return null;
    }

    /**
     * @return the listadoViaticos
     */
    public LazyDataModel<Viaticos> getListadoViaticos() {
        return listadoViaticos;
    }

    /**
     * @param listadoViaticos the listadoViaticos to set
     */
    public void setListadoViaticos(LazyDataModel<Viaticos> listadoViaticos) {
        this.listadoViaticos = listadoViaticos;
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
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
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
     * @return the empleado
     */
    public Viaticosempleado getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Viaticosempleado empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the viatico
     */
    public Viaticos getViatico() {
        return viatico;
    }

    /**
     * @param viatico the viatico to set
     */
    public void setViatico(Viaticos viatico) {
        this.viatico = viatico;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the ejbViaticos
     */
    public ViaticosFacade getEjbViaticos() {
        return ejbViaticos;
    }

    /**
     * @param ejbViaticos the ejbViaticos to set
     */
    public void setEjbViaticos(ViaticosFacade ejbViaticos) {
        this.ejbViaticos = ejbViaticos;
    }

    /**
     * @return the ejbViaticosEmpleados
     */
    public ViaticosempleadoFacade getEjbViaticosEmpleados() {
        return ejbViaticosEmpleados;
    }

    /**
     * @param ejbViaticosEmpleados the ejbViaticosEmpleados to set
     */
    public void setEjbViaticosEmpleados(ViaticosempleadoFacade ejbViaticosEmpleados) {
        this.ejbViaticosEmpleados = ejbViaticosEmpleados;
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
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
    }
}
