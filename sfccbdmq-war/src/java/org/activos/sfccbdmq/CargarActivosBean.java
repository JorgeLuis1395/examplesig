/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import com.lowagie.text.DocumentException;
import org.compras.sfccbdmq.ProveedoresBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.beans.sfccbdmq.ActasFacade;
import org.beans.sfccbdmq.ActasactivosFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.GrupoactivosFacade;
import org.beans.sfccbdmq.MarcasFacade;
import org.beans.sfccbdmq.SubgruposactivosFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Actasactivos;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Subgruposactivos;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.CodigosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cargarActivosSfccbdmq")
@ViewScoped
public class CargarActivosBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public CargarActivosBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImagen = new Formulario();
    private Perfiles perfil;
    private String separador = ";";
    private List errores;
    private Activos activo;
    private Grupoactivos grupo;
    private List<Activos> listaActivo;
    private List<Activos> listaActivoControl;
    private List<Activos> listaActivoFijo;
    private List<Imagenes> listaImagenes = new LinkedList<>();
//    private List<Imagenes> listaImagenesAntes = new LinkedList<>();
    private String nombrepdf;
    private Contratos contrato;
    private int f1 = 0;
    private int f2 = 0;
    private int f3 = 0;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private GrupoactivosFacade ejbGrupos;
    @EJB
    private SubgruposactivosFacade ejbSubGrupo;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ActasactivosFacade ejbActasActivos;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private MarcasFacade ejbMarcas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{actasActivosSfccbdmq}")
    private ActasActivosBean actasActivosBean;
    private Recurso reportepdf;
    private DocumentoPDF pdf;
    private Formulario formularioImprimir = new Formulario();
    private Archivos archivoImagen;
