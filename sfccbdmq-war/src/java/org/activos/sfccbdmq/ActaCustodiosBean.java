/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
//import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.beans.sfccbdmq.ActasFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.ExternosFacade;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "actaCustodioSfccbdmq")
@ViewScoped
public class ActaCustodiosBean implements Serializable {

    /**
     * Creates a new instance of OficinasBean
     */
    public ActaCustodiosBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioEntregan = new Formulario();
    private Formulario formularioReciben = new Formulario();
    private Oficinas oficina;
    private Actas acta;
    private Integer numeroActa;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ExternosFacade ejbExternos;
    private Perfiles perfil;
    private Edificios edificio;
    private String quienesEntregan;
    private String quienesReciben;
    private List<AuxiliarCarga> listaReporte;
    private AuxiliarCarga auxiliar;
    private AuxiliarCarga entrega;
    private List<AuxiliarCarga> listaExternos;
    private List<AuxiliarCarga> listaEntregan;
    private List<AuxiliarCarga> listaReciben;
    private List<Activos> listaActivos;
    private List<Activos> listaActivosImprimir;

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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ActaCustodiosVista";
        if (perfilString == null) {
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
     * @return the
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    public String buscar() {
//        if (!menusBean.getPerfil().getConsulta()) {
//            MensajesErrores.advertencia("No tiene autorización para consultar");
//            return null;
//        }
        listaReporte = new LinkedList<>();
        listaExternos = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.custodio.id");
            parametros.put(";campo", "o.custodio.id");
            parametros.put(";suma", "sum(o.valoralta)");
            parametros.put(";where", "o.fechaalta is not null and o.baja is null and o.acta is null");
            List<Object[]> listaSuma = ejbActivos.sumar(parametros);
            double suma = 0;
            for (Object[] o : listaSuma) {
                Integer idEmpleado = (Integer) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                if (valor != null) {
                    suma += valor.doubleValue();
                } else {
                    valor = new BigDecimal(BigInteger.ZERO);
                }
                // traer empleado
                Empleados e = ejbEmpleados.find(idEmpleado);
                AuxiliarCarga a = new AuxiliarCarga();
                a.setTotal(e.getEntidad().getApellidos() + " " + e.getEntidad().getNombres());
                a.setCuenta(e.getOficina().getEdificio().getCalleprimaria() + " " + e.getOficina().getEdificio().getCalleprimaria() + " No: " + e.getOficina().getEdificio().getNumero());
                a.setReferencia(e.getEntidad().getPin());
                a.setId(idEmpleado);
                a.setImputable(true);
                a.setSaldoInicial(valor);
                listaReporte.add(a);
                //traer  entidad

            }
            AuxiliarCarga a = new AuxiliarCarga();
            a.setSaldoInicial(new BigDecimal(suma));
            listaReporte.add(a);
            parametros = new HashMap();
            parametros.put(";orden", "o.externo.id");
            parametros.put(";campo", "o.externo.id");
            parametros.put(";suma", "sum(o.valoralta)");
            parametros.put(";where", "o.alta is not null and o.baja is null and o.acta is null");
            listaSuma = ejbActivos.sumar(parametros);
            suma = 0;
            for (Object[] o : listaSuma) {
                Integer idEmpleado = (Integer) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                if (valor != null) {
                    suma += valor.doubleValue();
                } else {
                    valor = new BigDecimal(BigInteger.ZERO);
                }
                // traer empleado
                Externos e = ejbExternos.find(idEmpleado);
                a = new AuxiliarCarga();
                a.setTotal(e.getNombre());
//                a.setCuenta(e.getEmpresa());
                a.setReferencia(e.getEmpresa());
                a.setCuenta(e.getEdificio().getCalleprimaria() + " " + e.getEdificio().getCalleprimaria() + " No: " + e.getEdificio().getNumero());
                a.setSaldoInicial(valor);
                a.setId(idEmpleado);
                a.setImputable(false);
                listaExternos.add(a);
                //traer  entidad

            }
            a = new AuxiliarCarga();
            a.setSaldoInicial(new BigDecimal(suma));
            listaExternos.add(a);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ActaCustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
     * @return the edificio
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    /**
     * @return the listaReporte
     */
    public List<AuxiliarCarga> getListaReporte() {
        return listaReporte;
    }

    /**
     * @param listaReporte the listaReporte to set
     */
    public void setListaReporte(List<AuxiliarCarga> listaReporte) {
        this.listaReporte = listaReporte;
    }

    /**
     * @return the listaExternos
     */
    public List<AuxiliarCarga> getListaExternos() {
        return listaExternos;
    }

    /**
     * @param listaExternos the listaExternos to set
     */
    public void setListaExternos(List<AuxiliarCarga> listaExternos) {
        this.listaExternos = listaExternos;
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

    public String seleccionaInterno(AuxiliarCarga a) {
        try {
            // empleado
            auxiliar = a;
            listaEntregan=new LinkedList<>();
            listaReciben=new LinkedList<>();
            Empleados e = ejbEmpleados.find(a.getId());
            Map parametros = new HashMap();
            parametros.put(";where", "o.fechaalta is not null "
                    + "and o.baja is null and o.acta is null and o.custodio=:custodio");
            parametros.put("custodio", e);

            listaActivos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.actaultima");
            numeroActa = ejbActivos.maximoNumero(parametros);
            if (numeroActa == null) {
                numeroActa = 0;
            }
            acta = new Actas();
            numeroActa++;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActaCustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String seleccionaExterno(AuxiliarCarga a) {
        try {

            // externo
            auxiliar = a;
            Externos e = ejbExternos.find(a.getId());
//            listaReporte.add(a);
            acta = new Actas();
            listaEntregan=new LinkedList<>();
            listaReciben=new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.alta is not null and o.baja is null and o.acta is null and o.externo=:externo");
            parametros.put("externo", e);
            listaActivos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.actaultima");
            numeroActa = ejbActivos.maximoNumero(parametros);
            if (numeroActa == null) {
                numeroActa = 0;
            }
            numeroActa++;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActaCustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String nuevaEntrega() {
        entrega = new AuxiliarCarga();
        entrega.setTotal(null);
        getFormularioEntregan().editar();
        return null;
    }

    public String nuevaRecibe() {
        entrega = new AuxiliarCarga();
        entrega.setTotal(null);
        getFormularioEntregan().insertar();
        return null;
    }

    public String borraEntrega() {
        listaEntregan.remove(getFormularioEntregan().getFila().getRowIndex());

        return null;
    }

    public String borraRecibe() {
        listaReciben.remove(getFormularioReciben().getFila().getRowIndex());

        return null;
    }

    public String grabaEntrega() {
        if ((entrega.getTotal() == null) || (entrega.getTotal().isEmpty())) {
            MensajesErrores.advertencia("Ingrese los nombres");
            return null;
        }
        if ((entrega.getCuenta() == null) || (entrega.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el cargo");
            return null;
        }
        if ((entrega.getAuxiliar() == null) || (entrega.getAuxiliar().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el titulo");
            return null;
        }
        listaEntregan.add(entrega);
        getFormularioEntregan().cancelar();
        return null;
    }

    public String grabaReciben() {
        if ((entrega.getTotal() == null) || (entrega.getTotal().isEmpty())) {
            MensajesErrores.advertencia("Ingrese los nombres");
            return null;
        }
        if ((entrega.getCuenta() == null) || (entrega.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el cargo");
            return null;
        }
        if ((entrega.getAuxiliar() == null) || (entrega.getAuxiliar().isEmpty())) {
            MensajesErrores.advertencia("Ingrese el titulo");
            return null;
        }
        listaReciben.add(entrega);
        getFormularioEntregan().cancelar();
        return null;
    }

    public String grabar() {
        try {
            if ((acta.getAntecedentes()== null) || (acta.getAntecedentes().isEmpty())) {
                MensajesErrores.advertencia("Ingrese los antecedentes");
                return null;
            }
            if (listaReciben.isEmpty()){
                MensajesErrores.advertencia("Ingrese quien(es) reciben");
                return null;
            }
            if (listaEntregan.isEmpty()){
                MensajesErrores.advertencia("Ingrese quien(es) entregan");
                return null;
            }
            listaActivosImprimir = new LinkedList<>();
            for (Activos a : listaActivos) {
                if (a.isSeleccionado()) {
                    a.setActa(numeroActa);
                    a.setActaultima(numeroActa);
                    listaActivosImprimir.add(a);
                    ejbActivos.edit(a, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            String qEntrega="";
            for (AuxiliarCarga a:listaEntregan){
                if (!qEntrega.isEmpty()){
                    qEntrega+="#";
                    quienesEntregan+=" , ";
                }
                qEntrega+=a.getTotal()+"@"+a.getAuxiliar()+"@"+a.getCuenta();
                quienesEntregan+=a.getTotal()+" "+a.getAuxiliar()+" "+a.getCuenta();
            }
            acta.setEntregan(qEntrega);
            qEntrega="";
            for (AuxiliarCarga a:listaReciben){
                if (!qEntrega.isEmpty()){
                    qEntrega+="#";
                    quienesReciben+=" , ";
                }
                qEntrega+=a.getTotal()+"@"+a.getAuxiliar()+"@"+a.getCuenta();
                quienesReciben+=a.getTotal()+" "+a.getAuxiliar()+" "+a.getCuenta();
            }
            DecimalFormat df=new DecimalFormat("0000");
            acta.setReciben(qEntrega);
            acta.setNumero(numeroActa);
            acta.setNumerotexto(df.format(numeroActa)+"-EMS-AF-A-"+getAnio());
            ejbActas.create(acta, parametrosSeguridad.getLogueado().getUserid());
            formulario.cancelar();
            formularioImprimir.insertar();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActaCustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        return null;
    }

    /**
     * @return the listaActivos
     */
    public List<Activos> getListaActivos() {
        return listaActivos;
    }

    /**
     * @param listaActivos the listaActivos to set
     */
    public void setListaActivos(List<Activos> listaActivos) {
        this.listaActivos = listaActivos;
    }

    /**
     * @return the numeroActa
     */
    public Integer getNumeroActa() {
        return numeroActa;
    }

    /**
     * @param numeroActa the numeroActa to set
     */
    public void setNumeroActa(Integer numeroActa) {
        this.numeroActa = numeroActa;
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
     * @return the auxiliar
     */
    public AuxiliarCarga getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(AuxiliarCarga auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getFechaActa() {
        for (Activos a : listaActivos) {
            Calendar c = Calendar.getInstance();
            c.setTime(a.getFechaalta());
            int anio = c.get(Calendar.YEAR);
            int dia = c.get(Calendar.DATE);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMMMM");
            return dia + " días del mes de " + sdf.format(a.getFechaalta()) + " de " + anio;
        }
        return null;
    }
    public String getAnio() {
        for (Activos a : listaActivos) {
            Calendar c = Calendar.getInstance();
            c.setTime(a.getFechaalta());
            int anio = c.get(Calendar.YEAR);
            return "" + anio;
        }
        return null;
    }
    /**
     * @return the listaActivosImprimir
     */
    public List<Activos> getListaActivosImprimir() {
        return listaActivosImprimir;
    }

    /**
     * @param listaActivosImprimir the listaActivosImprimir to set
     */
    public void setListaActivosImprimir(List<Activos> listaActivosImprimir) {
        this.listaActivosImprimir = listaActivosImprimir;
    }

    /**
     * @return the acta
     */
    public Actas getActa() {
        return acta;
    }

    /**
     * @param acta the acta to set
     */
    public void setActa(Actas acta) {
        this.acta = acta;
    }

    /**
     * @return the listaEntregan
     */
    public List<AuxiliarCarga> getListaEntregan() {
        return listaEntregan;
    }

    /**
     * @param listaEntregan the listaEntregan to set
     */
    public void setListaEntregan(List<AuxiliarCarga> listaEntregan) {
        this.listaEntregan = listaEntregan;
    }

    /**
     * @return the listaReciben
     */
    public List<AuxiliarCarga> getListaReciben() {
        return listaReciben;
    }

    /**
     * @param listaReciben the listaReciben to set
     */
    public void setListaReciben(List<AuxiliarCarga> listaReciben) {
        this.listaReciben = listaReciben;
    }

    /**
     * @return the entrega
     */
    public AuxiliarCarga getEntrega() {
        return entrega;
    }

    /**
     * @param entrega the entrega to set
     */
    public void setEntrega(AuxiliarCarga entrega) {
        this.entrega = entrega;
    }

    /**
     * @return the formularioEntregan
     */
    public Formulario getFormularioEntregan() {
        return formularioEntregan;
    }

    /**
     * @param formularioEntregan the formularioEntregan to set
     */
    public void setFormularioEntregan(Formulario formularioEntregan) {
        this.formularioEntregan = formularioEntregan;
    }

    /**
     * @return the formularioReciben
     */
    public Formulario getFormularioReciben() {
        return formularioReciben;
    }

    /**
     * @param formularioReciben the formularioReciben to set
     */
    public void setFormularioReciben(Formulario formularioReciben) {
        this.formularioReciben = formularioReciben;
    }

    /**
     * @return the quienesEntregan
     */
    public String getQuienesEntregan() {
        return quienesEntregan;
    }

    /**
     * @param quienesEntregan the quienesEntregan to set
     */
    public void setQuienesEntregan(String quienesEntregan) {
        this.quienesEntregan = quienesEntregan;
    }

    /**
     * @return the quienesReciben
     */
    public String getQuienesReciben() {
        return quienesReciben;
    }

    /**
     * @param quienesReciben the quienesReciben to set
     */
    public void setQuienesReciben(String quienesReciben) {
        this.quienesReciben = quienesReciben;
    }
}
