/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.beans.sfccbdmq.NovedadesxempleadoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.compras.sfccbdmq.ReporteContratosBean;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "cesarFuncionesEmpleadoSfccbdmq")
@ViewScoped
public class CesarFuncionesEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Historialcargos> listaHistorial;
//    private Historialcargos cargo;
    private Historialcargos cargoAnterior;
    private Formulario formulario = new Formulario();
    private List<Auxiliar> listaAcumuladosIngresos;
    private List<Pagosempleados> listaIngresos;
    private List<Auxiliar> listaAcumuladosEgresos;
    private List<Pagosempleados> listaEgresos;
    private List<Pagosempleados> listaExtra;
    @EJB
    private AmortizacionesFacade ejbPrestamos;
    private double valorPrestamos;
    private double rmu;
    private double valorIngresos;
    private double valorEgresos;
    private double multas;
    private Date fecha = new Date();
    private Resource recurso;
    @EJB
    private HistorialcargosFacade ejbHistorialcargos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private HistorialsancionesFacade ejbSanciones;
    @EJB
    private ConceptosFacade ejbCon;
    @EJB
    private NovedadesxempleadoFacade ejbNovedadesxEmpleado;
    @EJB
    private ConceptosFacade ejbConceptos;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private FormatosFacade ejbFormatos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private VacacionesFacade ejbVacaciones;

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

        String nombreForma = "CesarFuncionesEmpleadosVista";
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
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of HistorialcargosEmpleadoBean
     */
    public CesarFuncionesEmpleadoBean() {
    }

    private void anterior() {
        for (Historialcargos h : listaHistorial) {
            if (h.getCargo().equals(empleadoBean.getEmpleadoSeleccionado().getCargoactual())) {
                cargoAnterior = h;
                return;
            }
        }
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        if (empleadoBean.getEmpleadoSeleccionado().getCargoactual() == null) {
            MensajesErrores.advertencia("No existe cargo actual en registro");
            return null;
        }
        fecha = empleadoBean.getEmpleadoSeleccionado().getFechasalida();
        // validar la fecha
        if (fecha == null) {
            MensajesErrores.advertencia("Ingrese la fecha de cese, en la opción de mantenimiento de empleados");
            return null;
        }
        Calendar inicioMes = Calendar.getInstance();
        Calendar finMes = Calendar.getInstance();
        inicioMes.set(inicioMes.get(Calendar.YEAR), inicioMes.get(Calendar.MONTH), 1);
        finMes.set(inicioMes.get(Calendar.YEAR), inicioMes.get(Calendar.MONTH) + 1, 1);
        finMes.add(Calendar.DATE, -1);
        Calendar fechaCese = Calendar.getInstance();
        fechaCese.setTime(fecha);
        int anioCese = fechaCese.get(Calendar.YEAR);
        int mesCese = fechaCese.get(Calendar.MONTH) + 1;
        int anioMes = inicioMes.get(Calendar.YEAR);
        int mesMes = inicioMes.get(Calendar.MONTH) + 1;
        int diaMes = fechaCese.get(Calendar.DATE);
        if (diaMes == 31) {
            diaMes = 30;
        }

        cargoAnterior = null;
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("CESAR");
        imagenesBean.Buscar();
        DecimalFormat formatoMes = new DecimalFormat("00");
        DecimalFormat formatoDias = new DecimalFormat("0.00");
        valorIngresos = 0;
        valorEgresos = 0;
        listaEgresos = new LinkedList<>();
        listaIngresos = new LinkedList<>();
        listaExtra = new LinkedList<>();
        int dias = 30;
        if ((empleadoBean.getEmpleadoSeleccionado().getTipocontrato().getRegimen().equals("CODTRAB"))) {
            dias = finMes.get(Calendar.DATE) - fechaCese.get(Calendar.DATE);
        }
        try {

            Map parametros = new HashMap();
            String where = "o.empleado=:empleado and o.compromiso is not null and o.liquidacion=true";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            if (ejbPagos.contar(parametros) > 0) {
                MensajesErrores.advertencia("Liquidación ya generado el compromiso");
                return null;
            }
            parametros = new HashMap();
            where = "o.empleado=:empleado ";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");

            listaHistorial = ejbHistorialcargos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }

            anterior();
            String partida = empleadoBean.getEmpleadoSeleccionado().getPartida().substring(0, 2);
            String cuenta = empleadoBean.getEmpleadoSeleccionado().getPartida().substring(2, 6);
            double sueldo = empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
            rmu = sueldo;
//            if (cargoAnterior == null) {
            cargoAnterior = new Historialcargos();
            cargoAnterior.setCargo(empleadoBean.getEmpleadoSeleccionado().getCargoactual());
            cargoAnterior.setDesde(empleadoBean.getEmpleadoSeleccionado().getFechaingreso());
            cargoAnterior.setSueldobase(empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo().getEscalasalarial().getSueldobase());
            cargoAnterior.setMotivo("Cese de Funciones");
            cargoAnterior.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
            cargoAnterior.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            cargoAnterior.setFecha(fecha);
            cargoAnterior.setVigente(Boolean.TRUE);
            cargoAnterior.setAprobacion(Boolean.TRUE);
            cargoAnterior.setActivo(Boolean.FALSE);
            // colocar el rmu de los 6 días
            Pagosempleados p = new Pagosempleados();

            parametros = new HashMap();
            parametros.put(";where", "o.concepto.provision=true "
                    + " and o.concepto.vacaciones=false "
                    + " and o.concepto.sobre=false "
                    + " and o.concepto.liquidacion=false"
                    + " and o.concepto.ingreso=true "
                    + " and o.concepto.proveedor is null  "
                    + " and o.kardexbanco  is null and o.empleado=:empleado");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";orden", "o.concepto.id,o.anio,o.mes");

            listaIngresos = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p1 : listaIngresos) {
                p1.setTituloTemporal("" + formatoMes.format(p1.getMes()) + "/"
                        + formatoMes.format(p1.getAnio()));
                valorIngresos += p1.getValor().doubleValue();
            }

            parametros = new HashMap();
            parametros.put(";where", "o.concepto.provision=true "
                    + " and o.concepto.vacaciones=false "
                    + " and o.concepto.sobre=false "
                    + " and o.concepto.liquidacion=false"
                    + " and o.concepto.ingreso=false "
                    + " and o.concepto.proveedor is null  "
                    //                    + " and o.pagado=true"
                    + " and o.kardexbanco  is null and o.empleado=:empleado");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";orden", "o.concepto.id,o.anio,o.mes");

            listaEgresos = ejbPagos.encontarParametros(parametros);
            for (Pagosempleados p1 : listaEgresos) {
                p1.setTituloTemporal("" + formatoMes.format(p1.getMes()) + "/"
                        + formatoMes.format(p1.getAnio()));
                valorEgresos += p1.getValor().doubleValue();
            }

            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put(";suma", "sum((o.ganado+o.ganadofs)-(o.utilizado+o.utilizadofs)),"
                    + "max(o.anio),min(o.anio),max(o.mes),min(o.mes)");
            parametros.put(";campo", "o.empleado");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            double vac = 0;
            List<Object[]> lista = ejbVacaciones.sumar(parametros);
            String campoTitulo = "";
            double diasvac = 0;
            for (Object[] o : lista) {
                Long x = (Long) o[1];
                if (x == null) {
                    x = 0l;
                }
//                String valStr = String.valueOf(x / (60 * 60 * 8));
                vac += (double) x / (60 * 8);

                Integer anioMaximo = (Integer) o[2];
                Integer anioMinimo = (Integer) o[3];
                Integer mesMaximo = (Integer) o[4];
                Integer mesMinimo = (Integer) o[5];

                // los del mes son 
                Double segundos = Constantes.MILLSECS_PER_DAY * .6;
                Double segundosFs = Constantes.MILLSECS_PER_DAY * .4;

                if ((empleadoBean.getEmpleadoSeleccionado().getTipocontrato().getRegimen().equals("CODTRAB"))) {
                    vac += diaMes * 1.25 / 30;
                } else {
                    vac += diaMes * 2.5 / 30;
                }
                diasvac = vac;
                if (vac > 60) {
                    vac = 60;
                }
                // ver el número de días
                campoTitulo = "Desde : " + formatoMes.format(anioMinimo) + "/"
                        + formatoMes.format(mesMinimo) + " Hasta : "
                        + formatoMes.format(anioMaximo) + "/"
                        + formatoMes.format(mesMaximo) + " + "
                        + " Ganados en Mes de Cese :" + formatoMes.format(mesCese) + "/"
                        + formatoMes.format(anioCese) + " No días : " + formatoDias.format(vac);
            }

