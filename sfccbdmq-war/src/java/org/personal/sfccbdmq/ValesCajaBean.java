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
import java.util.Collections;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.ValeCaja;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.Formulario107Bean;
import org.contabilidad.sfccbdmq.ReporteFondosBean;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "valesCajaSfccbdmq")
@ViewScoped
public class ValesCajaBean {

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
    private Cajas caja;
    private Recurso reportepdf;
    private LazyDataModel<Valescajas> listaVales;
    private List<Auxiliar> detalles;
    private Auxiliar detalle;
    private Valescajas vale;
    private Formulario formulario = new Formulario();
    private Formulario formularioImrpimir = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioProveedor = new Formulario();
    private Formulario formularioVale = new Formulario();
    private Perfiles perfil;
    private int numeroVale = 0;
    private Resource reporteVale;
    private Codigos cod;
    private Recurso reporteV;
    private Recurso reporteARL;

    @EJB
    private ValescajasFacade ejbVales;
    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CodigosFacade ejbCodigos;

    public ValesCajaBean() {
        listaVales = new LazyDataModel<Valescajas>() {
            @Override
            public List<Valescajas> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
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
        String nombreForma = "ValeCajaVista";
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

    public List<Valescajas> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fecha desc, o.id desc");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        //String where = "  o.caja.empleado=:empleado and o.fecha between :desde and :hasta";
        String where = "  o.caja.empleado=:empleado and o.fecha between :desde and :hasta and o.caja.apertura is null";
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
        if (!((caja == null))) {
            where += " and o.caja=:caja";
            parametros.put("caja", caja);
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
        listaVales = new LazyDataModel<Valescajas>() {
            @Override
            public List<Valescajas> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, i1, scs, map);
            }
        };
        return null;
    }

