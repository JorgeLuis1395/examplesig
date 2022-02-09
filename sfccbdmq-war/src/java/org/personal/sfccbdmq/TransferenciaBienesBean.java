/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.ConstatacionesFacade;
import org.beans.sfccbdmq.CustodiosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.OficinasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.SolicitudesFacade;
import org.beans.sfccbdmq.TransferenciasFacade;
import org.entidades.sfccbdmq.Constataciones;
import org.entidades.sfccbdmq.Custodios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Solicitudes;
import org.entidades.sfccbdmq.Transferencias;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "tranferenciaBienes")
@ViewScoped
public class TransferenciaBienesBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;

    private Solicitudes solicitud;
    private Transferencias tranferencia;
    private Empleados empleado;
    private List<Constataciones> listaConstataciones;
    private List<Solicitudes> listaSolicitudes;
    private Resource reporte;
    private DocumentoPDF pdf;
    private Recurso reportepdf;
    private Formulario formulario = new Formulario();
    private Formulario formularioActa = new Formulario();
    private Formulario formularioDocumento = new Formulario();
    private Boolean esAdministrativo;

    @EJB
    private SolicitudesFacade ejbSolicitudes;
    @EJB
    private TransferenciasFacade ejbTransferencias;
    @EJB
    private ConstatacionesFacade ejbConstataciones;
    @EJB
    private CustodiosFacade ejbCustodios;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private OficinasFacade ejbOficinas;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    public TransferenciaBienesBean() {

    }

    @PostConstruct
    private void activar() {
//        empleado = seguridadbean.getLogueado().getEmpleados();
        esAdministrativo = ejbCustodios.esAdministrativo(seguridadbean.getLogueado().getPin());
        if (!esAdministrativo) {
            empleado = seguridadbean.getLogueado().getEmpleados();
        }
    }

    public String buscar() {
        if (empleado == null) {
            MensajesErrores.advertencia("Seleccione un custodio de bienes.");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.fechasolicitud is not null and o.usuariosolicitante=:usuariosolicitante");
        parametros.put("usuariosolicitante", empleado.getEntidad().getPin());
        parametros.put(";orden", "o.id desc");

        try {
            listaSolicitudes = ejbSolicitudes.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (!esAdministrativo) {
            MensajesErrores.error("Opción sólo para custodios administrativos");
            return null;
        }
        if (empleado == null) {
            MensajesErrores.advertencia("Seleccione un custodio de bienes.");
            return null;
        }
        solicitud = new Solicitudes();
        solicitud.setFechasolicitud(new Date());
        solicitud.setUsuariosolicitante(empleado.getEntidad().getPin());
        solicitud.setEstado(0);
        Map parametros = new HashMap();
        parametros.put(";where", "o.cicustodio=:cicustodio");
        parametros.put("cicustodio", empleado.getEntidad().getPin());
        parametros.put(";orden", "o.id desc");
        try {
            listaConstataciones = ejbConstataciones.encontarParametros(parametros);

            for (Constataciones c : listaConstataciones) {
                Oficinas o = c.getUbicacion() != null ? ejbOficinas.find(c.getUbicacion()) : null;
                c.setNombreOficina(o != null ? o.getNombre() : "");
                c.setNombreEdificio(o != null ? o.getEdificio().getNombre() : "");
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        empleadoBean.setEntidad(null);
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos("");
        formulario.insertar();
        return null;
    }

    public String modificar(Solicitudes s) {
        if (!esAdministrativo) {
            MensajesErrores.advertencia("Opción sólo para custodios administrativos");
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.cicustodio=:cicustodio");
        parametros.put("cicustodio", empleado.getEntidad().getPin());
        parametros.put(";orden", "o.id desc");
        try {
            listaConstataciones = ejbConstataciones.encontarParametros(parametros);
            for (Constataciones c : listaConstataciones) {
                parametros = new HashMap();
                parametros.put(";where", "o.bien=:bien and o.solicitud=:solicitud");
                parametros.put("bien", c);
                parametros.put("solicitud", s);
                if (ejbTransferencias.contar(parametros) > 0) {
                    c.setSeleccionado(Boolean.TRUE);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String eliminar(Solicitudes s) {
        solicitud = s;
        if (!esAdministrativo) {
            MensajesErrores.advertencia("Opción sólo para custodios administrativos");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cicustodio=:cicustodio");
        parametros.put("cicustodio", empleado.getEntidad().getPin());
        parametros.put(";orden", "o.id desc");
        try {
            listaConstataciones = ejbConstataciones.encontarParametros(parametros);
            for (Constataciones c : listaConstataciones) {
                parametros = new HashMap();
                parametros.put(";where", "o.bien=:bien and o.solicitud=:solicitud");
                parametros.put("bien", c);
                parametros.put("solicitud", s);
                if (ejbTransferencias.contar(parametros) > 0) {
                    c.setSeleccionado(Boolean.TRUE);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if (!solicitud.getBodega()) {
            if (empleadoBean.getEmpleadoSeleccionado() == null) {
                MensajesErrores.advertencia("Ingrese Ususario a Transferir");
                return true;
            }
        }
        boolean seleccion = false;
        for (Constataciones c : listaConstataciones) {
            if (c.getSeleccionado()) {
                seleccion = true;
            }

        }
        if (!seleccion) {
            MensajesErrores.advertencia("Seleccione al menos un bien a transferir");
            return true;
        }

        for (Constataciones c : listaConstataciones) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.bien=:bien and o.solicitud.estado=0");
            parametros.put("bien", c);
//            try {
//                if (c.getSeleccionado()) {
//                    if (ejbTransferencias.contar(parametros) != 0) {
//                        MensajesErrores.advertencia("El bien seleccionado " + c.getDescripcion() + " esta en un estado de Solicitud");
//                        return true;
//                    }
//                }

//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
//                Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            if (!solicitud.getBodega()) {
                solicitud.setUsuariotranferir(empleadoBean.getEmpleadoSeleccionado().getEntidad().getPin());
            }
            ejbSolicitudes.create(solicitud, seguridadbean.getLogueado().getUserid());
            for (Constataciones c : listaConstataciones) {
                if (c.getSeleccionado()) {
                    Transferencias t = new Transferencias();
                    t.setBien(c);
                    t.setSolicitud(solicitud);
                    ejbTransferencias.create(t, seguridadbean.getLogueado().getUserid());
                }
            }

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        enviarEmail(solicitud);
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            for (Constataciones c : listaConstataciones) {
                Transferencias t = new Transferencias();
                if (c.getSeleccionado()) {
                    t.setBien(c);
                    t.setSolicitud(solicitud);
                    ejbTransferencias.edit(t, seguridadbean.getLogueado().getUserid());
                } else {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.bien=:bien and o.solicitud.estado=0");
                    parametros.put("bien", c);
                    List<Transferencias> list = ejbTransferencias.encontarParametros(parametros);
                    if (!list.isEmpty()) {
                        for (Transferencias tr : list) {
                            ejbTransferencias.remove(tr, seguridadbean.getLogueado().getUserid());
                        }
                    }

                }
            }
        } catch (ConsultarException | BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.solicitud=:solicitud");
            parametros.put("solicitud", solicitud);
            List<Transferencias> listaT = ejbTransferencias.encontarParametros(parametros);
            for (Transferencias t : listaT) {
                ejbTransferencias.remove(t, seguridadbean.getLogueado().getUserid());
            }
            ejbSolicitudes.remove(solicitud, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String documentos(Solicitudes s) {
        if (!esAdministrativo) {
            MensajesErrores.advertencia("Opción sólo para custodios administrativos");
            return null;
        }
        imagenesBean.setModulo("SOLICITUDTRANSFERENCIAS");
        imagenesBean.setIdModulo(s.getId());
        imagenesBean.Buscar();
        formularioDocumento.editar();
        return null;
    }

    public String salir() {
        formulario.cancelar();
        return null;
    }

    public String seleccionar() {
        for (Constataciones c : listaConstataciones) {
            if (!c.getSeleccionado()) {
                c.setSeleccionado(Boolean.TRUE);
            }
        }
        return null;
    }

    public String quitar() {
        for (Constataciones c : listaConstataciones) {
            if (c.getSeleccionado()) {
                c.setSeleccionado(Boolean.FALSE);
            }
        }
        return null;
    }

    public String reporteActa(Solicitudes s) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.solicitud=:solicitud");
        parametros.put("solicitud", s);
        try {
            List<Transferencias> listaTransferencias = ejbTransferencias.encontarParametros(parametros);

            Empleados eSolicitar = traerEmpleado(s.getUsuariosolicitante());
            String nombreSolicitar = eSolicitar != null ? eSolicitar.toString() : "";

            String organigramaS = empleado.getOficina() != null ? empleado.getOficina().getOrganigrama().getNombre() : "";

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 5, true, "CÓDIGO DEL BIEN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "DESCRIPCIÓN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "MARCA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "MODELO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "SERIE"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "NRO. CHASIS"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "NRO. MOTOR"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "COLOR"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "ESTADO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "UBICACIÓN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "OBSERVACION"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String anio = new SimpleDateFormat("yyyy").format(s.getFechasolicitud());
            String mes = new SimpleDateFormat("MM").format(s.getFechasolicitud());
            String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
            String dia = new SimpleDateFormat("dd").format(s.getFechasolicitud());

            //SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("ACTIVOS FIJOS /BIENES DE CONTROL ", AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo("\nCAMBIO DE USUARIO FINAL", AuxiliarReporte.ALIGN_CENTER, 11, false);
            if (s.getUsuariotranferir() != null) {
                Organigrama organigramaT = seguridadbean.traerDirEmpleado(s.getUsuariotranferir());
                Empleados eTransferir = traerEmpleado(s.getUsuariotranferir());
                String nombretTransferir = eTransferir != null ? eTransferir.toString() : "";
                String fechaT = " " + dia + " de " + nomMes + " de " + anio + " ";
//                pdf.agregaParrafo("\n En la ciudad de Quito, " + sdf.format(s.getFechasolicitud() != null ? s.getFechasolicitud() : "") + ", el Sr. "
                pdf.agregaParrafo("\n En la ciudad de Quito, " + fechaT + ", el Sr. "
                        + nombreSolicitar + " de " + organigramaS + " del CBDMQ quien entrega a " + nombretTransferir + " de " + organigramaT
                        + " del CBDMQ; acuerdan suscribir la presente Acta  de Entrega Recepción para dejar constancia de haberse entregado a entera "
                        + "satisfacción los bienes que a continuación se detallan:",
                        AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
            } else {
                pdf.agregaParrafo("\n En la ciudad de Quito, " + sdf.format(s.getFechasolicitud() != null ? s.getFechasolicitud() : "") + ", el Sr. "
                        + nombreSolicitar + " de " + organigramaS + " del CBDMQ quien entrega a Bodega Central"
                        + " de Bodega Central del CBDMQ; acuerdan suscribir la presente Acta  de Entrega Recepción para "
                        + "dejar constancia de haberse entregado a entera satisfacción los bienes que a continuación se detallan:",
                        AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
            }

            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            for (Transferencias t : listaTransferencias) {
                Oficinas o = t.getBien().getUbicacion() != null ? ejbOficinas.find(t.getBien().getUbicacion()) : null;
//                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 5, false, t.getBien().getCodigobien() != null ? t.getBien().getCodigobien() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getCodigo() != null ? t.getBien().getCodigo() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getDescripcion() != null ? t.getBien().getDescripcion() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getMarca() != null ? t.getBien().getMarca() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getModelo() != null ? t.getBien().getModelo() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getNroserie() != null ? t.getBien().getNroserie() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getNrochasis() != null ? t.getBien().getNrochasis() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getNromotor() != null ? t.getBien().getNromotor() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getColor() != null ? t.getBien().getColor() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getEstadobien() != null ? t.getBien().getEstadobien() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, o != null ? o.getNombre(): ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 10, false, t.getBien().getObservacion() != null ? t.getBien().getObservacion() : ""));
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");

            pdf.agregaParrafo("\n Total: " + listaTransferencias.size(), AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            pdf.agregaParrafo("\n El custodio de los bienes declara conocer el REGLAMENTO GENERAL PARA LA ADMINISTRACION, UTILIZACIÓN, "
                    + "MANEJO Y CONTROL DE BIENES Y EXISTENCIAS DEL SECTOR PÚBLICO EMITIDO POR LA CONTRALORIA GENERAL "
                    + "DEL ESTADO, quien deberá dar oportuno cumplimiento, que en su artículo dice:", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            pdf.agregaParrafo("\n Art. 19.- Custodio administrativo.- ", AuxiliarReporte.ALIGN_JUSTIFIED, 8, true);
            pdf.agregaParrafo("Será el/la responsable de mantener actualizados los registros de ingresos, egresos y traspasos de los "
                    + "bienes y/o inventarios en el área donde presta sus servicios, conforme a las necesidades de los Usuarios Finales.\n"
                    + "El Custodio Administrativo, además, realizará la constatación física de bienes y/o inventarios en las unidades a la "
                    + "que pertenece, previo conocimiento y autorización del titular de la unidad, para remitir a la Unidad de Administración "
                    + "de Bienes e Inventarios de la entidad u organismo.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            pdf.agregaParrafo("\n Art. 20.- Usuario Final.-", AuxiliarReporte.ALIGN_LEFT, 8, true);
            pdf.agregaParrafo("Será el responsable del cuidado, buen uso, custodia y conservación de los bienes e inventarios a él "
                    + "asignados para el desempeño de sus funciones y los que por delegación expresa se agreguen a su cuidado, conforme "
                    + "a las disposiciones legales y reglamentarias correspondientes.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            if (s.getUsuariotranferir() != null) {
                Organigrama organigramaT = seguridadbean.traerDirEmpleado(s.getUsuariotranferir());
                Empleados eTransferir = traerEmpleado(s.getUsuariotranferir());
                pdf.agregaParrafo("\n Los bienes descritos en mención, pasarán a ser utilizados en la "
                        + organigramaT + " del CBDMQ, con el fin de cubrir las necesidades del personal; estos bienes estarán bajo la  "
                        + "responsabilidad del usuario final " + eTransferir + ", quien  declara conocer la ley vigente "
                        + "EMITIDO POR LA CONTRALORIA GENERAL DEL ESTADO, y cumplirá oportunamente las disposiciones "
                        + "contenidas en dicho Acuerdo.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            } else {
                pdf.agregaParrafo("\n Los bienes descritos en mención, pasarán a ser utilizados en la Dodega Central"
                        + " del CBDMQ, con el fin de cubrir las necesidades del personal; estos bienes estarán bajo la  "
                        + "responsabilidad del usuario final Bodega Central, quien  declara conocer la ley vigente "
                        + "EMITIDO POR LA CONTRALORIA GENERAL DEL ESTADO, y cumplirá oportunamente las disposiciones "
                        + "contenidas en dicho Acuerdo.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            }
            pdf.agregaParrafo("\n Art. 11.- Unidad de Administración de Bienes e Inventarios-", AuxiliarReporte.ALIGN_LEFT, 8, true);
            pdf.agregaParrafo("La Unidad de Administración de Bienes e Inventarios, o aquella que hiciere sus veces a nivel institucional, "
                    + "orientará y dirigirá la correcta conservación y cuidado de los bienes que han sido adquiridos o asignados para uso de "
                    + "la entidad u organismo y que se hallen en custodia de los Usuarios Finales a cualquier título como: compra venta, "
                    + "transferencia gratuita, comodato, depósito u otros semejantes, de acuerdo con este reglamento y las demás disposiciones "
                    + "que dicte la Contraloría General del Estado y la propia entidad u organismo.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            pdf.agregaParrafo("\n Art. 54.- Procedencia.-", AuxiliarReporte.ALIGN_LEFT, 8, true);
            pdf.agregaParrafo("En cada área de las entidades u organismos comprendidos en el artículo 1 del presente reglamento, se efectuará "
                    + "la constatación física de los bienes e inventarios, por lo menos una vez al año, en el tercer trimestre de cada ejercicio "
                    + "fiscal ...\" \"Los resultados de la constatación física serán enviados a la Unidad Administrativa para fines de consolidación.",
                    AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            pdf.agregaParrafo("\n OBSERVACIÓN :", AuxiliarReporte.ALIGN_JUSTIFIED, 8, true);

            pdf.agregaParrafo("Esta entrega recepción se sujeta a las siguientes cláusulas conforme establece el Reglamento General Sustitutivo "
                    + "para el Manejo y Administración de bienes del Sector Publico: ", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("• Todos los bienes descritos en la presente Acta serán de exclusiva responsabilidad, buen uso y cuidado de quien (es) reciben.",
                    AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("• En caso de cambio, retiro  o incremento de bienes, estos deberán ser notificados al la Unidad de Gestión de Bienes, "
                    + "para su actualización, registro y control.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("• En caso de hurto, robo, abigeato, fuerza mayor o caso fortuito, deberá proceder conforme lo establece el "
                    + "\"Art. 151.- Denuncia.- Cuando alguno de los bienes hubiere desaparecido por hurto, robo, "
                    + "abigeato o por cualquier causa semejante, presunta, el Usuario Final, Custodio Administrativo o el Guardalmacén a quien haga "
                    + "sus veces, según sea el caso, comunicará por escrito inmediatamente después de conocido el hecho al titular de la Unidad "
                    + "Administrativa, quien a su vez comunicará a la máxima autoridad de la entidad u organismo o su delegado. (...) El Usuario "
                    + "Final, el Custodio Administrativo o el Guardalmacén o quien haga sus veces, a petición del abogado que llevará la causa, "
                    + "facilitarán y entregarán la información necesaria para los trámites legales; el abogado será el responsable de impulsar "
                    + "la causa hasta la conclusión del proceso, de acuerdo a las formalidades establecidas en el Código Orgánico Integral Penal.\"",
                    AuxiliarReporte.ALIGN_JUSTIFIED, 8, true);
            pdf.agregaParrafo("• En caso de establecer responsabilidad en su contra, la reposición de los bienes, se hará en dinero a precio de "
                    + "mercado o en especies de iguales características del bien desaparecido, destruido o inutilizado, conforme lo establece el "
                    + "Art.49.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("Art. 49.- Daño, pérdida o destrucción de bienes e inventarios.- La máxima autoridad o su delegado, dispondrá la "
                    + "reposición de bienes nuevos de similares o superiores características; o, el pago del valor actual del mercado al "
                    + "Usuario Final o los terceros que de cualquier manera tengan acceso al bien cuando realicen acciones de mantenimiento "
                    + "o reparación por requerimiento propio, salvo que se conozca o se compruebe la identidad de la persona causante de la "
                    + "afectación al bien, sustentado en los respectivos informes técnicos y demás documentos administrativos y/o judiciales.",
                    AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            parametros = new HashMap();
            parametros.put(";where", "o.cibien=:cibien");
            parametros.put("cibien", empleado.getEntidad().getPin());

            Empleados e = null;
            List<Custodios> listac = ejbCustodios.encontarParametros(parametros);
            if (!listac.isEmpty()) {
                e = traerEmpleado(listac.get(0).getCiadministrativo());

            }
            List<String> titu = new LinkedList<>();
            valores = new LinkedList<>();
            titu.add("CUSTODIO ADMINISTRATIVO");
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "C.I.: " + (e != null ? e.getEntidad().getPin() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "Nombre: " + (e != null ? e.toString() : "")));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "firma"));
            pdf.agregarTabla(titu, valores, 1, 100, "", AuxiliarReporte.ALIGN_LEFT);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            List<String> tit = new LinkedList<>();
            valores = new LinkedList<>();
            if (s.getUsuariotranferir() != null) {
                Empleados eTransferir = traerEmpleado(s.getUsuariotranferir());
                String nombretTransferir = eTransferir != null ? eTransferir.toString() : "";
                tit.add("ENTREGO CONFORME. USUARIO FINAL");
                tit.add("RECIBO CONFORME. USUARIO FINAL");
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "C.I.: " + s.getUsuariosolicitante()));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "C.I.: " + s.getUsuariotranferir()));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "Nombre: " + nombreSolicitar));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "Nombre: " + nombretTransferir));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "___________________________________________"));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "___________________________________________"));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "firma"));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "firma"));
            } else {
                tit.add("ENTREGO CONFORME. USUARIO FINAL");
                tit.add("RECIBO CONFORME. USUARIO FINAL");
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "C.I.: " + s.getUsuariosolicitante()));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "Bodega Central"));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "Nombre: " + nombreSolicitar));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "Nombre: Bodega Central"));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 10, false, ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "___________________________________________"));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "___________________________________________"));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "firma"));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, true, "firma"));
            }

            pdf.agregarTabla(tit, valores, 2, 100, "", AuxiliarReporte.ALIGN_LEFT);

            pdf.agregaParrafo("\n\n Para fe y constancia se firma la presente acta CUATRO EJEMPLARES de igual valor y tenor. ", AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafo("2 PARA C/USUARIOS FINAL", AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafo("1 CUSTODIO ADMINISTRATIVO", AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafo("1 UNIDAD DE BIENENES (ENVIO ELECTRONICO Y FISICO)", AuxiliarReporte.ALIGN_LEFT, 6, false);

            reportepdf = pdf.traerRecurso();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioActa.insertar();
        return null;
    }

    public String nombremes(Integer i) {
        switch (i) {
            case 0:
                return "Enero";
            case 1:
                return "Febrero";
            case 2:
                return "Marzo";
            case 3:
                return "Abril";
            case 4:
                return "Mayo";
            case 5:
                return "Junio";
            case 6:
                return "Julio";
            case 7:
                return "Agosto";
            case 8:
                return "Septiembre";
            case 9:
                return "Octubre";
            case 10:
                return "Noviembre";
            case 11:
                return "Diciembre";
        }
        return "";
    }

    public Empleados traerEmpleado(String pin) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.pin=:pin");
        parametros.put("pin", pin.trim());
        try {
            List<Empleados> listaEmple = ejbEmpleados.encontarParametros(parametros);
            for (Empleados c : listaEmple) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Oficinas traerOficina(Integer oficina) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:id");
        parametros.put("id", oficina);
        try {
            List<Oficinas> listaOfi = ejbOficinas.encontarParametros(parametros);
            for (Oficinas c : listaOfi) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Organigrama traerDirEmpleado(String cedula) {
        if (cedula == null) {
            return null;
        }
        try {
            // traer empleado
            Empleados e;

            e = seguridadbean.traerCedula(cedula).getEmpleados();

            if (e == null) {
                return null;
            }
            if (e.getCargoactual() == null) {
                return null;
            }
            boolean si = false;
            Organigrama o = e.getCargoactual().getOrganigrama();
            while (true) {
                if (o.getEsdireccion() == null) {
                    o.setEsdireccion(false);
                }
                if (o.getEsdireccion()) {
                    return o;
                }
                o = o.getSuperior();
                if (o == null) {
                    return null;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Solicitudes traerSolicitud(Integer soli) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.id=:id");
        parametros.put("id", soli);
        try {
            List<Solicitudes> listaS = ejbSolicitudes.encontarParametros(parametros);
            for (Solicitudes s : listaS) {
                return s;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboCustodioBienes() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.ciadministrativo=:administrativo");
            parametros.put("administrativo", seguridadbean.getLogueado().getPin());
            List<Custodios> lista = ejbCustodios.encontarParametros(parametros);
            List<Empleados> retorno = new LinkedList<>();
            for (Custodios c : lista) {
                Entidades e = seguridadbean.traerCedula(c.getCibien());

                if (e != null) {
                    boolean finalizado;
                    parametros = new HashMap();
                    parametros.put(";where", "o.cicustodio=:ci");
                    parametros.put("ci", e.getPin());
                    if (ejbConstataciones.contar(parametros) == 0) {
                        finalizado = false;
                    } else {
                        parametros = new HashMap();
                        parametros.put(";where", "o.cicustodio=:ci and o.estado=false");
                        parametros.put("ci", e.getPin());
                        finalizado = ejbConstataciones.contar(parametros) == 0;
                    }
                    e.setNombres(e.getNombres());
                    retorno.add(e.getEmpleados());
                }
            }
            return Combos.getSelectItems(retorno, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String enviarEmail(Solicitudes s) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.solicitud=:solicitud");
        parametros.put("solicitud", s);
        try {
            List<Transferencias> listaTransferencias = ejbTransferencias.encontarParametros(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.cicustodio=:cicustodio");
            parametros.put("cicustodio", empleado.getEntidad().getPin());
            Oficinas c = null;
            List<Constataciones> listaco = ejbConstataciones.encontarParametros(parametros);
            if (!listaco.isEmpty()) {
                c = traerOficina(listaco.get(0).getUbicacion());
            }

            String texto = "<p>Estimad@:</p>";
            texto += "<p>Se ha realizado la solicitud de tranferencia de los siguientes bienes: </p>\n";

            texto += "<table border=\"1\">";
            texto += "<tr><td><b> Código </b></td>";
            texto += "<td><b> Descripción </b></td>";
            texto += "<td><b> Serie </b></td>";
            texto += "<td><b> Color </b></td>";
            texto += "<td><b> Estado del bien </b></td>";
            texto += "<td><b> Observación </b></td>";
            texto += "<td><b> Ubicación </b></td></tr>";

            for (Transferencias t : listaTransferencias) {
                texto += "<tr><td> " + t.getBien().getCodigo() + "</td>\n";
                texto += "<td> " + t.getBien().getDescripcion() + "</td>\n";
                texto += "<td> " + t.getBien().getNroserie() + "</td>\n";
                texto += "<td> " + t.getBien().getColor() + "</td>\n";
                texto += "<td> " + t.getBien().getEstadobien() + "</td>\n";
                texto += "<td> " + (c != null ? c.getNombre() : "") + "</td>\n";
                texto += "<td> " + t.getBien().getObservacion() + "</td></tr>\n";
            }
            texto += "</table>";
            parametros = new HashMap();
            parametros.put(";where", "o.cibien=:cibien");
            parametros.put("cibien", s.getUsuariosolicitante());

            Empleados eA = null;
            List<Custodios> listaCA = ejbCustodios.encontarParametros(parametros);
            if (!listaCA.isEmpty()) {
                eA = traerEmpleado(listaCA.get(0).getCiadministrativo());

            }
            parametros = new HashMap();
            parametros.put(";where", "o.cibien=:cibien");
            parametros.put("cibien", s.getUsuariotranferir());
            Empleados eN = null;
            List<Custodios> listaCN = ejbCustodios.encontarParametros(parametros);
            if (!listaCN.isEmpty()) {
                eN = traerEmpleado(listaCN.get(0).getCiadministrativo());

            }

            Empleados eSolicitar = traerEmpleado(s.getUsuariosolicitante());
            String emailSolicita = eSolicitar != null ? eSolicitar.getEntidad().getEmail() : "";
            if (s.getUsuariotranferir() != null) {
                Empleados eTransferir = traerEmpleado(s.getUsuariotranferir());
                String emailTransferir = eTransferir != null ? eTransferir.getEntidad().getEmail() : "";
                texto += "<p></p>";
                texto += "<p> Custodio Administrativo Actual: " + traerEmpleado(eA != null ? eA.getEntidad().getPin() : "") + "</p>\n";
                texto += "<p> Ususario Actual: " + traerEmpleado(s.getUsuariosolicitante()) + "</p>\n";
                texto += "<p> Custodio Administrativo Nuevo: " + traerEmpleado(eN != null ? eN.getEntidad().getPin() : "") + "</p>\n";
                texto += "<p> Usuario Nuevo: " + eTransferir + "</p>\n";
                texto += "<p></p>";
                texto += "<p><Strong>Nota:</Strong> El envío de este correo es automático, por favor no lo responda.";
                ejbCorreos.enviarCorreo(eA != null ? eA.getEntidad().getEmail() : "", "Tranferencia de Bienes", texto);//Administrativo Actual
                ejbCorreos.enviarCorreo(emailSolicita, "Tranferencia de Bienes", texto);//Custodio Actual
                ejbCorreos.enviarCorreo(eN != null ? eN.getEntidad().getEmail() : "", "Tranferencia de Bienes", texto);//Administrativo Nuevo
                ejbCorreos.enviarCorreo(emailTransferir, "Tranferencia de Bienes", texto);//Nuevo Custodio
            } else {
                texto += "<p></p>";
                texto += "<p> Custodio Administrativo Actual: " + traerEmpleado(eA != null ? eA.getEntidad().getPin() : "") + "</p>\n";
                texto += "<p> Custodio Actual: " + traerEmpleado(s.getUsuariosolicitante()) + "</p>\n";
                texto += "<p> Custodio Nuevo: Bodega Central </p>\n";

                texto += "<p></p>";
                texto += "<p><Strong>Nota:</Strong> El envío de este correo es automático, por favor no lo responda.";
                ejbCorreos.enviarCorreo(eA != null ? eA.getEntidad().getEmail() : "", "Tranferencia de Bienes", texto);//Administrativo Actual
                ejbCorreos.enviarCorreo(emailSolicita, "Tranferencia de Bienes", texto);//Custodio Actual
            }

        } catch (MessagingException | UnsupportedEncodingException | ConsultarException ex) {
            Logger.getLogger(TransferenciaBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
     * @return the solicitud
     */
    public Solicitudes getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(Solicitudes solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the listaConstataciones
     */
    public List<Constataciones> getListaConstataciones() {
        return listaConstataciones;
    }

    /**
     * @param listaConstataciones the listaConstataciones to set
     */
    public void setListaConstataciones(List<Constataciones> listaConstataciones) {
        this.listaConstataciones = listaConstataciones;
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
     * @return the tranferencia
     */
    public Transferencias getTranferencia() {
        return tranferencia;
    }

    /**
     * @param tranferencia the tranferencia to set
     */
    public void setTranferencia(Transferencias tranferencia) {
        this.tranferencia = tranferencia;
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
     * @return the pdf
     */
    public DocumentoPDF getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(DocumentoPDF pdf) {
        this.pdf = pdf;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the formularioActa
     */
    public Formulario getFormularioActa() {
        return formularioActa;
    }

    /**
     * @param formularioActa the formularioActa to set
     */
    public void setFormularioActa(Formulario formularioActa) {
        this.formularioActa = formularioActa;
    }

    /**
     * @return the listaSolicitudes
     */
    public List<Solicitudes> getListaSolicitudes() {
        return listaSolicitudes;
    }

    /**
     * @param listaSolicitudes the listaSolicitudes to set
     */
    public void setListaSolicitudes(List<Solicitudes> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    /**
     * @return the esAdministrativo
     */
    public Boolean getEsAdministrativo() {
        return esAdministrativo;
    }

    /**
     * @param esAdministrativo the esAdministrativo to set
     */
    public void setEsAdministrativo(Boolean esAdministrativo) {
        this.esAdministrativo = esAdministrativo;
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
     * @return the formularioDocumento
     */
    public Formulario getFormularioDocumento() {
        return formularioDocumento;
    }

    /**
     * @param formularioDocumento the formularioDocumento to set
     */
    public void setFormularioDocumento(Formulario formularioDocumento) {
        this.formularioDocumento = formularioDocumento;
    }
}
