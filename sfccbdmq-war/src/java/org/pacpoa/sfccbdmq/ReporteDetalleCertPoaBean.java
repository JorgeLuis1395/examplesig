/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.errores.sfccbdmq.ConsultarException;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
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
import javax.faces.bean.ViewScoped;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.ReporteAsientoBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "reporteDetallePoa")
@ViewScoped
public class ReporteDetalleCertPoaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Detallecertificacionespoa> detalleCert;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Resource reporteXls;

    @EJB
    private DetallecertificacionespoaFacade ejbDetalle;

    public ReporteDetalleCertPoaBean() {
    }

    @PostConstruct
    private void activar() {

    }

    public String buscar() {

        Map parametros = new HashMap();
        String where = " o.id is not null";
        parametros.put(";where", where);
        parametros.put(";orden", "o.certificacion.id asc");
        try {
            detalleCert = ejbDetalle.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCertPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        try {
            if (detalleCert == null) {
                MensajesErrores.fatal("No existe Renglones");
            }
            if (detalleCert.isEmpty()) {
                MensajesErrores.fatal("No existe Cabecera");
            }
            DocumentoXLS xls = new DocumentoXLS("Detalle Certificaciones");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Numero Poa"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Aprobado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Anulado"));
            xls.agregarFila(campos, true);

            for (Detallecertificacionespoa c : detalleCert) {
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCertificacion().getNumero()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getAsignacion().getProyecto().getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getAsignacion().getPartida().getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getValor() + ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCertificacion().getImpreso() ? "SI" : "NO"));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCertificacion().getAnulado() ? "SI" : "NO"));
                xls.agregarFila(campos, false);
            }
            reporteXls = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the detalleCert
     */
    public List<Detallecertificacionespoa> getDetalleCert() {
        return detalleCert;
    }

    /**
     * @param detalleCert the detalleCert to set
     */
    public void setDetalleCert(List<Detallecertificacionespoa> detalleCert) {
        this.detalleCert = detalleCert;
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

}
