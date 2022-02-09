/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import javax.faces.application.Resource;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarSigef;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "esigeInicialSfccbdmq")
@ViewScoped
public class EsigeInicialBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public EsigeInicialBean() {
    }
    private List<AuxiliarSigef> balance;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private FormatosFacade ejbFormatos;
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

    private int mes;
    private int anio;
    private Resource recursoTxt;

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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        mes = 1;
        anio = c.get(Calendar.YEAR);

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        String nombreForma = "EsigeInicialVista";
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

    public String generarArchivo() throws IOException {
        FileWriter fichero = null;
        PrintWriter pw = null;
        Calendar c = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        String archivoNombre = "Salida_" + c.getTimeInMillis() + ".txt";
        try {
            fichero = new FileWriter(archivoNombre);
            pw = new PrintWriter(fichero);
            for (AuxiliarSigef a : balance) {
                String linea = "";
                linea += a.getPeriodo() + "|";
                linea += a.getMayor() + "|";
                linea += a.getNivel1() + "|";
                linea += a.getNivel2() + "|";
                linea += df.format(a.getInicialDeudor()) + "|";
                linea += df.format(a.getInicialAcreedor());
//                linea += a.getInicialAcreedor()+"|";
//                linea += a.getFlujoDeudor()+"|";
//                linea += a.getFlujoAcreedor()+"|";
//                linea += a.getSumasDeudor()+"|";
//                linea += a.getSumasAcreedor()+"|";
//                linea += a.getSaldoDeudor()+"|";
//                linea += a.getSaldoAcreedor();
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

        balance = new LinkedList<>();
        balance = new LinkedList<>();
        // buscar formatos

        Calendar fechaDesde = Calendar.getInstance();
        fechaDesde.set(anio, 0, 1);
        double totalSf = 0;

        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        try {
            List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
            // preparar cuentas son de 3 y 5 de longitud
            for (Cuentas c : lCuentas) {
                AuxiliarSigef a = new AuxiliarSigef();
                if (!((c.getCodigofinanzas() == null) || (c.getCodigofinanzas().isEmpty()))) {
//                    es una cuenta que se debe usar hay que ver el tamaño
                    String cuenta = c.getCodigo();
                    a.setNivel2(c.getCodigofinanzas());
//                    extraerr los primeros 3 caracteres
                    String mayor = cuenta.substring(0, 3);
                    String nivel1 = cuenta.substring(3, 5);
                    a.setNivel1(nivel1);
                    a.setMayor(mayor);
                    a.setPeriodo(mes);
                    // saldos iniciales
                    parametros = new HashMap();
                    parametros.put(";where", "o.cuenta like :codigo and o.fecha=:desde"
                            + " and o.cabecera.tipo.equivalencia.codigo in ('I')");
                    parametros.put("codigo", c.getCodigo() + "%");
                    parametros.put("desde", fechaDesde.getTime());
                    parametros.put(";campo", "o.valor");
                    double inicial = ejbRenglones.sumarCampo(parametros).doubleValue();
                    if (inicial > 0) {
                        a.setInicialDeudor(Math.abs(inicial));
                    } else {
                        a.setInicialAcreedor(Math.abs(inicial));
                    }
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :codigo");
//                    parametros.put("codigo", c.getCodigo() + "%");
//                    parametros.put("desde", fechaDesde.getTime());
//                    parametros.put("hasta", fechaHasta.getTime());
//                    parametros.put(";campo", "o.valor");
//                    double flujo = ejbRenglones.sumarCampo(parametros).doubleValue();
//                    if (flujo > 0) {
//                        a.setFlujoDeudor(Math.abs(flujo));
//                    } else {
//                        a.setFlujoAcreedor(Math.abs(flujo));
//                    }
//                    a.setSumasDeudor(a.getInicialDeudor() + a.getFlujoDeudor());
//                    a.setSumasAcreedor(a.getInicialAcreedor() + a.getFlujoAcreedor());
//                    double saldo = a.getSumasDeudor() - a.getSumasAcreedor();
//                    if (saldo > 0) {
//                        a.setSaldoDeudor(Math.abs(saldo));
//                    } else {
//                        a.setSaldoAcreedor(Math.abs(saldo));
//                    }
                    if ((a.getInicialDeudor() + a.getInicialAcreedor()) != 0) {
                        balance.add(a);
                    }
                }

            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(EsigeInicialBean.class
                            .getName()).log(Level.SEVERE, null, ex);
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
     *
     *
     * /
     *
     **
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

}