//    private Imagenes imagen;
//    private Imagenes imagenActivo;
    private String numeroActa;
    private double totalControl;
    private double totalActivo;
    private int tipoCompra = 0;

    private String nombreArchivo;

    private boolean validar() {
//        if ((activo.getCodigo() == null) || (activo.getCodigo().isEmpty())) {
//            MensajesErrores.advertencia("Código es Obligatorio es obligatorio");
//            return true;
//        }

//        if (grupo == null) {
//            MensajesErrores.advertencia("Grupo es obligatorio");
//            return true;
//        }
//        activo.setGrupo(grupo);
//        if (activo.getSubgrupo() == null) {
//            MensajesErrores.advertencia("Sub Grupo es obligatorio");
//            return true;
//        }
        if (activo.getEstado() == null) {
            MensajesErrores.advertencia("Estado es obligatorio");
            return true;
        }
        if (activo.getClasificacion() == null) {
            MensajesErrores.advertencia("Clasificación es obligatoria");
            return true;
        }
        if (proveedoresBean.getEmpresa() != null) {
            if (tipoCompra == 3) {
                MensajesErrores.advertencia("Seleccione un Tipo de Compra");
                return true;
            }
//            if (tipoCompra == 0) {
//                if (f1 == 0 || f2 == 0 || f3 == 0) {
//                    MensajesErrores.advertencia("Factura es obligatorio");
//                    return true;
//                }
//            }
//            if (tipoCompra == 1) {
//                if (f3 == 0) {
//                    MensajesErrores.advertencia("Factura es obligatorio");
//                    return true;
//                }
//            }
        }

        return false;
    }

    public String insertar() {
        numeroActa = null;
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }
        if (listaActivo == null) {
            MensajesErrores.advertencia("No hay nada que grabar");
            return null;
        }
        if (listaActivo.isEmpty()) {
            MensajesErrores.advertencia("No hay nada que grabar");
            return null;
        }
        if (!errores.isEmpty()) {
            MensajesErrores.advertencia("Existen errores no es posible grabar");
            return null;
        }
        if (validar()) {
            return null;
        }

        try {
            Integer numero = 0;
            Trackingactivos t = new Trackingactivos();
//            DecimalFormat df = new DecimalFormat("0000000000");

//            *************************************incrementar para externo********
//Número de acta
            Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "06");
            if (tipoActa == null) {
                MensajesErrores.advertencia("No existe tipo de acta de código 06");
                return null;
            }
            Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "06");
            if (configuracion
                    == null) {
                MensajesErrores.advertencia("No existe configuración de acta de código 06");
                return null;
            }
            Actas actaNueva = new Actas();

            actaNueva.setAceptacion(configuracion.getNombre());
            actaNueva.setAntecedentes(configuracion.getDescripcion());
            actaNueva.setTipo(tipoActa);
            String numeroActaIngreso = tipoActa.getParametros();
            if ((numeroActaIngreso
                    == null) || (numeroActaIngreso.isEmpty())) {
                numeroActaIngreso = "1";
            }
            int num = Integer.parseInt(numeroActaIngreso);

            Date fecha = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            DecimalFormat df1 = new DecimalFormat("000000");
            actaNueva.setNumero(num);
            actaNueva.setNumerotexto(sdf.format(fecha) + df1.format(num));
            actaNueva.setReciben(seguridadbean.getLogueado().getUserid());
            actaNueva.setFecha(new Date());
            numeroActa = actaNueva.getNumerotexto();
            int nuevoNumero = num + 1;

            tipoActa.setParametros("" + nuevoNumero);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            Integer numeroActaAlta = Integer.parseInt(actaNueva.getNumerotexto());
            for (Activos a : listaActivo) {
                numero = a.getGrupo().getSecuencia();
                a.setDepreciable(a.getGrupo().getDepreciable());
                a.setVidautil(a.getGrupo().getVidautil());
                if (a.getControl()) {
                    a.setValorresidual(new Float(0));
                } else {
                    a.setValorresidual(a.getGrupo().getValorresidual().floatValue());
                }
                a.setFechaingreso(new Date());
                a.setFechaalta(getActivo().getFechaalta());
                a.setValoradquisiscion(a.getValoralta());
                a.setValorreposicion(a.getValoralta());
                a.setLocalizacion(getActivo().getLocalizacion());
                a.setEstado(getActivo().getEstado());
                a.setClasificacion(getActivo().getClasificacion());
                a.setCcosto(getActivo().getCcosto());
//                a.setFactura(getActivo().getFactura());
                a.setAlta(getActivo().getAlta());
                a.setActa(numeroActaAlta);
//                a.setControl(Boolean.FALSE);
                if (getProveedoresBean().getEmpresa() != null) {
                    getActivo().setProveedor(getProveedoresBean().getEmpresa().getProveedores());
                    a.setProveedor(getActivo().getProveedor());

                    DecimalFormat dfact = new DecimalFormat("000");
                    a.setContrato(contrato);
                    if (a.getFactura() == null || a.getFactura().isEmpty()) {
                        if (tipoCompra == 0) { //interna
                            a.setFactura(dfact.format(f1) + "-" + dfact.format(f2) + "-" + f3);
                        } else { //externa
                            a.setFactura("999-999-" + f3);
                        }
                    }

                } else {
                    a.setProveedor(null);
                }
                ejbActivos.create(a, getSeguridadbean().getLogueado().getUserid());

                for (Imagenes imagen : listaImagenes) {
                    if (a.getNombreImagen() != null) {
                        if (a.getNombreImagen().toUpperCase().equals(imagen.getNombre().toUpperCase())) {
                            imagen.setModulo("ACTIVOFOTO");
                            imagen.setIdmodulo(a.getId());
                            ejbimagenes.create(imagen);
                            Archivos archivoI = new Archivos();
                            archivoI.setImagen(imagen);
                            archivoI.setArchivo(archivoImagen.getArchivo());
                            ejbArchivo.create(archivoI);
                        }
                    }
                }
                a.setCodigo(a.getId() + "");
                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
                if (a.getCodigo() != null) {
                    if (a.getBarras() == null || a.getBarras().isEmpty()) {
                        a.setBarras(a.getCodigo());
                    }
                    if (a.getNumeroserie() == null || a.getNumeroserie().isEmpty()) {
                        a.setNumeroserie(a.getCodigo());
                    }
                }
                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());

                t.setActivo(a);
                t.setDescripcion("CREACION DE ACTIVO");
                t.setFecha(new Date());
                t.setTipo(2);
                t.setUsuario(getSeguridadbean().getLogueado().getUserid());
                t.setValor(a.getValoralta() == null ? 0 : a.getValoralta().floatValue());
                t.setValornuevo(a.getValoradquisiscion() == null ? 0 : a.getValoradquisiscion().floatValue());
                t.setCuenta1("SIN USUARIO");
                t.setCuenta2("INGRESO A BOGEGA");
                t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                Grupoactivos g = a.getGrupo();
                g.setSecuencia(numero);
                ejbGrupos.edit(g, getSeguridadbean().getLogueado().getUserid());
            }
            for (Activos a : listaActivo) {
                Actasactivos aa = new Actasactivos();
                aa.setActa(actaNueva);
                aa.setActivo(a);
                ejbActasActivos.create(aa, seguridadbean.getLogueado().getUserid());
            }
            actasActivosBean.imprime(actaNueva);
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
//        reporte(listaActivo);
        listaActivo = null;
        formulario.cancelar();
        listaActivo = new LinkedList<>();
        listaActivoControl = new LinkedList<>();
        listaActivoFijo = new LinkedList<>();
        listaImagenes = new LinkedList<>();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc("");
        contrato = null;
        totalActivo = 0;
        totalControl = 0;
        f1 = 0;
        f2 = 0;
        f3 = 0;
        return null;
    }

    public String multimediaListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                File file = i.getFile();
