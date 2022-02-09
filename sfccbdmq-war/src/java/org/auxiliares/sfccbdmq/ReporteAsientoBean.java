/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import org.utilitarios.sfccbdmq.Recurso;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.ClientesBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "utilitarioAsientoSfccbdmq")
@ViewScoped
public class ReporteAsientoBean {

    private Recurso asiento;
    private Resource asientoXls;
    private Cabeceras cabecera;
    private String usuario;
    private Configuracion empresa;
    private boolean incluyeAux;

    private List<Renglones> renglones;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{clientesSfccbdmq}")
    private ClientesBean clientesBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadBean;

    /**
     * @return the asiento
     */
    public Recurso getAsiento() {
        return asiento;
    }

    /**
     * @param asiento the asiento to set
     */
    public void setAsiento(Recurso asiento) {
        this.asiento = asiento;
    }

    /**
     * @return the cabecera
     */
    public Cabeceras getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
    }

    private void ponerAuxiliarAfectacion(List<AuxiliarAfectacion> lista, AuxiliarAfectacion afectacion) {
        for (AuxiliarAfectacion a : lista) {
            if (a.getDetalles().equals(afectacion.getDetalles())) {
                a.setValor(a.getValor() + afectacion.getValor());
                return;
            }
        }
        lista.add(afectacion);
    }

    public void imprimirReporte() {
        if (cabecera == null) {
            MensajesErrores.fatal("No existe Cabecera");
        }
        if (renglones == null) {
            MensajesErrores.fatal("No existe Renglones");
        }
        if (renglones.isEmpty()) {
            MensajesErrores.fatal("No existe Cabecera");
        }
        List<AuxiliarAfectacion> detallesImpresion = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            DocumentoPDF pdf = new DocumentoPDF("Asiento contable expresado en : " + empresa.getExpresado(), empresa.getNombre(), null, usuario, true);
//            pdf.agregaParrafo("Asiento contable : " + empresa.getExpresado() + "\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf.format(cabecera.getFecha())));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Documento"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, cabecera.getTipo() == null ? "" : cabecera.getTipo().getNombre() + " - " + cabecera.getNumero()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Módulo"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, cabecera.getModulo() == null ? "" : cabecera.getModulo().getNombre()
                    + " - " + (cabecera.getIdmodulo() == null ? "" : cabecera.getIdmodulo().toString())));
            pdf.agregarTabla(null, columnas, 2, 100, null);
