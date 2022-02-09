/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.NovedadesxempleadoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Detalletoma;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Novedadesxempleado;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.presupuestos.sfccbdmq.CargarAsignacionesBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "novedadesConcepto")
@ViewScoped
public class NovedadesConceptoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Novedadesxempleado> listaNovedadesxempleado;
    private List<Pagosempleados> listaPagosEmpleados;
    private List<Conceptos> listaConceptos;
    private Formulario formulario = new Formulario();
    private Conceptos concepto;
    private String separador = ",";
    @EJB
    private NovedadesxempleadoFacade ejbNovedadesxempleado;
    @EJB
    private ConceptosFacade ejbConceptos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private PagosempleadosFacade ejbPagos;
    private int mes;
    private int anio;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
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

    /* @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        setAnio(c.get((Calendar.YEAR)));
        setMes(c.get((Calendar.MONTH)) + 1);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "NovedadesConceptoVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of NovedadesxempleadoEmpleadoBean
     */
    public NovedadesConceptoBean() {
    }

    public String buscar() {

        if (concepto == null) {
            MensajesErrores.advertencia("Ingrese un concepto");
            return null;
        }

        try {

            Map parametros = new HashMap();
            String where1 = "o.anio=:anio and o.mes=:mes and o.liquidacion=false and o.compromiso is not null"
                    + " and o.concepto=:concepto";
            parametros.put("where", where1);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", concepto);
            int total = ejbPagos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya generado presupuesto, no se puede reprocesar rol");
                return null;
            }

            parametros = new HashMap();
            String where = "o.cargoactual is not null and o.activo=true";
            parametros.put(";orden", "o.entidad.apellidos");
            parametros.put(";where", where);
            List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
            if ((concepto.getCodigo().equals("BONOCOMP") || (!concepto.getSobre() && concepto.getProvision()))) {
                parametros = new HashMap();
                where = "o.concepto=:concepto and o.mes=:mes and o.anio=:anio";
                parametros.put("concepto", concepto);
                parametros.put("mes", getMes());
                parametros.put("anio", getAnio());
                parametros.put(";where", where);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                listaPagosEmpleados = ejbPagos.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    estaPago(e, concepto);
                }
            } else {
                parametros = new HashMap();
                where = "o.concepto=:concepto and o.mes=:mes and o.anio=:anio";
                parametros.put("concepto", concepto);
                parametros.put("mes", getMes());
                parametros.put("anio", getAnio());
                parametros.put(";where", where);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                listaNovedadesxempleado = ejbNovedadesxempleado.encontarParametros(parametros);
                for (Empleados e : listaEmpleados) {
                    esta(e, concepto);
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NovedadesConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    private void esta(Empleados e, Conceptos c) {
        if (listaNovedadesxempleado == null) {
            listaNovedadesxempleado = new LinkedList<>();
        }
        for (Novedadesxempleado n : listaNovedadesxempleado) {
            if (n.getEmpleado().equals(e)) {
                return;
            }
        }
        Novedadesxempleado n = new Novedadesxempleado();
        n.setAnio(getAnio());
        n.setMes(getMes());
        n.setValor(new Float(0));
        n.setConcepto(c);
        n.setEmpleado(e);
        listaNovedadesxempleado.add(n);
    }

    private void estaPago(Empleados e, Conceptos c) {
        if (listaPagosEmpleados == null) {
            listaPagosEmpleados = new LinkedList<>();
        }
        for (Pagosempleados n : listaPagosEmpleados) {
            if (n.getEmpleado().equals(e)) {
                return;
            }
        }
        Pagosempleados n = new Pagosempleados();
        n.setAnio(getAnio());
        n.setMes(getMes());
        n.setValor(new Float(0));
        n.setConcepto(c);
        n.setEmpleado(e);
        listaPagosEmpleados.add(n);
    }

    public String grabar() {
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        if (c.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha menor a periodo vigente");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.anio=:anio and o.mes=:mes and o.liquidacion=false and o.compromiso is not null"
                    + " and o.concepto=:concepto";
            parametros.put("where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", concepto);
            int total = ejbPagos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya generado presupuesto, no se puede reprocesar rol");
                return null;
            }
            if ((concepto.getCodigo().equals("BONOCOMP") || (!concepto.getSobre() && concepto.getProvision()))) {
                for (Pagosempleados n : listaPagosEmpleados) {
                    n.setAnio(anio);
                    n.setMes(mes);
                    n.setPagado(Boolean.FALSE);
                    n.setLiquidacion(Boolean.FALSE);
                    n.setCargado(Boolean.FALSE);
                    n.setDia(new Date());
                    if (n.getId() == null) {
                        ejbPagos.create(n, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbPagos.edit(n, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            } else {
                for (Novedadesxempleado n : listaNovedadesxempleado) {
                    n.setAnio(anio);
                    n.setMes(mes);
                    if (n.getId() == null) {
                        ejbNovedadesxempleado.create(n, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbNovedadesxempleado.edit(n, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            }

        } catch (InsertarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NovedadesConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registros se grabaron correctamente");
        return null;
    }

    public String limpiar() {

        for (Novedadesxempleado n : listaNovedadesxempleado) {
            n.setValor(new Float(0));
        }

        MensajesErrores.informacion("Registros se enceraron correctamente");
        return null;
    }

    public String cancelar() {
        listaConceptos = null;
        listaNovedadesxempleado = null;
        getFormulario().cancelar();
//        empleadoBean.setEmpleadoSeleccionado(null);
//        empleadoBean.setApellidos(null);
        return null;
    }

    /**
     * @return the listaNovedadesxempleado
     */
    public List<Novedadesxempleado> getListaNovedadesxempleado() {
        return listaNovedadesxempleado;
    }

    /**
     * @param listaNovedadesxempleado the listaNovedadesxempleado to set
     */
    public void setListaNovedadesxempleado(List<Novedadesxempleado> listaNovedadesxempleado) {
        this.listaNovedadesxempleado = listaNovedadesxempleado;
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
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        while ((sb = entrada.readLine()) != null) {
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            Detalletoma d = new Detalletoma();
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(d, cabecera[j].toLowerCase().trim(), aux[j].toUpperCase().trim());
                                }
                            }
                            if ((d.getCodigobarras() == null) || (d.getCodigobarras().isEmpty())) {
                                MensajesErrores.fatal("No existe inf del empleado : " + d.getCodigobarras());
                                return;
                            }
                            if (d.getCodigobarras().length() < 10) {
                                d.setCodigobarras("0" + d.getCodigobarras());
                            }
                            if ((concepto.getCodigo().equals("BONOCOMP") || (!concepto.getSobre() && concepto.getProvision()))) {
                                Pagosempleados n = traerCedulaPago(d.getCodigobarras());
                                if (n == null) {
                                    MensajesErrores.fatal("No existe inf del empleado : " + d.getCodigobarras());
                                    return;
                                } else {
                                    if (!((d.getLocalidadStr() == null) || (d.getLocalidadStr().isEmpty()))) {
//                                    BigDecimal v=new BigDecimal(n.getValor().doubleValue()).add(new BigDecimal(d.getLocalidadStr()));
                                        double valorNovedad = Math.round(n.getValor() * 100);
                                        double valorDato = new BigDecimal(d.getLocalidadStr()).doubleValue();
                                        double valorActual = valorNovedad / 100;
                                        valorActual += valorDato;
                                        n.setValor(new Float(valorActual));
                                    }
                                }
                            } else {
                                Novedadesxempleado n = traerCedula(d.getCodigobarras());
                                if (n == null) {
                                    MensajesErrores.fatal("No existe inf del empleado : " + d.getCodigobarras());
                                    return;
                                } else {
                                    if (!((d.getLocalidadStr() == null) || (d.getLocalidadStr().isEmpty()))) {
//                                    BigDecimal v=new BigDecimal(n.getValor().doubleValue()).add(new BigDecimal(d.getLocalidadStr()));
                                        double valorNovedad = Math.round(n.getValor() * 100);
                                        double valorDato = new BigDecimal(d.getLocalidadStr()).doubleValue();
                                        double valorActual = valorNovedad / 100;
                                        valorActual += valorDato;
                                        n.setValor(new Float(valorActual));
                                    }
                                }
                            }

                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
//               

                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }

    }

    private void ubicar(Detalletoma am, String titulo, String valor) {

        if (titulo.contains("cedula")) {
            am.setCodigobarras(valor);

        } else if (titulo.contains("empleado")) {
            // buscar el clasificador
            am.setCodigobarras(valor);

        } else if (titulo.contains("valor")) {
            valor = valor.replace(",", ".");
            am.setLocalidadStr(valor);
        } else if (titulo.contains("cantidad")) {
            valor = valor.replace(",", ".");
            am.setLocalidadStr(valor);
        }

    }

    public Novedadesxempleado traerCedula(String cedula) {
        for (Novedadesxempleado n : listaNovedadesxempleado) {
            if (n.getEmpleado().getEntidad().getPin().equals(cedula)) {
                return n;
            }
        }
        return null;
    }

    public Pagosempleados traerCedulaPago(String cedula) {
        for (Pagosempleados n : listaPagosEmpleados) {
            if (n.getEmpleado().getEntidad().getPin().equals(cedula)) {
                return n;
            }
        }
        return null;
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
     * @return the listaPagosEmpleados
     */
    public List<Pagosempleados> getListaPagosEmpleados() {
        return listaPagosEmpleados;
    }

    /**
     * @param listaPagosEmpleados the listaPagosEmpleados to set
     */
    public void setListaPagosEmpleados(List<Pagosempleados> listaPagosEmpleados) {
        this.listaPagosEmpleados = listaPagosEmpleados;
    }
}