    public boolean isPuedePedir() {

        // traer los datos que e necesita
//        PSC
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.liquidado=false and o.apertura is null");
        parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
        try {
            List<Cajas> lExtra = ejbCajas.encontarParametros(parametros);

            for (Cajas c : lExtra) {
//                setCaja(c);
                return true;
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        return getCaja() != null;
        return false;
    }

    public String crear() {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene caja chica aperturada o no esta pagado la aperura");
            return null;
        }

        if (caja == null) {
            MensajesErrores.advertencia("No existe Apertura");
            return null;
        }
        vale = new Valescajas();
        vale.setFecha(new Date());
        vale.setCaja(caja);
        vale.setBaseimponible(new BigDecimal(BigInteger.ZERO));
        vale.setBaseimponiblecero(new BigDecimal(BigInteger.ZERO));
        vale.setEstado(-1);
        vale.setNumero(0);
        try {
            cod = ejbCodigos.traerCodigo("NUM", "07");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para vales de caja");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
            Integer num = Integer.parseInt(cod.getParametros());
            Integer nume = num + 1;
            numeroVale = nume;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        detalles = new LinkedList<>();
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
        formulario.insertar();
        return null;
    }

    public String modificar(Valescajas va) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso generar un vale de caja");
            return null;
        }
        vale = va;
        try {
            cod = ejbCodigos.traerCodigo("NUM", "07");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para vales de caja");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
            Integer num = Integer.parseInt(cod.getParametros());
            Integer nume = num + 1;
            if (vale.getNumerovale() == null || vale.getNumerovale() == 0) {
                numeroVale = nume;
            } else {
                numeroVale = vale.getNumerovale();
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public String modificarProveedor(Valescajas vale) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso generar un vale de caja");
            return null;
        }
        this.vale = vale;
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

    public String getTotalCaja() {
        double valor = 0;
        Map parametros = new HashMap();
        String where = "o.caja=:caja and  o.caja.empleado=:empleado and o.fecha between :desde and :hasta";
        parametros.put("caja", caja);
        parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        try {
            List<Valescajas> aux = ejbVales.encontarParametros(parametros);
            for (Valescajas vc : aux) {
                valor += vc.getValor().doubleValue();
            }

            DecimalFormat df = new DecimalFormat("###,##0.00");
            return "Total caja: " + df.format(valor) + " / " + df.format(caja.getValor());

        } catch (ConsultarException ex) {
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
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
        if (detalles != null) {
            if (!detalles.isEmpty()) {
                for (Auxiliar d : detalles) {
                    valor += d.getTotal().doubleValue();
                }
            }
        }

        return valor;
    }

    public String eliminar(Valescajas vale) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso generar un vale de caja");
            return null;
        }
        this.vale = vale;
//        String[] aux = vale.getConcepto().split("#");
//        detalles = new LinkedList<>();
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
        if (numeroVale == 0) {
            MensajesErrores.advertencia("Ingresar número de Vale de Caja");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.numerovale=:numerovale");
            parametros.put("numerovale", numeroVale);
            int con = 0;
            con = ejbVales.contar(parametros);
            if (con > 0) {
//                MensajesErrores.advertencia("Número de Vale de Caja repetido");
//                return null;
                parametros = new HashMap();
                parametros.put(";where", "o.numerovale is not null");
                parametros.put(";orden", "o.numerovale desc");
                List<Valescajas> ultimoVale = ejbVales.encontarParametros(parametros);
                if (!ultimoVale.isEmpty()) {
                    Valescajas valeUltimo = ultimoVale.get(0);
                    int ultimoNumero = valeUltimo.getNumerovale();
                    numeroVale = ultimoNumero + 1;
                }
            }
            vale.setNumerovale(numeroVale);
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

//        if ((vale.getFecha() == null) || (vale.getFecha().before(configuracionBean.getConfiguracion().getPvigente()))) {
//            MensajesErrores.advertencia("Fecha de vale debe ser mayor a periodo vigente");
//            return null;
//        }
//            vale.setEstado(0);
            vale.setEstado(-1);
            vale.setValor(new BigDecimal(traerValor()));
            //            ES-2018-04-05 Control para el valor del vale
            if (controlPorcentajeVale(vale)) {
                return null;
            }
            vale.setSolicitante(seguridadbean.getLogueado().getEmpleados());
            vale.setSolicitante(empleadoBean.getEmpleadoSeleccionado());
            if (vale.getSolicitante() == null) {
                MensajesErrores.advertencia("Indique el solicitante");
                return null;
            }
            ejbVales.create(vale, seguridadbean.getLogueado().getUserid());
            cod.setParametros(vale.getNumerovale() + "");
            ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
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
        if (numeroVale == 0) {
            MensajesErrores.advertencia("Ingresar número de Vale de Caja");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.numero=:numero");
            parametros.put("numero", numeroVale);
            int con = 0;
            con = ejbVales.contar(parametros);
            if (con > 0) {
                parametros = new HashMap();
                parametros.put(";where", "o.numerovale is not null");
                parametros.put(";orden", "o.numerovale desc");
                List<Valescajas> ultimoVale = ejbVales.encontarParametros(parametros);
                if (!ultimoVale.isEmpty()) {
                    Valescajas valeUltimo = ultimoVale.get(0);
                    int ultimoNumero = valeUltimo.getNumerovale();
                    numeroVale = ultimoNumero + 1;
                }
            }
            vale.setNumerovale(numeroVale);
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
//            vale.setSolicitante(seguridadbean.getLogueado().getEmpleados());
            vale.setSolicitante(empleadoBean.getEmpleadoSeleccionado());
            ejbVales.edit(vale, seguridadbean.getLogueado().getUserid());
//            cod.setParametros(vale.getNumerovale() + "");
//            ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabarProveedor() {

        try {
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
            if (vale.getTipodocumento() == null) {
                MensajesErrores.advertencia("Seleccione tipo de documento");
                return null;
            }
            vale.setEstado(0);
            vale.setProveedor(proveedorsBean.getProveedor());
            BigDecimal ciento = new BigDecimal(100);
            if (vale.getBaseimponible() == null) {
                vale.setBaseimponible(new BigDecimal(BigInteger.ZERO));
            }
            if (vale.getBaseimponiblecero() == null) {
                vale.setBaseimponiblecero(new BigDecimal(BigInteger.ZERO));
            }
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
            double cuadre2 = Math.round(((vale.getBaseimponiblecero() == null ? 0 : vale.getBaseimponiblecero().doubleValue())
                    + vale.getBaseimponible().doubleValue() + vale.getIva().doubleValue()) * 100);
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double totalFactura = valortotal2.doubleValue();
//            double totalFactura = (vale.getBaseimponiblecero() == null ? 0 : vale.getBaseimponiblecero().doubleValue())
//                    + vale.getBaseimponible().doubleValue() + vale.getIva().doubleValue();
            double cuadre = Math.round(vale.getValor().doubleValue() * 100);
            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            double valeC = valortotal.doubleValue();
            if (totalFactura > valeC) {
                MensajesErrores.advertencia("El valor total ingresado no debe exceder al señalado en el vale");
                return null;
            }
            ejbVales.edit(vale, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        proveedorsBean.setProveedor(null);
        proveedorsBean.setRuc("");
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
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

//    public String reporte(Valescajas vale) {
//        this.vale = vale;
//
//        formularioImrpimir.editar();
//        try {
//            List<AuxiliarReporte> valores = new LinkedList<>();
//            List<AuxiliarReporte> titulos = new LinkedList<>();
//            //titulos
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PRODUCTO"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PARTIDA"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "FUENTE DE FINANCIAMIENTO"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "CÓDIGO"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "VALOR INCLUIDO IVA"));
//            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
//            DocumentoPDF pdf = new DocumentoPDF("VALE DE CAJA ", null, seguridadbean.getLogueado().getUserid());
//            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//            pdf.agregaParrafo("Fondos de caja Chica: " + getCaja().getId().toString(), AuxiliarReporte.ALIGN_RIGHT, 11, true);
//            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//            pdf.agregaParrafo("Fecha: Quito, " + sdf.format(vale.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
//            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//            pdf.agregaParrafo("Custodio del fondo:" + vale.getCaja().getEmpleado().toString(), AuxiliarReporte.ALIGN_LEFT, 11, false);
//            pdf.agregaParrafo("Unidad Administrativa:" + vale.getCaja().getDepartamento().toString(), AuxiliarReporte.ALIGN_LEFT, 11, false);
//            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//
//            pdf.agregaParrafo("\n\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//            valores = new LinkedList<>();
//            List<String> tit = new LinkedList<>();
//            tit.add("ENTREGA:");
//            tit.add("RECIBE:");
//            tit.add("AUTORIZDO:");
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
//            List<Codigos> aux = codigosBean.traerCodigoMaestro("DIREC");
//
//            pdf.agregarTabla(tit, valores, 3, 100, "", AuxiliarReporte.ALIGN_CENTER);
//            setReportepdf(pdf.traerRecurso());
//        } catch (IOException | DocumentException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    public String reporte(Valescajas vale) {
        this.vale = vale;
        String[] au = vale.getConcepto().split("#");
        detalles = new LinkedList<>();
        for (String auxInterior : au) {
            auxInterior = auxInterior.replace("|", "#");
            auxInterior = auxInterior.replace(".", "");
            auxInterior = auxInterior.replace(",", ".");
            //String[] datos = auxInterior.split("|");
            String[] datos = auxInterior.split("#");
            Auxiliar a = new Auxiliar();
            a.setTitulo1(datos[0]);
            a.setTotal(new BigDecimal(datos[1]));
            detalles.add(a);
        }

        formularioImrpimir.editar();
        try {
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> valoresDetalles = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            //titulos

            for (Auxiliar a : detalles) {
                valoresDetalles.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, a.getTitulo1()));
                valoresDetalles.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, a.getTotal().doubleValue()));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            DocumentoPDF pdf = new DocumentoPDF("VALE DE CAJA ", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fondos de caja Chica: " + vale.getCaja().getId().toString(), AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fecha: Quito, " + sdf.format(vale.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Custodio del fondo: " + vale.getCaja().getEmpleado().toString(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Unidad Administrativa: " + vale.getCaja().getDepartamento().toString(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

//            pdf.agregaParrafo("\n\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            List<String> tit2 = new LinkedList<>();
            tit2.add("CONCEPTO");
            tit2.add("MONTO");

            pdf.agregarTabla(tit2, valoresDetalles, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);

            valores = new LinkedList<>();
            List<String> tit = new LinkedList<>();
            tit.add("ENTREGA:");
            tit.add("RECIBE:");
            tit.add("AUTORIZADO POR:");
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            List<Codigos> aux = codigosBean.traerCodigoMaestro("DIREC");
            pdf.agregaParrafo("\n\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregarTabla(tit, valores, 3, 100, "", AuxiliarReporte.ALIGN_CENTER);
            setReportepdf(pdf.traerRecurso());
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean controlPorcentajeVale(Valescajas v) {
        double totalVales = 0;
        double totalRestoCaja = 0;
        double maxVale = v.getCaja().getValor().doubleValue() * (v.getCaja().getPrcvale().doubleValue() / 100);
        Map parametros = new HashMap();
        parametros.put(";where", "o.caja=:caja");
        parametros.put("caja", v.getCaja());
        try {
            List<Valescajas> aux = ejbVales.encontarParametros(parametros);
            List<Valescajas> aux1 = new LinkedList<>();
            if (v.getId() == null) {
                aux1 = ejbVales.encontarParametros(parametros);
            } else {
                for (Valescajas vc : aux) {
                    if (!vc.getId().equals(v.getId())) {
                        aux1.add(vc);
                    }
                }
            }
            for (Valescajas vc : aux1) {
                totalVales += vc.getValor().doubleValue();
            }
            //totalRestoCaja = v.getCaja().getValor().doubleValue() - totalVales;
            //double maxVale = totalRestoCaja * (v.getCaja().getPrcvale().doubleValue() / 100);
//            if (v.getValor().doubleValue() > maxVale) {
//                MensajesErrores.advertencia("El valor del vale no puede exceder el " + v.getCaja().getPrcvale().doubleValue() + "% del total de caja chica");
//                return true;
//            }
            totalVales += v.getValor().doubleValue();

            double cuadre3 = Math.round(totalVales);
            double dividido3 = cuadre3 / 100;
            BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
            double totalVales1 = (valortotal3.doubleValue());

            double cuadre2 = Math.round(v.getCaja().getValor().doubleValue());
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double valorCaja = (valortotal2.doubleValue());

            if (totalVales1 > valorCaja) {
                MensajesErrores.advertencia("El valor del vale excede el monto total de la caja");
                return true;
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String traeNroFactura(Valescajas v) {
        String retorno = "";
        if (v != null) {
            String tipo = v.getTipodocumento() == null ? " " : v.getTipodocumento().getNombre();
            String nro = v.getNumero() == null ? " " : v.getNumero().toString();
            String establecimiento = v.getEstablecimiento() == null ? " " : v.getEstablecimiento();
            String ptoemision = v.getPuntoemision() == null ? " " : v.getPuntoemision();
            retorno = tipo + " - " + establecimiento + " - " + ptoemision + " - " + nro;
        }
        return retorno;
    }

    public SelectItem[] getComboApertura() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.apertura is null and o.liquidado=false");
        parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
        try {
            return Combos.getSelectItems(ejbCajas.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirSolicitud() {
        try {
            reporteVale = null;
            reporteV = null;
            if (caja == null) {
                MensajesErrores.advertencia("Seleccione una Apertura de Caja chica");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.estado=1 and o.caja.empleado=:empleado and o.caja=:caja");
            parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
            parametros.put("caja", caja);
            List<Valescajas> lista = ejbVales.encontarParametros(parametros);
            if (lista.isEmpty()) {
                MensajesErrores.advertencia("No existe Vales en Reembolso");
                return null;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            org.auxiliares.sfccbdmq.DocumentoPDF pdf = new org.auxiliares.sfccbdmq.DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD",
                    null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("SOLUCITUD DE APERTURA, REPOSICIÓN O LIQUIDACIÓN DEL FONDO FIJO DE CAJA CHICA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "FORMULARIO AF-1"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 7, false, " No "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, caja.getNumeroreposicion() != null ? caja.getNumeroreposicion() + "" : ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            columnas = new LinkedList<>();
            Date fecha;
            if (caja.getFechareembolso() == null) {
                fecha = new Date();
            } else {
                fecha = caja.getFechareembolso();
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Quito, " + sdf.format(fecha)));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Para :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, caja.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "De Estación:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, caja.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Unidad Administrativa"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaTitulo("\n");
            columnas = new LinkedList<>();
            double valor = 0;
            for (Valescajas vc : lista) {
                valor += vc.getValor().doubleValue();
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "APERTURA "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "REPOSICIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "X"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO SOLICITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(valor)));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "LIQUIDAIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO DEPOSITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            pdf.agregaTitulo("\n");
            if (!lista.isEmpty()) {
                List<AuxiliarReporte> titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO DE DOCUMENTO (Factura,nota de Venta,Formulario AF-2,recibo)"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "NÚMERO"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "FECHA"));
                titulos.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, true, "CONCEPTO"));
                titulos.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROVEEDOR"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "VALOR\nDESEMBOLSADO"));

                for (Valescajas vc : lista) {
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getTipodocumento().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getNumero().toString()));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(vc.getFecha())));
                    columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getConcepto()));
                    columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getProveedor() != null ? (vc.getProveedor().getEmpresa() != null ? vc.getProveedor().getEmpresa().toString() : "") : ""));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(vc.getValor())));
                }

                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, "Valor Total Utilizado "));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(valor)));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, null);
            }

            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("OBSERVACIONES: " + caja.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 6, Boolean.FALSE);
            pdf.agregaTitulo("\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "RESPONSABLE DEL FONDO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "JEFE INMEDIATO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre: " + caja.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "cc: " + caja.getEmpleado().getEntidad().getPin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "cc: "));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            setReporteARL(pdf.traerRecurso());
            formularioVale.insertar();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
    public LazyDataModel<Valescajas> getListaVales() {
        return listaVales;
    }

