/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.activos.sfccbdmq.BajasActivosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteDistributivoSfccbdmq")
@ViewScoped
public class ReporteDistributivoBean implements Serializable {

    /**
     * Creates a new instance of MaestrosBean
     */
    public ReporteDistributivoBean() {
        organigramas = new LazyDataModel<Organigrama>() {
            @Override
            public List<Organigrama> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Organigrama> organigramas;
    private Perfiles perfil;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    //Autocompletar
   private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioExportar = new Formulario();
    private String nombre;
    private String codigo;
    private Resource reporte;
    private int total;
    private int mes;
    private int operativo;
    private int anio;
    
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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

    public String buscar() {
        total = 0;
        setOrganigramas(new LazyDataModel<Organigrama>() {
            @Override
            public List<Organigrama> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = " o.activo=true  ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", "%" + getNombre().toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and upper(o.codigo) like :codigo";
                    parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
                }
                parametros.put(";where", where);
                int total = 0;
                try {
                    total = ejbOrganigrama.contar(parametros);
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
                try {
                    organigramas.setRowCount(total);

                    return ejbOrganigrama.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        return null;
    }
    public String imprimir() {

        Map parametros = new HashMap();
        total = 0;
        parametros.put(";orden", "o.codigo");
        String where = " o.activo=true ";
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre", "%" + getNombre().toUpperCase() + "%");
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and upper(o.codigo) like :codigo";
            parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
        }
        parametros.put(";where", where);

        try {

            List<Organigrama> lista = ejbOrganigrama.encontarParametros(parametros);
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE DISTRIBUTIVO", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. Ingreso"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cédula"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Empleado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cargo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Grado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Denominación"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "R.M.U."));
            double total = 0;
            for (Organigrama o : lista) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getCodigoalterno()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getSuperior() == null ? "" : o.getSuperior().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, null));
                List<Empleados> lAux = traerEmpleados(o);
                for (Empleados e : lAux) {
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getFechaingreso()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEntidad().getPin()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getEntidad().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getCargoactual().getCargo().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getCargoactual().getCargo().getEscalasalarial().getGrado().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, e.getCargoactual().getCargo().getEscalasalarial().getNombre()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue()));
                }
//                total += h.getValor().doubleValue();
            }
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "DISTRIBUTIVO");
            reporte = pdf.traerRecurso();
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteDistributivoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        total = 0;
        Map parametros = new HashMap();

        parametros.put(";orden", "o.codigo");
        String where = " o.activo=true ";
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre", "%" + getNombre().toUpperCase() + "%");
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and upper(o.codigo) like :codigo";
            parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
        }
        parametros.put(";where", where);

        try {

            List<Organigrama> lista = ejbOrganigrama.encontarParametros(parametros);
            DocumentoXLS xls = new DocumentoXLS("Distributivo");
            List<AuxiliarReporte> campos = new LinkedList<>();
            
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código Proceso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proceso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Ciudad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Tipo de contrato"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Operativo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha de Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Empleado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cargo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Grado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Escala salarial"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "R.M.U."));
            xls.agregarFila(campos, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            for (Organigrama o : lista) {
//                campos = new LinkedList<>();
//                campos.add( new AuxiliarReporte("String", o.getCodigo()));
//                campos.add(new AuxiliarReporte("String", o.getNombre()));
//                campos.add(new AuxiliarReporte("String", ""));
//                campos.add(new AuxiliarReporte("String", ""));
//                campos.add(new AuxiliarReporte("String", o.getCodigoalterno()));
//                campos.add(new AuxiliarReporte("String", o.getSuperior() == null ? "" : o.getSuperior().getNombre()));
//                campos.add(new AuxiliarReporte("String", ""));
//                campos.add(new AuxiliarReporte("String", ""));
//                xls.agregarFila(campos, false);
                List<Empleados> lAux = traerEmpleados(o);
                for (Empleados e : lAux) {
                    String ciudad = "";
                    if (e.getOficina() != null) {
                        if (e.getOficina().getEdificio() != null) {
                            if (e.getOficina().getEdificio().getCiudad() != null) {
                                ciudad=e.getOficina().getEdificio().getCiudad().getNombre();
                            }

                        }

                    }
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getCodigo()));// codigo organigrama
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, o.getNombre())); // Nombre Organigrama
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ciudad)); // Ciudad
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getTipocontrato().getNombre())); // Tipod e contrato
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getOperativo()?"SI":"NO")); // operativo
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sdf.format(e.getFechaingreso()))); // Fecha de ingreso
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getEmpleados().getCodigo())); // Código empleado
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getPin())); // CI
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getEmpleados().toString())); // Apellidos y nombres
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getCargo().toString())); // Cargo Actual
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getCargo().getEscalasalarial().getGrado().toString())); // Grado
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getCargo().getEscalasalarial().getNombre()));// Escala salarial
                    campos.add(new AuxiliarReporte("double", e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue())); //sueldo
                    xls.agregarFila(campos, false);
                }
