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
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detalletoma;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
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
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "nominaConceptoSfccbdmq")
@ViewScoped
public class NominaConceptoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Pagosempleados> listaPagosempleados;
    private List<Conceptos> listaConceptos;
    private Formulario formulario = new Formulario();
    private Conceptos concepto;
    private String separador = ",";
    @EJB
    private PagosempleadosFacade ejbPagosempleados;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private FormatosFacade ejbFormatos;
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
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

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
        String nombreForma = "NominaConceptoVista";
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
     * Creates a new instance of PagosempleadosEmpleadoBean
     */
    public NominaConceptoBean() {
    }

    public String buscar() {

//        if (concepto == null) {
//            MensajesErrores.advertencia("Ingrese un concepto");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            String where = "o.concepto=:concepto and o.mes=:mes and o.anio=:anio";
            parametros.put("concepto", concepto);
            parametros.put("mes", getMes());
            parametros.put("anio", getAnio());
            parametros.put(";where", where);

            parametros.put(";orden", "o.empleado.entidad.apellidos");
            listaPagosempleados = ejbPagosempleados.encontarParametros(parametros);
            parametros = new HashMap();
            where = "o.cargoactual is not null and o.activo =true";
            parametros.put(";orden", "o.entidad.apellidos");
//            parametros.put("concepto", concepto);
            parametros.put(";where", where);
            List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
            for (Empleados e : listaEmpleados) {
                // ver si el cargo esta en la lista del concepto

                esta(e, concepto);

            }
        } catch (ConsultarException ex) {
            Logger.getLogger(NominaConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    private void esta(Empleados e, Conceptos c) {
        if (listaPagosempleados == null) {
            listaPagosempleados = new LinkedList<>();
        }
        for (Pagosempleados n : listaPagosempleados) {
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
        listaPagosempleados.add(n);
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
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            for (Pagosempleados n : listaPagosempleados) {
                if (n.getValor() != 0) {
                    n.setAnio(anio);
                    n.setMes(mes);
                    n.setCargado(Boolean.TRUE);
                    String partida = n.getEmpleado().getPartida().substring(0, 2);
//                    String cuenta = n.getEmpleado().getPartida().substring(2, 6);
                    ejbPagosempleados.ponerCuentasPago(concepto, n, ctaInicio, ctaFin, partida);
                    if (n.getId() == null) {
                        ejbPagosempleados.create(n, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbPagosempleados.edit(n, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            }

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NominaConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(NominaConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registros se grabaron correctamente");
        return null;
    }
    public String grabarRmu() {
        Calendar c = Calendar.getInstance();
        c.set(anio, mes - 1, 1);
        if (c.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha menor a periodo vigente");
            return null;
        }
        String partidaEncargo = codigosBean.traerCodigo("TIPACPER", "ENCARGO").getDescripcion();
        String partidaSubrogacion = codigosBean.traerCodigo("TIPACPER", "SUROGACION").getDescripcion();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            for (Pagosempleados n : listaPagosempleados) {
                if (n.getValor() != 0) {
                    n.setAnio(anio);
                    n.setMes(mes);
                    n.setConcepto(null);
                    // cuentas

                    // fin cuentas
                    n.setCargado(Boolean.TRUE);
                    String partida = n.getEmpleado().getPartida().substring(0, 2);
                    String cuenta = n.getEmpleado().getPartida().substring(2, 6);
                    String cuentaSubrogacion = "";
                    String cuentaEncargo = "";
                    if (partidaEncargo != null) {
                        cuentaSubrogacion = partida + partidaEncargo;
                    }
                    if (partidaEncargo != null) {
                        cuentaSubrogacion = partida + partidaEncargo;
                    }
                    if (!((n.getCantidad() == null) || (n.getCantidad().doubleValue() != 0))) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.presupuesto=:clasificador and o.imputable=true");
                        parametros.put("clasificador", cuentaSubrogacion);
                        parametros.put(";orden", "o.codigo");
                        List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
                        String ctaGasto = "";
                        for (Cuentas cta : lCuentas) {
                            if (!ctaGasto.isEmpty()) {
                                ctaGasto += "#";
                            }
                            ctaGasto += cta.getCodigo();
                        }
                        n.setCuentasubrogacion(ctaGasto);
                    }
                    if (!((n.getEncargo() == null) || (n.getEncargo().doubleValue() != 0))) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.presupuesto=:clasificador and o.imputable=true");
                        parametros.put("clasificador", cuentaSubrogacion);
                        parametros.put(";orden", "o.codigo");
                        List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
                        String ctaGasto = "";
                        for (Cuentas cta : lCuentas) {
                            if (!ctaGasto.isEmpty()) {
                                ctaGasto += "#";
                            }
                            ctaGasto += cta.getCodigo();
                        }
                        n.setCuentasubrogacion(ctaGasto);
                    }
                    n.setCuentaporpagar(ctaInicio + partida + ctaFin);
                    n.setClasificadorencargo(cuentaSubrogacion);

                    parametros = new HashMap();
                    parametros.put(";where", "o.presupuesto=:clasificador and o.imputable=true");
                    parametros.put("clasificador", partida + cuenta);
                    parametros.put(";orden", "o.codigo");
                    List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
                    String ctaGasto = "";
                    for (Cuentas cta : lCuentas) {
                        if (!ctaGasto.isEmpty()) {
                            ctaGasto += "#";
                        }
                        ctaGasto += cta.getCodigo();
                    }
                    n.setCuentagasto(ctaGasto);

                    if (n.getId() == null) {
                        ejbPagosempleados.create(n, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbPagosempleados.edit(n, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            }

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NominaConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(NominaConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registros se grabaron correctamente");
        return null;
    }

    public String limpiar() {

        for (Pagosempleados n : listaPagosempleados) {
            n.setValor(new Float(0));
        }

        MensajesErrores.informacion("Registros se enceraron correctamente");
        return null;
    }

    public String cancelar() {
        listaConceptos = null;
        listaPagosempleados = null;
        getFormulario().cancelar();
//        empleadoBean.setEmpleadoSeleccionado(null);
//        empleadoBean.setApellidos(null);
        return null;
    }

    /**
     * @return the listaPagosempleados
     */
    public List<Pagosempleados> getListaPagosempleados() {
        return listaPagosempleados;
    }

    /**
     * @param listaPagosempleados the listaPagosempleados to set
     */
    public void setListaPagosempleados(List<Pagosempleados> listaPagosempleados) {
        this.listaPagosempleados = listaPagosempleados;
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
                            Pagosempleados n = traerCedula(d.getCodigobarras());
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

    private void ubicarRmu(Detalletoma am, String titulo, String valor) {

        if (titulo.contains("cedula")) {
            am.setCodigobarras(valor);

        } else if (titulo.contains("empleado")) {
            // buscar el clasificador
            am.setCodigobarras(valor);

        } else if (titulo.contains("rmu")) {
            valor = valor.replace(",", ".");
            am.setLocalidadStr(valor);
        } else if (titulo.contains("encargo")) {
            valor = valor.replace(",", ".");
            am.setEstadoStr(valor);
        } else if (titulo.contains("subrogacion")) {
            valor = valor.replace(",", ".");
            am.setObservaciones(valor);
        } else if (titulo.contains("cantidad")) {
            valor = valor.replace(",", ".");
            am.setLocalidadStr(valor);
        }

    }

    public Pagosempleados traerCedula(String cedula) {
        for (Pagosempleados n : listaPagosempleados) {
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

    public void archivoListenerRmu(FileEntryEvent e) {
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
                                    ubicarRmu(d, cabecera[j].toLowerCase().trim(), aux[j].toUpperCase().trim());
                                }
                            }
                            if ((d.getCodigobarras() == null) || (d.getCodigobarras().isEmpty())) {
                                MensajesErrores.fatal("No existe inf del empleado : " + d.getCodigobarras());
                                return;
                            }
                            if (d.getCodigobarras().length() < 10) {
                                d.setCodigobarras("0" + d.getCodigobarras());
                            }
                            Pagosempleados n = traerCedula(d.getCodigobarras());
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
                                    // Encargos
                                    valorDato = new BigDecimal(d.getEstadoStr()).doubleValue();
                                    n.setEncargo(new Float(valorDato));
                                    // subrogaciones
                                    valorDato = new BigDecimal(d.getObservaciones()).doubleValue();
                                    n.setCantidad(new Float(valorDato));
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

}