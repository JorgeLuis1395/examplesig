/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Tipomovbancos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteKardexBancosAnticipoSfccbdmq")
@ViewScoped
public class ReporteKardexBancoAnticipoBean {

    /**
     * Creates a new instance of KardexbancoBean
     */
    public ReporteKardexBancoAnticipoBean() {
        listakardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Kardexbanco> listakardex;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    private Proveedores proveedor;
    private Kardexbanco kardex;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private String documento;
    private Integer egreso;
    private String observaciones;
    private Bancos banco;
    private Date desde;
    private Date hasta;
    private Double saldoFinal;
    private Double saldoInicial;
    private Tipomovbancos tipo;
    private double valor;
    private int numero;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

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
     * @return the listakardex
     */
    public LazyDataModel<Kardexbanco> getListakardex() {
        return listakardex;
    }

    /**
     * @param listakardex the listakardex to set
     */
    public void setListakardex(LazyDataModel<Kardexbanco> listakardex) {
        this.listakardex = listakardex;
    }

    public String buscar() {

        saldoFinal = new Double(0);
        saldoInicial = new Double(0);
        listakardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                Map parametrosFinal = new HashMap();
                Map parametrosInicial = new HashMap();
                Calendar cInicial = Calendar.getInstance();
                cInicial.setTime(desde);
                cInicial.add(Calendar.DATE, -1);
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fechamov desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fechamov between :desde and :hasta  and o.estado>=0 and o.anticipo is not null";
                String whereInicial = "  o.fechamov <=:fecha and o.estado>=0 ";
                String whereFinal = "  o.fechamov <=:fecha and o.estado>=0 ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (egreso != null) {
                    if (egreso > 0) {
                        where += "  and o.egreso=:egreso";
                        whereInicial += " and  o.egreso=:egreso";
                        whereFinal += " and o.egreso=:egreso";
                        parametros.put("egreso", egreso);
                        parametrosFinal.put("egreso", egreso);
                        parametrosInicial.put("egreso", egreso);
                    }
                }
                parametros.put("desde", desde);
//                parametrosFinal.put("desde", desde);
                parametros.put("hasta", hasta);
                parametrosFinal.put("fecha", hasta);
                parametrosInicial.put("fecha", cInicial.getTime());

                int total = 0;
                try {

                    parametros.put(";where", where);
                    parametrosFinal.put(";where", where);
                    parametrosInicial.put(";where", whereInicial);
                    parametrosFinal.put(";where", whereFinal);
                    parametrosFinal.put(";campo", "o.valor");
                    parametrosInicial.put(";campo", "o.valor");
                    saldoInicial = ejbKardexbanco.sumarCampo(parametrosInicial).doubleValue();
                    saldoFinal = ejbKardexbanco.sumarCampo(parametrosFinal).doubleValue();
                    total = ejbKardexbanco.contar(parametros);
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
                listakardex.setRowCount(total);
                try {
                    return ejbKardexbanco.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

//    public String enviarMail(Proveedores proveedor)
//    {
//        
//        
//        
//        return null;
//        
//    }
    public String enviarNotificacion(Proveedores prov) {

        if (prov == null) {
            return null;
        }
        Codigos textoCodigo = codigosBean.traerCodigo("MAIL", "MAILANTICIPO");

        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#proveedor#", kardex.getProveedor().getEmpresa().toString());
            String correos = prov.getEmpresa().getEmail();
            texto = texto.replace("#doc#", kardex.getDocumento());
            texto = texto.replace("#banco#", kardex.getBanco().getNombre());
            texto = texto.replace("#numcop#", kardex.getEgreso().toString());
            texto = texto.replace("#fechmov#", sdf.format(kardex.getFechamov()));
            texto = texto.replace("#fechgrav#", sdf.format(kardex.getFentrega()));
            texto = texto.replace("#observaciones#", kardex.getObservaciones());
            if (correos != null) {
                ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
                MensajesErrores.informacion("Se Ha Enviado un E-Mail a:" + correos);
            }
        } catch (MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(ReporteKardexBancoAnticipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteKardexBancoVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getSeguridadbean().cerraSession();
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
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the banco
     */
    public Bancos getBanco() {
        return banco;
    }

    /**
     * @param banco the banco to set
     */
    public void setBanco(Bancos banco) {
        this.banco = banco;
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
     * @return the tipo
     */
    public Tipomovbancos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipomovbancos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the numero
     */
    public int getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * @return the saldoFinal
     */
    public Double getSaldoFinal() {
        return saldoFinal;
    }

    /**
     * @param saldoFinal the saldoFinal to set
     */
    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    /**
     * @return the saldoInicial
     */
    public Double getSaldoInicial() {
        return saldoInicial;
    }

    /**
     * @param saldoInicial the saldoInicial to set
     */
    public void setSaldoInicial(Double saldoInicial) {
        this.saldoInicial = saldoInicial;
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
     * @return the egreso
     */
    public Integer getEgreso() {
        return egreso;
    }

    /**
     * @param egreso the egreso to set
     */
    public void setEgreso(Integer egreso) {
        this.egreso = egreso;
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
     * @return the proveedor
     */
    public Proveedores getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
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

}
