/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.RetencionesBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.NotascreditoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Notascredito;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoegreso;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "notasCreditoSfccbdmq")
@ViewScoped
public class NotasCreditoBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public NotasCreditoBean() {
        Calendar c = Calendar.getInstance();
        obligaciones = new LazyDataModel<Obligaciones>() {

            @Override
            public List<Obligaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        notasCredito = new LazyDataModel<Notascredito>() {

            @Override
            public List<Notascredito> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Obligaciones> obligaciones;
    private LazyDataModel<Notascredito> notasCredito;
    private Notascredito notaCredito;
    private List<Rascompras> detalles;
    private List<Renglones> renglones;
    private List<Renglones> renglonesnc;
    private Obligaciones obligacion;
    private Autorizaciones autorizacion;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RascomprasFacade ejbDetalles;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private NotascreditoFacade ejbNotas;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{facturaSri}")
    private FacturaSriBean facturaBean;
    private Perfiles perfil;
    private Date desde;
    private Date desdeNc;
    private Date hasta;
    private Date hastaNc;
    private String tipoFecha = "o.fechaemision";
    private String tipoFechaNc = "o.emision";
    private String concepto;
    private String conceptoNc;
    private Integer estadoNc = 1;
    private Integer numero;
    private Integer numeroNc;
    private Integer id;
    private Integer idNc;
    private Integer estado = 1;
    private Tipoegreso tipoEgreso;
    private Codigos tipoDocumento;
    private Contratos contrato;

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
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        hastaNc = configuracionBean.getConfiguracion().getPfinal();
        desdeNc = configuracionBean.getConfiguracion().getPinicial();

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        if (redirect == null) {
//            return;
        } else {
            String perfilLocal = (String) params.get("p");
            String nombreForma = "NotasCreditosVista";
            if (perfilLocal == null) {
                MensajesErrores.fatal("Sin perfil válido");
                getSeguridadbean().cerraSession();
            }
            this.setPerfil(getSeguridadbean().traerPerfil(perfilLocal));
            if (this.getPerfil() == null) {
                MensajesErrores.fatal("Sin perfil válido");
                getSeguridadbean().cerraSession();
            }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//            if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//                if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                    MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                    getSeguridadbean().cerraSession();
//                }
//            }
        }
    }

    public List<Obligaciones> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fechaingreso desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = tipoFecha + " between :desde and :hasta ";
//        String where = tipoFecha + " between :desde and :hasta and o.compromiso is null";
//                        + "and o.detcertificacion.certificacion.anulado=false ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }
        if (getProveedorBean().getProveedor()!= null) {
            if (contrato != null) {
                where += " and o.contrato=:contrato";
                parametros.put("contrato", contrato);
            } else {
                where += " and o.proveedor=:proveedor";
                parametros.put("proveedor", getProveedorBean().getProveedor());
            }
        }
        if (estado != null) {
            where += " and o.estado=:estado";
            parametros.put("estado", estado);
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.concepto) like :concepto";
            parametros.put("concepto", concepto.toUpperCase() + "%");
        }
        if (tipoEgreso != null) {
            where += " and o.tipoobligacion=:tipoobligacion";
            parametros.put("tipoobligacion", tipoEgreso);
        }
        if (tipoDocumento != null) {
            where += " and o.tipodocumento=:tipodocumento";
            parametros.put("tipodocumento", tipoDocumento);
            if (numero != null) {
                where += " and o.documento=:documento";
                parametros.put("documento", numero);
            }
        }

