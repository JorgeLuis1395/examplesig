/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.ProyectospoaFacade;
import org.entidades.sfccbdmq.Proyectospoa;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Organigrama;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cargaProyectos")
@ViewScoped
public class CargaProyectosBean {

    public CargaProyectosBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    private List errores;
    private String separador = ";";
    private int anioCarga;
    private Date fecha;

    private Formulario formulario = new Formulario();

    private List<Proyectospoa> listaProyectos;

    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private OrganigramaFacade ejbOrganigrama;

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anioCarga = c.get(Calendar.YEAR);
    }

    public String insertar() {
        formulario.insertar();
        if (!(getErrores() == null || getErrores().isEmpty())) {
            MensajesErrores.advertencia("Existen errores");
            return null;
        }
        if (listaProyectos == null || listaProyectos.isEmpty()) {
            MensajesErrores.advertencia("No existen registros para grabar");
            return null;
        }
        try {
            for (Proyectospoa p : listaProyectos) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.anio=:anio and o.codigo=:codigo");
                parametros.put("anio", p.getAnio());
                parametros.put("codigo", p.getCodigo());
                List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
                Proyectospoa pre = null;
                for (Proyectospoa pp : lpe) {
                    pre = pp;
                }
                if (pre != null) {
                    ejbProyectos.edit(p, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbProyectos.create(p, seguridadbean.getLogueado().getUserid());
                }

                String codigoBuscar = null;
                Proyectospoa proyectoPadre = null;
                if (!(p.getNivel() == null)) {
                    if (p.getNivel() == 1) {
                        p.setPadre(null);
                        p.setDireccion(null);
                    } else {
                        if (p.getNivel() == 2) {
                            codigoBuscar = p.getCodigo().substring(0, 2);
                        } else {
                            if (p.getNivel() == 3) {
                                codigoBuscar = p.getCodigo().substring(0, 4);
                            } else {
                                if (p.getNivel() == 4) {
                                    codigoBuscar = p.getCodigo().substring(0, 7);
                                }
                            }
                        }

                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo");
                        parametros.put("codigo", codigoBuscar.toUpperCase());
                        List<Proyectospoa> lProyectos = ejbProyectos.encontarParametros(parametros);
                        for (Proyectospoa pp : lProyectos) {
                            proyectoPadre = pp;
                        }
                        if (proyectoPadre != null) {
                            p.setPadre(proyectoPadre);
                        }
                    }
                    ejbProyectos.edit(p, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CargaProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(CargaProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
    }

    public String salir() {
        formulario.cancelar();
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listaProyectos = new LinkedList<>();
        errores = new LinkedList();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();
                    BufferedReader entrada = null;
                    try {
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        String sb;
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        int registro = 2;
                        while ((sb = entrada.readLine()) != null) {
                            Proyectospoa pu = new Proyectospoa();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(pu, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            Proyectospoa pr = new Proyectospoa();
                            pr.setActivo(Boolean.TRUE);
                            pr.setAnio(anioCarga);
                            pr.setFechacreacion(fecha);

                            Organigrama direccion = null;
                            Codigos cCpc = null;
                            Codigos cCompra = null;
                            Codigos cProducto = null;
                            Codigos cProcedimiento = null;
                            Codigos cRegimen = null;
                            Codigos cPresupuesto = null;
                            Map parametros;
                            registro = registro + 1;
                            if (!(pu.getCodigo() == null || pu.getCodigo().isEmpty())) {
                                pr.setCodigo(pu.getCodigo().toUpperCase());
                            } else {
                                getErrores().add("Código no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                            }
//                            if (!(pu.getAnio() == null)) {
//                                pr.setAnio(pu.getAnio());
//                            } else {
//                                getErrores().add("Año no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
//                            }
                            if (!(pu.getNombre() == null || pu.getNombre().isEmpty())) {
                                pr.setNombre(pu.getNombre());
                            } else {
                                getErrores().add("Nombre no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                            }
                            if (!(pu.getNivel() == null)) {
                                pr.setNivel(pu.getNivel());
                            } else {
                                getErrores().add("Nivel no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                            }
                            if (!(pu.getIngreso() == null)) {
                                pr.setIngreso(pu.getIngreso());
                            } else {
                                getErrores().add("Ingreso no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                            }
                            if (!(pu.getImputable() == null)) {
                                pr.setImputable(pu.getImputable());
                            } else {
                                getErrores().add("Imputable no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                            }
                            if (!(pu.getObservaciones() == null || pu.getObservaciones().isEmpty())) {
                                pr.setObservaciones(pu.getObservaciones());
                            }
                            if (!(pu.getNivel() == 1)) {
                                if (!(pu.getDireccion() == null || pu.getDireccion().isEmpty())) {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.codigo=:codigo");
                                    parametros.put("codigo", pu.getDireccion().toUpperCase());
                                    List<Organigrama> lorganigrama = ejbOrganigrama.encontarParametros(parametros);
                                    for (Organigrama cO : lorganigrama) {
                                        direccion = cO;
                                    }
                                    if (direccion == null) {
                                        getErrores().add("Dirección no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                                    } else {
                                        pr.setDireccion(direccion.getCodigo());
                                    }
                                }
                            }
                            if ((pu.getNivel() == 4)) {
                                if (!(pu.getCpc() == null || pu.getCpc().isEmpty())) {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.codigo=:codigo");
                                    parametros.put("codigo", pu.getCpc());
                                    List<Codigos> lcpc = ejbCodigos.encontarParametros(parametros);
                                    for (Codigos cC : lcpc) {
                                        cCpc = cC;
                                    }
                                    if (cCpc == null) {
                                        getErrores().add("CPC no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                                    } else {
                                        pr.setCpc(cCpc.getCodigo());
                                    }

                                    pr.setUnidad("UNI");
                                    pr.setFechapac(fecha);
                                }
                                if (!(pu.getTipocompra() == null || pu.getTipocompra().isEmpty())) {
                                    parametros = new HashMap();
                                    parametros.put(";where", " upper(o.nombre)=:nombre");
                                    parametros.put("nombre", pu.getTipocompra().toUpperCase());
                                    List<Codigos> lc = ejbCodigos.encontarParametros(parametros);
                                    for (Codigos cCom : lc) {
                                        cCompra = cCom;
                                    }
                                    if (cCompra == null) {
                                        getErrores().add("Tipo de Compra no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                                    } else {
                                        pr.setTipocompra(cCompra.getCodigo());
                                    }

                                    pr.setUnidad("UNI");
                                }
                                if (!(pu.getDetalle() == null || pu.getDetalle().isEmpty())) {
                                    pr.setDetalle(pu.getDetalle());
                                }
                                if (!(pu.getCantidad() == null)) {
                                    pr.setCantidad(pu.getCantidad());
                                }
                                if (!(pu.getValoriva() == null)) {
                                    pr.setValoriva(pu.getValoriva());
                                }
                                if (!(pu.getImpuesto() == null)) {
                                    pr.setImpuesto(pu.getImpuesto());
                                }
                                if (!(pu.getCuatrimestre1() == null)) {
                                    pr.setCuatrimestre1(pu.getCuatrimestre1());
                                }
                                if (!(pu.getCuatrimestre2() == null)) {
                                    pr.setCuatrimestre2(pu.getCuatrimestre2());
                                }
                                if (!(pu.getCuatrimestre3() == null)) {
                                    pr.setCuatrimestre3(pu.getCuatrimestre3());
                                }
                                if (!(pu.getTipoproducto() == null || pu.getTipoproducto().isEmpty())) {
                                    parametros = new HashMap();
                                    parametros.put(";where", " upper(o.nombre)=:nombre");
                                    parametros.put("nombre", pu.getTipoproducto().toUpperCase());
                                    List<Codigos> ltp = ejbCodigos.encontarParametros(parametros);
                                    for (Codigos cTP : ltp) {
                                        cProducto = cTP;
                                    }
                                    if (cProducto == null) {
                                        getErrores().add("Tipo de Producto no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                                    } else {
                                        pr.setTipoproducto(cProducto.getCodigo());
                                    }
                                }
                                if (!(pu.getCatalogoelectronico() == null)) {
                                    pr.setCatalogoelectronico(pu.getCatalogoelectronico());
                                }
                                if (!(pu.getProcedimientocontratacion() == null || pu.getProcedimientocontratacion().isEmpty())) {
                                    parametros = new HashMap();
                                    parametros.put(";where", " upper(o.nombre)=:nombre");
                                    parametros.put("nombre", pu.getProcedimientocontratacion().toUpperCase());
                                    List<Codigos> lpc = ejbCodigos.encontarParametros(parametros);
                                    for (Codigos cTP : lpc) {
                                        cProcedimiento = cTP;
                                    }
                                    if (cProcedimiento == null) {
                                        getErrores().add("Tipo de Procedimiento no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                                    } else {
                                        pr.setProcedimientocontratacion(cProcedimiento.getCodigo());
                                    }
                                }
                                if (!(pu.getFondobid() == null)) {
                                    pr.setFondobid(pu.getFondobid());
                                }
                                if (!(pu.getNumerooperacion() == null || pu.getNumerooperacion().isEmpty())) {
                                    pr.setNumerooperacion(pu.getNumerooperacion());
                                }
                                if (!(pu.getCodigooperacion() == null || pu.getCodigooperacion().isEmpty())) {
                                    pr.setCodigooperacion(pu.getCodigooperacion());
                                }
                                if (!(pu.getRegimen() == null || pu.getRegimen().isEmpty())) {
                                    parametros = new HashMap();
                                    parametros.put(";where", " upper(o.nombre)=:nombre");
                                    parametros.put("nombre", pu.getRegimen().toUpperCase());
                                    List<Codigos> lregimen = ejbCodigos.encontarParametros(parametros);
                                    for (Codigos cr : lregimen) {
                                        cRegimen = cr;
                                    }
                                    if (cRegimen == null) {
                                        getErrores().add("Régimen no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                                    } else {
                                        pr.setRegimen(cRegimen.getCodigo());
                                    }
                                }
                                if (!(pu.getPresupuesto() == null || pu.getPresupuesto().isEmpty())) {
                                    parametros = new HashMap();
                                    parametros.put(";where", " upper(o.nombre)=:nombre");
                                    parametros.put("nombre", pu.getPresupuesto().toUpperCase());
                                    List<Codigos> lpresupuesto = ejbCodigos.encontarParametros(parametros);
                                    for (Codigos cp : lpresupuesto) {
                                        cPresupuesto = cp;
                                    }
                                    if (cPresupuesto == null) {
                                        getErrores().add("Presupuesto no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                                    } else {
                                        pr.setPresupuesto(cPresupuesto.getCodigo());
                                    }
                                }
                            }
                            listaProyectos.add(pr);
                        }//fin while
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (org.errores.sfccbdmq.ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargaProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: " + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
            }
        }
    }

    private void ubicar(Proyectospoa p, String titulo, String valor) {
        // 1= true    0 = false
        if (titulo.contains("codigo")) {
            p.setCodigo(valor);
//        } else if (titulo.contains("anio")) {
//            p.setAnio(Integer.parseInt(valor));
        } else if (titulo.contains("nombre")) {
            p.setNombre(valor);
        } else if (titulo.contains("nivel")) {
            p.setNivel(Integer.parseInt(valor));
        } else if (titulo.contains("ingreso")) {
            if ("1".equals(valor)) { //Ingreso
                p.setIngreso(Boolean.TRUE);
            } else if ("0".equals(valor)) { // Egreso
                p.setIngreso(Boolean.FALSE);
            }
        } else if (titulo.contains("imputable")) {
            if ("1".equals(valor)) {
                p.setImputable(Boolean.TRUE);
            } else if ("0".equals(valor)) {
                p.setImputable(Boolean.FALSE);
            }
        } else if (titulo.contains("observaciones")) {
            p.setObservaciones(valor);
        } else if (titulo.contains("direccion")) {
            p.setDireccion(valor);
        } else if (titulo.contains("cpc")) {
            p.setCpc(valor);
        } else if (titulo.contains("compra")) {
            p.setTipocompra(valor);
        } else if (titulo.contains("detalle")) {
            p.setDetalle(valor);
        } else if (titulo.contains("cantidad")) {
            if (!(valor.equals(""))) {
                p.setCantidad(Integer.parseInt(valor));
            }
        } else if (titulo.contains("valor")) {
            if (!(valor.equals(""))) {
                p.setValoriva(new BigDecimal(valor));
            }
        } else if (titulo.contains("impuesto")) {
            if (!(valor.equals(""))) {
                p.setImpuesto(new BigDecimal(valor));
            }
        } else if (titulo.contains("cuatrimestre1")) {
            if ("1".equals(valor)) {
                p.setCuatrimestre1(Boolean.TRUE);
            } else if ("0".equals(valor)) {
                p.setCuatrimestre1(Boolean.FALSE);
            }
        } else if (titulo.contains("cuatrimestre2")) {
            if ("1".equals(valor)) {
                p.setCuatrimestre2(Boolean.TRUE);
            } else if ("0".equals(valor)) {
                p.setCuatrimestre2(Boolean.FALSE);
            }
        } else if (titulo.contains("cuatrimestre3")) {
            if ("1".equals(valor)) {
                p.setCuatrimestre3(Boolean.TRUE);
            } else if ("0".equals(valor)) {
                p.setCuatrimestre3(Boolean.FALSE);
            }
        } else if (titulo.contains("producto")) {
            p.setTipoproducto(valor);
        } else if (titulo.contains("catalogo")) {
            if ("1".equals(valor)) {
                p.setCatalogoelectronico(Boolean.TRUE);
            } else if ("0".equals(valor)) {
                p.setCatalogoelectronico(Boolean.FALSE);
            }
        } else if (titulo.contains("procedimiento")) {
            p.setProcedimientocontratacion(valor);
        } else if (titulo.contains("fondobid")) {
            if ("1".equals(valor)) {
                p.setFondobid(Boolean.TRUE);
            } else if ("0".equals(valor)) {
                p.setFondobid(Boolean.FALSE);
            }
        } else if (titulo.contains("noperacion")) {
            p.setNumerooperacion(valor);
        } else if (titulo.contains("coperacion")) {
            p.setCodigooperacion(valor);
        } else if (titulo.contains("regimen")) {
            p.setRegimen(valor);
        } else if (titulo.contains("presupuesto")) {
            p.setPresupuesto(valor);
        }
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
     * @return the listaProyectos
     */
    public List<Proyectospoa> getListaProyectos() {
        return listaProyectos;
    }

    /**
     * @param listaProyectos the listaProyectos to set
     */
    public void setListaProyectos(List<Proyectospoa> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }

    /**
     * @return the ejbProyectos
     */
    public ProyectospoaFacade getEjbProyectos() {
        return ejbProyectos;
    }

    /**
     * @param ejbProyectos the ejbProyectos to set
     */
    public void setEjbProyectos(ProyectospoaFacade ejbProyectos) {
        this.ejbProyectos = ejbProyectos;
    }

    /**
     * @return the ejbCodigos
     */
    public CodigosFacade getEjbCodigos() {
        return ejbCodigos;
    }

    /**
     * @param ejbCodigos the ejbCodigos to set
     */
    public void setEjbCodigos(CodigosFacade ejbCodigos) {
        this.ejbCodigos = ejbCodigos;
    }

    /**
     * @return the ejbOrganigrama
     */
    public OrganigramaFacade getEjbOrganigrama() {
        return ejbOrganigrama;
    }

    /**
     * @param ejbOrganigrama the ejbOrganigrama to set
     */
    public void setEjbOrganigrama(OrganigramaFacade ejbOrganigrama) {
        this.ejbOrganigrama = ejbOrganigrama;
    }

    /**
     * @return the anioCarga
     */
    public int getAnioCarga() {
        return anioCarga;
    }

    /**
     * @param anioCarga the anioCarga to set
     */
    public void setAnioCarga(int anioCarga) {
        this.anioCarga = anioCarga;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
}
