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
import org.auxiliares.sfccbdmq.AuxiliarParametros;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.Documento101;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.text.DecimalFormat;
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
import org.beans.sfccbdmq.LineasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Campos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Lineas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "formulario101Sfccbdmq")
@ViewScoped
public class Formulario101Bean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public Formulario101Bean() {
        Calendar c = Calendar.getInstance();

    }
    private List<AuxiliarParametros> listaParametros;
    private AuxiliarParametros parametro;
    private List<Lineas> listaLineas;
    private int anio;
    private Codigos reporte;
    private String tituloTotal;
    private boolean total;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioParametro = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private LineasFacade ejbLineas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;
    private Resource recurso;
    private Resource formulario101;

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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinal());
//        c.set(Calendar.MONTH, 0);
//        c.set(Calendar.DATE, 1);
//        desde = c.getTime();
//        c.set(Calendar.MONTH, 11);
//        c.set(Calendar.DATE, 31);
//        hasta = c.getTime();
//        listaParametros = new LinkedList<>();
//        parametro = new AuxiliarParametros();
//        parametro.setDesde(configuracionBean.getConfiguracion().getPinicial());
//        parametro.setHasta(configuracionBean.getConfiguracion().getPfinal());
//        parametro.setPositivo(true);
//        parametro.setNombre("Período  Actual");
//        listaParametros.add(parametro);
        anio = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        String nombreForma = "Formulario101Vista";
        if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilStr));
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

    public String buscar() {
        Calendar cDesde = Calendar.getInstance();
//        Calendar cDesde=null;
//        Calendar cHasta=null;
        Calendar cHasta = Calendar.getInstance();
        cDesde.set(anio, 0, 1, 0, 0);
        cHasta.set(anio, 11, 31, 0, 0);
//        Date desde = new Date(anio, 0, 1);
//        Date hasta = new Date(anio, 11, 31);
        reporte = codigosBean.traerCodigo(Constantes.REPORTES, "IMPRENTA");
        if (reporte == null) {
            MensajesErrores.advertencia("Seleccione un reporte primero");
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";orden", "o.posicion asc");
        parametros.put(";where", "o.reporte=:reporte");
        parametros.put("reporte", reporte);
        try {
            Documento101 hoja = new Documento101();
            hoja.ponerConcepto("102", anio, 0);
            hoja.ponerRuc(configuracionBean.getConfiguracion().getRuc());
            hoja.ponerRazon(configuracionBean.getConfiguracion().getNombre());
            listaLineas = ejbLineas.encontarParametros(parametros);
            for (Lineas l : listaLineas) {
                String nombre = "";
                String codigo = l.getCodigo();
//                double valor = calculaDoble(l, cDesde.getTime(), hasta);
                double valor = calculaDoble(l, cDesde, cHasta);
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    hoja.ponerConcepto(codigo, valor, 0);
                }
            }
            formulario101 = hoja.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(Formulario101Bean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        Calendar cDesde = Calendar.getInstance();
        Calendar cHasta = Calendar.getInstance();
        cDesde.set(anio, 0, 1);
        cHasta.set(anio, 11, 31);
        reporte = codigosBean.traerCodigo(Constantes.REPORTES, "IMPRENTA");
        if (reporte == null) {
            MensajesErrores.advertencia("Seleccione un reporte primero");
            return null;
        }
        if ((listaParametros == null) || (listaParametros.isEmpty())) {
            MensajesErrores.advertencia("No existen parametros para el reporte");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.posicion asc");
        parametros.put(";where", "o.reporte=:reporte");
        parametros.put("reporte", reporte);
        try {
            listaLineas = ejbLineas.encontarParametros(parametros);
            DocumentoPDF pdf = new DocumentoPDF(reporte.getNombre() + " \n"
                    + configuracionBean.getConfiguracion().getExpresado(),
                    null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Código"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            for (AuxiliarParametros l : listaParametros) {
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, l.getNombre()));
            }
            int tamanio = listaParametros.size() + 2;
            if (total) {
                tamanio++;
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, tituloTotal));
            }
            double totalValor = 0;
            for (Lineas l : listaLineas) {
                String nombre = "";
                if (l.getCampo() != null) {
                    nombre = l.getCampo().getNombre();
                } else if (l.getOperacion() != null) {
                    nombre = l.getOperacion().getNombre();
                } else if (l.getTexto() != null) {
                    nombre = l.getTexto();
                }
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, l.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, nombre));
                double valor = calculaDoble(l, cDesde, cHasta);
                totalValor += 0;
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                if (total) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, calculaTotal(l, cDesde, cHasta)));
                }
            }
            pdf.agregarTablaReporte(titulos, columnas, tamanio, 100, reporte.getNombre());
            recurso = pdf.traerRecurso();
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LineasBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(Formulario101Bean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the formularioParametro
     */
    public Formulario getFormularioParametro() {
        return formularioParametro;
    }

    /**
     * @param formularioParametro the formularioParametro to set
     */
    public void setFormularioParametro(Formulario formularioParametro) {
        this.formularioParametro = formularioParametro;
    }

    /**
     * @return the listaParametros
     */
    public List<AuxiliarParametros> getListaParametros() {
        return listaParametros;
    }

    /**
     * @param listaParametros the listaParametros to set
     */
    public void setListaParametros(List<AuxiliarParametros> listaParametros) {
        this.listaParametros = listaParametros;
    }

    /**
     * @return the listaLineas
     */
    public List<Lineas> getListaLineas() {
        return listaLineas;
    }

    /**
     * @param listaLineas the listaLineas to set
     */
    public void setListaLineas(List<Lineas> listaLineas) {
        this.listaLineas = listaLineas;
    }

    /**
     * @return the reporte
     */
    public Codigos getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Codigos reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the parametro
     */
    public AuxiliarParametros getParametro() {
        return parametro;
    }

    /**
     * @param parametro the parametro to set
     */
    public void setParametro(AuxiliarParametros parametro) {
        this.parametro = parametro;
    }

    /**
     * @return the tituloTotal
     */
    public String getTituloTotal() {
        return tituloTotal;
    }

    /**
     * @param tituloTotal the tituloTotal to set
     */
    public void setTituloTotal(String tituloTotal) {
        this.tituloTotal = tituloTotal;
    }

    /**
     * @return the total
     */
    public boolean isTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(boolean total) {
        this.total = total;
    }

    public String calcula(Lineas l, Calendar desde, Calendar hasta) {
        DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
        if (l.getCampo() != null) {
            // formateo y retorno
            return df.format(calculaCampo(l.getCampo(), desde, hasta));
        } else if (l.getOperacion() != null) {
            double campo1 = calculaCampo(l.getOperacion().getCampo1(), desde, hasta);
            double valor = 0;
            if (l.getOperacion().getCampo2() == null) {
                valor = l.getOperacion().getConstante();
            } else if (l.getOperacion().getCampo2() != null) {
                valor = calculaCampo(l.getOperacion().getCampo2(), desde, hasta);
            }
            if (l.getOperacion().getOperacion() == 0) {
                return df.format(campo1 * valor);
            } else if (l.getOperacion().getOperacion() == 1) {
                if (valor == 0) {
                    return "NA";
                } else {
                    return df.format(campo1 / valor);
                }
            } else if (l.getOperacion().getOperacion() == 2) {
                return df.format(campo1 + valor);
            } else if (l.getOperacion().getOperacion() == 3) {
                return df.format(campo1 - valor);
            }
        }
        return null;
    }

    public double calculaDoble(Lineas l, Calendar desde, Calendar hasta) {
//        DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
        if (l.getCampo() != null) {
            // formateo y retorno
            return calculaCampo(l.getCampo(), desde, hasta);
        } else if (l.getOperacion() != null) {
            double campo1 = calculaCampo(l.getOperacion().getCampo1(), desde, hasta);
            double valor = 0;
            if (l.getOperacion().getCampo2() == null) {
                valor = l.getOperacion().getConstante();
            } else if (l.getOperacion().getCampo2() != null) {
                valor = calculaCampo(l.getOperacion().getCampo2(), desde, hasta);
            }
            if (l.getOperacion().getOperacion() == 0) {
                return campo1 * valor;
            } else if (l.getOperacion().getOperacion() == 1) {
                if (valor == 0) {
                    return 0.0;
                } else {
                    return campo1 / valor;
                }
            } else if (l.getOperacion().getOperacion() == 2) {
                return campo1 + valor;
            } else if (l.getOperacion().getOperacion() == 3) {
                return campo1 - valor;
            }
        }
        return 0.0;
    }

    private double calculaCampo(Campos c, Calendar desde, Calendar hasta) {
        double suma = 0;
        desde.set(Calendar.MINUTE, 0);
        desde.set(Calendar.SECOND, 0);
        desde.set(Calendar.MILLISECOND, 0);
        hasta.set(Calendar.MINUTE, 0);
        hasta.set(Calendar.SECOND, 0);
        hasta.set(Calendar.MILLISECOND, 0);
        String[] aux = c.getCuenta().split("#");
        for (int i = 0; i <= aux.length - 1; i++) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta");
                parametros.put("cuenta", aux[i] + "%");
                parametros.put("desde", desde.getTime());
                parametros.put("hasta", hasta.getTime());
//                List<Renglones> lista = ejbRenglones.encontarParametros(parametros);
                parametros.put(";campo", "o.valor");

                double valor = ejbRenglones.sumarCampo(parametros).doubleValue();

                suma += valor;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(Formulario101Bean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // formateo y retorno
        if (c.getSigno() == 0) {
            // no hacer nada
        } else if (c.getSigno() == 2) {
            // saldos iniciales
        } else {
            suma = suma * c.getSigno();
        }
        return suma;
    }

    public String calculaTotal(Lineas l, Calendar desde, Calendar hasta) {
        DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
        if (l.getCampo() != null) {
            // formateo y retorno
            double valor = 0;
            for (AuxiliarParametros p : listaParametros) {
                if (p.isPositivo()) {
                    valor += calculaCampo(l.getCampo(), desde, hasta);
                } else {
                    valor += calculaCampo(l.getCampo(), desde, hasta) * -1;
                }

            }
            return df.format(valor);
        } else if (l.getOperacion() != null) {
            double valorRetorno = 0;
            for (AuxiliarParametros p : listaParametros) {
                double campo1 = calculaCampo(l.getOperacion().getCampo1(), desde, hasta);
                double valor = 0;
                if (l.getOperacion().getCampo2() == null) {
                    valor = l.getOperacion().getConstante();
                } else if (l.getOperacion().getCampo2() != null) {
                    valor = calculaCampo(l.getOperacion().getCampo2(), desde, hasta);
                }
                if (l.getOperacion().getOperacion() == 0) {
                    if (p.isPositivo()) {
                        valorRetorno += campo1 * valor;
                    } else {
                        valorRetorno += campo1 * valor * -1;
                    }
//                    return df.format(campo1 * valor);
                } else if (l.getOperacion().getOperacion() == 1) {
                    if (valor == 0) {
//                        return "NA";
                    } else {
                        if (p.isPositivo()) {
                            valorRetorno += campo1 / valor;
                        } else {
                            valorRetorno += campo1 / valor * -1;
                        }
//                        return df.format(campo1 / valor);
                    }
                } else if (l.getOperacion().getOperacion() == 2) {

                    if (p.isPositivo()) {
                        valorRetorno += campo1 + valor;
                    } else {
                        valorRetorno += campo1 + valor * -1;
                    }
//                  
                } else if (l.getOperacion().getOperacion() == 3) {

                    if (p.isPositivo()) {
                        valorRetorno += campo1 - valor;
                    } else {
                        valorRetorno += campo1 - valor * -1;
                    }
//                  
                }
            }
            return df.format(valorRetorno);
        }
        return null;
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

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return the formulario101
     */
    public Resource getFormulario101() {
        return formulario101;
    }

    /**
     * @param formulario101 the formulario101 to set
     */
    public void setFormulario101(Resource formulario101) {
        this.formulario101 = formulario101;
    }
}
