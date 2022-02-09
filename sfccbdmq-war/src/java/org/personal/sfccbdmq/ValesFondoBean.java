/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.ImpuestosFacade;
import org.beans.sfccbdmq.ValesfondosFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.ImpuestosBean;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Valesfondos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "valesFondoSfccbdmq")
@ViewScoped
public class ValesFondoBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;
    private Date desde;
    private Date hasta;
    private String concepto;
    private Fondos fondo;
    private Recurso reportepdf;
    private LazyDataModel<Valesfondos> listaVales;
    private List<Auxiliar> detalles;
    private Auxiliar detalle;
    private Valesfondos vale;
    private Formulario formulario = new Formulario();
    private Formulario formularioImrpimir = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioProveedor = new Formulario();
    private Perfiles perfil;
    private boolean movilizacion = false;
    @EJB
    private ValesfondosFacade ejbVales;
    @EJB
    private FondosFacade ejbFondos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ImpuestosFacade ejbImpuestos;

    public ValesFondoBean() {
        listaVales = new LazyDataModel<Valesfondos>() {
            @Override
            public List<Valesfondos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, i1, scs, map);
            }
        };

    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicial();
        setHasta(configuracionBean.getConfiguracion().getPfinal());
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ValeFondoVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            return;
        }

//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
////                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    public List<Valesfondos> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fecha desc, o.id desc");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        //String where = "  o.fondo.empleado=:empleado and o.fecha between :desde and :hasta";
        String where = "  o.fondo.empleado=:empleado and o.fecha between :desde and :hasta and o.fondo.cerrado=false";
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + clave + ") like :" + clave;
            parametros.put(clave, valor.toUpperCase() + "%");
        }
        parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.concepto) like :motivo";
            parametros.put("motivo", concepto.toUpperCase() + "%");
        }
        if (!((fondo == null))) {
            where += " and o.fondo=:fondo";
            parametros.put("fondo", fondo);
        }

        int total = 0;
        try {
            parametros.put(";where", where);
            total = ejbVales.contar(parametros);
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
        listaVales.setRowCount(total);
        try {
            return ejbVales.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        listaVales = new LazyDataModel<Valesfondos>() {
            @Override
            public List<Valesfondos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, i1, scs, map);
            }
        };
        return null;
    }

    public boolean isPuedePedir() {

        // traer los datos que e necesita
//        PSC
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.cerrado=false");
        parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
        try {
            List<Fondos> lExtra = ejbFondos.encontarParametros(parametros);

            for (Fondos c : lExtra) {
//                setFondo(c);
                return true;
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        return getFondo() != null;
        return false;
    }

    public String crear() {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso generar un vale de fondo");
            return null;
        }

//        if (getFondo() == null) {
//            MensajesErrores.advertencia("No exsite una fondo sin liquidar");
//            return null;
//        }
        if (fondo == null) {
            MensajesErrores.advertencia("Seleccione una Apertura");
            return null;
        }
        vale = new Valesfondos();
        vale.setFecha(new Date());
        vale.setFondo(getFondo());
        vale.setBaseimponible(new BigDecimal(BigInteger.ZERO));
        vale.setBaseimponiblecero(new BigDecimal(BigInteger.ZERO));
        vale.setEstado(-1);
        detalles = new LinkedList<>();
        formularioProveedor.insertar();
        return null;
    }

    public String modificar(Valesfondos vale) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso generar un vale de fondo");
            return null;
        }
        this.vale = vale;
        String[] aux = vale.getConcepto().split("#");
        detalles = new LinkedList<>();
        for (String auxInterior : aux) {
            auxInterior = auxInterior.replace("|", "#");
            auxInterior = auxInterior.replace(".", "");
            auxInterior = auxInterior.replace(",", ".");

            String[] datos = auxInterior.split("#");
            Auxiliar a = new Auxiliar();
            a.setTitulo1(datos[0]);
            a.setTotal(new BigDecimal(datos[1]));
            detalles.add(a);
        }
        formulario.editar();
        return null;
    }

    public String modificarProveedor(Valesfondos val) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso generar un vale de fondo");
            return null;
        }
        vale = val;
        fondo = vale.getFondo();
        if (vale.getProveedor() != null) {
            proveedorsBean.setTipobuscar("-1");
            proveedorsBean.setRuc(vale.getProveedor().getEmpresa().getRuc());
            proveedorsBean.setProveedor(vale.getProveedor());
            proveedorsBean.setEmpresa(vale.getProveedor().getEmpresa());
        } else {
            proveedorsBean.setProveedor(new Proveedores());
        }
        formularioProveedor.editar();
        return null;
    }

    public String getTotalVale() {
        double valor = traerValor();
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(valor);
    }

    public String getTotalFondo() {
        double valor = 0;
        Map parametros = new HashMap();
        String where = "o.fondo=:fondo and  o.fondo.empleado=:empleado and o.fecha between :desde and :hasta";
        parametros.put("fondo", getFondo());
        parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        try {
            List<Valesfondos> aux = ejbVales.encontarParametros(parametros);
            for (Valesfondos vc : aux) {
                valor += vc.getValor().doubleValue();
            }

            DecimalFormat df = new DecimalFormat("###,##0.00");
            return "Total fondo: " + df.format(valor) + " / " + df.format(getFondo().getValor());

        } catch (ConsultarException ex) {
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public double traerValor() {
        if (vale == null) {
            return 0;
        }
//        if (vale.getConcepto()==null) {
//            return 0;
//        }
        //String[] aux = vale.getConcepto().split("#");
        //detalles = new LinkedList<>();
        double valor = 0;
//        for (String auxInterior : aux) {
//            auxInterior=auxInterior.replace("|", "#");
//            auxInterior=auxInterior.replace(".", "");
//            auxInterior=auxInterior.replace(",", ".");
//            
//            String[] datos = auxInterior.split("#");
//            Auxiliar a = new Auxiliar();
//            a.setTotal(new BigDecimal(datos[1]));
//            valor+=a.getTotal().doubleValue();
//        }

        for (Auxiliar d : detalles) {
            valor += d.getTotal().doubleValue();
        }

        return valor;
    }

    public String eliminar(Valesfondos vale) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso generar un vale de fondo");
            return null;
        }
        this.vale = vale;
        String[] aux = vale.getConcepto().split("#");
        detalles = new LinkedList<>();
//        for (String auxInterior : aux) {
//            auxInterior = auxInterior.replace("|", "#");
//            auxInterior = auxInterior.replace(".", "");
//            auxInterior = auxInterior.replace(",", ".");
//            //String[] datos = auxInterior.split("|");
//            String[] datos = auxInterior.split("#");
//            Auxiliar a = new Auxiliar();
//            a.setTitulo1(datos[0]);
//            a.setTotal(new BigDecimal(datos[1]));
//            detalles.add(a);
//        }
        formulario.eliminar();
        return null;
    }

    public String insertar() {
        if (detalles.isEmpty()) {
            MensajesErrores.advertencia("No existe detalle del vale");
            return null;
        }
        String resultado = "";
        DecimalFormat df = new DecimalFormat("0.00");
        for (Auxiliar a : detalles) {
            if (!resultado.isEmpty()) {
                resultado += "#";
            }
            resultado += a.getTitulo1() + "|" + df.format(a.getTotal());
        }
        vale.setConcepto(resultado);
        if ((vale.getConcepto() == null) || (vale.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de vale");
            return null;
        }

        if ((vale.getFecha() == null) || (vale.getFecha().before(configuracionBean.getConfiguracion().getPvigente()))) {
            MensajesErrores.advertencia("Fecha de vale debe ser mayor a periodo vigente");
            return null;
        }

        try {
//            vale.setEstado(0);
            vale.setEstado(-1);
            vale.setValor(new BigDecimal(traerValor()));
            vale.setSolicitante(seguridadbean.getLogueado().getEmpleados());
            //            ES-2018-04-05 Control para el valor del vale
            if (controlPorcentajeVale(vale)) {
                return null;
            }
            ejbVales.create(vale, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabar() {
        if (detalles.isEmpty()) {
            MensajesErrores.advertencia("No existe detalle del vale");
            return null;
        }
        String resultado = "";
        DecimalFormat df = new DecimalFormat("0.00");
        for (Auxiliar a : detalles) {
            if (!resultado.isEmpty()) {
                resultado += "#";
            }
            resultado += a.getTitulo1() + "|" + df.format(a.getTotal());
        }
        vale.setConcepto(resultado);
        if ((vale.getConcepto() == null) || (vale.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de vale");
            return null;
        }
        try {
            if (detalles.isEmpty()) {
                MensajesErrores.advertencia("No existe detalle del vale");
                return null;
            }
            vale.setEstado(-1);
            //vale.setValor(new BigDecimal(getTotalVale()));
            vale.setValor(new BigDecimal(traerValor()));
            //            ES-2018-04-05 Control para el valor del vale
            if (controlPorcentajeVale(vale)) {
                return null;
            }
            ejbVales.edit(vale, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabarProveedor() {

        try {

            if ((vale.getFecha() == null) || (vale.getFecha().before(configuracionBean.getConfiguracion().getPvigente()))) {
                MensajesErrores.advertencia("Fecha de vale debe ser mayor a periodo vigente");
                return null;
            }
            if ((vale.getConcepto() == null) || (vale.getConcepto().isEmpty())) {
                MensajesErrores.advertencia("Es necesario concepto de vale");
                return null;
            }
            if (vale.getTipodocumento() == null) {
                MensajesErrores.advertencia("Seleccione tipo de documento");
                return null;
            }
            if (!movilizacion) {
                if (proveedorsBean.getProveedor() == null) {
                    MensajesErrores.advertencia("Seleccione un proveedor");
                    return null;
                }
                if ((vale.getAutorizacion() == null) || (vale.getAutorizacion().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria autorización");
                    return null;
                }
                if ((vale.getPuntoemision() == null) || (vale.getPuntoemision().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria punto de emisión");
                    return null;
                }
                if ((vale.getEstablecimiento() == null) || (vale.getEstablecimiento().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria establecimiento");
                    return null;
                }
                if ((vale.getNumero() == null) || (vale.getNumero() <= 0)) {
                    MensajesErrores.advertencia("Necesaria número de documento");
                    return null;
                }
            }
            vale.setEstado(0);
            vale.setProveedor(proveedorsBean.getProveedor());
            BigDecimal ciento = new BigDecimal(100);
            if (vale.getImpuesto() != null) {
                double valorIva = vale.getBaseimponible().doubleValue() * vale.getImpuesto().getPorcentaje().doubleValue() / 100;
                double ivaValor = (double) vale.getAjuste() / 100;
                valorIva += ivaValor;
                vale.setIva(new BigDecimal(valorIva + vale.getAjuste() / 100));
//                double ivaValor=vale.getIva().doubleValue();
//                System.out.println(""+ivaValor);
            } else {
                vale.setIva(new BigDecimal(BigInteger.ZERO));
            }
            double cuadre2 = (vale.getBaseimponiblecero() == null ? 0 : vale.getBaseimponiblecero().doubleValue())
                    + vale.getBaseimponible().doubleValue() + vale.getIva().doubleValue();
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double totalFactura = valortotal2.doubleValue();

//            vale.setValor(new BigDecimal(traerValor()));
            if (vale.getBaseimponible() == null) {
                vale.setBaseimponible(BigDecimal.ZERO);
            }
            if (vale.getBaseimponiblecero() == null) {
                vale.setBaseimponiblecero(BigDecimal.ZERO);
            }
            if (vale.getImpuesto() == null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo='IVA0' and o.activo=true");
                List<Impuestos> lista = ejbImpuestos.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Impuestos i = lista.get(0);
                    vale.setImpuesto(i);
                } else {
                    MensajesErrores.advertencia("Seleccione un Impuesto");
                    return null;
                }
            }
            double val = vale.getBaseimponiblecero().doubleValue() + vale.getBaseimponible().doubleValue() + (vale.getBaseimponible().doubleValue() * vale.getImpuesto().getPorcentaje().doubleValue() / 100 + vale.getAjuste() / 100);
            vale.setValor(new BigDecimal(val));
            vale.setSolicitante(seguridadbean.getLogueado().getEmpleados());
            //            ES-2018-04-05 Control para el valor del vale
            if (controlPorcentajeVale(vale)) {
                return null;
            }
            if (totalFactura > vale.getValor().doubleValue()) {
                MensajesErrores.advertencia("El valor total ingresado no debe exceder al señalado en el vale");
                return null;
            }

            if (vale.getId() == null) {
                ejbVales.create(vale, seguridadbean.getLogueado().getUserid());
            } else {
                ejbVales.edit(vale, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioProveedor.cancelar();
        return null;
    }

    public String borrar() {
        try {
            //buscar que no existan reformas,certificaciones para poder borrar
            if (vale.getEstado() > 1) {
                MensajesErrores.advertencia("Vale ya esta repuesto");
                return null;
            }
            ejbVales.remove(vale, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String reporte(Valesfondos vale) {
        this.vale = vale;

        formularioImrpimir.editar();
        try {
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            //titulos
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "FUENTE DE FINANCIAMIENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "VALOR INCLUIDO IVA"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            DocumentoPDF pdf = new DocumentoPDF("VALE DE CAJA ", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fondos de fondo Chica: " + getFondo().getId().toString(), AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fecha: Quito, " + sdf.format(vale.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Custodio del fondo:" + vale.getFondo().getEmpleado().toString(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Unidad Administrativa:" + vale.getFondo().getDepartamento().toString(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            pdf.agregaParrafo("\n\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            valores = new LinkedList<>();
            List<String> tit = new LinkedList<>();
            tit.add("ENTREGA:");
            tit.add("RECIBE:");
            tit.add("AUTORIZDO:");
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            List<Codigos> aux = codigosBean.traerCodigoMaestro("DIREC");

            pdf.agregarTabla(tit, valores, 3, 100, "", AuxiliarReporte.ALIGN_CENTER);
            setReportepdf(pdf.traerRecurso());
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean controlPorcentajeVale(Valesfondos v) {
        double totalVales = 0;
        double totalRestoFondo = 0;
//        double maxVale = v.getFondo().getValor().doubleValue() * (v.getFondo().getPrcvale().doubleValue() / 100);
        double maxVale = fondo.getValor().doubleValue() * (fondo.getPrcvale().doubleValue() / 100);
        Map parametros = new HashMap();
        parametros.put(";where", "o.fondo=:fondo");
//        parametros.put("fondo", v.getFondo());
        parametros.put("fondo", fondo);
        try {
            List<Valesfondos> aux = ejbVales.encontarParametros(parametros);
            List<Valesfondos> aux1 = new LinkedList<>();
            if (v.getId() == null) {
                aux1 = ejbVales.encontarParametros(parametros);
            } else {
                for (Valesfondos vc : aux) {
                    if (!vc.getId().equals(v.getId())) {
                        aux1.add(vc);
                    }
                }
            }
            for (Valesfondos vc : aux1) {
                totalVales += vc.getValor().doubleValue();
            }
            //totalRestoFondo = v.getFondo().getValor().doubleValue() - totalVales;
            //double maxVale = totalRestoFondo * (v.getFondo().getPrcvale().doubleValue() / 100);
            if (v.getValor().doubleValue() > maxVale) {
//                MensajesErrores.advertencia("El valor del vale no puede exceder el " + v.getFondo().getPrcvale().doubleValue() + "% del total de fondo chica");
                MensajesErrores.advertencia("El valor del vale no puede exceder el " + fondo.getPrcvale().doubleValue() + "% del total de fondo chica");
                return true;
            }
            totalVales += v.getValor().doubleValue();
//            if (totalVales > v.getFondo().getValor().doubleValue()) {
            if (totalVales > fondo.getValor().doubleValue()) {
                MensajesErrores.advertencia("El valor del vale excede el monto total de la fondo");
                return true;
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void cambiaValorImponible(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        DecimalFormat df = new DecimalFormat("0.00");
        if (vale.getImpuesto() != null) {
            BigDecimal ciento = new BigDecimal(100);
            vale.setIva(valor.multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
            //vale.setTotalFactura(valor.add(vale.getBaseimponiblecero().add(vale.getIva())));
        }
    }

    public void cambiaValorImponibleCero(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        DecimalFormat df = new DecimalFormat("0.00");
        if (vale.getImpuesto() != null) {
            BigDecimal ciento = new BigDecimal(100);
            vale.setIva(vale.getBaseimponible().multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
            //           vale.setTotalFactura(valor.add(vale.getBaseimponible().add(vale.getIva())));
        }
    }

    public String traeNroFactura(Valesfondos v) {
        String retorno = "";
        if (v != null) {
            String tipo = v.getTipodocumento() == null ? " " : v.getTipodocumento().getNombre();
            String nro = v.getNumero() == null ? " " : v.getNumero().toString();
            String establecimiento = v.getEstablecimiento() == null ? " " : v.getEstablecimiento();
            String ptoemision = v.getPuntoemision() == null ? " " : v.getPuntoemision();
            retorno = tipo + " - " + nro + " - " + ptoemision + " - " + establecimiento;
        }
        return retorno;
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
     * @return the listaVales
     */
    public LazyDataModel<Valesfondos> getListaVales() {
        return listaVales;
    }

    /**
     * @param listaVales the listaVales to set
     */
    public void setListaVales(LazyDataModel<Valesfondos> listaVales) {
        this.listaVales = listaVales;
    }

    /**
     * @return the vale
     */
    public Valesfondos getVale() {
        return vale;
    }

    /**
     * @param vale the vale to set
     */
    public void setVale(Valesfondos vale) {
        this.vale = vale;
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
     * @return the formularioImrpimir
     */
    public Formulario getFormularioImrpimir() {
        return formularioImrpimir;
    }

    /**
     * @param formularioImrpimir the formularioImrpimir to set
     */
    public void setFormularioImrpimir(Formulario formularioImrpimir) {
        this.formularioImrpimir = formularioImrpimir;
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
     * @return the detalles
     */
    public List<Auxiliar> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Auxiliar> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the detalle
     */
    public Auxiliar getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Auxiliar detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
    }

    public String nuevoDetalle() {
        detalle = new Auxiliar();
        detalle.setTotal(BigDecimal.ZERO);
        formularioDetalle.insertar();
        return null;
    }

    public String modificaDetalle(Auxiliar detalle) {
        this.detalle = detalle;
        formularioDetalle.editar();
        return null;
    }

    public String eliminaDetalle(Auxiliar detalle) {
        this.detalle = detalle;
        detalles.remove(detalle);
        return null;
    }
//    public String eliminaDetalle(int fila) {
//        detalles.remove(fila);
//        return null;
//    }

    public String insertarDetalle() {
        if ((detalle.getTitulo1() == null) || (detalle.getTitulo1().isEmpty())) {
            MensajesErrores.advertencia("Necesario concepto");
            return null;
        }
        if ((detalle.getTotal() == null) || (detalle.getTotal().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Necesario valor");
            return null;
        }
        detalles.add(detalle);
        formularioDetalle.cancelar();
        return null;
    }

    public String modificarDetalle() {
        if ((detalle.getTitulo1() == null) || (detalle.getTitulo1().isEmpty())) {
            MensajesErrores.advertencia("Necesario concepto");
            return null;
        }
        if ((detalle.getTotal() == null) || (detalle.getTotal().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Necesario valor");
            return null;
        }
//        detalles.add(detalle);
        formularioDetalle.cancelar();
        return null;
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

    public SelectItem[] getComboEmpleados() {
//        if (getFondo() == null) {
        if (fondo == null) {
            return null;
        }
//        if (getFondo().getDepartamento() == null) {
        if (fondo.getDepartamento() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cargoactual.organigrama=:organigrama";
//        parametros.put("organigrama", getFondo().getDepartamento());
        parametros.put("organigrama", fondo.getDepartamento());
        parametros.put(";where", where);
//        parametros.put(";orden", " o.codigo");
        try {
            return Combos.getSelectItems(ejbEmpleados.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboApertura() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.cerrado=false");
        parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
        try {
            return Combos.getSelectItems(ejbFondos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
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
     * @return the formularioProveedor
     */
    public Formulario getFormularioProveedor() {
        return formularioProveedor;
    }

    /**
     * @param formularioProveedor the formularioProveedor to set
     */
    public void setFormularioProveedor(Formulario formularioProveedor) {
        this.formularioProveedor = formularioProveedor;
    }

    /**
     * @return the proveedorsBean
     */
    public ProveedoresBean getProveedorsBean() {
        return proveedorsBean;
    }

    /**
     * @param proveedorsBean the proveedorsBean to set
     */
    public void setProveedorsBean(ProveedoresBean proveedorsBean) {
        this.proveedorsBean = proveedorsBean;
    }

    /**
     * @return the fondo
     */
    public Fondos getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
    }

    public String sube() {
        int ajuste = vale.getAjuste();
        ajuste++;
        vale.setAjuste(ajuste);
        return null;
    }

    public String baja() {
        int ajuste = vale.getAjuste();
        ajuste--;
        vale.setAjuste(ajuste);
        return null;
    }

    /**
     * @return the movilizacion
     */
    public boolean isMovilizacion() {
        return movilizacion;
    }

    /**
     * @param movilizacion the movilizacion to set
     */
    public void setMovilizacion(boolean movilizacion) {
        this.movilizacion = movilizacion;
    }
}
