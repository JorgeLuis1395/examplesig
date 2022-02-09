/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AperReposLiquiCaja;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.DepositosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Depositos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.ConsultarException;
import org.pagos.sfccbdmq.PagoCajaChicaBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteLiquidacionesCajasSfccbdmq")
@ViewScoped
public class ReporteLiquidacionesCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public ReporteLiquidacionesCajasBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;

    private Formulario formularioReportes = new Formulario();
    private Cajas caja;
    private List<Cajas> listaCajas;
    private Resource reporte;
    private Resource reporteLiq;
    private Resource reporteARL;
    private Perfiles perfil;
    private List<Renglones> listaReglones;
    private List<Renglones> listadoKardex;
    private Kardexbanco kardexDeposito;

    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private DepositosFacade ejbDepositos;

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
        String nombreForma = "ReporteLiquidacionesCajasVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }
    // fin perfiles

    public String buscarCajas() {
        if (caja == null) {
            MensajesErrores.advertencia("Seleccione una caja");
            return null;
        }
        return null;
    }

    public String buscarImprimir() {
        if (caja == null) {
            MensajesErrores.advertencia("Seleccione una caja");
            return null;
        }
        kardexDeposito = caja.getKardexbanco();
        imprimir(caja);
//        grabarEnHoja(caja);
        imprimirSolicitud(caja);
        return null;
    }

    //COMBO CAJAS 
    public SelectItem[] getComboCajas() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.opcion='LIQUIDACION_CAJAS' and o.idmodulo is not null");
            List<Cabeceras> listaLiquidados = ejbCabeceras.encontarParametros(parametros);
            List<Cajas> listaCombo = new LinkedList<>();
            for (Cabeceras c : listaLiquidados) {
                parametros = new HashMap();
                parametros.put(";where", "o.id=:id and o.apertura.liquidado=true");
                parametros.put("id", c.getIdmodulo());
                parametros.put(";orden", "o.empleado.entidad.apellidos ");
                List<Cajas> lista = ejbCajas.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Cajas ca = lista.get(0);
                    listaCombo.add(ca);
                }
            }
            parametros = new HashMap();
            parametros.put(";where", "o.apertura.liquidado=true and o.renglonliquidacion is not null");
            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            List<Cajas> listaRenglon = ejbCajas.encontarParametros(parametros);
            for (Cajas c : listaRenglon) {
                listaCombo.add(c);
            }
            parametros = new HashMap();
            parametros.put(";where", "o.apertura.liquidado=true and o.apertura.observaciones='SALDO INICIAL CAJA CHICA DIC 2017'");

            List<Cajas> listaInicial = ejbCajas.encontarParametros(parametros);
            for (Cajas ca : listaInicial) {
                parametros = new HashMap();
                parametros.put(";where", "o.idmodulo=:idmodulo and o.opcion='LIQUIDACION_CAJAS'");
                parametros.put("idmodulo", ca.getId());
                List<Cabeceras> listaCab = ejbCabeceras.encontarParametros(parametros);
                if (!listaCab.isEmpty()) {
                    listaCombo.add(ca);
                }
            }

            return Combos.getSelectItems(listaCombo, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteLiquidacionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerKardex(Kardexbanco k) {
        if (k == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='KARDEX BANCOS'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteLiquidacionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerLiquidacion(Cajas k) {
        try {
            if (k != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='LIQUIDACION_CAJAS'");
                parametros.put("id", k.getId());
                return ejbRenglones.encontarParametros(parametros);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteLiquidacionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerRenglonesDepositoVarios(Cajas ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);
                if (!rl.isEmpty()) {
                    for (Depositos d : rl) {
                        if (d.getKardexliquidacion() != null) {
                            parametros = new HashMap();
                            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('KARDEX BANCOS')");
                            parametros.put("id", d.getKardexliquidacion().getId());
                            parametros.put(";orden", "o.cuenta desc,o.valor");
                            rl2 = ejbRenglones.encontarParametros(parametros);
                            for (Renglones r : rl2) {
                                rl3.add(r);
                            }
                        }
                    }
                }

                return rl3;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesVarios(Cajas ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);
                if (!rl.isEmpty()) {
                    for (Depositos d : rl) {
                        if (d.getRenglonliquidacion() != null) {
                            parametros = new HashMap();
                            parametros.put(";where", "o.id=:id");
                            parametros.put("id", d.getRenglonliquidacion().getId());
                            parametros.put(";orden", "o.cuenta desc,o.valor");
                            rl2 = ejbRenglones.encontarParametros(parametros);
                            for (Renglones r : rl2) {
                                rl3.add(r);
                            }
                        }
                    }
                }

                return rl3;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String imprimir(Cajas caj) {
        try {
            listaReglones = new LinkedList<>();
            listadoKardex = new LinkedList<>();
            if (kardexDeposito != null) {
                listadoKardex = traerKardex(kardexDeposito);
            }
//            listaReglones = traerLiquidacion(kardexDeposito);
            listaReglones = traerLiquidacion(caj);

            List<Renglones> listadoDepositoVarios = traerRenglonesDepositoVarios(caj);
            List<Renglones> listadoRenglonesVarios = traerRenglonesVarios(caj);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("LIQUIDACIÓN DE CAJA CHICA N° : " + caj.getId(), null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario: "));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, caj.getEmpleado().toString()));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaParrafo("\n\n");

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            Cabeceras c = new Cabeceras();
            if (listadoKardex != null) {
                if (!listadoKardex.isEmpty()) {
                    for (Renglones r : listadoKardex) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                            sumaCreditos += valor * -1;
                        }
                        c = r.getCabecera();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITO N° " + c.getNumero());
                }
            }
            pdf.agregaParrafo("\n\n");
            sumaDebitos = 0.0;
            sumaCreditos = 0.0;
            if (!listaReglones.isEmpty()) {
                columnas = new LinkedList<>();
                for (Renglones r : listaReglones) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        sumaDebitos += valor;
                    } else {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                        sumaCreditos += valor * -1;
                    }
                    c = r.getCabecera();
                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "LIQUIDACIÓN N° " + c.getNumero());
            }
            pdf.agregaParrafo("\n\n");
            //Varios Depositos
            if (listadoDepositoVarios != null) {
                if (!listadoDepositoVarios.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : listadoDepositoVarios) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                            sumaCreditos += valor * -1;
                        }
                        c = r.getCabecera();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITOS N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            //Descuento por Rol
            if (caj.getRenglonliquidacion() != null) {
                titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(caj.getRenglonliquidacion().getCabecera().getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, caj.getRenglonliquidacion().getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(caj.getRenglonliquidacion().getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, caj.getRenglonliquidacion().getCentrocosto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, caj.getRenglonliquidacion().getAuxiliar()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(caj.getRenglonliquidacion().getValor())));

                c = caj.getRenglonliquidacion().getCabecera();
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DESCUENTO POR ROL N° " + c.getNumero());
                pdf.agregaParrafo("\n");
            }
            //Varios Descuentos
            if (listadoRenglonesVarios != null) {
                if (!listadoRenglonesVarios.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : listadoRenglonesVarios) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                            sumaCreditos += valor * -1;
                        }
                        c = r.getCabecera();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DESCUENTOS N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            pdf.agregaParrafo("\n\n");

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporte(pdf.traerRecurso());
            formularioReportes.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteLiquidacionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirSolicitud(Cajas caja) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD ",
                    null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("SOLUCITUD DE APERTURA, REPOSICIÓN O LIQUIDACIÓN DEL FONDO FIJO DE CAJA CHICA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n\n");

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FORMULARIO AF-1"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, " No "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getNumeroliquidacion() != null ? caja.getNumeroliquidacion() + "" : ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Quito, " + sdf.format(caja.getFecha())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Para :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "De Estación:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Unidad Administrativa"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            pdf.agregarTabla(null, columnas, 2, 100, null);

            pdf.agregaTitulo("\n");

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "APERTURA "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "REPOSICIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO SOLICITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "LIQUIDAIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "X"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO DEPOSITADO: "));

            if (kardexDeposito != null) {
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, df.format(kardexDeposito.getValor())));
            } else {
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, "0"));

            }
            pdf.agregarTabla(null, columnas, 4, 100, null);

            pdf.agregaTitulo("\n\n");
            pdf.agregaParrafoCompleto("OBSERVACIONES: " + caja.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 9, Boolean.FALSE);
            pdf.agregaTitulo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "RESPONSABLE DEL FONDO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "JEFE INMEDIATO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre: " + caja.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cc: " + caja.getEmpleado().getEntidad().getPin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cc: "));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteARL = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String grabarEnHoja(Cajas caj) {
        try {
            if (caj.getNumeroliquidacion() == null) {
                caj.setNumeroliquidacion(0);
            }
            AperReposLiquiCaja hoja = new AperReposLiquiCaja();
            hoja.llenar(caj, 3, null, kardexDeposito.getValor().doubleValue());
            reporteLiq = hoja.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(ReporteLiquidacionesCajasBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    /**
     * @return the formularioReportes
     */
    public Formulario getFormularioReportes() {
        return formularioReportes;
    }

    /**
     * @param formularioReportes the formularioReportes to set
     */
    public void setFormularioReportes(Formulario formularioReportes) {
        this.formularioReportes = formularioReportes;
    }

    /**
     * @return the caja
     */
    public Cajas getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(Cajas caja) {
        this.caja = caja;
    }

    /**
     * @return the listaCajas
     */
    public List<Cajas> getListaCajas() {
        return listaCajas;
    }

    /**
     * @param listaCajas the listaCajas to set
     */
    public void setListaCajas(List<Cajas> listaCajas) {
        this.listaCajas = listaCajas;
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
     * @return the reporteLiq
     */
    public Resource getReporteLiq() {
        return reporteLiq;
    }

    /**
     * @param reporteLiq the reporteLiq to set
     */
    public void setReporteLiq(Resource reporteLiq) {
        this.reporteLiq = reporteLiq;
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
     * @return the listaReglones
     */
    public List<Renglones> getListaReglones() {
        return listaReglones;
    }

    /**
     * @param listaReglones the listaReglones to set
     */
    public void setListaReglones(List<Renglones> listaReglones) {
        this.listaReglones = listaReglones;
    }

    /**
     * @return the listadoKardex
     */
    public List<Renglones> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(List<Renglones> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    /**
     * @return the kardexDeposito
     */
    public Kardexbanco getKardexDeposito() {
        return kardexDeposito;
    }

    /**
     * @param kardexDeposito the kardexDeposito to set
     */
    public void setKardexDeposito(Kardexbanco kardexDeposito) {
        this.kardexDeposito = kardexDeposito;
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
     * @return the reporteARL
     */
    public Resource getReporteARL() {
        return reporteARL;
    }

    /**
     * @param reporteARL the reporteARL to set
     */
    public void setReporteARL(Resource reporteARL) {
        this.reporteARL = reporteARL;
    }

}
