/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.contabilidad.sfccbdmq.MayorBean;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipomovbancos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteKardexSpiSfccbdmq")
@ViewScoped
public class ReporteKardexSpiBean {

    /**
     * Creates a new instance of KardexbancoBean
     */
    public ReporteKardexSpiBean() {
        listakardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Kardexbanco> listakardex;
    private List<Spi> listaSpi;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private String documento;
    private Integer egreso;
    private String observaciones;
    private Bancos banco;
    private Date desde;
    private Date hasta;
    private Double saldoFinal;
    private Double saldoInicial;
    private Tipomovbancos tipo;
    private double valor;
    private int numero;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
    private Resource reporte;
    private Formulario formularioReporte = new Formulario();
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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
     * @return the listakardex
     */
    public LazyDataModel<Kardexbanco> getListakardex() {
        return listakardex;
    }

    /**
     * @param listakardex the listakardex to set
     */
    public void setListakardex(LazyDataModel<Kardexbanco> listakardex) {
        this.listakardex = listakardex;
    }

    public String exportar() {

        Map parametros = new HashMap();

        parametros.put(";orden", "o.fechamov asc,o.tipomov.ingreso desc");
        String where = "  o.fechamov between :desde and :hasta  and o.estado>=0 and o.banco=:banco";
        try {
            if (egreso != null) {
                if (egreso > 0) {
                    where += "  and o.egreso=:egreso";
                    parametros.put("egreso", egreso);
                }
            }
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("banco", banco);
            List<Kardexbanco> lista = ejbKardexbanco.encontarParametros(parametros);
            Calendar c = Calendar.getInstance();
            // 
            DocumentoXLS xls = new DocumentoXLS("Movimientos Bancarios");
            List<AuxiliarReporte> campos = new LinkedList<>();
            AuxiliarReporte a = new AuxiliarReporte("String", "Cuenta");
            campos.add(a);
            a = new AuxiliarReporte("String", "Fecha Movimiento");
            campos.add(a);
            a = new AuxiliarReporte("String", "Tipo Movimiento");
            campos.add(a);
            a = new AuxiliarReporte("String", "Número de Comprobante");
            campos.add(a);
            a = new AuxiliarReporte("String", "Número de SPI");
            campos.add(a);
            a = new AuxiliarReporte("String", "Fecha SPI");
            campos.add(a);
            a = new AuxiliarReporte("String", "Observaciones");
            campos.add(a);
            a = new AuxiliarReporte("String", "Beneficiario");
            campos.add(a);
            a = new AuxiliarReporte("String", "Debe");
            campos.add(a);
            a = new AuxiliarReporte("String", "Haber");
            campos.add(a);
            xls.agregarFila(campos, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "SALDO INICIAL"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            if (saldoInicial > 0) {
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, saldoInicial));
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, 0.0));
            } else {
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, 0.0));
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, saldoInicial));

            }
            xls.agregarFila(campos, false);
            for (Kardexbanco au : lista) {
                campos = new LinkedList<>();
                a = new AuxiliarReporte("Date", au.getFechamov());
                campos.add(a);
                a = new AuxiliarReporte("String", au.getTipomov().toString());
                campos.add(a);
                if (au.getEgreso() != null) {
                    a = new AuxiliarReporte("Integer", au.getEgreso());
                    campos.add(a);
                } else {
                    campos.add(new AuxiliarReporte("String", ""));
                }
                if (au.getSpi() != null) {
                    a = new AuxiliarReporte("Integer", au.getSpi().getId());
                    campos.add(a);
                    campos.add(new AuxiliarReporte("Date", au.getSpi().getFecha()));
                } else {
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                }
                campos.add(new AuxiliarReporte("String", au.getObservaciones()));
                campos.add(new AuxiliarReporte("String", au.getBeneficiario()));
                if (au.getTipomov().getIngreso()) {
                    campos.add(new AuxiliarReporte("Double", au.getValor().doubleValue()));
                    campos.add(new AuxiliarReporte("Double", 0.0));
                } else {
                    campos.add(new AuxiliarReporte("Double", 0.0));
                    campos.add(new AuxiliarReporte("Double", au.getValor().doubleValue()));

                }
                xls.agregarFila(campos, false);
            }
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "SALDO FINAL"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            if (saldoFinal > 0) {
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, saldoFinal));
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, 0.0));
            } else {
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, 0.0));
                campos.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, saldoFinal));

            }
            xls.agregarFila(campos, false);
            setNombreArchivo("MovimientosBanco.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            setReporte(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

//    public String buscarSpi() {
//        String where = "  o.fecha between :desde and :hasta  and o.estado>=0 and o.banco=:banco";
//        Map parametros = new HashMap();
//        parametros.put(";orden", "o.fecha desc, o.id")
//        return null;
//    }
    public String buscar() {
        if (banco == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        saldoFinal = new Double(0);
        saldoInicial = new Double(0);
        listakardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                Map parametrosFinalDebito = new HashMap();
                Map parametrosFinalCredito = new HashMap();
                Map parametrosInicialDebito = new HashMap();
                Map parametrosInicialCredito = new HashMap();
                Calendar cInicial = Calendar.getInstance();
                cInicial.setTime(desde);
                cInicial.add(Calendar.DATE, -1);
                if (scs.length == 0) {
                    parametros.put(";orden", "o.spi.fecha desc,o.spi.id desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.spi.fecha between :desde and :hasta  and o.estado>=0 and o.banco=:banco "
                        + " and o.spi is not null";
                String whereInicialDebito = "  o.spi.fecha <=:fecha and o.estado>=0 and o.banco=:banco "
                        + "and o.tipomov.ingreso=true and o.spi is not null";
                String whereInicialCredito = "  o.spi.fecha <=:fecha and o.estado>=0 "
                        + "and o.banco=:banco and o.tipomov.ingreso=false and o.spi is not null";
                String whereFinalDebito = "  o.spi.fecha <=:fecha and o.estado>=0 and o.banco=:banco "
                        + "and o.tipomov.ingreso=true and o.spi is not null";
                String whereFinalCredito = "  o.spi.fecha <=:fecha and o.estado>=0 and o.banco=:banco "
                        + " and o.tipomov.ingreso=false"
                        + " and o.spi is not null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    Object dato = e.getValue();
                    
                    if (clave.contains("fecha")) {
                        Date valor = (Date) e.getValue();
                        where += " and o." + clave + " = :" + clave;
                        parametros.put(clave, valor);
                    } else if (!(dato instanceof String)) {
                        where += " and o." + clave + " = :" + clave;
                        parametros.put(clave, dato);
                    } else if ((dato instanceof String)) {
                        String valor = (String) e.getValue();
                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    } else {
                        where += " and o." + clave + " = :" + clave;
                        parametros.put(clave, dato);
                    }
                }
                if (egreso != null) {
                    if (egreso > 0) {
                        where += "  and o.egreso=:egreso";
                        whereInicialDebito += " and  o.egreso=:egreso";
                        whereInicialCredito += " and  o.egreso=:egreso";
                        whereFinalDebito += " and o.egreso=:egreso";
                        whereFinalCredito += " and o.egreso=:egreso";
                        parametros.put("egreso", egreso);
                        parametrosFinalDebito.put("egreso", egreso);
                        parametrosFinalCredito.put("egreso", egreso);
                        parametrosInicialDebito.put("egreso", egreso);
                        parametrosInicialCredito.put("egreso", egreso);
                    }
                }
                parametros.put("desde", desde);
//                parametrosFinal.put("desde", desde);
                parametros.put("hasta", hasta);
                parametrosFinalDebito.put("fecha", hasta);
                parametrosFinalCredito.put("fecha", hasta);
                parametrosFinalDebito.put("banco", banco);
                parametrosFinalCredito.put("banco", banco);
                parametros.put("banco", banco);
                parametrosInicialDebito.put("banco", banco);
                parametrosInicialCredito.put("banco", banco);
                parametrosInicialDebito.put("fecha", cInicial.getTime());
                parametrosInicialCredito.put("fecha", cInicial.getTime());

                int total = 0;
                try {

                    parametros.put(";where", where);
                    parametrosFinalDebito.put(";where", whereFinalDebito);
                    parametrosFinalCredito.put(";where", whereFinalCredito);
                    parametrosInicialDebito.put(";where", whereInicialDebito);
                    parametrosInicialCredito.put(";where", whereInicialCredito);
                    parametrosFinalDebito.put(";campo", "o.valor");
                    parametrosFinalCredito.put(";campo", "o.valor");
                    parametrosInicialDebito.put(";campo", "o.valor");
                    parametrosInicialCredito.put(";campo", "o.valor");
                    double siDeb = ejbKardexbanco.sumarCampo(parametrosInicialDebito).doubleValue();
                    double siCre = ejbKardexbanco.sumarCampo(parametrosInicialCredito).doubleValue();
                    saldoInicial = siDeb - siCre;
                    double sfinDeb = ejbKardexbanco.sumarCampo(parametrosFinalDebito).doubleValue();
                    double sfinCre = ejbKardexbanco.sumarCampo(parametrosFinalCredito).doubleValue();
                    saldoFinal = sfinDeb - sfinCre;
                    total = ejbKardexbanco.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }
                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                listakardex.setRowCount(total);
                try {
                    return ejbKardexbanco.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "KardexSpiVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getSeguridadbean().cerraSession();
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
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the banco
     */
    public Bancos getBanco() {
        return banco;
    }

    /**
     * @param banco the banco to set
     */
    public void setBanco(Bancos banco) {
        this.banco = banco;
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
     * @return the tipo
     */
    public Tipomovbancos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipomovbancos tipo) {
        this.tipo = tipo;
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
     * @return the numero
     */
    public int getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * @return the saldoFinal
     */
    public Double getSaldoFinal() {
        return saldoFinal;
    }

    /**
     * @param saldoFinal the saldoFinal to set
     */
    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    /**
     * @return the saldoInicial
     */
    public Double getSaldoInicial() {
        return saldoInicial;
    }

    /**
     * @param saldoInicial the saldoInicial to set
     */
    public void setSaldoInicial(Double saldoInicial) {
        this.saldoInicial = saldoInicial;
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
     * @return the egreso
     */
    public Integer getEgreso() {
        return egreso;
    }

    /**
     * @param egreso the egreso to set
     */
    public void setEgreso(Integer egreso) {
        this.egreso = egreso;
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
     * @return the listaSpi
     */
    public List<Spi> getListaSpi() {
        return listaSpi;
    }

    /**
     * @param listaSpi the listaSpi to set
     */
    public void setListaSpi(List<Spi> listaSpi) {
        this.listaSpi = listaSpi;
    }

}
