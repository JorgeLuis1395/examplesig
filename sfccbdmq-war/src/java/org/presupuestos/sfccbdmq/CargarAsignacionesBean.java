/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.PartidaspoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Partidaspoa;
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
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.pacpoa.sfccbdmq.PartidasPoaBean;
import org.pacpoa.sfccbdmq.ProyectosPoaBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cargarAsignaciones")
@ViewScoped
public class CargarAsignacionesBean {

    /**
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasPoaBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
    }

    /**
     * @return the proyectosPoaBean
     */
    public ProyectosPoaBean getProyectosPoaBean() {
        return proyectosPoaBean;
    }

    /**
     * @param proyectosPoaBean the proyectosPoaBean to set
     */
    public void setProyectosPoaBean(ProyectosPoaBean proyectosPoaBean) {
        this.proyectosPoaBean = proyectosPoaBean;
    }

    /**
     * Creates a new instance of AsignacionesBean
     */
    public CargarAsignacionesBean() {
    }
    private Proyectos partida;
    private Asignaciones asignacion;
    private Codigos fuente;
    private int anioCarga;
    private List<Asignaciones> asignaciones;
    private List<AuxiliarCarga> totales;
    private Formulario formulario = new Formulario();
    private Formulario formularioErrores = new Formulario();
    private Perfiles perfil;
    private String separador = ",";
    private List errores;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private AsignacionespoaFacade ejbAsignacionesPoa;
    @EJB
    private CertificacionesFacade ejbCertificaciones;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    // poa
    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;

    /**
     * @return the partida
     */
    public Proyectos getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(Proyectos partida) {
        this.partida = partida;
    }

    /**
     * @return the asignacion
     */
    public Asignaciones getAsignacion() {
        return asignacion;
    }

