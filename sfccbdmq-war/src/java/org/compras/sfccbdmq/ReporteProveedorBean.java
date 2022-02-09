/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.presupuestos.sfccbdmq.CompromisosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
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
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteProveedorSfccbdmq")
@ViewScoped
public class ReporteProveedorBean {

    /**
     * Creates a new instance of ContratosBean
     */
    public ReporteProveedorBean() {
    }
    private List<Obligaciones> obligaciones;
    private List<Compromisos> compromisos;
    private List<Certificaciones> certificaciones;
    private List<Pagosvencimientos> pagos;
    private Formulario formulario = new Formulario();
    private double valorAnticipo;
    private double valorPagado;
    private double porcetntajePagado;
    private double porcetntajeFisisco;
    private double valorCompromisos;
    private double saldoCompromisos;
    private Perfiles perfil;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private PagosvencimientosFacade ejbPagos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource recurso;
    public String buscar() {
        if (proveedorBean.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione una proveedor primero");
            return null;
        }
        Map parametros = new HashMap();
//        parametros.put(";where", "o.proveedor=:proveedor and o.contrato is null");
        parametros.put(";where", "o.proveedor=:proveedor");
        parametros.put("proveedor", proveedorBean.getProveedor());
//        parametros.put(";orden", "o.fin desc");
        try {
            obligaciones = ejbObligaciones.encontarParametros(parametros);
            compromisos = ejbCompromisos.encontarParametros(parametros);

            valorPagado = 0;
            pagos = new LinkedList<>();
            for (Obligaciones o : obligaciones) {
//                valorPagado += o.getApagar().doubleValue();
                o.setMonto(getValorObligacion(o));
                o.setValorRetenciones(getValorRetencion(o));
                o.setNoCompromiso(getNumeroCompromiso(o));
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", o);
                List<Pagosvencimientos> lp = ejbPagos.encontarParametros(parametros);
                for (Pagosvencimientos p : lp) {
                    pagos.add(p);
                }
            }
            valorCompromisos = 0;
            saldoCompromisos = 0;
            certificaciones = new LinkedList<>();
            Compromisos cTotal = new Compromisos();
            for (Compromisos c : compromisos) {

                parametros = new HashMap();
                parametros.put(";where", "o.compromiso=:compromiso");
                parametros.put("compromiso", c);
                List<Detallecompromiso> dlComp = ejbDetComp.encontarParametros(parametros);
                double total = 0;
                double saldo = 0;
                for (Detallecompromiso d : dlComp) {
                    valorCompromisos += d.getValor().doubleValue();
                    total += d.getValor().doubleValue();
                    saldoCompromisos += d.getSaldo().doubleValue();
                    saldo += d.getSaldo().doubleValue();
                    ponerCertificacion(d.getCompromiso().getCertificacion());

                }
                c.setTotal(total);
                c.setSaldo(saldo);
            }
            cTotal.setTotal(valorCompromisos);
            cTotal.setSaldo(saldoCompromisos);
            cTotal.setMotivo("Total");
            compromisos.add(cTotal);
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE CONTRATOS", null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,"Código"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,proveedorBean.getProveedor().getCodigo()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,"Nombre del Proveedor"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,proveedorBean.getProveedor().getEmpresa().toString()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,"RUC"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,proveedorBean.getProveedor().getEmpresa().getRuc()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,""));
            

            pdf.agregarTabla(null, columnas, 4, 100, null);
            // un parafo
            
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Utilizado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo"));

            columnas = new LinkedList<>();
            for (Certificaciones c : certificaciones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getId().toString()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getFecha()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getMotivo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, c.getMonto()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, c.getSaldo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, c.getMonto() - c.getSaldo()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CERTIFICACIONES");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo"));

            columnas = new LinkedList<>();
            for (Compromisos com : compromisos) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, (com.getId() == null ? "SN" : com.getId().toString())));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, com.getFecha()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, com.getMotivo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, com.getTotal()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, com.getSaldo()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "COMPROMISOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No. Comp"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "T. Doc"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Monto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "I.V.A."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "No. Rete."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Val Ret."));

            columnas = new LinkedList<>();
            for (Obligaciones o : obligaciones) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getFechaemision()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getConcepto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getNoCompromiso()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getTipodocumento() == null ? "" : o.getTipodocumento().getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getDocumento() == null ? "" : o.getDocumento().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getMonto()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getIva()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getNumeror() == null ? "" : o.getNumeror().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getValorRetenciones()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, "DETALLE FACTURAS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha E.C."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No. Comp. Eg."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha SPI"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No SPI"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            for (Pagosvencimientos p : pagos) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco()==null?null:p.getKardexbanco().getFechamov()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco()==null?"":p.getKardexbanco().getId().toString()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco()==null?null:p.getKardexbanco().getSpi() == null ? null : p.getKardexbanco().getSpi().getFecha()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco()==null?"":p.getKardexbanco().getSpi() == null ? "" : p.getKardexbanco().getSpi().getId().toString()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco()==null?"":p.getKardexbanco().getObservaciones()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, p.getValor().doubleValue()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "PAGOS");
            recurso = pdf.traerRecurso();
            
            
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void ponerCertificacion(Certificaciones c) {
        if (c==null){
            return;
        }
        for (Certificaciones c1 : certificaciones) {
            if (c.equals(c1)) {
                return;
            }
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put(";campo", "o.valor");
        parametros.put("certificacion", c);
        double valor = 0;
        try {
            valor = ejbDetCert.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(ReporteProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        c.setMonto(valor);
        certificaciones.add(c);
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
        String nombreForma = "ReporteProveedorVista";
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
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
    }

    /**
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the obligaciones
     */
    public List<Obligaciones> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(List<Obligaciones> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the valorAnticipo
     */
    public double getValorAnticipo() {
        return valorAnticipo;
    }

    /**
     * @param valorAnticipo the valorAnticipo to set
     */
    public void setValorAnticipo(double valorAnticipo) {
        this.valorAnticipo = valorAnticipo;
    }

    /**
     * @return the valorPagado
     */
    public double getValorPagado() {
        return valorPagado;
    }

    /**
     * @param valorPagado the valorPagado to set
     */
    public void setValorPagado(double valorPagado) {
        this.valorPagado = valorPagado;
    }

    /**
     * @return the porcetntajePagado
     */
    public double getPorcetntajePagado() {
        return porcetntajePagado;
    }

    /**
     * @param porcetntajePagado the porcetntajePagado to set
     */
    public void setPorcetntajePagado(double porcetntajePagado) {
        this.porcetntajePagado = porcetntajePagado;
    }

    /**
     * @return the porcetntajeFisisco
     */
    public double getPorcetntajeFisisco() {
        return porcetntajeFisisco;
    }

    /**
     * @param porcetntajeFisisco the porcetntajeFisisco to set
     */
    public void setPorcetntajeFisisco(double porcetntajeFisisco) {
        this.porcetntajeFisisco = porcetntajeFisisco;
    }

    /**
     * @return the compromisos
     */
    public List<Compromisos> getCompromisos() {
        return compromisos;
    }

    /**
     * @param compromisos the compromisos to set
     */
    public void setCompromisos(List<Compromisos> compromisos) {
        this.compromisos = compromisos;
    }

    /**
     * @return the valorCompromisos
     */
    public double getValorCompromisos() {
        return valorCompromisos;
    }

    /**
     * @param valorCompromisos the valorCompromisos to set
     */
    public void setValorCompromisos(double valorCompromisos) {
        this.valorCompromisos = valorCompromisos;
    }

    /**
     * @return the saldoCompromisos
     */
    public double getSaldoCompromisos() {
        return saldoCompromisos;
    }

    /**
     * @param saldoCompromisos the saldoCompromisos to set
     */
    public void setSaldoCompromisos(double saldoCompromisos) {
        this.saldoCompromisos = saldoCompromisos;
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

    public double getValorObligacion(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            retorno = ejbRasCompras.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getValorRetencion(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valorret+o.vretimpuesto");
        try {
            retorno = ejbRasCompras.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String getNumeroCompromiso(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        String retorno = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        try {
            List<Rascompras> rl = ejbRasCompras.encontarParametros(parametros);
            for (Rascompras r : rl) {
                if (r.getDetallecompromiso() != null) {
                    String noCompromiso = "[" + r.getDetallecompromiso().getCompromiso().getId().toString() + "]";
                    int respuesta = retorno.indexOf(noCompromiso);
                    if (respuesta == -1) {
                        retorno += noCompromiso;
                    }
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    /**
     * @return the certificaciones
     */
    public List<Certificaciones> getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(List<Certificaciones> certificaciones) {
        this.certificaciones = certificaciones;
    }

    /**
     * @return the pagos
     */
    public List<Pagosvencimientos> getPagos() {
        return pagos;
    }

    /**
     * @param pagos the pagos to set
     */
    public void setPagos(List<Pagosvencimientos> pagos) {
        this.pagos = pagos;
    }

    /**
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
    }

}