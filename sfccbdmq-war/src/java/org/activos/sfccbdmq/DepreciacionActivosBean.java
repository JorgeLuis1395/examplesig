/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.VidasutilesFacade;
import org.beans.sfccbdmq.VistaActivosSinDepreciacionFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.VistaActivosSinDepreciacion;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "depreciacionActivosSfccbdmq")
@ViewScoped
public class DepreciacionActivosBean {

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
     * Creates a new instance of ActivoBean
     */
    public DepreciacionActivosBean() {

    }
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private CabecerasFacade ejbCas;
    @EJB
    private RenglonesFacade ejbRas;
    @EJB
    private TipoasientoFacade ejbTipo;
    @EJB
    private VidasutilesFacade ejbVida;
    @EJB
    private VistaActivosSinDepreciacionFacade ejbActivosSinDepreciacion;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
//    private int tamano;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;
    private Integer mes;
    private Integer anio;
    private Integer ultimoDiaDelMes;
    private List<Depreciaciones> depreciaciones;
    private List<Renglones> listaRas;
    private Formulario formulario = new Formulario();

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
        try {
            mes = ejbDepreciaciones.ultimoMes();
            String mesStr = String.valueOf(mes);
            String anioStr = mesStr.substring(0, 4);
//            String anioStr = mesStr.substring(mesStr.length() - 4, mesStr.length());
            anio = Integer.parseInt(anioStr);
            mesStr = mesStr.replace(anioStr, "");
            mes = Integer.parseInt(mesStr);
//            anio = ejbDepreciaciones.ultimoAnio();
            if ((mes == 0) && (anio == 0)) {
                // no viene nada
                Calendar c = Calendar.getInstance();
                c.setTime(configuracionBean.getConfiguracion().getPvigente());
                mes = c.get(Calendar.MONTH) + 1;
                anio = c.get(Calendar.YEAR);
                if (mes == 0) {
                    anio--;
                    mes = 12;
                }
            } else {
                mes++;
                if (mes == 13) {
                    mes = 1;
                    anio++;
                }
            }
            Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String perfil = (String) params.get("p");
            String redirect = (String) params.get("faces-redirect");
            if (redirect == null) {
                return;
            }
            String nombreForma = "DepreciacionActivosVista";
            if (perfil == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
            this.setPerfil(seguridadbean.traerPerfil(perfil));
            if (this.getPerfil() == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
            Calendar auxilira = Calendar.getInstance();
            auxilira.set(anio, mes, 1);
            auxilira.add(Calendar.DATE, -1);
            ultimoDiaDelMes = auxilira.get(Calendar.DATE);
//        if (nombreForma==null){
//            nombreForma="";
//        }
//            if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//                if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                    MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                    seguridadbean.cerraSession();
//                }
//            }
//    }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ////////////////////////////////////////////////////////77Trato de cambiar el proceso
    public String buscar() {
        depreciaciones = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Calendar c = Calendar.getInstance();
            Calendar cCerrado = Calendar.getInstance();
            c.setTime(configuracionBean.getConfiguracion().getPvigente());
            cCerrado.setTime(configuracionBean.getConfiguracion().getPvigente());
            int anioActual = c.get(Calendar.YEAR);
            int mesActual = c.get(Calendar.MONTH) + 1;
//            if (anioActual < anio) {
//                MensajesErrores.advertencia("No es posible ejecutar la depreciación en fechas mayores de la actual");
//                return null;
//            }

            c.set(anio, mes, 1);
            cCerrado.set(anio, mes - 1, 1);
//            if (cCerrado.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
//                MensajesErrores.advertencia("No es posible ejecutar la depreciación en fechas menores al periodo vigente");
//                return null;
//            }
            System.out.println("Buscando activos cuya fecha de alta sea menor a "
                    + sdf.format(c.getTime()) + " y fecha de baja mayor a " + sdf.format(c.getTime()));
            String vale = ejbCas.validarCierre(cCerrado.getTime());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            c.add(Calendar.DATE, -1);
            cCerrado.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
//            parametros.put(";where", "(o.activo.fechaingreso<:alta) and (o.activo.fechabaja is null or o.activo.fechabaja>:baja ) "
            parametros.put(";where", "(o.activo.fechaingreso<:alta) and (o.activo.fechasolicitud is null or o.activo.fechasolicitud>:baja ) "
                    + "and o.activo.depreciable=true and o.activo.control=false");
//            MensajesErrores.informacion("Buscando activos cuya fecha de alta sea menor a "
//                    + sdf.format(c.getTime()) + " y fecha de baja mayor a " + sdf.format(c.getTime()));
            parametros.put("alta", c.getTime());
            parametros.put("baja", c.getTime());
            parametros.put(";campo", "o.activo");
            parametros.put(";suma", "sum(o.valor),count(o)");
            List<Object[]> listaObjetos = ejbDepreciaciones.sumar(parametros);
            depreciaciones = new LinkedList<>();
            listaRas = new LinkedList<>();
            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            int i = 0;
            for (Object[] objeto : listaObjetos) {

                // Activo
                Activos a = (Activos) objeto[0];

                Double sumaDepreciacion = (Double) objeto[1];
                int cuantasCoutas = ((Long) objeto[2]).intValue();
                Calendar cActivo = Calendar.getInstance();
                cActivo.setTime(a.getFechaingreso());
//                cActivo.setTime(a.getFechaalta());
                int mesAlta = cActivo.get(Calendar.MONTH) + 1;
                int anioAlta = cActivo.get(Calendar.YEAR);
                int diaAlta = cActivo.get(Calendar.DATE);
                cActivo.add(Calendar.MONTH, a.getVidautil() + 1);
//                cActivo.add(Calendar.MONTH, 1);
                if (cActivo.getTime().after(c.getTime())) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.activo=:activo");
                    parametros.put("activo", a);
                    parametros.put(";campo", "o.valor");
                    int dias = 30;
                    if ((anio == anioAlta) && (mes == mesAlta)) {
                        dias = ultimoDiaDelMes - diaAlta;
                    }
                    if (a.getId() == 271074) {
                        int x = 0;
                    }
                    float porcentaje = (1 - (a.getValorresidual() == null ? 0 : a.getValorresidual()) / 100);
                    float valorResidual = a.getValoralta().floatValue() * porcentaje;
                    Depreciaciones d = new Depreciaciones();
                    d.setActivo(a);
                    d.setAnio(anio);
                    d.setMes(mes);
                    d.setDepreciacion(a.getVidautil());
                    d.setDias(dias);

                    float valorDepreciarDiaria = (valorResidual) / (a.getVidautil() * 30);
                    float valorDepreciar = valorDepreciarDiaria * dias;
                    d.setValor(valorDepreciar);
                    if (sumaDepreciacion + valorDepreciar <= valorResidual) {

                        String cuentaDebito = d.getActivo().getClasificacion().getParametros();
//                        String cuentaDebito = d.getActivo().getClasificacion().getParametros() + d.getActivo().getGrupo().getTipo().getCuenta();
                        String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                        Renglones r = new Renglones();
                        r.setCuenta(cuentaDebito);
                        r.setFecha(c.getTime());
                        r.setReferencia("Cuenta Activo depreciación : " + anioMes);
                        r.setValor(new BigDecimal(d.getValor()));
                        estaEnRas(r);
                        r = new Renglones();
                        r.setCuenta(cuentaCredito);
                        r.setFecha(c.getTime());
                        r.setReferencia("Cuenta crédito depreciación : " + anioMes);
                        r.setValor(new BigDecimal(d.getValor() * -1));
                        estaEnRas(r);
                        d.setCuentaDebito(cuentaDebito);
                        d.setCuentaCredito(cuentaCredito);
                        depreciaciones.add(d);

                    } else if (cuantasCoutas + 1 == a.getVidautil()) {
                        String cuentaDebito = d.getActivo().getClasificacion().getParametros();
                        String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                        Renglones r = new Renglones();
                        r.setCuenta(cuentaDebito);
                        r.setFecha(c.getTime());
                        r.setReferencia("Cuenta Activo depreciación : " + anioMes);
                        r.setValor(new BigDecimal(d.getValor()));
                        estaEnRas(r);
                        r = new Renglones();
                        r.setCuenta(cuentaCredito);
                        r.setFecha(c.getTime());
                        r.setReferencia("Cuenta crédito depreciación : " + anioMes);
                        r.setValor(new BigDecimal(d.getValor() * -1));
                        estaEnRas(r);
                        d.setCuentaDebito(cuentaDebito);
                        d.setCuentaCredito(cuentaCredito);
                        depreciaciones.add(d);
                    }
                }

                //  fin
                ++i;
                System.out.println("Activo : " + a.getCodigo() + " " + i);
            }
            buscarActivosSinDepreciar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
//cambia a procesar de la nueva vista
    public String buscarActivosSinDepreciar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Calendar c = Calendar.getInstance();
            Calendar cCerrado = Calendar.getInstance();
            c.setTime(configuracionBean.getConfiguracion().getPvigente());
            cCerrado.setTime(configuracionBean.getConfiguracion().getPvigente());
            int anioActual = c.get(Calendar.YEAR);
            int mesActual = c.get(Calendar.MONTH) + 1;
            if (anioActual < anio) {
                MensajesErrores.advertencia("No es posible ejecutar la depreciación en fechas mayores de la actual");
                return null;

            }

            c.set(anio, mes, 1);
            cCerrado.set(anio, mes - 1, 1);
//            estaba el ultimo dia del mes anterior ahora es el ultimo dia del mes actual
//            c.set(anio, mes - 1, 1);
            if (cCerrado.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {

                MensajesErrores.advertencia("No es posible ejecutar la depreciación en fechas menores al periodo vigente");
                return null;
            }
            System.out.println("Buscando activos cuya fecha de alta sea menor a "
                    + sdf.format(c.getTime()) + " y fecha de baja mayor a " + sdf.format(c.getTime()));
            String vale = ejbCas.validarCierre(cCerrado.getTime());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
//            c.set(anio, mes, 1);
            c.add(Calendar.DATE, -1);
            cCerrado.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
//            parametros.put(";where", "(o.fechaalta<:alta) and (o.fechabaja is null or o.fechabaja>:baja ) "
            parametros.put(";where", "(o.fechaingreso<:alta) and (o.fechabaja is null or o.fechabaja>:baja )");
//            MensajesErrores.informacion("Buscando activos cuya fecha de alta sea menor a "
//                    + sdf.format(c.getTime()) + " y fecha de baja mayor a " + sdf.format(c.getTime()));
            parametros.put("alta", c.getTime());
            parametros.put("baja", c.getTime());
            List<VistaActivosSinDepreciacion> vistaLista = ejbActivosSinDepreciacion.encontarParametros(parametros);

            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            int i = 0;
            for (VistaActivosSinDepreciacion v : vistaLista) {
                // Activo
                Activos a = ejbActivos.find(v.getId());
                Calendar cActivo = Calendar.getInstance();
                cActivo.setTime(a.getFechaingreso());
//                cActivo.setTime(a.getFechaalta());
                int mesAlta = cActivo.get(Calendar.MONTH) + 1;
                int anioAlta = cActivo.get(Calendar.YEAR);
                int diaAlta = cActivo.get(Calendar.DATE);
                cActivo.add(Calendar.MONTH, a.getVidautil() + 1);
//                cActivo.add(Calendar.MONTH, 1);
                if (cActivo.getTime().after(c.getTime())) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.activo=:activo");
                    parametros.put("activo", a);
                    parametros.put(";campo", "o.valor");
                    int dias = 30;
                    if ((anio == anioAlta) && (mes == mesAlta)) {
                        dias = ultimoDiaDelMes - diaAlta;
                    }
                    float sumaDepreciacion = 0;

                    float porcentaje = (1 - (a.getValorresidual() == null ? 0 : a.getValorresidual()) / 100);
                    float valorResidual = a.getValoralta().floatValue() * porcentaje;
                    Depreciaciones d = new Depreciaciones();
                    d.setActivo(a);
                    d.setAnio(anio);
                    d.setMes(mes);
                    d.setDepreciacion(a.getVidautil());
                    float valorDepreciarDiaria = (valorResidual) / (a.getVidautil() * 30);
                    float valorDepreciar = valorDepreciarDiaria * dias;
                    d.setValor(valorDepreciar);
                    if (sumaDepreciacion + valorDepreciar <= valorResidual) {

                        String cuentaDebito = d.getActivo().getClasificacion().getParametros();
//                        String cuentaDebito = d.getActivo().getClasificacion().getParametros() + d.getActivo().getGrupo().getTipo().getCuenta();

                        String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                        Renglones r = new Renglones();
                        r.setCuenta(cuentaDebito);
                        r.setFecha(c.getTime());
                        r.setReferencia("Cuenta Activo depreciación : " + anioMes);
                        r.setValor(new BigDecimal(d.getValor()));
                        estaEnRas(r);
                        r = new Renglones();
                        r.setCuenta(cuentaCredito);
                        r.setFecha(c.getTime());
                        r.setReferencia("Cuenta crédito depreciación : " + anioMes);
                        r.setValor(new BigDecimal(d.getValor() * -1));
                        estaEnRas(r);
                        d.setCuentaDebito(cuentaDebito);
                        d.setCuentaCredito(cuentaCredito);
                        depreciaciones.add(d);

                    }
                }

                //  fin
                ++i;
                System.out.println("Activo : " + a.getCodigo() + " " + i);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean estaEnRas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        for (Renglones r : getListaRas()) {
            if (r.getCentrocosto() == null) {
                r.setCentrocosto("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && (r.getCentrocosto().equals(r1.getCentrocosto()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
//                r.setFecha(new Date());
                return true;
            }
        }
        getListaRas().add(r1);
        return false;
    }

    public String nuevo() {
        // grabar la depreciacion y los asientos
        try {
            if ((getListaRas() == null) || (getListaRas().isEmpty())) {
                MensajesErrores.advertencia("No existen cuentas para generar");
                return null;
            }
            int total = 0;
            for (Renglones r : getListaRas()) {
                Cuentas cta = cuentasBean.traerCodigo(r.getCuenta());
                if (cta == null) {
                    MensajesErrores.advertencia("No existe cuenta contable : " + r.getCuenta());
                    return null;
                }
//                total += (int) r.getValor().doubleValue() * 100;
                if (r.getValor() == null) {
                    r.setValor(BigDecimal.ZERO);
                }
                total += (r.getValor().doubleValue() * 100);
                MensajesErrores.advertencia("valor: " + r.getValor().doubleValue());
            }
            double cuadre = Math.round((total) * 100);
            double dividido = cuadre / 100;
            BigDecimal valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            if (valorBase.doubleValue() != 0) {
                MensajesErrores.advertencia("Asiento descuadrado no se puede grabar");
                return null;
            }

            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            ejbDepreciaciones.depreciar(depreciaciones, anioMes, seguridadbean.getLogueado().getUserid());
            Tipoasiento ta = configuracionBean.getConfiguracion().getTadepreciacion();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);

            ejbTipo.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras asiento = new Cabeceras();
            Calendar fechaAsiento = Calendar.getInstance();
            fechaAsiento.set(anio, mes, 1);
            fechaAsiento.add(Calendar.DATE, -1);
            asiento = new Cabeceras();
            asiento.setDescripcion("Asiento de depreciación de : " + anioMes);
//            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(ta);
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(fechaAsiento.getTime());
            asiento.setIdmodulo(0);
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("DEPRECIACION" + anioMes);
            ejbCas.create(asiento, seguridadbean.getLogueado().getUserid());
            for (Renglones r : getListaRas()) {
                r.setCabecera(asiento);
                r.setFecha(asiento.getFecha());
                ejbRas.create(r, seguridadbean.getLogueado().getUserid());
            }
            depreciaciones = new LinkedList<>();
            listaRas = new LinkedList<>();
            anio = ejbDepreciaciones.ultimoAnio();
            mes = ejbDepreciaciones.ultimoMes();
            MensajesErrores.advertencia("Depreciación creada con éxito : " + numeroAsiento);
        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DepreciacionActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "ActivosVista.jsf?faces-redirect=true";
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
     * @return the mes
     */
    public Integer getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(Integer mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * @return the depreciaciones
     */
    public List<Depreciaciones> getDepreciaciones() {
        return depreciaciones;
    }

    /**
     * @param depreciaciones the depreciaciones to set
     */
    public void setDepreciaciones(List<Depreciaciones> depreciaciones) {
        this.depreciaciones = depreciaciones;
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
     * @return the listaRas
     */
    public List<Renglones> getListaRas() {
        return listaRas;
    }

    /**
     * @param listaRas the listaRas to set
     */
    public void setListaRas(List<Renglones> listaRas) {
        this.listaRas = listaRas;
    }

}