    /**
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(Asignaciones asignacion) {
        this.asignacion = asignacion;
    }

    /**
     * @return the asignaciones
     */
    public List<Asignaciones> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<Asignaciones> asignaciones) {
        this.asignaciones = asignaciones;
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

    public String insertar() {
        if (errores == null) {
            errores = new LinkedList();
        }
        if (!errores.isEmpty()) {
            MensajesErrores.advertencia("Existen errores en imporatación de archivo. Corrijá los antes de continuar");
            return null;
        }
        if (asignaciones == null) {
            MensajesErrores.advertencia("No existen  datos a importar");
            return null;
        }
        if (asignaciones.isEmpty()) {
            MensajesErrores.advertencia("No existen  datos a importar");
            return null;
        }
        double cuadre = 0;
        for (AuxiliarCarga a : totales) {
            // redondemos
            double ingresos = Math.round(a.getIngresos().doubleValue() * 100) / 100;
            double egresos = Math.round(a.getEgresos().doubleValue() * 100) / 100;
            double cero = ingresos - egresos;
            if (cero != 0) {
                MensajesErrores.advertencia("Existen errores en imporatación de archivo. no está cuadrada la fuente de financiamiento : " + a.getFuente().getNombre());
                return null;
            }
//            cuadre = a.getIngresos().doubleValue() - a.getEgresos().doubleValue();
        }
        Calendar c = Calendar.getInstance();
        int anio = anioCarga;
//        int anio = c.get(Calendar.YEAR);
        for (Asignaciones a : asignaciones) {
            if (anio == 0) {
                anio = a.getAnio();
            }
            if (a.getAnio() < anioCarga) {
                MensajesErrores.advertencia("No se pueden hacer cargas de años anteriores");
                return null;
            }
            if (anio != a.getAnio()) {
                MensajesErrores.advertencia("No se pueden hacer cargas de años distintos");
                return null;
            }
//            cuadre += a.getValor().doubleValue() * (a.getClasificador().getIngreso() ? 1 : -1);

        }
//        if (cuadre != 0) {
//            MensajesErrores.advertencia("No se carga, no está cuadrado ");
//            return null;
//        }
        try {
            for (Asignaciones a : asignaciones) {
                ejbAsignaciones.create(a, seguridadbean.getLogueado().getUserid());
//                ****************************************************************************
                //nuevo para que grabe en el pacpoa
                // proyectos pac en base al codigo
                Proyectospoa ppoa = proyectosPoaBean.traerCodigo(a.getProyecto().getCodigo());
                // clasificadores pac en base al codigo
                Partidaspoa papoa = partidasPoaBean.traerCodigo(a.getClasificador().getCodigo());
                // Nuevo elemento
                Asignacionespoa ap = new Asignacionespoa();
                ap.setPartida(papoa);
                ap.setProyecto(ppoa);
                ap.setFuente(a.getFuente().getCodigo());
                ap.setValor(a.getValor());
                ap.setActivo(Boolean.TRUE);
                ejbAsignacionesPoa.create(ap, seguridadbean.getLogueado().getUserid());
//                *****************************************************************************
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Los registros se grabaron correctamente");
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

    /**
     * @return the fuente
     */
    public Codigos getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(Codigos fuente) {
        this.fuente = fuente;
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String nombreForma = "AsignacionVista";
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
        anioCarga = c.get(Calendar.YEAR);
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
    public void setErrores(List errores) {
        this.errores = errores;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;

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
                        asignaciones = new LinkedList<>();
                        totales = new LinkedList<>();
                        List<Codigos> fuentes = codigosBean.getFuentesFinanciamiento();
                        for (Codigos c : fuentes) {
                            AuxiliarCarga auxiliarCarga = new AuxiliarCarga();
                            auxiliarCarga.setFuente(c);
                            auxiliarCarga.setIngresos(new BigDecimal(0));
                            auxiliarCarga.setEgresos(new BigDecimal(0));
                            totales.add(auxiliarCarga);
                        }
                        double totalIngreso = 0;
                        double totalEgreso = 0;
                        errores = new LinkedList();
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            Asignaciones a = new Asignaciones();
                            a.setAnio(anioCarga);
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(a, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            registro++;
                            //***************Verificar si esta en pacpoa el proyecto y la partida******************
                            Proyectospoa proyectoPoa = null;
                            Partidaspoa partidaPoa = null;
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.imputable=true");
                            parametros.put("anio", a.getAnio());
                            parametros.put("codigo", a.getProyectoStr());
                            List<Proyectospoa> lpoa = ejbProyectospoa.encontarParametros(parametros);
                            for (Proyectospoa poaProyecto : lpoa) {
                                proyectoPoa = poaProyecto;
                            }
                            boolean error = false;
                            if (proyectoPoa == null) {
                                errores.add("Proyecto no existe en PACPOA en registro: " + String.valueOf(registro) + " " + a.getProyectoStr());
                                error = true;
                            }

                            parametros = new HashMap();
                            parametros.put(";where", "o.codigo=:codigo and o.activo=true");
                            parametros.put("codigo", a.getClasificador().getCodigo());
                            List<Partidaspoa> lpartida = ejbPartidaspoa.encontarParametros(parametros);
                            for (Partidaspoa poaPartida : lpartida) {
                                partidaPoa = poaPartida;
                            }
                            if (partidaPoa == null) {
                                errores.add("Partida no existe en PACPOA en registro: " + String.valueOf(registro) + " " + a.getClasificador().getCodigo());
                                error = true;
                            }

                            //***************Fin de verificacion del pacpoa******************
                            // ver si esta ben el registro o es error 
                            if (error) {
                            } else if (a.getFuente() == null) {
                                errores.add("Fuente no válida en registro: " + String.valueOf(registro));
                            } else if (a.getClasificador() == null) {
                                errores.add("Clasificador no válido en registro: " + String.valueOf(registro));
                            } else if (a.getClasificador().getIngreso() == null) {
                                errores.add("Clasificador no válido en registro: " + String.valueOf(registro) + " - " + a.getClasificador().toString());
                            } else {
                                parametros = new HashMap();
                                parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.imputable=true");
                                parametros.put("anio", a.getAnio());
                                parametros.put("codigo", a.getProyectoStr());
                                List<Proyectos> lp = ejbProyectos.encontarParametros(parametros);
                                for (Proyectos p : lp) {
                                    a.setProyecto(p);
                                }
                                if (a.getProyecto() == null) {
                                    errores.add("Proyecto no válido en registro: " + String.valueOf(registro));
                                } else {
                                    // ver si ya esta repetido
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.proyecto=:proyecto and o.clasificador=:clasificador and o.fuente=:fuente  ");
                                    parametros.put("proyecto", a.getProyecto());
                                    parametros.put("clasificador", a.getClasificador());
                                    parametros.put("fuente", a.getFuente());
                                    int contar = ejbAsignaciones.contar(parametros);
                                    if (contar > 0) {
                                        errores.add("Clasificador ya tiene una carga inicial, para modificar utilizar una reforma : " + String.valueOf(registro));
                                    } else {
                                        a.setAnio(anioCarga);
                                        asignaciones.add(a);
                                        // ver el total
                                        for (AuxiliarCarga auxCarga : totales) {
                                            if (a.getFuente().equals(auxCarga.getFuente())) {
                                                if (a.getClasificador().getIngreso()) {
                                                    auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + a.getValor().doubleValue()));
                                                    totalIngreso += a.getValor().doubleValue();
                                                } else {
                                                    auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + a.getValor().doubleValue()));
                                                    totalEgreso += a.getValor().doubleValue();
                                                }
                                            } // fin fic de suma
                                        } //fin if de for
                                    }
                                }
                            }

                        }
                        AuxiliarCarga aux = new AuxiliarCarga();
                        aux.setFuente(null);
                        aux.setIngresos(new BigDecimal(totalIngreso));
                        aux.setEgresos(new BigDecimal(totalEgreso));
                        totales.add(aux);
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }
        if (errores.size() > 0) {
            formularioErrores.insertar();
        }

    }

    private void ubicar(Asignaciones am, String titulo, String valor) {

        if (titulo.contains("anio")) {
            am.setAnio(Integer.valueOf(valor));

        } else if (titulo.contains("clasificador")) {
            // buscar el clasificador
            am.setClasificador(clasificadorBean.traerCodigo(valor));
//        } else if (titulo.contains("partida")) {
//            // buscar el clasificador
//            am.setClasificador(clasificadorBean.traerCodigo(valor));
        } else if (titulo.contains("proyecto")) {
            am.setProyectoStr(valor);
//        } else if (titulo.contains("actividad")) {
//            am.setProyectoStr(valor);
//        } else if (titulo.contains("programa")) {
//            am.setProyectoStr(valor);
//        } else if (titulo.contains("producto")) {
//            am.setProyectoStr(valor);
//        } else if (titulo.contains("programa")) {
//            am.setProyectoStr(valor);
        } else if (titulo.contains("fuente")) {
            am.setFuente(codigosBean.traerCodigo("FUENFIN", valor));
        } else if (titulo.contains("valor")) {
            am.setValor(new BigDecimal(valor.trim()));
        }

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

    public String verErrores() {
        formulario.insertar();
        return null;
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
     * @return the formularioErrores
     */
    public Formulario getFormularioErrores() {
        return formularioErrores;
    }

    /**
     * @param formularioErrores the formularioErrores to set
     */
    public void setFormularioErrores(Formulario formularioErrores) {
        this.formularioErrores = formularioErrores;
    }
}
