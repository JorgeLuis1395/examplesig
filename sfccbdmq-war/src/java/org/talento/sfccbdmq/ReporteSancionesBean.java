/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
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
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.entidades.sfccbdmq.Historialsanciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposanciones;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteSanciones")
@ViewScoped
public class ReporteSancionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Historialsanciones> listadoHistorialsanciones;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private String motivo;
    private int especunaria;
    private int esleve;
    private Tiposanciones tipo;
    private Date desde;
    private Date hasta;
    @EJB
    private HistorialsancionesFacade ejbHistorialsanciones;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;

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
    /* @return the parametrosSeguridad
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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String motivoForma = "ReporteSancionesVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (motivoForma==null){
//            motivoForma="";
//        }
//        if (motivoForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of HistorialsancionesEmpleadoBean
     */
    public ReporteSancionesBean() {
        listadoHistorialsanciones = new LazyDataModel<Historialsanciones>() {

            @Override
            public List<Historialsanciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoHistorialsanciones = new LazyDataModel<Historialsanciones>() {

            @Override
            public List<Historialsanciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                String where = "o.fsancion between :desde and :hasta ";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo";
                    parametros.put("motivo", "%" + motivo.toUpperCase() + "%");
                }
                if (tipo != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (tipo != null) {
                    where += " and o.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (empleadoBean.getEmpleadoSeleccionado() != null) {
                    where += " and o.empleado=:empleado";
                    parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
                }
                if ((especunaria == 1)) {
                    where += " and o.especunaria=true";
                } else if (especunaria == 2) {
                    where += " and o.especunaria=false";
                }
                if ((esleve == 1)) {
                    where += " and o.esleve=true";
                } else if (especunaria == 2) {
                    where += " and o.esleve=false";
                }

                int total;
                try {
                    parametros.put(";orden", "o.fsancion asc");
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbHistorialsanciones.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoHistorialsanciones.setRowCount(total);
                    return ejbHistorialsanciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String imprimir() {

        String where = "o.fsancion between :desde and :hasta ";
        Map parametros = new HashMap();

        if (!((motivo == null) || (motivo.isEmpty()))) {
            where += " and upper(o.motivo) like :motivo";
            parametros.put("motivo", "%" + motivo.toUpperCase() + "%");
        }
        if (tipo != null) {
            where += " and o.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (tipo != null) {
            where += " and o.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (empleadoBean.getEmpleadoSeleccionado() != null) {
            where += " and o.empleado=:empleado";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        }
        if ((especunaria == 1)) {
            where += " and o.especunaria=true";
        } else if (especunaria == 2) {
            where += " and o.especunaria=false";
        }
        if ((esleve == 1)) {
            where += " and o.esleve=true";
        } else if (especunaria == 2) {
            where += " and o.esleve=false";
        }

        try {
            parametros.put(";orden", "o.fsancion asc");
            parametros.put(";where", where);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<Historialsanciones> lista = ejbHistorialsanciones.encontarParametros(parametros);
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE SANCIONES \n Desde :" + sdf.format(desde) + " Hasta : " + sdf.format(hasta), null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Empleado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proceso"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Motivo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo"));
            titulos.add(new AuxiliarReporte("Valor", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            double total = 0;
            for (Historialsanciones h : lista) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, h.getFsancion()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, h.getEmpleado().getEntidad().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, h.getEmpleado().getCargoactual().getOrganigrama().toString()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, h.getMotivo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, h.getTipo().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, h.getValor()==null?null:h.getValor().doubleValue()));
//                total += h.getValor().doubleValue();
            }
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "SANCIONES");
            reporte = pdf.traerRecurso();
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteSancionesBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listadoFamiliares
     */
    public LazyDataModel<Historialsanciones> getListadoHistorialsanciones() {
        return listadoHistorialsanciones;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Historialsanciones> listadoFamiliares) {
        this.listadoHistorialsanciones = listadoFamiliares;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
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
     * @return the especunaria
     */
    public int getEspecunaria() {
        return especunaria;
    }

    /**
     * @param especunaria the especunaria to set
     */
    public void setEspecunaria(int especunaria) {
        this.especunaria = especunaria;
    }

    /**
     * @return the tipo
     */
    public Tiposanciones getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposanciones tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the esleve
     */
    public int getEsleve() {
        return esleve;
    }

    /**
     * @param esleve the esleve to set
     */
    public void setEsleve(int esleve) {
        this.esleve = esleve;
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

}
