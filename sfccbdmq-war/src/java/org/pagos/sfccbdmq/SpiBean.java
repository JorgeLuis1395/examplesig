/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import javax.faces.application.Resource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarSpi;
import org.auxiliares.sfccbdmq.Codificador;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.PlantillaSpi;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.utilitarios.sfccbdmq.AuxiliaresCorreos;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "spiSfccbdmq")
@ViewScoped
public class SpiBean {

    /**
     * Creates a new instance of KardexbancoBean
     */
    public SpiBean() {
        listaSpi = new LazyDataModel<Spi>() {

            @Override
            public List<Spi> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private List<Kardexbanco> listakardex;
    private List<Kardexbanco> listaSeleccionada;
    private List<Kardexbanco> listaSeleccionadab;
    private List<AuxiliarSpi> listaAuxiliar;
    private List<Auxiliar> listaTotales;
    private LazyDataModel<Spi> listaSpi;
    private Spi spi;
    private String spiMd5;
    private String propuesta;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioKardex = new Formulario();
    private Formulario formularioSelecciona = new Formulario();
    private Formulario formularioColoca = new Formulario();
    private Perfiles perfil;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @EJB
    private SpiFacade ejbSpi;
    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private ContratosFacade ejbContratos;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RetencionescomprasFacade ejbRetenciones;
    @EJB
    private PagosvencimientosFacade ejbPagosvencimientos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Bancos banco;
    private Kardexbanco kardex;
    private Date desde;
    private Date hasta;
    private Integer estado;
    private Integer numeroAnterior;
    private double total;
    private Resource recursoTxt;
    private Resource recursoXls;
    private Resource recursoPdf;
    // Para seleccionar
    public String selectedEffectType = "default";
    public String selectedSearchMode = "contains";
    public String searchQuery = "";
    public String[] selectedColumns = new String[]{"name", "id", "chassis", "weight", "acceleration", "mpg", "cost"};
    public int lastFoundIndex = -1;
    private boolean caseSensitive;

    // fin seleccionar
    public String buscar() {

        listaSpi = new LazyDataModel<Spi>() {

            @Override
            public List<Spi> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc,o.id");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta and o.estado>=0";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();
                    if (clave.contains("numero")) {
                        where += " and upper(o." + clave + ") =:" + clave;
                        parametros.put(clave, valor.toUpperCase());
                    } else if (clave.contains("id")) {
                        where += " and upper(o." + clave + ") =:" + clave;
                        parametros.put(clave, valor.toUpperCase());
                    } else {
                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                }
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                if (banco != null) {
                    where += " and o.banco=:banco";
                    parametros.put("banco", banco);
                }
                if (estado != null) {
                    where += " and o.estado=:estado";
                    parametros.put("estado", estado);
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbSpi.contar(parametros);
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
                listaSpi.setRowCount(total);
                try {
                    return ejbSpi.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return "TesoreriaVista.jsf?faces-redirect=true";
    }

    public String salir() {
        formulario.cancelar();
        listaSeleccionada = new LinkedList<>();
        listaSeleccionadab = new LinkedList<>();
        listakardex = new LinkedList<>();
        return null;
    }

    public String nuevo() {
        if (banco == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        formulario.insertar();
        spi = new Spi();
        spi.setBanco(banco);
        spi.setEstado(0);
        propuesta = null;
        spi.setUsuario(seguridadbean.getLogueado().getUserid());
//        spi.setFecha(configuracionBean.getConfiguracion().getPvigente());
        spi.setFecha(new Date());
        // buscar kardex no pagados del banco al cual vamos a psaas
//        Map parametros = new HashMap();
//        parametros.put(";where", " o.spi is null ");
//        parametros.put(";orden", "o.fechamov asc");
//        parametros.put("banco", banco);
//        try {
//            listakardex = ejbKardexbanco.encontarParametros(parametros);
        listakardex = new LinkedList<>();
        listaSeleccionada = new LinkedList<>();
        listaSeleccionadab = new LinkedList<>();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
//        }

        return null;
    }

    public String generar(Spi spiParametro) throws IOException {
        listaAuxiliar = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.spi=:spi");
            parametros.put(";orden", "o.fechamov asc");
            parametros.put("spi", spiParametro);
            listaSeleccionada = ejbKardexbanco.encontarParametros(parametros);

            DecimalFormat df = new DecimalFormat("0.00");
            for (Kardexbanco k : listaSeleccionada) {
                AuxiliarSpi s = new AuxiliarSpi();
                s.setReferencia(spiParametro.getId());

                if (k.getProveedor() != null) {
                    s.setCedula(k.getProveedor().getRucbeneficiario());
                    s.setNombre(k.getProveedor().getNombrebeneficiario());
                    s.setBanco(k.getProveedor().getBanco().getCodigo());
                    s.setCuenta(k.getProveedor().getCtabancaria());
                    s.setTipocuenta(k.getProveedor().getTipocta().getParametros());

                } else if (k.getAnticipo() != null) {
                    s.setCedula(k.getAnticipo().getProveedor().getRucbeneficiario());
                    s.setNombre(k.getAnticipo().getProveedor().getNombrebeneficiario());
                    s.setBanco(k.getAnticipo().getProveedor().getBanco().getCodigo());
                    s.setCuenta(k.getAnticipo().getProveedor().getCtabancaria());
                    s.setTipocuenta(k.getAnticipo().getProveedor().getTipocta().getParametros());
                } else {
                    s.setCedula(k.getDocumento());
                    s.setNombre(k.getBeneficiario());
                    s.setBanco(k.getBancotransferencia() == null ? "" : k.getBancotransferencia().getCodigo());
                    s.setCuenta(k.getCuentatrans());
                    s.setTipocuenta(k.getTcuentatrans().toString());
                }
                String vStr = df.format(k.getValor().doubleValue());
                vStr = vStr.replace(",", ".");
                s.setValor(vStr);
                s.setConcepto(k.getCodigospi());
                s.setDetalle(k.getObservaciones());
                listaAuxiliar.add(s);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.insertar();
        spiParametro.setEstado(1);
        try {
            ejbSpi.edit(spiParametro, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void otroBanco(String codigoBanco, double valor, List<Auxiliar> lista) {
        for (Auxiliar a : lista) {
            if (codigoBanco.equals(a.getTitulo1())) {
                double valorTotal = a.getTotal().doubleValue() + valor;
                a.setTotal(new BigDecimal(valorTotal));
                a.setIndice(a.getIndice() + 1);
                return;
            }
        }
    }

    public String generarAnt(Spi spiParametro) throws IOException {
        List<Codigos> listadoBancos = codigosBean.getListadoBancos();
        double total = 0;
        int cuantos = 0;
        List<Auxiliar> listaTotalesInterno = new LinkedList<>();
        List<AuxiliaresCorreos> listaAuiliarCorreos=new LinkedList<>();
        for (Codigos c : listadoBancos) {
            Auxiliar a = new Auxiliar();
            a.setIndice(0);
            a.setTotal(BigDecimal.ZERO);
            a.setTitulo1(c.getCodigo());
            a.setTitulo2(c.getNombre());
            listaTotalesInterno.add(a);
        }
        // para la cabecera
        spi = spiParametro;
        FileWriter fichero = null;
//        PrintWriter pw = null;
        String directorio = System.getProperty("java.io.tmpdir");
//        String directorio = getConfiguracionBean().getConfiguracion().getDirectorio() + "/";//"/home/edwin/Escritorio/comprobantes/";
//        String directorio = getConfiguracionBean().getConfiguracion().getDirectorio() + "/" + spiParametro.getId().toString() + "salida.txt";//"/home/edwin/Escritorio/comprobantes/";
        Calendar c = Calendar.getInstance();
        String nombreArchivo = "spi-sp.txt";
        String nombreArchivoSindirectorio = directorio + "spi-sp.txt";
//        String archivoNombre = "Salida_" + c.getTimeInMillis() + ".txt";
        String linea = "";
        try {
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
            DecimalFormat df1 = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
            DecimalFormat df2 = new DecimalFormat("00", new DecimalFormatSymbols(Locale.US));
            DecimalFormat df5 = new DecimalFormat("000000", new DecimalFormatSymbols(Locale.US));
            Map parametros = new HashMap();
            parametros.put(";where", "o.spi=:spi");
            parametros.put(";orden", "o.fechamov asc");
            parametros.put("spi", spiParametro);
            listaSeleccionada = ejbKardexbanco.encontarParametros(parametros);
            if (listaSeleccionada.isEmpty()) {
                MensajesErrores.advertencia("No es posible generar archivo, no existen pagos");
                return null;
            }
            double sumaCuenta = 0;
            for (Kardexbanco k : listaSeleccionada) {

                if (k.getProveedor() != null) {
                    // numero de SPI
                    // Institucion bancaria
                    sumaCuenta += Double.parseDouble(k.getProveedor().getBanco().getCodigo());
                } else if (k.getAnticipo() != null) {
                    // numero de SPI
                    sumaCuenta += Double.parseDouble(k.getAnticipo().getProveedor().getBanco().getCodigo());
                } else if (k != null) {
                    // numero de SPI
                    // que tenemos para buscar antes ded esto
                    // buscar empleado con documento y luego con auxiliar
                    Entidades entidad = entidadesBean.traerCedula(k.getDocumento());
                    if (entidad == null) {
                        entidad = entidadesBean.traerCedula(k.getAuxiliar());
                    }
                    if (entidad != null) {
                        Empleados emp = entidad.getEmpleados();
                        Codigos bancoTrans = traerBanco(emp);
                        k.setBancotransferencia(bancoTrans);
                        k.setCuentatrans(traer(emp, "NUMCUENTA"));
                        k.setDocumento(entidad.getPin());
                        k.setAuxiliar(entidad.getPin());
                        String tipoCta = traerTipoCuenta(emp);
                        k.setTcuentatrans(Integer.parseInt(tipoCta));
                        ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
                    }
                    if (k.getBancotransferencia() == null) {
                        arreglarEmpleado(k);
                        if (k.getBancotransferencia() == null) {
                            MensajesErrores.advertencia("No existe cuenta bancaria para " + k.getBeneficiario());
                            return null;
                        }
                    }
                    if (k.getBancotransferencia().getCodigo() == null) {
                        MensajesErrores.advertencia("No existe código de cuenta bancaria para " + k.getBeneficiario());
                        return null;
                    }
                    sumaCuenta += Double.parseDouble(k.getBancotransferencia().getCodigo());
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
            SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
//            SimpleDateFormat sdf1 = new SimpleDateFormat("MM-yyyy");
//            fecha hora
//            String lineaCabecera = "00:00:00,";
            String lineaCabecera = sdf.format(spi.getFecha()) + ",";
//            String lineaCabecera = sdf.format(spi.getFecha()) + " 00:00:00,";
            // numero de SPI
            String parSpi = sdfAnio.format(spi.getFecha()).substring(2) + df5.format(spiParametro.getNumero());
            lineaCabecera += parSpi + ",";
//            lineaCabecera += spiParametro.getId().toString() + ",";
//            numero de registros

            lineaCabecera += df2.format(listaSeleccionada.size()) + ",01,";
//            valor a trasferir
            double valorAtrasferir = 0;
            Bancos b = new Bancos();
            for (Kardexbanco k : listaSeleccionada) {
                valorAtrasferir += k.getValor().doubleValue();
                b = k.getBanco();
            }
            String vStr = df.format(valorAtrasferir);
            lineaCabecera += vStr + ",";
            //nuemro que cambia
            double sumaControl = sumaCuenta + Math.round(valorAtrasferir * 100) + 107 + Double.parseDouble(b.getNumerocuenta()) * 2;
            lineaCabecera += df1.format(sumaControl) + ",";
//            lineaCabecera += "5570322,";
            // institucion bancaria
            lineaCabecera += b.getNumerocuenta() + ",";
            lineaCabecera += b.getNumerocuenta() + ",";
            // Nombre de la empresa
//            lineaCabecera += configuracionBean.getConfiguracion().getNombre() + ",";
            lineaCabecera += "EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD,";
            //
            lineaCabecera += "QUITO,";
            // periodo del proceso
            lineaCabecera += sdf1.format(spi.getFecha()) + "\r\n";
//            lineaCabecera += "00:00:00\r\n";
            linea = lineaCabecera;
//            fichero = new FileWriter(nombreArchivo);
//            pw = new PrintWriter(fichero);
//            pw.println(lineaCabecera);
            
            for (Kardexbanco k : listaSeleccionada) {
                if ((k.getCodigospi() == null) || (k.getCodigospi().isEmpty())) {
                    k.setCodigospi("40101");
                }
                AuxiliaresCorreos auxiCor=new AuxiliaresCorreos();
                if (k.getProveedor() != null) {
                    // numero de SPI
                    linea += parSpi + ",";
                    // Valor de la transferencia
                    vStr = df.format(k.getValor().doubleValue());
//                    vStr = vStr.replace(",", ".");
                    linea += vStr + ",";
                    // codigo SPI
                    linea += k.getCodigospi() + ",";
                    // Institucion bancaria
                    linea += k.getProveedor().getBanco().getCodigo() + ",";
                    
                    otroBanco(k.getProveedor().getBanco().getCodigo(), k.getValor().doubleValue(), listaTotalesInterno);
                    total += k.getValor().doubleValue();
                    auxiCor.setValor(k.getValor().doubleValue());
                    auxiCor.setCuenta(k.getProveedor().getCtabancaria());
                    auxiCor.setBanco(k.getProveedor().getBanco().getNombre());
                    auxiCor.setNumbre(k.getProveedor().getEmpresa().toString());
                    auxiCor.setCorreo(k.getProveedor().getEmpresa().getEmail());
                    cuantos++;
//                    Numero de cuenta
                    linea += k.getProveedor().getCtabancaria() + ",";
//                    tipo de cuenta
                    linea += k.getProveedor().getTipocta().getParametros() + ",";
                    String nombre = k.getProveedor().getNombrebeneficiario();
//                    String nombre = ;
                    if (nombre == null) {
                        nombre = k.getBeneficiario();
                    }
                    if (nombre == null) {
                        nombre = "NO PRESENTE";
                    }
                    nombre = nombre.toLowerCase();
//                    nombre = k.getProveedor().getNombrebeneficiario().toLowerCase();
                    String cambiado = reemplazarCaracteresRaros(nombre);
//                    String cambiado = nombre.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n");
                    if (cambiado.length() > 30) {
                        linea += cambiado.substring(0, 29).toUpperCase() + ",";
                    } else {
                        linea += cambiado.toUpperCase() + ",";
                    }
//                    concepto
                    cambiado = reemplazarCaracteresRaros(k.getObservaciones());
                    if (cambiado.length() > 50) {
                        linea += cambiado.substring(0, 49).toUpperCase() + ",";
                    } else {
                        linea += cambiado.toUpperCase() + ",";
                    }
//                    linea += cambiado + ",";
                    // Cedula o RUC
                    linea += k.getProveedor().getRucbeneficiario() + "\r\n";
                } else if (k.getAnticipo() != null) {
                    // numero de SPI
                    linea += parSpi + ",";
                    // Valor de la transferencia
                    vStr = df.format(k.getValor().doubleValue());
//                    vStr = vStr.replace(",", ".");
                    linea += vStr + ",";
                    // codigo SPI
                    linea += k.getCodigospi() + ",";
//                    Institucion bancaria
                    linea += k.getAnticipo().getProveedor().getBanco().getCodigo() + ",";
                    otroBanco(k.getAnticipo().getProveedor().getBanco().getCodigo(), k.getValor().doubleValue(), listaTotalesInterno);
                    total += k.getValor().doubleValue();
                    cuantos++;
//                    numero de cuenta
                    linea += k.getAnticipo().getProveedor().getCtabancaria() + ",";
//                    tipo de cuenta
                    linea += k.getAnticipo().getProveedor().getTipocta().getParametros() + ",";
//                    nombre de beneficiario
                    String nombre = k.getAnticipo().getProveedor().getNombrebeneficiario();
                    String cambiado = reemplazarCaracteresRaros(nombre);
//                    String cambiado = nombre.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n");
                    if (cambiado.length() > 30) {
                        linea += cambiado.substring(0, 29).toUpperCase() + ",";
                    } else {
                        linea += cambiado.toUpperCase() + ",";
                    }
                    //                    concepto
                    cambiado = reemplazarCaracteresRaros(k.getObservaciones());
                    if (cambiado.length() > 50) {
                        linea += cambiado.substring(0, 49).toUpperCase() + ",";
                    } else {
                        linea += cambiado.toUpperCase() + ",";
                    }
//                    linea += cambiado + ",";
//                    ruc proveedor
                    linea += k.getAnticipo().getProveedor().getRucbeneficiario() + "\r\n";
                    auxiCor.setValor(k.getValor().doubleValue());
                    auxiCor.setCuenta(k.getAnticipo().getProveedor().getCtabancaria());
                    auxiCor.setBanco(k.getAnticipo().getProveedor().getBanco().getNombre());
                    auxiCor.setNumbre(k.getAnticipo().getProveedor().getEmpresa().toString());
                    auxiCor.setCorreo(k.getAnticipo().getProveedor().getEmpresa().getEmail());

                } else if (k != null) {
                    // numero de SPI
                    linea += parSpi + ",";
                    // Valor de la transferencia
                    vStr = df.format(k.getValor().doubleValue());
//                    vStr = vStr.replace(",", ".");
                    linea += vStr + ",";
                    // codigo SPI
                    linea += k.getCodigospi() + ",";
//                    Institucion bancaria
                    linea += k.getBancotransferencia().getCodigo() + ",";
                    otroBanco(k.getBancotransferencia().getCodigo(), k.getValor().doubleValue(), listaTotalesInterno);
                    total += k.getValor().doubleValue();
                    cuantos++;
//                    numero de cuenta
                    linea += k.getCuentatrans() + ",";
//                    tipo de cuenta
                    linea += k.getTcuentatrans() + ",";
//                    nombre de beneficiario

                    String nombre = k.getBeneficiario();
                    if ((nombre == null) || (nombre.isEmpty())) {
                        Entidades en = entidadesBean.traerCedula(k.getDocumento());
                        nombre = en.toString();
                        auxiCor.setCorreo(en.getEmail());
                    }
                    String cambiado = reemplazarCaracteresRaros(nombre);
//                    String cambiado = nombre.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n");
                    if (cambiado.length() > 30) {
                        linea += cambiado.substring(0, 29).toUpperCase() + ",";
                    } else {
                        linea += cambiado.toUpperCase() + ",";
                    }
                    //                    concepto
                    cambiado = reemplazarCaracteresRaros(k.getObservaciones());
                    if (cambiado.length() > 50) {
                        linea += cambiado.substring(0, 49).toUpperCase() + ",";
                    } else {
                        linea += cambiado.toUpperCase() + ",";
                    }
//                    linea += cambiado + ",";
//                    ruc proveedor
                    linea += k.getDocumento() + "\r\n";
                    auxiCor.setValor(k.getValor().doubleValue());
                    auxiCor.setCuenta(k.getCuentatrans());
                    auxiCor.setBanco(k.getBancotransferencia().getNombre());
                    auxiCor.setNumbre(nombre);
                    
                }
                listaAuiliarCorreos.add(auxiCor);
//                pw.println(linea);
            }
//            linea = reemplazarCaracteresRaros(linea);
            listaTotales = new LinkedList<>();
            for (Auxiliar ax : listaTotalesInterno) {
                if (ax.getIndice() != 0) {
                    listaTotales.add(ax);
                }
            }
            Auxiliar a = new Auxiliar();
            a.setTitulo2("TOTALES");
            a.setIndice(cuantos);
            a.setTotal(new BigDecimal(total));
            listaTotales.add(a);
            File archivo1 = new File(nombreArchivo);
            File archivo2 = new File(nombreArchivoSindirectorio);
            BufferedWriter bw;
            BufferedWriter bw1;
            if (archivo1.exists()) {
//                String cadena = muestraContenido("/home/edwin/Descargas/spi-sp/spi-sp.txt");
                bw = new BufferedWriter(new FileWriter(archivo1));
                bw.write(linea);
            } else {
//                String cadena = muestraContenido("/home/edwin/Descargas/spi-sp/spi-sp.txt");
                bw = new BufferedWriter(new FileWriter(archivo1));
                bw.write(linea);
            }
            bw.close();
        } catch (ConsultarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            MensajesErrores.fatal("Cuenta no numérica" + ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
                spiMd5 = Codificador.getCodigoHash(nombreArchivo);
//                String md5Archivo = spiMd5 +"  "+ nombreArchivo + "";
                String md5Archivo = spiMd5 + "   c:\\SPI-2005\\" + nombreArchivo + "";
                String nombreArchivoMd5 = "spi-sp.md5";
//                String archivoNombreMd5 = "MD5_" + c.getTimeInMillis() + ".txt";
                FileWriter ficheroMd5 = null;
                PrintWriter pwMd5 = null;
                ficheroMd5 = new FileWriter(nombreArchivoMd5);
                pwMd5 = new PrintWriter(ficheroMd5);
                pwMd5.println(md5Archivo);
                if (null != ficheroMd5) {
                    ficheroMd5.close();
                }
                Calendar ck = Calendar.getInstance();
                String archivoNuevo = "spi-sp" + Calendar.getInstance().getTimeInMillis();
                File fZip = File.createTempFile("spi-sp" + Calendar.getInstance().getTimeInMillis(), ".zip");
                FileOutputStream fos = new FileOutputStream(fZip);
//                FileOutputStream fos = new FileOutputStream(directorio+"/spi-sp"+ck.getTimeInMillis()+".zip");
                ZipOutputStream zos = new ZipOutputStream(fos);
                Codificador.addToZipFile(nombreArchivo, zos);
                Codificador.addToZipFile(nombreArchivoMd5, zos);
                Path path = fZip.toPath();
                zos.close();
                fos.close();
                byte[] data = Files.readAllBytes(path);
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();
                recursoTxt = new Recurso(data);
//                recursoTxt = new txtRecurso(ec, archivoNuevo+".zip", data);
                formularioImprimir.insertar();
                if (spiParametro.getEstado()==0){
                    // enviar Advertencia
//                    ***********************************************
                 ejbSpi.alertaSpi(listaAuiliarCorreos);
//                            ********************************+
                    // enviar
                }
                spiParametro.setEstado(1);
                ejbSpi.edit(spiParametro, seguridadbean.getLogueado().getUserid());
                ////////////////////////HOJA EXCEL ////////////////////////
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                DocumentoPDF pdf = new DocumentoPDF("REPORTE DE CONTROL TRANSFERENCIAS SPI - " + spi.getNumero(),
                        null, seguridadbean.getLogueado().getUserid());
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de aceptación :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(spi.getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número de control :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, spiMd5));
                pdf.agregarTabla(null, columnas, 4, 100, null);
                //
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Institución Pagadora"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "# Pagos"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "US$ MONTO"));
                columnas = new LinkedList<>();
                for (Auxiliar at : listaTotales) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, at.getTitulo1()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, at.getTitulo2()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, String.valueOf(at.getIndice())));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, at.getTotal().doubleValue()));
                }
                pdf.agregarTablaReporte(titulos, columnas, 4, 100, "");
                List<AuxiliarSpi> listaSpiAux = new LinkedList<>();
                titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número de Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo de Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "US$ MONTO"));
                columnas = new LinkedList<>();
                for (Kardexbanco k : listaSeleccionada) {
                    AuxiliarSpi s = new AuxiliarSpi();
                    s.setReferencia(spiParametro.getId());

                    if (k.getProveedor() != null) {
                        s.setCedula(k.getProveedor().getRucbeneficiario());
                        String nombre = k.getBeneficiario();
                        if (nombre == null) {
                            nombre = k.getProveedor().getNombrebeneficiario();
                        }
                        if (nombre == null) {
                            nombre = "NO PRESENTE";
                        }
                        s.setNombre(nombre);
                        s.setBanco(k.getProveedor().getBanco().getCodigo());
                        s.setCuenta(k.getProveedor().getCtabancaria());
                        s.setTipocuenta(k.getProveedor().getTipocta().getParametros());
                        ///

                        ///
                    } else if (k.getAnticipo() != null) {
                        s.setCedula(k.getAnticipo().getProveedor().getRucbeneficiario());
                        s.setNombre(k.getAnticipo().getProveedor().getNombrebeneficiario());
                        s.setBanco(k.getAnticipo().getProveedor().getBanco().getCodigo());
                        s.setCuenta(k.getAnticipo().getProveedor().getCtabancaria());
                        s.setTipocuenta(k.getAnticipo().getProveedor().getTipocta().getParametros());
                        Contratos contr = k.getAnticipo().getContrato();
                        contr.setFechaanticipo(spi.getFecha());
                        ejbContratos.edit(contr, seguridadbean.getLogueado().getUserid());
                    } else {
                        if (k.getTcuentatrans() == null) {
                            MensajesErrores.advertencia("No existe tipo de cuenta : " + k.getBeneficiario());
                        }
                        s.setCedula(k.getDocumento());
                        s.setNombre(k.getBeneficiario());
                        s.setBanco(k.getBancotransferencia() == null ? "" : k.getBancotransferencia().getCodigo());
                        s.setCuenta(k.getCuentatrans());
                        s.setTipocuenta(k.getTcuentatrans() == null ? "" : k.getTcuentatrans().toString());
                    }
//                    String vStr = df.format(k.getValor().doubleValue());
//                    vStr = vStr.replace(",", ".");
//                    s.setValor(vStr);
                    ///
                    s.setValorDoble(k.getValor().doubleValue());
                    s.setDetalle(k.getObservaciones());
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, s.getDetalle()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, s.getNombre()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, s.getBanco()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, s.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, s.getTipocuenta()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, s.getValorDoble()));
                    ///

                    s.setConcepto(k.getCodigospi());

                    listaSpiAux.add(s);
                }
                /////////////////////// Generear la hoja ///////////////////////////
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DETALLE");
                PlantillaSpi plantilla = new PlantillaSpi();
                plantilla.llenar(listaSpiAux);
                recursoXls = plantilla.traerRecurso();
                // Hacer el pdf

                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,  "Revisado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,  "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,  "Contador"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

                pdf.agregarTabla(null, columnas, 2, 100, null);
                recursoPdf = pdf.traerRecurso();
               
            } catch (IOException | NoSuchAlgorithmException | GrabarException | DocumentException e2) {
                MensajesErrores.fatal(e2.getMessage());
                Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, e2);
            }
        }

//        try {
//            
//        } catch (GrabarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }

    public String muestraContenido(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        String cadenaRetorno = "";
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while ((cadena = b.readLine()) != null) {
            cadenaRetorno += cadena + "\r\n";
        }
        b.close();
        return cadenaRetorno;
    }

    /**
     * Función que elimina acentos y caracteres especiales de una cadena de
     * texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public String reemplazarCaracteresRaros(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ/%&,.#+-¡?¿()!°|[]º";
//        System.out.println("Original"+original.length());
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC              N    ";
//        System.out.println("Ascii"+ascii.length());
        String output = input;
        if (output == null) {
            return "";
        }
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        output = output.replace("\n", "");
        output = output.replace("\t", "");
        return output;
    }

    public String modifica(Spi spi) {
        propuesta = null;
        this.spi = spi;
        recargar();
        formulario.editar();

        return null;
    }

    public String coloca(Spi spi) {

        this.spi = spi;
        banco = spi.getBanco();
//        spi = new Spi();
//        spi.setBanco(banco);
        // buscar kardex no pagados del banco al cual vamos a psaas
        Map parametros = new HashMap();
        try {
            // buscar los ya asignados
            parametros.put(";where", "o.spi=:spi");
            parametros.put(";orden", "o.fechamov asc");
            parametros.put("spi", spi);
            listaSeleccionada = ejbKardexbanco.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(SpiBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formularioColoca.editar();

        return null;
    }

    private void recargar() {
        banco = spi.getBanco();
//        spi = new Spi();
//        spi.setBanco(banco);
        // buscar kardex no pagados del banco al cual vamos a psaas

//        parametros.put("banco", banco);
        try {
            Map parametros = new HashMap();
            if (!((propuesta == null) || (propuesta.isEmpty()))) {
                parametros.put(";where", "o.spi is null and upper(o.propuesta) = :propuetas");
                parametros.put("propuetas", propuesta.toUpperCase());
                parametros.put(";orden", "o.fechamov asc");
                listakardex = ejbKardexbanco.encontarParametros(parametros);
            }

//        parametros.put(";where", "o.banco=:banco and o.spi is null and o.formapago.parametros='T'");
            // buscar los ya asignados
            parametros = new HashMap();
            parametros.put(";where", "o.spi=:spi");
            parametros.put(";orden", "o.fechamov asc");
            parametros.put("spi", spi);
            listaSeleccionada = ejbKardexbanco.encontarParametros(parametros);
//            listaSeleccionadab = new LinkedList<>();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void recargarNulas() {
        banco = spi.getBanco();
//        spi = new Spi();
//        spi.setBanco(banco);
        // buscar kardex no pagados del banco al cual vamos a psaas

//        parametros.put("banco", banco);
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.spi is null and o.propuesta is null ");
//            parametros.put("propuetas", propuesta.toUpperCase());
            parametros.put(";orden", "o.fechamov asc");
            listakardex = ejbKardexbanco.encontarParametros(parametros);

//        parametros.put(";where", "o.banco=:banco and o.spi is null and o.formapago.parametros='T'");
            // buscar los ya asignados
            parametros = new HashMap();
            parametros.put(";where", "o.spi=:spi");
            parametros.put(";orden", "o.fechamov asc");
            parametros.put("spi", spi);
            listaSeleccionada = ejbKardexbanco.encontarParametros(parametros);
//            listaSeleccionadab = new LinkedList<>();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String cambiaFecha(Spi spi) {

        this.spi = spi;
        banco = spi.getBanco();
        numeroAnterior = spi.getNumero();
//        spi = new Spi();
//        spi.setBanco(banco);
        // buscar kardex no pagados del banco al cual vamos a psaas
        Map parametros = new HashMap();
        try {
            // buscar los ya asignados
            parametros.put(";where", "o.spi=:spi");
            parametros.put(";orden", "o.egreso asc");
            parametros.put("spi", spi);
            listaSeleccionada = ejbKardexbanco.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioColoca.insertar();

        return null;
    }

    public String grabaColoca() {
        if ((spi.getClavebanco() == null) || (spi.getClavebanco().isEmpty())) {
            MensajesErrores.advertencia("Favor colocar el código del banco");
            return null;
        }
        try {
            ejbSpi.edit(spi, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioColoca.cancelar();
        return null;
    }

    public String grabaFecha() {

        try {
            for (Kardexbanco k : listaSeleccionada) {
                if (spi.getFecha() == null) {
                    MensajesErrores.advertencia("Es necesario fecha de movimiento");
                    return null;
                }
//                ver las fechas 
                if (spi.getFecha().before(k.getFechamov())) {
                    MensajesErrores.advertencia("La fecha del SPI no puede ser menor a la fecha del movimiento");
                    return null;
                }
            }
            
            if (!Objects.equals(numeroAnterior, spi.getNumero())) {
                // cambiar los kardex con la nueva numeración
                DecimalFormat df1 = new DecimalFormat("000");
                DecimalFormat df2 = new DecimalFormat("00000");
                int i = 0;
                for (Kardexbanco k : listaSeleccionada) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(k.getFechamov());
                    i++;
                    String noNEgreso = String.valueOf(c.get(Calendar.YEAR)).substring(2) + df2.format(spi.getNumero()) + df1.format(i);
                    k.setEgreso(Integer.parseInt(noNEgreso));
                    ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
                }
            }

            ejbSpi.edit(spi, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioColoca.cancelar();
        return null;
    }

    public String eliminar(Spi spi) {

        this.spi = spi;
        banco = spi.getBanco();
//        spi = new Spi();
        spi.setBanco(banco);
        // buscar kardex no pagados del banco al cual vamos a psaas
        Map parametros = new HashMap();
        parametros.put(";where", "o.banco=:banco and o.spi is null");
        parametros.put(";orden", "o.fechamov asc");
        parametros.put("banco", banco);
        try {
            listakardex = ejbKardexbanco.encontarParametros(parametros);
            // buscar los ya asignados

            parametros = new HashMap();
            parametros.put(";where", "o.spi=:spi");
            parametros.put(";orden", "o.fechamov asc");
            parametros.put("spi", spi);
            listaSeleccionada = ejbKardexbanco.encontarParametros(parametros);
            listaSeleccionadab = new LinkedList<>();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(SpiBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();

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

    private boolean validar() {

//        if ((spi.getNumero()== null) || (spi.getNumero()>0)) {
//            MensajesErrores.advertencia("Es necesario número de spi ");
//            return true;
//        }
//        if ((kardex.getFechamov() == null) || (kardex.getFechamov().after(new Date()))) {
//            MensajesErrores.advertencia("Es necesario fecha de movimiento válido en movimiento");
//            return true;
//        }
//        if (kardex.getBanco() == null) {
//            MensajesErrores.advertencia("Es necesario banco del movimiento");
//            return true;
//        }
//        if (kardex.getBanco() == null) {
//            MensajesErrores.advertencia("Es necesario tipo de movimiento");
//            return true;
//        }
//        if ((kardex.getValor() == null) || (kardex.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Es necesario valor de movimiento");
//            return true;
//        }
//        Map parametros=new HashMap();
//        parametros.put(";where", "o.numero=:numero");
//        parametros.put("numero", spi.getNumero());
//        try {
//            int total=ejbSpi.contar(parametros);
//            if (formulario.isNuevo()){
//                if (total>0){
//                    MensajesErrores.fatal("Número de SPI ya registrado");
//                    return true;
//                }
//            } else {
//                if (total>1){
//                    MensajesErrores.fatal("Número de SPI ya registrado");
//                    return true;
//                }
//            }
//        } catch (ConsultarException ex) {
//            Logger.getLogger(SpiBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return false;
    }

    public String insertar() {
//        if (validar()) {
//            return null;
//        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
//            spi.setFecha(new Date());
            spi.setEstado(0);
            spi.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());
//            for (Kardexbanco k : listaSeleccionada) {
//                k.setSpi(spi);
//                ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
//            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(SpiBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabarNuevo() {
        kardex = new Kardexbanco();
        kardex.setBanco(banco);
        kardex.setFechamov(spi.getFecha());
        formularioKardex.insertar();
        return null;
    }

    private void estaEnRenglones(List<Renglones> lista, Renglones nuevo) {
        String cuentaNueva = nuevo.getCuenta();
        String auxiliarNueva = nuevo.getAuxiliar() == null ? "" : nuevo.getAuxiliar();
        double valorNuevo = nuevo.getValor().doubleValue();
        for (Renglones r : lista) {
            String cuenta = r.getCuenta();
            String auxiliar = r.getAuxiliar() == null ? "" : r.getAuxiliar();
            double valor = r.getValor().doubleValue();

            if ((cuenta.equals(cuentaNueva)) && (auxiliar.equals(auxiliarNueva))) {
                r.setValor(new BigDecimal(valorNuevo + valor));
            }
        }
        lista.add(nuevo);
    }

    // proveedores
//    private void contabilizarPagoLote(List<Renglones> renglones, Kardexbanco k) {
//        try {
//            // traer el tipo de codgios para reclasificacion
//            Codigos codigoReclasificacion = codigosBean.traerCodigo(Constantes.TIPO_RECLASIFICACION, "RET");
//
//            Formatos f = ejbObligaciones.traerFormato();
//            renglones = new LinkedList<>();
//            // buscar tipo de siento de reclasificaion
//            // toca las cuentas de notas de credito
//            // Hacer el asiento en base a la suma del rascompras
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.kardexbanco=:kardexbanco");
//            parametros.put("kardexbanco", k);
//            List<Pagosvencimientos> listaPagos = ejbPagosvencimientos.encontarParametros(parametros);
//            for (Pagosvencimientos pagos : listaPagos) {
//                parametros = new HashMap();
//                parametros.put(";where", "o.obligacion=:obligacion");
//                parametros.put("obligacion", pagos.getObligacion());
//                List<Retencionescompras> rl = ejbRetenciones.encontarParametros(parametros);
//                double vanticipo = pagos.getValoranticipo() == null ? 0 : pagos.getValoranticipo().doubleValue();
//                double valorTotalPago = 0;
//                double vmulta = pagos.getValormulta() == null ? 0 : pagos.getValormulta().doubleValue();
//                for (Retencionescompras rc : rl) {
//                    double valor = rc.getBaseimponible().doubleValue()
//                            + rc.getValoriva().doubleValue()
//                            + rc.getBaseimponible0().doubleValue()
//                            + rc.getIva().doubleValue()
//                            - rc.getValor().doubleValue()
//                            - rc.getValoriva().doubleValue();
//                    valorTotalPago += valor;
//                }
//                for (Retencionescompras rc : rl) {
//                    Renglones r = new Renglones(); // reglon de proveedores
//                    r.setFecha(kardex.getFechamov());
//                    r.setReferencia(kardex.getObservaciones());
//                    double valor = rc.getBaseimponible().doubleValue()
//                            + rc.getValoriva().doubleValue()
//                            + rc.getBaseimponible0().doubleValue()
//                            + rc.getIva().doubleValue()
//                            - rc.getValor().doubleValue()
//                            - rc.getValoriva().doubleValue();
//
//                    // distribuir el anticipo
//                    if (vanticipo != 0) {
//                        valor = valor * vanticipo / valorTotalPago;
//                    }
//                    if (vmulta != 0) {
//                        valor = valor * vmulta / valorTotalPago;
//                    }
//                    String cuentaProveedor = f.getCxpinicio() + rc.getPartida() + f.getCxpfin();
//                    r.setValor(new BigDecimal(valor));
//                    r.setCuenta(cuentaProveedor);
//                    Cuentas cuentaP = getCuentasBean().traerCodigo(cuentaProveedor);
//                    r.setPresupuesto(cuentaP.getPresupuesto());
//                    if (cuentaP.getAuxiliares() != null) {
//                        if (pagos.getObligacion().getProveedor() != null) {
//                            r.setAuxiliar(pagos.getObligacion().getProveedor().getEmpresa().getRuc());
//                        }
//                    }
//                    estaEnRenglones(renglones, r);
////                    totalObligacion += valor;
//                }
//            }
//
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
////        formulario.cancelar();
////        imprimirKardex1(kardex);
////        formularioReporte.insertar();
//    }
// fin de probveedores
//    public List<Renglones> getRenglones() {
//        List<Renglones> lista = new LinkedList<>();
//        Cuentas cuentaBanco = getCuentasBean().traerCodigo(kardex.getBanco().getCuenta());
//        for (Kardexbanco k : listaSeleccionada) {
//            if (k.getCuenta() == null) {
//                // ver el origen
//
//                if (k.getAnticipo() != null) {
//
//                    k.setOrigen("ANTICIPO PROVEEDORES");
//                }
//                if (k.getOrigen().equals("ANTICIPO PROVEEDORES")) {
//                    k.setCuenta(k.getAnticipo().getCuenta());
//                }
//            }
//            String aux = k.getDocumento();
//            if (k.getProveedor() != null) {
//                aux = k.getProveedor().getEmpresa().getRuc();
//            }
//            // banco
//            Renglones r1 = new Renglones(); // reglon de banco
//            r1.setFecha(kardex.getFechamov());
//            r1.setReferencia(kardex.getObservaciones());
//            r1.setValor(new BigDecimal(k.getValor().doubleValue() * -1));
//            r1.setCuenta(cuentaBanco.getCodigo());
//            if (cuentaBanco.getAuxiliares() != null) {
//                r1.setAuxiliar(aux);
//            }
//            r1.setPresupuesto(cuentaBanco.getPresupuesto());
//            estaEnRenglones(lista, r1);
//            //otracuenta
//            Cuentas cuentaAnticipo = cuentasBean.traerCodigo(k.getCuenta());
//            Renglones r = new Renglones(); // reglon de banco
//            r.setReferencia(kardex.getObservaciones());
//            r.setFecha(kardex.getFechamov());
//            r.setValor(new BigDecimal(k.getValor().doubleValue()));
//            r.setCuenta(cuentaAnticipo.getCodigo());
//            if (cuentaAnticipo.getAuxiliares() != null) {
//                r.setAuxiliar(aux);
//            }
//            r.setPresupuesto(cuentaAnticipo.getPresupuesto());
//            estaEnRenglones(lista, r);
//        }
//        return lista;
//    }
//
//    public List<Renglones> getRenglonesReclasificacion() {
//        List<Renglones> lista = new LinkedList<>();
//        return lista;
//    }
    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            spi.setEstado(0);
            spi.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbSpi.edit(spi, seguridadbean.getLogueado().getUserid());
            for (Kardexbanco k : listaSeleccionada) {
                k.setSpi(spi);
                ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
            }
            for (Kardexbanco k : listaSeleccionadab) {
                k.setSpi(null);
                ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(SpiBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar
            for (Kardexbanco k : listaSeleccionada) {
                k.setSpi(null);
                ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
            }
            for (Kardexbanco k : listaSeleccionadab) {
                k.setSpi(null);
                ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
            }
            ejbSpi.remove(spi, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(SpiBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
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

    public Kardexbanco traer(Integer id) throws ConsultarException {
        return ejbKardexbanco.find(id);
    }

    @PostConstruct
    private void activar() {

        desde = configuracionBean.getConfiguracion().getPinicial();
        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "SpiVista";
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

    public String getValorSeleccionado() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double valor = 0;
        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        for (Kardexbanco k : listaSeleccionada) {
            valor += k.getValor().doubleValue();
        }
        return df.format(valor);
    }

    public String getValorSeleccionar() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        double valor = 0;
        if (listakardex == null) {
            listakardex = new LinkedList<>();
        }
        for (Kardexbanco k : listakardex) {
            valor += k.getValor().doubleValue();
        }
        return df.format(valor);
    }

    public double getValorFila() {
        Spi s = (Spi) listaSpi.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.spi=:spi");
        parametros.put(";campo", "o.valor");
        parametros.put("spi", s);
        try {
            return ejbKardexbanco.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the listaSpi
     */
    public LazyDataModel<Spi> getListaSpi() {
        return listaSpi;
    }

    /**
     * @param listaSpi the listaSpi to set
     */
    public void setListaSpi(LazyDataModel<Spi> listaSpi) {
        this.listaSpi = listaSpi;
    }

    /**
     * @return the spi
     */
    public Spi getSpi() {
        return spi;
    }

    /**
     * @param spi the spi to set
     */
    public void setSpi(Spi spi) {
        this.spi = spi;
    }

    /**
     * @return the estado
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    /**
     * @return the listaSeleccionada
     */
    public List<Kardexbanco> getListaSeleccionada() {
        return listaSeleccionada;
    }

    /**
     * @param listaSeleccionada the listaSeleccionada to set
     */
    public void setListaSeleccionada(List<Kardexbanco> listaSeleccionada) {
        this.listaSeleccionada = listaSeleccionada;
    }

    /**
     * @return the listakardex
     */
    public List<Kardexbanco> getListakardex() {
        return listakardex;
    }

    /**
     * @param listakardex the listakardex to set
     */
    public void setListakardex(List<Kardexbanco> listakardex) {
        this.listakardex = listakardex;
    }

    public String colocar(Kardexbanco k) {
        try {
            if (spi.getId() == null) {

                ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());

            }
            k.setBanco(spi.getBanco());
            k.setSpi(spi);
            ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | InsertarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        recargar();
        return null;
    }
//    public String colocar(int i) {
//        int kind = 0;
//        Kardexbanco k = new Kardexbanco();
//        for (Kardexbanco k1 : listakardex) {
//            kind++;
//            if (k1.getId() == i) {
//                k = k1;
//                listakardex.remove(kind - 1);
//                break;
//            }
//        }
//
//        if (listaSeleccionada == null) {
//            listaSeleccionada = new LinkedList<>();
//        }
//        listaSeleccionada.add(k);
////        listakardex.remove(kind);
//        return null;
//    }

    public String retirar(Kardexbanco k) {
        k.setSpi(null);
        k.setBanco(null);
        try {
            ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        recargar();
        return null;
    }

    public String retirar1(int i) {
        Kardexbanco k = listaSeleccionada.get(i);
//        k.setBanco(null);
        if (listaSeleccionadab == null) {
            listaSeleccionadab = new LinkedList<>();
        }

        listakardex.add(k);
        listaSeleccionadab.add(k);
        listaSeleccionada.remove(i);
        return null;
    }

    public String colocarTodas() {
        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        try {
            if (spi.getId() == null) {

                ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());

            }
            int i = 0;
            for (Kardexbanco k : listakardex) {
//            listaSeleccionada.add(k);
                k.setBanco(spi.getBanco());
                k.setSpi(spi);
                ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
//            listakardex.remove(i++);

            }
        } catch (GrabarException | InsertarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
//        listakardex = new LinkedList<>();
//        for (Kardexbanco k : listakardex) {
////            listaSeleccionada.add(k);
//            listakardex.remove(i++);
//        }
        recargar();
        return null;
    }

    public String colocarSeleccionadas() {
        if (listaSeleccionada == null) {
            listaSeleccionada = new LinkedList<>();
        }
        try {
            if (spi.getId() == null) {

                ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());

            }
            int i = 0;
            for (Kardexbanco k : listakardex) {
                if (k.isSeleccionar()) {
//            listaSeleccionada.add(k);
                    k.setBanco(spi.getBanco());
                    k.setSpi(spi);
                    ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());

                }
//            listakardex.remove(i++);
            }
        } catch (GrabarException | InsertarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
//        listakardex = new LinkedList<>();
//        for (Kardexbanco k : listakardex) {
////            listaSeleccionada.add(k);
//            listakardex.remove(i++);
//        }
        recargar();
        return null;
    }

    public String retirarTodas() {
//        if (listaSeleccionadab == null) {
//            listaSeleccionadab = new LinkedList<>();
//        }
        try {
            if (spi.getId() == null) {

                ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());

            }
            int i = 0;
            for (Kardexbanco k : listaSeleccionada) {
//            listaSeleccionada.add(k);
                k.setBanco(null);
                k.setSpi(null);
                ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());
//            listakardex.remove(i++);

            }
        } catch (GrabarException | InsertarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        recargar();
//        int i = 0;
//        for (Kardexbanco k : listaSeleccionada) {
//            listaSeleccionadab.add(k);
//            listakardex.add(k);
////            listaSeleccionada.remove(i++);
//        }
//        listaSeleccionada = new LinkedList<>();
////        for (Kardexbanco k : listakardex) {
//////            listaSeleccionadab.add(k);
//////            listakardex.add(k);
////            listaSeleccionada.remove(i++);
////        }
        return null;
    }

    public String retirarSeleccionadas() {
//        if (listaSeleccionadab == null) {
//            listaSeleccionadab = new LinkedList<>();
//        }
        try {
            if (spi.getId() == null) {

                ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());

            }
            int i = 0;
            for (Kardexbanco k : listaSeleccionada) {
//            listaSeleccionada.add(k);
                if (k.isSeleccionar()) {
                    k.setBanco(null);
                    k.setSpi(null);
                    ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());

                }
//            listakardex.remove(i++);
            }
        } catch (GrabarException | InsertarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        recargar();
//        int i = 0;
//        for (Kardexbanco k : listaSeleccionada) {
//            listaSeleccionadab.add(k);
//            listakardex.add(k);
////            listaSeleccionada.remove(i++);
//        }
//        listaSeleccionada = new LinkedList<>();
////        for (Kardexbanco k : listakardex) {
//////            listaSeleccionadab.add(k);
//////            listakardex.add(k);
////            listaSeleccionada.remove(i++);
////        }
        return null;
    }