//            sueldo = (double)Math.round(sueldo / 30*100)/100;
            sueldo = sueldo / 30;
//            vac=(double)Math.round(vac *100)/100;
            vac = vac * sueldo;
//            vac =(double) Math.round(vac * sueldo*100)/100;

            valorIngresos += vac;
            // traer el concepto de vacaiones
            where = "o.ingreso=true "
                    + " and o.liquidacion=false "
                    + " and o.vacaciones=true "
                    + " and o.activo=true";
//                    + " and o.cargo=:cargo";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.orden");
//            parametros.put("cargo", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo());
            List<Conceptos> listaConceptos = ejbConceptos.encontarParametros(parametros);
            Conceptos concepto = null;
            for (Conceptos c : listaConceptos) {
                concepto = c;
            }
            // borrar el concepto de vacaciones
            
            if (concepto == null) {
                MensajesErrores.advertencia("No existe concepto para registrar vacaciones");
                return null;
            }
            p = new Pagosempleados();
            p.setAnio(anioCese);
            p.setMes(mesCese);
            p.setValor(new Float(vac));
            p.setCantidad(new Float(diasvac));
            p.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
            p.setCantidad(new Float(0));
            p.setEncargo(new Float(0));
            p.setConcepto(concepto);
            p.setTituloTemporal(campoTitulo);
            p.setLiquidacion(Boolean.TRUE);
            ejbPagos.ponerCuentasPago(concepto, p, ctaInicio, ctaFin, partida);
