/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfccbdmq.rest;

import com.sfccbdmq.restauxiliares.AuxiliarRetorno;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.ConfiguracionFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.entidades.sfccbdmq.Tipopermisos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;

/**
 * REST Web Service
 *
 * @author edwin
 */
@Stateless(name = "pedirVacaciones")
@Path("pedirVacacionesRest")
public class PedirVacaciones {

    @EJB
    private TipopermisosFacade ejbTipos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private VacacionesFacade ejbVacaciones;
    @EJB
    private LicenciasFacade ejbLicencias;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private ConfiguracionFacade ejbConfiguracion;
    @Context
    private UriInfo context;
    private Integer vacionesGanadas;
    private Integer vacionesUsadas;

    /**
     * Creates a new instance of TiposVacacioness
     */
    public PedirVacaciones() {
    }

    @GET
    @Path("{pedirVacaciones}")
//    @Produces(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public Response getPedirVacaciones(@PathParam("pedirVacaciones") String parametro) throws ConsultarException {
        String[] aux = parametro.split("_");
        int diaInicio = Integer.parseInt(aux[0]);
        int mesInicio = Integer.parseInt(aux[1]);
        int anioInicio = Integer.parseInt(aux[2]);
        int diaFin = Integer.parseInt(aux[3]);
        int mesFin = Integer.parseInt(aux[4]);
        int anioFin = Integer.parseInt(aux[5]);
        int tipo = Integer.parseInt(aux[6]);
        String cedula = aux[7];
        String observaciones = aux[8];
        // traer empleado
        Empleados empleado = traer(cedula);
        Tipopermisos tipoVacaciones = ejbTipos.find(tipo);
        Licencias licencia = new Licencias();
        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        Calendar desde = Calendar.getInstance();
        Calendar hasta = Calendar.getInstance();
        inicio.set(anioInicio, mesInicio - 1, diaInicio);
        desde.set(anioInicio, mesInicio - 1, diaInicio);
        hasta.set(anioInicio, mesInicio - 1, diaInicio);
        fin.set(anioFin, mesFin - 1, diaFin);
        licencia.setFin(fin.getTime());
        licencia.setFechaingreso(new Date());
        licencia.setInicio(inicio.getTime());
        licencia.setDesde(desde.getTime());
        licencia.setHasta(hasta.getTime());
        licencia.setEmpleado(empleado);
        licencia.setSolicitud(new Date());
        licencia.setObservaciones(observaciones);
        licencia.setTipo(tipoVacaciones);
        diasVacaciones(licencia);
        String retorno = validar(licencia);
        if (retorno == null) {
            try {
                ejbLicencias.create(licencia, empleado.getEntidad().getUserid());
                retorno = "OK";
                Codigos textoCodigo = traerCodigo("MAIL", "SOLICITUD");
                if (textoCodigo == null) {
                    retorno = "No configurado texto para email en codigos";
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                    String texto = textoCodigo.getParametros().replace("#empleado#", licencia.getEmpleado().getEntidad().toString());
                    texto = texto.replace("#autoriza#", licencia.getAutoriza().toString());
                    String aprobadoStr = "";
                    String correos = licencia.getAutoriza().getEntidad().getEmail()
                            + (licencia.getValida() == null ? "" : "," + licencia.getValida().getEntidad().toString());
                    texto = texto.replace("#autorizado#", aprobadoStr);
                    texto = texto.replace("#tipo#", licencia.getTipo().getNombre());
                    texto = texto.replace("#solicitado#", licencia.getEmpleado().getEntidad().toString());
                    texto = texto.replace("#fecha#", sdf.format(licencia.getFechaingreso()));
                    texto = texto.replace("#observaciones#", licencia.getObservaciones());
                    if (licencia.getTipo().getHoras()) {
                        texto = texto.replace("#periodo#", " con desde : "
                                + sdf.format(licencia.getInicio()) 
                                + " hasta :" + sdf.format(licencia.getFin()) + " de "
                                + sdf1.format(licencia.getDesde()) + " a "
                                + sdf1.format(licencia.getHasta()));
                    } else {
                        texto = texto.replace("#periodo#", " desde :" + sdf.format(licencia.getInicio()) + " hasta :" + sdf.format(licencia.getFin()));
                    }
                    if (licencia.getTipo().getAdjuntos()) {
                        texto += "<p><strong>Favor adjuntar respaldos para justificar su ausencia, los documentos físicos entregar en la dirección de TH Gracias</strong><p>";
                    }
                    ejbCorreos.enviarCorreo(correos,
                            textoCodigo.getDescripcion(), texto);
                }
            } catch (InsertarException | MessagingException | UnsupportedEncodingException ex) {
//            } catch (InsertarException  ex) {
                retorno = ex.getMessage();
            }
        }
        List<AuxiliarRetorno> lista=new LinkedList<>();
        AuxiliarRetorno auxRet=new AuxiliarRetorno();
        auxRet.setOk(retorno);
        lista.add(auxRet);
        GenericEntity< List<AuxiliarRetorno>> entity;
        entity = new GenericEntity<List<AuxiliarRetorno>>(lista) {
        };
        return Response.ok(entity).build();

    }

    public Empleados traer(String cedula) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.pin=:pin");
        parametros.put("pin", cedula);
        try {
            List<Empleados> el = ejbEmpleados.encontarParametros(parametros);
            if ((el == null) || (el.isEmpty())) {
                return null;
            }

            return el.get(0);
        } catch (ConsultarException ex) {
            Logger.getLogger("errroror").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String validar(Licencias licencia) {
        licencia.setAutoriza(getJefe(licencia));

        if (licencia.getAutoriza() == null) {
            return "No existe información de jefe superior";
        }

        if (licencia.getInicio() == null) {
            return "Ingrese fecha de inicio";
        }
        if (!licencia.getTipo().getHoras()) {
            if (licencia.getFin() != null) {
                if (licencia.getFin().before(licencia.getInicio())) {
                    return "Rango de fechas erróneas";
                }
            }
        }
        if (licencia.getTipo().getRecursivo() != null) {
            if (licencia.getTipo().getRecursivo()) {
                if (licencia.getFin() != null) {
                    if (licencia.getFin().before(licencia.getInicio())) {
                        return "Rango de fechas erróneas";
                    }
                }
            }
        }
        if (licencia.getTipo() == null) {
            return "Ingrese tipo de permiso";
        }
        int tipo = licencia.getTipo().getTipo();
        if ((tipo == 1) || (tipo == 0)) {
            if ((licencia.getObservaciones() == null) || (licencia.getObservaciones().isEmpty())) {
                return "Ingrese una Observación";
            }
            if (licencia.getTipo().getHoras()) {
//                es horas
                if (licencia.getInicio() == null) {
                    return "Ingrese hora de inicio";
                }
                int horasVacaciones = licencia.getHoras();
                int maximoHoras = 100000;
                if (licencia.getTipo().getDuracion() != null) {
                    maximoHoras = licencia.getTipo().getDuracion();
                }
                if (horasVacaciones > maximoHoras) {
                    return "Tiempo excede los " + licencia.getTipo().getDuracion() + " minutos";
                }
                if (licencia.getFin() == null) {
                    licencia.setFin(licencia.getInicio());
                }

                // var saldo de vaciones
                if (licencia.getTipo().getCargovacaciones()) {
                    if (licencia.getEmpleado().getOperativo()) {
                        int minutos = getSaldoVaciones(licencia) * 24 * 60;
                        if (minutos < licencia.getHoras()) {
                            return "No puede solicitar más de su saldo de vacaciones";
                        }
                    } else {
                        int minutos = getSaldoVaciones(licencia) * 8 * 60;
                        if (minutos < licencia.getHoras()) {
                            return "No puede solicitar más de su saldo de vacaciones";
                        }
                    }
                }
            } else {
//                es rango de fechas
                if (licencia.getInicio() == null) {
                    return "Ingrese hora de inicio";
                }
                if (licencia.getFin() == null) {
                    return "Ingrese hora de inicio";
                }

                if (licencia.getTipo().getDuracion() != null) {
                    int duracion = licencia.getTipo().getDuracion();
                    if (licencia.getDias() > duracion) {
                        return "La duración en días no puede ser mayor a la duración configurada para este tipo de permisos";
                    }
                }
                if (licencia.getTipo().getCargovacaciones()) {
                    // es en dias
                    if (getSaldoVaciones(licencia) < licencia.getDias()) {
                        return "No puede solicitar más de su saldo de vacaciones";
                    }
                }
            }
        } else if (tipo == 2) {
            // Vacaciones
//            ver el numero de dias

            if ((licencia.getDias() == null) || (licencia.getDias() <= 0)) {
                return "Ingrese la duración en días";
            }
            int saldovacaciones = getSaldoVaciones(licencia);
            if (saldovacaciones < licencia.getDias()) {
                return "No puede solicitar más de su saldo";
            }
            Calendar c = Calendar.getInstance();
            c.setTime(licencia.getInicio());
            c.add(Calendar.DATE, licencia.getDias());
//            licencia.setFin(c.getTime());
        }

//         ver si es necesario ya cumplio el máximo de tiempo de los permisos
        if (licencia.getTipo().getMaximo() != null) {
//            vemos si esta bien configurado
            if (licencia.getTipo().getPeriodo() == null) {
                return "Mal configurado tipo de permiso, no exsite información sobre el periodo en meses";
            }
            Map parametros = new HashMap();
            Calendar cDesde = Calendar.getInstance();
            cDesde.add(Calendar.MONTH, -licencia.getTipo().getPeriodo());
            if (licencia.getId() == null) {
                parametros.put(";where", "o.inicio<=:desde and o.empleado=:empleado and o.tipo=:tipo");
                parametros.put("desde", cDesde.getTime());
                parametros.put("empleado", licencia.getEmpleado());
                parametros.put("tipo", licencia.getTipo());
            } else {
                parametros.put(";where", "o.inicio<=:desde and o.empleado=:empleado and o.tipo=:tipo and o.id!=:id");
                parametros.put("desde", cDesde.getTime());
                parametros.put("empleado", licencia.getEmpleado());
                parametros.put("tipo", licencia.getTipo());
                parametros.put("id", licencia.getId());
            }
            List<Licencias> lista;
            try {
                lista = ejbLicencias.encontarParametros(parametros);
                Integer cuanto = 0;
                for (Licencias l : lista) {
                    if ((l.getDesde() == null) && ((l.getHasta() == null))) {
                        // es rango de fechas
                        cuanto += (Integer.parseInt(String.valueOf(l.getFin().getTime() - l.getInicio().getTime()))) / (1000 * 60 * 60 * 24);
                    } else {
                        // solo rango de horas
                        cuanto += (Integer.parseInt(String.valueOf(l.getHasta().getTime() - l.getDesde().getTime()))) / (1000 * 60);
                    }
                }
                if (cuanto > licencia.getTipo().getMaximo()) {
                    return "Valor solicitado sobrepasa el máximo en periodo";
                }
            } catch (ConsultarException ex) {
                return ex.getMessage();
            }

        }
        if (licencia.getDesde() == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(licencia.getDesde());
            c.add(Calendar.DATE, licencia.getTipo().getJustificacion() == null ? 0 : licencia.getTipo().getJustificacion());
            licencia.setFechamaximalegalizacion(c.getTime());
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(licencia.getDesde());
            c.add(Calendar.DATE, licencia.getTipo().getJustificacion() == null ? 0 : licencia.getTipo().getJustificacion());
            licencia.setFechamaximalegalizacion(c.getTime());
        }
        return null;
    }

    public Empleados getJefe(Licencias licencia) {
        if (licencia == null) {
            return null;
        }
        if (licencia.getEmpleado() == null) {
            return null;
        }
        if (licencia.getEmpleado().getOperativo() == null) {
            licencia.getEmpleado().setOperativo(Boolean.FALSE);
        }
        if (licencia.getEmpleado().getOperativo()) {
            // ver si es 4 horas
            if (licencia.getTipo() == null) {
                try {
                    return ejbEmpleados.traerJefePeloton(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();

                } catch (ConsultarException ex) {
                    return null;
                }
            }
            if (licencia.getTipo().getHoras() == null) {
                try {
                    return ejbEmpleados.traerJefePeloton(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();

                } catch (ConsultarException ex) {
                    return null;
                }
            }
            if (licencia.getTipo().getHoras()) {
                // hasta 4 jefe de peloton
                int cuantos = (int) ((licencia.getHasta().getTime() - licencia.getDesde().getTime()) / (1000 * 60));
                if (cuantos < 0) {
                    cuantos = cuantos * 24 * 60 * -1;
                }
                if (cuantos > 240) {
                    try {
                        Entidades entidad = ejbEmpleados.traerJefeEstacion(licencia.getEmpleado().getEntidad().getPin());
                        if (entidad == null) {
                            // es operativo pero el jefe es administrativo
                            Cargosxorganigrama cxoJefe = licencia.getEmpleado().getEntidad().getEmpleados().getCargoactual().getReporta();
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                            parametros.put("cargo", cxoJefe);
                            parametros.put(";orden", "o.entidad.apellidos");
                            List<Empleados> listaEmpleados = null;
                            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                            Empleados empleado = null;
                            for (Empleados x : listaEmpleados) {
                                if (x.getJefeproceso() == null) {
                                    x.setJefeproceso(Boolean.FALSE);
                                }
                                if (x.getJefeproceso()) {
                                    return x;
                                }
                            }
                            if (empleado == null) {
                                for (Empleados x : listaEmpleados) {
                                    return x;
                                }
                            } else {
                                return empleado;
                            }
                            // Linea final
                        } else {
                            Empleados e = entidad.getEmpleados();
                            // ya esta
                            if (e == null) {
                                return null;
                            }
                            if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
                                return ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                            }
                            return e;

                        }
                    } catch (ConsultarException ex) {
                        return null;
                    }
                } else {
                    try {
                        Empleados e = ejbEmpleados.traerJefePeloton(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                        if (e == null) {
                            return null;
                        }
                        if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
                            e = ejbEmpleados.traerJefeEstacion(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                            // ya esta
                            if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
                                e = ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
                            }
                        }
                        return e;
                        // traer jefe de esacion si es el mismo

                    } catch (ConsultarException ex) {
                        return null;
                    }
                }
            } else {
                if (licencia.getTipo().getTipo() == 2) {
                    try {
                        Entidades entidad = ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin());
//                        Entidades entidad = ejbEmpleados.traerJefeEstacion(licencia.getEmpleado().getEntidad().getPin());
                        if (entidad == null) {
                            // es operativo pero el jefe es administrativo
                            Cargosxorganigrama cxoJefe = licencia.getEmpleado().getEntidad().getEmpleados().getCargoactual().getReporta();
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.cargoactual=:cargo and o.activo=true");
                            parametros.put("cargo", cxoJefe);
                            parametros.put(";orden", "o.entidad.apellidos");
                            List<Empleados> listaEmpleados = null;
                            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                            Empleados empleado = null;
                            for (Empleados x : listaEmpleados) {
                                if (x.getJefeproceso() == null) {
                                    x.setJefeproceso(Boolean.FALSE);
                                }
                                if (x.getJefeproceso()) {
                                    return x;
                                }
                            }
                            if (empleado == null) {
                                for (Empleados x : listaEmpleados) {
                                    return x;
                                }
                            } else {
                                return empleado;
                            }
                            // Linea final
//                        } else {
//                            Empleados e = entidad.getEmpleados();
//                            // ya esta
//                            if (e == null) {
//                                return null;
//                            }
//                            if (e.getEntidad().getPin().equals(licencia.getEmpleado().getEntidad().getPin())) {
//                                return ejbEmpleados.traerJefeDistrito(licencia.getEmpleado().getEntidad().getPin()).getEmpleados();
//                            }
//                            return e;
                        } else {
                            return entidad.getEmpleados();

                        }
                    } catch (ConsultarException ex) {
                        return null;
                    }
                }
            }
        } else {
            // ver si esta de vacaciones

            Cargosxorganigrama cxoJefe = licencia.getEmpleado().getCargoactual().getReporta();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargoactual=:cargo and o.activo=true ");
            parametros.put("cargo", cxoJefe);
            parametros.put(";orden", "o.entidad.apellidos");
            List<Empleados> listaEmpleados = null;
            try {
                listaEmpleados = ejbEmpleados.encontarParametros(parametros);
                Empleados empleado = null;
                for (Empleados x : listaEmpleados) {
                    if (x.getJefeproceso() == null) {
                        x.setJefeproceso(Boolean.FALSE);
                    }
                    if (x.getJefeproceso()) {
                        return x;
                    }
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.aprovado=true  "
//                            + " and o.autoriza is not null"
//                            + " and o.fretorno is  null "
//                            + " and o.empleado=:empleado");
//                    parametros.put("empleado", x);
//                    int total= ejbLicencias.contar(parametros);
//                    if (total==0){
//                        return x;
//                    }
                }
                if (empleado == null) {
                    for (Empleados x : listaEmpleados) {
                        return x;
                    }
                } else {
                    return empleado;
                }
            } catch (ConsultarException ex) {
                return null;
            }
        }
        return null;
    }

    public Integer getSaldoVaciones(Licencias licencia) {
        Integer saldoVaciones = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";suma", "sum(o.utilizado),sum(o.utilizadofs),sum(o.ganado),sum(o.ganadofs)");
        parametros.put("empleado", licencia.getEmpleado());
        try {
            List<Object[]> listaObjetos = ejbVacaciones.sumar(parametros);
            for (Object[] o : listaObjetos) {
                Long valor = (Long) o[2];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesGanadas = valor.intValue();
                valor = (Long) o[3];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesGanadas += valor.intValue();
                valor = (Long) o[0];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesUsadas = valor.intValue();
                valor = (Long) o[1];
                if (valor == null) {
                    valor = 0l;
                }
                vacionesUsadas += valor.intValue();
//                if (licencia.getEmpleado().getOperativo()) {
                saldoVaciones = (vacionesGanadas - vacionesUsadas) / (60 * 8);//86400
//                } else {
//                    saldoVaciones = (vacionesGanadas - vacionesUsadas) / (60 * 24);//86400
//                }
            }
        } catch (ConsultarException ex) {
            return null;
        }
        return saldoVaciones;
    }

    public double diasVacaciones(Licencias licencia) {
        List<Configuracion> lc;
        Configuracion configuracion=new Configuracion();
        try {
            lc = ejbConfiguracion.findAll();
            for (Configuracion c : lc) {
                configuracion=c;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PedirPermiso.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (licencia == null) {
            return 0;
        }
        if (licencia.getInicio() == null) {
            return 0;
        }
        if (licencia.getFin() == null) {
            return 0;
        }
        long retorno = ((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
        BigDecimal factorEncontrado = ejbLicencias.factor(licencia,configuracion.getPinicial(),configuracion.getPfinal());
        double factor = 0;
        if (factorEncontrado != null) {
            factor = factorEncontrado.doubleValue();
        }
//        String strRetorno = String.valueOf(retorno + factor - 1);
        licencia.setDias((int) (retorno));
        return retorno;
    }

    public Codigos traerCodigo(String maestro, String codigo) {
        try {
            return ejbCodigos.traerCodigo(maestro, codigo);
        } catch (ConsultarException ex) {
            return null;
        }
    }
}
