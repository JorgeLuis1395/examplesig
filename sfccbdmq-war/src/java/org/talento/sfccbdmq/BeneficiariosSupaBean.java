/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
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
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.BeneficiariossupaFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EmpresasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.ProveedoresFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.ContabilizarRolBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Beneficiariossupa;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.pacpoa.sfccbdmq.AsignacionesPoaBean;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.pagos.sfccbdmq.PagoConceptoProveedorBean;
import org.pagos.sfccbdmq.PagoRolEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "beneficiariosSupaSfccbdmq")
@ViewScoped
public class BeneficiariosSupaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioCopiar = new Formulario();
    private Formulario formularioCargar = new Formulario();
    private Formulario formularioDescargar = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioBorrar = new Formulario();
    private Perfiles perfil;
    private List<Beneficiariossupa> listaSupa = new LinkedList<>();
    private List<Beneficiariossupa> listaNoDescontados = new LinkedList<>();
    private List<Pagosempleados> listaAPagar = new LinkedList<>();
    private List errores = new LinkedList<>();
    private List<Beneficiariossupa> listaCarga = new LinkedList<>();
    private List<Empleados> listaUsuarios;
    private List<Renglones> renglonesBeneficiarios;
    private List<Renglones> renglonesPrestado;
    private List<Renglones> renglonesAñosAnteriores;
    private Beneficiariossupa beneficiario;
    private Proveedores proveedor;
    private int tipoBenef;
    private int mes;
    private int anio;
    private int mesNuevo;
    private int anioNuevo;
    private int anioAnterior = 0;
    private int mesAnterior = 0;
    private String separador = ";";
    private String apellidos;
    private Empleados empleadoSeleccionado;
    private Entidades entidad = new Entidades();
    private Resource reporteXls;
    private Kardexbanco kardex;
    private Integer numeroSpi;
    private Resource reporte;
    private Resource reportePropuesta;
    private double total;
    private String codigoAsiento;
    private double rolTotal;
    private double saldoTotal;
    private Conceptos concepto;
    private Conceptos conceptoExtra;

    @EJB
    private BeneficiariossupaFacade ejbBeneficiariossupa;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private EmpresasFacade ejbEmpresas;
    @EJB
    private ProveedoresFacade ejbProveedores;
    @EJB
    private ConceptosFacade ejbconConceptos;
    @EJB
    private PagosempleadosFacade ejbPagosempleados;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private SpiFacade ejbSpi;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private FormatosFacade ejbFormatos;

    public BeneficiariosSupaBean() {

    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "Beneficiarios SUPA";
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
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        mes = c.get(Calendar.MONTH);
        anio = c.get(Calendar.YEAR);
    }

    public String buscar() {
        try {
            if (mes == 0) {
                MensajesErrores.advertencia("Ingrese el mes");
                return null;
            }
            if (anio == 0) {
                MensajesErrores.advertencia("Ingrese el año");
                return null;
            }
            rolTotal = 0;
            saldoTotal = 0;
            Map parametros = new HashMap();
            String where = "o.mes=:mes and o.anio=:anio";
            if (conceptoExtra != null) {
                where += " and o.conceptoextra=:conceptoextra";
                parametros.put("conceptoextra", conceptoExtra);
            } else {
                where += " and o.conceptoextra is null";
            }
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos");
            listaSupa = ejbBeneficiariossupa.encontarParametros(parametros);

            for (Beneficiariossupa bf : listaSupa) {
                bf.setRol(traerValorRol(bf.getConcepto(), bf));
                bf.setSaldo(traerSaldoAnterior(bf));
            }

            parametros = new HashMap();
            String whereT = "o.mes=:mes and o.anio=:anio";
            parametros.put(";campo", "o.valor");
            if (conceptoExtra != null) {
                whereT += " and o.conceptoextra=:conceptoextra";
                parametros.put("conceptoextra", conceptoExtra);
            } else {
                whereT += " and o.conceptoextra is null";
            }
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";where", whereT);
            double totalR = ejbBeneficiariossupa.sumarCampo(parametros).doubleValue();
            Beneficiariossupa benTotal = new Beneficiariossupa();
            benTotal.setNombrebeneficiario("Total");
            benTotal.setValor(new BigDecimal(totalR));
            benTotal.setRol(rolTotal);
            benTotal.setSaldo(saldoTotal);
            listaSupa.add(benTotal);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CursosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        beneficiario = new Beneficiariossupa();
        beneficiario.setMes(mes);
        beneficiario.setAnio(anio);
        empleadoBean.setEmpleadoAdicional(null);
        entidad = null;
        empleadoSeleccionado = null;
        proveedoresBean.setEmpresa(null);
        proveedoresBean.setProveedor(null);
        proveedoresBean.setTipobuscar("");
        apellidos = "";
        tipoBenef = 0;

        formulario.insertar();
        return null;
    }

    public String modificar(Beneficiariossupa ben) {

        try {
            beneficiario = ben;
            if (beneficiario.getTipobeneficiario() != null) {
                if (beneficiario.getTipobeneficiario().equals("E")) {
                    tipoBenef = 1;
                } else {
                    tipoBenef = -1;

                }
            } else {
                tipoBenef = 0;
            }
            proveedoresBean.setProveedor(null);
            proveedoresBean.setEmpresa(null);
            entidad = null;
            empleadoSeleccionado = null;
            if (beneficiario.getCedulabeneficiario() != null) {
                Map parametros = new HashMap();
                if (beneficiario.getTipobeneficiario().equals("P")) {
                    parametros.put(";where", "o.empresa.ruc=:ruc");
                    parametros.put("ruc", beneficiario.getCedulabeneficiario());
                    List<Proveedores> listaBeneficiario2 = ejbProveedores.encontarParametros(parametros);
                    Proveedores em = null;
                    if (!listaBeneficiario2.isEmpty()) {
                        em = listaBeneficiario2.get(0);
                        proveedoresBean.setProveedor(em);
                        proveedoresBean.getProveedor().setEmpresa(em.getEmpresa());
                    } else {
                        proveedoresBean.setProveedor(null);
                        proveedoresBean.setEmpresa(null);
                    }
                } else {
                    if (beneficiario.getTipobeneficiario().equals("P")) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.entidad.pin=:pin");
                        parametros.put("pin", beneficiario.getCedulabeneficiario());
                        List<Empleados> listaBeneficiario = ejbEmpleados.encontarParametros(parametros);
                        if (!listaBeneficiario.isEmpty()) {
                            Empleados em = listaBeneficiario.get(0);
                            entidad = em.getEntidad();
                            empleadoSeleccionado = em;
                        }
                    } else {
                        entidad = null;
                        empleadoSeleccionado = null;
                    }
                }
            } else {
                proveedoresBean.setProveedor(null);
                proveedoresBean.setEmpresa(null);
                entidad = null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String borrar(Beneficiariossupa ben) {
        try {
            beneficiario = ben;
            if (beneficiario.getTipobeneficiario() != null) {
                if (beneficiario.getTipobeneficiario().equals("E")) {
                    tipoBenef = 1;
                } else {
                    tipoBenef = -1;

                }
            } else {
                tipoBenef = 0;
            }
            if (beneficiario.getCedulabeneficiario() != null) {
                Map parametros = new HashMap();
                if (beneficiario.getTipobeneficiario().equals("P")) {
                    parametros.put(";where", "o.empresas.ruc=:ruc");
                    parametros.put("ruc", beneficiario.getCedulabeneficiario());
                    List<Proveedores> listaBeneficiario2 = ejbProveedores.encontarParametros(parametros);
                    Proveedores em = null;
                    if (!listaBeneficiario2.isEmpty()) {
                        em = listaBeneficiario2.get(0);
                        proveedoresBean.setProveedor(em);
                        proveedoresBean.getProveedor().setEmpresa(em.getEmpresa());
                    } else {
                        proveedoresBean.setProveedor(null);
                        proveedoresBean.setEmpresa(null);

                    }
                } else {
                    if (beneficiario.getTipobeneficiario().equals("P")) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.entidad.pin=:pin");
                        parametros.put("pin", beneficiario.getCedulabeneficiario());
                        List<Empleados> listaBeneficiario = ejbEmpleados.encontarParametros(parametros);
                        if (!listaBeneficiario.isEmpty()) {
                            Empleados em = listaBeneficiario.get(0);
                            entidad = em.getEntidad();
                        }
                    } else {
                        entidad = null;
                    }
                }
            } else {
                proveedoresBean.setProveedor(null);
                entidad = null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public boolean validar() {
        if (beneficiario.getCedulabeneficiario() == null || (beneficiario.getCedulabeneficiario().isEmpty())) {
            MensajesErrores.advertencia("Ingrese cédula del beneficiario");
            return true;
        }
        if (beneficiario.getNombrebeneficiario() == null || (beneficiario.getNombrebeneficiario().isEmpty())) {
            MensajesErrores.advertencia("Ingrese nombre del beneficiario");
            return true;
        }
        if (beneficiario.getValor() == null) {
            MensajesErrores.advertencia("Ingres un valor");
            return true;
        }
//        if (beneficiario.getCodigossupa() == null) {
//            MensajesErrores.advertencia("Ingrese el código SUPA");
//            return true;
//        }
        if (beneficiario.getMes() == null) {
            MensajesErrores.advertencia("Ingrese el mes");
            return true;
        }
        if (beneficiario.getMes() == 0) {
            MensajesErrores.advertencia("Ingrese el mes");
            return true;
        }
        if (beneficiario.getAnio() == null) {
            MensajesErrores.advertencia("Ingrese el año");
            return true;
        }
        if (beneficiario.getAnio() == 0) {
            MensajesErrores.advertencia("Ingrese el año");
            return true;
        }
        if (beneficiario.getConcepto() == null) {
            MensajesErrores.advertencia("Seleccione un concepto");
            return true;
        }
        if (tipoBenef == 0) {
            MensajesErrores.advertencia("Seleccione un Tipo de beneficiario");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (entidad != null) {
            beneficiario.setNombrebeneficiario(entidad.toString());
            beneficiario.setCedulabeneficiario(entidad.getPin());
        } else {
            if (proveedoresBean.getEmpresa() != null) {
                beneficiario.setNombrebeneficiario(proveedoresBean.getEmpresa().getNombre());
                beneficiario.setCedulabeneficiario(proveedoresBean.getEmpresa().getRuc());
            }
        }
        if (validar()) {
            return null;
        }
        beneficiario.setEmpleado(empleadoBean.getEmpleadoAdicional());
        if (tipoBenef == 1) {
            beneficiario.setTipobeneficiario("E");
        } else {
            beneficiario.setTipobeneficiario("P");

        }
        try {
            ejbBeneficiariossupa.create(beneficiario, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabar() {
        if (entidad != null) {
            beneficiario.setNombrebeneficiario(entidad.toString());
            beneficiario.setCedulabeneficiario(entidad.getPin());
        } else {
            if (proveedoresBean.getEmpresa() != null) {
                beneficiario.setNombrebeneficiario(proveedoresBean.getEmpresa().getNombre());
                beneficiario.setCedulabeneficiario(proveedoresBean.getEmpresa().getRuc());
            }
        }
        if (validar()) {
            return null;
        }
        if (tipoBenef == 1) {
            beneficiario.setTipobeneficiario("E");
        } else {
            beneficiario.setTipobeneficiario("P");

        }
        try {
            ejbBeneficiariossupa.edit(beneficiario, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String eliminar() {
        try {
            ejbBeneficiariossupa.remove(beneficiario, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String copiar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id is not null");
            parametros.put(";orden", "o.anio desc, o.mes desc");
            List<Beneficiariossupa> list = ejbBeneficiariossupa.encontarParametros(parametros);
            if (!list.isEmpty()) {
                Beneficiariossupa c = list.get(0);
                mesAnterior = c.getMes();
                anioAnterior = c.getAnio();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCopiar.insertar();
        return null;
    }

    public String grabarCopiar() {
        try {
            mesNuevo = mesAnterior + 1;
            anioNuevo = anioAnterior;
            if (mesNuevo == 13) {
                mesNuevo = 1;
                anioNuevo = anioNuevo + 1;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio");
            parametros.put("mes", mesAnterior);
            parametros.put("anio", anioAnterior);
            List<Beneficiariossupa> listacopiar = ejbBeneficiariossupa.encontarParametros(parametros);
            for (Beneficiariossupa bs : listacopiar) {
                Beneficiariossupa nuevoBeneficiario = new Beneficiariossupa();
                nuevoBeneficiario.setEmpleado(bs.getEmpleado());
                nuevoBeneficiario.setCedulabeneficiario(bs.getCedulabeneficiario());
                nuevoBeneficiario.setNombrebeneficiario(bs.getNombrebeneficiario());
                nuevoBeneficiario.setConcepto(bs.getConcepto());
                nuevoBeneficiario.setValor(bs.getValor());
                nuevoBeneficiario.setCodigossupa(bs.getCodigossupa());
                nuevoBeneficiario.setMes(mesNuevo);
                nuevoBeneficiario.setAnio(anioNuevo);
                nuevoBeneficiario.setTipobeneficiario(bs.getTipobeneficiario());
                if (conceptoExtra != null) {
                    nuevoBeneficiario.setConceptoextra(conceptoExtra);
                }
                ejbBeneficiariossupa.create(nuevoBeneficiario, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCopiar.cancelar();
        return null;
    }

    public String cargar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id is not null");
            parametros.put(";orden", "o.anio desc, o.mes desc");
            List<Beneficiariossupa> list = ejbBeneficiariossupa.encontarParametros(parametros);
            if (!list.isEmpty()) {
                Beneficiariossupa c = list.get(0);
                mesAnterior = c.getMes();
                anioAnterior = c.getAnio();

                mesNuevo = mesAnterior + 1;
                anioNuevo = anioAnterior;
                if (mesNuevo == 13) {
                    mesNuevo = 1;
                    anioNuevo = anioNuevo + 1;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCargar.insertar();
        return null;
    }

    public String grabarCarga() {
        try {
            if (!errores.isEmpty()) {
                MensajesErrores.advertencia("Existen errores no se puede grabar");
                return null;
            }
            if (listaCarga.isEmpty()) {
                MensajesErrores.advertencia("No existen datos para grabar");
                return null;
            }
            if (listaCarga == null) {
                MensajesErrores.advertencia("No existen datos para grabar");
                return null;
            }
            if (mesNuevo == 0) {
                MensajesErrores.advertencia("Ingrese el mes");
                return null;
            }
            if (anioNuevo == 0) {
                MensajesErrores.advertencia("Ingrese el año");
                return null;
            }
            for (Beneficiariossupa bs : listaCarga) {
                Beneficiariossupa nuevoBeneficiario = new Beneficiariossupa();
                nuevoBeneficiario.setEmpleado(bs.getEmpleado());
                nuevoBeneficiario.setCedulabeneficiario(bs.getCedulabeneficiario());
                nuevoBeneficiario.setNombrebeneficiario(bs.getNombrebeneficiario());
                nuevoBeneficiario.setConcepto(bs.getConcepto());
                nuevoBeneficiario.setValor(bs.getValor());
                nuevoBeneficiario.setCodigossupa(bs.getCodigossupa());
                nuevoBeneficiario.setMes(mesNuevo);
                nuevoBeneficiario.setAnio(anioNuevo);
                nuevoBeneficiario.setTipobeneficiario(bs.getTipobeneficiario());
                if (conceptoExtra != null) {
                    nuevoBeneficiario.setConceptoextra(conceptoExtra);
                }
                ejbBeneficiariossupa.create(nuevoBeneficiario, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCargar.cancelar();
        return null;
    }

    public String traer(Empleados e, String que) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto=:que and o.empleado=:empleado");
            paremetros.put("empleado", e);
            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getCodigo() + " - " + codigo.getNombre();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void ubicar(Beneficiariossupa b, String titulo, String valor) {

        if (titulo.contains("empleado")) {
            b.setCedulaEmpleado(valor);
        } else if (titulo.contains("beneficiario")) {
            b.setCedulabeneficiario(valor);
        } else if (titulo.contains("concepto")) {
            b.setConceptoStr(valor);
        } else if (titulo.contains("valor")) {
            valor = valor.replace(",", ".");
            b.setValor(new BigDecimal(valor));
        } else if (titulo.contains("codigosupa")) {
            b.setCodigossupa(valor);
        } else if (titulo.contains("tipo")) {
            b.setTipobeneficiario(valor.toUpperCase());
        }
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        //get data About File
        listaCarga = new LinkedList<>();
        List<Beneficiariossupa> listaCargaP = new LinkedList<>();
        errores = new LinkedList();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                File file = i.getFile();
                if (file != null) {
                    try {
                        parent = file.getParentFile();
                        BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        String sb = entrada.readLine();
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            registro++;
                            Beneficiariossupa bene = new Beneficiariossupa();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(bene, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            if (bene.getValor() == null) {
                                getErrores().add("Valor no valido ");
                            }
                            if (bene.getCedulaEmpleado() == null || bene.getCedulaEmpleado().trim().isEmpty()) {
                                getErrores().add("Cédula empleado no válida ");
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.entidad.pin=:pin and o.activo=true and o.entidad.activo = true");
                                parametros.put("pin", bene.getCedulaEmpleado());
                                List<Empleados> listaEmpleado = ejbEmpleados.encontarParametros(parametros);
                                if (!listaEmpleado.isEmpty()) {
                                    Empleados em = listaEmpleado.get(0);
                                    bene.setEmpleado(em);
                                } else {
                                    getErrores().add("Cédula sin empleado " + bene.getCedulaEmpleado());
                                }
                                if (bene.getTipobeneficiario() == null || bene.getTipobeneficiario().trim().isEmpty()) {
                                    getErrores().add("Tipo de Benefeciario no válido ");
                                } else {
                                    if (bene.getCedulabeneficiario() == null || bene.getCedulabeneficiario().trim().isEmpty()) {
                                        getErrores().add("Cédula de beneficiario no válido ");
                                    } else {
                                        if (bene.getTipobeneficiario().equals("E")) {
                                            parametros = new HashMap();
                                            parametros.put(";where", "o.entidad.pin=:pin");
                                            parametros.put("pin", bene.getCedulabeneficiario());
                                            List<Empleados> listaBeneficiario = ejbEmpleados.encontarParametros(parametros);
                                            if (!listaBeneficiario.isEmpty()) {
                                                Empleados em = listaBeneficiario.get(0);
                                                bene.setNombrebeneficiario(em.toString());
                                            } else {
                                                bene.setNombrebeneficiario("");
                                            }
                                        }
                                        if (bene.getTipobeneficiario().equals("P")) {
                                            parametros = new HashMap();
                                            parametros.put(";where", "o.ruc=:ruc");
                                            parametros.put("ruc", bene.getCedulabeneficiario());
                                            List<Empresas> listaBeneficiario2 = ejbEmpresas.encontarParametros(parametros);
                                            if (!listaBeneficiario2.isEmpty()) {
                                                Empresas em = listaBeneficiario2.get(0);
                                                bene.setNombrebeneficiario(em.getNombre());
                                            } else {
                                                bene.setNombrebeneficiario("");
                                            }
                                        }
                                        if (bene.getConceptoStr() == null || bene.getConceptoStr().trim().isEmpty()) {
                                            getErrores().add("Concepto no valido ");
                                        } else {
                                            parametros = new HashMap();
                                            parametros.put(";where", "o.codigo=:codigo and o.retencion=true and o.activo=true");
                                            parametros.put("codigo", bene.getConceptoStr());
                                            List<Conceptos> listaConceptos = ejbconConceptos.encontarParametros(parametros);
                                            if (!listaConceptos.isEmpty()) {
                                                Conceptos em = listaConceptos.get(0);
                                                bene.setConcepto(em);
                                            } else {
                                                getErrores().add("Concepto no valido " + bene.getConceptoStr());
                                            }
                                        }
                                    }
                                }
                            }
                            listaCargaP.add(bene);
                            estaEnBeneficiario(bene);

                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ConsultarException ex) {
                        Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
                }
            }

        }
    }

    private boolean estaEnBeneficiario(Beneficiariossupa r1) {
        for (Beneficiariossupa r : listaCarga) {
            if ((r.getEmpleado().equals(r1.getEmpleado())) && (r.getCedulabeneficiario().equals(r1.getCedulabeneficiario()))
                    && (r.getConcepto().equals(r1.getConcepto()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }
        listaCarga.add(r1);
        return false;
    }

    public void cambiaApellido(ValueChangeEvent event) {
        entidad = null;
        empleadoSeleccionado = null;
        if (listaUsuarios == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Empleados e : listaUsuarios) {
            if (e.getEntidad().toString().compareToIgnoreCase(newWord) == 0) {
                empleadoSeleccionado = e;
                entidad = empleadoSeleccionado.getEntidad();
            }
        }

    }

    public void empleadoChangeEventHandler(TextChangeEvent event) {
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = "";
        where += " upper(o.entidad.apellidos) like :apellidos";
        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.entidad.apellidos");
        int total = 50;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaUsuarios = ejbEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public double traerValorRol(Conceptos concep, Beneficiariossupa emple) {
        try {
            double valorRol = 0;
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.empleado=:empleado and o.concepto=:concepto and o.mes=:mes and o.anio=:anio");
            paremetros.put("empleado", emple.getEmpleado());
            paremetros.put("concepto", concep);
            paremetros.put("mes", emple.getMes());
            paremetros.put("anio", emple.getAnio());
            List<Pagosempleados> lista = ejbPagosempleados.encontarParametros(paremetros);
            for (Pagosempleados c : lista) {
                valorRol = c.getValor().doubleValue();
                rolTotal += valorRol;
            }
            return valorRol;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerSaldoAnterior(Beneficiariossupa emple) {
        try {
            if (concepto != null) {
                return 0;
            }
            Codigos cuenta1 = codigosBean.traerCodigo("CCRJ", "CRJ");
            if (cuenta1 == null) {
                MensajesErrores.advertencia("No existe cuenta de cruce en Códigos");
                return 0;
            }
            if (cuenta1.getDescripcion() == null) {
                MensajesErrores.advertencia("No existe cuenta de cruce descripción");
                return 0;
            }
            String partida = emple.getEmpleado().getPartida().substring(0, 2);
            String cuentaCompleta = cuenta1.getDescripcion() + partida;

            Calendar desde = Calendar.getInstance();
            anio = desde.get(Calendar.YEAR);
            desde.set(anio, 0, 1);

            Calendar hasta = Calendar.getInstance();
            hasta.set(anio, mes, 1);
            hasta.add(Calendar.DATE, -1);

            Date fechaDesde = desde.getTime();
            Date fechaHasta = hasta.getTime();
            double valorSA = 0;
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.auxiliar=:auxiliar and  o.cuenta=:cuenta and o.fecha between :desde and :hasta");
            paremetros.put("auxiliar", emple.getCedulabeneficiario());
            paremetros.put("cuenta", cuentaCompleta);
            paremetros.put("desde", fechaDesde);
            paremetros.put("hasta", fechaHasta);
            paremetros.put(";campo", "o.valor * o.signo");
            valorSA = ejbRenglones.sumarCampo(paremetros).doubleValue();
            if (valorSA < 0) {
                valorSA = valorSA * -1;
            } else {
                return 0;
            }
            saldoTotal += valorSA;
            return valorSA;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerSaldoAnteriorTotal() {
        try {
            Codigos cuenta1 = codigosBean.traerCodigo("CCRJ", "CRJ");
            if (cuenta1 == null) {
                MensajesErrores.advertencia("No existe cuenta de cruce en Códigos");
                return 0;
            }
            if (cuenta1.getDescripcion() == null) {
                MensajesErrores.advertencia("No existe cuenta de cruce descripción");
                return 0;
            }
            double valorST = 0;
            for (Beneficiariossupa bf : listaSupa) {
                if (bf.getId() != null) {
                    String partida = bf.getEmpleado().getPartida().substring(0, 2);
                    String cuentaCompleta = cuenta1.getDescripcion() + partida;

                    Calendar desde = Calendar.getInstance();
                    anio = desde.get(Calendar.YEAR);
                    desde.set(anio, 0, 1);

                    Calendar hasta = Calendar.getInstance();
                    hasta.set(anio, mes, 1);
                    hasta.add(Calendar.DATE, -1);

                    Date fechaDesde = desde.getTime();
                    Date fechaHasta = hasta.getTime();
                    double valorSA = 0;
                    Map paremetros = new HashMap();
                    paremetros.put(";where", "o.auxiliar=:auxiliar and  o.cuenta=:cuenta and o.fecha between :desde and :hasta");
                    paremetros.put("auxiliar", bf.getCedulabeneficiario());
                    paremetros.put("cuenta", cuentaCompleta);
                    paremetros.put("desde", fechaDesde);
                    paremetros.put("hasta", fechaHasta);
                    paremetros.put(";campo", "o.valor * o.signo");
                    valorSA = ejbRenglones.sumarCampo(paremetros).doubleValue();
                    if (valorSA < 0) {
                        valorST += valorSA;
                    }
                }
            }
            return valorST * -1;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String exportar() {
        try {
            buscar();
            if (listaSupa == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("Beneficiarios");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "empleado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "nombre"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "beneficiario"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "nombre"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "concepto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "valor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "codigosupa"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tipo"));
            xls.agregarFila(campos, true);
            for (Beneficiariossupa e : listaSupa) {
                if (e != null) {
                    campos = new LinkedList<>();
                    if (e != null) {
                        if (e.getValor() == null) {
                            e.setValor(BigDecimal.ZERO);
                        }
                        DecimalFormat df = new DecimalFormat("########0.00");
                        campos.add(new AuxiliarReporte("String", e.getEmpleado() != null ? (e.getEmpleado().getEntidad().getPin()) : " "));
                        campos.add(new AuxiliarReporte("String", e.getEmpleado() != null ? (e.getEmpleado().getEntidad().toString()) : " "));
                        campos.add(new AuxiliarReporte("String", e.getCedulabeneficiario() != null ? (e.getCedulabeneficiario()) : " "));
                        campos.add(new AuxiliarReporte("String", e.getNombrebeneficiario() != null ? e.getNombrebeneficiario() : " "));
                        campos.add(new AuxiliarReporte("String", e.getConcepto() != null ? (e.getConcepto().getCodigo()) : " "));
                        campos.add(new AuxiliarReporte("String", df.format(e.getValor())));
                        campos.add(new AuxiliarReporte("String", e.getCodigossupa() != null ? (e.getCodigossupa()) : " "));
                        campos.add(new AuxiliarReporte("String", e.getTipobeneficiario() != null ? (e.getTipobeneficiario()) : " "));
                        xls.agregarFila(campos, false);
                    }
                }
            }
            setReporteXls(xls.traerRecurso());
            formularioDescargar.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String contalizar() {
        try {
            kardex = new Kardexbanco();
            kardex.setUsuariograba(seguridadbean.getLogueado());
            kardex.setFechamov(configuracionBean.getConfiguracion().getPvigente());
            kardex.setValor(new BigDecimal(total));
            Map parametros = new HashMap();
            parametros.put(";campo", "o.numero");
            setNumeroSpi(ejbSpi.maximoNumero(parametros));

            if ((getNumeroSpi() == null) || (getNumeroSpi() == 0)) {
                setNumeroSpi((Integer) 307);
            }
            setNumeroSpi((Integer) (getNumeroSpi() + 1));
            renglonesBeneficiarios = new LinkedList<>();
            renglonesPrestado = new LinkedList<>();
            renglonesAñosAnteriores = new LinkedList<>();
            buscar();
            Codigos cuenta1 = codigosBean.traerCodigo("CCRJ", "CRJ");
            if (cuenta1 == null) {
                MensajesErrores.advertencia("No existe cuenta de cruce en Códigos");
                return null;
            }
            if (cuenta1.getDescripcion() == null) {
                MensajesErrores.advertencia("No existe cuenta de cruce descripción");
                return null;
            }
            if (cuenta1.getParametros() == null) {
                MensajesErrores.advertencia("No existe cuenta de cruce parámetros");
                return null;
            }
            String cuentaEmpl = "";//1128001000
            String cuentaPresta = "";//2120301001
            String tipoAsiento = "";//32
            String añosAnteriores = "";//21398010
            if (cuenta1.getParametros() != null) {
                String[] campos = cuenta1.getParametros().split("#");
                cuentaEmpl = campos[0];
                cuentaPresta = campos[1];
                tipoAsiento = campos[2];
                añosAnteriores = campos[3];
            }

            codigoAsiento = tipoAsiento;
            Cuentas cuenta2 = getCuentasBean().traerCodigo(cuentaEmpl);
            if (cuenta2 == null) {
                MensajesErrores.advertencia("No existe cuenta : " + cuentaEmpl);
                return null;
            }
            Cuentas cuenta3 = getCuentasBean().traerCodigo(cuentaPresta);
            if (cuenta3 == null) {
                MensajesErrores.advertencia("No existe cuenta : " + cuentaPresta);
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            total = 0;
            for (Beneficiariossupa bf : listaSupa) {
                if (bf.getId() != null) {
                    if(bf.getEmpleado().getEntidad().getPin().equals("1707870109")){
                        System.out.println();
                    }
                        
                    String partida = bf.getEmpleado().getPartida().substring(0, 2);
                    String cuentaCompleta = cuenta1.getDescripcion() + partida; //22498010

                    Cuentas cuenta = getCuentasBean().traerCodigo(cuentaCompleta);
                    if (cuenta == null) {
                        MensajesErrores.advertencia("No existe cuenta Inicial : " + cuentaCompleta
                                + " Empleado " + bf.getEmpleado().getEntidad().toString() + " Cta : " + cuentaCompleta);
                        return null;
                    }
                    String añosAnterioresCompleta = añosAnteriores + partida;
                    Cuentas cuenta5 = getCuentasBean().traerCodigo(añosAnterioresCompleta);
                    if (cuenta5 == null) {
                        MensajesErrores.advertencia("No existe cuenta : " + añosAnterioresCompleta);
                        return null;
                    }

                    String cuentaPagar = ctaInicio + partida + ctaFin;
                    Cuentas cuenta4 = getCuentasBean().traerCodigo(cuentaPagar);
                    if (cuenta4 == null) {
                        MensajesErrores.advertencia("No existe por pagar : " + cuentaPagar
                                + " Empleado " + bf.getEmpleado().getEntidad().toString() + " Cta : " + cuentaPagar);
                        return null;
                    }

                    double cuadre1 = Math.round(bf.getValor().doubleValue() * 100);
                    double dividido1 = cuadre1 / 100;
                    BigDecimal valortotal1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);

                    double valorRol = traerValorRol(bf.getConcepto(), bf);
                    double cuadre2 = Math.round(valorRol * 100);
                    double dividido2 = cuadre2 / 100;
                    BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);

                    double diferencia2 = valortotal1.doubleValue() - valortotal2.doubleValue();

                    if (diferencia2 <= 0) {
                        total += bf.getValor().doubleValue();
                        Renglones r = new Renglones();
                        r.setCuenta(cuentaPagar);
                        r.setValor(bf.getValor());
                        r.setSigno(1);
                        r.setReferencia("Cxp Beneficiario: " + bf.getNombrebeneficiario());

                        if (cuenta4.getAuxiliares() != null) {
                            r.setAuxiliar(bf.getCedulabeneficiario());
                        }
                        r.setNombre(cuenta4.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        renglonesBeneficiarios.add(r);
                    } else {
                        double valorSA = traerSaldoAnterior(bf);
                        double cuadre3 = Math.round(valorSA * 100);
                        double dividido3 = cuadre3 / 100;
                        BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);

                        double diferencia3 = valortotal3.doubleValue() - diferencia2;
                        //Asiento que se crea si esq hay diferencia y Saldo inicial pero no le alcanza a para
                        // o por que no tiene de donde pagar la diferencia
                        //La diferencia queda en negativo y para contabilizar tiene q ser positiva
                        if (diferencia3 < 0) {
                            Renglones r = new Renglones();
                            r.setCuenta(cuentaEmpl);
                            r.setValor(new BigDecimal(diferencia3 * -1));
                            r.setSigno(1);
                            r.setReferencia("Cxp Empleado: " + bf.getEmpleado().getEntidad().toString());

                            if (cuenta2.getAuxiliares() != null) {
                                r.setAuxiliar(bf.getEmpleado().getEntidad().getPin());
                            }
                            r.setNombre(cuenta2.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            renglonesPrestado.add(r);
                            r = new Renglones();
                            r.setCuenta(cuentaPresta);
                            r.setValor(new BigDecimal(diferencia3));
                            r.setSigno(1);
                            r.setReferencia("Cxp Beneficiario: " + bf.getNombrebeneficiario());

                            if (cuenta3.getAuxiliares() != null) {
                                r.setAuxiliar(bf.getCedulabeneficiario());
                            }
                            r.setNombre(cuenta3.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            renglonesPrestado.add(r);
                        }
                        double valorSaldoI = 0;
                        valorSaldoI += valortotal2.doubleValue();
                        total += valortotal2.doubleValue();
                        Renglones r = new Renglones();
                        r.setCuenta(cuentaPagar);
                        r.setValor(valortotal2);
                        r.setSigno(1);
                        r.setReferencia("Cxp Beneficiario: " + bf.getNombrebeneficiario());

                        if (cuenta4.getAuxiliares() != null) {
                            r.setAuxiliar(bf.getCedulabeneficiario());
                        }
                        r.setNombre(cuenta4.getNombre());
                        if (r.getValor().doubleValue() > 0) {
                            r.setDebitos(r.getValor().doubleValue());
                        } else {
                            r.setDebitos(r.getValor().doubleValue() * -1);
                        }
                        renglonesBeneficiarios.add(r);
                        //La diferencia queda en negativo y para contabilizar tiene q ser positiva
                        if (diferencia3 < 0) {
                            double diferencia3P = diferencia3 * -1;
                            valorSaldoI += diferencia3P;
                            total += diferencia3P;
                            r = new Renglones();
                            r.setCuenta(cuentaPresta);
                            r.setValor(new BigDecimal(diferencia3P));
                            r.setSigno(1);
                            r.setReferencia("Cxp Beneficiario: " + bf.getNombrebeneficiario());

                            if (cuenta3.getAuxiliares() != null) {
                                r.setAuxiliar(bf.getCedulabeneficiario());
                            }
                            r.setNombre(cuenta3.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            renglonesBeneficiarios.add(r);
                        }
                        if (valortotal3.doubleValue() != 0) {
                            double valorFinal = 0;
                            valorFinal = valortotal1.doubleValue() - valorSaldoI;
                            total += valorFinal;

                            r = new Renglones();
                            r.setCuenta(cuentaCompleta);
                            r.setValor(new BigDecimal(valorFinal));
                            r.setSigno(1);
                            r.setReferencia("Cxp Beneficiario: " + bf.getNombrebeneficiario());
                            if (cuenta.getAuxiliares() != null) {
                                r.setAuxiliar(bf.getCedulabeneficiario());
                            }
                            r.setNombre(cuenta.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            renglonesAñosAnteriores.add(r);

                            r = new Renglones();
                            r.setCuenta(añosAnterioresCompleta);
                            r.setValor(new BigDecimal(valorFinal * -1));
                            r.setSigno(1);
                            r.setReferencia("Cxp Beneficiario: " + bf.getNombrebeneficiario());
                            if (cuenta.getAuxiliares() != null) {
                                r.setAuxiliar(bf.getCedulabeneficiario());
                            }
                            r.setNombre(cuenta.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            renglonesAñosAnteriores.add(r);

                            r = new Renglones();
                            r.setCuenta(añosAnterioresCompleta);
                            r.setValor(new BigDecimal(valorFinal));
                            r.setSigno(1);
                            r.setReferencia("Cxp Beneficiario: " + bf.getNombrebeneficiario());
                            if (cuenta.getAuxiliares() != null) {
                                r.setAuxiliar(bf.getCedulabeneficiario());
                            }
                            r.setNombre(cuenta.getNombre());
                            if (r.getValor().doubleValue() > 0) {
                                r.setDebitos(r.getValor().doubleValue());
                            } else {
                                r.setDebitos(r.getValor().doubleValue() * -1);
                            }
                            renglonesBeneficiarios.add(r);

                        }
                    }
                }
            }

            Renglones rBanco = new Renglones();
            rBanco.setAuxiliar(null);
            rBanco.setCentrocosto(null);
            rBanco.setCuenta("Banco");
            rBanco.setValor(new BigDecimal(total * -1));
            rBanco.setSigno(1);
            renglonesBeneficiarios.add(rBanco);

            //Renglones Beneficiario
            double tdebe = 0;
            double cred = 0;
            for (Renglones r : renglonesBeneficiarios) {
                Cuentas cuen = getCuentasBean().traerCodigo(r.getCuenta());
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());

                }
                r.setSigno(1);
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
            }
            Renglones r1 = new Renglones();
            if (!renglonesBeneficiarios.isEmpty()) {

                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                renglonesBeneficiarios.add(r1);
            }
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesPrestado) {
                Cuentas cuen = getCuentasBean().traerCodigo(r.getCuenta());
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());

                }
                r.setSigno(1);
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
            }
            Renglones rp = new Renglones();
            if (!renglonesPrestado.isEmpty()) {

                rp.setReferencia("TOTAL");
                rp.setDebitos(tdebe);
                rp.setCreditos(cred);
                rp.setSigno(1);
                renglonesPrestado.add(rp);
            }
            tdebe = 0;
            cred = 0;
            for (Renglones r : renglonesAñosAnteriores) {
                Cuentas cuen = getCuentasBean().traerCodigo(r.getCuenta());
                if (r.getValor().doubleValue() > 0) {
                    r.setDebitos(r.getValor().abs().doubleValue());
                    r.setCreditos(new Double(0));
                } else {
                    r.setDebitos(new Double(0));
                    r.setCreditos(r.getValor().abs().doubleValue());

                }
                r.setSigno(1);
                tdebe += r.getDebitos() == null ? 0 : r.getDebitos();
                cred += r.getCreditos() == null ? 0 : r.getCreditos();
            }
            Renglones ra = new Renglones();
            if (!renglonesAñosAnteriores.isEmpty()) {

                ra.setReferencia("TOTAL");
                ra.setDebitos(tdebe);
                ra.setCreditos(cred);
                ra.setSigno(1);
                renglonesAñosAnteriores.add(rp);
            }
            getFormularioContabilizar().insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "OK";
    }

    public String insertarKardex() {
        if (getKardex().getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        if ((getKardex().getObservaciones() == null) || (getKardex().getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Necesario un observaciones");
            return null;
        }
        String vale = ejbCabeceras.validarCierre(getKardex().getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        if (renglonesBeneficiarios.isEmpty()) {
            MensajesErrores.advertencia("No existe nada para contabilizar");
            return null;
        }
        // sumar para ver si esta cuadardo
        double valor = 0;
        for (Renglones r : renglonesBeneficiarios) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        for (Renglones r : renglonesPrestado) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        for (Renglones r : renglonesAñosAnteriores) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento descuadrado no se puede grabar");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigoAsiento);
            List<Tipoasiento> lista = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento ta = null;
            if (!lista.isEmpty()) {
                ta = lista.get(0);
            } else {
                MensajesErrores.advertencia("No existe tipo de asiento para Retenciones");
                return null;
            }

            for (Beneficiariossupa bf : listaSupa) {
                if (bf.getId() != null) {
                    if (bf.getCedulabeneficiario() != null) {
                        parametros = new HashMap();
                        if (bf.getTipobeneficiario().equals("P")) {
                            parametros.put(";where", "o.empresa.ruc=:ruc");
                            parametros.put("ruc", bf.getCedulabeneficiario());
                            List<Proveedores> listaBeneficiario2 = ejbProveedores.encontarParametros(parametros);
                            Proveedores em = null;
                            if (!listaBeneficiario2.isEmpty()) {
                                em = listaBeneficiario2.get(0);
                                if (em.getBanco() == null) {
                                    MensajesErrores.advertencia("No existe Banco para: " + bf.getNombrebeneficiario());
                                    return null;
                                }
                                if (em.getCtabancaria() == null) {
                                    MensajesErrores.advertencia("No existe Cuenta para: " + bf.getNombrebeneficiario());
                                    return null;
                                }
                                if (em.getTipocta() == null) {
                                    MensajesErrores.advertencia("No existe Tipo Cuenta para: " + bf.getNombrebeneficiario());
                                    return null;
                                }
                            }
                        } else {
                            parametros = new HashMap();
                            parametros.put(";where", "o.entidad.pin=:pin");
                            parametros.put("pin", bf.getCedulabeneficiario());
                            List<Empleados> listaBeneficiario = ejbEmpleados.encontarParametros(parametros);
                            if (!listaBeneficiario.isEmpty()) {
                                Empleados em = listaBeneficiario.get(0);
                                Codigos codigoBanco = traerBanco(em);
                                String numeroCuenta = traerNC(em, "NUMCUENTA");
                                String tipoCuenta = traerTipoCuenta(em);
                                if (codigoBanco == null) {
                                    MensajesErrores.advertencia("No existe Banco para: " + bf.getNombrebeneficiario());
                                    return null;
                                }
                                if (numeroCuenta == null) {
                                    MensajesErrores.advertencia("No existe Cuenta para: " + bf.getNombrebeneficiario());
                                    return null;
                                }
                                if (tipoCuenta == null) {
                                    MensajesErrores.advertencia("No existe Tipo Cuenta para: " + bf.getNombrebeneficiario());
                                    return null;
                                }
                            }
                        }
                    }
                }
            }

            Spi spi = new Spi();
            spi.setEstado(0);
            spi.setFecha(getKardex().getFechamov());
            spi.setNumero(getNumeroSpi());
            spi.setUsuario(seguridadbean.getLogueado().getUserid());
            spi.setBanco(getKardex().getBanco());
            if (getKardex().getTipomov().getSpi()) {
                ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());
            }
            DecimalFormat df1 = new DecimalFormat("000");
            DecimalFormat df2 = new DecimalFormat("000");
            int i = 0;
            Kardexbanco k = null;
            for (Beneficiariossupa bf : listaSupa) {
                if (bf.getId() != null) {
                    System.out.println("Id beneficiario : " + bf.getId());
                    k = new Kardexbanco();
                    k.setUsuariograba(seguridadbean.getLogueado());
                    k.setBanco(getKardex().getBanco());
                    k.setBeneficiario(bf.getNombrebeneficiario());
                    if (bf.getCedulabeneficiario() != null) {
                        parametros = new HashMap();
                        if (bf.getTipobeneficiario().equals("P")) {
                            parametros.put(";where", "o.empresa.ruc=:ruc");
                            parametros.put("ruc", bf.getCedulabeneficiario());
                            List<Proveedores> listaBeneficiario2 = ejbProveedores.encontarParametros(parametros);
                            Proveedores em = null;
                            if (!listaBeneficiario2.isEmpty()) {
                                em = listaBeneficiario2.get(0);
                                k.setBancotransferencia(em.getBanco());
                                k.setCuentatrans(em.getCtabancaria());
                                k.setTcuentatrans(Integer.parseInt(em.getTipocta().getParametros()));
                            }
                        } else {
                            parametros = new HashMap();
                            parametros.put(";where", "o.entidad.pin=:pin");
                            parametros.put("pin", bf.getCedulabeneficiario());
                            List<Empleados> listaBeneficiario = ejbEmpleados.encontarParametros(parametros);
                            if (!listaBeneficiario.isEmpty()) {
                                Empleados em = listaBeneficiario.get(0);
                                Codigos codigoBanco = traerBanco(em);
                                String numeroCuenta = traerNC(em, "NUMCUENTA");
                                String tipoCuenta = traerTipoCuenta(em);
                                k.setBancotransferencia(codigoBanco);
                                k.setCuentatrans(numeroCuenta);
                                k.setTcuentatrans(Integer.parseInt(tipoCuenta));
                            }
                        }
                        k.setEgreso(Integer.parseInt(String.valueOf(anio).substring(2) + df2.format(spi.getNumero()) + "" + df1.format(++i)));
//                        k.setCodigospi("40111");
                        k.setCodigospi("40101");
                        k.setFechaabono(getKardex().getFechamov());
                        k.setFechagraba(new Date());
                        getKardex().setOrigen("PAGO BENEFICIARIO SUPA");
                        k.setOrigen("PAGO BENEFICIARIO SUPA");
                        k.setFechamov(getKardex().getFechamov());
                        k.setFormapago(getKardex().getFormapago());
                        k.setDocumento(bf.getCedulabeneficiario());
                        k.setEstado(1);
                        k.setValor(bf.getValor());
                        k.setObservaciones(getKardex().getObservaciones());
                        k.setTipomov(getKardex().getTipomov());
                        k.setSpi(spi);
                        k.setPropuesta(numeroSpi + "");
                        ejbKardex.create(k, seguridadbean.getLogueado().getUserid());
                        bf.setKardex(k);
                        ejbBeneficiariossupa.edit(bf, seguridadbean.getLogueado().getUserid());
                    }
                }
            }

            int numero = ta.getUltimo();
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(kardex.getObservaciones());
            cab.setDia(new Date());
            cab.setFecha(kardex.getFechamov());
            cab.setIdmodulo(spi.getId());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setNumero(numero);
            cab.setOpcion("BENEFICIARIO_SUPA");
            cab.setTipo(ta);
            cab.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
            for (Renglones r : renglonesBeneficiarios) {
                if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                } else {
                    if (r.getCuenta().equals("Banco")) {
                        r.setCuenta(kardex.getBanco().getCuenta());
                    }
                    r.setCabecera(cab);
                    r.setFecha(kardex.getFechamov());
                    r.setReferencia(kardex.getObservaciones());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Renglones r : renglonesPrestado) {
                if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                } else {
                    if (r.getCuenta().equals("Banco")) {
                        r.setCuenta(kardex.getBanco().getCuenta());
                    }
                    r.setCabecera(cab);
                    r.setFecha(kardex.getFechamov());
                    r.setReferencia(kardex.getObservaciones());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Renglones r : renglonesAñosAnteriores) {
                if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                } else {
                    if (r.getCuenta().equals("Banco")) {
                        r.setCuenta(kardex.getBanco().getCuenta());
                    }
                    r.setCabecera(cab);
                    r.setFecha(kardex.getFechamov());
                    r.setReferencia(kardex.getObservaciones());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            imprimirKardex();
        } catch (InsertarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoConceptoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        renglonesBeneficiarios = new LinkedList<>();
        renglonesPrestado = new LinkedList<>();
        renglonesAñosAnteriores = new LinkedList<>();
        formularioContabilizar.cancelar();
        return null;
    }

    public Codigos traerBanco(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                return codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerTipoCuenta(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='TIPOCUENTA' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo == null ? "" : codigo.getParametros();
                }
                return retorno;

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerNC(Empleados e, String que) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto=:que and o.empleado=:empleado");
            paremetros.put("empleado", e);
            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    if (codigo == null) {
                        return "SIN BANCO";
                    }
                    return codigo.getCodigo() + " - " + codigo.getNombre();
                }
                return retorno;

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirKardex() {
        try {
            buscar();
            Kardexbanco k = null;
            if (listaSupa.isEmpty()) {
                MensajesErrores.advertencia("No existen datos");
                return null;
            } else {
                k = listaSupa.get(0).getKardex();
            }
            if (k == null) {
                MensajesErrores.advertencia("No existe contabilización");
                return null;
            }

            renglonesBeneficiarios = new LinkedList<>();
            renglonesBeneficiarios = traerRenglones(k.getSpi());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n");
            pdf.agregaTitulo("COMPROBANTE DE PAGO BENEFICIARIOS - " + k.getEgreso());
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBanco().toString()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(k.getFechamov())));

            columnas.add(new AuxiliarReporte("String", "Valor:"));
            columnas.add(new AuxiliarReporte("String", ConvertirNumeroALetras.convertNumberToLetter(total)));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", df.format(total)));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo(k.getObservaciones());
            pdf.agregaParrafo("\n\n");
            // asiento
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            //
            columnas = new LinkedList<>();
            for (Renglones r : renglonesBeneficiarios) {
                String cuenta = "";
                String auxiliar = r.getAuxiliar() != null ? r.getAuxiliar() : "";
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                if (cta != null) {
                    cuenta = cta.getNombre();

                }
                if (!auxiliar.isEmpty()) {
                    Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                    if (p != null) {
                        auxiliar = p.toString();

                    }
                    if (!auxiliar.isEmpty()) {
                        String e = empleadoBean.traerCedula(r.getAuxiliar());
                        if (e != null) {
                            auxiliar = e;

                        }
                    }
                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));
                double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
                if (valor > 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    sumaDebitos += valor;
                } else {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                    sumaCreditos += valor * -1;
                }

            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");

            // disponible el pagos
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporte(pdf.traerRecurso());
            imprimir(k);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir(Kardexbanco k1) {
        try {

            String observacionPropuesta = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            observacionPropuesta = k1.getPropuesta() + "";
            DocumentoPDF pdf = new DocumentoPDF("PROPUESTA DE PAGO  " + observacionPropuesta, null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            double valorTotal = 0;
            for (Beneficiariossupa bf : listaSupa) {
                if (bf.getKardex() != null) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(bf.getKardex().getFechamov())));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, bf.getKardex().getObservaciones()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    String numeroCuenta = bf.getKardex().getCuentatrans();
                    String tipoCuenta = bf.getKardex().getTcuentatrans().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, bf.getKardex().getBeneficiario()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, bf.getKardex().getBancotransferencia().toString()));
                    /////////////////////FIN EMPLEADO
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                            bf.getKardex().getValor().doubleValue()));
                    valorTotal += bf.getKardex().getValor().doubleValue();
                }
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, null);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReportePropuesta(pdf.traerRecurso());
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());
        }
    }

    private List<Renglones> traerRenglones(Spi spi) {
        if (spi != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id  and o.cabecera.opcion='BENEFICIARIO_SUPA'");
            parametros.put("id", spi.getId());
            parametros.put(";orden", "o.id asc");
            try {
                return ejbRenglones.encontarParametros(parametros);

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String borrarTodo() {
        formularioBorrar.insertar();
        return null;
    }

    public String eliminarTodo() {
        try {
            Map parametros = new HashMap();
            String where = "o.mes=:mes and o.anio=:anio";
            if (conceptoExtra != null) {
                where += " and o.conceptoextra=:conceptoextra";
                parametros.put("conceptoextra", conceptoExtra);
            }
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put(";where", where);
            List<Beneficiariossupa> lista = ejbBeneficiariossupa.encontarParametros(parametros);
            for (Beneficiariossupa bs : lista) {
                ejbBeneficiariossupa.remove(bs, seguridadbean.getLogueado().getUserid());
            }
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BeneficiariosSupaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioBorrar.cancelar();
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
     * @return the listaSupa
     */
    public List<Beneficiariossupa> getListaSupa() {
        return listaSupa;
    }

    /**
     * @param listaSupa the listaSupa to set
     */
    public void setListaSupa(List<Beneficiariossupa> listaSupa) {
        this.listaSupa = listaSupa;
    }

    /**
     * @return the beneficiario
     */
    public Beneficiariossupa getBeneficiario() {
        return beneficiario;
    }

    /**
     * @param beneficiario the beneficiario to set
     */
    public void setBeneficiario(Beneficiariossupa beneficiario) {
        this.beneficiario = beneficiario;
    }

    /**
     * @return the proveedor
     */
    public Proveedores getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @return the tipoBenef
     */
    public int getTipoBenef() {
        return tipoBenef;
    }

    /**
     * @param tipoBenef the tipoBenef to set
     */
    public void setTipoBenef(int tipoBenef) {
        this.tipoBenef = tipoBenef;
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
     * @return the mesNuevo
     */
    public int getMesNuevo() {
        return mesNuevo;
    }

    /**
     * @param mesNuevo the mesNuevo to set
     */
    public void setMesNuevo(int mesNuevo) {
        this.mesNuevo = mesNuevo;
    }

    /**
     * @return the anioNuevo
     */
    public int getAnioNuevo() {
        return anioNuevo;
    }

    /**
     * @param anioNuevo the anioNuevo to set
     */
    public void setAnioNuevo(int anioNuevo) {
        this.anioNuevo = anioNuevo;
    }

    /**
     * @return the formularioCopiar
     */
    public Formulario getFormularioCopiar() {
        return formularioCopiar;
    }

    /**
     * @param formularioCopiar the formularioCopiar to set
     */
    public void setFormularioCopiar(Formulario formularioCopiar) {
        this.formularioCopiar = formularioCopiar;
    }

    /**
     * @return the anioAnterior
     */
    public int getAnioAnterior() {
        return anioAnterior;
    }

    /**
     * @param anioAnterior the anioAnterior to set
     */
    public void setAnioAnterior(int anioAnterior) {
        this.anioAnterior = anioAnterior;
    }

    /**
     * @return the mesAnterior
     */
    public int getMesAnterior() {
        return mesAnterior;
    }

    /**
     * @param mesAnterior the mesAnterior to set
     */
    public void setMesAnterior(int mesAnterior) {
        this.mesAnterior = mesAnterior;
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
     * @return the formularioCargar
     */
    public Formulario getFormularioCargar() {
        return formularioCargar;
    }

    /**
     * @param formularioCargar the formularioCargar to set
     */
    public void setFormularioCargar(Formulario formularioCargar) {
        this.formularioCargar = formularioCargar;
    }

    /**
     * @return the listaCarga
     */
    public List<Beneficiariossupa> getListaCarga() {
        return listaCarga;
    }

    /**
     * @param listaCarga the listaCarga to set
     */
    public void setListaCarga(List<Beneficiariossupa> listaCarga) {
        this.listaCarga = listaCarga;
    }

    /**
     * @return the separador
     */
    public String getSeparador() {
        return separador;
    }

    /**
     * @param separador the separador to set
     */
    public void setSeparador(String separador) {
        this.separador = separador;
    }

    /**
     * @return the errores
     */
    public List getErrores() {
        return errores;
    }

    /**
     * @param errores the errores to set
     */
    public void setErrores(List errores) {
        this.errores = errores;
    }

    /**
     * @return the listaUsuarios
     */
    public List<Empleados> getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List<Empleados> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the empleadoSeleccionado
     */
    public Empleados getEmpleadoSeleccionado() {
        return empleadoSeleccionado;
    }

    /**
     * @param empleadoSeleccionado the empleadoSeleccionado to set
     */
    public void setEmpleadoSeleccionado(Empleados empleadoSeleccionado) {
        this.empleadoSeleccionado = empleadoSeleccionado;
    }

    /**
     * @return the entidad
     */
    public Entidades getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
    }

    /**
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
    }

    /**
     * @return the listaAPagar
     */
    public List<Pagosempleados> getListaAPagar() {
        return listaAPagar;
    }

    /**
     * @param listaAPagar the listaAPagar to set
     */
    public void setListaAPagar(List<Pagosempleados> listaAPagar) {
        this.listaAPagar = listaAPagar;
    }

    /**
     * @return the listaNoDescontados
     */
    public List<Beneficiariossupa> getListaNoDescontados() {
        return listaNoDescontados;
    }

    /**
     * @param listaNoDescontados the listaNoDescontados to set
     */
    public void setListaNoDescontados(List<Beneficiariossupa> listaNoDescontados) {
        this.listaNoDescontados = listaNoDescontados;
    }

    /**
     * @return the formularioDescargar
     */
    public Formulario getFormularioDescargar() {
        return formularioDescargar;
    }

    /**
     * @param formularioDescargar the formularioDescargar to set
     */
    public void setFormularioDescargar(Formulario formularioDescargar) {
        this.formularioDescargar = formularioDescargar;
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
     * @return the formularioContabilizar
     */
    public Formulario getFormularioContabilizar() {
        return formularioContabilizar;
    }

    /**
     * @param formularioContabilizar the formularioContabilizar to set
     */
    public void setFormularioContabilizar(Formulario formularioContabilizar) {
        this.formularioContabilizar = formularioContabilizar;
    }

    /**
     * @return the kardex
     */
    public Kardexbanco getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexbanco kardex) {
        this.kardex = kardex;
    }

    /**
     * @return the numeroSpi
     */
    public Integer getNumeroSpi() {
        return numeroSpi;
    }

    /**
     * @param numeroSpi the numeroSpi to set
     */
    public void setNumeroSpi(Integer numeroSpi) {
        this.numeroSpi = numeroSpi;
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
     * @return the reportePropuesta
     */
    public Resource getReportePropuesta() {
        return reportePropuesta;
    }

    /**
     * @param reportePropuesta the reportePropuesta to set
     */
    public void setReportePropuesta(Resource reportePropuesta) {
        this.reportePropuesta = reportePropuesta;
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
     * @return the renglonesBeneficiarios
     */
    public List<Renglones> getRenglonesBeneficiarios() {
        return renglonesBeneficiarios;
    }

    /**
     * @param renglonesBeneficiarios the renglonesBeneficiarios to set
     */
    public void setRenglonesBeneficiarios(List<Renglones> renglonesBeneficiarios) {
        this.renglonesBeneficiarios = renglonesBeneficiarios;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * @return the codigoAsiento
     */
    public String getCodigoAsiento() {
        return codigoAsiento;
    }

    /**
     * @param codigoAsiento the codigoAsiento to set
     */
    public void setCodigoAsiento(String codigoAsiento) {
        this.codigoAsiento = codigoAsiento;
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
     * @return the renglonesPrestado
     */
    public List<Renglones> getRenglonesPrestado() {
        return renglonesPrestado;
    }

    /**
     * @param renglonesPrestado the renglonesPrestado to set
     */
    public void setRenglonesPrestado(List<Renglones> renglonesPrestado) {
        this.renglonesPrestado = renglonesPrestado;
    }

    /**
     * @return the renglonesAñosAnteriores
     */
    public List<Renglones> getRenglonesAñosAnteriores() {
        return renglonesAñosAnteriores;
    }

    /**
     * @param renglonesAñosAnteriores the renglonesAñosAnteriores to set
     */
    public void setRenglonesAñosAnteriores(List<Renglones> renglonesAñosAnteriores) {
        this.renglonesAñosAnteriores = renglonesAñosAnteriores;
    }

    /**
     * @return the rolTotal
     */
    public double getRolTotal() {
        return rolTotal;
    }

    /**
     * @param rolTotal the rolTotal to set
     */
    public void setRolTotal(double rolTotal) {
        this.rolTotal = rolTotal;
    }

    /**
     * @return the saldoTotal
     */
    public double getSaldoTotal() {
        return saldoTotal;
    }

    /**
     * @param saldoTotal the saldoTotal to set
     */
    public void setSaldoTotal(double saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    /**
     * @return the formularioBorrar
     */
    public Formulario getFormularioBorrar() {
        return formularioBorrar;
    }

    /**
     * @param formularioBorrar the formularioBorrar to set
     */
    public void setFormularioBorrar(Formulario formularioBorrar) {
        this.formularioBorrar = formularioBorrar;
    }

    /**
     * @return the concepto
     */
    public Conceptos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the conceptoExtra
     */
    public Conceptos getConceptoExtra() {
        return conceptoExtra;
    }

    /**
     * @param conceptoExtra the conceptoExtra to set
     */
    public void setConceptoExtra(Conceptos conceptoExtra) {
        this.conceptoExtra = conceptoExtra;
    }
}