    /**
     * @param listaVales the listaVales to set
     */
    public void setListaVales(LazyDataModel<Valescajas> listaVales) {
        this.listaVales = listaVales;
    }

    /**
     * @return the vale
     */
    public Valescajas getVale() {
        return vale;
    }

    /**
     * @param vale the vale to set
     */
    public void setVale(Valescajas vale) {
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
        if (!detalles.isEmpty()) {
            MensajesErrores.advertencia("Solo se puede ingresar un vale");
            return null;
        }
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
        if (caja == null) {
            return null;
        }
        if (caja.getDepartamento() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cargoactual.organigrama=:organigrama";
        parametros.put("organigrama", caja.getDepartamento());
        parametros.put(";where", where);
        parametros.put(";orden", " o.entidad.apellidos, o.entidad.nombres");
        try {
            return Combos.getSelectItems(ejbEmpleados.encontarParametros(parametros), true);
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
     * @return the caja
     */
    public Cajas getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(Cajas caja) {
        this.caja = caja;
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
     * @return the numeroVale
     */
    public int getNumeroVale() {
        return numeroVale;
    }

    /**
     * @param numeroVale the numeroVale to set
     */
    public void setNumeroVale(int numeroVale) {
        this.numeroVale = numeroVale;
    }

    public String grabarEnHoja(Valescajas val) {
        try {
            if (val.getNumerovale() == null) {
                val.setNumerovale(0);
            }
            ValeCaja hoja = new ValeCaja();
            hoja.llenar(val);
            reporteVale = hoja.traerRecurso();
            formularioVale.insertar();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirVales() {
        if (caja == null) {
            MensajesErrores.advertencia("Seleccione una Apertura de Caja chica");
            return null;
        }
        buscar();
        reporteARL = null;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", caja);
            List<Valescajas> lista = ejbVales.encontarParametros(parametros);
            if (lista.isEmpty()) {
                MensajesErrores.advertencia("No existen vales de caja");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            org.auxiliares.sfccbdmq.DocumentoPDF pdf = new org.auxiliares.sfccbdmq.DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD",
                    null, seguridadbean.getLogueado().getUserid());
            boolean segunda = false;
            for (Valescajas vc : lista) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
                if (segunda) {
                    pdf.finDePagina();
                }
                segunda = true;
                SimpleDateFormat dia = new SimpleDateFormat("dd");
                SimpleDateFormat mes = new SimpleDateFormat("MM");
                SimpleDateFormat anio = new SimpleDateFormat("yyyy");
                if (vc.getFecha() == null) {
                    vc.setFecha(new Date());
                }
                if (vc.getValor() == null) {
                    vc.setValor(BigDecimal.ZERO);
                }
                pdf.agregaParrafoCompleto("VALE DE CAJA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
                pdf.agregaParrafoCompleto("FONDOS DE CAJA CHICA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.FALSE);
                pdf.agregaParrafoCompleto("N° " + (vc.getNumerovale() != null ? vc.getNumerovale() : ""), AuxiliarReporte.ALIGN_RIGHT, 9, Boolean.FALSE);
                pdf.agregaTitulo("\n");

                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, "Custodio del Fondo :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, vc.getCaja().getEmpleado() != null ? vc.getCaja().getEmpleado().toString() : ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, "Unidad Administrativa :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, vc.getCaja().getDepartamento() != null ? vc.getCaja().getDepartamento().toString() : ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, "Solicitante :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, vc.getSolicitante() != null ? vc.getSolicitante().toString() : ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, "Dia: " + dia.format(vc.getFecha()) + " Mes: " + mes.format(vc.getFecha()) + " Año : " + anio.format(vc.getFecha())));
                pdf.agregarTabla(null, columnas, 2, 100, null);
                pdf.agregaParrafo("\n\n");
                List<AuxiliarReporte> titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, true, "CONCEPTO"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 9, true, "MONTO"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 9, false, vc.getConcepto() != null ? vc.getConcepto() : ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 9, false, df.format(vc.getValor())));
                pdf.agregarTablaReporte(titulos, columnas, 2, 100, null);
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafoCompleto("Valor en Letras: " + (ConvertirNumeroALetras.convertNumberToLetter(vc.getValor().doubleValue())), AuxiliarReporte.ALIGN_LEFT, 9, Boolean.FALSE);
                pdf.agregaParrafo("\n\n\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "(firma y nombre del custodio)"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "(firma y nombre)"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, true, "ENTREGA"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, true, "RECIBE"));
                pdf.agregarTabla(null, columnas, 2, 100, null);
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_CENTER, 9, false, "__________________________________"));
                columnas.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 6, false, "JEFE INMEDIATO"));
                columnas.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_CENTER, 9, true, "AUTORIZADO POR:"));
                pdf.agregarTabla(null, columnas, 1, 100, null);
            }
            reporteV = pdf.traerRecurso();
            formularioVale.insertar();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the formularioVale
     */
    public Formulario getFormularioVale() {
        return formularioVale;
    }

    /**
     * @param formularioVale the formularioVale to set
     */
    public void setFormularioVale(Formulario formularioVale) {
        this.formularioVale = formularioVale;
    }

    /**
     * @return the reporteVale
     */
    public Resource getReporteVale() {
        return reporteVale;
    }

    /**
     * @param reporteVale the reporteVale to set
     */
    public void setReporteVale(Resource reporteVale) {
        this.reporteVale = reporteVale;
    }

    /**
     * @return the cod
     */
    public Codigos getCod() {
        return cod;
    }

    /**
     * @param cod the cod to set
     */
    public void setCod(Codigos cod) {
        this.cod = cod;
    }

    /**
     * @return the reporteV
     */
    public Recurso getReporteV() {
        return reporteV;
    }

    /**
     * @param reporteV the reporteV to set
     */
    public void setReporteV(Recurso reporteV) {
        this.reporteV = reporteV;
    }

    /**
     * @return the reporteARL
     */
    public Recurso getReporteARL() {
        return reporteARL;
    }

    /**
     * @param reporteARL the reporteARL to set
     */
    public void setReporteARL(Recurso reporteARL) {
        this.reporteARL = reporteARL;
    }
}
