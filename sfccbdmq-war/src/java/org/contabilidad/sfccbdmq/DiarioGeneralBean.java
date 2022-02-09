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
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.math.BigDecimal;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "diarioGeneralSfccbdmq")
@ViewScoped
public class DiarioGeneralBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public DiarioGeneralBean() {
        Calendar c = Calendar.getInstance();

    }
    private List<Renglones> renglones;
    private List<Cabeceras> cabeceras;
    private boolean resumido = true;
    private List<AuxiliarCarga> totales;
    private List<AuxiliarMayor> listaMayor;
    private Formulario formulario = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabeceras;
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
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoreBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private Cabeceras cabecera;
    private Formulario formularioReporte = new Formulario();
    private Resource reporte;
    private int indice = 0;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;

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
        String nombreForma = "DiarioGeneralVista";
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

    public String imprimir() {
        try {
            //        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Diario General Expresado en : " + configuracionBean.getConfiguracion().getExpresado());
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        Calendar c = Calendar.getInstance();
//        setReporte(new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/LibroDiario.jasper"),
//                renglones, "LibroDiario" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            String usuario=seguridadbean.getLogueado().getUserid();    
            String empresa=configuracionBean.getConfiguracion().getNombre();
            String titulo="DIARIO GENERAL";
            String parTitulos="DESDE :" + sdf.format(desde) + 
                    " HASTA : " + sdf.format(hasta) + 
                    "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado();
            DocumentoPDF pdf;
            pdf = new DocumentoPDF(titulo, empresa, parTitulos, usuario,true);
//            DocumentoPDF pdf = new DocumentoPDF("DIARIO GENERAL\n Desde : "
//                    + sdf.format(desde) + " Hasta :" + sdf.format(hasta), null, seguridadbean.getLogueado().getUserid());
            for (Cabeceras c : cabeceras) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Fecha :"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,sdf.format(c.getFecha())));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Tipo :"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,c.getTipo().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Nombre :"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,c.getTipo().getNombre()));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Número :"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,c.getNumero().toString()));

                pdf.agregarTabla(null, columnas, 4, 100, null);
                pdf.agregaParrafo("Descripción :" + c.getDescripcion());
                // un parafo
                pdf.agregaParrafo("\n\n");
                List<AuxiliarReporte> titulos = new LinkedList<>();
                int totalColumnas = 6;
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
                if (!resumido) {
                    titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                    totalColumnas++;
                }
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
                columnas = new LinkedList<>();
                renglones = new LinkedList<>();
                traer(c);
                for (Renglones r : renglones) {
                    boolean bold = (r.getCuenta().equals("TOTAL"));
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getFecha()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getCuenta()));
                    Cuentas cuenta = cuentasBean.traerCodigo(r.getCuenta());
                    if (cuenta != null) {
                        columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, cuenta.getNombre()));
                    } else {
                        columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, ""));
                    }
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getReferencia()));
                    if (!resumido) {
                        columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getAuxiliarNombre()));
                    }
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, bold, r.getDebitos()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, bold, r.getCreditos()));
                }
                pdf.agregarTablaReporte(titulos, columnas, totalColumnas, 100, null);
                pdf.agregaParrafo("\n\n");

            }
            reporte = pdf.traerRecurso();
            nombreArchivo = "DiarioGeneral.pdf";
            tipoArchivo = "Exportar a PDF";
            tipoMime = "application/pdf";
            getFormularioReporte().insertar();
            traer(cabecera);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(DiarioGeneralBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoXLS xls = new DocumentoXLS("Diario general");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Tipo :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Número :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Descripción :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cuenta"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Referencia"));
            if (!resumido) {
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Auxiliar"));
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Debe"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Haber"));
            xls.agregarFila(columnas, true);

            for (Cabeceras c : cabeceras) {

                renglones = new LinkedList<>();
                traer(c);
                for (Renglones r : renglones) {
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sdf.format(c.getFecha())));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getTipo().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getTipo().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getNumero().toString()));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getDescripcion()));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getCuenta()));
                    Cuentas cuenta = cuentasBean.traerCodigo(r.getCuenta());
                    if (cuenta != null) {
                        columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cuenta.getNombre()));
                    } else {
                        columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
                    }
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getReferencia()));
                    if (!resumido) {
                        columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getAuxiliarNombre()));
                    }
                    columnas.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getDebitos()==null?0.0:r.getDebitos()));
                    columnas.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getCreditos()==null?0.0:r.getCreditos()));
                    xls.agregarFila(columnas, false);
                }

            }
            setNombreArchivo("DiarioGeneral.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            reporte = xls.traerRecurso();
            formularioReporte.insertar();
            getFormularioReporte().insertar();
            traer(cabecera);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(DiarioGeneralBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirAsiento() {
        try {
            //        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Diario General Expresado en : " + configuracionBean.getConfiguracion().getExpresado());
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        Calendar c = Calendar.getInstance();
//        setReporte(new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/LibroDiario.jasper"),
//                renglones, "LibroDiario" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("ASIENTO DE DIARIO GENERAL\n ", null, seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Fecha :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,sdf.format(cabecera.getFecha())));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Tipo :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cabecera.getTipo().getCodigo()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Nombre :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cabecera.getTipo().getNombre()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Número :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cabecera.getNumero().toString()));

            pdf.agregarTabla(null, columnas, 4, 100, null);
            // un parafo
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            int totalColumnas = 6;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            if (!resumido) {
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                totalColumnas++;
            }
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            pdf.agregaParrafo("Descripción :" + cabecera.getDescripcion());
            // un parafo
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();

//            traer(cabecera);
            for (Renglones r : renglones) {
                boolean bold = (r.getCuenta().equals("TOTAL"));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getFecha()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getCuenta()));
                Cuentas cuenta = cuentasBean.traerCodigo(r.getCuenta());
                if (cuenta != null) {
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, cuenta.getNombre()));
                } else {
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, ""));
                }
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getReferencia()));
                if (!resumido) {
                    columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, bold, r.getAuxiliarNombre()));
                }
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, bold, r.getDebitos()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, bold, r.getCreditos()));
            }
            pdf.agregarTablaReporte(titulos, columnas, totalColumnas, 100, null);
            pdf.agregaParrafo("\n\n");
            reporte = pdf.traerRecurso();
            nombreArchivo = "DiarioGeneral.pdf";
            tipoArchivo = "Exportar a PDF";
            tipoMime = "application/pdf";
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(DiarioGeneralBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.fecha asc,o.tipo.codigo,o.numero");
        parametros.put(";where", "o.fecha between :desde and :hasta");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        indice = 0;
        try {
            setCabeceras(ejbCabeceras.encontarParametros(parametros));
            if (!cabeceras.isEmpty()) {
                cabecera = getCabeceras().get(0);
                renglones = new LinkedList<>();
                traer(cabecera);

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DiarioGeneralBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String siguiente() {
        indice++;
        if (indice >= cabeceras.size() - 1) {
            indice = 0;
        }
        cabecera = cabeceras.get(indice);
        renglones = new LinkedList<>();
        traer(cabecera);
        return null;
    }

    public String anterior() {
        indice--;
        if (indice < 0) {
            indice = cabeceras.size() - 1;
        }
        cabecera = cabeceras.get(indice);
        renglones = new LinkedList<>();
        traer(cabecera);
        return null;
    }

    public String inicio() {
        indice = 0;
        cabecera = cabeceras.get(indice);
        renglones = new LinkedList<>();
        traer(cabecera);
        return null;
    }

    public String fin() {
        cabecera = cabeceras.get(indice);
        renglones = new LinkedList<>();
        traer(cabecera);
        return null;
    }

    public void valueChangeMethod(ValueChangeEvent e) {
        indice = (int) e.getNewValue();
        if (indice < 0) {
            indice = getCabeceras().size() - 1;
        }
        if (indice >= cabeceras.size() - 1) {
            indice = 0;
        }
        cabecera = cabeceras.get(indice);
        renglones = new LinkedList<>();
        traer(cabecera);
    }

    public String traer(Cabeceras c) {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.cuenta asc");
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", c);
        double credito = 0;
        double debito = 0;
        try {
            List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
            if (resumido) {

                for (Renglones r : listaRenglones) {
                    r.setNombre(cuentasBean.traerCodigo(r.getCuenta()).getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                        debito += r.getValor().doubleValue();
                    } else {
                        r.setCreditos(r.getValor().doubleValue() * -1);
                        credito += r.getValor().doubleValue() * -1;
                    }
                    estaEnRas(r);
                }
            } else {
                renglones = new LinkedList<>();
                for (Renglones r : listaRenglones) {
                    r.setNombre(cuentasBean.traerCodigo(r.getCuenta()).getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                        debito += r.getValor().doubleValue();
                    } else {
                        r.setCreditos(r.getValor().doubleValue() * -1);
                        credito += r.getValor().doubleValue() * -1;
                    }
                    // traer los auxiliares
                    if (r.getAuxiliar() != null) {
                        String xuNombre = empleadosBean.traerCedula(r.getAuxiliar());
                        if (xuNombre == null) {
                            Empresas e = proveedoreBean.taerRuc(r.getAuxiliar());
                            if (e == null) {
                                xuNombre = r.getAuxiliar();

                            } else {
                                xuNombre = e.toString();
                            }
                        }
                        r.setAuxiliarNombre(xuNombre);
                    }
                    renglones.add(r);
                }
//                setRenglones(listaRenglones);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DiarioGeneralBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Renglones r = new Renglones();
        r.setCuenta("TOTAL");
        r.setDebitos(debito);
        r.setCreditos(credito);
        renglones.add(r);
        return null;
    }

    public String buscarAnt() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.cabecera.fecha asc,o.cabecera.tipo.codigo,o.cabecera.numero,o.cuenta asc");
        parametros.put(";where", "o.cabecera.fecha between :desde and :hasta");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        double credito = 0;
        double debito = 0;
        try {
            List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
            if (resumido) {
                renglones = new LinkedList<>();
                for (Renglones r : listaRenglones) {
                    r.setNombre(cuentasBean.traerCodigo(r.getCuenta()).getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                        debito += r.getValor().doubleValue();
                    } else {
                        r.setCreditos(r.getValor().doubleValue() * -1);
                        credito += r.getValor().doubleValue() * -1;
                    }
                    estaEnRas(r);
                }
            } else {
                renglones = new LinkedList<>();
                for (Renglones r : listaRenglones) {
                    r.setNombre(cuentasBean.traerCodigo(r.getCuenta()).getNombre());
                    if (r.getValor().doubleValue() > 0) {
                        r.setDebitos(r.getValor().doubleValue());
                        debito += r.getValor().doubleValue();
                    } else {
                        r.setCreditos(r.getValor().doubleValue() * -1);
                        credito += r.getValor().doubleValue() * -1;
                    }
                    // traer los auxiliares
                    if (r.getAuxiliar() != null) {
                        String xuNombre = empleadosBean.traerCedula(r.getAuxiliar());
                        if (xuNombre == null) {
                            Empresas e = proveedoreBean.taerRuc(r.getAuxiliar());
                            if (e == null) {
                                xuNombre = r.getAuxiliar();

                            } else {
                                xuNombre = e.toString();
                            }
                        }
                        r.setAuxiliarNombre(xuNombre);
                    }
                    renglones.add(r);
                }
//                setRenglones(listaRenglones);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DiarioGeneralBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Renglones r = new Renglones();

        r.setDebitos(debito);
        r.setCreditos(credito);
        renglones.add(r);
        return null;
    }

    private void estaEnRas(Renglones r) {
        for (Renglones r1 : getRenglones()) {
            if ((r.getCuenta().equals(r1.getCuenta()) && (r.getCabecera().equals(r1.getCabecera())))) {
                double valorR = r.getValor().doubleValue();
                double valorR1 = r1.getValor().doubleValue();
                r1.setValor(new BigDecimal(valorR + valorR1));
                return;
            }
        }
        getRenglones().add(r);

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
     * @return the resumido
     */
    public boolean isResumido() {
        return resumido;
    }

    /**
     * @param resumido the resumido to set
     */
    public void setResumido(boolean resumido) {
        this.resumido = resumido;
    }

    /**
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
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
     * @return the cabecera
     */
    public Cabeceras getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
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
     * @return the cabeceras
     */
    public List<Cabeceras> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(List<Cabeceras> cabeceras) {
        this.cabeceras = cabeceras;
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

}
