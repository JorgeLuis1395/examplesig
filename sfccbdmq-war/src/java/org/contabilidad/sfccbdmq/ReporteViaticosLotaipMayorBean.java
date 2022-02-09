/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.text.DecimalFormat;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.AuxiliarMayor;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteViaticosLotaipMayorSfccbdmq")
@ViewScoped
public class ReporteViaticosLotaipMayorBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteViaticosLotaipMayorBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private List<AuxiliarMayor> listaMayor;
    private List<Cuentas> listaCuentas;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;

    private double saldoInicialDeudor;
    private double saldoInicialAcreedor;
    private double debeInicial;
    private double haberInicial;
    private double saldoFinal;
    private String cuenta;
    private String cuentaHasta;
    private Resource reporte;

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;

    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CuentasFacade ejbCuentas;

    public String buscar() {
        if (((getCuentaHasta() == null) || (getCuentaHasta().isEmpty()))) {
            setCuentaHasta(getCuenta());
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.imputable=true and o.activo=true"
                + " and o.codigo between :desde and :hasta");

        parametros.put(";orden", "o.codigo asc");
        parametros.put("desde", getCuenta());
        parametros.put("hasta", getCuentaHasta());
        try {
            listaMayor = new LinkedList<>();
            listaCuentas = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : listaCuentas) {
                creaMayor(c, getListaMayor());
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void creaMayor(Cuentas c, List<AuxiliarMayor> lista) {
        try {
            String where = "  o.fecha between :desde and :hasta and "
                    + " o.cuenta=:cuenta and o.cabecera.tipo.equivalencia.codigo not in ('I','C')";
            Map parametros = new HashMap();
            parametros.put("desde", getDesde());
            parametros.put("hasta", getHasta());
            parametros.put("cuenta", c.getCodigo());
            parametros.put(";orden", "o.fecha");
            parametros.put(";where", where);
            calculaTotales(c.getCodigo());
            List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
            double saldoDeudor = 0;
            double totalDebe = 0;
            double saldoAcreedor = 0;
            double totalHaber = 0;
            double saldo = getSaldoInicialDeudor() + getSaldoInicialAcreedor();
//            if (saldo > 0) {
            saldoDeudor = getDebeInicial();
            totalDebe = getDebeInicial();
//            } else {
            saldoAcreedor = getHaberInicial() * -1;
            totalHaber = getHaberInicial() * -1;
//            }
            // ademas los movimientos
            if ((saldo != 0) || (!listaRenglones.isEmpty())) {
                AuxiliarMayor a = new AuxiliarMayor();
                a.setCuenta("");
                a.setNombre("");
                a.setReferencia(c.getCodigo() + " - " + c.getNombre());
                a.setDebe(Math.abs(saldoDeudor));
                a.setDebeNuevo(Math.abs(getSaldoInicialDeudor()));
                a.setHaber(Math.abs(saldoAcreedor));
                a.setHaberNuevo(Math.abs(getSaldoInicialAcreedor()));
                a.setSaldoDeudor(Math.abs(getSaldoInicialDeudor()));
                a.setSaldoAcreedor(Math.abs(getSaldoInicialAcreedor()));
                lista.add(a);
                for (Renglones r : listaRenglones) {
                    a = new AuxiliarMayor();
                    a.setCuenta(c.getCodigo());
                    a.setNombre(c.getNombre());
                    a.setCc(r.getCentrocosto());
                    a.setFecha(r.getFecha());
                    a.setTipo(r.getCabecera().getTipo().getCodigo());
                    a.setEquivalencia(r.getCabecera().getTipo().getEquivalencia().getCodigo());
                    a.setRubro(r.getCabecera().getTipo().getRubro().toString());
                    a.setNumero(r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    a.setCabecera(r.getCabecera());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        // nuevo cambia por el signo
//                        if (r.getSigno() == 1) {
                        a.setDebe(valor * r.getSigno());
                        a.setDebeNuevo(valor * r.getSigno());
                        a.setHaber(0);
                        a.setHaberNuevo(0);
                        totalDebe += valor * r.getSigno();
                        saldo += valor * r.getSigno();
                    } else {
//                        if (r.getSigno() == 1) {
                        a.setDebe(0);
                        a.setDebeNuevo(0);
                        a.setHaber(valor * -1 * r.getSigno());
                        a.setHaberNuevo(valor * -1 * r.getSigno());
                        totalHaber += valor * -1 * r.getSigno();
                        saldo += valor * r.getSigno();
                    }
                    if (saldo > 0) {
                        a.setSaldoDeudor(Math.abs(saldo));
                    } else {
                        a.setSaldoAcreedor(Math.abs(saldo));
                    }
                    lista.add(a);
                }

                a = new AuxiliarMayor();
                a.setCuenta(c.getCodigo());
                a.setNombre(c.getNombre());
                a.setReferencia("TOTAL");
                a.setDebe(totalDebe);
                a.setHaber(totalHaber);
                lista.add(a);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteNivelesVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        setHasta(getConfiguracionBean().getConfiguracion().getPfinal());
        setDesde(getConfiguracionBean().getConfiguracion().getPinicial());
    }

    private void calculaTotales(String cta) {

        Calendar c = Calendar.getInstance();
        c.setTime(getDesde());
        c.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        parametros.put(";campo", "(o.valor*o.signo)");
        String where = "o.cuenta=:cuenta and o.valor>0";
        parametros.put("cuenta", cta);

        Calendar cAnioPedido = Calendar.getInstance();
        cAnioPedido.setTime(getConfiguracionBean().getConfiguracion().getPinicial());
        Calendar cAnioPeriodo = Calendar.getInstance();
        cAnioPeriodo.setTime(getConfiguracionBean().getConfiguracion().getPinicial());
        boolean esEsteAnio = cAnioPedido.get(Calendar.YEAR) == cAnioPeriodo.get(Calendar.YEAR);
        if (esEsteAnio) {
            where += " and  o.cabecera.tipo.equivalencia.codigo='I'"
                    + " and o.fecha between :desde1 and :hasta";
//                    + " or (o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha=:desde1))";
            parametros.put("hasta", getHasta());
            parametros.put("desde1", c.getTime());
        } else {
            where += " and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<=:desde";
            parametros.put("desde", getConfiguracionBean().getConfiguracion().getPinicial());

        }

        try {
            parametros.put(";where", where);
            setDebeInicial(ejbRenglones.sumarCampo(parametros).doubleValue());

            parametros = new HashMap();
            parametros.put(";campo", "(o.valor*o.signo)");
            String where2 = "o.cuenta=:cuenta and o.valor<0";
            parametros.put("cuenta", cta);
            if (esEsteAnio) {
                where2 += " and ( o.cabecera.tipo.equivalencia.codigo='I'"
                        + " and o.fecha between :desde1 and :hasta)";
                parametros.put("hasta", getHasta());
                parametros.put("desde1", c.getTime());
            } else {
                where2 += " and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.fecha<=:desde";
                parametros.put("desde", getConfiguracionBean().getConfiguracion().getPinicial());
            }

            parametros.put(";where", where2);
            setHaberInicial(ejbRenglones.sumarCampo(parametros).doubleValue());

            setSaldoInicialDeudor(getDebeInicial() + getHaberInicial());
            setSaldoInicialAcreedor(0);
            if (getSaldoInicialDeudor() < 0) {
                setSaldoInicialAcreedor(getSaldoInicialDeudor());
                setSaldoInicialDeudor(0);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String imprimir() {
        reporte = null;
        buscar();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto(("DETALLE DE " + (getCuentasBean().traerNombre(cuenta) + " - " + (getCuentasBean().traerNombre(cuentaHasta)))), AuxiliarReporte.ALIGN_CENTER, 8, true);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafoCompleto(("Periodo Desde: " + (sdf.format(desde) + " Hasta: " + (sdf.format(hasta)))), AuxiliarReporte.ALIGN_LEFT, 6, true);
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No."));
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            double totalFinalDebe = 0.0;
            double totalFinalHaber = 0.0;
            for (AuxiliarMayor r : listaMayor) {
                if (!r.getReferencia().equals("TOTAL")) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, getCuentasBean().traerNombre(r.getCuenta())));
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getFecha()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getTipo()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getNumero() != null ? r.getNumero() + "" : ""));
                    columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(r.getDebe())));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(r.getHaber())));
                } else {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, r.getReferencia() + " " + getCuentasBean().traerNombre(r.getCuenta())));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(r.getDebe())));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(r.getHaber())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    totalFinalDebe += r.getDebe();
                    totalFinalHaber += r.getHaber();
                }
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL " + (getCuentasBean().traerNombre(cuenta) + " - " + (getCuentasBean().traerNombre(cuentaHasta)))));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(totalFinalDebe)));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(totalFinalHaber)));
            pdf.agregarTablaReporte(titulos, columnas, 8, 100, null);
            // disponible el pagos
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporte(pdf.traerRecurso());
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosLotaipMayorBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listaCuentas
     */
    public List<Cuentas> getListaCuentas() {
        return listaCuentas;
    }

    /**
     * @param listaCuentas the listaCuentas to set
     */
    public void setListaCuentas(List<Cuentas> listaCuentas) {
        this.listaCuentas = listaCuentas;
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
     * @return the saldoInicialDeudor
     */
    public double getSaldoInicialDeudor() {
        return saldoInicialDeudor;
    }

    /**
     * @param saldoInicialDeudor the saldoInicialDeudor to set
     */
    public void setSaldoInicialDeudor(double saldoInicialDeudor) {
        this.saldoInicialDeudor = saldoInicialDeudor;
    }

    /**
     * @return the saldoInicialAcreedor
     */
    public double getSaldoInicialAcreedor() {
        return saldoInicialAcreedor;
    }

    /**
     * @param saldoInicialAcreedor the saldoInicialAcreedor to set
     */
    public void setSaldoInicialAcreedor(double saldoInicialAcreedor) {
        this.saldoInicialAcreedor = saldoInicialAcreedor;
    }

    /**
     * @return the debeInicial
     */
    public double getDebeInicial() {
        return debeInicial;
    }

    /**
     * @param debeInicial the debeInicial to set
     */
    public void setDebeInicial(double debeInicial) {
        this.debeInicial = debeInicial;
    }

    /**
     * @return the haberInicial
     */
    public double getHaberInicial() {
        return haberInicial;
    }

    /**
     * @param haberInicial the haberInicial to set
     */
    public void setHaberInicial(double haberInicial) {
        this.haberInicial = haberInicial;
    }

    /**
     * @return the saldoFinal
     */
    public double getSaldoFinal() {
        return saldoFinal;
    }

    /**
     * @param saldoFinal the saldoFinal to set
     */
    public void setSaldoFinal(double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    /**
     * @return the cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    /**
     * @return the cuentaHasta
     */
    public String getCuentaHasta() {
        return cuentaHasta;
    }

    /**
     * @param cuentaHasta the cuentaHasta to set
     */
    public void setCuentaHasta(String cuentaHasta) {
        this.cuentaHasta = cuentaHasta;
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
}
