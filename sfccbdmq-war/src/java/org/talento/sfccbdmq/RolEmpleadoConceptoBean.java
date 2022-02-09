/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarCabeceraEmpleados;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.BeneficiariossupaFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "rolEmpleadosConceptoSfccbdmq")
@ViewScoped
public class RolEmpleadoConceptoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private List<Pagosempleados> listaPagos;
    private List<Conceptos> listaConceptosIngresos;
    private List<Conceptos> listaConceptosEgresos;
    private List<Empleados> listaEmpleados;
    private List<AuxiliarCabeceraEmpleados> lista14 = new LinkedList<>();
    private List<AuxiliarCabeceraEmpleados> lista13 = new LinkedList<>();
    private int mes;
    private int anio;
    private Perfiles perfil;
    private Resource reporte;
    private Codigos subrogacionCodigos;
    private Date desde;
    private Date hasta;
    private Conceptos concepto;
    private String rangoFechas = "";
    private Cabecerasrrhh cabeceraRrHh;
    private double total;
    private double total1;
    private double total2;
    private double total3;

    @EJB
    private HistorialcargosFacade ejbHistorial;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private BeneficiariossupaFacade ejbBeneficiariossupa;

    public RolEmpleadoConceptoBean() {
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = c.get((Calendar.YEAR));
        mes = c.get((Calendar.MONTH)) + 1;
        c.set(Calendar.MONTH, mes);
        c.add(Calendar.DATE, -1);
        desde = configuracionBean.getConfiguracion().getPvigente();
        hasta = c.getTime();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        perfil = parametrosSeguridad.traerPerfil(perfilString);
    }

    public String buscar() {
        try {
            if (concepto == null) {
                MensajesErrores.advertencia("Seleccione un concepto");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.concepto=:concepto"
                    + " and o.pagado=true");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            parametros.put("concepto", concepto);
            int contar = ejbPagos.contar(parametros);
            if (contar > 0) {
                MensajesErrores.advertencia("Mes ya generado");
                return null;
            }
            lista14 = new LinkedList<>();
            lista13 = new LinkedList<>();
            rangoFechas = "";
            if (concepto.getCodigo().equals("D14A")) {
                Calendar inicio = Calendar.getInstance();
                Calendar fin = Calendar.getInstance();
                inicio.set(anio - 1, mes, 1);
                fin.set(anio, mes, 1);
                fin.add(Calendar.DATE, -1);
                desde = inicio.getTime();
                hasta = fin.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                rangoFechas = ("Desde: " + sdf.format(desde) + " Hasta: " + sdf.format(hasta));
            }

            if (concepto.getCodigo().equals("D13A")) {
                desde = configuracionBean.getConfiguracion().getPinicial();
                hasta = configuracionBean.getConfiguracion().getPfinal();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                rangoFechas = ("Desde: " + sdf.format(desde) + " Hasta: " + sdf.format(hasta));
            }

            parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.fechasalida is null and o.entidad.activo=true");
            parametros.put(";orden", "o.entidad.apellidos,o.entidad.nombres");
            List<Empleados> listaE = ejbEmpleados.encontarParametros(parametros);

            //Lista de Empledos Extra para pagar el decimo
            Codigos cE = codigosBean.traerCodigo("DECEXT", "DECEXT");
            if (cE != null) {
                if (cE.getParametros() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.entidad.pin in (" + cE.getParametros() + ")");
                    parametros.put(";orden", "o.entidad.apellidos");
                    List<Empleados> listaEEx = ejbEmpleados.encontarParametros(parametros);
                    listaE.addAll(listaEEx);
                }
            }
            total = 0;
            total1 = 0;
            total2 = 0;
            total3 = 0;
            listaEmpleados = new LinkedList<>();
            for (Empleados e : listaE) {
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.pagado=false and o.fechapago is null and o.compromiso is null"
                        + " and o.empleado=:empleado and o.concepto=:concepto");
                parametros.put("empleado", e);
                parametros.put("concepto", concepto);
                double valor1 = ejbPagos.sumarCampoDoble(parametros);
                double valorDic = 0;
                if (e.getTipocontrato().getRegimen().equals("CODTRAB")) {
                    parametros = new HashMap();
                    parametros.put(";campo", "o.valor");
                    parametros.put(";where", "o.pagado=false and o.fechapago is null and o.compromiso is null"
                            + " and o.empleado=:empleado and o.concepto=:concepto and o.anio=:anio and o.mes=12");
                    parametros.put("empleado", e);
                    parametros.put("anio", anio);
                    parametros.put("concepto", concepto);
                    valorDic = ejbPagos.sumarCampoDoble(parametros);
                }
                valor1 = valor1 - valorDic;
                double cuadre = Math.round(valor1 * 100);
                double dividido = cuadre / 100;
                BigDecimal valorb = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                if (valorb.doubleValue() != 0) {
                    double diferencia = 0;
                    if (concepto.getCodigo().equals("D14A")) {
                        AuxiliarCabeceraEmpleados aux = new AuxiliarCabeceraEmpleados();
                        aux.setCedula(e.getEntidad().getPin());
                        aux.setNombre(e.toString());
                        aux.setVivienda(traerValorMes(e, 8, anio - 1));
                        aux.setSalud(traerValorMes(e, 9, anio - 1));
                        aux.setEducacion(traerValorMes(e, 10, anio - 1));
                        aux.setAlimentacion(traerValorMes(e, 11, anio - 1));
                        aux.setVestimenta(traerValorMes(e, 12, anio - 1));
                        aux.setArteCultura(traerValorMes(e, 1, anio));
                        aux.setDiscapacidad(traerValorMes(e, 2, anio));
                        aux.setTerceraEdad(traerValorMes(e, 3, anio));
                        aux.setIngresosGrabados(traerValorMes(e, 4, anio));
                        aux.setRebajas(traerValorMes(e, 5, anio));
                        aux.setRetenido(traerValorMes(e, 6, anio));
                        aux.setExtra(traerValorMes(e, 7, anio));
                        aux.setTotal(valorb.doubleValue());
                        lista14.add(aux);
                    }
                    if (concepto.getCodigo().equals("D13A")) {
                        AuxiliarCabeceraEmpleados aux = new AuxiliarCabeceraEmpleados();
                        aux.setCedula(e.getEntidad().getPin());
                        aux.setNombre(e.toString());
                        aux.setArteCultura(traerValorMes(e, 1, anio));
                        aux.setDiscapacidad(traerValorMes(e, 2, anio));
                        aux.setTerceraEdad(traerValorMes(e, 3, anio));
                        aux.setIngresosGrabados(traerValorMes(e, 4, anio));
                        aux.setRebajas(traerValorMes(e, 5, anio));
                        aux.setRetenido(traerValorMes(e, 6, anio));
                        aux.setExtra(traerValorMes(e, 7, anio));
                        aux.setVivienda(traerValorMes(e, 8, anio));
                        aux.setSalud(traerValorMes(e, 9, anio));
                        aux.setEducacion(traerValorMes(e, 10, anio));
                        aux.setAlimentacion(traerValorMes(e, 11, anio));
                        //Calculo de redondeo
                        double diferencia1 = calculoDiferencia(e, valorb.doubleValue());
                        //Sumar el ajuste por titilo del concepto
                        double diferencia2 = traerAjuste(e, valorb.doubleValue());
                        diferencia = diferencia1 + diferencia2;
                        if (diferencia1 < 0.10 || diferencia1 > -0.10) {
                            aux.setVestimenta(traerValorMesDiciembre(e, 12, anio) + diferencia1);
                            aux.setTotal(valorb.doubleValue() + diferencia1);
                        } else {
                            aux.setVestimenta(traerValorMesDiciembre(e, 12, anio));;
                            aux.setTotal(valorb.doubleValue());
                        }

                        lista13.add(aux);
                    }
                    Empleados e1;
                    e1 = e;
                    e1.setTotalIngresos(valorb.doubleValue() + diferencia);
                    e1.setEncargoTemporal(traerValorBeneficiarios(e));//Beneficiarios
                    if (concepto.getCodigo().equals("D13A")) {

                        e1.setSubrogacionTemporal(traerValorDiciembre(e));//Prestamo
                    } else {
                        e1.setSubrogacionTemporal(0);
                    }
                    e1.setRmu(traerDescuentos(e));//Descuentos
                    e1.setTotalEgresos(e1.getEncargoTemporal() + e1.getSubrogacionTemporal() + e1.getRmu());
                    e1.setTotalPagar(e1.getTotalIngresos() - e1.getTotalEgresos());
                    total += valorb.doubleValue() + diferencia;
                    total1 += e.getEncargoTemporal();
                    total2 += e.getSubrogacionTemporal();
                    total3 += e.getRmu();
                    listaEmpleados.add(e1);
                }
            }
            Empleados e1 = new Empleados();
            Entidades en = new Entidades();
            en.setApellidos("Total");
            en.setNombres("");
            e1.setEntidad(en);
            e1.setTotalIngresos(total);
            e1.setEncargoTemporal(total1);//Beneficiarios
            e1.setSubrogacionTemporal(total2);//Prestamo
            e1.setRmu(total3);//Descuentos
            e1.setTotalEgresos(total1 + total2 + total3);
            e1.setTotalPagar(total - e1.getTotalEgresos());
            listaEmpleados.add(e1);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public double calculoDiferencia(Empleados e, double valorb) {
        try {
            if (e.getFechasalida() == null) {
                if (e.getTipocontrato() != null) {
                    if (e.getTipocontrato().getRegimen() != null) {

                        Map parametros = new HashMap();
                        parametros.put(";where", "o.empleado=:empleado and o.tipoacciones.codigo in ('SUROGACION','ENCARGO')"
                                + " and (o.desde between :desde1 and :hasta1 or o.hasta between :desde1 and :hasta1)");
                        parametros.put("empleado", e);
                        parametros.put("desde1", configuracionBean.getConfiguracion().getPinicial());
                        parametros.put("hasta1", configuracionBean.getConfiguracion().getPfinal());
                        List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);
                        if (lista.isEmpty()) {
                            parametros = new HashMap();
                            parametros.put(";where", "o.id=:empleado and o.fechaingreso between :desde and :hasta");
                            parametros.put("empleado", e.getId());
                            parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
                            parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
                            List<Empleados> listaEm = ejbEmpleados.encontarParametros(parametros);
                            if (listaEm.isEmpty()) {
                                double valorDif1 = e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
                                double diferencia = valorDif1 - valorb;
                                if (diferencia == 0) {
                                    return 0;
                                }
                                if (diferencia > 0.10) {
                                    return 0;
                                }
                                if (diferencia < -0.10) {
                                    return 0;
                                }
                                System.out.println("Diferencia: " + diferencia + " Empleado: " + e.toString());
                                if (!e.getTipocontrato().getRegimen().equals("CODTRAB")) {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.pagado=false and o.fechapago is null and o.compromiso is null"
                                            + " and o.empleado=:empleado and o.concepto=:concepto and o.anio=:anio");
                                    parametros.put("empleado", e);
                                    parametros.put("anio", anio);
                                    parametros.put("concepto", concepto);
                                    parametros.put(";orden", "o.id desc");
                                    List<Pagosempleados> listaP = ejbPagos.encontarParametros(parametros);
                                    Pagosempleados p = listaP.get(0);
                                    double valorNuevo = p.getValor().doubleValue() + diferencia;
                                    p.setValor(new Float(valorNuevo));
                                    ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                                } else {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.pagado=false and o.fechapago is null and o.compromiso is null"
                                            + " and o.empleado=:empleado and o.concepto=:concepto and o.anio=:anio and o.mes!=12");
                                    parametros.put("empleado", e);
                                    parametros.put("anio", anio);
                                    parametros.put("concepto", concepto);
                                    parametros.put(";orden", "o.id desc");
                                    List<Pagosempleados> listaP = ejbPagos.encontarParametros(parametros);
                                    Pagosempleados p = listaP.get(0);
                                    double valorNuevo = p.getValor().doubleValue() + diferencia;
                                    p.setValor(new Float(valorNuevo));
                                    ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                                }
                                return diferencia;
                            }
                        }
                    }
                }
            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerAjuste(Empleados e, double valorb) {
        try {
            double diferencia;
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.empleado=:empleado and o.concepto.titulo=:titulo and o.anio=:anio and o.concepto.codigo!=:codigo");
            parametros.put("empleado", e);
            parametros.put("titulo", concepto.getTitulo());
            parametros.put("anio", anio);
            parametros.put("codigo", concepto.getCodigo());
            diferencia = ejbPagos.sumarCampoDoble(parametros);
            return diferencia;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerValorBeneficiarios(Empleados e) {
        try {
            double diferencia;
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.empleado=:empleado and o.conceptoextra=:conceptoextra and o.anio=:anio");
            parametros.put("empleado", e);
            parametros.put("conceptoextra", concepto);
            parametros.put("anio", anio);
            diferencia = ejbBeneficiariossupa.sumarCampo(parametros).doubleValue();
            return diferencia;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerValorDiciembre(Empleados e) {
        try {
            double diferencia = 0;
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valordiciembre");
            parametros.put(";where", "o.empleado=:empleado and o.kardexbanco is not null and o.anio=:anio"
                    + " and o.fechasolicitud between :desde and :hasta and o.cancelado=false");
            parametros.put("empleado", e);
            parametros.put("anio", anio);
            parametros.put("desde", configuracionBean.getConfiguracion().getPinicial());
            parametros.put("hasta", configuracionBean.getConfiguracion().getPfinal());
            diferencia = ejbPrestamos.sumarCampoDoble(parametros);
            return diferencia;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerDescuentos(Empleados e) {
        try {
            double diferencia;
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.empleado=:empleado and o.concepto.codigo='FONDREC' and o.anio=:anio and o.mes=:mes");
            parametros.put("empleado", e);
            parametros.put("anio", anio);
            parametros.put("mes", mes);
            diferencia = ejbPagos.sumarCampoDoble(parametros);
            return diferencia;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String exportar() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoXLS xls = new DocumentoXLS("Rol de Pagos Concepto");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", "CÉDULA"));
            campos.add(new AuxiliarReporte("String", "EMPLEADO"));
            campos.add(new AuxiliarReporte("String", "FEHCA INGRESO"));
            campos.add(new AuxiliarReporte("String", concepto.getNombre()));
            xls.agregarFila(campos, true);
            for (Empleados e : listaEmpleados) {
                if (e.getId() != null) {
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", e.getEntidad().getPin()));
                    campos.add(new AuxiliarReporte("String", e.getEntidad().toString()));
                    campos.add(new AuxiliarReporte("String", sdf.format(e.getFechaingreso())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getTotalIngresos())));
                    xls.agregarFila(campos, false);
                } else {
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", e.getEntidad().toString()));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", df.format(e.getTotalIngresos())));
                    xls.agregarFila(campos, false);
                }
            }
            reporte = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String grabar() {
        try {
            listaPagos = new LinkedList<>();
            for (Empleados e : listaEmpleados) {
                if (e != null) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.empleado=:empleado  and o.concepto=:concepto");
                    parametros.put("empleado", e);
                    parametros.put("concepto", concepto);
                    listaPagos = ejbPagos.encontarParametros(parametros);
                    if (e.getTipocontrato() != null) {
                        if (e.getTipocontrato().getRegimen() != null) {
                            if (e.getTipocontrato().getRegimen().equals("CODTRAB")) {
                                parametros = new HashMap();
                                parametros.put(";where", "o.empleado=:empleado  and o.concepto=:concepto and o.anio=:anio and o.mes=12");
                                parametros.put("empleado", e);
                                parametros.put("anio", anio);
                                parametros.put("concepto", concepto);
                                List<Pagosempleados> listaPagosAd = ejbPagos.encontarParametros(parametros);
                                if (!listaPagosAd.isEmpty()) {
                                    listaPagos.remove(listaPagosAd.get(0));
                                }
                            }
                        }
                    }
                    //Agrgar a la lista de pagos los ajustes
                    parametros = new HashMap();
                    parametros.put(";where", "o.empleado=:empleado and o.concepto.titulo=:titulo and o.anio=:anio and o.concepto.codigo!=:codigo");
                    parametros.put("empleado", e);
                    parametros.put("titulo", concepto.getTitulo());
                    parametros.put("anio", anio);
                    parametros.put("codigo", concepto.getCodigo());
                    List<Pagosempleados> listaPagosAd = ejbPagos.encontarParametros(parametros);
                    if (!listaPagosAd.isEmpty()) {
                        listaPagos.add(listaPagosAd.get(0));
                    }
                    for (Pagosempleados pe : listaPagos) {
                        String partida1 = e.getPartida().substring(0, 2);
                        String partida2 = concepto.getPartida();
                        if (concepto.getPartida().length() == 6) {
                            pe.setClasificador(partida2);
                        } else {
                            pe.setClasificador(partida1 + partida2);
                        }
                        ejbPagos.edit(pe, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Nómina preliminar grabada correctamente");
        formulario.cancelar();
        return null;
    }

    public double traerValorMes(Empleados e, int mesp, int aniop) {

        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.pagado=false and o.fechapago is null and o.compromiso is null"
                    + " and o.empleado=:empleado and o.concepto=:concepto and o.anio=:anio and o.mes=:mes");
            parametros.put("empleado", e);
            parametros.put("anio", aniop);
            parametros.put("mes", mesp);
            parametros.put("concepto", concepto);
            return ejbPagos.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerValorMesDiciembre(Empleados e, int mesp, int aniop) {

        try {
            if (e.getTipocontrato().getRegimen().equals("CODTRAB")) {
                aniop = aniop - 1;
            }
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.pagado=false and o.fechapago is null and o.compromiso is null"
                    + " and o.empleado=:empleado and o.concepto=:concepto and o.anio=:anio and o.mes=:mes");
            parametros.put("empleado", e);
            parametros.put("anio", aniop);
            parametros.put("mes", mesp);
            parametros.put("concepto", concepto);
            return ejbPagos.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the parametrosSeguridad
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
     * @return the listaPagos
     */
    public List<Pagosempleados> getListaPagos() {
        return listaPagos;
    }

    /**
     * @param listaPagos the listaPagos to set
     */
    public void setListaPagos(List<Pagosempleados> listaPagos) {
        this.listaPagos = listaPagos;
    }

    /**
     * @return the listaConceptosIngresos
     */
    public List<Conceptos> getListaConceptosIngresos() {
        return listaConceptosIngresos;
    }

    /**
     * @param listaConceptosIngresos the listaConceptosIngresos to set
     */
    public void setListaConceptosIngresos(List<Conceptos> listaConceptosIngresos) {
        this.listaConceptosIngresos = listaConceptosIngresos;
    }

    /**
     * @return the listaConceptosEgresos
     */
    public List<Conceptos> getListaConceptosEgresos() {
        return listaConceptosEgresos;
    }

    /**
     * @param listaConceptosEgresos the listaConceptosEgresos to set
     */
    public void setListaConceptosEgresos(List<Conceptos> listaConceptosEgresos) {
        this.listaConceptosEgresos = listaConceptosEgresos;
    }

    /**
     * @return the listaEmpleados
     */
    public List<Empleados> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<Empleados> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
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
     * @return the subrogacionCodigos
     */
    public Codigos getSubrogacionCodigos() {
        return subrogacionCodigos;
    }

    /**
     * @param subrogacionCodigos the subrogacionCodigos to set
     */
    public void setSubrogacionCodigos(Codigos subrogacionCodigos) {
        this.subrogacionCodigos = subrogacionCodigos;
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

    /**
     * @return the rangoFechas
     */
    public String getRangoFechas() {
        return rangoFechas;
    }

    /**
     * @param rangoFechas the rangoFechas to set
     */
    public void setRangoFechas(String rangoFechas) {
        this.rangoFechas = rangoFechas;
    }

    /**
     * @return the cabeceraRrHh
     */
    public Cabecerasrrhh getCabeceraRrHh() {
        return cabeceraRrHh;
    }

    /**
     * @param cabeceraRrHh the cabeceraRrHh to set
     */
    public void setCabeceraRrHh(Cabecerasrrhh cabeceraRrHh) {
        this.cabeceraRrHh = cabeceraRrHh;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * @return the lista14
     */
    public List<AuxiliarCabeceraEmpleados> getLista14() {
        return lista14;
    }

    /**
     * @param lista14 the lista14 to set
     */
    public void setLista14(List<AuxiliarCabeceraEmpleados> lista14) {
        this.lista14 = lista14;
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
     * @return the lista13
     */
    public List<AuxiliarCabeceraEmpleados> getLista13() {
        return lista13;
    }

    /**
     * @param lista13 the lista13 to set
     */
    public void setLista13(List<AuxiliarCabeceraEmpleados> lista13) {
        this.lista13 = lista13;
    }

    /**
     * @return the total1
     */
    public double getTotal1() {
        return total1;
    }

    /**
     * @param total1 the total1 to set
     */
    public void setTotal1(double total1) {
        this.total1 = total1;
    }

    /**
     * @return the total2
     */
    public double getTotal2() {
        return total2;
    }

    /**
     * @param total2 the total2 to set
     */
    public void setTotal2(double total2) {
        this.total2 = total2;
    }

    /**
     * @return the total3
     */
    public double getTotal3() {
        return total3;
    }

    /**
     * @param total3 the total3 to set
     */
    public void setTotal3(double total3) {
        this.total3 = total3;
    }
}
