/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarSigef;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.contabilidad.sfccbdmq.EsigeInicialBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "asignacionesEsigefSfccbdmq")
@ViewScoped
public class AsignacionesEsigefBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public AsignacionesEsigefBean() {
    }
//    private Proyectos proyectosBean.getProyectoSeleccionado();
    private Formulario formulario = new Formulario();
    private List<AuxiliarSigef> balance;
    private Formulario formularioReporte = new Formulario();
    private Perfiles perfil;
    private int anio;
    private int mes;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ReformasFacade ejbReformas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource recursoTxt;
    private Resource reporte;

    public String generarArchivo() throws IOException {
        FileWriter fichero = null;
        PrintWriter pw = null;
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        Calendar c = Calendar.getInstance();
        String archivoNombre = "Salida_" + c.getTimeInMillis() + ".txt";
        try {
            fichero = new FileWriter(archivoNombre);
            pw = new PrintWriter(fichero);
            for (AuxiliarSigef a : balance) {
                String linea = "";
                linea += a.getPeriodo() + "|";
                linea += a.getTipo() + "|";
                linea += a.getMayor() + "|";
                linea += a.getNivel1() + "|";
                linea += a.getNivel2() + "|";
                linea += a.getFuncion() + "|";
                linea += a.getOrientacion() + "|";
                linea += df.format(a.getSaldoAcreedor());
                pw.println(linea);
            }
//            numero de registros

        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EsigeInicialBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }

            } catch (Exception e2) {
                MensajesErrores.fatal(e2.getMessage());
                Logger.getLogger(EsigeInicialBean.class.getName()).log(Level.SEVERE, null, e2);
            }
        }
        Path path = Paths.get(archivoNombre);
        byte[] data = Files.readAllBytes(path);

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        recursoTxt = new Recurso(data);
//        recursoTxt = new txtRecurso(ec, archivoNombre, data);
        formularioReporte.insertar();
        return null;
    }

    public String buscar() {
        if (getMes() <= 0) {
            MensajesErrores.fatal("Mes no válido");
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.set(anio, getMes(), 1);
        Map parametros = new HashMap();
        parametros.put(";orden", "o.clasificador.codigo");
        String where = "o.proyecto.anio=:anio";
        parametros.put(";where", where);
        parametros.put("anio", getAnio());
        try {
            List<Asignaciones> asignaciones = ejbAsignaciones.encontarParametros(parametros);
            DocumentoPDF pdf;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> filas = new LinkedList<>();
            pdf = new DocumentoPDF("AASIGNACION INICIAL ESIGEF\n del anño :" + anio + " " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
            // pocas columans preferible vertical
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SUB GRUPO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ITEM"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "FUNCION"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ORIENTACION DEL GASTO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR"));
            balance = new LinkedList<>();
            for (Asignaciones a1 : asignaciones) {
                AuxiliarSigef a = new AuxiliarSigef();
                if (a1.getProyecto().getIngreso()) {
                    a.setTipo("I");
                } else {
                    a.setTipo("G");
                }
                a.setPeriodo(getMes());
                String grupo = a1.getClasificador().getCodigo().substring(0, 2);
                String subgrupo = a1.getClasificador().getCodigo().substring(2, 4);
                String item = a1.getClasificador().getCodigo().substring(4, 6);
                a.setMayor(grupo);
                a.setNivel1(subgrupo);
                a.setNivel2(item);
                a.setFuncion("000");
                a.setOrientacion("999999999");
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, String.valueOf(a.getPeriodo())));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTipo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, grupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, subgrupo));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, item));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getFuncion()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getOrientacion()));
                // traer reformas
                parametros = new HashMap();
                parametros.put(";where", "o.asignacion=:asignacion "
                        + "and o.fecha >= :desde and o.fecha <:hasta and o.cabecera.definitivo=true");
                parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                parametros.put("hasta", c.getTime());
                parametros.put("asignacion", a1);
                parametros.put(";campo", "o.valor");
                double valor = ejbReformas.sumarCampo(parametros).doubleValue();
                valor += a1.getValor().doubleValue();
                a.setSaldoAcreedor(valor);
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, valor));
                balance.add(a);
            }
             pdf.agregarTablaReporte(titulos, filas, 8, 100, "ASIGANCION");
            reporte=pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesEsigefBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "AsignacionEsigefVista";
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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        setMes(c.get(Calendar.MONTH) + 1);
        anio = c.get(Calendar.YEAR);
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
     * @return the balance
     */
    public List<AuxiliarSigef> getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(List<AuxiliarSigef> balance) {
        this.balance = balance;
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

    
}