        int total = 0;
        try {
            parametros.put(";where", where);
            total = ejbObligaciones.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }
        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        obligaciones.setRowCount(total);
        try {
            return ejbObligaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Notascredito> cargaBusquedaNc(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.emision desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = tipoFechaNc + " between :desde and :hasta ";
//                        + "and o.detcertificacion.certificacion.anulado=false ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }
//        if (getProveedorBean().getEmpresa() != null) {
//            where += " and o.obligacion.proveedor=:proveedor";
//            parametros.put("proveedor", getProveedorBean().getEmpresa().getProveedores());
//        }

        parametros.put("desde", desdeNc);
        parametros.put("hasta", hastaNc);
        if (!((conceptoNc == null) || (conceptoNc.isEmpty()))) {
            where += " and upper(o.concepto) like:concepto";
            parametros.put("concepto", conceptoNc.toUpperCase() + "%");
        }

        int total = 0;
        try {
            parametros.put(";where", where);
            total = ejbNotas.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }
        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        notasCredito.setRowCount(total);
        try {
            return ejbNotas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        obligaciones = new LazyDataModel<Obligaciones>() {

            @Override
            public List<Obligaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };

        return null;
    }

    public String buscarNc() {

        notasCredito = new LazyDataModel<Notascredito>() {

            @Override
            public List<Notascredito> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusquedaNc(i, pageSize, scs, map);
            }
        };

        return null;
    }

    public String nuevo() {
        formularioObligacion.insertar();
        return null;
    }

    public String selecciona(Obligaciones obligacion) {
        notaCredito = new Notascredito();
        notaCredito.setObligacion(obligacion);
        notaCredito.setFecha(new Date());
        // traer ras compras para llenar lo correcto y ver el valor máximo
        traerRenglones();
        traerRasCompras();
        formularioObligacion.cancelar();
        proveedorBean.setEmpresa(notaCredito.getObligacion().getProveedor().getEmpresa());
        proveedorBean.setProveedor(notaCredito.getObligacion().getProveedor());
        formulario.insertar();
        return null;
    }

    private void traerRenglones() {
        if (notaCredito.getObligacion() != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='OBLIGACIONES'");
            parametros.put("id", notaCredito.getObligacion().getId());
            parametros.put(";orden", "o.valor desc");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void traerRasCompras() {
        if (notaCredito.getObligacion() != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", notaCredito.getObligacion());
            try {
                detalles = ejbDetalles.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void traerRasComprasNc() {
        if (notaCredito != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.notacredito=:notacredito");
            parametros.put("notacredito", notaCredito);
            try {
                detalles = ejbDetalles.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void traerRenglonesNc() {
        if (notaCredito != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='NOTACREDITO'");
            parametros.put("id", notaCredito.getId());
            parametros.put(";orden", "o.valor desc");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglonesnc = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String modifica(Notascredito notaCredito) {
        this.notaCredito = notaCredito;

        proveedorBean.setEmpresa(notaCredito.getObligacion().getProveedor().getEmpresa());
        proveedorBean.setProveedor(notaCredito.getObligacion().getProveedor());
//        traerRenglones();
//        traerRasCompras();
        traerRasComprasNc();
        traerRenglonesNc();
        formularioReporte.insertar();
        if (notaCredito.getElectronico() != null) {
            facturaBean.cargarNc(notaCredito.getElectronico());
        }
        return null;
    }

    public String contabiliza(Notascredito notaCredito) {
        this.notaCredito = notaCredito;
        traerRasComprasNc();
        traerRenglonesNc();
        proveedorBean.setEmpresa(notaCredito.getObligacion().getProveedor().getEmpresa());
        proveedorBean.setProveedor(notaCredito.getObligacion().getProveedor());
        try {
            String resultado = ejbNotas.contabilizar(notaCredito, getSeguridadbean().getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo());
            if (resultado.contains("ERROR")) {
                MensajesErrores.advertencia(resultado);
                return null;
            }
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NotasCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRasComprasNc();
        traerRenglonesNc();
        formularioReporte.editar();
        return null;
    }

    public String elimina(Notascredito notaCredito) {
        this.notaCredito = notaCredito;
        traerRasComprasNc();
        traerRenglonesNc();
        formulario.eliminar();
        proveedorBean.setEmpresa(notaCredito.getObligacion().getProveedor().getEmpresa());
        proveedorBean.setProveedor(notaCredito.getObligacion().getProveedor());
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

    public boolean validar() {
//        if ((obligacion.getValorcertificacion() == null) || (obligacion.getValorcertificacion().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Es necesario valor de obligacion");
//            return true;
//        }
        if ((notaCredito.getConcepto() == null) || (notaCredito.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion");
            return true;
        }

        if ((notaCredito.getNumero() == null) || (notaCredito.getNumero().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Número");
            return true;
        }
        if (autorizacion == null) {
            MensajesErrores.advertencia("Seleccione una Autorización");
            return true;
        }
        if (notaCredito.getEmision().before(autorizacion.getFechaemision())) {
            MensajesErrores.advertencia("Fecha de emisión menor a fecha de autorización");
            return true;
        }
        if (notaCredito.getEmision().before(configuracionBean.getConfiguracion().getPvigente())){
            MensajesErrores.advertencia("Fecha de emisión menor a periodo vigente");
            return true;
        }
        if (notaCredito.getEmision().after(autorizacion.getFechacaducidad())) {
            MensajesErrores.advertencia("Fecha de emisión mayor a fecha de caducidad");
            return true;
        }
        Integer numeroDocumento = Integer.parseInt(notaCredito.getNumero());
        if (numeroDocumento < autorizacion.getInicio()) {
            MensajesErrores.advertencia("Número de documento menor a inicio de autorización");
            return true;
        }
        if (numeroDocumento > autorizacion.getFin()) {
            MensajesErrores.advertencia("Número de documento mayor a fin de autorización");
            return true;
        }
        if (notaCredito.getValor() == null) {
            MensajesErrores.advertencia("Valor es necesario");
            return true;
        }
        if (notaCredito.getValor().doubleValue() <= 0) {
            MensajesErrores.advertencia("Valor debe ser mayor a cero");
            return true;
        }
        if (notaCredito.getValor().doubleValue() > notaCredito.getObligacion().getApagar().doubleValue()) {
            MensajesErrores.advertencia("Valor no puede ser mayor al valor a pagar");
            return true;
        }
        // ver si es unico del proveedor, punto estab y estacion
        if (notaCredito.getElectronico() == null) {
            notaCredito.setEstablecimiento(autorizacion.getEstablecimiento());
            notaCredito.setPunto(autorizacion.getPuntoemision());
            notaCredito.setAutorizacion(autorizacion.getAutorizacion());
        }
        Map parametros = new HashMap();
        if (notaCredito.getId() != null) {
            parametros.put(";where", "o.obligacion.proveedor=:proveedor and "
                    + "o.establecimiento=:establecimiento and o.punto=:punto and o.numero=:numero and id!=:id");
            parametros.put("id", notaCredito.getId());
        } else {
            parametros.put(";where", "o.obligacion.proveedor=:proveedor and "
                    + "o.establecimiento=:establecimiento and o.punto=:punto and o.numero=:numero");
        }
        parametros.put("proveedor", notaCredito.getObligacion().getProveedor());
        parametros.put("establecimiento", notaCredito.getEstablecimiento());
        parametros.put("punto", notaCredito.getPunto());
        parametros.put("numero", notaCredito.getNumero());
        try {
            int total = ejbNotas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Número de documento ya utilizado");
                return true;
            }
            parametros = new HashMap();
            if (notaCredito.getId() != null) {
                parametros.put(";where", "o.obligacion=:obligacion"
                        + "id!=:id");
                parametros.put("id", notaCredito.getId());
            } else {
                parametros.put(";where", "o.obligacion=:obligacion");
            }
            parametros.put("obligacion", notaCredito.getObligacion());
            total = ejbNotas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Obligación ya tiene una NC asociada");
                return true;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(NotasCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
//            sumar para distribuir el valor sin impuestos
            double total = 0;
            for (Rascompras d : detalles) {
                total += d.getValor().doubleValue() + d.getValorimpuesto().doubleValue();

            }
            double factor = notaCredito.getValor().doubleValue() / total;
            ejbNotas.create(notaCredito, getSeguridadbean().getLogueado().getUserid());
            // Hacer el ras compras para luego generar el asiento
            for (Rascompras d : detalles) {
                Rascompras r = new Rascompras();
                r.setBien(d.getBien());
                r.setCba("N");
                r.setCc(d.getCc());
                r.setCuenta(d.getCuenta());
                r.setIdcuenta(d.getIdcuenta());
                r.setImpuesto(d.getImpuesto());
                r.setNotacredito(notaCredito);
                r.setNumero(d.getNumero());
                r.setReferencia(d.getReferencia());
                r.setRetencion(null);
                r.setRetimpuesto(null);
                r.setTipoegreso(null);
                r.setDetallecompromiso(d.getDetallecompromiso());
                double valor = (d.getValor().doubleValue() + d.getValorimpuesto().doubleValue()) * factor * -1;
                r.setValor(new BigDecimal(valor));
                r.setValorimpuesto(new BigDecimal(0));
                r.setValorret(new BigDecimal(0));
                r.setVretimpuesto(new BigDecimal(0));
                ejbDetalles.create(r, getSeguridadbean().getLogueado().getUserid());
                Detallecompromiso d1=r.getDetallecompromiso();
                d1.setSaldo(new BigDecimal(d1.getSaldo().doubleValue()-valor));
                ejbDetComp.edit(d1, getSeguridadbean().getLogueado().getUserid());
                
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NotasCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
    }

    public String grabar() {
        // asume que ya esta ras compras
        if (validar()) {
            return null;
        }
        try {
            ejbNotas.edit(notaCredito, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(NotasCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrar() {
        try {
//            obligacion.setEstado(-1);
            for (Rascompras r : detalles) {
                ejbDetalles.remove(r, getSeguridadbean().getLogueado().getUserid());
            }
            // borrar el asiento
            Cabeceras c = null;
            for (Renglones r : renglones) {
                c = r.getCabecera();
                ejbRenglones.remove(r, getSeguridadbean().getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabeceras.remove(c, getSeguridadbean().getLogueado().getUserid());
            }
            ejbNotas.remove(notaCredito, getSeguridadbean().getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NotasCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
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
     * @return the obligaciones
     */
    public LazyDataModel<Obligaciones> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(LazyDataModel<Obligaciones> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the obligacion
     */
    public Obligaciones getObligacion() {
        return obligacion;
    }

    /**
     * @param obligacion the obligacion to set
     */
    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
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
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
    }

    /**
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the estado
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    /**
     * @return the tipoEgreso
     */
    public Tipoegreso getTipoEgreso() {
        return tipoEgreso;
    }

    /**
     * @param tipoEgreso the tipoEgreso to set
     */
    public void setTipoEgreso(Tipoegreso tipoEgreso) {
        this.tipoEgreso = tipoEgreso;
    }

    /**
     * @return the tipoDocumento
     */
    public Codigos getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento the tipoDocumento to set
     */
    public void setTipoDocumento(Codigos tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the detalles
     */
    public List<Rascompras> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Rascompras> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
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
     * @return the notasCredito
     */
    public LazyDataModel<Notascredito> getNotasCredito() {
        return notasCredito;
    }

    /**
     * @param notasCredito the notasCredito to set
     */
    public void setNotasCredito(LazyDataModel<Notascredito> notasCredito) {
        this.notasCredito = notasCredito;
    }

    /**
     * @return the formularioObligacion
     */
    public Formulario getFormularioObligacion() {
        return formularioObligacion;
    }

    /**
     * @param formularioObligacion the formularioObligacion to set
     */
    public void setFormularioObligacion(Formulario formularioObligacion) {
        this.formularioObligacion = formularioObligacion;
    }

    /**
     * @return the notaCredito
     */
    public Notascredito getNotaCredito() {
        return notaCredito;
    }

    /**
     * @param notaCredito the notaCredito to set
     */
    public void setNotaCredito(Notascredito notaCredito) {
        this.notaCredito = notaCredito;
    }

    /**
     * @return the desdeNc
     */
    public Date getDesdeNc() {
        return desdeNc;
    }

    /**
     * @param desdeNc the desdeNc to set
     */
    public void setDesdeNc(Date desdeNc) {
        this.desdeNc = desdeNc;
    }

    /**
     * @return the hastaNc
     */
    public Date getHastaNc() {
        return hastaNc;
    }

    /**
     * @param hastaNc the hastaNc to set
     */
    public void setHastaNc(Date hastaNc) {
        this.hastaNc = hastaNc;
    }

    /**
     * @return the tipoFechaNc
     */
    public String getTipoFechaNc() {
        return tipoFechaNc;
    }

    /**
     * @param tipoFechaNc the tipoFechaNc to set
     */
    public void setTipoFechaNc(String tipoFechaNc) {
        this.tipoFechaNc = tipoFechaNc;
    }

    /**
     * @return the conceptoNc
     */
    public String getConceptoNc() {
        return conceptoNc;
    }

    /**
     * @param conceptoNc the conceptoNc to set
     */
    public void setConceptoNc(String conceptoNc) {
        this.conceptoNc = conceptoNc;
    }

    /**
     * @return the estadoNc
     */
    public Integer getEstadoNc() {
        return estadoNc;
    }

    /**
     * @param estadoNc the estadoNc to set
     */
    public void setEstadoNc(Integer estadoNc) {
        this.estadoNc = estadoNc;
    }

    /**
     * @return the numeroNc
     */
    public Integer getNumeroNc() {
        return numeroNc;
    }

    /**
     * @param numeroNc the numeroNc to set
     */
    public void setNumeroNc(Integer numeroNc) {
        this.numeroNc = numeroNc;
    }

    /**
     * @return the idNc
     */
    public Integer getIdNc() {
        return idNc;
    }

    /**
     * @param idNc the idNc to set
     */
    public void setIdNc(Integer idNc) {
        this.idNc = idNc;
    }

    /**
     * @return the autorizacion
     */
    public Autorizaciones getAutorizacion() {
        return autorizacion;
    }

    /**
     * @param autorizacion the autorizacion to set
     */
    public void setAutorizacion(Autorizaciones autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getValorStr() {
        if (notaCredito == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(notaCredito.getValor());
    }

    public void xmlListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        File xmlFileFactura;
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                xmlFileFactura = i.getFile();
                // trarer los xml y grabarlos
                String xmlStr = "";
                String sb;
                try {
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFileFactura), "UTF-8"));
                    while ((sb = entrada.readLine()) != null) {
                        xmlStr += sb;
                    }
                    notaCredito.setElectronico(xmlStr);
                    facturaBean.cargarNc(xmlStr);
                    notaCredito.setEstablecimiento(facturaBean.getNotaCr().getInfoTributaria().getEstab());
                    notaCredito.setPunto(facturaBean.getNotaCr().getInfoTributaria().getPtoEmi());
                    notaCredito.setAutorizacion(facturaBean.getNotaCr().getAutorizacion().getNumeroAutorizacion());
                    notaCredito.setEmision(facturaBean.getNotaCr().getFechaEmi());
                    notaCredito.setValor(new BigDecimal(facturaBean.getNotaCr().getInfoNotaCredito().getValorModificacion()));
                    notaCredito.setNumero(facturaBean.getNotaCr().getInfoTributaria().getSecuencial());
                    //
                } catch (IOException ex) {
                    MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                    Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } //fin if
        }// fin for
    }

    public String booraFacturaElectronica() {
        notaCredito.setElectronico(null);
        notaCredito.setEstablecimiento(null);
        notaCredito.setPunto(null);
        notaCredito.setAutorizacion(null);
        return null;
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
     * @return the facturaBean
     */
    public FacturaSriBean getFacturaBean() {
        return facturaBean;
    }

    /**
     * @param facturaBean the facturaBean to set
     */
    public void setFacturaBean(FacturaSriBean facturaBean) {
        this.facturaBean = facturaBean;
    }

    public Notascredito traer(Integer id) throws ConsultarException {
        return ejbNotas.find(id);
    }

    /**
     * @return the renglonesnc
     */
    public List<Renglones> getRenglonesnc() {
        return renglonesnc;
    }

    /**
     * @param renglonesnc the renglonesnc to set
     */
    public void setRenglonesnc(List<Renglones> renglonesnc) {
        this.renglonesnc = renglonesnc;
    }
}
