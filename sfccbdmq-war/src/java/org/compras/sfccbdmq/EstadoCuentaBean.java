/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

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
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "estadoCuentaSfccbdmq")
@ViewScoped
public class EstadoCuentaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Obligaciones> listadoObligaciones;
    private LazyDataModel<Kardexbanco> listadoKardex;
    private LazyDataModel<Anticipos> listadoAnticipos;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private String tipoFecha = "o.inicio";
    private Date desde;
    private Date hasta;
    private double totalAnticipos;
    private double totalObligaciones;
    private double totalKardex;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RascomprasFacade ejbRasCompras;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
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

        String nombreForma = "EstadoCuentaVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
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
    /**
     * Creates a new instance of ContratosEmpleadoBean
     */
    public EstadoCuentaBean() {
        listadoAnticipos = new LazyDataModel<Anticipos>() {

            @Override
            public List<Anticipos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }

        };
        listadoKardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        listadoObligaciones = new LazyDataModel<Obligaciones>() {

            @Override
            public List<Obligaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }

        };
    }

    public String buscar() {
        totalKardex = 0;
        totalAnticipos = 0;
        totalObligaciones = 0;
        listadoAnticipos = new LazyDataModel<Anticipos>() {

            @Override
            public List<Anticipos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                if (proveedorBean.getProveedor() == null) {
                    return null;

                }
                try {
                    String where = "o.fechacontable between :desde and :hasta and o.proveedor=:proveedor";
                    Map parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.fechacontable desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put("proveedor", proveedorBean.getProveedor());
                    parametros.put(";campo", "o.valor");
                    totalAnticipos = ejbAnticipos.sumarCampo(parametros).doubleValue();
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.fechacontable desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put("proveedor", proveedorBean.getProveedor());
                    int total = ejbAnticipos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);

                    listadoAnticipos.setRowCount(total);
                    return ejbAnticipos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }

        };
        listadoKardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                if (proveedorBean.getProveedor() == null) {
                    return null;

                }
                try {
                    String where = "o.fechamov between :desde and :hasta and o.proveedor=:proveedor";
                    Map parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.fechamov desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put("proveedor", proveedorBean.getProveedor());
                    parametros.put(";campo", "o.valor");
                    totalKardex = ejbKardex.sumarCampo(parametros).doubleValue();
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.fechamov desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put("proveedor", proveedorBean.getProveedor());
                    int total = ejbKardex.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoKardex.setRowCount(total);
                    return ejbKardex.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        listadoObligaciones = new LazyDataModel<Obligaciones>() {
            @Override
            public List<Obligaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                if (proveedorBean.getProveedor() == null) {
                    return null;

                }
                try {
                    String where = "o.obligacion.fechacontable between :desde and :hasta and o.obligacion.proveedor=:proveedor";
                    Map parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.obligacion.fechacontable desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    parametros.put("proveedor", proveedorBean.getProveedor());
                    parametros.put(";campo", "o.valor+o.valorimpuesto");
                    totalObligaciones = ejbRasCompras.sumarCampo(parametros).doubleValue();
                    where = "o.fechacontable between :desde and :hasta and o.proveedor=:proveedor";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.fechaemision desc");
                    parametros.put("desde", desde);
                    parametros.put("proveedor", proveedorBean.getProveedor());
                    parametros.put("hasta", hasta);
                    int total = ejbObligaciones.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoObligaciones.setRowCount(total);
                    return ejbObligaciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }

        };

        return null;
    }

    public String imprimir() {
        if (proveedorBean.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            totalKardex = 0;
            totalAnticipos = 0;
            totalObligaciones = 0;
            String where = "o.fechacontable between :desde and :hasta and o.proveedor=:proveedor";
            Map parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechacontable desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("proveedor", proveedorBean.getProveedor());
            parametros.put(";campo", "o.valor");
            totalAnticipos = ejbAnticipos.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechacontable desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("proveedor", proveedorBean.getProveedor());
            List<Anticipos> lAnticipos = ejbAnticipos.encontarParametros(parametros);
            // kardex banco
            where = "o.fechamov between :desde and :hasta and o.proveedor=:proveedor";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechamov desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("proveedor", proveedorBean.getProveedor());
            parametros.put(";campo", "o.valor");
            totalKardex = ejbKardex.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechamov desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("proveedor", proveedorBean.getProveedor());
            List<Kardexbanco> listaKardex = ejbKardex.encontarParametros(parametros);
            where = "o.obligacion.fechacontable between :desde and :hasta and o.obligacion.proveedor=:proveedor";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.obligacion.fechacontable desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("proveedor", proveedorBean.getProveedor());
            parametros.put(";campo", "o.valor+o.valorimpuesto");
            totalObligaciones = ejbRasCompras.sumarCampo(parametros).doubleValue();
            where = "o.fechacontable between :desde and :hasta and o.proveedor=:proveedor";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechaemision desc");
            parametros.put("desde", desde);
            parametros.put("proveedor", proveedorBean.getProveedor());
            parametros.put("hasta", hasta);
            List<Obligaciones> listaObligaciones = ejbObligaciones.encontarParametros(parametros);
            DocumentoPDF pdf = new DocumentoPDF("ESTADO DE CUENTA DE PROVEEDORES\n" + "Periodo desde : " + sdf.format(desde)
                    + "  hasta : " + sdf.format(hasta), null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Código"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,proveedorBean.getProveedor().getCodigo()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Nombre del Proveedor"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,proveedorBean.getProveedor().getEmpresa().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Razon Social"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,proveedorBean.getProveedor().getEmpresa().getNombrecomercial()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"RUC"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,proveedorBean.getProveedor().getEmpresa().getRuc()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total de Facturas:"));
            columnas.add(new AuxiliarReporte("Double", totalObligaciones));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total de Anticipos :"));
            columnas.add(new AuxiliarReporte("Double", totalAnticipos));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total de Transferencias:"));
            columnas.add(new AuxiliarReporte("Double", totalKardex));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "T. Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Retención"));

            columnas = new LinkedList<>();