//                if (archivoImagen == null) {
//                    archivoImagen = new Archivos();
//                } else {
//                    if (archivoImagen.getId() == null) {
//                        archivoImagen = new Archivos();
//                    }
//                }
//                if (imagen == null) {
//                    imagen = new Imagenes();
//                } else {
//                    if (imagen.getId() == null) {
//                        imagen = new Imagenes();
//                    }
//                }
                Imagenes imagen = new Imagenes();
                imagen.setModulo("ACTIVOFOTO");
                imagen.setIdmodulo(0);
                imagen.setNombre(i.getFileName());
                imagen.setFechaingreso(new Date());
                imagen.setTipo("FOTO");
                archivoImagen = new Archivos();
                archivoImagen.setArchivo(Files.readAllBytes(file.toPath()));
//                archivoImagen.setImagen(imagen);
//                ejbArchivo.create(archivoImagen);
                listaImagenes.add(imagen);

//                archivoImagen.setTipo(i.getContentType());
            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String nombreForma = "CargarActvivosVista";
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
        setActivo(new Activos());

//        Map parametros = new HashMap();
//        parametros.put(";where", "o.idmodulo=0");
//        listaImagenesAntes = ejbimagenes.encontarParametros(parametros);
    }

    public String salir() {
        formulario.cancelar();
        formularioImprimir.cancelar();
        listaActivo = new LinkedList<>();
        listaActivoControl = new LinkedList<>();
        listaActivoFijo = new LinkedList<>();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc("");
        contrato = null;
        totalActivo = 0;
        totalControl = 0;
        f1 = 0;
        f2 = 0;
        f3 = 0;
        return null;
    }

    public void setErrores(List errores) {
        this.errores = errores;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listaActivo = new LinkedList<>();
        listaActivoControl = new LinkedList<>();
        listaActivoFijo = new LinkedList<>();
        errores = new LinkedList();
        totalActivo = 0;
        totalControl = 0;
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();
                    BufferedReader entrada = null;
                    try {
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));
                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            Activos a = new Activos();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(a, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.codigo=:codigo");
                            parametros.put("codigo", a.getActivoMarc());
                            List<Marcas> lista = ejbMarcas.encontarParametros(parametros);
                            if (lista.isEmpty()) {
                                getErrores().add("No existe el codigo de Marca " + a.getMarca() + " en el registro " + String.valueOf(registro + 1));
                            } else {
                                Marcas m = lista.get(0);
                                a.setActivomarca(m);
                            }
                            parametros = new HashMap();
                            parametros.put(";where", "o.codigo=:codigo");
                            parametros.put("codigo", a.getSubgrupoSrt());
                            List<Subgruposactivos> listaSubGrupos = ejbSubGrupo.encontarParametros(parametros);
                            if (listaSubGrupos.isEmpty()) {
                                getErrores().add("No existe el codigo de Sub Grupo " + a.getSubgrupoSrt() + " en el registro " + String.valueOf(registro + 1));
                            } else {
                                Subgruposactivos sga = listaSubGrupos.get(0);
                                a.setSubgrupo(sga);
                                a.setGrupo(sga.getGrupo());
                            }
                            double valorActaFinal = 0;
                            if (a.getControl()) {
                                listaActivoControl.add(a);
                                if (a.getValorAltaSinIva() != null) {
                                    if (a.getIva() != null) {
                                        if (a.getIva().doubleValue() != 0.00) {
//                                            totalControl += (a.getValorAltaSinIva().doubleValue() + (a.getValorAltaSinIva().doubleValue() * a.getIva().doubleValue()));
                                            valorActaFinal = (a.getValorAltaSinIva().doubleValue() + (a.getValorAltaSinIva().doubleValue() * a.getIva().doubleValue()));
                                            a.setValoralta(new BigDecimal(valorActaFinal));
                                        } else {
//                                            totalControl += a.getValorAltaSinIva().doubleValue();
                                            valorActaFinal = a.getValorAltaSinIva().doubleValue();
                                            a.setValoralta(new BigDecimal(valorActaFinal));
                                        }
                                    } else {
//                                        totalControl += a.getValorAltaSinIva().doubleValue();
                                        valorActaFinal = a.getValorAltaSinIva().doubleValue();
                                        a.setValoralta(new BigDecimal(valorActaFinal));
                                    }
                                    totalControl += valorActaFinal;
                                }
                            } else {
                                listaActivoFijo.add(a);
                                if (a.getValorAltaSinIva() != null) {
                                    if (a.getIva() != null) {
                                        if (a.getIva().doubleValue() != 0.00) {
//                                            totalActivo += (a.getValorAltaSinIva().doubleValue() + (a.getValorAltaSinIva().doubleValue() * a.getIva().doubleValue()));
                                            valorActaFinal = (a.getValorAltaSinIva().doubleValue() + (a.getValorAltaSinIva().doubleValue() * a.getIva().doubleValue()));
                                            a.setValoralta(new BigDecimal(valorActaFinal));
                                        } else {
//                                            totalActivo += a.getValorAltaSinIva().doubleValue();
                                            valorActaFinal = a.getValorAltaSinIva().doubleValue();
                                            a.setValoralta(new BigDecimal(valorActaFinal));
                                        }
                                    } else {
//                                        totalActivo += a.getValorAltaSinIva().doubleValue();
                                        valorActaFinal = a.getValorAltaSinIva().doubleValue();
                                        a.setValoralta(new BigDecimal(valorActaFinal));
                                    }
                                    totalActivo += valorActaFinal;
                                }
                            }
                            listaActivo.add(a);
                            registro++;
                            // ver si esta bien el registro o es error 
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarActivosBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarActivosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
            }
        }
    }

    private void ubicar(Activos am, String titulo, String valor) {
        String tituloNuevo = reemplazarCaracteresRaros(titulo).toLowerCase();

        if (titulo.contains("descripcion")) {
            am.setDescripcion(valor);
        } else if (tituloNuevo.contains("codigomarca")) {
            am.setActivoMarc(valor);
        } else if (tituloNuevo.contains("modelo")) {
            am.setModelo(valor);
        } else if (tituloNuevo.contains("color")) {
            am.setColor(valor);
        } else if (tituloNuevo.contains("barras")) {
            am.setBarras(valor);
        } else if (tituloNuevo.contains("nombre")) {
            am.setNombre(valor);
        } else if (tituloNuevo.contains("numeroserie")) {
            am.setNumeroserie(valor);
        } else if (tituloNuevo.contains("observaciones")) {
            am.setObservaciones(valor);
//        } else if (tituloNuevo.contains("valor")) {
//            am.setValoralta(new BigDecimal(valor.trim()));
        } else if (tituloNuevo.contains("valor")) {
            am.setValorAltaSinIva(new BigDecimal(valor.trim()));
        } else if (tituloNuevo.contains("iva")) {
            am.setIva(new BigDecimal(valor.trim()));
        } else if (tituloNuevo.contains("control")) {
            if (valor.trim().contains("0")) {
                am.setControl(Boolean.TRUE);
            } else {
                am.setControl(Boolean.FALSE);
            }
//        } else if (tituloNuevo.contains("grupo")) {
//            am.setGrupoSrt(valor);
        } else if (tituloNuevo.contains("subgrupo")) {
            am.setSubgrupoSrt(valor);
            //Vehiculos
        } else if (tituloNuevo.contains("chasis")) { //vehiculos- marca
            am.setMarca(valor);
        } else if (tituloNuevo.contains("motor")) {//vehiculos- dimensiones
            am.setDimensiones(valor);
        } else if (tituloNuevo.contains("placa")) {//vehiculos- caracteristicas
            am.setCaracteristicas(valor);
        } else if (tituloNuevo.contains("tipo")) {
            am.setTipoauxiliar(valor);
        } else if (tituloNuevo.contains("nominativo")) {
            am.setNominativo(valor);
        } else if (tituloNuevo.contains("factura")) {
            am.setFactura(valor);
        } else if (tituloNuevo.contains("imagen")) {
            am.setNombreImagen(valor);
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
    /**
     * Función que elimina acentos y caracteres especiales de una cadena de
     * texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public String reemplazarCaracteresRaros(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ/%&,.#+-¡?¿()!°|[]";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC                  ";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
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
     * @return the activo
     */
    public Activos getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(Activos activo) {
        this.activo = activo;
    }

    /**
     * @return the listaActivo
     */
    public List<Activos> getListaActivo() {
        return listaActivo;
    }

    /**
     * @param listaActivo the listaActivo to set
     */
    public void setListaActivo(List<Activos> listaActivo) {
        this.listaActivo = listaActivo;
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

    public void cambiaGrupo(ValueChangeEvent ve) {
        Grupoactivos valor = (Grupoactivos) ve.getNewValue();
        if (valor != null) {
//            if (activo==null){
//                activo=new Activos();
//            }
            getActivo().setValorresidual(valor.getValorresidual().floatValue());
            getActivo().setDepreciable(valor.getDepreciable());
            getActivo().setVidautil(valor.getVidautil());
            subgrupoBean.setGrupo(valor);
            subgrupoBean.getComboSubGrupo();
        }
    }

    public SelectItem[] getComboSubGrupo() {
        if (grupo == null) {
            return null;
        }
//        if (activo.getGrupo() == null) {
//            return null;
//        }
        try {

//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.grupo=:grupo");
            parametros.put("grupo", grupo);
            return Combos.getSelectItems(ejbSubGrupo.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger
                    .getLogger(SubGruposBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the subgrupoBean
     */
    public SubGruposBean getSubgrupoBean() {
        return subgrupoBean;
    }

    /**
     * @param subgrupoBean the subgrupoBean to set
     */
    public void setSubgrupoBean(SubGruposBean subgrupoBean) {
        this.subgrupoBean = subgrupoBean;
    }

    public SelectItem[] getComboGrupoEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
//            grupos = ejbGrupo.encontarParametros(parametros);
            return Combos.getSelectItems(ejbGrupos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger
                    .getLogger(GrupoActivosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the grupo
     */
    public Grupoactivos getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the pdf
     */
    public DocumentoPDF getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(DocumentoPDF pdf) {
        this.pdf = pdf;
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

    public String reporte(List<Activos> activos) {
        listaActivo = activos;
        try {
            pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("\n", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("ACTIVOS FIJOS", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("\n", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("INGRESO DE BIENES", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("\n", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto(("Acta Nro. " + (numeroActa != null ? numeroActa : "")), AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("\n\n\n\n", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("DETALLE DE LOS BIENES", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("\n", AuxiliarReporte.ALIGN_CENTER, 9, false);

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "CODIGO" + "\n" + "ACTIVOS"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "NOMBRE"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "MARCA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "MODELO"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "ESTADO DEL BIEN"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "SERIE"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "OBSERVACIÓN"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 8, true, "VALOR"));
            //Bien de control
//            Double SubtotalBienC = 0.00;
            Double IvaBienC = 0.00;
            Double totalIvaBienC = 0.00;
            Double totalSinIvaBienC = 0.00;
            Double totalBienC = 0.00;
            Integer numeroBienC = 0;
            for (Activos a : listaActivo) {
                if (a.getControl()) {
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCodigo() != null ? a.getCodigo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getNombre() != null ? a.getNombre() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getActivomarca() != null ? a.getActivomarca().getNombre() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getModelo() != null ? a.getModelo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getEstado() != null ? a.getEstado().getNombre() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getNumeroserie() != null ? a.getNumeroserie() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getDescripcion() != null ? a.getDescripcion() : ""));
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 8, false, a.getValoralta() != null
                            ? (a.getIva() != null ? (a.getValoralta().doubleValue() + (a.getValoralta().doubleValue() * a.getIva().doubleValue())) : a.getValoralta().doubleValue()) : ""));
                    numeroBienC++;
                    if (!(a.getIva() == null && a.getId().equals(0.00))) {
                        IvaBienC += a.getValoralta().doubleValue() * a.getIva().doubleValue();
                        totalIvaBienC += a.getValoralta().doubleValue() + (a.getValoralta().doubleValue() * a.getIva().doubleValue());
                    } else {
                        totalSinIvaBienC += a.getValoralta().doubleValue();
                    }
                    listaActivoControl.add(a);
                }
            }
            totalBienC = totalIvaBienC + totalSinIvaBienC;
            pdf.agregarTablaReporte(titulos, valores, 8, 100, "Bien de Control");
            pdf.agregaParrafo("\n");
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total Bien de Control: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroBienC + ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBienC));
            pdf.agregarTabla(null, valores, 3, 100, "");
            pdf.agregaParrafo("\n");
            pdf.agregaParrafo("\n");

            //Activos Fijo
            valores = new LinkedList<>();
//            Double SubtotalAF = 0.00;
            Double IvaAF = 0.00;
            Double totalIvaAF = 0.00;
            Double totalSinIvaAF = 0.00;
            Double totalAF = 0.00;
            Integer numeroAF = 0;
            for (Activos a : listaActivo) {
                activo = a;
                if (!a.getControl()) {
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCodigo() != null ? a.getCodigo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getDescripcion() != null ? a.getDescripcion() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getActivomarca() != null ? a.getActivomarca().getNombre() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getModelo() != null ? a.getModelo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getEstado() != null ? a.getEstado().getNombre() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getNumeroserie() != null ? a.getNumeroserie() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getDescripcion() != null ? a.getDescripcion() : ""));
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 8, false, a.getValoralta() != null
                            ? (a.getIva() != null ? (a.getValoralta().doubleValue() + (a.getValoralta().doubleValue() * a.getIva().doubleValue())) : a.getValoralta().doubleValue()) : ""));
//                    SubtotalAF += a.getValoralta().doubleValue();
                    numeroAF++;
                    if (!(a.getIva() == null && a.getId().equals(0.00))) {
                        IvaAF += a.getValoralta().doubleValue() * a.getIva().doubleValue();
                        totalIvaAF += a.getValoralta().doubleValue() + (a.getValoralta().doubleValue() * a.getIva().doubleValue());
                    } else {
                        totalSinIvaAF += a.getValoralta().doubleValue();
                    }
                    listaActivoFijo.add(a);
                }
            }
            totalAF = totalIvaAF + totalSinIvaAF;
            pdf.agregarTablaReporte(titulos, valores, 8, 100, "Activos Fijos");
            pdf.agregaParrafo("\n");
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total Activos Fijo: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroAF + ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalAF));
            pdf.agregarTabla(null, valores, 3, 100, "");
            pdf.agregaParrafo("\n");
            pdf.agregaParrafo("\n");
            valores = new LinkedList<>();
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Subtotal: "));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, SubtotalBienC + SubtotalAF));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Subtotal: "));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, IvaBienC + IvaAF));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, activos.size() + ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBienC + totalAF));
            pdf.agregarTabla(null, valores, 3, 100, "");
            pdf.agregaParrafo("\n");
            Codigos codigoTexto = codigosBean.traerCodigo(Constantes.TEXTO_REGISTRO_ACTIVOS, "01");
            if (codigoTexto == null) {
                MensajesErrores.advertencia("No existe texto de reglamento");
                return null;
            }
            String texto = codigoTexto.getParametros();
            pdf.agregaParrafoCompleto(texto, AuxiliarReporte.ALIGN_JUSTIFIED, 6, false);
            pdf.agregaParrafo("\n");
//            Empleados e = null;
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.cargoactual.descripcion='RESPONSABLE DE UNIDAD DE BIENES'");
//            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
//            if (!lista.isEmpty()) {
//                e = lista.get(0);
//            }
            Codigos bien = codigosBean.traerCodigo(Constantes.RESPONSABLE_UNIDAD, "01");
            if (bien == null) {
                MensajesErrores.advertencia("No existe Responsable de Bienes");
                return null;
            }
            String adm = null;
            String ciAdm = null;
            if (activo.getContrato() != null) {
                adm = activo.getContrato().getAdministrador().toString();
                ciAdm = activo.getContrato().getAdministrador().getPin();
            }
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "ENTREGUE CONFORME"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "UNIDADES DE BIENES"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "ELABORADO POR"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "ADMINISTRADOR DEL CONTRATO\n" + (adm != null ? adm : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "RESPONSABLE DE LA UNIDAD DE BIENES\n" + (bien.getDescripcion() != null ? bien.getDescripcion() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, seguridadbean.getLogueado().toString()));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + (ciAdm != null ? ciAdm : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + (bien.getParametros() != null ? bien.getParametros() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + (seguridadbean.getLogueado().getPin())));
            pdf.agregarTabla(null, valores, 3, 100, "");

            reportepdf = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(CargarActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nombrepdf = "Ingreso de Bienes-" + numeroActa + ".pdf";
        formularioImprimir.insertar();
        return null;
    }

    public String borrarDocumento(Imagenes i) {
        listaImagenes.remove(i);
        formularioImagen.eliminar();
        return null;
    }

    public String ficheroDocumentoLista(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        if (getListaImagenes() == null) {
            setListaImagenes(new LinkedList<>());
        }
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            Imagenes d = new Imagenes();
            getListaImagenes().add(d);
        }
        return null;
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
     * @return the archivoImagen
     */
    public Archivos getArchivoImagen() {
        return archivoImagen;
    }

    /**
     * @param archivoImagen the archivoImagen to set
     */
    public void setArchivoImagen(Archivos archivoImagen) {
        this.archivoImagen = archivoImagen;
    }
//
//    /**
//     * @return the imagen
//     */
//    public Imagenes getImagen() {
//        return imagen;
//    }
//
//    /**
//     * @param imagen the imagen to set
//     */
//    public void setImagen(Imagenes imagen) {
//        this.imagen = imagen;
//    }

    /**
     * @return the numeroActa
     */
    public String getNumeroActa() {
        return numeroActa;
    }

    /**
     * @param numeroActa the numeroActa to set
     */
    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    /**
     * @return the nombrepdf
     */
    public String getNombrepdf() {
        return nombrepdf;
    }

    /**
     * @param nombrepdf the nombrepdf to set
     */
    public void setNombrepdf(String nombrepdf) {
        this.nombrepdf = nombrepdf;
    }

    /**
     * @return the f1
     */
    public int getF1() {
        return f1;
    }

    /**
     * @param f1 the f1 to set
     */
    public void setF1(int f1) {
        this.f1 = f1;
    }

    /**
     * @return the f2
     */
    public int getF2() {
        return f2;
    }

    /**
     * @param f2 the f2 to set
     */
    public void setF2(int f2) {
        this.f2 = f2;
    }

    /**
     * @return the f3
     */
    public int getF3() {
        return f3;
    }

    /**
     * @param f3 the f3 to set
     */
    public void setF3(int f3) {
        this.f3 = f3;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the listaActivoControl
     */
    public List<Activos> getListaActivoControl() {
        return listaActivoControl;
    }

    /**
     * @param listaActivoControl the listaActivoControl to set
     */
    public void setListaActivoControl(List<Activos> listaActivoControl) {
        this.listaActivoControl = listaActivoControl;
    }

    /**
     * @return the listaActivoFijo
     */
    public List<Activos> getListaActivoFijo() {
        return listaActivoFijo;
    }

    /**
     * @param listaActivoFijo the listaActivoFijo to set
     */
    public void setListaActivoFijo(List<Activos> listaActivoFijo) {
        this.listaActivoFijo = listaActivoFijo;
    }

    /**
     * @return the totalControl
     */
    public double getTotalControl() {
        return totalControl;
    }

    /**
     * @param totalControl the totalControl to set
     */
    public void setTotalControl(double totalControl) {
        this.totalControl = totalControl;
    }

    /**
     * @return the totalActivo
     */
    public double getTotalActivo() {
        return totalActivo;
    }

    /**
     * @param totalActivo the totalActivo to set
     */
    public void setTotalActivo(double totalActivo) {
        this.totalActivo = totalActivo;
    }

    /**
     * @return the tipoCompra
     */
    public int getTipoCompra() {
        return tipoCompra;
    }

    /**
     * @param tipoCompra the tipoCompra to set
     */
    public void setTipoCompra(int tipoCompra) {
        this.tipoCompra = tipoCompra;
    }

    /**
     * @return the actasActivosBean
     */
    public ActasActivosBean getActasActivosBean() {
        return actasActivosBean;
    }

    /**
     * @param actasActivosBean the actasActivosBean to set
     */
    public void setActasActivosBean(ActasActivosBean actasActivosBean) {
        this.actasActivosBean = actasActivosBean;
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

//    /**
//     * @return the imagenActivo
//     */
//    public Imagenes getImagenActivo() {
//        return imagenActivo;
//    }
//
//    /**
//     * @param imagenActivo the imagenActivo to set
//     */
//    public void setImagenActivo(Imagenes imagenActivo) {
//        this.imagenActivo = imagenActivo;
//    }
    /**
     * @return the listaImagenes
     */
    public List<Imagenes> getListaImagenes() {
        return listaImagenes;
    }

    /**
     * @param listaImagenes the listaImagenes to set
     */
    public void setListaImagenes(List<Imagenes> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }
//
//    /**
//     * @return the listaImagenesAntes
//     */
//    public List<Imagenes> getListaImagenesAntes() {
//        return listaImagenesAntes;
//    }
//
//    /**
//     * @param listaImagenesAntes the listaImagenesAntes to set
//     */
//    public void setListaImagenesAntes(List<Imagenes> listaImagenesAntes) {
//        this.listaImagenesAntes = listaImagenesAntes;
//    }

    /**
     * @return the formularioImagen
     */
    public Formulario getFormularioImagen() {
        return formularioImagen;
    }

    /**
     * @param formularioImagen the formularioImagen to set
     */
    public void setFormularioImagen(Formulario formularioImagen) {
        this.formularioImagen = formularioImagen;
    }

}
