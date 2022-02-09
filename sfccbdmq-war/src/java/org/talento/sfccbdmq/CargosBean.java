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
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
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
import org.beans.sfccbdmq.ActividadescargosFacade;
import org.beans.sfccbdmq.AreasseleccionFacade;
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.EscalassalarialesFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.RequerimientoscargoFacade;
import org.beans.sfccbdmq.ValoresrequerimientosFacade;
import org.entidades.sfccbdmq.Actividadescargos;
import org.entidades.sfccbdmq.Areasseleccion;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Escalassalariales;
import org.entidades.sfccbdmq.Nivelesgestion;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Requerimientoscargo;
import org.entidades.sfccbdmq.Valoresrequerimientos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "cargos")
@ViewScoped
public class CargosBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of CargosBean
     */

    private Formulario formulario = new Formulario();
    private Cargos cargo;
    //private Organigrama organigrama;
    private List<Actividadescargos> listaResponsabilidades;
    private List<Actividadescargos> listaResponsabilidadesB;
    private Actividadescargos responsabilidad;
    private List<Valoresrequerimientos> listaRequerimientos;
    private List<Valoresrequerimientos> listaRequerimientosB;
    private List<Valoresrequerimientos> listaRequerimientosC;
    private List<Valoresrequerimientos> listaRequerimientosCB;
    private Valoresrequerimientos valorrequerimiento;
    private LazyDataModel<Cargos> listaCargos;
    private Nivelesgestion nivel;
    private String msjbusqueda;
    private Formulario formularioResponsabilidades = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioRequerimientos = new Formulario();
    private Formulario formularioRequerimientosC = new Formulario();
    private Escalassalariales salario;
    private Areasseleccion area;
    private Codigos competencia;
    private Requerimientoscargo requerimiento;
    private boolean controlArea = Boolean.TRUE;
    private String nombre;
    private String codigo;
    private Nivelesgestion nivelB;
    private String cdirecta;
    private Escalassalariales salarioB;
    @EJB
    private CargosFacade ejbCargos;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private ActividadescargosFacade ejbResponsabilidades;
    @EJB
    private ValoresrequerimientosFacade ejbValoresRequerimientos;
    @EJB
    private RequerimientoscargoFacade ejbRequerimientos;
    @EJB
    private AreasseleccionFacade ejbAreas;
    @EJB
    private EscalassalarialesFacade ejbEscalas;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource recurso;

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
        String nombreForma = "CargosVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }
    // fin perfiles

    public CargosBean() {
        listaCargos = new LazyDataModel<Cargos>() {
            @Override
            public List<Cargos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };
    }

    public List<Cargos> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {

        Map parametros = new HashMap();
        String where = "o.activo=true";
//        String where = "o.id is not null and o.activo=true";
        //Criterios de busqueda
//        //ORGANIGRAMA
//        if (organigrama != null) {
//            where += " and o.organigrama=:organigrama";
//            parametros.put("organigrama", organigrama);
//        }
        //CARGO
        //AREA
//        if (cargo != null) {
//            where += " and o.id=:cargo";
//            parametros.put("cargo", cargo.getId());
//        }
        //CODIGO
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and upper(o.codigo) like :codigo";
            parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
        }
        //NOMBRE
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre",  nombre.toUpperCase() + "%");
        }
        //NIV GESTION
        if (nivelB != null) {
            where += " and o.nivel= :nivel";
            parametros.put("nivel", nivelB);
        }
        //ESCALA SALARIAL
        if (salarioB != null) {
            where += " and o.escalasalarial= :salario";
            parametros.put("salario", salarioB);
        }

        parametros.put(";where", where);
        parametros.put(";orden", "o.codigo asc");
        int total = 0;
        try {
            total = ejbCargos.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }

        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        listaCargos.setRowCount(total);
        try {
            List<Cargos> aux = ejbCargos.encontarParametros(parametros);
            return aux;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Cargos traer(Integer id) throws ConsultarException {
        return ejbCargos.find(id);
    }

    public String buscar() {
        setListaCargos(new LazyDataModel<Cargos>() {
            @Override
            public List<Cargos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
               
                return carga(i, i1, scs, map);
            }
        });
        return null;
    }

    public String nuevo() {
        cargo = new Cargos();
        cargo.setActivo(Boolean.TRUE);
        listaResponsabilidades = new LinkedList<>();
        listaResponsabilidadesB = new LinkedList<>();
        listaRequerimientos = new LinkedList<>();
        listaRequerimientosB = new LinkedList<>();
        formulario.insertar();
        return null;
    }

    public boolean validar() {
        if (cargo.getCodigo() == null || cargo.getCodigo().isEmpty()) {
            MensajesErrores.advertencia("Código es necesario");
            return true;
        }
//        if (cargo.getCoodigoalterno() == null || cargo.getCoodigoalterno().isEmpty()) {
//            MensajesErrores.advertencia("Código alterno es necesario");
//            return true;
//        }
        if (!formulario.isModificar()) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "upper(o.codigo) like:codigo");
                parametros.put("codigo", "%" + cargo.getCodigo().toUpperCase() + "%");
                List<Cargos> aux = ejbCargos.encontarParametros(parametros);
                if (!(aux.isEmpty())) {
                    MensajesErrores.advertencia("Código de Cargo ya registrado");
                    return true;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (cargo.getNombre() == null || cargo.getNombre().isEmpty()) {
            MensajesErrores.advertencia("Nombre de Cargo es necesario");
            return true;
        }
        if (cargo.getNivel() == null) {
            MensajesErrores.advertencia("Rol es necesario");
            return true;
        }
//        if (cargo.getContactosinternos() == null || cargo.getContactosinternos().isEmpty()) {
//            MensajesErrores.advertencia("Contacto Interno es necesario");
//            return true;
//        }
//        if (cargo.getContactosexternos() == null || cargo.getContactosexternos().isEmpty()) {
//            MensajesErrores.advertencia("Contacto Externo es necesario");
//            return true;
//        }
        if (cargo.getObjetivo() == null || cargo.getObjetivo().isEmpty()) {
            MensajesErrores.advertencia("MISIÓN del cargo es necesario");
            return true;
        }
//        if ((listaResponsabilidades == null) | (listaResponsabilidades.isEmpty())) {
//            MensajesErrores.advertencia("Indique las responsabilidades del cargo");
//            return true;
//        }
//        if (cargo.getDedicacionlab() == null || cargo.getDedicacionlab().isEmpty()) {
//            MensajesErrores.advertencia("Dedicación laboral es necesario");
//            return true;
//        }
//        if (cargo.getDesempenio() == null || cargo.getDesempenio().isEmpty()) {
//            MensajesErrores.advertencia("Función de Desempeño es necesario");
//            return true;
//        }
        if (cargo.getEscalasalarial() == null) {
            MensajesErrores.advertencia("Grupo Ocupacional es necesario");
            return true;
        }
//        if (cargo.getFuncional() == null || cargo.getFuncional().isEmpty()) {
//            MensajesErrores.advertencia("Funcional Laboral es necesario");
//            return true;
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            cargo.setCodigo(cargo.getCodigo().toUpperCase());
            ejbCargos.create(cargo, parametrosSeguridad.getLogueado().getUserid());
            for (Actividadescargos f : listaResponsabilidades) {
                f.setCargo(cargo);
                ejbResponsabilidades.create(f, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Valoresrequerimientos v : listaRequerimientos) {
                v.setComportamiento(Boolean.FALSE);
                ejbValoresRequerimientos.create(v, parametrosSeguridad.getLogueado().getUserid());
            }

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String modificar() {
        cargo = (Cargos) listaCargos.getRowData();
        nivel = cargo.getNivel();
        salario = cargo.getEscalasalarial();
        listaResponsabilidadesB = new LinkedList<>();
        listaRequerimientosB = new LinkedList<>();
        //listaRequerimientosCB = new LinkedList<>();
        try {
            //responsabilidades 
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargo=:cargo and o.activo=true");
            parametros.put("cargo", cargo);
            listaResponsabilidades = ejbResponsabilidades.encontarParametros(parametros);
            //requerimientos
            parametros = new HashMap();
            parametros.put(";where", "o.cargo=:cargo and o.activo=true and o.comportamiento=false");
            parametros.put("cargo", cargo);
            listaRequerimientos = ejbValoresRequerimientos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String imprimir() {
        cargo = (Cargos) listaCargos.getRowData();
        nivel = cargo.getNivel();
        salario = cargo.getEscalasalarial();
        listaResponsabilidadesB = new LinkedList<>();
        listaRequerimientosB = new LinkedList<>();
        //listaRequerimientosCB = new LinkedList<>();
        try {
            //responsabilidades 
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargo=:cargo and o.activo=true");
            parametros.put("cargo", cargo);
            listaResponsabilidades = ejbResponsabilidades.encontarParametros(parametros);
            //requerimientos
            parametros = new HashMap();
            parametros.put(";where", "o.cargo=:cargo and o.activo=true and o.comportamiento=false");
            parametros.put("cargo", cargo);
            listaRequerimientos = ejbValoresRequerimientos.encontarParametros(parametros);
            DocumentoPDF pdf = new DocumentoPDF("DATOS DE IDENTIFICACION DEL PUESTO\n", null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Nombre : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getNombre()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Código Alterno :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getCoodigoalterno()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Rol : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getNivel().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Grupo Ocupacional : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getEscalasalarial().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Ambito : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getContactosexternos()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            pdf.agregaParrafo("MISIÓN :" + cargo.getObjetivo());
            pdf.agregaParrafo("RELACIONES INTERNAS Y EXTERNAS :" + cargo.getObjetivo());
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Nivel de Instrucción : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getNiveleducacion().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Título Requerido : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getAspectoslegales()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Area de conocimiento : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getAspectosotros()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
//            pdf.agregarTabla(null, columnas, 4, 100, null);
//            pdf.agregarTabla(null, columnas, 4, 100, "INSTRUCCIÓN FORMAL REQUERIDA");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Tiempo de Experiencia : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getDesempenio()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Especifidad de la experiencia : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,cargo.getDedicacionlab()));
            pdf.agregarTabla(null, columnas, 4, 100, null);
//            pdf.agregarTabla(null, columnas, 4, 100, "EXPERIENCIA LABORAL");

            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ACTIVIDADES ESENCIALES"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "CONOCIMIENTOS"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "DESTREZAS HABILIDADES"));
            columnas = new LinkedList<>();
            for (Actividadescargos a : listaResponsabilidades) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getActividad()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getConocimiento()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getDestreza()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 3, 100, "ACTIVIDADES");
            pdf.agregaParrafo("\n\n");
            recurso = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.editar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbCargos.edit(cargo, parametrosSeguridad.getLogueado().getUserid());
            for (Actividadescargos f : listaResponsabilidades) {
                f.setCargo(cargo);
                if (f.getId() == null) {
                    ejbResponsabilidades.create(f, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbResponsabilidades.edit(f, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Valoresrequerimientos v : listaRequerimientos) {
                if (v.getId() == null) {
                    ejbValoresRequerimientos.create(v, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbValoresRequerimientos.edit(v, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Valoresrequerimientos v : listaRequerimientosB) {
                if (v.getId() != null) {
                    v.setActivo(Boolean.FALSE);
                    ejbValoresRequerimientos.edit(v, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String eliminar() {
        cargo = (Cargos) listaCargos.getRowData();
        nivel = cargo.getNivel();
        salario = cargo.getEscalasalarial();
        listaResponsabilidadesB = new LinkedList<>();
        listaRequerimientosB = new LinkedList<>();
        try {
            //responsabilidades 
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargo=:cargo and o.activo=true");
            parametros.put("cargo", cargo);
            listaResponsabilidades = ejbResponsabilidades.encontarParametros(parametros);
            //requerimientos
            parametros = new HashMap();
            parametros.put(";where", "o.cargo=:cargo and o.activo=true and o.comportamiento=false");
            parametros.put("cargo", cargo);
            listaRequerimientos = ejbValoresRequerimientos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public String borrar() {

        try {
            cargo.setActivo(Boolean.FALSE);
            ejbCargos.edit(cargo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String cerrar() {
        return "RecursosVista.jsf?faces-redirect=true";
    }

    public String cancelar() {
        buscar();
        formulario.cancelar();
        return null;
    }

    private String UbicacionOrganigrama(Organigrama org, boolean a) {
        String retorno = new String();
        try {
            Map parametros = new HashMap();
            String where = "o.id is not null and o.activo=true";
            if (a) {
                where += " and o.id=:organigrama";
                parametros.put("organigrama", org.getId());
                parametros.put(";where", where);
                List<Organigrama> aux = ejbOrganigrama.encontarParametros(parametros);
                if (aux.isEmpty()) {
                    retorno = "No tiene cargo inferior";
                } else {
                    for (Organigrama o : aux) {
                        if (o.getSuperior() != null) {
                            retorno += o.getSuperior().getNombre() + " ";
                        } else {
                            retorno = "No tiene cargo superior";
                        }
                    }
                }
            }
            if (!a) {
                where += " and o.superior=:organigrama";
                parametros.put("organigrama", org);
                parametros.put(";where", where);
                List<Organigrama> aux = ejbOrganigrama.encontarParametros(parametros);
                if (aux.isEmpty()) {
                    retorno = "No tiene cargo inferior";
                } else {
                    for (Organigrama o : aux) {
                        if (o.getSuperior() != null) {
                            retorno += o.getNombre() + " ";
                        } else {
                            retorno = "No tiene cargo inferior";
                        }
                    }
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    //RESPONSABILIDADES
    public String nuevoResponsabilidades() {
        responsabilidad = new Actividadescargos();
        responsabilidad.setActivo(Boolean.TRUE);
        formularioResponsabilidades.insertar();
        return null;
    }

    public boolean validarResponsabilidades() {
        if (responsabilidad.getActividad() == null || responsabilidad.getActividad().isEmpty()) {
            MensajesErrores.advertencia("Actividad es necesaria");
            return true;
        }
        if (responsabilidad.getDestreza() == null || responsabilidad.getDestreza().isEmpty()) {
            MensajesErrores.advertencia("Destreza es necesaria");
            return true;
        }
        if (responsabilidad.getConocimiento() == null || responsabilidad.getConocimiento().isEmpty()) {
            MensajesErrores.advertencia("Conocimiento es necesario");
            return true;
        }
        return false;
    }

    public String insertarResponsabilidades() {
        if (validarResponsabilidades()) {
            return null;
        }
        responsabilidad.setActivo(Boolean.TRUE);
        responsabilidad.setCargo(cargo);
        listaResponsabilidades.add(getResponsabilidad());
        formularioResponsabilidades.cancelar();
        return null;
    }

    public String modificarResponsabilidades() {
        responsabilidad = listaResponsabilidades.get(formularioResponsabilidades.getFila().getRowIndex());
        formularioResponsabilidades.setIndiceFila(formularioResponsabilidades.getFila().getRowIndex());
        formularioResponsabilidades.editar();
        return null;
    }

    public String grabarResponsabilidades() {
        if (validarResponsabilidades()) {
            return null;
        }
        listaResponsabilidades.set(formularioResponsabilidades.getIndiceFila(), responsabilidad);
        formularioResponsabilidades.cancelar();
        return null;
    }

    public String eliminarResponsabilidades() {
        responsabilidad = listaResponsabilidades.get(formularioResponsabilidades.getFila().getRowIndex());
        formularioResponsabilidades.eliminar();
        return null;
    }

    public String borrarResponsabilidades() {
        listaResponsabilidadesB.add(responsabilidad);
        listaResponsabilidades.remove(responsabilidad);
        formularioResponsabilidades.cancelar();
        return null;
    }

    //REQUERIMIENTOS
    public String nuevoRequerimientos() {
        valorrequerimiento = new Valoresrequerimientos();
        valorrequerimiento.setActivo(Boolean.TRUE);
        valorrequerimiento.setComportamiento(Boolean.FALSE);
        competencia = new Codigos();
        requerimiento = new Requerimientoscargo();
        area = new Areasseleccion();
        formularioRequerimientos.insertar();
        return null;
    }

    public boolean validarRequerimientos() {
        if (valorrequerimiento.getValor() == null || valorrequerimiento.getValor().isEmpty()) {
            MensajesErrores.advertencia("Descripción necesaria");
            return true;
        }
        return false;
    }

    public String insertarRequerimientos() {
        if (validarRequerimientos()) {
            return null;
        }

        listaRequerimientos.add(valorrequerimiento);
        formularioRequerimientos.cancelar();
        return null;
    }

    public String modificarRequerimientos() {
        valorrequerimiento = listaRequerimientos.get(formularioRequerimientos.getFila().getRowIndex());
        formularioRequerimientos.setIndiceFila(formularioRequerimientos.getFila().getRowIndex());
        formularioRequerimientos.editar();
        return null;
    }

    public String grabarRequerimientos() {
        if (validarRequerimientos()) {
            return null;
        }
        listaRequerimientos.set(formularioRequerimientos.getIndiceFila(), valorrequerimiento);
        formularioRequerimientos.cancelar();
        return null;
    }

    public String eliminarRequerimientos() {
        valorrequerimiento = listaRequerimientos.get(formularioRequerimientos.getFila().getRowIndex());
        formularioRequerimientos.eliminar();
        return null;
    }

    public String borrarRequerimientos() {
        listaRequerimientosB.add(valorrequerimiento);
        listaRequerimientos.remove(valorrequerimiento);
        formularioRequerimientos.cancelar();
        return null;
    }

    public SelectItem[] getComboRequerimientos() {
        if (competencia == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", " o.activo=true and o.competencia=:competencia");
        parametros.put("competencia", competencia);
        try {
            return Combos.getSelectItems(ejbRequerimientos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void cambiaCompetencias(ValueChangeEvent event) {
        competencia = (Codigos) event.getNewValue();
        area = null;
    }

    public SelectItem[] getComboCargos() throws ConsultarException {
        Map parametros = new HashMap();
        String where = "o.activo=true ";
        parametros.put(";where", where);
        List<Cargos> aux = ejbCargos.encontarParametros(parametros);
        return Combos.getSelectItems(aux, true);
    }

    public boolean controlAreas() {
        if (competencia.getParametros() == null || competencia.getParametros().isEmpty()) {
            return true;
        } else {
            return false;
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

    /**
     * @return the cargo
     */
    public Cargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargos cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the listaCargos
     */
    public LazyDataModel<Cargos> getListaCargos() {
        return listaCargos;
    }

    /**
     * @param listaCargos the listaCargos to set
     */
    public void setListaCargos(LazyDataModel<Cargos> listaCargos) {
        this.listaCargos = listaCargos;
    }

    /**
     * @return the msjbusqueda
     */
    public String getMsjbusqueda() {
        return msjbusqueda;
    }

    /**
     * @param msjbusqueda the msjbusqueda to set
     */
    public void setMsjbusqueda(String msjbusqueda) {
        this.msjbusqueda = msjbusqueda;
    }

    /**
     * @return the nivel
     */
    public Nivelesgestion getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Nivelesgestion nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the formularioRequerimientos
     */
    public Formulario getFormularioRequerimientos() {
        return formularioRequerimientos;
    }

    /**
     * @param formularioRequerimientos the formularioRequerimientos to set
     */
    public void setFormularioRequerimientos(Formulario formularioRequerimientos) {
        this.formularioRequerimientos = formularioRequerimientos;
    }

    /**
     * @return the salario
     */
    public Escalassalariales getSalario() {
        return salario;
    }

    /**
     * @param salario the salario to set
     */
    public void setSalario(Escalassalariales salario) {
        this.salario = salario;
    }

    /**
     * @return the responsabilidad
     */
    public Actividadescargos getResponsabilidad() {
        return responsabilidad;
    }

    /**
     * @param responsabilidad the responsabilidad to set
     */
    public void setResponsabilidad(Actividadescargos responsabilidad) {
        this.responsabilidad = responsabilidad;
    }

    /**
     * @return the listaResponsabilidades
     */
    public List<Actividadescargos> getListaResponsabilidades() {
        return listaResponsabilidades;
    }

    /**
     * @param listaResponsabilidades the listaResponsabilidades to set
     */
    public void setListaResponsabilidades(List<Actividadescargos> listaResponsabilidades) {
        this.listaResponsabilidades = listaResponsabilidades;
    }

    /**
     * @return the listaResponsabilidadesB
     */
    public List<Actividadescargos> getListaResponsabilidadesB() {
        return listaResponsabilidadesB;
    }

    /**
     * @param listaResponsabilidadesB the listaResponsabilidadesB to set
     */
    public void setListaResponsabilidadesB(List<Actividadescargos> listaResponsabilidadesB) {
        this.listaResponsabilidadesB = listaResponsabilidadesB;
    }

    /**
     * @return the formularioResponsabilidades
     */
    public Formulario getFormularioResponsabilidades() {
        return formularioResponsabilidades;
    }

    /**
     * @param formularioResponsabilidades the formularioResponsabilidades to set
     */
    public void setFormularioResponsabilidades(Formulario formularioResponsabilidades) {
        this.formularioResponsabilidades = formularioResponsabilidades;
    }

    /**
     * @return the area
     */
    public Areasseleccion getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(Areasseleccion area) {
        this.area = area;
    }

    /**
     * @return the listaRequerimientos
     */
    public List<Valoresrequerimientos> getListaRequerimientos() {
        return listaRequerimientos;
    }

    /**
     * @param listaRequerimientos the listaRequerimientos to set
     */
    public void setListaRequerimientos(List<Valoresrequerimientos> listaRequerimientos) {
        this.listaRequerimientos = listaRequerimientos;
    }

    /**
     * @return the listaRequerimientosB
     */
    public List<Valoresrequerimientos> getListaRequerimientosB() {
        return listaRequerimientosB;
    }

    /**
     * @param listaRequerimientosB the listaRequerimientosB to set
     */
    public void setListaRequerimientosB(List<Valoresrequerimientos> listaRequerimientosB) {
        this.listaRequerimientosB = listaRequerimientosB;
    }

    /**
     * @return the listaRequerimientosC
     */
    public List<Valoresrequerimientos> getListaRequerimientosC() {
        return listaRequerimientosC;
    }

    /**
     * @param listaRequerimientosC the listaRequerimientosC to set
     */
    public void setListaRequerimientosC(List<Valoresrequerimientos> listaRequerimientosC) {
        this.listaRequerimientosC = listaRequerimientosC;
    }

    /**
     * @return the listaRequerimientosCB
     */
    public List<Valoresrequerimientos> getListaRequerimientosCB() {
        return listaRequerimientosCB;
    }

    /**
     * @param listaRequerimientosCB the listaRequerimientosCB to set
     */
    public void setListaRequerimientosCB(List<Valoresrequerimientos> listaRequerimientosCB) {
        this.listaRequerimientosCB = listaRequerimientosCB;
    }

    /**
     * @return the formularioRequerimientosC
     */
    public Formulario getFormularioRequerimientosC() {
        return formularioRequerimientosC;
    }

    /**
     * @param formularioRequerimientosC the formularioRequerimientosC to set
     */
    public void setFormularioRequerimientosC(Formulario formularioRequerimientosC) {
        this.formularioRequerimientosC = formularioRequerimientosC;
    }

    /**
     * @return the competencia
     */
    public Codigos getCompetencia() {
        return competencia;
    }

    /**
     * @param competencia the competencia to set
     */
    public void setCompetencia(Codigos competencia) {
        this.competencia = competencia;
    }

    /**
     * @return the valorrequerimiento
     */
    public Valoresrequerimientos getValorrequerimiento() {
        return valorrequerimiento;
    }

    /**
     * @param valorrequerimiento the valorrequerimiento to set
     */
    public void setValorrequerimiento(Valoresrequerimientos valorrequerimiento) {
        this.valorrequerimiento = valorrequerimiento;
    }

    /**
     * @return the requerimiento
     */
    public Requerimientoscargo getRequerimiento() {
        return requerimiento;
    }

    /**
     * @param requerimiento the requerimiento to set
     */
    public void setRequerimiento(Requerimientoscargo requerimiento) {
        this.requerimiento = requerimiento;
    }

    /**
     * @return the controlArea
     */
    public boolean isControlArea() {
        return controlArea;
    }

    /**
     * @param controlArea the controlArea to set
     */
    public void setControlArea(boolean controlArea) {
        this.controlArea = controlArea;
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

    /**
     * @return the nivelB
     */
    public Nivelesgestion getNivelB() {
        return nivelB;
    }

    /**
     * @param nivelB the nivelB to set
     */
    public void setNivelB(Nivelesgestion nivelB) {
        this.nivelB = nivelB;
    }

    /**
     * @return the cdirecta
     */
    public String getCdirecta() {
        return cdirecta;
    }

    /**
     * @param cdirecta the cdirecta to set
     */
    public void setCdirecta(String cdirecta) {
        this.cdirecta = cdirecta;
    }

    /**
     * @return the salarioB
     */
    public Escalassalariales getSalarioB() {
        return salarioB;
    }

    /**
     * @param salarioB the salarioB to set
     */
    public void setSalarioB(Escalassalariales salarioB) {
        this.salarioB = salarioB;
    }

    public SelectItem[] getComboEscalas() {
        if (cargo == null) {
            return null;

        }
        if (cargo.getNivel() == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = " o.activo=true and o.niveldegestion=:niveldegestion";
            parametros.put("niveldegestion", cargo.getNivel());
            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            List<Escalassalariales> listaEscalas = ejbEscalas.encontarParametros(parametros);
            return Combos.getSelectItems(listaEscalas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String formatoId(Cargos c) {
        DecimalFormat df = new DecimalFormat("000");
        return df.format(c.getId());
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