//            valorModificaciones=0;
            for (Obligaciones o : listaObligaciones) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getFechaemision()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getConcepto()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, (o.getContrato() == null ? "" : o.getContrato().toString())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, (o.getTipodocumento() == null ? "" : o.getTipodocumento().getNombre())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, (o.getDocumento() == null ? "" : o.getDocumento().toString())));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, traerValorObligaciones(o)));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,(o.getNumeror()==null?"":o.getNumeror().toString())));
//                valorModificaciones+=m.getValor().doubleValue();
            }

            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "DETALLE DE FACTURAS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
//            valorModificaciones=0;
            for (Anticipos a : lAnticipos) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getFechaingreso()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, (a.getContrato() == null ? "" : a.getContrato().toString())));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getValor().doubleValue()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "DETALLE DE ANTICIPOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Mov"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "No. Comp. Egr."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
//            valorModificaciones=0;
            for (Kardexbanco k:listaKardex) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, (k.getBanco()==null?"":k.getBanco().toString())));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, (k.getTipomov()==null?"":k.getTipomov().toString())));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_RIGHT, 6, false, (k.getDocumento()== null ? "" : k.getDocumento())));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_RIGHT, 6, false, (k.getEgreso()== null ? "" : k.getEgreso().toString())));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getValor().doubleValue()));
            }
            pdf.agregarTablaReporte(titulos, columnas,6, 100, "DETALLE DE TRANSFERENCIAS");
             recurso = pdf.traerRecurso();
            formularioImprimir.insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EstadoCuentaBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
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
     * @return the listadoObligaciones
     */
    public LazyDataModel<Obligaciones> getListadoObligaciones() {
        return listadoObligaciones;
    }

    /**
     * @param listadoObligaciones the listadoObligaciones to set
     */
    public void setListadoObligaciones(LazyDataModel<Obligaciones> listadoObligaciones) {
        this.listadoObligaciones = listadoObligaciones;
    }

    /**
     * @return the listadoKardex
     */
    public LazyDataModel<Kardexbanco> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(LazyDataModel<Kardexbanco> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    /**
     * @return the listadoAnticipos
     */
    public LazyDataModel<Anticipos> getListadoAnticipos() {
        return listadoAnticipos;
    }

    /**
     * @param listadoAnticipos the listadoAnticipos to set
     */
    public void setListadoAnticipos(LazyDataModel<Anticipos> listadoAnticipos) {
        this.listadoAnticipos = listadoAnticipos;
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
     * @return the totalAnticipos
     */
    public double getTotalAnticipos() {
        return totalAnticipos;
    }

    /**
     * @param totalAnticipos the totalAnticipos to set
     */
    public void setTotalAnticipos(double totalAnticipos) {
        this.totalAnticipos = totalAnticipos;
    }

    /**
     * @return the totalObligaciones
     */
    public double getTotalObligaciones() {
        return totalObligaciones;
    }

    /**
     * @param totalObligaciones the totalObligaciones to set
     */
    public void setTotalObligaciones(double totalObligaciones) {
        this.totalObligaciones = totalObligaciones;
    }

    /**
     * @return the totalKardex
     */
    public double getTotalKardex() {
        return totalKardex;
    }

    /**
     * @param totalKardex the totalKardex to set
     */
    public void setTotalKardex(double totalKardex) {
        this.totalKardex = totalKardex;
    }

    public double getValorObligacion() {
        Obligaciones o = (Obligaciones) listadoObligaciones.getRowData();
        return traerValorObligaciones(o);
    }

    public double traerValorObligaciones(Obligaciones o) {
        String where = "o.obligacion=:obligacion";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            return ejbRasCompras.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(EstadoCuentaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