//            columnas.add(new AuxiliarReporte("String","Descripción"));
//            columnas.add(new AuxiliarReporte("String", cabecera.getDescripcion()));
            pdf.agregaParrafo("Descripción : " + cabecera.getDescripcion() + "\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cent. Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglones == null) {
                renglones = new LinkedList<>();
            }
            columnas = new LinkedList<>();
            for (Renglones r : renglones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                String cuenta = "";
                String auxiliar = r.getAuxiliar();
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                String centrodecosto = "";
                if (r.getDetallecompromiso() != null) {
                    AuxiliarAfectacion a = new AuxiliarAfectacion();
                    a.setDetalles(r.getDetallecompromiso());
                    a.setValor(Math.abs(r.getValor().doubleValue()));
                    a.setPartida(r.getDetallecompromiso().toString());
                    ponerAuxiliarAfectacion(detallesImpresion, a);
                    centrodecosto = r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo()
                            + " - " + r.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
                }
                if (cta != null) {
                    cuenta = cta.getNombre();
                    if (cta.getAuxiliares() != null) {
                        VistaAuxiliares v = seguridadBean.traerAuxiliar(r.getAuxiliar());
                        auxiliar = v == null ? "" : v.getNombre();

                    }
                }
                if (!incluyeAux) {
                    auxiliar = "";
                    centrodecosto = "";
                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, centrodecosto));
                double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
                if (valor > 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)*r.getSigno()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    sumaDebitos += valor*r.getSigno();
                } else {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)*r.getSigno()));
                    sumaCreditos += valor * -1*r.getSigno();
                }

            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "CONTABILIZACION");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            for (AuxiliarAfectacion d : detallesImpresion) {
                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6,
                        false, d.getPartida()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        d.getValor()));

            }
            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "AFECTACION PRESUPUESTARIA");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 3, 100, null);

            pdf.agregaParrafo("\n\nElaborado por : " + cabecera.getUsuario());
            pdf.agregaParrafo("Ciudad : QUITO");
            asiento = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String exportar() {
        try {
            if (renglones == null) {
                MensajesErrores.fatal("No existe Renglones");
            }
            if (renglones.isEmpty()) {
                MensajesErrores.fatal("No existe Cabecera");
            }
            List<AuxiliarAfectacion> detallesImpresion = new LinkedList<>();
            DocumentoXLS xls = new DocumentoXLS("Asientos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cuenta"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre Cuenta"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Referencia"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Auxiliar"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cent. Costo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Debe"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Haber"));
            xls.agregarFila(campos, true);
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglones == null) {
                renglones = new LinkedList<>();
            }
            for (Renglones r : renglones) {
                campos = new LinkedList<>();
                String cuenta = "";
                String auxiliar = r.getAuxiliar();
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                String centrodecosto = "";
                if (r.getDetallecompromiso() != null) {
                    AuxiliarAfectacion a = new AuxiliarAfectacion();
                    a.setDetalles(r.getDetallecompromiso());
                    a.setValor(Math.abs(r.getValor().doubleValue()));
                    a.setPartida(r.getDetallecompromiso().toString());
                    ponerAuxiliarAfectacion(detallesImpresion, a);
                    centrodecosto = r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo()
                            + " - " + r.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
                }
                if (cta != null) {
                    cuenta = cta.getNombre();
                    if (cta.getAuxiliares() != null) {
                        VistaAuxiliares v = seguridadBean.traerAuxiliar(r.getAuxiliar());
                        auxiliar = v == null ? "" : v.getNombre();
                    }
                }
                if (!incluyeAux) {
                    auxiliar = "";
                    centrodecosto = "";
                }
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getCuenta()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cuenta));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getReferencia()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, auxiliar));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, centrodecosto));
                double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
                if (valor > 0) {
                    campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, Math.abs(valor)*r.getSigno()));
                    campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, 0.0));
                    sumaDebitos += valor*r.getSigno();
                } else {
                    campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, 0.0));
                    campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, Math.abs(valor)*r.getSigno()));
                    sumaCreditos += valor * -1*r.getSigno();
                }
                xls.agregarFila(campos, false);
            }
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sumaDebitos));
            campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sumaCreditos));
            xls.agregarFila(campos, false);
            asientoXls = xls.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the empresa
     */
    public Configuracion getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(Configuracion empresa) {
        this.empresa = empresa;
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
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
    }

    /**
     * @return the clientesBean
     */
    public ClientesBean getClientesBean() {
        return clientesBean;
    }

    /**
     * @param clientesBean the clientesBean to set
     */
    public void setClientesBean(ClientesBean clientesBean) {
        this.clientesBean = clientesBean;
    }

    /**
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
    }

    /**
     * @return the seguridadBean
     */
    public ParametrosSfccbdmqBean getSeguridadBean() {
        return seguridadBean;
    }

    /**
     * @param seguridadBean the seguridadBean to set
     */
    public void setSeguridadBean(ParametrosSfccbdmqBean seguridadBean) {
        this.seguridadBean = seguridadBean;
    }

    /**
     * @return the asientoXls
     */
    public Resource getAsientoXls() {
        return asientoXls;
    }

    /**
     * @param asientoXls the asientoXls to set
     */
    public void setAsientoXls(Resource asientoXls) {
        this.asientoXls = asientoXls;
    }

    /**
     * @return the incluyeAux
     */
    public boolean isIncluyeAux() {
        return incluyeAux;
    }

    /**
     * @param incluyeAux the incluyeAux to set
     */
    public void setIncluyeAux(boolean incluyeAux) {
        this.incluyeAux = incluyeAux;
    }
}