/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CustodiosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.entidades.sfccbdmq.Custodios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author luis
 */
@ManagedBean(name = "custodios")
@ViewScoped
public class CustodiosBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;

    private LazyDataModel<Custodios> listaCustodios;
    private String administrativo;
    private Custodios custodio;
    private Resource reporte;
    private Formulario formulario = new Formulario();
    private List<Empleados> listaEmpleados;

    @EJB
    private CustodiosFacade ejbCustodios;
    @EJB
    private EmpleadosFacade ejbEmpleados;

    public CustodiosBean() {
        listaCustodios = new LazyDataModel<Custodios>() {
            @Override
            public List<Custodios> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public List<Custodios> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {

        if (seguridadbean.getLogueado() == null) {
            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
            return null;
        }

        Map parametros = new HashMap();
        String where = "o.id is not null";
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + clave + ") like :" + clave.replaceAll("\\.", "");
            parametros.put(clave.replaceAll("\\.", ""), valor.toUpperCase() + "%");

        }

        if (administrativo != null) {
            where += " and o.ciadministrativo=:administrativo";
            parametros.put("administrativo", administrativo);
        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.id desc");
        int total = 0;

        try {
            total = ejbCustodios.contar(parametros);
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }
        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        listaCustodios.setRowCount(total);

        try {
            List<Custodios> lista = ejbCustodios.encontarParametros(parametros);

            for (Custodios c : lista) {
                c.setCustodioAdministrativo(traerEmpleado(c.getCiadministrativo()) + " [" + c.getCiadministrativo() + "]");
                c.setCustodioBien(traerEmpleado(c.getCibien()) + " [" + c.getCibien() + "]");
            }
            return lista;
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String buscar() {
        listaCustodios = new LazyDataModel<Custodios>() {
            @Override
            public List<Custodios> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };
        exportar();
        return null;
    }

    public String crear() {
        custodio = new Custodios();
        custodio.setCiadministrativo(administrativo);
        formulario.insertar();
        return null;
    }

    public String modificar() {
        custodio = (Custodios) listaCustodios.getRowData();
        formulario.editar();
        return null;
    }

    public String eliminar() {
        custodio = (Custodios) listaCustodios.getRowData();
        formulario.eliminar();
        return null;
    }

    private boolean validar() throws ConsultarException {
        if (custodio.getCiadministrativo() == null) {
            MensajesErrores.advertencia("Ingrese Custodio Administrativo");
            return true;
        }
        if (custodio.getCibien() == null) {
            MensajesErrores.advertencia("Ingrese Custodio de Bienes");
            return true;
        }

        Map parametros = new HashMap();
        String where = "o.ciadministrativo=:cia and o.cibien=:cib";
        parametros.put("cia", custodio.getCiadministrativo());
        parametros.put("cib", custodio.getCibien());
        if (custodio.getId() != null) {
            where += " and o.id!=:id";
            parametros.put("id", custodio.getId());
        }
        parametros.put(";where", where);

        if (ejbCustodios.contar(parametros) > 0) {
            MensajesErrores.advertencia("Registro duplicado");
            return true;
        }

        return false;
    }

    public String insertar() {

        try {
            if (validar()) {
                return null;
            }
            ejbCustodios.create(custodio, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        try {
            if (validar()) {
                return null;
            }
            ejbCustodios.edit(custodio, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            ejbCustodios.remove(custodio, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrarSinConfirmar() {
        try {
            custodio = (Custodios) listaCustodios.getRowData();
            ejbCustodios.remove(custodio, seguridadbean.getLogueado().getUserid());
            MensajesErrores.informacion("Registro borrado");
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String salir() {
        buscar();
        formulario.cancelar();
        return null;
    }

    public String traerEmpleado(String pin) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.pin=:pin");
        parametros.put("pin", pin.trim());
        try {
            List<Empleados> listaEmple = ejbEmpleados.encontarParametros(parametros);
            for (Empleados c : listaEmple) {
                return c.toString();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        try {
            Map parametros = new HashMap();
            String where = "o.id is not null";
            if (administrativo != null) {
                where += " and o.ciadministrativo=:administrativo";
                parametros.put("administrativo", administrativo);
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.id desc");
            try {
                List<Custodios> lista = ejbCustodios.encontarParametros(parametros);

                for (Custodios c : lista) {
                    c.setCustodioAdministrativo(traerEmpleado(c.getCiadministrativo()));
                    c.setCustodioBien(traerEmpleado(c.getCibien()));
                }
                DocumentoXLS xls = new DocumentoXLS("Constataciones");
                List<AuxiliarReporte> campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "N°"));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cu stodio Administrativo"));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Custodio de Bienes"));
                xls.agregarFila(campos, true);
                int fila = 1;
                for (Custodios c : lista) {
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, fila++));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCiadministrativo() != null ? c.getCustodioAdministrativo() + " [" + c.getCiadministrativo() + "]" : ""));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCibien() != null ? c.getCustodioBien() + " [" + c.getCibien() + "]" : ""));
                    xls.agregarFila(campos, false);
                }
                reporte = xls.traerRecurso();
            } catch (ConsultarException ex) {
                Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboCustodiosAdministrativo() {

        Map parametros = new HashMap();
        parametros.put(";where", "o.ciadministrativo is not null");
        parametros.put(";campo", "o.ciadministrativo");
        parametros.put(";suma", "count(o.ciadministrativo)");
        List<Entidades> aux = new LinkedList<>();
        try {
            List<Object[]> resp = ejbCustodios.sumar(parametros);
            for (int i = 0; i < resp.size(); i++) {
                Entidades e = new Entidades();
                e.setPin((String) resp.get(i)[0]);
                e.setNombres(traerEmpleado(e.getPin()) + " [" + e.getPin() + "]");
                aux.add(e);
            }

            if (aux.size() > 0) {
                Collections.sort(aux, new Comparator<Entidades>() {
                    @Override
                    public int compare(Entidades e, Entidades e1) {
                        return e.getNombres().compareTo(e1.getNombres());

                    }
                });
            }

            SelectItem[] items = new SelectItem[aux.size() + 1];
            int i = 0;
            items[0] = new SelectItem(null, "---");
            i++;
            for (Entidades x : aux) {
                items[i++] = new SelectItem(x.getPin(), x.getNombres());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(Custodios.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SelectItem[] getComboEmpleados() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.activo=true");
        parametros.put(";orden", "o.entidad.apellidos asc");

        try {
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            SelectItem[] items = new SelectItem[lista.size() + 1];
            int i = 0;
            items[0] = new SelectItem(null, "---");
            i++;
            for (Empleados x : lista) {
                items[i++] = new SelectItem(x.getEntidad().getPin(), x.toString());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CustodiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void empleadoChangeEventHandler(TextChangeEvent event) {
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.entidad.activo=true and upper(o.entidad.apellidos) like :apellidos");
        parametros.put(";orden", "o.entidad.apellidos asc");
        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 20);
        try {
            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaAdministrativo(ValueChangeEvent event) {
        custodio.setCiadministrativo(null);
        if (listaEmpleados == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Empleados e : listaEmpleados) {
            String acomparar = e.toString();
            if (acomparar.compareToIgnoreCase(newWord) == 0) {
                custodio.setCiadministrativo(e.getEntidad().getPin());
                custodio.setCustodioAdministrativo(e.toString());
            }
        }
    }

    public void cambiaBien(ValueChangeEvent event) {
        custodio.setCibien(null);
        if (listaEmpleados == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Empleados e : listaEmpleados) {
            String acomparar = e.toString();
            if (acomparar.compareToIgnoreCase(newWord) == 0) {
                custodio.setCibien(e.getEntidad().getPin());
                custodio.setCustodioBien(e.toString());
            }
        }
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @return the listaCustodios
     */
    public LazyDataModel<Custodios> getListaCustodios() {
        return listaCustodios;
    }

    /**
     * @return the administrativo
     */
    public String getAdministrativo() {
        return administrativo;
    }

    /**
     * @return the custodio
     */
    public Custodios getCustodio() {
        return custodio;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    /**
     * @param listaCustodios the listaCustodios to set
     */
    public void setListaCustodios(LazyDataModel<Custodios> listaCustodios) {
        this.listaCustodios = listaCustodios;
    }

    /**
     * @param administrativo the administrativo to set
     */
    public void setAdministrativo(String administrativo) {
        this.administrativo = administrativo;
    }

    /**
     * @param custodio the custodio to set
     */
    public void setCustodio(Custodios custodio) {
        this.custodio = custodio;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * @return the listaEmpleados
     */
    public List<Empleados> getListaEmpleados() {
        return listaEmpleados;
    }
}
