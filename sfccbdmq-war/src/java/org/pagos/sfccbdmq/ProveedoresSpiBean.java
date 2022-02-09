/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import javax.faces.application.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarSpi;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.ProveedoresFacade;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "proveedoresSpiSfccbdmq")
@ViewScoped
public class ProveedoresSpiBean {

    /**
     * Creates a new instance of KardexbancoBean
     */
    public ProveedoresSpiBean() {

    }
    private List<Proveedores> listaProveedores;
    private List<AuxiliarSpi> listaSpi;
    private List<Empleados> listaEmpleados;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private ProveedoresFacade ejbProveedores;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CabeceraempleadosFacade ejbCabeceras;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource recursoTxt;
    private Bancos banco;

    public String buscar() {
        if (banco == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.entidad.apellidos ");
        parametros.put(";where", "o.activo=true and o.tipocontrato is not null");
        try {
            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
            parametros = new HashMap();
             parametros.put(";where", "o.spi=true");
            parametros.put(";orden", "o.empresa.nombre ");
            listaProveedores = ejbProveedores.encontarParametros(parametros);
            generarNuevo();
//            generarAnt();
        } catch (ConsultarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProveedoresSpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return "TesoreriaVista.jsf?faces-redirect=true";
    }

    /**
     * Función que elimina acentos y caracteres especiales de una cadena de
     * texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public String reemplazarCaracteresRaros(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ/%&,.#+-¡?¿()!°|[]";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC                  ";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    public String generarAnt() throws IOException {
//        buscar();
        FileWriter fichero = null;
        PrintWriter pw = null;
        Calendar c = Calendar.getInstance();
//        String directorio = getConfiguracionBean().getConfiguracion().getDirectorio() + "/" + c.getTimeInMillis() + "proveedores.txt";//"/home/edwin/Escritorio/comprobantes/";
        String archivoNombre = "Salida_" + c.getTimeInMillis() + ".csv";
        try {

            fichero = new FileWriter(archivoNombre);
            pw = new PrintWriter(fichero);
            DecimalFormat df = new DecimalFormat("0.00");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            String empresa = configuracionBean.getConfiguracion().getNombre();
//            if (empresa.length() > 30) {
//                empresa = configuracionBean.getConfiguracion().getNombre().substring(0, 29) + "  ";
//            }
            String empresa = "CBDMQ";
            String linea = sdf.format(new Date()) + "\t" + banco.getNumerocuenta() + "\t" + empresa + "";
            pw.println(linea);
            listaSpi = new LinkedList<>();
            for (Proveedores p : listaProveedores) {
                AuxiliarSpi aSpi = new AuxiliarSpi();
                linea = "";
                if ((p.getRucbeneficiario() == null) || (p.getRucbeneficiario().isEmpty())) {
                    MensajesErrores.fatal("No existe cuenta ruc vbeneficiario de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                aSpi.setCedula(p.getRucbeneficiario());
                aSpi.setNombre(p.getNombrebeneficiario());
                linea += p.getRucbeneficiario() + "\t";
                if (p.getNombrebeneficiario().length() > 30) {
                    linea += reemplazarCaracteresRaros(p.getNombrebeneficiario().substring(0, 29)) + "\t";
                } else {
                    linea += reemplazarCaracteresRaros(p.getNombrebeneficiario()) + "\t";
                }
                if ((p.getCtabancaria() == null) || (p.getCtabancaria().isEmpty())) {
                    MensajesErrores.fatal("No existe cuenta bancaria de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                aSpi.setCuenta(p.getCtabancaria());
                linea += p.getCtabancaria() + "\t";
                if (p.getLimitetransferencia() == null) {
                    MensajesErrores.fatal("No existe límite de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                if (p.getBanco() == null) {
                    MensajesErrores.fatal("No existe Banco de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                aSpi.setBanco(p.getBanco().getCodigo());
                aSpi.setValor(p.getLimitetransferencia().toString());
                linea += df.format(p.getLimitetransferencia()) + "\t";
                linea += p.getBanco().getCodigo() + "\t";
                linea += "1";
                pw.println(linea);
                listaSpi.add(aSpi);
            }
            for (Empleados e : listaEmpleados) {
                linea = "";
                AuxiliarSpi aSpi = new AuxiliarSpi();
                aSpi.setCedula(e.getEntidad().getPin());
                linea += e.getEntidad().getPin() + "\t";
                String nombre = e.getEntidad().getApellidos() + " " + e.getEntidad().getNombres();

                if (nombre.length() > 30) {
                    linea += reemplazarCaracteresRaros(nombre.substring(0, 29)) + "\t";
                } else {
                    linea += reemplazarCaracteresRaros(nombre) + "\t";
                }
                String cuenta = traerCuenta(e);
                if ((cuenta == null) || (cuenta.isEmpty())) {
                    MensajesErrores.fatal("No existe cuenta bancaria de transferencia para el empleado : " + e.getEntidad().toString());
                    return null;
                }
                aSpi.setCuenta(cuenta);
                aSpi.setNombre(nombre);

                linea += cuenta + "\t";
                linea += df.format(10000) + "\t";
                String banco = traerBanco(e);
                if ((banco == null) || (banco.isEmpty())) {
                    MensajesErrores.fatal("No existe banco de transferencia para el empleado : " + e.getEntidad().toString());
                    return null;
                }
                aSpi.setBanco(banco);
                aSpi.setValor(df.format(10000));

                linea += banco + "\t";
                linea += "2";
                pw.println(linea);
                listaSpi.add(aSpi);
            }

        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProveedoresSpiBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                MensajesErrores.fatal(e2.getMessage());
                Logger.getLogger(ProveedoresSpiBean.class.getName()).log(Level.SEVERE, null, e2);
            }
        }
        Path path = Paths.get(archivoNombre);
        byte[] data = Files.readAllBytes(path);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        recursoTxt = new Recurso(data);
        formulario.insertar();
        return null;
    }

    public String generarNuevo() throws IOException {
//        buscar();
        FileWriter fichero = null;
//        PrintWriter pw = null;
        Calendar c = Calendar.getInstance();
//        String directorio = getConfiguracionBean().getConfiguracion().getDirectorio() + "/" + c.getTimeInMillis() + "proveedores.txt";//"/home/edwin/Escritorio/comprobantes/";
        String archivoNombre = "Salida_" + c.getTimeInMillis() + ".csv";
        try {

            fichero = new FileWriter(archivoNombre);
//            pw = new PrintWriter(fichero);
            DecimalFormat df = new DecimalFormat("0.00",new DecimalFormatSymbols(Locale.US));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            String empresa = configuracionBean.getConfiguracion().getNombre();
//            if (empresa.length() > 30) {
//                empresa = configuracionBean.getConfiguracion().getNombre().substring(0, 29) + "  ";
//            }
            Calendar cal=Calendar.getInstance();
//            cal.add(Calendar.DATE, -1);
            String empresa = "CBDMQ";
            String linea = sdf.format(cal.getTime()) + "\t" + banco.getNumerocuenta() + "\t" + empresa + "\r\n";
//            pw.println(linea);
            listaSpi = new LinkedList<>();
            for (Proveedores p : listaProveedores) {
                AuxiliarSpi aSpi = new AuxiliarSpi();
//                linea = "";
                if ((p.getRucbeneficiario() == null) || (p.getRucbeneficiario().isEmpty())) {
                    MensajesErrores.fatal("No existe cuenta ruc vbeneficiario de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                aSpi.setCedula(p.getRucbeneficiario());
                aSpi.setNombre(p.getNombrebeneficiario());
                linea += p.getRucbeneficiario() + "\t";
                if (p.getNombrebeneficiario().length() > 30) {
                    linea += reemplazarCaracteresRaros(p.getNombrebeneficiario().substring(0, 29)) + "\t";
                } else {
                    linea += reemplazarCaracteresRaros(p.getNombrebeneficiario()) + "\t";
                }
                if ((p.getCtabancaria() == null) || (p.getCtabancaria().isEmpty())) {
                    MensajesErrores.fatal("No existe cuenta bancaria de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                aSpi.setCuenta(p.getCtabancaria());
                linea += p.getCtabancaria() + "\t";
                if (p.getLimitetransferencia() == null) {
                    MensajesErrores.fatal("No existe límite de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                if (p.getBanco() == null) {
                    MensajesErrores.fatal("No existe Banco de transferencia para : " + p.getEmpresa().getNombre());
                    return null;
                }
                aSpi.setBanco(p.getBanco().getCodigo());
                aSpi.setValor(p.getLimitetransferencia().toString());
                linea += df.format(p.getLimitetransferencia()) + "\t";
                linea += p.getBanco().getCodigo() + "\t";
                linea += "2\r\n";
//                pw.println(linea);
                listaSpi.add(aSpi);
            }
            for (Empleados e : listaEmpleados) {
//                linea = "";
                AuxiliarSpi aSpi = new AuxiliarSpi();
                aSpi.setCedula(e.getEntidad().getPin());
                linea += e.getEntidad().getPin() + "\t";
                String nombre = e.getEntidad().getApellidos() + " " + e.getEntidad().getNombres();

                if (nombre.length() > 30) {
                    linea += reemplazarCaracteresRaros(nombre.substring(0, 29)) + "\t";
                } else {
                    linea += reemplazarCaracteresRaros(nombre) + "\t";
                }
                String cuenta = traerCuenta(e);
                if ((cuenta == null) || (cuenta.isEmpty())) {
                    MensajesErrores.fatal("No existe cuenta bancaria de transferencia para el empleado : " + e.getEntidad().toString());
                    return null;
                }
                aSpi.setCuenta(cuenta);
                aSpi.setNombre(nombre);

                linea += cuenta + "\t";
                linea += df.format(10000) + "\t";
                String banco = traerBanco(e);
                if ((banco == null) || (banco.isEmpty())) {
                    MensajesErrores.fatal("No existe banco de transferencia para el empleado : " + e.getEntidad().toString());
                    return null;
                }
                aSpi.setBanco(banco);
                aSpi.setValor(df.format(10000));

                linea += banco + "\t";
                linea += "1\r\n";
//                pw.println(linea);
                listaSpi.add(aSpi);
            }
            // nuevo sino borrar
            File archivo1 = new File(archivoNombre);
            BufferedWriter bw;
            if (archivo1.exists()) {
                bw = new BufferedWriter(new FileWriter(archivo1));
                bw.write(linea);
            } else {
                bw = new BufferedWriter(new FileWriter(archivo1));
                bw.write(linea);
            }
            bw.close();
        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProveedoresSpiBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                MensajesErrores.fatal(e2.getMessage());
                Logger.getLogger(ProveedoresSpiBean.class.getName()).log(Level.SEVERE, null, e2);
            }
        }
        Path path = Paths.get(archivoNombre);
        byte[] data = Files.readAllBytes(path);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        recursoTxt = new Recurso(data);
        formulario.insertar();
        return null;
    }

    private String traerBanco(Empleados e) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
        parametros.put("empleado", e);
        try {
            List<Cabeceraempleados> cabeceras = ejbCabeceras.encontarParametros(parametros);
            for (Cabeceraempleados c : cabeceras) {
                return c.getCodigo();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProveedoresSpiBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private String traerCuenta(Empleados e) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='NUMCUENTA' and o.empleado=:empleado");
        parametros.put("empleado", e);
        try {
            List<Cabeceraempleados> cabeceras = ejbCabeceras.encontarParametros(parametros);
            for (Cabeceraempleados c : cabeceras) {
                return c.getValortexto();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProveedoresSpiBean.class.getName()).log(Level.SEVERE, null, ex);
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

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ProveedoresSpiVista";
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
     * @return the recursoTxt
     */
    public Resource getRecursoTxt() {
        return recursoTxt;
    }

    /**
     * @param recursoTxt the recursoTxt to set
     */
    public void setRecursoTxt(Resource recursoTxt) {
        this.recursoTxt = recursoTxt;
    }

    /**
     * @return the listaProveedores
     */
    public List<Proveedores> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * @param listaProveedores the listaProveedores to set
     */
    public void setListaProveedores(List<Proveedores> listaProveedores) {
        this.listaProveedores = listaProveedores;
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
     * @return the listaSpi
     */
    public List<AuxiliarSpi> getListaSpi() {
        return listaSpi;
    }

    /**
     * @param listaSpi the listaSpi to set
     */
    public void setListaSpi(List<AuxiliarSpi> listaSpi) {
        this.listaSpi = listaSpi;
    }

    
}
