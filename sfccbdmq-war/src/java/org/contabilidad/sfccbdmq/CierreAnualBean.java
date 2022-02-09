/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cierreAnualSfccbdmq")
@ViewScoped
public class CierreAnualBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public CierreAnualBean() {

    }

    private Formulario formulario = new Formulario();
    private List<Renglones> listaAsientoUno;
    private List<Renglones> listaAsientoDos;
    private List<Renglones> listaAsientoTres;
    private List<Renglones> listaAsientoTresReclasi;
    private List<Renglones> listaAsientoCuatro;
    private List<Renglones> listaAsientoCinco;
    private List<Renglones> listaResumido;
    private List<Cuentas> listaCuentasFinal;
    private boolean existenPendientes;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabecerasFacade ejbCabeceras;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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
//        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "CierreAnualVista";
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
        buscar();
    }

    public String buscar() {
        try {
            Map parametros = new HashMap();
            Calendar c = Calendar.getInstance();
            Calendar periodo = Calendar.getInstance();
            periodo.setTime(configuracionBean.getConfiguracion().getPvigente());
            c.setTime(configuracionBean.getConfiguracion().getPfinal());
            periodo.add(Calendar.MONTH, 1);
            existenPendientes = false;
            if (c.getTimeInMillis() >= periodo.getTimeInMillis()) {
                MensajesErrores.advertencia("Existen peridos no cerrados");
                existenPendientes = true;
//                return null;
            }

            parametros.put(";where", "o.tipo=1");
//            SimpleDateFormat sdf = new SimpleDateFormat("YYYY");

            Date fecha = getConfiguracionBean().getConfiguracion().getPfinal();
            Calendar ca = Calendar.getInstance();
            ca.setTime(fecha);
            int anio = ca.get(Calendar.YEAR);
            int anioApertura = anio + 1;

            parametros.put(";orden", "o.inicial asc");
            List<Formatos> lf = ejbFormatos.encontarParametros(parametros);
            double total = 0;
            double totalEjerciciosAnt = 0;
            listaAsientoUno = new LinkedList<>();
            for (Formatos f : lf) {
                // traer los registros por cuentas
                parametros = new HashMap();
                parametros.put(";where", "o.codigo like :codigo and o.imputable=true");
                parametros.put(";orden", "o.codigo");
                parametros.put("codigo", f.getInicial() + "%");
                List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);

                for (Cuentas cuenta : lCuentas) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cuenta = :codigo and o.fecha between :desde and :hasta");
                    parametros.put("codigo", cuenta.getCodigo());
                    parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                    parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                    parametros.put(";campo", "(o.valor * o.signo)");
//                    double egresos = ejbRenglones.sumarCampo(parametros).doubleValue();
                    double egresos = ejbRenglones.sumarCampo(parametros).doubleValue() * -1;
                    if (egresos != 0) {
                        Renglones r = new Renglones();
                        r.setCuenta(cuenta.getCodigo());
                        r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                        r.setValor(new BigDecimal(egresos));
                        if (egresos > 0) {
                            r.setDebitos(egresos);
                        } else {
                            r.setCreditos(egresos * -1);
                        }
                        listaAsientoUno.add(r);
                        total += egresos;
                    }
                } // fin for cuentas

            }// fin for  formatos

            Renglones r = new Renglones();
            r.setCuenta(configuracionBean.getConfiguracion().getCtaresultado());
            r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
            r.setValor(new BigDecimal(total * -1));
            listaAsientoUno.add(r);
            // Segundo Asiento
            listaAsientoDos = new LinkedList<>();
            r = new Renglones();
            r.setCuenta(configuracionBean.getConfiguracion().getCtaresultado());
            r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
            r.setValor(new BigDecimal(total));
            listaAsientoDos.add(r);
            r = new Renglones();
            r.setCuenta(configuracionBean.getConfiguracion().getCtareacumulados());
            r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
            r.setValor(new BigDecimal(total * -1));
            listaAsientoDos.add(r);
            totalEjerciciosAnt = total;

            // Asiento tres
            listaAsientoTres = new LinkedList<>();
            listaAsientoTresReclasi = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";where", "o.cuentacierre is not null and o.imputable=true");
            parametros.put(";orden", "o.cuentacierre");
            List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
            List<Renglones> listaCierre = new LinkedList<>();

            for (Cuentas cuenta : lCuentas) {
                parametros = new HashMap();
                parametros.put(";where", "o.cuenta=:codigo and o.fecha between :desde and :hasta");
                parametros.put("codigo", cuenta.getCodigo());
                parametros.put(";campo", "o.auxiliar");
                parametros.put(";suma", "sum(o.valor * o.signo)");
                parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                List<Object[]> objetos = ejbRenglones.sumar(parametros);
                total = 0;
                for (Object[] objeto : objetos) {
                    BigDecimal valor = (BigDecimal) objeto[1];
//                    double valorRedondo = Math.floor(valor.doubleValue() * 100) / 100;
                    double valorRedondo = valor.doubleValue();
                    if (valorRedondo != 0) {
                        r = new Renglones();
                        r.setCuenta(cuenta.getCuentacierre());
                        r.setAuxiliar((String) objeto[0]);
                        r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                        r.setValor(new BigDecimal(valor.doubleValue()));
                        listaAsientoTres.add(r);
                        total += valor.doubleValue();
                        Cuentas cuentaCierre2 = getCuentasBean().traerCuentaCierre(cuenta.getCuentacierre());
                        if (cuentaCierre2 == null) {
                            listaCierre.add(r);
                        }

                        r = new Renglones();
                        r.setCuenta(cuenta.getCodigo());
                        r.setAuxiliar((String) objeto[0]);
                        r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                        r.setValor(new BigDecimal(valor.doubleValue() * -1));
                        listaAsientoTres.add(r);
                    }

                    Cuentas cuentaCierre2 = getCuentasBean().traerCuentaCierre(cuenta.getCuentacierre());
                    if (cuentaCierre2 != null) {
                        r = new Renglones();
                        r.setCuenta(cuentaCierre2.getCuentacierre());
                        r.setAuxiliar((String) objeto[0]);
                        r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                        r.setValor(new BigDecimal(valor.doubleValue()));
                        listaAsientoTresReclasi.add(r);
                        total += valor.doubleValue();
                        listaCierre.add(r);

                        r = new Renglones();
                        r.setCuenta(cuentaCierre2.getCodigo());
                        r.setAuxiliar((String) objeto[0]);
                        r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                        r.setValor(new BigDecimal(valor.doubleValue() * -1));
                        listaAsientoTresReclasi.add(r);
                    }

                }
//                if (total != 0) {
//                    r = new Renglones();
//                    r.setCuenta(cuenta.getCodigo());
//                    r.setReferencia("CUENTA CIERRE PERIODO " + sdf.format(configuracionBean.getConfiguracion().getPfinal()));
//                    r.setValor(new BigDecimal(total));
//                    listaAsientoTres.add(r);
//                }
            }
            // Asiento 4 y 5
            parametros = new HashMap();
            parametros.put(";where", "o.tipo in (0,2)");
            parametros.put(";orden", "o.inicial asc");
            lf = ejbFormatos.encontarParametros(parametros);
            listaAsientoCuatro = new LinkedList<>();
            listaAsientoCinco = new LinkedList<>();
            List<Cuentas> listaCuentasParaCierre = new LinkedList<>();
            for (Formatos f : lf) {
                // traer los registros por cuentas
                parametros = new HashMap();
                parametros.put(";where", "o.codigo like :codigo and o.imputable=true and o.cuentacierre is null");
                parametros.put(";orden", "o.codigo");
                parametros.put("codigo", f.getInicial() + "%");
                lCuentas = ejbCuentas.encontarParametros(parametros);
                for (Cuentas cuenta : lCuentas) {
                    if (cuenta.getAuxiliares() == null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.cuenta=:codigo and o.fecha between :desde and :hasta");
                        parametros.put("codigo", cuenta.getCodigo());
                        parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                        parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                        parametros.put(";campo", "(o.valor * o.signo)");
//                    double egresos = ejbRenglones.sumarCampo(parametros).doubleValue();
                        double egresos = ejbRenglones.sumarCampo(parametros).doubleValue();
                        if (egresos != 0) {
                            r = new Renglones();
                            r.setCuenta(cuenta.getCodigo());
                            r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                            r.setValor(new BigDecimal(egresos * -1));
                            listaAsientoCuatro.add(r);
                            r = new Renglones();
                            r.setCuenta(cuenta.getCodigo());
                            r.setReferencia("ASIENTO APERTURA PERIODO " + anioApertura);
                            r.setValor(new BigDecimal(egresos));
                            listaAsientoCinco.add(r);
                            listaCuentasParaCierre.add(cuenta);
                        }
                    } else {
                        parametros = new HashMap();
                        parametros.put(";where", "o.cuenta=:codigo and o.fecha between :desde and :hasta");
                        parametros.put("codigo", cuenta.getCodigo());
                        parametros.put(";campo", "o.auxiliar");
                        parametros.put(";suma", "sum(o.valor * o.signo)");
                        parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                        parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                        List<Object[]> objetos = ejbRenglones.sumar(parametros);
                        total = 0;
                        for (Object[] objeto : objetos) {
                            BigDecimal valor = (BigDecimal) objeto[1];
//                            double egresos = Math.floor(valor.doubleValue() * 100) / 100;
                            double egresos = valor.doubleValue();
                            if (egresos != 0) {
                                r = new Renglones();
                                r.setCuenta(cuenta.getCodigo());
                                r.setAuxiliar((String) objeto[0]);
                                r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                                r.setValor(new BigDecimal(egresos * -1));
                                listaAsientoCuatro.add(r);
                                r = new Renglones();
                                r.setCuenta(cuenta.getCodigo());
                                r.setAuxiliar((String) objeto[0]);
                                r.setReferencia("ASIENTO APERTURA PERIODO " + anioApertura);
                                r.setValor(new BigDecimal(egresos));
                                listaAsientoCinco.add(r);
                                listaCuentasParaCierre.add(cuenta);
                            }
                        }
                    }
                }
            }// fin for  formatos
            //Cuentas de cierre para el asiento de apertura
            for (Renglones rc : listaCierre) {
                r = new Renglones();
                r.setCuenta(rc.getCuenta());
                r.setAuxiliar((rc.getAuxiliar()));
                r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                r.setValor(new BigDecimal(rc.getValor().doubleValue() * -1));
                listaAsientoCuatro.add(r);

                r = new Renglones();
                r.setCuenta(rc.getCuenta());
                r.setAuxiliar((rc.getAuxiliar()));
                r.setReferencia("ASIENTO APERTURA PERIODO " + anioApertura);
                r.setValor(new BigDecimal(rc.getValor().doubleValue()));
                listaAsientoCinco.add(r);
            }

            //Acumulado las cuentas de cierre para la apertura
            listaCuentasFinal = new LinkedList<>();
            for (Renglones rcf : listaCierre) {
                Cuentas cu = getCuentasBean().traerCodigo(rcf.getCuenta());
                if (cu != null) {
                    if (listaCuentasFinal.isEmpty()) {
                        listaCuentasFinal.add(cu);
                    } else {
                        yaEsta(cu);
                    }
                }
            }
            for (Cuentas cue : listaCuentasParaCierre) {
                if (yaEstaCierre(cue)) {
                    listaCuentasFinal.remove(cue);
                }
            }

            for (Cuentas cuenta : listaCuentasFinal) {
                if (cuenta.getAuxiliares() == null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cuenta=:codigo and o.fecha between :desde and :hasta");
                    parametros.put("codigo", cuenta.getCodigo());
                    parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                    parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                    parametros.put(";campo", "(o.valor * o.signo)");
                    double egresos = ejbRenglones.sumarCampo(parametros).doubleValue();
                    if (egresos != 0) {
                        r = new Renglones();
                        r.setCuenta(cuenta.getCodigo());
                        r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                        r.setValor(new BigDecimal(egresos * -1));
                        listaAsientoCuatro.add(r);
                        r = new Renglones();
                        r.setCuenta(cuenta.getCodigo());
                        r.setReferencia("ASIENTO APERTURA PERIODO " + anioApertura);
                        r.setValor(new BigDecimal(egresos));
                        listaAsientoCinco.add(r);
                    }
                } else {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cuenta=:codigo and o.fecha between :desde and :hasta");
                    parametros.put("codigo", cuenta.getCodigo());
                    parametros.put(";campo", "o.auxiliar");
                    parametros.put(";suma", "sum(o.valor * o.signo)");
                    parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                    parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                    List<Object[]> objetos = ejbRenglones.sumar(parametros);
                    total = 0;
                    for (Object[] objeto : objetos) {
                        BigDecimal valor = (BigDecimal) objeto[1];
//                        double egresos = Math.floor(valor.doubleValue() * 100) / 100;
                        double egresos = valor.doubleValue();
                        if (egresos != 0) {
                            r = new Renglones();
                            r.setCuenta(cuenta.getCodigo());
                            r.setAuxiliar((String) objeto[0]);
                            r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
                            r.setValor(new BigDecimal(egresos * -1));
                            listaAsientoCuatro.add(r);
                            r = new Renglones();
                            r.setCuenta(cuenta.getCodigo());
                            r.setAuxiliar((String) objeto[0]);
                            r.setReferencia("ASIENTO APERTURA PERIODO " + anioApertura);
                            r.setValor(new BigDecimal(egresos));
                            listaAsientoCinco.add(r);
                        }
                    }
                }
            }

            r = new Renglones();
            r.setCuenta(configuracionBean.getConfiguracion().getCtareacumulados());
            r.setReferencia("ASIENTO CIERRE PERIODO " + anio);
            r.setValor(new BigDecimal(totalEjerciciosAnt));
            listaAsientoCuatro.add(r);
            r = new Renglones();
            r.setCuenta(configuracionBean.getConfiguracion().getCtareacumulados());
            r.setReferencia("ASIENTO APERTURA PERIODO " + anioApertura);
            r.setValor(new BigDecimal(totalEjerciciosAnt * -1));
            listaAsientoCinco.add(r);

            //Totales de las listas
            //lista uno
            double tdebe = 0;
            double cred = 0;
            for (Renglones r1 : listaAsientoUno) {
                if (r1.getValor().doubleValue() > 0) {
                    r1.setDebitos(r1.getValor().abs().doubleValue());
                    r1.setCreditos(new Double(0));
                } else {
                    r1.setDebitos(new Double(0));
                    r1.setCreditos(r1.getValor().abs().doubleValue());

                }
                r1.setSigno(1);
                tdebe += r1.getDebitos() == null ? 0 : r1.getDebitos();
                cred += r1.getCreditos() == null ? 0 : r1.getCreditos();
            }
            Renglones r1 = new Renglones();
            if (!listaAsientoUno.isEmpty()) {
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                r1.setSigno(1);
                listaAsientoUno.add(r1);
            }
            //lista dos
            tdebe = 0;
            cred = 0;
            for (Renglones r2 : listaAsientoDos) {
                if (r2.getValor().doubleValue() > 0) {
                    r2.setDebitos(r2.getValor().abs().doubleValue());
                    r2.setCreditos(new Double(0));
                } else {
                    r2.setDebitos(new Double(0));
                    r2.setCreditos(r2.getValor().abs().doubleValue());
                }
                tdebe += r2.getDebitos() == null ? 0 : r2.getDebitos();
                cred += r2.getCreditos() == null ? 0 : r2.getCreditos();
                r2.setSigno(1);
            }
            if (!listaAsientoDos.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                listaAsientoDos.add(r1);
            }
            //lista tres
            tdebe = 0;
            cred = 0;
            for (Renglones r3 : listaAsientoTres) {
                if (r3.getValor().doubleValue() > 0) {
                    r3.setDebitos(r3.getValor().abs().doubleValue());
                    r3.setCreditos(new Double(0));
                } else {
                    r3.setDebitos(new Double(0));
                    r3.setCreditos(r3.getValor().abs().doubleValue());
                }
                tdebe += r3.getDebitos() == null ? 0 : r3.getDebitos();
                cred += r3.getCreditos() == null ? 0 : r3.getCreditos();
                r3.setSigno(1);
            }
            if (!listaAsientoTres.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                listaAsientoTres.add(r1);
            }
            //lista tres rec
            tdebe = 0;
            cred = 0;
            for (Renglones r31 : listaAsientoTresReclasi) {
                if (r31.getValor().doubleValue() > 0) {
                    r31.setDebitos(r31.getValor().abs().doubleValue());
                    r31.setCreditos(new Double(0));
                } else {
                    r31.setDebitos(new Double(0));
                    r31.setCreditos(r31.getValor().abs().doubleValue());
                }
                tdebe += r31.getDebitos() == null ? 0 : r31.getDebitos();
                cred += r31.getCreditos() == null ? 0 : r31.getCreditos();
                r31.setSigno(1);
            }
            if (!listaAsientoTresReclasi.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                listaAsientoTresReclasi.add(r1);
            }
            //lista cuatro
            tdebe = 0;
            cred = 0;
            for (Renglones r4 : listaAsientoCuatro) {
                if (r4.getValor().doubleValue() > 0) {
                    r4.setDebitos(r4.getValor().abs().doubleValue());
                    r4.setCreditos(new Double(0));
                } else {
                    r4.setDebitos(new Double(0));
                    r4.setCreditos(r4.getValor().abs().doubleValue());
                }
                tdebe += r4.getDebitos() == null ? 0 : r4.getDebitos();
                cred += r4.getCreditos() == null ? 0 : r4.getCreditos();
                r4.setSigno(1);
            }
            if (!listaAsientoCuatro.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                listaAsientoCuatro.add(r1);
            }
            //lista cinco
            setListaResumido(new LinkedList<>());
            //Por alguna razon el resumido le esta afectando al asiento de apertura al cinco
