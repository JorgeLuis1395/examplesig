/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import org.talento.sfccbdmq.*;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarReporteProveedores;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerafacturasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DetallefacturasFacade;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabecerafacturas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallefacturas;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteImpuestosProveedoresSfccbdmq")
@ViewScoped
public class ReporteImpuestosProveedoresBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    private Formulario formulario = new Formulario();
    private List<AuxiliarReporteProveedores> lista1;
    private List<AuxiliarReporteProveedores> lista2;
    private List<AuxiliarReporteProveedores> lista3;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private Resource reporte;
    private Resource reporte2;
    private Resource reporte3;

    @EJB
    private ValescajasFacade ejbValescajas;
    @EJB
    private CabecerafacturasFacade ejbCabecerafacturas;
    @EJB
    private DetallefacturasFacade ejbDetallefacturas;
    @EJB
    private RetencionescomprasFacade ejbRetencionescompras;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private DocumentoselectronicosFacade ejbDocumentoselectronicos;
    @EJB
    private ObligacionesFacade ejboObligaciones;
    @EJB
    private CodigosFacade ejbCodigos;

    public ReporteImpuestosProveedoresBean() {
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "Beneficiarios SUPA";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        String empleadoString = (String) params.get("x");
//        if (empleadoString != null) {
//            Integer idEmpleado = Integer.parseInt(empleadoString);
//            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
//            return;
//        }

//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//            }
//        }
        setDesde(getConfiguracionBean().getConfiguracion().getPinicialpresupuesto());
        setHasta(getConfiguracionBean().getConfiguracion().getPfinalpresupuesto());
    }

    public String buscar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja.fecha between :desde and :hasta "
                    + " and o.reposicion is not null");
            parametros.put("desde", getDesde());
            parametros.put("hasta", getHasta());
            parametros.put(";orden", "o.reposicion.numerodocumento desc");
            List<Valescajas> listaVales = ejbValescajas.encontarParametros(parametros);

            //reembolso
            setLista1(new LinkedList<>());
            DecimalFormat df = new DecimalFormat("000000000");
            DecimalFormat dfid = new DecimalFormat("00000000");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Valescajas vc : listaVales) {
                AuxiliarReporteProveedores aux = new AuxiliarReporteProveedores();
                aux.setRef("01" + dfid.format(vc.getCaja().getId())); //Que referencia es ?? 
                aux.setCempresa("01");//Siempre 01
                aux.setRelacionado(false);
                if(vc.getProveedor().getEmpresa().getPersonanatural()){
                    aux.setTipoProveedor("01");
                }else{
                    aux.setTipoProveedor("02");
                }
                aux.setFechaemi(vc.getReposicion().getFecha());
                aux.setFechareg(vc.getReposicion().getFecha());
                String tipo = "";
                if (vc.getCaja().getEmpleado().getEntidad().getPin().length() == 10) {
                    tipo = "02"; //cedula
                } else {
                    tipo = "01";//Ruc
                }
                String tipoDocumento = "";
                if (vc.getTipodocumento().getCodigo().equals("FACT")) {
                    tipoDocumento = "01"; //Factura
                } else {
                    if (vc.getTipodocumento().getCodigo().equals("NOTVENT")) {
                        tipoDocumento = "02"; //nota de venta
                    } else {
                        if (vc.getTipodocumento().getCodigo().equals("LIQCOM")) {
                            tipoDocumento = "03"; //liquidacion de compras
                        }
                    }
                }
                aux.setTipoide(tipo);
                aux.setCproveedor(vc.getCaja().getEmpleado().getId().toString()); //que codigo es ??
                aux.setProveedor(vc.getCaja().getEmpleado().toString());
                aux.setNumeroide(vc.getCaja().getEmpleado().getEntidad().getPin());
                aux.setCdocumento("41");//codigo de liquidacion de compras
                aux.setSerie(vc.getReposicion().getEstablecimiento() + "001");
                aux.setAutsri("1123046077");
                aux.setSecuencia(df.format(vc.getReposicion().getNumerodocumento()));
                aux.setRtipoide("01");
                aux.setRnumeroide(vc.getProveedor().getEmpresa().getRuc());
                aux.setRcdocument(tipoDocumento);
                aux.setRserie(vc.getEstablecimiento() + vc.getPuntoemision());
                aux.setRsecuencia(df.format(vc.getNumero()));
                aux.setRautsri(vc.getAutorizacion());
                aux.setRfechaemi(vc.getFecha());
                if (vc.getBaseimponiblecero() == null) {
                    vc.setBaseimponiblecero(BigDecimal.ZERO);
                }
                if (vc.getBaseimponible() == null) {
                    vc.setBaseimponible(BigDecimal.ZERO);
                }
                if (vc.getIva() == null) {
                    vc.setIva(BigDecimal.ZERO);
                }
                aux.setRbasimpng(vc.getBaseimponiblecero().doubleValue()); //base 0
                aux.setRbasimpsg(vc.getBaseimponible().doubleValue()); //base 12
                aux.setRvaliva(vc.getIva().doubleValue()); //valor iva
                aux.setRbasimpno(0); //siempre 0 
                aux.setRvalice(0); // siempre 0
                aux.setRbasimpex(0); // siempre 0
                getLista1().add(aux);
            }

            //reporte ventas sri
            setLista2(new LinkedList<>());
            parametros = new HashMap();
            parametros.put(";where", "o.fecha between :desde and :hasta");
            parametros.put("desde", getDesde());
            parametros.put("hasta", getHasta());
            parametros.put(";orden", "o.nrodocumento desc");
            List<Cabecerafacturas> listaFacturas = ejbCabecerafacturas.encontarParametros(parametros);
            setLista2(new LinkedList<>());
            for (Cabecerafacturas vc : listaFacturas) {
                parametros = new HashMap();
                parametros.put(";where", "o.factura=:factura");
                parametros.put("factura", vc);
                List<Detallefacturas> listaDF = ejbDetallefacturas.encontarParametros(parametros);
                double base0 = 0;
                double base = 0;
                double iva = 0;
                double prcIva = 0;
                for (Detallefacturas dt : listaDF) {
                    if (dt.getValorimpuesto().doubleValue() != 0) {
                        base += (dt.getValor().doubleValue() * dt.getCantidad());
                        iva += ((dt.getValor().doubleValue() * dt.getCantidad()) * (dt.getValorimpuesto().doubleValue() / 100));
                        prcIva = dt.getValorimpuesto().doubleValue();
                    } else {
                        base0 += (dt.getValor().doubleValue());
                        prcIva = 12;
                    }
                }

                AuxiliarReporteProveedores aux = new AuxiliarReporteProveedores();
                aux.setCempresa("01");//Siempre 01
                aux.setNumeroide(vc.getCliente().getEmpresa().getRuc());
                aux.setProveedor(vc.getCliente().toString());//Cliente
                String tipo = "";
                if (vc.getCliente().getEmpresa().getRuc().length() == 13) {
                    tipo = "04"; //Ruc
                } else {
                    if (vc.getCliente().getEmpresa().getRuc().substring(0, 1).equals("P")) {
                        tipo = "06"; //Pasaporte
                    } else {
                        if (vc.getCliente().getEmpresa().getRuc().length() == 10) {
                            tipo = "05"; //Cedula
                        } else {
                            tipo = "07";//Consumidor Final
                        }
                    }
                }
                aux.setTipoide(tipo);
                aux.setRelacionad(Boolean.FALSE);//Siempre falso->0
                aux.setTipoclie("");
                aux.setFechaemi(vc.getFecha());
                String tipoDocumento = "";
                if (vc.getTipodocumento().getCodigo().equals("FACT")) {
                    tipoDocumento = "01"; //Factura
                } else {
                    if (vc.getTipodocumento().getCodigo().equals("NTCR")) {
                        tipoDocumento = "04"; //nota de venta
                    }
                }
                aux.setCdocumento(tipoDocumento);
                aux.setRbasimpex(0); // siempre 0  // bas_nobj
                aux.setRbasimpng(base0); //base 0 //basimpng
                aux.setRbasimpsg(base); //base 12  //basimpsg
                aux.setRvaliva(prcIva); //valor iva  //poriva
                aux.setRbasimpno(iva); // valiva
                aux.setRvalice(0); // siempre 0  // valice
                aux.setRcdocument("1");//documentos
                aux.setRtipoide("01");//tipo
                aux.setRserie(vc.getSucursal() + vc.getPuntoemision());//serie
                aux.setRsecuencia(df.format(vc.getNrodocumento()));//nventa
                aux.setRet_ir(0);//simepre 0
                aux.setRet_iva(0);//simepre 0
                aux.setLiquida(1);//siempre 1
                aux.setRef(""); //retser
                aux.setSerie("");//retsec
                aux.setCproveedor(""); //retaut
                aux.setAutsri("");//retfec
                aux.setCompsolida(0);//Siempre 0
                aux.setCompsolida(0);//siempre 0
                aux.setDocelectro(Boolean.TRUE);//verdaadero
                aux.setAnulada(Boolean.FALSE);//simpre falso
                getLista2().add(aux);
            }

            //compras
            setLista3(new LinkedList<>());
            parametros = new HashMap();
            parametros.put(";where", "o.fechaemision between :desde and :hasta "
                    + " and (o.tipodocumento.codigo in ('FACT','LIQCOM') or o.pagoExterior is not null) "
                    + " and o.estado=2");
            parametros.put("desde", getDesde());
            parametros.put("hasta", getHasta());
            parametros.put(";orden", "o.id desc");
            List<Obligaciones> listaO = ejboObligaciones.encontarParametros(parametros);
            setLista3(new LinkedList<>());
            for (Obligaciones vc : listaO) {
                AuxiliarReporteProveedores aux = new AuxiliarReporteProveedores();
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", vc);
                List<Retencionescompras> listaR = ejbRetencionescompras.encontarParametros(parametros);
                if (!listaR.isEmpty()) {
                    aux.setNumeroide(vc.getProveedor() != null ? vc.getProveedor().getEmpresa().getRuc() : vc.getCompromiso().getBeneficiario().getEntidad().getPin());
                    aux.setProveedor(vc.getProveedor() != null ? vc.getProveedor().toString() : vc.getCompromiso().getBeneficiario().toString());
                    aux.setCempresa("01");
                    
                    
                    aux.setFechaemi(vc.getFechaemision());//fechaemision
                    aux.setFechareg(vc.getFechaingreso());//fecharegristro      
                    String tipo = "";
                    if (vc.getPagoExterior() != null) {
                        tipo = "03"; //Liquidaciones de compras en el exterior
                    } else {
                        if (vc.getTipodocumento().getCodigo().equals("LIQCOM")) {
                            tipo = "41"; //liquidacion de Compras
                        } else {
                            if (vc.getTipodocumento().getCodigo().equals("FACT")) {
                                tipo = "01";//Facturas
                            }
                        }
                    }
                    aux.setCdocumento(tipo);
                    aux.setSerie(vc.getEstablecimiento() + vc.getPuntoemision());
                    aux.setSecuencia(vc.getDocumento() + "");
                    aux.setAutsri(vc.getAutorizacion());
                    String fechaCaducaFactura = "";
                    if (vc.getProveedor() != null) {
                        if (vc.getProveedor().getEmpresa() != null) {
                            if (vc.getTipodocumento() != null) {
                                parametros = new HashMap();
                                parametros.put(";where", "o.empresa=:empresa and o.tipodocumento=:tipodocumento");
                                parametros.put("empresa", vc.getProveedor().getEmpresa());
                                parametros.put("tipodocumento", vc.getTipodocumento());
                                parametros.put(";orden", "o.id desc");
                                List<Autorizaciones> listaA = ejbAutorizaciones.encontarParametros(parametros);
                                if (!listaA.isEmpty()) {
                                    fechaCaducaFactura = sdf.format(listaA.get(0).getFechacaducidad());
                                }
                            }
                        }
                    }
                    aux.setFechavencimientofactura(fechaCaducaFactura);
                    aux.setIdeiva("02");
                    aux.setDeviva("S");
                    aux.setConceptfac(vc.getConcepto());
                    aux.setPoriva(12);
                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion");
                    parametros.put("obligacion", vc);
                    List<Documentoselectronicos> listaD = ejbDocumentoselectronicos.encontarParametros(parametros);
                    double base = 0;
                    double base0 = 0;
                    double iva = 0;
                    for (Documentoselectronicos de : listaD) {
                        if (de.getBaseimponible() == null) {
                            de.setBaseimponible(BigDecimal.ZERO);
                        }
                        if (de.getBaseimponible0() == null) {
                            de.setBaseimponible0(BigDecimal.ZERO);
                        }
                        if (de.getIva() == null) {
                            de.setIva(BigDecimal.ZERO);
                        }
                        base += de.getBaseimponible().doubleValue();
                        base0 += de.getBaseimponible0().doubleValue();
                        iva += de.getIva().doubleValue();

                    }
                    aux.setRbasimpng(base0);//siempre 0
                    aux.setRbasimpsg(base);
                    aux.setRvaliva(iva);
                    aux.setRbasimpno(0);
                    aux.setRbasimpex(0);//Siempre 0
                    aux.setRvalice(0);//siempre 0
                    double valor10 = 0;
                    double valor20 = 0;
                    double valor30 = 0;
                    double valor50 = 0;
                    double valor70 = 0;
                    double valor100 = 0;
                    String etiqueta = "";
                    double basret = 0;
                    double porret = 0;
                    double valret = 0;

                    for (Retencionescompras rc : listaR) {
                        if (rc.getRetencionimpuesto() != null) {
                            if (rc.getRetencionimpuesto().getPorcentaje().doubleValue() == 10) {
                                valor10 += rc.getValoriva().doubleValue();
                            }
                            if (rc.getRetencionimpuesto().getPorcentaje().doubleValue() == 20) {
                                valor20 += rc.getValoriva().doubleValue();
                            }
                            if (rc.getRetencionimpuesto().getPorcentaje().doubleValue() == 30) {
                                valor30 += rc.getValoriva().doubleValue();
                            }
                            if (rc.getRetencionimpuesto().getPorcentaje().doubleValue() == 50) {
                                valor50 += rc.getValoriva().doubleValue();
                            }
                            if (rc.getRetencionimpuesto().getPorcentaje().doubleValue() == 70) {
                                valor70 += rc.getValoriva().doubleValue();
                            }
                            if (rc.getRetencionimpuesto().getPorcentaje().doubleValue() == 100) {
                                valor100 += rc.getValoriva().doubleValue();
                            }
                        }
                        if (rc.getRetencion() != null) {
                            if (rc.getRetencion().getPorcentaje().doubleValue() != 0) {
                                if (!etiqueta.isEmpty()) {
                                    etiqueta += ";";
                                }
                                etiqueta += rc.getRetencion().getEtiqueta();
                                basret += rc.getBaseimponible().doubleValue();
                                porret += rc.getRetencion().getPorcentaje().doubleValue();
                                valret += rc.getValor().doubleValue();
                            }

                        }
                    }
                    aux.setRetiva10(valor10);
                    aux.setRetiva20(valor20);
                    aux.setRetiva30(valor30);
                    aux.setRetiva50(valor50);
                    aux.setRetiva70(valor70);
                    aux.setRetiva100(valor100);
                    aux.setCodret(etiqueta);
                    aux.setBasret(basret);
                    aux.setPorret(porret);
                    aux.setValret(valret);
                    
                    aux.setLiquidar("1");
                    aux.setTipoPagoSRI("01");
                    
                    if (vc.getDocumento() != null) {
                        aux.setRetser(vc.getEstablecimientor() + vc.getPuntor());
                        aux.setNretencion(df.format(vc.getNumeror()));
                        aux.setRetaut(vc.getClaver());
                        if (vc.getNumeror() == null) {
                            vc.setNumeror(0);
                        }
                        aux.setPeriodofec(sdf.format(vc.getFechar()));
                    } else {
                        aux.setRetser("");
                        aux.setNretencion("");
                        aux.setRetaut("");
                        aux.setPeriodofec("");
                    }
                    if (vc.getPagoExterior() != null) {
                        aux.setPaglocext("02");
                        aux.setRegfispre(0);
                        aux.setAplconvdob(0);
                        aux.setPesretnleg(1);
                        aux.setDenopago("20");
                        aux.setCompsolida(0);
                    } else {
                        aux.setPaglocext("01");
                        aux.setRegfispre(0);
                        aux.setAplconvdob(0);
                        aux.setPesretnleg(0);
                        aux.setDenopago("20");
                        aux.setCompsolida(0);
                    }
                    getLista3().add(aux);
                    
//                    aux.setRef("01" + dfid.format(vc.getId())); //id obligacion ??
//                    aux.setReforg("");
//                    aux.setRefnum("");
//                    aux.setTipoasi("");
//                    aux.setNasiento("");           
//                    aux.setCproveedor(vc.getProveedor() != null ? vc.getProveedor().getCodigo() : "");
//                    aux.setAuxiliar(""); //auxiliar??
//                    
//                        aux.setConceptret("");
//                        aux.setFacturas("0");
//                        aux.setDocelectror("0");
//                        aux.setAmbiente("");
//                        aux.setEstado("");
//                        aux.setTpemision("");
//                        aux.setClacceso("");
//                        aux.setFechaaut("");
//                        aux.setRusugre("0005");
//                        aux.setRfecagr("");
//                        aux.setRusumod("0005");
//                        aux.setRfecmod("01/01/2000 00:00:00");
//                        
//                       parametros.put(";where", "upper(o.nombre) like :nombre");
//                        parametros.put("nombre", vc.getPagoExterior().getPaisGeneral().getNombre().substring(0, 4).toUpperCase() + "%");
//                        List<Codigos> listaC = ejbCodigos.encontarParametros(parametros);
//                        if (!listaC.isEmpty()) {
//                            aux.setPaisefecpa(listaC.get(0).getCodigo());
//                            aux.setPaisefpgge(listaC.get(0).getCodigo());
//
//                        } else {
//                            aux.setPaisefecpa("");
//                            aux.setPaisefpgge("");
//                        }
//                        aux.setTiporegi(vc.getPagoExterior().getTipoRegimen().getCodigo());
//                        aux.setPaisefpgpf(""); 
                }
            }

            exportar1();
            exportar2();
            exportar3();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CursosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar1() {
        reporte = null;
        try {
            DocumentoXLS xls = new DocumentoXLS("Gastos Deducibles");
            List<AuxiliarReporte> campos = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "numeroide"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cliente"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tipoide"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "relacionad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tipoclie"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "fechaemi"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cdocumento"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "bas_nobj"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "basimpng"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "basimpsg"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "poriva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "valiva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "valice"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "documentos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tipo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "serie"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "nventa"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ret_ir"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ret_iva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "liquida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retser"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retsec"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retaut"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retfec"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "compsolida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "compdinele"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "docelectro"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "anulada"));
            xls.agregarFila(campos, true);
            for (AuxiliarReporteProveedores e : lista1) {
                DecimalFormat df = new DecimalFormat("########0.00");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", e.getCempresa()));
                campos.add(new AuxiliarReporte("String", e.getNumeroide()));
                campos.add(new AuxiliarReporte("String", e.getProveedor()));
                campos.add(new AuxiliarReporte("String", e.getTipoide()));
                campos.add(new AuxiliarReporte("String", e.isRelacionad() ? "1" : "0"));
                campos.add(new AuxiliarReporte("String", e.getTipoclie()));
                campos.add(new AuxiliarReporte("String", sdf.format(e.getFechaemi())));
                campos.add(new AuxiliarReporte("String", e.getCdocumento()));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpex())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpng())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpsg())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRvaliva())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpno())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRvalice())));
                campos.add(new AuxiliarReporte("String", e.getRcdocument()));
                campos.add(new AuxiliarReporte("String", e.getRtipoide()));
                campos.add(new AuxiliarReporte("String", e.getRserie()));
                campos.add(new AuxiliarReporte("String", e.getRsecuencia()));
                campos.add(new AuxiliarReporte("String", df.format(e.getRet_ir())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRet_iva())));
                campos.add(new AuxiliarReporte("String", df.format(e.getLiquida())));
                campos.add(new AuxiliarReporte("String", e.getRef()));
                campos.add(new AuxiliarReporte("String", e.getSerie()));
                campos.add(new AuxiliarReporte("String", e.getCproveedor()));
                campos.add(new AuxiliarReporte("String", e.getAutsri()));
                campos.add(new AuxiliarReporte("String", df.format(e.getCompsolida())));
                campos.add(new AuxiliarReporte("String", df.format(e.getCompdinele())));
                campos.add(new AuxiliarReporte("String", e.isDocelectro() ? "1" : "0"));
                campos.add(new AuxiliarReporte("String", e.isAnulada() ? "1" : "0"));
                xls.agregarFila(campos, false);
            }
            reporte = xls.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar2() {
        reporte2 = null;
        try {
            DocumentoXLS xls = new DocumentoXLS("Ventas");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ref"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cempresa"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "fechaemi"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "fechareg"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tipoide"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cproveedor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "proveedor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "numeroide"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cdocumento"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "serie"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "autsri"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "secuencia"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rtipoide"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rnumeroide"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rcdocument"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rserie"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rsecuencia"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rautsri"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rfechaemi"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rbasimpng"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rbasimpsg"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rvaliva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rbasimpno"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rvalice"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rbasimpex"));
            xls.agregarFila(campos, true);
            for (AuxiliarReporteProveedores e : lista2) {
                DecimalFormat df = new DecimalFormat("########0.00");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", e.getRef()));
                campos.add(new AuxiliarReporte("String", e.getCempresa()));
                campos.add(new AuxiliarReporte("String", e.getFechaemi() != null ? sdf.format(e.getFechaemi()) : ""));
                campos.add(new AuxiliarReporte("String", e.getFechareg() != null ? sdf.format(e.getFechareg()) : ""));
                campos.add(new AuxiliarReporte("String", e.getTipoide()));
                campos.add(new AuxiliarReporte("String", e.getCproveedor()));
                campos.add(new AuxiliarReporte("String", e.getProveedor()));
                campos.add(new AuxiliarReporte("String", e.getNumeroide()));
                campos.add(new AuxiliarReporte("String", e.getCdocumento()));
                campos.add(new AuxiliarReporte("String", e.getSerie()));
                campos.add(new AuxiliarReporte("String", e.getAutsri()));
                campos.add(new AuxiliarReporte("String", e.getSecuencia()));
                campos.add(new AuxiliarReporte("String", e.getRtipoide()));
                campos.add(new AuxiliarReporte("String", e.getRnumeroide()));
                campos.add(new AuxiliarReporte("String", e.getRcdocument()));
                campos.add(new AuxiliarReporte("String", e.getRserie()));
                campos.add(new AuxiliarReporte("String", e.getRsecuencia()));
                campos.add(new AuxiliarReporte("String", e.getRautsri()));
                campos.add(new AuxiliarReporte("String", e.getRfechaemi() != null ? sdf.format(e.getRfechaemi()) : ""));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpng())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpsg())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRvaliva())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpno())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRvalice())));
                campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpex())));
                xls.agregarFila(campos, false);
            }
            reporte2 = xls.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar3() {
        reporte3 = null;
        try {
            DocumentoXLS xls = new DocumentoXLS("Pagos Empleados");
            List<AuxiliarReporte> campos = new LinkedList<>();
            
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "numeroide"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "proveedor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cempresa"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "D"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "E"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "fechaemision"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "fecharegristro"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cdocumento"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "serie"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "secuencia"));
            
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "autsri"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "fechavencimientofactura"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ideiva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "deviva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "conceptfac"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "poriva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "basimpng"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "basimpsg"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "valiva"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "basimpno"));
            
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "basimpex"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "valice"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retiva10"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retiva20"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retiva30"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retiva50"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retiva70"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retiva100"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "codret"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "basret"));
            
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "porret"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "valret"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AG"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AH"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AI"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AJ"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AK"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retser"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "nretencion"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "retaut"));
            
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "periodofec"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AP"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AQ"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AT"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AU"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AV"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AW"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AX"));
            
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AY"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "AZ"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "paglocext"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "regfispre"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "aplconvdob"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "pesretnleg"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "denopago"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "compsolida"));

