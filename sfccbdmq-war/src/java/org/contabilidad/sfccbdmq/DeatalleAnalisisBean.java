/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarMayor;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "detalleAnalisisSfccbdmq")
@ViewScoped
public class DeatalleAnalisisBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public DeatalleAnalisisBean() {
        Calendar c = Calendar.getInstance();
        renglones = new LazyDataModel<Renglones>() {

            @Override
            public List<Renglones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Renglones> renglones;
    private Renglones renglon;
    private List<AuxiliarCarga> totales;
    private List<AuxiliarMayor> listaMayor;
    private List<Cuentas> listaCuentas;
    private Cuentas cuentaContable;
    private int indice = 0;
    private int que = 0;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoreBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String auxiliar;
    private String centrocosto;
    private String partida;
    private String fuente;
    private Codigos fuenteFinanciamiento;
    private String cuenta;
    private String cuentaHasta;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
    private double saldoInicialDeudor;
    private double saldoInicialAcreedor;
    private double saldoFinal;
    private Formulario formularioReporte = new Formulario();
    private Resource reporte;

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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        String nombreForma = "DeatalleAnalisisVista";
        if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilStr));
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

    public String buscar() {
        listaMayor = new LinkedList<>();
        if (que == 0) {
            if ((auxiliar == null) || (auxiliar.isEmpty())) {
                MensajesErrores.advertencia("Seleccione un auxiliar");
                return null;
            }
            partida = null;
            centrocosto = null;
            fuente = null;
        } else if (que == 1) {
            if ((centrocosto == null) || (centrocosto.isEmpty())) {
                MensajesErrores.advertencia("Seleccione un centro de costo");
                return null;
            }
            partida = null;
            auxiliar = null;
            fuente = null;
        } else if (que == 2) {
            if ((partida == null) || (partida.isEmpty())) {
                MensajesErrores.advertencia("Seleccione una partida");
                return null;
            }
            centrocosto = null;
            auxiliar = null;
            fuente = null;
        }
        if (que == 3) {
            if ((fuenteFinanciamiento == null)) {
                MensajesErrores.advertencia("Seleccione una fuente de financiamiento");
                return null;
            }
            centrocosto = null;
            auxiliar = null;
            fuente = fuenteFinanciamiento.getCodigo();

        }
        creaMayor(listaMayor);
        return null;
    }

    public String imprimirInterno() {
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "Libro Mayor Expresado en : " + configuracionBean.getConfiguracion().getExpresado());
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        Calendar c = Calendar.getInstance();
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Mayor.jasper"),
                listaMayor, "Mayor" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        nombreArchivo = "Mayor.pdf";
        tipoArchivo = "Exportar a PDF";
        tipoMime = "application/pdf";
        formularioReporte.insertar();
        return null;
    }

    public String imprimir() {
//        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Detalle de Análisis Expresado en : " + configuracionBean.getConfiguracion().getExpresado());
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        Calendar c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Mayor.jasper"),
//                listaMayor, "Analisis" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/Mm/yyyy");
        try {
            String usuario=seguridadbean.getLogueado().getUserid();    
            String empresa=configuracionBean.getConfiguracion().getNombre();
            String titulo="DETALLE DE ANALISIS";
            String parTitulos="DESDE :" + sdf.format(desde) + 
                    " HASTA : " + sdf.format(hasta) + 
                    "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado();
            DocumentoPDF pdf;
            pdf = new DocumentoPDF(titulo, empresa, parTitulos, usuario,false);
//            DocumentoPDF pdf = new DocumentoPDF("DETALLE DE ANALISIS\n DESDE :" + sdf.format(desde) + " Hasta :" + sdf.format(hasta), seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "FECHA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CUENTA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "EQUIVALENCIA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "NUMERO"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "REFERENCIA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEBE"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "HABER"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO DEUDOR"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO ACREEDOR"));
            for (AuxiliarMayor a : listaMayor) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getFecha()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getEquivalencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTipo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getNumero()==null?"":a.getNumero().toString()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getReferencia()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getDebe()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getHaber()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getSaldoDeudor()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getSaldoAcreedor()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 10, 100, null);
            reporte = pdf.traerRecurso();
            nombreArchivo = "Analisis.pdf";
            tipoArchivo = "Exportar a PDF";
            tipoMime = "application/pdf";
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(DeatalleAnalisisBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {

        try {
            List<AuxiliarMayor> lista = new LinkedList<>();
            creaMayor(lista);
            Calendar c = Calendar.getInstance();
            // 
            DocumentoXLS xls = new DocumentoXLS("Libro Mayor ");
            List<AuxiliarReporte> campos = new LinkedList<>();
            AuxiliarReporte a = new AuxiliarReporte("String", "Cuenta");
            campos.add(a);
            a = new AuxiliarReporte("String", "Nombre");
            campos.add(a);
            a = new AuxiliarReporte("String", "Fecha");
            campos.add(a);
            a = new AuxiliarReporte("String", "Equivalencia");
            campos.add(a);
            a = new AuxiliarReporte("String", "Tipo");
            campos.add(a);
            a = new AuxiliarReporte("String", "No");
            campos.add(a);
            a = new AuxiliarReporte("String", "Referencia");
            campos.add(a);
            a = new AuxiliarReporte("String", "Auxiliar");
            campos.add(a);
            a = new AuxiliarReporte("String", "Debe");
            campos.add(a);
            a = new AuxiliarReporte("String", "Haber");
            campos.add(a);
            a = new AuxiliarReporte("String", "Saldo Deudor");
            campos.add(a);
            a = new AuxiliarReporte("String", "Saldo Acreedor");
            campos.add(a);
//            a = new AuxiliarReporte("String", true);
//            campos.add(a);
            xls.agregarFila(campos, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            for (AuxiliarMayor au : lista) {
                campos = new LinkedList<>();
                a = new AuxiliarReporte("String", au.getCuenta());
                campos.add(a);
                a = new AuxiliarReporte("String", au.getNombre());
                campos.add(a);
                if (au.getFecha() == null) {
                    a = new AuxiliarReporte("String", "");
                    campos.add(a);
                } else {
                    a = new AuxiliarReporte("String", sdf.format(au.getFecha()));
                    campos.add(a);
                }
                a = new AuxiliarReporte("String", au.getEquivalencia());
                campos.add(a);
                a = new AuxiliarReporte("String", au.getTipo());
                campos.add(a);
                if (au.getNumero() == null) {
                    a = new AuxiliarReporte("String", "");
                    campos.add(a);
                } else {
                    a = new AuxiliarReporte("String", au.getNumero().toString());
                    campos.add(a);
                }
                a = new AuxiliarReporte("String", au.getReferencia());
                campos.add(a);
                a = new AuxiliarReporte("String", au.getAuxiliar());
                campos.add(a);
                a = new AuxiliarReporte("double", au.getDebe());
                campos.add(a);
                a = new AuxiliarReporte("double", au.getHaber());
                campos.add(a);
                a = new AuxiliarReporte("double", au.getSaldoDeudor());
                campos.add(a);
                a = new AuxiliarReporte("double", au.getSaldoAcreedor());
                campos.add(a);
                xls.agregarFila(campos, false);
            }
            nombreArchivo = "Mayor.xls";
            tipoArchivo = "Exportar a XLS";
            tipoMime = "application/xls";
            reporte = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DeatalleAnalisisBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void creaMayor(List<AuxiliarMayor> lista) {
        try {
//            List<AuxiliarMayor> lista=new LinkedList<>();
            String where = "  o.fecha between :desde and :hasta and "
                    + "  o.cabecera.tipo.equivalencia.codigo not in ('I','C')";
            Map parametros = new HashMap();
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put(";orden", "o.fecha asc,o.cuenta asc");

            if (!((auxiliar == null) || (auxiliar.isEmpty()))) {
                where += " and upper(o.auxiliar)=:auxiliar";
                parametros.put("auxiliar", auxiliar.toUpperCase());
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo=:que";
                parametros.put("que", getPartida().toUpperCase());
            }
            if (!((fuente == null) || (fuente.isEmpty()))) {
                where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente.codigo=:que";
                parametros.put("que", fuente.toUpperCase());
            }
            if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
                where += " and o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo=:que";
                parametros.put("que", centrocosto.toUpperCase());
            }
            parametros.put(";where", where);
            List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
            double saldoDeudor = 0;
            double saldoAcreedor = 0;
            double saldo = saldoInicialDeudor + saldoInicialAcreedor;
            if (saldo > 0) {
                saldoDeudor = saldo;
            } else {
                saldoAcreedor = saldo * -1;
            }
            // ademas los movimientos
            if ((saldo != 0) || (!listaRenglones.isEmpty())) {
                AuxiliarMayor a = new AuxiliarMayor();
                a.setCuenta("");
                a.setNombre("");
                a.setReferencia("SALDO ANTERIOR");
                a.setDebe(Math.abs(0));
                a.setDebeNuevo(Math.abs(saldoInicialDeudor));
                a.setHaber(Math.abs(0));
                a.setHaberNuevo(Math.abs(saldoInicialAcreedor));
//                a.setSaldoDeudor(Math.abs(saldoDeudor));
                a.setSaldoDeudor(Math.abs(saldoInicialDeudor));
//                a.setSaldoAcreedor(Math.abs(saldoAcreedor));
                a.setSaldoAcreedor(Math.abs(saldoInicialAcreedor));
                lista.add(a);
                for (Renglones r : listaRenglones) {
                    a = new AuxiliarMayor();
                    Cuentas cta = cuentasBean.traerCodigo(r.getCuenta());
                    a.setCuenta(r.getCuenta());
                    a.setNombre(cta.getNombre());
                    a.setCc(r.getCentrocosto());
                    if (que == 0) {
                        a.setAuxiliar(r.getAuxiliar());
                    } else if (que == 1) {
                        a.setAuxiliar(r.getDetallecompromiso().getAsignacion().getProyecto().toString());
                    } else if (que == 2) {
                        a.setAuxiliar(r.getDetallecompromiso().getAsignacion().getClasificador().toString());
                    } else if (que == 3) {
                        a.setAuxiliar(fuenteFinanciamiento.getNombre());
                    }
                    a.setFecha(r.getFecha());
                    a.setTipo(r.getCabecera().getTipo().getCodigo());
                    a.setEquivalencia(r.getCabecera().getTipo().getEquivalencia().getCodigo());
                    a.setRubro(r.getCabecera().getTipo().getRubro().toString());
                    a.setNumero(r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    a.setCabecera(r.getCabecera());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        a.setDebe(valor);
                        a.setDebeNuevo(valor);
                        a.setHaber(0);
                        a.setHaberNuevo(0);
                        saldo += valor;
                    } else {
                        a.setDebe(0);
                        a.setDebeNuevo(0);
                        a.setHaber(valor * -1);
                        a.setHaberNuevo(valor * -1);
                        saldo += valor;
                    }
                    if (saldo > 0) {
                        a.setSaldoDeudor(Math.abs(saldo));
                    } else {
                        a.setSaldoAcreedor(Math.abs(saldo));
                    }
                    lista.add(a);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DeatalleAnalisisBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    /**
     * @return the cabeceras
     */
    public LazyDataModel<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones
     */
    public void setRenglones(LazyDataModel<Renglones> renglones) {
        this.renglones = renglones;
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
     * @return the renglon
     */
    public Renglones getRenglon() {
        return renglon;
    }

    /**
     * @param renglon the renglon to set
     */
    public void setRenglon(Renglones renglon) {
        this.renglon = renglon;
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

    private void calculaTotales() {

        Calendar c = Calendar.getInstance();
        c.setTime(desde);
        c.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        String where = "";
        parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());

        Calendar cAnioPedido = Calendar.getInstance();
        cAnioPedido.setTime(configuracionBean.getConfiguracion().getPinicial());
        Calendar cAnioPeriodo = Calendar.getInstance();
        cAnioPeriodo.setTime(configuracionBean.getConfiguracion().getPinicial());
        boolean esEsteAnio = cAnioPedido.get(Calendar.YEAR) == cAnioPeriodo.get(Calendar.YEAR);
        if (esEsteAnio) {
            where += " (( o.cabecera.tipo.equivalencia.codigo='I'"
                    + " and o.fecha between :desde and :hasta) or "
                    + "(o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<=:desde1))";
            parametros.put("hasta", hasta);
            parametros.put("desde1", c.getTime());
        } else {
            where += " o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<=:desde";
        }
        if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
            where += " and upper(o.centrocosto)=:centrocosto";
            parametros.put("centrocosto", centrocosto.toUpperCase());
        }
//        if (auxiliar.equals("1002914560")){
//            System.err.println("");
//        }
        if (!((auxiliar == null) || (auxiliar.isEmpty()))) {
            where += " and upper(o.auxiliar)=:auxiliar";
            parametros.put("auxiliar", auxiliar.toUpperCase());
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo=:que";
            parametros.put("que", getPartida().toUpperCase());
        }
        if (!((fuente == null) || (fuente.isEmpty()))) {
            where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente.codigo=:que";
            parametros.put("que", fuente.toUpperCase());
        }
        if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
            where += " and o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo=:que";
            parametros.put("que", centrocosto.toUpperCase());
        }
        try {
            parametros.put(";where", where);
            saldoInicialDeudor = ejbRenglones.sumarCampo(parametros).doubleValue();
            saldoInicialAcreedor = 0;
            if (saldoInicialDeudor < 0) {
                saldoInicialAcreedor = saldoInicialDeudor;
                saldoInicialDeudor = 0;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DeatalleAnalisisBean.class.getName()).log(Level.SEVERE, null, ex);
        }

//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
    }

    private void calculaTotalesAnterior(String cta) {
//        Quitado por que se debe calcular los si sin separar debitos i creditos
        Calendar c = Calendar.getInstance();
        c.setTime(desde);
        c.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        String whereAcreedor = "o.cuenta=:cuenta";
        Map parametrosAcreedor = new HashMap();
        parametros.put(";campo", "o.valor");
        parametrosAcreedor.put(";campo", "o.valor");
        String where = "o.cuenta=:cuenta";
        parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
        parametros.put("cuenta", cta);
        parametrosAcreedor.put("desde", c.getTime());
        parametrosAcreedor.put("cuenta", cta);

        Calendar cAnioPedido = Calendar.getInstance();
        cAnioPedido.setTime(configuracionBean.getConfiguracion().getPinicial());
        Calendar cAnioPeriodo = Calendar.getInstance();
        cAnioPeriodo.setTime(configuracionBean.getConfiguracion().getPinicial());
        boolean esEsteAnio = cAnioPedido.get(Calendar.YEAR) == cAnioPeriodo.get(Calendar.YEAR);
        if (esEsteAnio) {
//            where += " and  o.cabecera.tipo.equivalencia.codigo='I' and o.fecha between :desde and :hasta ";
            where += " and (( o.cabecera.tipo.equivalencia.codigo='I' and o.fecha between :desde and :hasta) or (o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<:desde1))";
//            whereAcreedor += " and o.cabecera.tipo.equivalencia.codigo='I' and o.fecha between :desde and :hasta";
            whereAcreedor += " and ((o.cabecera.tipo.equivalencia.codigo='I' and o.fecha between :desde and :hasta) or (o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<:desde1))";
            parametros.put("hasta", hasta);
            parametrosAcreedor.put("hasta", hasta);
            parametros.put("desde1", c.getTime());
            parametrosAcreedor.put("desde1", c.getTime());
        } else {
            where += " and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<:desde";
            whereAcreedor += " and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<:desde";
        }
        if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
            where += " and upper(o.centrocosto)=:centrocosto";
            whereAcreedor += " and upper(o.centrocosto)=:centrocosto";
            parametros.put("centrocosto", centrocosto.toUpperCase());
            parametrosAcreedor.put("centrocosto", centrocosto.toUpperCase());
        }
        try {

            where += " and o.valor>0";
            whereAcreedor += " and o.valor<0";
            parametros.put(";where", where);
            parametrosAcreedor.put(";where", whereAcreedor);
            saldoInicialDeudor = ejbRenglones.sumarCampo(parametros).doubleValue();
            saldoInicialAcreedor = ejbRenglones.sumarCampo(parametrosAcreedor).doubleValue();
            // movimientos desde el periodo vigente hasta el actual
//            totales.add(a);
//            a = new AuxiliarCarga();
//            a.setTotal("Saldo Final");
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.fecha <=:desde and o.cuenta=:cuenta");
            parametros.put("desde", hasta);
            parametros.put("cuenta", cuenta);
//            saldoFinal = ejbRenglones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DeatalleAnalisisBean.class.getName()).log(Level.SEVERE, null, ex);
        }

//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
//        a.setIngresos(new BigDecimal(Math.abs(creditos)));
//        a.setEgresos(new BigDecimal(Math.abs(debitos)));
//        totales.add(a);
    }

    /**
     * @return the totales
     */
    public List<AuxiliarCarga> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCarga> totales) {
        this.totales = totales;
    }

    public double getCreditos() {
        Renglones r = (Renglones) renglones.getRowData();
        if (r.getValor().doubleValue() > 0) {
            return r.getValor().doubleValue();
        }
        return 0;
    }

    public double getDebitos() {
        Renglones r = (Renglones) renglones.getRowData();
        if (r.getValor().doubleValue() < 0) {
            return Math.abs(r.getValor().doubleValue());
        }
        return 0;
    }

//    public double getSaldo() {
//        Integer i = renglones.getRowIndex();
//        Renglones r = (Renglones) renglones.getRowData();
//        if (renglones.getRowIndex() == 0) {
//            // saldo con saldo inicial
//            return saldoInicial + r.getValor().doubleValue();
//        }
//        renglones.setRowIndex(i - 1);
//        Renglones rAnt = (Renglones) renglones.getRowData();
//        return r.getValor().doubleValue() + rAnt.getValor().doubleValue();
//    }
    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the centrocosto
     */
    public String getCentrocosto() {
        return centrocosto;
    }

    /**
     * @param centrocosto the centrocosto to set
     */
    public void setCentrocosto(String centrocosto) {
        this.centrocosto = centrocosto;
    }

    /**
     * @return the cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String imprimirMayor(AuxiliarCarga a, Date desde, Date hasta) {
        this.desde = desde;
        this.hasta = hasta;
        this.cuenta = a.getCuenta();
        this.cuentaHasta = a.getCuenta();
        centrocosto = null;
        auxiliar = null;
        buscar();
        formularioImprimir.insertar();
        return null;
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
     * @return the proveedoreBean
     */
    public ProveedoresBean getProveedoreBean() {
        return proveedoreBean;
    }

    /**
     * @param proveedoreBean the proveedoreBean to set
     */
    public void setProveedoreBean(ProveedoresBean proveedoreBean) {
        this.proveedoreBean = proveedoreBean;
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

    /**
     * @return the listaMayor
     */
    public List<AuxiliarMayor> getListaMayor() {
        return listaMayor;
    }

    /**
     * @param listaMayor the listaMayor to set
     */
    public void setListaMayor(List<AuxiliarMayor> listaMayor) {
        this.listaMayor = listaMayor;
    }

    /**
     * @return the cuentaHasta
     */
    public String getCuentaHasta() {
        return cuentaHasta;
    }

    /**
     * @param cuentaHasta the cuentaHasta to set
     */
    public void setCuentaHasta(String cuentaHasta) {
        this.cuentaHasta = cuentaHasta;
    }

    /**
     * @return the cuentaContable
     */
    public Cuentas getCuentaContable() {
        return cuentaContable;
    }

    /**
     * @param cuentaContable the cuentaContable to set
     */
    public void setCuentaContable(Cuentas cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return the listaCuentas
     */
    public List<Cuentas> getListaCuentas() {
        return listaCuentas;
    }

    /**
     * @param listaCuentas the listaCuentas to set
     */
    public void setListaCuentas(List<Cuentas> listaCuentas) {
        this.listaCuentas = listaCuentas;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
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
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the tipoMime
     */
    public String getTipoMime() {
        return tipoMime;
    }

    /**
     * @param tipoMime the tipoMime to set
     */
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    /**
     * @return the fuenteFinanciamiento
     */
    public Codigos getFuenteFinanciamiento() {
        return fuenteFinanciamiento;
    }

    /**
     * @param fuenteFinanciamiento the fuenteFinanciamiento to set
     */
    public void setFuenteFinanciamiento(Codigos fuenteFinanciamiento) {
        this.fuenteFinanciamiento = fuenteFinanciamiento;
    }

    /**
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
    }

    /**
     * @return the que
     */
    public int getQue() {
        return que;
    }

    /**
     * @param que the que to set
     */
    public void setQue(int que) {
        this.que = que;
    }

}
