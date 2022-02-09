/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarMayor;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.math.BigDecimal;
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
import javax.faces.event.ValueChangeEvent;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "mayorCCSfccbdmq")
@ViewScoped
public class MayorCCBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public MayorCCBean() {
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
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioAnalisis = new Formulario();
    private Formulario formularioReporte = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CuentasFacade ejbCuentas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String auxiliar;
    private String centrocosto;
    private String cuenta;
    private String cuentaHasta;
    private Cuentas cuentaContable;
    private int indice = 0;
    private int que = 0;
    private List<Cuentas> listaCuentas;
    private double saldoInicialDeudor;
    private double saldoInicialAcreedor;
    private double saldoFinal;
    private Resource reporte;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
    private DocumentoPDF pdf;

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
        String nombreForma = "MayorCCVista";
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
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo asc");
        parametros.put(";where", "o.activo=true and o.imputable=true and o.ccosto  is not null");
        parametros.put(";inicial", 0);
        parametros.put(";final", 1);
        try {
            List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : lCuentas) {
                cuenta = c.getCodigo();
            }
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo desc");
            parametros.put(";where", "o.activo=true and o.imputable=true and o.ccosto  is not null");
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            lCuentas = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : lCuentas) {
                cuentaHasta = c.getCodigo();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String buscar() {
        auxiliar = null;

        if (((getCuentaHasta() == null) || (getCuentaHasta().isEmpty()))) {
            cuentaHasta = cuenta;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.imputable=true and o.activo=true and "
                //                + "o.codigo between :desde and :hasta and o.ccosto=true");
                + "o.codigo between :desde and :hasta and o.ccosto is not null");
        parametros.put(";orden", "o.codigo asc");
        parametros.put("desde", cuenta);
        parametros.put("hasta", cuentaHasta);
        try {
            listaMayor = new LinkedList<>();
            listaCuentas = ejbCuentas.encontarParametros(parametros);
            if (!listaCuentas.isEmpty()) {
                traerCuenta(getListaCuentas().get(0));
                cuentaContable = getListaCuentas().get(0);
            }
//            for (Cuentas c : listaCuentas) {
//
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorCCBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir() {
        try {
            if (!listaCuentas.isEmpty()) {
                pdf = new DocumentoPDF("MAYOR POR CENTRO DE COSTO\nEXPRESADO EN " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());

                for (Cuentas c : listaCuentas) {
                    traerCuentaImprimir(c);
                }
                reporte = pdf.traerRecurso();
                nombreArchivo = "Mayorcc.pdf";
                tipoArchivo = "Exportar a PDF";
                tipoMime = "application/pdf";
                formularioReporte.insertar();
            }
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorCCBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String exportar() {

        try {

            Calendar c = Calendar.getInstance();
            // 
            DocumentoXLS xls = new DocumentoXLS("Auxiliar de Mayor");
            List<AuxiliarReporte> campos = new LinkedList<>();
            AuxiliarReporte a = new AuxiliarReporte("String", "Cuenta");
            campos.add(a);
            a = new AuxiliarReporte("String", "Nombre");
            campos.add(a);
            a = new AuxiliarReporte("String", "Centro de Costo");
            campos.add(a);
            a = new AuxiliarReporte("String", "Nombre");
            campos.add(a);
            a = new AuxiliarReporte("String", "Debe");
            campos.add(a);

            xls.agregarFila(campos, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            for (Cuentas cta : listaCuentas) {
                List<AuxiliarMayor> lista = traerCuentaExportar(cta);
                for (AuxiliarMayor au : lista) {
                    campos = new LinkedList<>();
                    a = new AuxiliarReporte("String", au.getCuenta());
                    campos.add(a);
                    a = new AuxiliarReporte("String", au.getNombre());
                    campos.add(a);

                    a = new AuxiliarReporte("String", au.getReferencia());
                    campos.add(a);
                    a = new AuxiliarReporte("String", au.getEquivalencia());
                    campos.add(a);
                    a = new AuxiliarReporte("double", au.getDebe());
                    campos.add(a);
                    xls.agregarFila(campos, false);
                }
            }
            nombreArchivo = "AnexoMayor.xls";
            tipoArchivo = "Exportar a XLS";
            tipoMime = "application/xls";
            reporte = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String siguiente() {
        setIndice(getIndice() + 1);
        if (getIndice() >= getListaCuentas().size() - 1) {
            setIndice(0);
        }
        setCuentaContable(getListaCuentas().get(getIndice()));
        listaMayor = new LinkedList<>();
        traerCuenta(getCuentaContable());
        return null;
    }

    public String anterior() {
        setIndice(getIndice() - 1);
        if (getIndice() < 0) {
            setIndice(getListaCuentas().size() - 1);
        }
        setCuentaContable(getListaCuentas().get(getIndice()));
        listaMayor = new LinkedList<>();
        traerCuenta(getCuentaContable());
        return null;
    }

    public String inicio() {
        setIndice(0);
        setCuentaContable(getListaCuentas().get(getIndice()));
        listaMayor = new LinkedList<>();
        traerCuenta(getCuentaContable());
        return null;
    }

    public String fin() {
        setIndice(listaCuentas.size() - 1);
        setCuentaContable(getListaCuentas().get(getIndice()));
        listaMayor = new LinkedList<>();
        traerCuenta(getCuentaContable());
        return null;
    }

    public void valueChangeMethod(ValueChangeEvent e) {
        setIndice((int) e.getNewValue());
        if (getIndice() < 0) {
            setIndice(getListaCuentas().size() - 1);
        }
        if (getIndice() >= getListaCuentas().size() - 1) {
            setIndice(0);
        }
        setCuentaContable(getListaCuentas().get(getIndice()));
        listaMayor = new LinkedList<>();
        traerCuenta(getCuentaContable());
    }

    private void traerCuenta(Cuentas c) {
        try {
            cuentaContable = c;
            String where = "  o.fecha between :desde and :hasta and  o.centrocosto is not null and"
                    + " o.cuenta=:cuenta and o.cabecera.tipo.equivalencia.codigo not in ('C')";
//                    + " o.cuenta=:cuenta and o.cabecera.tipo.equivalencia.codigo not in ('I','C')";
            Map parametros = new HashMap();
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("cuenta", c.getCodigo());
//                parametros.put(";orden", "o.fecha");
           if (que == 0) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo");
            } else if (que == 1) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo");
            } else if (que == 2) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.fuente.codigo");
            }
            parametros.put(";suma", "sum(o.valor)");

            if (!((auxiliar == null) || (auxiliar.isEmpty()))) {
                where += " and upper(o.auxiliar)=:auxiliar";
                parametros.put("auxiliar", auxiliar.toUpperCase());
            }
            if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
                where += " and o.centrocosto=:centrocosto";
                parametros.put("centrocosto", centrocosto);
            }
            parametros.put(";where", where);
            List<Object[]> listaRenglones = ejbRenglones.sumar(parametros);
            double total = 0;
            for (Object[] o : listaRenglones) {
                AuxiliarMayor a = new AuxiliarMayor();
                a.setCuenta(c.getCodigo());
                a.setNombre(c.getNombre());
                String aux = (String) o[0];
                if (!((aux == null) || (aux.isEmpty()))) {
                    BigDecimal valor = (BigDecimal) o[1];
                    total += valor.doubleValue();
                    a.setReferencia(aux);
                    if (que == 0) {
                        Proyectos p = proyectosBean.traerCodigo(aux);
                        if (p != null) {
                            a.setEquivalencia(p.getNombre());
                        }

                    } else if (que == 1) {
                        Clasificadores clasificador = clasificadorBean.traerCodigo(aux);
                        if (clasificador != null) {
                            a.setEquivalencia(clasificador.getNombre());
                        } 
                    } else if (que == 2) {
                        Codigos cod = codigosBean.traerCodigo(Constantes.FUENTE_FINANACIAMIENTO, aux);
                        if (cod != null) {
                            a.setEquivalencia(cod.getNombre());
                        } 
                    }
                    a.setDebe(valor.doubleValue());
                    listaMayor.add(a);
                }
            }
            AuxiliarMayor a = new AuxiliarMayor();
            a.setCuenta(c.getCodigo());
            a.setNombre(c.getNombre());
            a.setEquivalencia("TOTAL");
            a.setDebe(total);
            listaMayor.add(a);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorCCBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<AuxiliarMayor> traerCuentaExportar(Cuentas c) {
        List<AuxiliarMayor> lista = new LinkedList<>();
        try {
            String where = "  o.fecha between :desde and :hasta and  o.centrocosto is not null and"
                    + " o.cuenta=:cuenta and o.cabecera.tipo.equivalencia.codigo not in ('C')";
//                    + " o.cuenta=:cuenta and o.cabecera.tipo.equivalencia.codigo not in ('I','C')";
            Map parametros = new HashMap();
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("cuenta", c.getCodigo());
//                parametros.put(";orden", "o.fecha");
           if (que == 0) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo");
            } else if (que == 1) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo");
            } else if (que == 2) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.fuente.codigo");
            }
            parametros.put(";suma", "sum(o.valor)");

            if (!((auxiliar == null) || (auxiliar.isEmpty()))) {
                where += " and upper(o.auxiliar)=:auxiliar";
                parametros.put("auxiliar", auxiliar.toUpperCase());
            }
            if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
                where += " and o.centrocosto=:centrocosto";
                parametros.put("centrocosto", centrocosto);
            }

            parametros.put(";where", where);
            List<Object[]> listaRenglones = ejbRenglones.sumar(parametros);
            double total = 0;
            for (Object[] o : listaRenglones) {
                AuxiliarMayor a = new AuxiliarMayor();
                a.setCuenta(c.getCodigo());
                a.setNombre(c.getNombre());
                String aux = (String) o[0];
                if (!((aux == null) || (aux.isEmpty()))) {
                    BigDecimal valor = (BigDecimal) o[1];
                    total += valor.doubleValue();
                    a.setReferencia(aux);
                    if (que == 0) {
                        Proyectos p = proyectosBean.traerCodigo(aux);
                        if (p != null) {
                            a.setEquivalencia(p.getNombre());
                        }

                    } else if (que == 1) {
                        Clasificadores clasificador = clasificadorBean.traerCodigo(aux);
                        if (clasificador != null) {
                            a.setEquivalencia(clasificador.getNombre());
                        } 
                    } else if (que == 2) {
                        Codigos cod = codigosBean.traerCodigo(Constantes.FUENTE_FINANACIAMIENTO, aux);
                        if (cod != null) {
                            a.setEquivalencia(cod.getNombre());
                        } 
                    }

                    a.setDebe(valor.doubleValue());
//                    Empresas empresa = proveedoreBean.taerRuc(aux);
//                    if (empresa == null) {
//                        String empleado = empleadosBean.traerCedula(aux);
//                        if (empleado == null) {
//                            a.setEquivalencia(aux);
//                        } else {
//                            a.setEquivalencia(empleado);
//                        }
//                    } else {
//                        a.setEquivalencia(empresa.toString());
//                    }
                    lista.add(a);
                }
            }
            AuxiliarMayor a = new AuxiliarMayor();
            a.setCuenta(c.getCodigo());
            a.setNombre(c.getNombre());
            a.setDebe(total);
            lista.add(a);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorCCBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    private void traerCuentaImprimir(Cuentas c) {
        try {
            String where = "  o.fecha between :desde and :hasta and  o.centrocosto is not null and"
                    + " o.cuenta=:cuenta and o.cabecera.tipo.equivalencia.codigo not in ('C')";
//                    + " o.cuenta=:cuenta and o.cabecera.tipo.equivalencia.codigo not in ('I','C')";
            Map parametros = new HashMap();
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("cuenta", c.getCodigo());
//                parametros.put(";orden", "o.fecha");
//            renglon.getDetallecompromiso().getDetallecertificacion().getAsignacion().getFuente()
            if (que == 0) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo");
            } else if (que == 1) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo");
            } else if (que == 2) {
                parametros.put(";campo", "o.detallecompromiso.detallecertificacion.asignacion.fuente.codigo");
            }

            parametros.put(";suma", "sum(o.valor)");

            if (!((auxiliar == null) || (auxiliar.isEmpty()))) {
                where += " and upper(o.auxiliar)=:auxiliar";
                parametros.put("auxiliar", auxiliar.toUpperCase());
            }
            if (!((centrocosto == null) || (centrocosto.isEmpty()))) {
                where += " and o.centrocosto=:centrocosto";
                parametros.put("centrocosto", centrocosto);
            }
            parametros.put(";where", where);
            List<Object[]> listaRenglones = ejbRenglones.sumar(parametros);
            double total = 0;
            boolean entra = false;
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referecia"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            for (Object[] o : listaRenglones) {

                String aux = (String) o[0];
                if (!((aux == null) || (aux.isEmpty()))) {
                    entra = true;
                    BigDecimal valor = (BigDecimal) o[1];
                    total += valor.doubleValue();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, aux));
                    if (que == 0) {
                        Proyectos p = proyectosBean.traerCodigo(aux);
                        if (p != null) {
                            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getNombre()));
                        } else {
                            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                        }

                    } else if (que == 1) {
                        Clasificadores clasificador = clasificadorBean.traerCodigo(aux);
                        if (clasificador != null) {
                            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, clasificador.getNombre()));
                        } else {
                            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                        }
                    } else if (que == 2) {
                        Codigos cod = codigosBean.traerCodigo(Constantes.FUENTE_FINANACIAMIENTO, aux);
                        if (cod != null) {
                            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, cod.getNombre()));
                        } else {
                            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                        }
                    }

                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor.doubleValue()));
                }
            }
            if (entra) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
                pdf.agregarTablaReporte(titulos, columnas, 3, 100, c.getCodigo() + " - " + c.getNombre());
                pdf.agregaParrafo("\n\n");
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorCCBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
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

    /**
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
    }

}