//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ref"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "reforg"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "refnum"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tipoasi"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "nasiento"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cproveedor"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "auxiliar"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "paisefecpa"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tiporegi"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "paisefpgge"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "paisefpgpf"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "conceptret"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "facturas"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "docelectro"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ambiente"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "estado"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "tpemision"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "clacceso"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "fechaaut"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rusugre"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rfecagr"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rusumod"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "rfecmod"));
            xls.agregarFila(campos, true);
            for (AuxiliarReporteProveedores e : lista3) {
                if (e != null) {
                    DecimalFormat df = new DecimalFormat("########0.00");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", e.getNumeroide()));
                    campos.add(new AuxiliarReporte("String", e.getProveedor()));
                    campos.add(new AuxiliarReporte("String", e.getCempresa()));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", sdf.format(e.getFechaemi())));
                    campos.add(new AuxiliarReporte("String", sdf.format(e.getFechareg())));
                    campos.add(new AuxiliarReporte("String", e.getCdocumento()));
                    campos.add(new AuxiliarReporte("String", e.getSerie()));
                    campos.add(new AuxiliarReporte("String", e.getSecuencia()));
                    
                    campos.add(new AuxiliarReporte("String", e.getAutsri()));
                    campos.add(new AuxiliarReporte("String", e.getFechavencimientofactura()));
                    campos.add(new AuxiliarReporte("String", e.getIdeiva()));
                    campos.add(new AuxiliarReporte("String", e.getDeviva()));
                    campos.add(new AuxiliarReporte("String", e.getConceptfac()));
                    campos.add(new AuxiliarReporte("String", df.format(e.getPoriva())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpng())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpsg())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRvaliva())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpno())));
                    
                    campos.add(new AuxiliarReporte("String", df.format(e.getRbasimpex())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRvalice())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRetiva10())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRetiva20())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRetiva30())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRetiva50())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRetiva70())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRetiva100())));
                    campos.add(new AuxiliarReporte("String", e.getCodret()));
                    campos.add(new AuxiliarReporte("String", df.format(e.getBasret())));
                    
                    campos.add(new AuxiliarReporte("String", df.format(e.getPorret())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getValret())));
                    campos.add(new AuxiliarReporte("String", "0"));
                    campos.add(new AuxiliarReporte("String", "0"));
                    campos.add(new AuxiliarReporte("String", "0"));
                    campos.add(new AuxiliarReporte("String", "0"));
                    campos.add(new AuxiliarReporte("String", "0"));
                    campos.add(new AuxiliarReporte("String", e.getRetser()));
                    campos.add(new AuxiliarReporte("String", e.getNretencion()));
                    campos.add(new AuxiliarReporte("String", e.getRetaut()));
                    
                    campos.add(new AuxiliarReporte("String", e.getPeriodofec()));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", ""));
                    campos.add(new AuxiliarReporte("String", e.getPaglocext()));
                    campos.add(new AuxiliarReporte("String", df.format(e.getRegfispre())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getAplconvdob())));
                    campos.add(new AuxiliarReporte("String", df.format(e.getPesretnleg())));
                    campos.add(new AuxiliarReporte("String", e.getDenopago()));
                    campos.add(new AuxiliarReporte("String", df.format(e.getCompsolida())));
                    