//            p.setCuentaporpagar(ctaInicio + partida + ctaFin);
            listaIngresos.add(p);
            // calcular los liquidacion
            where = ""
                    + " o.liquidacion=true "
                    + " and o.vacaciones=false "
                    + " and o.activo=true";
//                    + " and o.cargo=:cargo";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.orden");
//            parametros.put("cargo", empleadoBean.getEmpleadoSeleccionado().getCargoactual().getCargo());
            listaConceptos = ejbConceptos.encontarParametros(parametros);
            //*************************Ingresos Normales *******************************
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Conceptos c : listaConceptos) {
                // si es porcentaje
                double valor = ejbPagos.calculaConcepto(rmu, c, 0,
                        empleadoBean.getEmpleadoSeleccionado(),
                        dias, (Constantes.DIAS_POR_MES - dias), rmu,
                        finMes.getTime());
                p = new Pagosempleados();
                p.setAnio(anioCese);
                p.setMes(mesCese);
                p.setValor(new Float(valor));
                p.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
                p.setCantidad(new Float(0));
                p.setEncargo(new Float(0));
                p.setConcepto(c);
                p.setLiquidacion(Boolean.TRUE);
                ejbPagos.ponerCuentasPago(c, p, ctaInicio, ctaFin, partida);
                if (c.getIngreso()) {
                    if (valor > 0) {
                        listaIngresos.add(p);
                        valorIngresos += valor;
                    }
                } else {
                    if (valor > 0) {
                        listaEgresos.add(p);
                        valorEgresos += valor;
                    }
                }
            }
            // prestamos
            where = "o.prestamo.empleado=:empleado "
                    + " and o.prestamo.pagado=true and o.prestamo.aprobado=true"
                    + "  and (o.valorpagado is null or o.valorpagado=0) ";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";campo", "o.cuota");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            valorPrestamos = ejbPrestamos.sumarCampoDoble(parametros);
            valorEgresos += valorPrestamos;
            where = "o.empleado=:empleado  and o.faplicacion is null"
                    + " and o.especunaria=true";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";campo", "o.valor");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            multas = ejbSanciones.sumarCampo(parametros).doubleValue();
            valorEgresos += multas;
            reporte();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CesarFuncionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().insertar();
        return null;
    }

    public void reporte() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            List<String> lista = new LinkedList<>();
            lista.add("Código : " + empleadoBean.getEmpleadoSeleccionado().getCodigo());
            lista.add("Nombre : " + empleadoBean.getEmpleadoSeleccionado().toString());
            lista.add("C.I. : " + empleadoBean.getEmpleadoSeleccionado().getEntidad().getPin());
            DocumentoPDF pdf = new DocumentoPDF("Liquidación del Empleado", lista, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Cargo Anterior : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargoAnterior.getCargo().getOrganigrama().getNombre()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Fecha de Cesasión:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,sdf.format(fecha)));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"RMU"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,df.format(rmu)));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total Ingresos:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,df.format(getValorIngresos())));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total Anticipos:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,df.format(getValorPrestamos())));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total Multas:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,df.format(getMultas())));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total Egresos:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,df.format(getValorEgresos())));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"A pagar:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,df.format(getValorIngresos() - getValorEgresos())));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Período"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor"));
            columnas = new LinkedList<>();