//            for (Renglones rAper : listaAsientoCinco) {
//                agrupaEnRenglones(rAper);
//            }

            tdebe = 0;
            cred = 0;
            for (Renglones r5 : listaAsientoCinco) {
                if (r5.getValor().doubleValue() > 0) {
                    r5.setDebitos(r5.getValor().abs().doubleValue());
                    r5.setCreditos(new Double(0));
                } else {
                    r5.setDebitos(new Double(0));
                    r5.setCreditos(r5.getValor().abs().doubleValue());
                }
                tdebe += r5.getDebitos() == null ? 0 : r5.getDebitos();
                cred += r5.getCreditos() == null ? 0 : r5.getCreditos();
                r5.setSigno(1);
            }
            if (!listaAsientoCinco.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                listaAsientoCinco.add(r1);
            }

            //lista cinco Resumido
            tdebe = 0;
            cred = 0;
            for (Renglones r6 : getListaResumido()) {
                if (r6.getValor().doubleValue() > 0) {
                    r6.setDebitos(r6.getValor().abs().doubleValue());
                    r6.setCreditos(new Double(0));
                } else {
                    r6.setDebitos(new Double(0));
                    r6.setCreditos(r6.getValor().abs().doubleValue());
                }
                tdebe += r6.getDebitos() == null ? 0 : r6.getDebitos();
                cred += r6.getCreditos() == null ? 0 : r6.getCreditos();
                r6.setSigno(1);
            }
            if (!listaResumido.isEmpty()) {
                r1 = new Renglones();
                r1.setReferencia("TOTAL");
                r1.setDebitos(tdebe);
                r1.setCreditos(cred);
                getListaResumido().add(r1);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CierreAnualBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void yaEsta(Cuentas cuen) {
        for (Cuentas c : listaCuentasFinal) {
            if (c.getCodigo().equals(cuen.getCodigo())) {
                return;
            }
        }
        listaCuentasFinal.add(cuen);
    }

    public boolean yaEstaCierre(Cuentas cuen) {
        for (Cuentas c : listaCuentasFinal) {
            if (c.getCodigo().equals(cuen.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public String grabar() {
        //Cuadre de valores
        double valor = 0;
        for (Renglones r : listaAsientoUno) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        double cuadre = Math.round(valor * 100);
        double dividido = cuadre / 100;
        BigDecimal valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de Cierre Cuentas de Resultados descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : listaAsientoDos) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadre = Math.round(valor * 100);
        dividido = cuadre / 100;
        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de Cierre Resultados descuadrado no se puede grabar");
            return null;
        }
        valor = 0;
        for (Renglones r : listaAsientoTres) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadre = Math.round(valor * 100);
        dividido = cuadre / 100;
        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de Reclasificación Cuentas de Cierre descuadrado no se puede grabar");
            return null;
        }
        for (Renglones r : listaAsientoTresReclasi) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadre = Math.round(valor * 100);
        dividido = cuadre / 100;
        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de Reclasificación Cuentas 15292 - 15298 descuadrado no se puede grabar");
            return null;
        }
//        valor = 0;
//        for (Renglones r : listaAsientoCuatro) {
//            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
//        }
//        cuadre = Math.round(valor * 100);
//        dividido = cuadre / 100;
//        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
//        if (valorBase.doubleValue() != 0) {
//            MensajesErrores.fatal("Asiento cuatro descuadrado no se puede grabar");
//            return null;
//        }
        valor = 0;
        for (Renglones r : listaAsientoCinco) {
            valor += r.getValor() == null ? 0 : r.getValor().doubleValue();
        }
        cuadre = Math.round(valor * 100);
        dividido = cuadre / 100;
        valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        if (valorBase.doubleValue() != 0) {
            MensajesErrores.fatal("Asiento de Apertura descuadrado no se puede grabar");
            return null;
        }
        try {
            //Asientos Cierre = codigo = 90 y apertura = codigo = 00
            Tipoasiento tipoAisentoCierre = null;
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo='90'");
            List<Tipoasiento> listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoAisentoCierre = t;
            }
            if (tipoAisentoCierre == null) {
                MensajesErrores.advertencia("No existe  tipo de asiento cierre");
                return null;
            }
            int numeroAsiento = tipoAisentoCierre.getUltimo();
            numeroAsiento++;
            tipoAisentoCierre.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(tipoAisentoCierre, seguridadbean.getLogueado().getUserid());

            Tipoasiento tipoAisentoApertura = null;
            parametros = new HashMap();
            parametros.put(";where", "o.codigo='00'");
            List<Tipoasiento> listaTipoA = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipoA) {
                tipoAisentoApertura = t;
            }
            if (tipoAisentoApertura == null) {
                MensajesErrores.advertencia("No existe  tipo de asiento cierre");
                return null;
            }
            int numeroAsientoA = tipoAisentoApertura.getUltimo();
            numeroAsientoA++;
            tipoAisentoApertura.setUltimo(numeroAsientoA);
            ejbTipoAsiento.edit(tipoAisentoApertura, seguridadbean.getLogueado().getUserid());

            //lista cierre uno
            Cabeceras cab;
            if (!listaAsientoUno.isEmpty()) {
                cab = new Cabeceras();
                cab.setDescripcion("Cierre Cuentas de Resultados");
                cab.setDia(new Date());
                cab.setFecha(configuracionBean.getConfiguracion().getPfinal());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setTipo(tipoAisentoCierre);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : listaAsientoUno) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                    } else {
                        r.setCabecera(cab);
                        r.setFecha(configuracionBean.getConfiguracion().getPfinal());
                        r.setSigno(1);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            // lista cierre dos
            if (!listaAsientoDos.isEmpty()) {
                numeroAsiento = tipoAisentoCierre.getUltimo();
                numeroAsiento++;
                tipoAisentoCierre.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoCierre, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion("Cierre Resultados");
                cab.setDia(new Date());
                cab.setFecha(configuracionBean.getConfiguracion().getPfinal());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setTipo(tipoAisentoCierre);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : listaAsientoDos) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                    } else {
                        r.setCabecera(cab);
                        r.setFecha(configuracionBean.getConfiguracion().getPfinal());
                        r.setSigno(1);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            // lista asiento tres
            if (!listaAsientoTres.isEmpty()) {
                numeroAsiento = tipoAisentoCierre.getUltimo();
                numeroAsiento++;
                tipoAisentoCierre.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoCierre, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion("Reclasificación Cuentas de Cierre");
                cab.setDia(new Date());
                cab.setFecha(configuracionBean.getConfiguracion().getPfinal());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setTipo(tipoAisentoCierre);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : listaAsientoTres) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                    } else {
                        r.setCabecera(cab);
                        r.setFecha(configuracionBean.getConfiguracion().getPfinal());
                        r.setSigno(1);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            // lista asiento tres reclasif
            if (!listaAsientoTresReclasi.isEmpty()) {
                numeroAsiento = tipoAisentoCierre.getUltimo();
                numeroAsiento++;
                tipoAisentoCierre.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(tipoAisentoCierre, seguridadbean.getLogueado().getUserid());
                cab = new Cabeceras();
                cab.setDescripcion("Reclasificación Cuentas 15292 - 15298");
                cab.setDia(new Date());
                cab.setFecha(configuracionBean.getConfiguracion().getPfinal());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsiento);
                cab.setTipo(tipoAisentoCierre);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : listaAsientoTresReclasi) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                    } else {
                        r.setCabecera(cab);
                        r.setFecha(configuracionBean.getConfiguracion().getPfinal());
                        r.setSigno(1);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            // lista asiento cuatro
//            if (!listaAsientoCuatro.isEmpty()) {
//                numeroAsiento = tipoAisentoCierre.getUltimo();
//                numeroAsiento++;
//                tipoAisentoCierre.setUltimo(numeroAsiento);
//                ejbTipoAsiento.edit(tipoAisentoCierre, seguridadbean.getLogueado().getUserid());
//                cab = new Cabeceras();
//                cab.setDescripcion("Cuarto asiento cierre");
//                cab.setDia(new Date());
//                cab.setFecha(configuracionBean.getConfiguracion().getPfinal());
//                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
//                cab.setNumero(numeroAsiento);
//                cab.setTipo(tipoAisentoCierre);
//                cab.setUsuario(seguridadbean.getLogueado().getUserid());
//                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
//                for (Renglones r : listaAsientoCuatro) {
//                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
//                    } else {
//                        r.setCabecera(cab);
//                        r.setFecha(configuracionBean.getConfiguracion().getPfinal());
//                        r.setSigno(1);
//                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//                    }
//                }
//            }

            //Asiento apertura
            Calendar c = Calendar.getInstance();
            c.setTime(configuracionBean.getConfiguracion().getPinicial());
            int anio = c.get(Calendar.YEAR);
            anio = anio + 1;

            Calendar fechaApertura = Calendar.getInstance();
            fechaApertura.set(anio, 0, 1);

            if (!listaAsientoCinco.isEmpty()) {
                cab = new Cabeceras();
                cab.setDescripcion("Asiento Apertura");
                cab.setDia(new Date());
                cab.setFecha(fechaApertura.getTime());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setNumero(numeroAsientoA);
                cab.setTipo(tipoAisentoApertura);
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
                for (Renglones r : listaAsientoCinco) {
                    if ((r.getCuenta() == null) || (r.getCuenta().isEmpty())) {
                    } else {
                        r.setCabecera(cab);
                        r.setFecha(fechaApertura.getTime());
                        r.setSigno(1);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizarRolBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CierreAnualBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        listaAsientoUno = new LinkedList<>();
        listaAsientoDos = new LinkedList<>();
        listaAsientoTres = new LinkedList<>();
        listaAsientoTresReclasi = new LinkedList<>();
        listaAsientoCuatro = new LinkedList<>();
        listaAsientoCinco = new LinkedList<>();
        setListaResumido(new LinkedList<>());
        listaCuentasFinal = new LinkedList<>();
        formulario.cancelar();
        return null;
    }

    private void agrupaEnRenglones(Renglones rApertura) {
        for (Renglones rCierre : getListaResumido()) {
            if ((rCierre.getCuenta().equals(rApertura.getCuenta()))) {
                double valor = rApertura.getValor().doubleValue() + rCierre.getValor().doubleValue();
                rCierre.setValor(new BigDecimal(valor));
                return;
            }
        }
        getListaResumido().add(rApertura);
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
     * @return the existenPendientes
     */
    public boolean isExistenPendientes() {
        return existenPendientes;
    }

    /**
     * @param existenPendientes the existenPendientes to set
     */
    public void setExistenPendientes(boolean existenPendientes) {
        this.existenPendientes = existenPendientes;
    }

    /**
     * @return the listaAsientoUno
     */
    public List<Renglones> getListaAsientoUno() {
        return listaAsientoUno;
    }

    /**
     * @param listaAsientoUno the listaAsientoUno to set
     */
    public void setListaAsientoUno(List<Renglones> listaAsientoUno) {
        this.listaAsientoUno = listaAsientoUno;
    }

    /**
     * @return the listaAsientoDos
     */
    public List<Renglones> getListaAsientoDos() {
        return listaAsientoDos;
    }

    /**
     * @param listaAsientoDos the listaAsientoDos to set
     */
    public void setListaAsientoDos(List<Renglones> listaAsientoDos) {
        this.listaAsientoDos = listaAsientoDos;
    }

    /**
     * @return the listaAsientoTres
     */
    public List<Renglones> getListaAsientoTres() {
        return listaAsientoTres;
    }

    /**
     * @param listaAsientoTres the listaAsientoTres to set
     */
    public void setListaAsientoTres(List<Renglones> listaAsientoTres) {
        this.listaAsientoTres = listaAsientoTres;
    }

    /**
     * @return the listaAsientoCuatro
     */
    public List<Renglones> getListaAsientoCuatro() {
        return listaAsientoCuatro;
    }

    /**
     * @param listaAsientoCuatro the listaAsientoCuatro to set
     */
    public void setListaAsientoCuatro(List<Renglones> listaAsientoCuatro) {
        this.listaAsientoCuatro = listaAsientoCuatro;
    }

    /**
     * @return the listaAsientoCinco
     */
    public List<Renglones> getListaAsientoCinco() {
        return listaAsientoCinco;
    }

    /**
     * @param listaAsientoCinco the listaAsientoCinco to set
     */
    public void setListaAsientoCinco(List<Renglones> listaAsientoCinco) {
        this.listaAsientoCinco = listaAsientoCinco;
    }

    /**
     * @return the listaCuentasFinal
     */
    public List<Cuentas> getListaCuentasFinal() {
        return listaCuentasFinal;
    }

    /**
     * @param listaCuentasFinal the listaCuentasFinal to set
     */
    public void setListaCuentasFinal(List<Cuentas> listaCuentasFinal) {
        this.listaCuentasFinal = listaCuentasFinal;
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
     * @return the listaAsientoTresReclasi
     */
    public List<Renglones> getListaAsientoTresReclasi() {
        return listaAsientoTresReclasi;
    }

    /**
     * @param listaAsientoTresReclasi the listaAsientoTresReclasi to set
     */
    public void setListaAsientoTresReclasi(List<Renglones> listaAsientoTresReclasi) {
        this.listaAsientoTresReclasi = listaAsientoTresReclasi;
    }

    /**
     * @return the listaResumido
     */
    public List<Renglones> getListaResumido() {
        return listaResumido;
    }

    /**
     * @param listaResumido the listaResumido to set
     */
    public void setListaResumido(List<Renglones> listaResumido) {
        this.listaResumido = listaResumido;
    }
}