    /**
     * @return the formularioKardex
     */
    public Formulario getFormularioKardex() {
        return formularioKardex;
    }

    /**
     * @param formularioKardex the formularioKardex to set
     */
    public void setFormularioKardex(Formulario formularioKardex) {
        this.formularioKardex = formularioKardex;
    }

    /**
     * @return the formularioSelecciona
     */
    public Formulario getFormularioSelecciona() {
        return formularioSelecciona;
    }

    /**
     * @param formularioSelecciona the formularioSelecciona to set
     */
    public void setFormularioSelecciona(Formulario formularioSelecciona) {
        this.formularioSelecciona = formularioSelecciona;
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
     * @return the recursoTxt
     */
    public Resource getRecursoTxt() {
        return recursoTxt;
    }

    /**
     * @param recursoTxt the recursoTxt to set
     */
    public void setRecursoTxt(Resource recursoTxt) {
        this.recursoTxt = recursoTxt;
    }

    /**
     * @return the listaAuxiliar
     */
    public List<AuxiliarSpi> getListaAuxiliar() {
        return listaAuxiliar;
    }

    /**
     * @param listaAuxiliar the listaAuxiliar to set
     */
    public void setListaAuxiliar(List<AuxiliarSpi> listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    /**
     * @return the formularioColoca
     */
    public Formulario getFormularioColoca() {
        return formularioColoca;
    }

    /**
     * @param formularioColoca the formularioColoca to set
     */
    public void setFormularioColoca(Formulario formularioColoca) {
        this.formularioColoca = formularioColoca;
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
     * @return the listaTotales
     */
    public List<Auxiliar> getListaTotales() {
        return listaTotales;
    }

    /**
     * @param listaTotales the listaTotales to set
     */
    public void setListaTotales(List<Auxiliar> listaTotales) {
        this.listaTotales = listaTotales;
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
     * @return the spiMd5
     */
    public String getSpiMd5() {
        return spiMd5;
    }

    /**
     * @param spiMd5 the spiMd5 to set
     */
    public void setSpiMd5(String spiMd5) {
        this.spiMd5 = spiMd5;
    }

    public void arreglarEmpleado(Kardexbanco k) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.kardexbanco=:kardex");
            parametros.put("kardex", k);
            List<Prestamos> lista = ejbPrestamos.encontarParametros(parametros);
            Prestamos p = new Prestamos();
            for (Prestamos p1 : lista) {
                p = p1;
            }
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Codigos codigoBanco = traerBanco(p.getEmpleado());
            k.setBancotransferencia(codigoBanco);
            k.setCuentatrans(traer(p.getEmpleado(), "NUMCUENTA"));
//                    k.setDocumento(kardex.getDocumento());
            k.setDocumento(p.getEmpleado().getEntidad().getPin());
            String tipoCta = traerTipoCuenta(p.getEmpleado());
            k.setTcuentatrans(Integer.parseInt(tipoCta));
            k.setValor(new BigDecimal(p.getValor()));
            ejbKardexbanco.edit(k, seguridadbean.getLogueado().getUserid());

        } catch (ConsultarException | GrabarException ex) {
            Logger.getLogger(SpiBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
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
                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getParametros();
                }
                return retorno;

            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
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
                return getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());

            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
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
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the recursoXls
     */
    public Resource getRecursoXls() {
        return recursoXls;
    }

    /**
     * @param recursoXls the recursoXls to set
     */
    public void setRecursoXls(Resource recursoXls) {
        this.recursoXls = recursoXls;
    }

    // tratar de buscar en la tabla y seleccionar la fila
//    public void find(javax.faces.event.ActionEvent e) {
//        DataTable iceTable = ((SpiBean) (FacesUtils.getManagedBean("spiSfccbdmq"))).getTable(this.getClass());
//
//        DataTable.SearchType type = null;
//        if (selectedSearchMode.equals("contains")) {
//            type = DataTable.SearchType.CONTAINS;
//        } else if (selectedSearchMode.equals("startsWith")) {
//            type = DataTable.SearchType.STARTS_WITH;
//        } else if (selectedSearchMode.equals("endsWith")) {
//            type = DataTable.SearchType.ENDS_WITH;
//        } else if (selectedSearchMode.equals("exact")) {
//            type = DataTable.SearchType.EXACT;
//        } else {
//            type = DataTable.SearchType.CONTAINS;
//        }
//
//        int newFoundIndex = iceTable.findRow(searchQuery, selectedColumns, lastFoundIndex + 1, type, caseSensitive);
//
//        if (newFoundIndex < 0) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage(iceTable.getClientId(context),
//                    new FacesMessage("Search starting at index " + (lastFoundIndex + 1) + " for \"" + searchQuery + "\" did not return a result."));
//            return;
//        }
//
//        lastFoundIndex = newFoundIndex;
//
//        if (selectedEffectType.equals("default")) {
//            iceTable.navigateToRow(lastFoundIndex);
//        } else if (selectedEffectType.equals("pulsate")) {
//            iceTable.navigateToRow(lastFoundIndex, DataTable.SearchEffect.PULSATE);
//        } else if (selectedEffectType.equals("none")) {
//            iceTable.navigateToRow(lastFoundIndex, null);
//        }
//    }
    /**
     * @return the propuesta
     */
    public String getPropuesta() {
        return propuesta;
    }

    /**
     * @param propuesta the propuesta to set
     */
    public void setPropuesta(String propuesta) {
        this.propuesta = propuesta;
    }

    public SelectItem[] getComboPropuestas() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.banco is null");
            parametros.put(";campo", "o.propuesta");
            parametros.put(";suma", "sum(o.valor)");
            List<Object[]> lista = ejbKardexbanco.sumar(parametros);
            int size = lista.size();
//            int size = lista.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
//            items[0] = new SelectItem(null, "---");
//            i++;
            DecimalFormat df = new DecimalFormat("###,##0.00");

            for (Object[] o : lista) {
                String nombre = (String) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                items[i++] = new SelectItem(nombre, nombre + "  (" + df.format(valor.doubleValue()) + ")");

            }
            return items;

        } catch (ConsultarException ex) {
            Logger.getLogger(KardexPagosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void cambiaValor(ValueChangeEvent ve) {
        String cambia = (String) ve.getNewValue();
        if (cambia != null) {
            propuesta = cambia;
            recargar();
        } else {
            propuesta = null;
            recargarNulas();
        }
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
     * @return the recursoPdf
     */
    public Resource getRecursoPdf() {
        return recursoPdf;
    }

    /**
     * @param recursoPdf the recursoPdf to set
     */
    public void setRecursoPdf(Resource recursoPdf) {
        this.recursoPdf = recursoPdf;
    }
}
