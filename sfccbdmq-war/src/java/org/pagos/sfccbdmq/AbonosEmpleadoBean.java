/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Amortizaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "abonosEmpleadoSfccbdmq")
@ViewScoped
public class AbonosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    private List<Prestamos> listaPrestamos;
    private List<Amortizaciones> listaAmortizaciones;
    private Prestamos prestamo;
    private double ingresos;
    private double egersos;
    private String periodo;
    private double valormaximo;
    private double valorAbono;
    private Kardexbanco kardex;
    private boolean uno;
    private int tiempo;
    private int tiempoCuotas;
    private Formulario formulario = new Formulario();
    private Formulario formularioAmortizacion = new Formulario();
    @EJB
    private PrestamosFacade ejbPrestamos;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private AmortizacionesFacade ejbAmmortizaciones;
    @EJB
    private KardexbancoFacade ejbkardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "AbonosEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
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
//               MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of PrestamosEmpleadoBean
     */
    public AbonosEmpleadoBean() {
    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado and o.pagado=true and o.aprobado=true and o.aprobadogarante=true";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificaTabla(Prestamos prestamo) {
        if (!prestamo.getAprobado()) {
            MensajesErrores.advertencia("Solo se abona préstamos aprobados");
            return null;
        }
        this.prestamo = prestamo;
        tiempoCuotas = 0;
        valorAbono = 0;
        kardex = new Kardexbanco();
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setFechamov(new Date());
        kardex.setOrigen("ABONOS EMPLEADOS");
//        kardex.setCodigospi("40101");
//        kardex.setBeneficiario(prestamo.getEmpleado().getEntidad().toString());
        // traer el banco de la transferencia
        kardex.setUsuariograba(parametrosSeguridad.getLogueado());
        formulario.insertar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.prestamo=:prestamo");
        parametros.put("prestamo", prestamo);
        parametros.put(";orden", "o.anio,o.mes");
        double valor2 = 0;
        try {
            listaAmortizaciones = ejbAmmortizaciones.encontarParametros(parametros);
            for (Amortizaciones a : listaAmortizaciones) {
                double valorNovedad = Math.round(a.getCuota().doubleValue() * 100);
//                double valorActual = valorNovedad / 100;
//                a.setCuota(new Float(valorActual));
                if ((a.getValorpagado() == null) || (a.getValorpagado() == 0)) {
                    valor2 += valorNovedad;
                    tiempoCuotas++;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        valor2 = valor2 / 100;

        valor2 = valor2 + prestamo.getValordiciembre();
        double cuadre = Math.round(valor2 * 100);
        double dividido = cuadre / 100;
        BigDecimal cuota = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valorAbono = cuota.doubleValue();
        return null;
    }

    public double valorPendiente(Prestamos p) {
        try {
            if (p != null) {
                double valorPendiente = 0;
                Map parametros = new HashMap();
                parametros.put(";where", "o.prestamo=:prestamo");
                parametros.put(";campo", "o.valorpagado");
                parametros.put("prestamo", p);
                double valorPagado = ejbAmmortizaciones.sumarCampoDoble(parametros);
                valorPendiente = p.getValorcontabilidad().doubleValue() - valorPagado;
                return valorPendiente;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String aprueba() {
//        this.prestamo = prestamo;
        // ver cuanto debe
        try {
            if (kardex.getBanco() == null) {
                MensajesErrores.advertencia("Favor seleccione el banco");
                return null;
            }
            if (kardex.getTipomov() == null) {
                MensajesErrores.advertencia("Favor seleccione el Tipo de movimiento");
                return null;
            }
//            if (kardex.getDocumento() == null) {
//                MensajesErrores.advertencia("Favor ingrese número de documento");
//                return null;
//            }
            if (kardex.getFechamov() == null) {
                MensajesErrores.advertencia("Favor ingrese fecha de movimiento");
                return null;
            }
            if (kardex.getFechamov().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser mayor o igual a periodo vigente");
                return null;
            }
            if (kardex.getFechamov().after(new Date())) {
                MensajesErrores.advertencia("Fecha de movimiento debe ser menor o igual a hoy");
                return null;
            }
            double valorDebe = 0;
            int coutasFaltantes = 0;
            for (Amortizaciones a : listaAmortizaciones) {
                if (((a.getValorpagado() == null) || (a.getValorpagado() == 0))) {
                    valorDebe += a.getCuota().doubleValue() * 100;
                    coutasFaltantes++;
                }
            }

            valorDebe = valorDebe / 100;
            valorDebe = valorDebe + prestamo.getValordiciembre();
            double cuadre = Math.round(valorDebe * 100);
            double dividido = cuadre / 100;
            BigDecimal cuota = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            valorDebe = cuota.doubleValue();

            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            double cero = Math.round(valorAbono - valorDebe);
            if (cero > 0) {
                MensajesErrores.advertencia("Esta tratando de pagar más de lo que debe : " + df.format(valorDebe / 100));
                return null;
            } else if (cero == 0) {
                coutasFaltantes = 0;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            Integer idPrestamo = prestamo.getId();
            prestamo.setPagado(false);
            prestamo.setFechapago(kardex.getFechamov());
            kardex.setValor(new BigDecimal(valorAbono));
            kardex.setEstado(2);
            kardex.setDocumento(prestamo.getEmpleado().getEntidad().getPin());
            kardex.setAbonoprestamo(prestamo);
            ejbkardex.create(kardex, parametrosSeguridad.getLogueado().getUserid());
            ejbPrestamos.edit(prestamo, parametrosSeguridad.getLogueado().getUserid());
            int i = 1;
            for (Amortizaciones a : listaAmortizaciones) {
                a.setValorpagado(a.getCuota().floatValue());
                ejbAmmortizaciones.edit(a, parametrosSeguridad.getLogueado().getUserid());
            }
            Calendar c = Calendar.getInstance();
            int mes = c.get(Calendar.MONTH) + 1;
            if (cero != 0) { // falta pagar
                if (coutasFaltantes > 0) {
//          hacer la nueva tabla con el mes actual
                    double valorPrestamoAcctual = valorDebe / 100 - valorAbono;
                    Prestamos p = new Prestamos();
                    p.setAprobado(true);
                    p.setAprobadopor(parametrosSeguridad.getLogueado().getEmpleados());
                    p.setFechaaprobacion(new Date());
                    p.setEmpleado(prestamo.getEmpleado());
                    p.setFechapago(null);
                    p.setFechasolicitud(prestamo.getFechasolicitud());
                    p.setNombre(prestamo.getNombre());
                    p.setObservaciones(prestamo.getObservaciones());
                    p.setPagado(true);
                    p.setKardexbanco(prestamo.getKardexbanco());
                    p.setTipo(prestamo.getTipo());
                    p.setCouta(new Float(coutasFaltantes));
                    p.setProveedor(prestamo.getProveedor());
                    p.setValor(new Float(valorPrestamoAcctual));
                    p.setCancelado(Boolean.FALSE);
                    ejbPrestamos.create(p, parametrosSeguridad.getLogueado().getUserid());
                    // generar la tabla de amortizacion
                    generarTabla(p);
                    for (Amortizaciones a : listaAmortizaciones) {
                        a.setValorpagado(new Float(0));
                        a.setPrestamo(p);
                        ejbAmmortizaciones.create(a, parametrosSeguridad.getLogueado().getUserid());
                    }

                }
            }
            // manda al kardex bancos como anticipo empleados a pagar
//            grabar el asiento
            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
            int numero = ta.getUltimo();
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setDescripcion(kardex.getObservaciones());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(kardex.getFechamov());
            cab.setTipo(kardex.getTipomov().getTipoasiento());
            cab.setNumero(numero);
            cab.setFecha(kardex.getFechamov());
            cab.setIdmodulo(idPrestamo);
            cab.setOpcion("ABONOSEMPLEADOS");
            cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
            List<Renglones> lista = getListarenglones();
            for (Renglones r : lista) {
                r.setCabecera(cab);
                r.setFecha(kardex.getFechamov());
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
            }
            periodo = ta.getNombre() + " No: " + cab.getId();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó corecctamete");
        formulario.cancelar();
        formularioAmortizacion.insertar();
        buscar();
        return null;
    }

    private void generarTabla(Prestamos p) {
        Calendar c = Calendar.getInstance();
        double rmu = empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
        // mes actual
        int mesActual = c.get(Calendar.MONTH) + 1;
        int anioActual = c.get(Calendar.YEAR);
        listaAmortizaciones = new LinkedList<>();
        int totalMeses = p.getCouta().intValue();
        double couta = p.getValor() / totalMeses;
        float porPagar = p.getValor();
        BigDecimal saldoTotal = new BigDecimal(porPagar);
        for (int i = 1; i <= totalMeses; i++) {
            Amortizaciones a = new Amortizaciones();
            if (mesActual == 12) {

                if (porPagar > rmu * 0.7) {
                    double resto = p.getValor() - porPagar;
                    a.setCuota(new BigDecimal(rmu * 0.7));
                    porPagar -= rmu * 0.7;
                    int coutasFaltantes = totalMeses - i;
                    if (coutasFaltantes > 0) {
                        couta = porPagar / coutasFaltantes;
                    }
                } else {
                    a.setCuota(saldoTotal);
                    // calcula la nueva couta
                    porPagar = 0;
                    // cuantos cuotas falfa
                    couta = 0;
                }

            } else {
                a.setCuota(new BigDecimal(couta));
                porPagar -= couta;

            }

            a.setAnio(anioActual);
            a.setMes(mesActual);
            a.setPrestamo(p);
            a.setNumero(i);
            a.setValorpagado(new Float(0));
            getListaAmortizaciones().add(a);
            mesActual++;
            if (mesActual == 13) {
                anioActual++;
                mesActual = 1;
            }
        }

        formularioAmortizacion.editar();
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the listaPrestamos
     */
    public List<Prestamos> getListaPrestamos() {
        return listaPrestamos;
    }

    /**
     * @param listaPrestamos the listaPrestamos to set
     */
    public void setListaPrestamos(List<Prestamos> listaPrestamos) {
        this.listaPrestamos = listaPrestamos;
    }

    /**
     * @return the prestamo
     */
    public Prestamos getPrestamo() {
        return prestamo;
    }

    /**
     * @param prestamo the prestamo to set
     */
    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
    }

    /**
     * @return the ingresos
     */
    public double getIngresos() {
        return ingresos;
    }

    /**
     * @param ingresos the ingresos to set
     */
    public void setIngresos(double ingresos) {
        this.ingresos = ingresos;
    }

    /**
     * @return the egersos
     */
    public double getEgersos() {
        return egersos;
    }

    /**
     * @param egersos the egersos to set
     */
    public void setEgersos(double egersos) {
        this.egersos = egersos;
    }

    /**
     * @return the valormaximo
     */
    public double getValormaximo() {
        return valormaximo;
    }

    /**
     * @param valormaximo the valormaximo to set
     */
    public void setValormaximo(double valormaximo) {
        this.valormaximo = valormaximo;
    }

    /**
     * @return the tiempo
     */
    public int getTiempo() {
        return tiempo;
    }

    /**
     * @param tiempo the tiempo to set
     */
    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the uno
     */
    public boolean isUno() {
        return uno;
    }

    /**
     * @param uno the uno to set
     */
    public void setUno(boolean uno) {
        this.uno = uno;
    }

    /**
     * @return the formularioAmortizacion
     */
    public Formulario getFormularioAmortizacion() {
        return formularioAmortizacion;
    }

    /**
     * @param formularioAmortizacion the formularioAmortizacion to set
     */
    public void setFormularioAmortizacion(Formulario formularioAmortizacion) {
        this.formularioAmortizacion = formularioAmortizacion;
    }

    /**
     * @return the listaAmortizaciones
     */
    public List<Amortizaciones> getListaAmortizaciones() {
        return listaAmortizaciones;
    }

    /**
     * @param listaAmortizaciones the listaAmortizaciones to set
     */
    public void setListaAmortizaciones(List<Amortizaciones> listaAmortizaciones) {
        this.listaAmortizaciones = listaAmortizaciones;
    }

    /**
     * @return the valorAbono
     */
    public double getValorAbono() {
        return valorAbono;
    }

    /**
     * @param valorAbono the valorAbono to set
     */
    public void setValorAbono(double valorAbono) {
        this.valorAbono = valorAbono;
    }

    /**
     * @return the tiempoCuotas
     */
    public int getTiempoCuotas() {
        return tiempoCuotas;
    }

    /**
     * @param tiempoCuotas the tiempoCuotas to set
     */
    public void setTiempoCuotas(int tiempoCuotas) {
        this.tiempoCuotas = tiempoCuotas;
    }

    /**
     * @return the kardex
     */
    public Kardexbanco getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexbanco kardex) {
        this.kardex = kardex;
    }

    public List<Renglones> getListarenglones() {
        if (kardex == null) {
            return null;
        }
        if (kardex.getBanco() == null) {
            return null;
        }
        if (prestamo == null) {
            return null;
        }
        if (valorAbono == 0) {
            return null;
        }
        List<Renglones> listaRetorno = new LinkedList<>();
        Renglones r = new Renglones();
        r.setCuenta(prestamo.getTipo().getParametros());
        r.setFecha(kardex.getFechamov());
        r.setReferencia(kardex.getObservaciones());
        r.setAuxiliar(prestamo.getEmpleado().getEntidad().getPin());
        r.setValor(new BigDecimal(valorAbono * -1));
        listaRetorno.add(r);
        r = new Renglones();
        r.setCuenta(kardex.getBanco().getCuenta());
        r.setFecha(kardex.getFechamov());
        r.setReferencia(kardex.getObservaciones());
        r.setAuxiliar(prestamo.getEmpleado().getEntidad().getPin());
        r.setValor(new BigDecimal(valorAbono));
        listaRetorno.add(r);
        // lo nuevo también va
        return listaRetorno;
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(kardex.getValor().doubleValue());
    }
}
