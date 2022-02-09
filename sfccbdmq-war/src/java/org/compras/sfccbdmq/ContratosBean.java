/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.TrackingcontratoFacade;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Trackingcontrato;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "contratosSfccbdmq")
@ViewScoped
public class ContratosBean {

    /**
     * Creates a new instance of ContratosBean
     */
    public ContratosBean() {
    }
    private Contratos contrato;
    private Contratos contratoBusca;
    private List<Contratos> contratos;
    private List<Contratos> contratosActuales;
    private List<Contratos> contratosAnteriores;
    private LazyDataModel<Contratos> listacontratos;

    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Perfiles perfil;
    private Boolean tieneAnticipo;
    private Calendar fecha = Calendar.getInstance();
    private BigDecimal valorAnticipo;
    private Trackingcontrato tackingContra;
    private String numero;

    @EJB
    private ContratosFacade ejbContratos;
    @EJB
    private TrackingcontratoFacade ejbTrackingcontrato;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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

//    private List<Contratos> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
//        String where = "o.fcierre is null and o.padre is null ";
//
//        Map parametros = new HashMap();
//        for (Map.Entry e : map.entrySet()) {
//            String clave = (String) e.getKey();
//            String valor = (String) e.getValue();
//
//            where += " and upper(o." + clave + ") like :" + clave;
//            parametros.put(clave, valor.toUpperCase() + "%");
//        }
//        int total;
//        try {
//            if (proveedorBean.getProveedor() != null) {
//                parametros.put(";where", "o.proveedor=:proveedor ");
//                parametros.put("proveedor", proveedorBean.getProveedor());
//            }
//
//            parametros.put(";where", where);
//
//            parametros.put("desde", proveedorBean.getFechaInicio());
//            parametros.put("hasta", proveedorBean.getFechaFin());
//            parametros.put(";orden", "o.id desc");
//            total = ejbContratos.contar(parametros);
//            int endIndex = i + pageSize;
//            if (endIndex > total) {
//                endIndex = total;
//            }
//            parametros.put(";inicial", i);
//            parametros.put(";final", endIndex);
//            listacontratos.setRowCount(total);
//            return ejbContratos.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    public String buscar() {
//
//        listacontratos = new LazyDataModel<Contratos>() {
//
//            @Override
//            public List<Contratos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
//                return cargar(i, i1, scs, map);
//            }
//        };
//
//        if (proveedorBean.getProveedor() == null) {
//            setContratos(null);
//            MensajesErrores.advertencia("Seleccione un proveedor primero");
//            return null;
//        }
//
//        if (proveedorBean.getFechaInicio() == null) {
//
//            MensajesErrores.advertencia("Seleccione una fecha de inicio");
//            return null;
//        }
//
//        if (proveedorBean.getFechaFin() == null) {
//          
//            MensajesErrores.advertencia("Seleccione una fecha de fin");
//            return null;
//        }
//
//        return null;
//
//    }
    public String buscar() throws ConsultarException {
        Contratos con = null;
        if (proveedorBean.getProveedor() == null) {
            contratos = null;
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return null;
        }

        if (numero != null && !numero.trim().isEmpty()) {
            // numero de contrato
            Map parametros = new HashMap();
            parametros.put(";where", "o.numero=:numero and o.padre is null");
            parametros.put("numero", numero);
            List<Contratos> lista = ejbContratos.encontarParametros(parametros);
            for (Contratos c : lista) {
                con = c;
            }
        }

        if (contratoBusca == null) {
            MensajesErrores.advertencia("Seleccione una contrato primero");
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null and o.padre is null ");
        parametros.put("proveedor", proveedorBean.getProveedor());

        parametros = new HashMap();

        parametros.put(";where", "o.id=:id");
        parametros.put("id", contratoBusca.getId());

        parametros.put(";orden", "o.fin desc");
        try {
            contratos = ejbContratos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {

        if (proveedorBean.getProveedor() == null) {

            MensajesErrores.advertencia("Seleccione un contrato es primero");
            return null;
        }
        formulario.insertar();
        contrato = new Contratos();
        contrato.setProveedor(proveedorBean.getProveedor());
        contrato.setNumeroDias(0);
        contrato.setSubtotal(new BigDecimal(BigInteger.ZERO));
        contrato.setIva(new BigDecimal(BigInteger.ZERO));
        contrato.setFirma(new Date());
        contrato.setEsfirma(true);
//        ES-2018-03-14 SE AGREGO UN CAMPO PARA EL PORCENTAJE DE ANTICIPO
        contrato.setSaldoanterior(BigDecimal.ZERO);
        contrato.setPctanticipo(new BigDecimal(BigInteger.ZERO));
        entidadesBean.setApellidos(null);
        entidadesBean.setEntidad(null);
//        imagenesBean.setIdModulo(proveedorBean.getProveedor().getId());
//        imagenesBean.setModulo("CONTRATO");
//        imagenesBean.Buscar();
        tieneAnticipo = true;

        return null;
    }

    public String modificar(Contratos contrat) {

        this.contrato = contrat;
        if (contrato.getEsfirma() == null) {
            contrato.setEsfirma(false);
        }
        if (contrato.getSubtotal() == null) {
            contrato.setSubtotal(new BigDecimal(BigInteger.ZERO));
        }
        if (contrato.getIva() == null) {
            contrato.setIva(new BigDecimal(BigInteger.ZERO));
        }

        if (contrato.getPctanticipo() == null) {
            contrato.setPctanticipo(new BigDecimal(BigInteger.ZERO));
        }

        formulario.editar();
        entidadesBean.setApellidos(contrato.getAdministrador().getApellidos());
        entidadesBean.setEntidad(contrato.getAdministrador());
        // ver el numero e dias
        tieneAnticipo = (contrato.getAnticipo() != null);
        Calendar fAnticipo = Calendar.getInstance();
        Calendar fFirma = Calendar.getInstance();
        Calendar fFin = Calendar.getInstance();

        fFirma.setTime(contrato.getFirma());
        if (contrato.getFechaanticipo() != null) {
            fAnticipo.setTime(contrato.getFechaanticipo());
        }
        fFin.setTime(contrato.getFin());
        if (contrato.getEsfirma()) {
//            fAnticipo.setTime(contrato.getFirma());
            int dias = (int) ((fFin.getTimeInMillis() - fFirma.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        } else {
            int dias = (int) ((fFin.getTimeInMillis() - fAnticipo.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        }
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("CONTRATO");
        imagenesBean.Buscar();

//        diferencia en dias
        return null;
    }

    public String imprimir(Contratos contrat) {

        this.contrato = contrat;
        if (contrato.getEsfirma() == null) {
            contrato.setEsfirma(false);
        }
        formularioImprimir.editar();
        entidadesBean.setApellidos(contrato.getAdministrador().getApellidos());
        entidadesBean.setEntidad(contrato.getAdministrador());
        // ver el numero e dias
        tieneAnticipo = (contrato.getAnticipo() != null);
        Calendar fAnticipo = Calendar.getInstance();
        Calendar fFirma = Calendar.getInstance();
        Calendar fFin = Calendar.getInstance();

        fFirma.setTime(contrato.getFirma());
        if (contrato.getFechaanticipo() != null) {
            fAnticipo.setTime(contrato.getFechaanticipo());
        }
        fFin.setTime(contrato.getFin());
        if (contrato.getEsfirma()) {
//            fAnticipo.setTime(contrato.getFirma());
            int dias = (int) ((fFin.getTimeInMillis() - fFirma.getTimeInMillis()) / 86400000);
            contrato.setNumeroDias(dias);
        } else {
            int dias = (int) ((fFin.getTimeInMillis() - fAnticipo.getTimeInMillis()) / 86400000);
            contrato.setNumeroDias(dias);
        }
        imagenesBean.setIdModulo(contrato.getProveedor().getId());
        imagenesBean.setModulo("CONTRATO");
        imagenesBean.Buscar();
//        diferencia en dias
        return null;
    }

    public String liquidar(Contratos contrat) {

        this.contrato = contrat;
        if (contrato.getEstado() > 3) {
            MensajesErrores.advertencia("No se puede finalizar, no se encuentra en ejecición");
        }
        if (contrato.getEsfirma() == null) {
            contrato.setEsfirma(false);
        }
        contrato.setEstado(3);
//        formulario.editar();
        entidadesBean.setApellidos(contrato.getAdministrador().getApellidos());
        entidadesBean.setEntidad(contrato.getAdministrador());
        // ver el numero e dias
        tieneAnticipo = (contrato.getAnticipo() != null);
        Calendar fAnticipo = Calendar.getInstance();
        Calendar fFirma = Calendar.getInstance();
        Calendar fFin = Calendar.getInstance();

        fFirma.setTime(contrato.getFirma());
        if (contrato.getFechaanticipo() != null) {
            fAnticipo.setTime(contrato.getFechaanticipo());
        }
        fFin.setTime(contrato.getFin());
        contrato.setFcierre(new Date());
        if (contrato.getEsfirma()) {
//            fAnticipo.setTime(contrato.getFirma());
            int dias = (int) ((fFin.getTimeInMillis() - fFirma.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        } else {
            int dias = (int) ((fFin.getTimeInMillis() - fAnticipo.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        }
        imagenesBean.setIdModulo(contrato.getProveedor().getId());
        imagenesBean.setModulo("CONTRATO");
        imagenesBean.Buscar();
        MensajesErrores.advertencia("Contrato Liquidado");
//        diferencia en dias
        return null;
    }

    public String TerminarMutuoAcuerdo(Contratos contrat) {

        this.contrato = contrat;
        if (contrato.getEstado() > 3) {
            MensajesErrores.advertencia("No se puede finalizar, no se encuentra en ejecición");
        }
        if (contrato.getEsfirma() == null) {
            contrato.setEsfirma(false);
        }
        contrato.setEstado(4);
//        formulario.editar();
        entidadesBean.setApellidos(contrato.getAdministrador().getApellidos());
        entidadesBean.setEntidad(contrato.getAdministrador());
        // ver el numero e dias
        tieneAnticipo = (contrato.getAnticipo() != null);
        Calendar fAnticipo = Calendar.getInstance();
        Calendar fFirma = Calendar.getInstance();
        Calendar fFin = Calendar.getInstance();

        fFirma.setTime(contrato.getFirma());
        if (contrato.getFechaanticipo() != null) {
            fAnticipo.setTime(contrato.getFechaanticipo());
        }
        fFin.setTime(contrato.getFin());
        contrato.setFcierre(new Date());
        if (contrato.getEsfirma()) {
//            fAnticipo.setTime(contrato.getFirma());
            int dias = (int) ((fFin.getTimeInMillis() - fFirma.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        } else {
            int dias = (int) ((fFin.getTimeInMillis() - fAnticipo.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        }
        imagenesBean.setIdModulo(contrato.getProveedor().getId());
        imagenesBean.setModulo("CONTRATO");
        imagenesBean.Buscar();
        MensajesErrores.advertencia("Contrato terminado Mutuo Acuerdo");
//        diferencia en dias
        return null;
    }

    public String TerminarUnilateral(Contratos contrat) {

        this.contrato = contrat;
        if (contrato.getEstado() > 3) {
            MensajesErrores.advertencia("No se puede finalizar, no se encuentra en ejecición");
        }
        if (contrato.getEsfirma() == null) {
            contrato.setEsfirma(false);
        }
        contrato.setEstado(4);
//        formulario.editar();
        entidadesBean.setApellidos(contrato.getAdministrador().getApellidos());
        entidadesBean.setEntidad(contrato.getAdministrador());
        // ver el numero e dias
        tieneAnticipo = (contrato.getAnticipo() != null);
        Calendar fAnticipo = Calendar.getInstance();
        Calendar fFirma = Calendar.getInstance();
        Calendar fFin = Calendar.getInstance();

        fFirma.setTime(contrato.getFirma());
        if (contrato.getFechaanticipo() != null) {
            fAnticipo.setTime(contrato.getFechaanticipo());
        }
        fFin.setTime(contrato.getFin());
        contrato.setFcierre(new Date());
        if (contrato.getEsfirma()) {
//            fAnticipo.setTime(contrato.getFirma());
            int dias = (int) ((fFin.getTimeInMillis() - fFirma.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        } else {
            int dias = (int) ((fFin.getTimeInMillis() - fAnticipo.getTimeInMillis()) / 86400000) - 1;
            contrato.setNumeroDias(dias);
        }
        imagenesBean.setIdModulo(contrato.getProveedor().getId());
        imagenesBean.setModulo("CONTRATO");
        imagenesBean.Buscar();
        MensajesErrores.advertencia("Contrato terminado Unilateralmente");
//        diferencia en dias
        return null;
    }

    public String eliminar(Contratos contrat) {
        this.contrato = contrat;
        formulario.eliminar();
        entidadesBean.setApellidos(contrato.getAdministrador().getApellidos());
        entidadesBean.setEntidad(contrato.getAdministrador());
        return null;
    }

    //    FM 18 Sep 2018
    public void cambiaadministradorcontrato(ValueChangeEvent event) {

        if (getContratos() == null) {
            return;
        }
        contrato = null;

        String newWord = (String) event.getNewValue();
        for (Contratos c : getContratos()) {
            String aComparar = c.getAdministrador().getNombres();

            if (aComparar.compareToIgnoreCase(newWord) == 0) {
                contrato = c;
            }
        }
    }

    public void administradorChangeEventHandler(TextChangeEvent event) {
        // Buscar por nombre
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;
        Map parametros = new HashMap();
        String where = "  ";

//        where = " o.estado=:estado";
//        parametros.Cput("estado", true);
        //nombre
        where += " upper(o.administrador.nombres) like :nombre";
        parametros.put("nombre", "%" + newWord.toUpperCase() + "%");
        parametros.put(";orden", "o.administrador.nombres");
        try {
            parametros.put(";where", where);
            int total = 15;
//            int total = ((SelectInputText) event.getComponent()).getRows();
            // Contadores
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            setContratos(ejbContratos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

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

    private boolean validar() {
        if (contrato.getSubtotal() == null) {
            contrato.setSubtotal(new BigDecimal(BigInteger.ZERO));
        }
        if (contrato.getIva() == null) {
            contrato.setIva(new BigDecimal(BigInteger.ZERO));
        }

        if ((contrato.getSubtotal().doubleValue() + contrato.getIva().doubleValue() == 0)) {
            MensajesErrores.advertencia("Es necesario Base Imponible");
            return true;
        }
        double subTotal = contrato.getSubtotal().doubleValue();
        double iva = contrato.getIva() == null ? 0 : contrato.getIva().doubleValue();
        double total = subTotal + iva + iva * .12;
        contrato.setValor(new BigDecimal(total));

//        contrato.setValor(BigDecimal.ZERO);
        if ((contrato.getNumero() == null) || (contrato.getNumero().isEmpty())) {
            MensajesErrores.advertencia("Es necesario número de contrato");
            return true;
        }
        if ((contrato.getObjeto() == null) || (contrato.getObjeto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario objeto de contrato");
            return true;
        }
        if ((contrato.getTitulo() == null) || (contrato.getTitulo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario objeto de contrato");
            return true;
        }
        if ((contrato.getAdministrador() == null)) {
            MensajesErrores.advertencia("Es necesario administrador de contrato");
            return true;
        }
//        if ((contrato.getDireccion()== null)) {
//            MensajesErrores.advertencia("Es necesario direccion responsable del contrato");
//            return true;
//        }
        if ((contrato.getInicio() == null)) {
            MensajesErrores.advertencia("Es necesario Fecha de inicio");
            return true;
        }
//        if ((contrato.getInicio().before(configuracionBean.getConfiguracion().getPvigente()))) {
//            MensajesErrores.advertencia("Es necesario Fecha de inicio mayor que periodo vigente");
//            return true;
//        }
//        if ((contrato.getInicio() == null)) {
//            MensajesErrores.advertencia("Es necesario Fecha de inicio");
//            return true;
//        }
//        if ((contrato.getFin() == null)) {
//            MensajesErrores.advertencia("Es necesario Fecha de fin");
//            return true;
//        }
        if ((contrato.getFirma() == null)) {
            MensajesErrores.advertencia("Es necesario fecha de firma");
            return true;
        }
        if ((contrato.getInicio().after(contrato.getFin()))) {
            MensajesErrores.advertencia("Fecha de inicio de de ser antes de fecha de fin");
            return true;
        }
//        if ((contrato.getInicio().after(contrato.getFirma()))) {
//            MensajesErrores.advertencia("Fecha de inicio de de ser antes de fecha de firma");
//            return true;
//        }
//        if ((contrato.getFirma().after(contrato.getFin()))) {
//            MensajesErrores.advertencia("Fecha de firma de de ser antes de fecha de fin");
//            return true;
//        }
//        if ((contrato.getCertificacion()==null) ){
//            MensajesErrores.advertencia("Es necesario certificación de contrato");
//            return true;
//        }
        if ((contrato.getAdministrador() == null)) {
            MensajesErrores.advertencia("Es necesario administrador de contrato");
            return true;
        }
        if ((contrato.getNumeroDias() == null) || (contrato.getNumeroDias() <= 0)) {
            MensajesErrores.advertencia("Es necesario Número de días de contrato");
            return true;
        }
        if (contrato.getValor().doubleValue() < 0) {
            MensajesErrores.advertencia("Valor debe ser mayor o igual a cero");
            return true;
        }
        if (tieneAnticipo) {
            if ((contrato.getFechaanticipo() == null)) {
                MensajesErrores.advertencia("Necesario fecha de anticipo");
                return true;
            }
            if ((contrato.getAnticipo() == null)) {
                MensajesErrores.advertencia("Necesario valor del anticipo");
                return true;
            }
            double valor = contrato.getValor().doubleValue();
            double anticipo = contrato.getAnticipo().doubleValue();
            if (valor <= anticipo) {
                MensajesErrores.advertencia("Valor debe ser mayor al valor del anticipo");
                return true;
            }

            if ((contrato.getPctanticipo() == null)) {
                MensajesErrores.advertencia("Necesario porcentaje de anticipo");
                return true;
            }
        }
        // numero de be seer unico
        if (formulario.isNuevo()) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.numero=:numero");
            parametros.put("numero", contrato.getNumero());
            try {
                int total1 = ejbContratos.contar(parametros);
                if (total1 > 0) {
                    MensajesErrores.advertencia("Número de contrato ya utilizado");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger
                        .getLogger(ContratosBean.class
                                .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() throws ConsultarException {
        Trackingcontrato tc = new Trackingcontrato();
        contrato.setAdministrador(entidadesBean.getEntidad());
        if (validar()) {
            return null;
        }
        contrato.setEstado(0);
        Calendar c = Calendar.getInstance();
        if (contrato.getEsfirma() == null) {
            contrato.setEsfirma(false);
        }
        if (tieneAnticipo) {
            if (contrato.getFin() == null) {
                if (contrato.getEsfirma()) {
                    c.setTime(contrato.getFirma());
                    c.add(Calendar.DATE, contrato.getNumeroDias());
                    contrato.setFin(c.getTime());

                } else {
                    c.setTime(contrato.getFechaanticipo());
                    c.add(Calendar.DATE, contrato.getNumeroDias());
                    contrato.setFin(c.getTime());
                }
            }
        } else {
            if (contrato.getFin() == null) {
                c.setTime(contrato.getFirma());
                c.add(Calendar.DATE, contrato.getNumeroDias());
                contrato.setFin(c.getTime());
            }
        }
        try {
            // ver si es unico
            Map parametros = new HashMap();
            parametros.put(";where", "o.numero=:numero");
            parametros.put("numero", contrato.getNumero());
            int total = ejbContratos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Número de contrato ya repetido");
                return null;
            }
            // buscar si existe clasiificador ya con esa fuente en ese proyecto

            contrato.setProveedor(proveedorBean.getProveedor());
            ejbContratos.create(contrato, seguridadbean.getLogueado().getUserid());
            if (contrato.getIva() != null) {
                if (contrato.getIva().doubleValue() == 0) {
                    contrato.setTieneiva(Boolean.FALSE);
                } else {
                    contrato.setTieneiva(Boolean.TRUE);
                }
            } else {
                contrato.setTieneiva(Boolean.FALSE);
            }
            ejbContratos.edit(contrato, seguridadbean.getLogueado().getUserid());
            tc.setFecha(fecha.getTime());
            tc.setUsuario(seguridadbean.getLogueado().toString());
            tc.setContrato(contrato);
            tc.setAdministradorcontrato(contrato.getAdministrador().toString());
            ejbTrackingcontrato.create(tc, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() throws InsertarException, ConsultarException {
        Trackingcontrato tc = new Trackingcontrato();

        if (validar()) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        if (contrato.getEsfirma() == null) {
            contrato.setEsfirma(false);
        }
//        if (tieneAnticipo) {
//            if (contrato.getEsfirma()) {
//                c.setTime(contrato.getFirma());
//                c.add(Calendar.DATE, contrato.getNumeroDias() + 1);
//                contrato.setFin(c.getTime());
//            } else {
//                c.setTime(contrato.getFechaanticipo());
//                c.add(Calendar.DATE, contrato.getNumeroDias() + 1);
//                contrato.setFin(c.getTime());
//            }
//        } else {
//            c.setTime(contrato.getFirma());
//            c.add(Calendar.DATE, contrato.getNumeroDias() + 1);
//            contrato.setFin(c.getTime());
//        }
        try {
            contrato.setAdministrador(entidadesBean.getEntidad());
            if (contrato.getIva() != null) {
                if (contrato.getIva().doubleValue() == 0) {
                    contrato.setTieneiva(Boolean.FALSE);
                } else {
                    contrato.setTieneiva(Boolean.TRUE);
                }
            } else {
                contrato.setTieneiva(Boolean.FALSE);
            }
            ejbContratos.edit(contrato, seguridadbean.getLogueado().getUserid());
            tc.setFecha(fecha.getTime());
            tc.setUsuario(seguridadbean.getLogueado().toString());
            tc.setContrato(contrato);
            tc.setAdministradorcontrato(contrato.getAdministrador().getNombres());

            ejbTrackingcontrato.create(tc, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() throws ConsultarException {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", contrato);
            List<Trackingcontrato> lista = ejbTrackingcontrato.encontarParametros(parametros);
            for (Trackingcontrato tc : lista) {
                tc.setContrato(null);
                tc.setObservacion("Contrato N° " + contrato.getNumero() + "con id: " + contrato.getId() + "eliminado");
                ejbTrackingcontrato.edit(tc, seguridadbean.getLogueado().getUserid());
            }

            ejbContratos.remove(contrato, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
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

    public Contratos traer(Integer id) throws ConsultarException {
        return ejbContratos.find(id);
    }

    public SelectItem[] getComboContratos() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null");
        parametros.put("proveedor", proveedorBean.getProveedor());
        parametros.put(";orden", "o.fin desc");
        try {
            setContratos(ejbContratos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(getContratos(), true);
    }

    public SelectItem[] getComboContratosverdadero() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        Map parametros = new HashMap();
//        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null");
        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null");
        parametros.put("proveedor", proveedorBean.getProveedor());

        parametros.put(";orden", "o.fin desc");
        try {
            setContratos(ejbContratos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(getContratos(), true);
    }

    public SelectItem[] getComboPadre() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null and o.padre is null");
        parametros.put("proveedor", proveedorBean.getProveedor());
        parametros.put(";orden", "o.fin desc");
        try {
            setContratos(ejbContratos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(getContratos(), false);
    }

    public SelectItem[] getComboContratosProveedor() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null");
        parametros.put("proveedor", proveedorBean.getProveedor());
        parametros.put(";orden", "o.fin desc");
        try {
            setContratos(ejbContratos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(getContratos(), true);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ContratosVista";
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
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

//    public long getDiasAnticipo() {
//        Contratos c = contratos.get(formulario.getFila().getRowIndex());
//        if (c.getFechaanticipo() == null) {
//            return 0;
//        }
//        if (c.getFirma() == null) {
//            return 0;
//        }
//        Calendar fAnticipo = Calendar.getInstance();
//        Calendar fFirma = Calendar.getInstance();
//        fAnticipo.setTime(c.getInicio());
//        fFirma.setTime(c.getFin());
//        return (fAnticipo.getTimeInMillis() - fFirma.getTimeInMillis()) / 86400000;
//    }
    public void cambiaDias(ValueChangeEvent ve) {
        Integer valor = (Integer) ve.getNewValue();
        Calendar c = Calendar.getInstance();
        boolean esFirma = contrato.getEsfirma() == null ? false : contrato.getEsfirma();
        if (tieneAnticipo) {
            if (esFirma) {
                c.setTime(contrato.getFirma() == null ? new Date() : contrato.getFirma());
                c.add(Calendar.DATE, valor);
                contrato.setFin(c.getTime());
            } else {
                c.setTime(contrato.getFechaanticipo() == null ? new Date() : contrato.getFechaanticipo());
                c.add(Calendar.DATE, valor);
                contrato.setFin(c.getTime());
            }
        } else {
            c.setTime(contrato.getFirma() == null ? new Date() : contrato.getFirma());
            c.add(Calendar.DATE, valor);
            contrato.setFin(c.getTime());
        }

    }

    public void cambiaFirma(ValueChangeEvent ve) {
        Boolean valor = (Boolean) ve.getNewValue();
        Calendar c = Calendar.getInstance();
        if (tieneAnticipo) {
            if (valor) {
                c.setTime(contrato.getFirma() == null ? new Date() : contrato.getFirma());
                c.add(Calendar.DATE, contrato.getNumeroDias() == null ? 0 : contrato.getNumeroDias());
                contrato.setFin(c.getTime());
            } else {
                c.setTime(contrato.getFechaanticipo() == null ? new Date() : contrato.getFechaanticipo());
                c.add(Calendar.DATE, contrato.getNumeroDias() == null ? 0 : contrato.getNumeroDias());
                contrato.setFin(c.getTime());
            }
        } else {
            c.setTime(contrato.getFirma());
            c.add(Calendar.DATE, contrato.getNumeroDias());
            contrato.setFin(c.getTime());
        }

    }

    public void cambiaIva(ValueChangeEvent ve) {
        Boolean valor = (Boolean) ve.getNewValue();
        double cuanto = contrato.getSubtotal() == null ? 0 : contrato.getSubtotal().doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        if (valor) {
            String valorStr = df.format(cuanto * .12).replace(",", ".");
            contrato.setIva(new BigDecimal(valorStr));
        } else {
            contrato.setIva(new BigDecimal(0));
        }
    }

    public void cambiaValor(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        double cuanto = valor == null ? 0 : valor.doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        boolean tieneIva = contrato.getTieneiva() == null ? false : contrato.getTieneiva();
        if (tieneIva) {
            String valorStr = df.format(cuanto * .12).replace(",", ".");
            contrato.setIva(new BigDecimal(valorStr));
        } else {
            contrato.setIva(new BigDecimal(0));
        }

        //ES-2018-03-22 Implementación de calculo de anticipo
        if (tieneAnticipo) {
            if (contrato.getSubtotal() == null) {
                contrato.setSubtotal(BigDecimal.ZERO);
            }
            BigDecimal sub = contrato.getIva().add(contrato.getSubtotal());
            BigDecimal ciento = new BigDecimal(100);
            if (contrato.getIva() == null || contrato.getSubtotal() == null) {
                contrato.setAnticipo(sub.multiply(contrato.getPctanticipo()).divide(new BigDecimal(100)));
            }
            //valorAnticipo=contrato.getAnticipo();
            //traerAnticipo(contrato);
        }
    }

    public void cambiaValorIva(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        double cuanto = valor == null ? 0 : valor.doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        boolean tieneIva = contrato.getTieneiva() == null ? false : contrato.getTieneiva();
        if (tieneIva) {
            String valorStr = df.format(cuanto * .12).replace(",", ".");
            contrato.setIva(new BigDecimal(valorStr));
        } else {
            contrato.setIva(new BigDecimal(0));
        }

        //ES-2018-03-22 Implementación de calculo de anticipo
        if (tieneAnticipo) {
            BigDecimal sub = contrato.getIva().add(contrato.getSubtotal());
            BigDecimal ciento = new BigDecimal(100);
            if (contrato.getIva() == null || contrato.getSubtotal() == null) {
                contrato.setAnticipo(sub.multiply(contrato.getPctanticipo()).divide(new BigDecimal(100)));
            }
            //valorAnticipo=contrato.getAnticipo();
            //traerAnticipo(contrato);
        }
    }

    public void cambiaValorAnticipo(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        double cuanto = valor == null ? 0 : valor.doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        //ES-2018-03-22 Implementación de calculo de anticipo
        if (tieneAnticipo) {

            BigDecimal sub = valor.add(contrato.getSubtotal());
            BigDecimal ciento = new BigDecimal(100);
            if (valor == null || contrato.getSubtotal() == null) {
                contrato.setAnticipo(sub.multiply(valor).divide(new BigDecimal(100)));
            }
            //valorAnticipo=contrato.getAnticipo();
            //traerAnticipo(contrato);
        }
    }

    public BigDecimal traerAnticipo(Contratos c) {
        if (tieneAnticipo) {
            BigDecimal sub = c.getIva().add(c.getSubtotal());
            BigDecimal ciento = new BigDecimal(100);
//            contrato.setAnticipo(sub.multiply(contrato.getPctanticipo()).divide(new BigDecimal(100)));
            valorAnticipo = sub.multiply(c.getPctanticipo()).divide(new BigDecimal(100));
            return valorAnticipo;
        }
        return BigDecimal.ZERO;
    }

    public void dateSelectListenerFirma(ValueChangeEvent event) {
        Date fecha = (Date) event.getNewValue();
        Calendar c = Calendar.getInstance();
        boolean esFirma = contrato.getEsfirma() == null ? false : contrato.getEsfirma();
        if (tieneAnticipo) {
            if (esFirma) {
                c.setTime(fecha == null ? new Date() : fecha);
                c.add(Calendar.DATE, contrato.getNumeroDias());
                contrato.setFin(c.getTime());
            } else {
                c.setTime(contrato.getFechaanticipo() == null ? new Date() : contrato.getFechaanticipo());
                c.add(Calendar.DATE, contrato.getNumeroDias());
                contrato.setFin(c.getTime());
            }
        } else {
            c.setTime(fecha == null ? new Date() : fecha);
            c.add(Calendar.DATE, contrato.getNumeroDias());
            contrato.setFin(c.getTime());
        }
    }

    public void dateSelectListenerAnticipo(ValueChangeEvent event) {
        Date fecha = (Date) event.getNewValue();
        Calendar c = Calendar.getInstance();
        boolean esFirma = contrato.getEsfirma() == null ? false : contrato.getEsfirma();
        if (tieneAnticipo) {
            if (esFirma) {
                c.setTime(contrato.getFirma() == null ? new Date() : contrato.getFirma());
                c.add(Calendar.DATE, contrato.getNumeroDias());
                contrato.setFin(c.getTime());
            } else {
                c.setTime(fecha == null ? new Date() : fecha);
                c.add(Calendar.DATE, contrato.getNumeroDias());
                contrato.setFin(c.getTime());
            }
        } else {
            c.setTime(contrato.getFirma() == null ? new Date() : contrato.getFirma());
            c.add(Calendar.DATE, contrato.getNumeroDias());
            contrato.setFin(c.getTime());
        }
    }

    /**
     * @return the tieneAnticipo
     */
    public Boolean getTieneAnticipo() {
        return tieneAnticipo;
    }

    /**
     * @param tieneAnticipo the tieneAnticipo to set
     */
    public void setTieneAnticipo(Boolean tieneAnticipo) {
        this.tieneAnticipo = tieneAnticipo;
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
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

    /**
     * @return the valorAnticipo
     */
    public BigDecimal getValorAnticipo() {
        return valorAnticipo;
    }

    /**
     * @param valorAnticipo the valorAnticipo to set
     */
    public void setValorAnticipo(BigDecimal valorAnticipo) {
        this.valorAnticipo = valorAnticipo;
    }

    /**
     * @return the fecha
     */
    public Calendar getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the contratos
     */
    public List<Contratos> getContratos() {
        return contratos;
    }

    /**
     * @param contratos the contratos to set
     */
    public void setContratos(List<Contratos> contratos) {
        this.contratos = contratos;
    }

    /**
     * @return the listacontratos
     */
    public LazyDataModel<Contratos> getListacontratos() {
        return listacontratos;
    }

    /**
     * @param listacontratos the listacontratos to set
     */
    public void setListacontratos(LazyDataModel<Contratos> listacontratos) {
        this.listacontratos = listacontratos;
    }

    /**
     * @return the tackingContra
     */
    public Trackingcontrato getTackingContra() {
        return tackingContra;
    }

    /**
     * @param tackingContra the tackingContra to set
     */
    public void setTackingContra(Trackingcontrato tackingContra) {
        this.tackingContra = tackingContra;
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the contratoBusca
     */
    public Contratos getContratoBusca() {
        return contratoBusca;
    }

    /**
     * @param contratoBusca the contratoBusca to set
     */
    public void setContratoBusca(Contratos contratoBusca) {
        this.contratoBusca = contratoBusca;
    }

    public String traerCreacion(Contratos c) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
        parametros.put("contrato", c);
        parametros.put(";inicial", 0);
        parametros.put(";final", 1);
        parametros.put(";orden", "o.fecha asc");
        try {
            List<Trackingcontrato> listaTk = ejbTrackingcontrato.encontarParametros(parametros);
            for (Trackingcontrato t : listaTk) {
                return t.getUsuario();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Trackingcontrato> getHistorial() {
        if (contrato == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.fecha asc");
        try {
            return ejbTrackingcontrato.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboContratosCompromisos() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        List<Contratos> listaTodosContratosActual = new LinkedList<>();
//        if (contatoActual) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null");
        parametros.put("proveedor", proveedorBean.getProveedor());
        parametros.put(";orden", "o.fin desc");
        try {
            List<Contratos> listaTodosContratos = ejbContratos.encontarParametros(parametros);
            for (Contratos c : listaTodosContratos) {
                Calendar ca = Calendar.getInstance();
                ca.setTime(c.getInicio());
                int anioContrato = ca.get(Calendar.YEAR);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
                int anioPresupuesto = calendar.get(Calendar.YEAR);

                if (anioContrato == anioPresupuesto) {
                    listaTodosContratosActual.add(c);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        }
        return Combos.getSelectItems(listaTodosContratosActual, true);
    }

    public SelectItem[] getComboContratosCompromisosAnteriores() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        List<Contratos> listaTodosContratosAnteriores = new LinkedList<>();
//        if (!contatoActual) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null and o.tipocontrato.codigo in ('01','03')");
        parametros.put("proveedor", proveedorBean.getProveedor());
        parametros.put(";orden", "o.fin desc");
        try {
            List<Contratos> listaTodosContratos = ejbContratos.encontarParametros(parametros);
            for (Contratos c : listaTodosContratos) {
                Calendar ca = Calendar.getInstance();
                ca.setTime(c.getInicio());
                int anioContrato = ca.get(Calendar.YEAR);
                
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
                int anioPresupuesto = calendar.get(Calendar.YEAR);
                
                if (anioContrato < anioPresupuesto) {
                    listaTodosContratosAnteriores.add(c);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        }
        return Combos.getSelectItems(listaTodosContratosAnteriores, true);
    }

    /**
     * @return the contratosActuales
     */
    public List<Contratos> getContratosActuales() {
        return contratosActuales;
    }

    /**
     * @param contratosActuales the contratosActuales to set
     */
    public void setContratosActuales(List<Contratos> contratosActuales) {
        this.contratosActuales = contratosActuales;
    }

    /**
     * @return the contratosAnteriores
     */
    public List<Contratos> getContratosAnteriores() {
        return contratosAnteriores;
    }

    /**
     * @param contratosAnteriores the contratosAnteriores to set
     */
    public void setContratosAnteriores(List<Contratos> contratosAnteriores) {
        this.contratosAnteriores = contratosAnteriores;
    }
}