//            valorModificaciones=0;
            for (Pagosempleados p : listaIngresos) {
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, p.getConcepto().getNombre()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, p.getTituloTemporal()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, p.getValor().doubleValue()));
//                valorModificaciones+=m.getValor().doubleValue();
            }
            pdf.agregarTablaReporte(titulos, columnas, 3, 100, "INGRESOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "Período"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor"));
            columnas = new LinkedList<>();
//            valorModificaciones=0;
            for (Pagosempleados p : listaEgresos) {
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, p.getConcepto().getNombre()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, p.getTituloTemporal()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, p.getValor().doubleValue()));
//                valorModificaciones+=m.getValor().doubleValue();
            }
            pdf.agregarTablaReporte(titulos, columnas, 3, 100, "EGRESOS");
            recurso = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double traerRmu(Empleados e, int anio, int mes) {
        double retorno = e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().floatValue();
        int dias = Constantes.DIAS_POR_MES;
        double sueldoDiario = retorno / dias;
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.set(Calendar.MINUTE, 0);
        inicioMes.set(Calendar.YEAR, anio);
        inicioMes.set(Calendar.MONTH, mes - 1);
        inicioMes.set(Calendar.HOUR_OF_DAY, 0);
        inicioMes.set(Calendar.DATE, 1);
        Calendar finMes = Calendar.getInstance();
        finMes.set(Calendar.MINUTE, 0);
        finMes.set(Calendar.YEAR, anio);
        finMes.set(Calendar.MONTH, mes);
        finMes.set(Calendar.HOUR_OF_DAY, 0);
        finMes.set(Calendar.DATE, 1);
        finMes.add(Calendar.DATE, -1);
        String where = " o.empleado=:empleado and o.hasta between :desde and :hasta and o.aprobacion=true";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put(";orden", "o.desde desc");
        parametros.put("empleado", e);
        parametros.put("desde", inicioMes.getTime());
        parametros.put("hasta", finMes.getTime());
        try {
            List<Historialcargos> lHistorial = ejbHistorialcargos.encontarParametros(parametros);
            for (Historialcargos h : lHistorial) {
                int diasAnteriores = (int) Math.rint((h.getHasta().getTime() - inicioMes.getTimeInMillis()) / Constantes.MILLSECS_PER_DAY) + 1;
                int diasSiguientes = 30 - diasAnteriores;
                double sueldoAnterio = h.getCargo().getCargo().getEscalasalarial().getSueldobase().doubleValue() / Constantes.DIAS_POR_MES;;
                sueldoAnterio = Math.rint(sueldoAnterio * 100) / 100;
                retorno = sueldoAnterio * diasAnteriores + sueldoDiario * diasSiguientes;
//                retorno=Math.rint(retorno*100)/100;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        retorno = Math.rint(retorno * 100) / 100;
        return retorno;
    }

    public boolean validar() {

        if ((empleadoBean.getEmpleadoSeleccionado().getTipocontrato() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido un tipo de contrato");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getFechaingreso() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido una fecha de ingreso");
            return true;
        }
        if (fecha == null) {
            MensajesErrores.advertencia("Ingrese una fecha de cesación");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        Calendar inicioMes = Calendar.getInstance();
        Calendar finMes = Calendar.getInstance();
        inicioMes.set(inicioMes.get(Calendar.YEAR), inicioMes.get(Calendar.MONTH), 1);
        finMes.set(inicioMes.get(Calendar.YEAR), inicioMes.get(Calendar.MONTH) + 1, 1);
        finMes.add(Calendar.DATE, -1);
        Calendar fechaCese = Calendar.getInstance();
        fechaCese.setTime(fecha);
        int anioCese = fechaCese.get(Calendar.YEAR);
        int mesCese = fechaCese.get(Calendar.MONTH);
        int anioMes = inicioMes.get(Calendar.YEAR);
        int mesMes = inicioMes.get(Calendar.MONTH);
        int diaMes = fechaCese.get(Calendar.DATE);
        if (diaMes == 31) {
            diaMes = 30;
        }
        try {
            // borramos las vacaciones antes de nada
            Map parametros = new HashMap();
            parametros.put(";where", ""
                    + " (o.concepto.vacaciones=true or o.concepto.liquidacion=true)"
                    + " and o.kardexbanco  is null "
                    + " and o.empleado=:empleado");
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";orden", "o.concepto.id,o.anio,o.mes");

            List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
            
            for (Pagosempleados p : lista) {
                ejbPagos.remove(p, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Pagosempleados p : listaIngresos) {

                if (p.getAnio() != null) {
//                    Map parametros = new HashMap();
//                    parametros.put(";where", "o.empleado=:empleado and o.mes=:mes "
//                            + " and o.anio=:anio and o.concepto=:concepto");
//                    parametros.put("anio", anioCese);
//                    parametros.put("mes", mesCese);
//                    parametros.put("empleado", p.getEmpleado());
//                    parametros.put("concepto", p.getConcepto());
//                    List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
//                    Pagosempleados p2 = null;
//                    for (Pagosempleados p1 : lista) {
//                        p2 = p1;
//                    }
                    p.setLiquidacion(Boolean.TRUE);
                    if (p.getId() == null) {
//                        p.setLiquidacion(Boolean.TRUE);
                        ejbPagos.create(p, parametrosSeguridad.getLogueado().getUserid());
                    } else {
//                        p2.setAnio(p.getAnio());
//                        p2.setMes(p.getMes());
//                        p2.setValor(p.getValor());
//                        p2.setEmpleado(p.getEmpleado());
//                        p2.setCantidad(p.getCantidad());
//                        p2.setEncargo(p.getEncargo());
//                        p2.setConcepto(p.getConcepto());
//                        p2.setClasificador(p.getClasificador());
//                        p2.setCuentaporpagar(p.getCuentaporpagar());
//                        p2.setCuentagasto(p.getCuentagasto());
                        ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                    }

                }
            }
            for (Pagosempleados p : listaEgresos) {

                if (p.getAnio() != null) {
//                    Map parametros = new HashMap();
//                    parametros.put(";where", "o.empleado=:empleado and o.mes=:mes "
//                            + " and o.anio=:anio and o.concepto=:concepto");
//                    parametros.put("anio", anioCese);
//                    parametros.put("mes", mesCese);
//                    parametros.put("empleado", p.getEmpleado());
//                    parametros.put("concepto", p.getConcepto());
//                    List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
//                    Pagosempleados p2 = null;
//                    for (Pagosempleados p1 : lista) {
//                        p2 = p1;
//                    }
                    p.setLiquidacion(Boolean.TRUE);
                    if (p.getId() == null) {
//                        p.setLiquidacion(Boolean.TRUE);
                        ejbPagos.create(p, parametrosSeguridad.getLogueado().getUserid());
                    } else {
//                        p2.setAnio(p.getAnio());
//                        p2.setMes(p.getMes());
//                        p2.setValor(p.getValor());
//                        p2.setEmpleado(p.getEmpleado());
//                        p2.setCantidad(p.getCantidad());
//                        p2.setEncargo(p.getEncargo());
//                        p2.setConcepto(p.getConcepto());
//                        p2.setClasificador(p.getClasificador());
//                        p2.setCuentaporpagar(p.getCuentaporpagar());
//                        p2.setCuentagasto(p.getCuentagasto());
                        ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                    }

                }
            }
            for (Pagosempleados p : listaExtra) {
                p.setLiquidacion(Boolean.TRUE);
                if (p.getAnio() != null) {
//                    Map parametros = new HashMap();
//                    parametros.put(";where", "o.empleado=:empleado and o.mes=:mes "
//                            + " and o.anio=:anio and o.concepto=:concepto");
//                    parametros.put("anio", anioCese);
//                    parametros.put("mes", mesCese);
//                    parametros.put("empleado", p.getEmpleado());
//                    parametros.put("concepto", p.getConcepto());
//                    List<Pagosempleados> lista = ejbPagos.encontarParametros(parametros);
//                    Pagosempleados p2 = null;
//                    for (Pagosempleados p1 : lista) {
//                        p2 = p1;
//                    }
                    if (p.getId() == null) {

                        ejbPagos.create(p, parametrosSeguridad.getLogueado().getUserid());
                    } else {
//                        p2.setAnio(p.getAnio());
//                        p2.setMes(p.getMes());
//                        p2.setValor(p.getValor());
//                        p2.setEmpleado(p.getEmpleado());
//                        p2.setCantidad(p.getCantidad());
//                        p2.setEncargo(p.getEncargo());
//                        p2.setConcepto(p.getConcepto());
//                        p2.setClasificador(p.getClasificador());
//                        p2.setCuentaporpagar(p.getCuentaporpagar());
//                        p2.setCuentagasto(p.getCuentagasto());
//                        p.setLiquidacion(Boolean.TRUE);
                        ejbPagos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                    }

                }
            }

//        } catch (InsertarException | GrabarException | ConsultarException ex) {
        } catch (InsertarException | GrabarException | ConsultarException | BorrarException ex) {
            Logger.getLogger(CesarFuncionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        buscar();
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
        listaHistorial = null;
        cargoAnterior = null;
        getFormulario().cancelar();
        return null;
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
     * @return the listaHistorial
     */
    public List<Historialcargos> getListaHistorial() {
        return listaHistorial;
    }

    /**
     * @param listaHistorial the listaHistorial to set
     */
    public void setListaHistorial(List<Historialcargos> listaHistorial) {
        this.listaHistorial = listaHistorial;
    }

    /**
     * @return the cargoAnterior
     */
    public Historialcargos getCargoAnterior() {
        return cargoAnterior;
    }

    /**
     * @param cargoAnterior the cargoAnterior to set
     */
    public void setCargoAnterior(Historialcargos cargoAnterior) {
        this.cargoAnterior = cargoAnterior;
    }

    /**
     * @return the listaAcumuladosIngresos
     */
    public List<Auxiliar> getListaAcumuladosIngresos() {
        return listaAcumuladosIngresos;
    }

    /**
     * @param listaAcumuladosIngresos the listaAcumuladosIngresos to set
     */
    public void setListaAcumuladosIngresos(List<Auxiliar> listaAcumuladosIngresos) {
        this.listaAcumuladosIngresos = listaAcumuladosIngresos;
    }

    /**
     * @return the listaAcumuladosEgresos
     */
    public List<Auxiliar> getListaAcumuladosEgresos() {
        return listaAcumuladosEgresos;
    }

    /**
     * @param listaAcumuladosEgresos the listaAcumuladosEgresos to set
     */
    public void setListaAcumuladosEgresos(List<Auxiliar> listaAcumuladosEgresos) {
        this.listaAcumuladosEgresos = listaAcumuladosEgresos;
    }

    /**
     * @return the valorPrestamos
     */
    public double getValorPrestamos() {
        return valorPrestamos;
    }

    /**
     * @param valorPrestamos the valorPrestamos to set
     */
    public void setValorPrestamos(double valorPrestamos) {
        this.valorPrestamos = valorPrestamos;
    }

    /**
     * @return the valorIngresos
     */
    public double getValorIngresos() {
        return valorIngresos;
    }

    /**
     * @param valorIngresos the valorIngresos to set
     */
    public void setValorIngresos(double valorIngresos) {
        this.valorIngresos = valorIngresos;
    }

    /**
     * @return the valorEgresos
     */
    public double getValorEgresos() {
        return valorEgresos;
    }

    /**
     * @param valorEgresos the valorEgresos to set
     */
    public void setValorEgresos(double valorEgresos) {
        this.valorEgresos = valorEgresos;
    }

    /**
     * @return the multas
     */
    public double getMultas() {
        return multas;
    }

    /**
     * @param multas the multas to set
     */
    public void setMultas(double multas) {
        this.multas = multas;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the rmu
     */
    public double getRmu() {
        return rmu;
    }

    /**
     * @param rmu the rmu to set
     */
    public void setRmu(double rmu) {
        this.rmu = rmu;
    }

    /**
     * @return the listaIngresos
     */
    public List<Pagosempleados> getListaIngresos() {
        return listaIngresos;
    }

    /**
     * @param listaIngresos the listaIngresos to set
     */
    public void setListaIngresos(List<Pagosempleados> listaIngresos) {
        this.listaIngresos = listaIngresos;
    }

    /**
     * @return the listaEgresos
     */
    public List<Pagosempleados> getListaEgresos() {
        return listaEgresos;
    }

    /**
     * @param listaEgresos the listaEgresos to set
     */
    public void setListaEgresos(List<Pagosempleados> listaEgresos) {
        this.listaEgresos = listaEgresos;
    }

    /**
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
    }
}
