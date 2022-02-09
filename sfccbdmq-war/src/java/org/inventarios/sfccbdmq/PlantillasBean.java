/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.beans.sfccbdmq.PlantillasFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Plantillas;
import org.entidades.sfccbdmq.Suministros;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "plantillasSfccbdmq")
@ViewScoped
public class PlantillasBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioEditar = new Formulario();
    private List<Plantillas> listaPlantillas;
    private List<Plantillas> listaPlantillasSubir;
    private List errores;
    private Plantillas plantilla;
    private Codigos tipo;
    private String separador = ";";

    @EJB
    private PlantillasFacade ejbPlantillas;
    @EJB
    private SuministrosFacade ejbSuministros;

    public PlantillasBean() {
    }

    @PostConstruct
    private void activar() {
//        List<Codigos> configuracion = codigosBean.traerCodigoMaestro("CONF");
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        for (Codigos c : configuracion) {
//            try {
//                if (c.getCodigo().equals("PVGT")) {
//                    vigente = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PINI")) {
//                    desde = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PFIN")) {
//                    hasta = sdf.parse(c.getParametros());
//                }
//            } catch (ParseException ex) {
//                Logger.getLogger(ReporteTotalReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

    }

    public String buscar() {
        if (tipo == null) {
            MensajesErrores.advertencia("Seleccione un tipo");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.tipo=:tipo";
        parametros.put(";where", where);
        parametros.put("tipo", tipo);
        parametros.put(";orden", "o.id asc");

        try {
            listaPlantillas = ejbPlantillas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearCabecera() {
        if (tipo == null) {
            MensajesErrores.advertencia("Seleccione un tipo");
            return null;
        }
        listaPlantillasSubir = new LinkedList<>();
        errores = new LinkedList<>();
        formulario.insertar();
        return null;
    }

    public String modificar() {
        plantilla = listaPlantillas.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioEditar.editar();
        return null;
    }

    public String eliminar() {
        plantilla = listaPlantillas.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioEditar.eliminar();
        return null;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            for (Plantillas r : listaPlantillasSubir) {
                Plantillas p = new Plantillas();
                p.setTipo(tipo);
                p.setCodigo(r.getCodigo());
                p.setCantidad(r.getCantidad());
                p.setCantidadinv(r.getCantidadinv());
                ejbPlantillas.create(p, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    private boolean validar() {
        if (tipo == null) {
            MensajesErrores.advertencia("Seleccione un tipo");
            return true;
        }
        if (!errores.isEmpty()) {
            MensajesErrores.advertencia("Existen errores");
            return true;
        }
        if (listaPlantillasSubir.isEmpty()) {
            MensajesErrores.advertencia("No existen datos");
            return true;
        }
        if (listaPlantillasSubir == null) {
            MensajesErrores.advertencia("No existen datos");
            return true;
        }
        return false;
    }

    public String grabar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigobarras=:codigobarras");
            parametros.put("codigobarras", plantilla.getCodigo());
            List<Suministros> ls = ejbSuministros.encontarParametros(parametros);
            if (ls.isEmpty()) {
                MensajesErrores.advertencia("No existe código");
                return null;
            }
            ejbPlantillas.edit(plantilla, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioEditar.cancelar();
        return null;
    }

    public String borrar() {
        try {
            ejbPlantillas.remove(plantilla, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioEditar.cancelar();
        buscar();
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listaPlantillasSubir = new LinkedList<>();
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
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        setErrores(new LinkedList());
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            Plantillas r = new Plantillas();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(r, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            registro++;
                            // ver si esta ben el registro o es error 
                            if (r.getCodigo() == null) {
                                getErrores().add("Codigo no válido en registro: " + String.valueOf(registro));
                            } else if (r.getCantidad() == null) {
                                getErrores().add("Cantidad no válido en registro: " + String.valueOf(registro));
                            } else if (r.getCantidadinv()== null) {
                                getErrores().add("Cantidad inv no válido en registro: " + String.valueOf(registro));
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.codigobarras=:codigobarras");
                                parametros.put("codigobarras", r.getCodigo());
                                List<Suministros> ls = ejbSuministros.encontarParametros(parametros);
                                Suministros s = null;
                                for (Suministros su : ls) {
                                    s = su;
                                }
                                if (s == null) {
                                    getErrores().add("Suministro no válido : " + r.getCodigo());
                                } else {
                                    Plantillas plantillasSubir = new Plantillas();
                                    plantillasSubir.setTipo(tipo);
                                    plantillasSubir.setCodigo(r.getCodigo());
                                    plantillasSubir.setCantidad(r.getCantidad());
                                    plantillasSubir.setCantidadinv(r.getCantidadinv());
                                    listaPlantillasSubir.add(plantillasSubir);
                                }
                            }
                        }//fin while
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }

    }

    private void ubicar(Plantillas am, String titulo, String valor) {
        if (titulo.contains("codigo")) {
            am.setCodigo(valor);
        } else if (titulo.contains("cantidad")) {
            am.setCantidad(Integer.valueOf(valor));
        } else if (titulo.contains("cinv")) {
            am.setCantidadinv(Integer.valueOf(valor));
        }
    }

    public String salir() {
        formulario.cancelar();
        listaPlantillas = new LinkedList<>();
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
     * @return the listaPlantillas
     */
    public List<Plantillas> getListaPlantillas() {
        return listaPlantillas;
    }

    /**
     * @param listaPlantillas the listaPlantillas to set
     */
    public void setListaPlantillas(List<Plantillas> listaPlantillas) {
        this.listaPlantillas = listaPlantillas;
    }

    /**
     * @return the plantilla
     */
    public Plantillas getPlantilla() {
        return plantilla;
    }

    /**
     * @param plantilla the plantilla to set
     */
    public void setPlantilla(Plantillas plantilla) {
        this.plantilla = plantilla;
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
     * @return the tipo
     */
    public Codigos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the formularioEditar
     */
    public Formulario getFormularioEditar() {
        return formularioEditar;
    }

    /**
     * @param formularioEditar the formularioEditar to set
     */
    public void setFormularioEditar(Formulario formularioEditar) {
        this.formularioEditar = formularioEditar;
    }

    /**
     * @return the listaPlantillasSubir
     */
    public List<Plantillas> getListaPlantillasSubir() {
        return listaPlantillasSubir;
    }

    /**
     * @param listaPlantillasSubir the listaPlantillasSubir to set
     */
    public void setListaPlantillasSubir(List<Plantillas> listaPlantillasSubir) {
        this.listaPlantillasSubir = listaPlantillasSubir;
    }

}
