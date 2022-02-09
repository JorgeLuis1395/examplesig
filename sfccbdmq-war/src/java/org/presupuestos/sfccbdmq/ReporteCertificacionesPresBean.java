/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import org.beans.sfccbdmq.CertificacionespoaFacade;
import org.entidades.sfccbdmq.Certificacionespoa;
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
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author luis
 */
@ManagedBean(name = "reporteDetallePres")
@ViewScoped
public class ReporteCertificacionesPresBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Detallecertificaciones> detalleCert;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Resource reporteXls;

    @EJB
    private DetallecertificacionesFacade ejbDetalle;
    @EJB
    private CertificacionespoaFacade ejbCertificacionesPoa;

    public ReporteCertificacionesPresBean() {
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
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesPresBean.class.getName()).log(Level.SEVERE, null, ex);
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
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Numero Cert"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Numero Poa"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Aprobado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Anulado"));
            xls.agregarFila(campos, true);
            for (Detallecertificaciones c : detalleCert) {
                int nume = 0;
                String numeroPoa = null;
                if (c.getCertificacion().getPacpoa() == null) {
                    nume = 0;
                    numeroPoa = "";
                }else{
                    nume = c.getCertificacion().getPacpoa();
                    numeroPoa = traerPacpoa(nume);
                }
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCertificacion().getNumerocert() + ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, numeroPoa));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getAsignacion().getProyecto().getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getAsignacion().getClasificador().getCodigo()));
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

    public String traerPacpoa(Integer numero) {
        if (numero == null) {
            return "";
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", numero);
            List<Certificacionespoa> lista = ejbCertificacionesPoa.encontarParametros(parametros);
            for (Certificacionespoa c : lista) {
                return c != null ? (c.getNumero() + "") : "";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the detalleCert
     */
    public List<Detallecertificaciones> getDetalleCert() {
        return detalleCert;
    }

    /**
     * @param detalleCert the detalleCert to set
     */
    public void setDetalleCert(List<Detallecertificaciones> detalleCert) {
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