//                total += h.getValor().doubleValue();
            }
            reporte = xls.traerRecurso();
            formularioExportar.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteDistributivoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the organigramas
     */
    public LazyDataModel<Organigrama> getOrganigramas() {
        return organigramas;
    }

    /**
     * @param organigramas the organigramas to set
     */
    public void setOrganigramas(LazyDataModel<Organigrama> organigramas) {
        this.organigramas = organigramas;
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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        setAnio(c.get((Calendar.YEAR)));
        setMes(c.get((Calendar.MONTH)) + 1);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ReporteDistributivoVista";
        if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
        this.setPerfil(getParametrosSeguridad().traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getParametrosSeguridad().cerraSession();
//            }
//        }
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

    public List<Empleados> getEmpleados() {
        if (organigramas == null) {
            return null;
        }
        if (!organigramas.isRowAvailable()) {
            return null;
        }
        Organigrama a = (Organigrama) organigramas.getRowData();

        return traerEmpleados(a);
    }

    public List<Empleados> traerEmpleados(Organigrama a) {
        Calendar inicioMes = Calendar.getInstance();
        Calendar finMes = Calendar.getInstance();
        inicioMes.set(Calendar.MONTH, mes - 1);
        inicioMes.set(Calendar.YEAR, anio);
        inicioMes.set(Calendar.DATE, 1);
        finMes.add(Calendar.MONTH, mes);
        finMes.add(Calendar.YEAR, mes);
        finMes.add(Calendar.DATE, 1);
        finMes.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        switch (operativo) {
            case 1:
                
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                + "and o.tipocontrato is not null and o.cargoacatual.operativo=true  "
                + "and (o.fechasalida is null or o.fechasalida between :desde and :hasta)");
                break;
            case 2:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                + "and o.tipocontrato is not null and o.cargoacatual.operativo=false  "
                + "and (o.fechasalida is null or o.fechasalida between :desde and :hasta)");
                
                break;
            default:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                + "and o.tipocontrato is not null "
                + "and (o.fechasalida is null or o.fechasalida between :desde and :hasta)");
                
                break;
        }
        
        parametros.put("desde", inicioMes.getTime());
        parametros.put("hasta", finMes.getTime());
        parametros.put("organigrama", a);
        parametros.put(";orden", "o.entidad.apellidos");
        try {
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            total += lista.size();
            return lista;

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger
                    .getLogger(BajasActivosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getEmpleadosNumero() {
        Organigrama a = (Organigrama) organigramas.getRowData();
        Map parametros = new HashMap();
        switch (operativo) {
            case 1:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama  and o.cargoacatual.operativo=true "
                        + "and o.activo=true and o.tipocontrato is not null and o.fechasalida is null");
                break;
            case 2:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama  and o.cargoacatual.operativo=false "
                        + "and o.activo=true and o.tipocontrato is not null and o.fechasalida is null");
                break;
            default:
                parametros.put(";where", "o.cargoactual.organigrama=:organigrama  "
                        + "and o.activo=true and o.tipocontrato is not null and o.fechasalida is null");
                break;
        }
        
        parametros.put("organigrama", a);
        parametros.put(";orden", "o.entidad.apellidos");
        try {
            return ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger
                    .getLogger(BajasActivosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    /**
     * @return the formularioExportar
     */
    public Formulario getFormularioExportar() {
        return formularioExportar;
    }

    /**
     * @param formularioExportar the formularioExportar to set
     */
    public void setFormularioExportar(Formulario formularioExportar) {
        this.formularioExportar = formularioExportar;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
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

    public int getTotalempleados() {
        Calendar inicioMes = Calendar.getInstance();
        Calendar finMes = Calendar.getInstance();
        inicioMes.set(Calendar.MONTH, mes - 1);
        inicioMes.set(Calendar.YEAR, anio);
        inicioMes.set(Calendar.DATE, 1);
        finMes.add(Calendar.MONTH, mes);
        finMes.add(Calendar.YEAR, mes);
        finMes.add(Calendar.DATE, 1);
        finMes.add(Calendar.DATE, -1);
        Map parametros = new HashMap();
        if (operativo==1){
            parametros.put(";where", "o.activo=true and o.cargoacatual.operativo=true "
                + "and o.tipocontrato is not null "
                + "and (o.fechasalida is null or o.fechasalida between :desde and :hasta)");
        } else if (operativo==1){
            parametros.put(";where", "o.activo=true and o.cargoacatual.operativo=true "
                + "and o.tipocontrato is not null "
                + "and (o.fechasalida is null or o.fechasalida between :desde and :hasta)");
        } else {
            parametros.put(";where", "o.activo=true "
                + "and o.tipocontrato is not null "
                + "and (o.fechasalida is null or o.fechasalida between :desde and :hasta)");
        }
        
        parametros.put("desde", inicioMes.getTime());
        parametros.put("hasta", finMes.getTime());
        parametros.put(";orden", "o.entidad.apellidos");
       
        try {
//            List<Empleados> lista=ejbEmpleados.encontarParametros(parametros);
//            total+=lista.size();
            return ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger
                    .getLogger(BajasActivosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    
}