//                    campos.add(new AuxiliarReporte("String", e.getRef()));
//                    campos.add(new AuxiliarReporte("String", e.getReforg()));
//                    campos.add(new AuxiliarReporte("String", e.getRefnum()));
//                    campos.add(new AuxiliarReporte("String", e.getTipoasi()));
//                    campos.add(new AuxiliarReporte("String", e.getNasiento()));
//                    campos.add(new AuxiliarReporte("String", e.getCproveedor()));
//                    campos.add(new AuxiliarReporte("String", e.getAuxiliar()));
//                    campos.add(new AuxiliarReporte("String", e.getPaisefecpa()));
//                    campos.add(new AuxiliarReporte("String", e.getTiporegi()));
//                    campos.add(new AuxiliarReporte("String", e.getPaisefpgge()));
//                    campos.add(new AuxiliarReporte("String", e.getPaisefpgpf()));
//                    campos.add(new AuxiliarReporte("String", e.getConceptret()));
//                    campos.add(new AuxiliarReporte("String", e.getFacturas()));
//                    campos.add(new AuxiliarReporte("String", e.getDocelectror()));
//                    campos.add(new AuxiliarReporte("String", e.getAmbiente()));
//                    campos.add(new AuxiliarReporte("String", e.getEstado()));
//                    campos.add(new AuxiliarReporte("String", e.getTpemision()));
//                    campos.add(new AuxiliarReporte("String", e.getClacceso()));
//                    campos.add(new AuxiliarReporte("String", e.getFechaaut()));
//                    campos.add(new AuxiliarReporte("String", e.getRusugre()));
//                    campos.add(new AuxiliarReporte("String", e.getRfecagr()));
//                    campos.add(new AuxiliarReporte("String", e.getRusumod()));
//                    campos.add(new AuxiliarReporte("String", e.getRfecmod()));
                    xls.agregarFila(campos, false);
                }
            }
            reporte3 = xls.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
     * @return the reporte2
     */
    public Resource getReporte2() {
        return reporte2;
    }

    /**
     * @param reporte2 the reporte2 to set
     */
    public void setReporte2(Resource reporte2) {
        this.reporte2 = reporte2;
    }

    /**
     * @return the reporte3
     */
    public Resource getReporte3() {
        return reporte3;
    }

    /**
     * @param reporte3 the reporte3 to set
     */
    public void setReporte3(Resource reporte3) {
        this.reporte3 = reporte3;
    }

    /**
     * @return the lista1
     */
    public List<AuxiliarReporteProveedores> getLista1() {
        return lista1;
    }

    /**
     * @param lista1 the lista1 to set
     */
    public void setLista1(List<AuxiliarReporteProveedores> lista1) {
        this.lista1 = lista1;
    }

    /**
     * @return the lista2
     */
    public List<AuxiliarReporteProveedores> getLista2() {
        return lista2;
    }

    /**
     * @param lista2 the lista2 to set
     */
    public void setLista2(List<AuxiliarReporteProveedores> lista2) {
        this.lista2 = lista2;
    }

    /**
     * @return the lista3
     */
    public List<AuxiliarReporteProveedores> getLista3() {
        return lista3;
    }

    /**
     * @param lista3 the lista3 to set
     */
    public void setLista3(List<AuxiliarReporteProveedores> lista3) {
        this.lista3 = lista3;
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
}
