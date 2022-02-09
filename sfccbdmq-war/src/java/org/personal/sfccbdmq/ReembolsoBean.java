/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AperReposLiquiCaja;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.AperturaCajasBean;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reembolsoSfccbdmq")
@ViewScoped
public class ReembolsoBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;

    private Date desde;
    private Date hasta;
    private String concepto;
    private Cajas caja;
    private List<Valescajas> listaVales;
    private List<Valescajas> listaSeleccionadab;
    private List<Valescajas> listaSeleccionada;
    private List<Auxiliar> detalles;
    private Auxiliar detalle;
    private Valescajas vale;
    private Formulario formulario = new Formulario();
    private Formulario formularioRepo = new Formulario();
    private Perfiles perfil;
    private Resource reporteRep;
    private int numeroRep;
    private Codigos cod;
    private int anio;
    private int completo;
    private Recurso reporteARL;
    @EJB
    private ValescajasFacade ejbVales;
    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private CodigosFacade ejbCodigos;

    public ReembolsoBean() {

    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicial();
        setHasta(configuracionBean.getConfiguracion().getPfinal());
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReembolsoVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            return;
        }

//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//            }
//        }
        Date vigente = getConfiguracionBean().getConfiguracion().getPvigente();
        Calendar ca = Calendar.getInstance();
        ca.setTime(vigente);
        anio = ca.get(Calendar.YEAR);
    }

    public String buscar() {
        listaVales = new LinkedList<>();
        listaSeleccionada = new LinkedList<>();
        listaSeleccionadab = new LinkedList<>();
        Map parametros = new HashMap();
        String where = "o.caja.apertura is null and o.caja.empleado=:empleado and o.estado=:estado and o.proveedor is not null and o.fecha between :desde and :hasta ";
        parametros.put("empleado", getSeguridadbean().getLogueado().getEmpleados());
        parametros.put("estado", 0);
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        parametros.put(";where", where);
        try {
            listaVales = ejbVales.encontarParametros(parametros);
            if (!listaVales.isEmpty()) {
                formulario.insertar();
            }
            listaSeleccionada = ejbVales.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificar() {
        for (Valescajas lv : listaSeleccionadab) {
            caja = lv.getCaja();
        }
        numeroRep = 0;

        try {
            cod = ejbCodigos.traerCodigo("NUM", "08");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para apertura");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int numero = Integer.parseInt(cod.getParametros());
        int num = numero + 1;

        if (caja.getNumeroreposicion() == null) {
            numeroRep = num;
        } else {
            if (caja.getNumeroreposicion() == 0) {
                numeroRep = num;
            } else {
                numeroRep = num;
            }
        }
        formularioRepo.insertar();
        return null;
    }

    public String grabar() {
        Date vigente = getConfiguracionBean().getConfiguracion().getPvigente();
        Calendar ca = Calendar.getInstance();
        ca.setTime(vigente);
        anio = ca.get(Calendar.YEAR);
        if (numeroRep == 0) {
            MensajesErrores.advertencia("Número de Solicitud de Reposición es necesario");
            return null;
        }
//        DecimalFormat df = new DecimalFormat("0000");
//        completo = Integer.valueOf(String.valueOf(anio) + df.format(numeroRep));
        try {
//            if (numeroRep != 0) {
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.numeroapertura=:numeroreposicion or o.numeroreposicion=:numeroreposicion or o.numeroliquidacion=:numeroreposicion");
//                parametros.put("numeroreposicion", completo);
//                List<Cajas> lista = ejbCajas.encontarParametros(parametros);
//                if (!lista.isEmpty()) {
//                    MensajesErrores.advertencia("Número de Solicitud de Reposición duplicado");
//                    return null;
//                }
//            }
            for (Valescajas lv : listaSeleccionadab) {
                lv.setEstado(1);
                ejbVales.edit(lv, seguridadbean.getLogueado().getUserid());
            }
//            caja.setNumeroreposicion(completo);
            caja.setNumeroreposicion(numeroRep);
            caja.setFechareembolso(new Date());
            ejbCajas.edit(caja, seguridadbean.getLogueado().getUserid());
            cod.setParametros(numeroRep + "");
            ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        grabarEnHoja(caja);
        imprimirSolicitud(caja);
        buscar();
        formulario.cancelar();
        formularioRepo.cancelar();
        return null;
    }

    public String colocar(int i) {
        int kind = 0;
        Valescajas v = new Valescajas();
        for (Valescajas vl : listaSeleccionada) {
            kind++;
            if (vl.getId() == i) {
                v = vl;
                listaSeleccionada.remove(kind - 1);
                break;
            }
        }

        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        listaSeleccionadab.add(v);
        return null;
    }

    public String retirar(int i) {
        Valescajas v = listaSeleccionadab.get(i);
        if (listaSeleccionadab == null) {
            listaSeleccionadab = new LinkedList<>();
        }

        //listaVales.add(v);
        listaSeleccionada.add(v);
        listaSeleccionadab.remove(i);
        return null;
    }

    public String colocarTodas() {
        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        if (listaVales.size() != listaSeleccionadab.size()) {
            for (Valescajas v : listaVales) {
                listaSeleccionadab.add(v);
            }
        }
        listaSeleccionada = new LinkedList<>();
        return null;
    }

    public String retirarTodas() {
        if (listaSeleccionadab == null) {
            listaSeleccionadab = new LinkedList<>();
        }
        if (listaVales.size() != listaSeleccionada.size()) {
            for (Valescajas v : listaVales) {
                listaSeleccionada.add(v);
            }
        }

        listaSeleccionadab = new LinkedList<>();
        return null;
    }

    public String salir() {
        listaSeleccionada = new LinkedList<>();
        listaSeleccionadab = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    public String getValorSeleccionado() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double valor = 0;
        if (listaSeleccionadab == null) {
            listaSeleccionadab = new LinkedList<>();
        }
        for (Valescajas v : listaSeleccionadab) {
            valor += v.getBaseimponiblecero().doubleValue() + v.getBaseimponible().doubleValue() + v.getIva().doubleValue();
            //valor += v.getValor().doubleValue();
        }
        return df.format(valor);
    }

    public String getValorSeleccionar() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double valor = 0;
        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        for (Valescajas v : listaSeleccionada) {
            //valor += v.getValor().doubleValue()
            if (v.getBaseimponible() == null) {
                v.setBaseimponible(BigDecimal.ZERO);
            }
            if (v.getBaseimponiblecero() == null) {
                v.setBaseimponiblecero(BigDecimal.ZERO);
            }
            if (v.getIva() == null) {
                v.setIva(BigDecimal.ZERO);
            }
            valor += v.getBaseimponiblecero().doubleValue() + v.getBaseimponible().doubleValue() + v.getIva().doubleValue();
        }
        return df.format(valor);
    }

    public String grabarEnHoja(Cajas caj) {
        try {
            if (caj.getNumeroreposicion() == null) {
                caj.setNumeroreposicion(0);
            }
            AperReposLiquiCaja hoja = new AperReposLiquiCaja();
            hoja.llenar(caj, 2, listaSeleccionadab, 0);
            reporteRep = hoja.traerRecurso();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirSolicitud(Cajas caja) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            org.auxiliares.sfccbdmq.DocumentoPDF pdf = new org.auxiliares.sfccbdmq.DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD",
                    null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("SOLUCITUD DE APERTURA, REPOSICIÓN O LIQUIDACIÓN DEL FONDO FIJO DE CAJA CHICA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "FORMULARIO AF-1"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 7, false, " No "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, caja.getNumeroreposicion() != null ? caja.getNumeroreposicion() + "" : ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            columnas = new LinkedList<>();
            Date fecha;
            if (caja.getFechareembolso() == null) {
                fecha = new Date();
            } else {
                fecha = caja.getFechareembolso();
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Quito, " + sdf.format(fecha)));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Para :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, caja.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "De Estación:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, caja.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Unidad Administrativa"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaTitulo("\n");
            columnas = new LinkedList<>();
            double valor = 0;
            for (Valescajas vc : listaSeleccionadab) {
                valor += vc.getValor().doubleValue();
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "APERTURA "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "REPOSICIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "X"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO SOLICITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(valor)));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "LIQUIDAIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO DEPOSITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            pdf.agregaTitulo("\n");
            if (!listaSeleccionadab.isEmpty()) {
                List<AuxiliarReporte> titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO DE DOCUMENTO (Factura,nota de Venta,Formulario AF-2,recibo)"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "NÚMERO"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "FECHA"));
                titulos.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, true, "CONCEPTO"));
                titulos.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROVEEDOR"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "VALOR\nDESEMBOLSADO"));

                for (Valescajas vc : listaSeleccionadab) {
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getTipodocumento().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getNumero().toString()));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(vc.getFecha())));
                    columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getConcepto()));
                    columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getProveedor() != null ? (vc.getProveedor().getEmpresa() != null ? vc.getProveedor().getEmpresa().toString() : "") : ""));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(vc.getValor())));
                }

                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, "Valor Total Utilizado "));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(valor)));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, null);
            }

            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("OBSERVACIONES: " + caja.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 6, Boolean.FALSE);
            pdf.agregaTitulo("\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "RESPONSABLE DEL FONDO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "JEFE INMEDIATO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre: " + caja.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "cc: " + caja.getEmpleado().getEntidad().getPin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "cc: "));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteARL = pdf.traerRecurso();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the listaVales
     */
    public List<Valescajas> getListaVales() {
        return listaVales;
    }

    /**
     * @param listaVales the listaVales to set
     */
    public void setListaVales(List<Valescajas> listaVales) {
        this.listaVales = listaVales;
    }

    /**
     * @return the detalles
     */
    public List<Auxiliar> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Auxiliar> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the detalle
     */
    public Auxiliar getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Auxiliar detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the vale
     */
    public Valescajas getVale() {
        return vale;
    }

    /**
     * @param vale the vale to set
     */
    public void setVale(Valescajas vale) {
        this.vale = vale;
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
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
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
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the proveedorsBean
     */
    public ProveedoresBean getProveedorsBean() {
        return proveedorsBean;
    }

    /**
     * @param proveedorsBean the proveedorsBean to set
     */
    public void setProveedorsBean(ProveedoresBean proveedorsBean) {
        this.proveedorsBean = proveedorsBean;
    }

    /**
     * @return the listaSeleccionadab
     */
    public List<Valescajas> getListaSeleccionadab() {
        return listaSeleccionadab;
    }

    /**
     * @param listaSeleccionadab the listaSeleccionadab to set
     */
    public void setListaSeleccionadab(List<Valescajas> listaSeleccionadab) {
        this.listaSeleccionadab = listaSeleccionadab;
    }

    /**
     * @return the listaSeleccionada
     */
    public List<Valescajas> getListaSeleccionada() {
        return listaSeleccionada;
    }

    /**
     * @param listaSeleccionada the listaSeleccionada to set
     */
    public void setListaSeleccionada(List<Valescajas> listaSeleccionada) {
        this.listaSeleccionada = listaSeleccionada;
    }

    /**
     * @return the reporteRep
     */
    public Resource getReporteRep() {
        return reporteRep;
    }

    /**
     * @param reporteRep the reporteRep to set
     */
    public void setReporteRep(Resource reporteRep) {
        this.reporteRep = reporteRep;
    }

    /**
     * @return the formularioRepo
     */
    public Formulario getFormularioRepo() {
        return formularioRepo;
    }

    /**
     * @param formularioRepo the formularioRepo to set
     */
    public void setFormularioRepo(Formulario formularioRepo) {
        this.formularioRepo = formularioRepo;
    }

    /**
     * @return the numeroRep
     */
    public int getNumeroRep() {
        return numeroRep;
    }

    /**
     * @param numeroRep the numeroRep to set
     */
    public void setNumeroRep(int numeroRep) {
        this.numeroRep = numeroRep;
    }

    /**
     * @return the cod
     */
    public Codigos getCod() {
        return cod;
    }

    /**
     * @param cod the cod to set
     */
    public void setCod(Codigos cod) {
        this.cod = cod;
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
     * @return the completo
     */
    public int getCompleto() {
        return completo;
    }

    /**
     * @param completo the completo to set
     */
    public void setCompleto(int completo) {
        this.completo = completo;
    }

    /**
     * @return the reporteARL
     */
    public Recurso getReporteARL() {
        return reporteARL;
    }

    /**
     * @param reporteARL the reporteARL to set
     */
    public void setReporteARL(Recurso reporteARL) {
        this.reporteARL = reporteARL;
    }

}
