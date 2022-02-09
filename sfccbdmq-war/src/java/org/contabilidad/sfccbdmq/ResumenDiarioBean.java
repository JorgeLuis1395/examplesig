/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "resumenDiarioSfccbdmq")
@ViewScoped
public class ResumenDiarioBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ResumenDiarioBean() {

    }

    private Formulario formulario = new Formulario();
    private List<Auxiliar> listaAsientos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipo;
    @EJB
    private CabecerasFacade ejbCabecera;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Date desde;
    private Date hasta;
    private Resource recurso;

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
//        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "ResumenDiarioVista";
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
        desde = configuracionBean.getConfiguracion().getPinicial();
        hasta = configuracionBean.getConfiguracion().getPvigente();
        buscar();
    }

    public String buscar() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            DocumentoPDF pdf = new DocumentoPDF("RESUMEN DIARIOS\n Desde : "
//                    + sdf.format(desde) + " Hasta :" + sdf.format(hasta), null, seguridadbean.getLogueado().getUserid());
            String usuario=seguridadbean.getLogueado().getUserid();    
            String empresa=configuracionBean.getConfiguracion().getNombre();
            String titulo="RESUMEN DIARIOS";
            String parTitulos="DESDE :" + sdf.format(desde) + 
                    " HASTA : " + sdf.format(hasta) + 
                    "\n VALORES EXPRESADOS EN " + configuracionBean.getConfiguracion().getExpresado();
            DocumentoPDF pdf = new DocumentoPDF(titulo, empresa, parTitulos, usuario,true);
            List<AuxiliarReporte> titulos = new LinkedList<>();
//           

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Registo"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cantidad"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            List<AuxiliarReporte> columnas = new LinkedList<>();
            listaAsientos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.fecha between :desde and :hasta");
            parametros.put(";campo", "o.tipo.id");
            parametros.put(";suma", "count(o)");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<Object[]> objetos = ejbCabecera.sumar(parametros);
            int i = 0;
            int cuantos = 0;
            double total = 0;
            for (Object[] o : objetos) {
                i++;
                Auxiliar a = new Auxiliar();
                Integer idTipo = (Integer) o[0];
                Tipoasiento t = ejbTipo.find(idTipo);
                a.setTitulo1(t.getNombre());
                a.setErrores(((Long) o[1]).intValue());
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.fecha between :desde and :hasta and o.cabecera.tipo=:tipo and o.valor>0");
                parametros.put(";campo", "o.valor");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put("tipo", t);
                a.setTotal(ejbRenglones.sumarCampo(parametros));
                total += a.getTotal().doubleValue();
                cuantos += a.getErrores();
                listaAsientos.add(a);
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, String.valueOf(i)));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, String.valueOf(a.getErrores())));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getTotal().doubleValue()));
            }
            Auxiliar a = new Auxiliar();
            a.setTitulo1("TOTALES");
            a.setTotal(new BigDecimal(total));
            a.setErrores(cuantos);
            listaAsientos.add(a);
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTALES"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, String.valueOf(a.getErrores())));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getTotal().doubleValue()));
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "RESUMEN DE DIARIOS");
            recurso=pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ResumenDiarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ResumenDiarioBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listaAsientos
     */
    public List<Auxiliar> getListaAsientos() {
        return listaAsientos;
    }

    /**
     * @param listaAsientos the listaAsientos to set
     */
    public void setListaAsientos(List<Auxiliar> listaAsientos) {
        this.listaAsientos = listaAsientos;
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
