/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.presupuestos.sfccbdmq.CompromisosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.AvanceFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.GarantiasFacade;
import org.beans.sfccbdmq.InformesFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.VencimientosFacade;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Avance;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Informes;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Vencimientos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteContratosSfccbdmq")
@ViewScoped
public class ReporteContratosBean {

    /**
     * Creates a new instance of ContratosBean
     */
    public ReporteContratosBean() {
    }
    private Contratos contrato;
    private List<Contratos> modificaciones;
    private List<Garantias> garantias;
    private List<Anticipos> anticipos;
    private List<Obligaciones> obligaciones;
    private List<Compromisos> compromisos;
    private List<Kardexbanco> pagosKardex;
    private List<Certificaciones> certificaciones;
    private List<Informes> informes;
    private List<Pagosvencimientos> pagos;
    private List<Avance> avances;
    private Formulario formulario = new Formulario();
    private double valorAnticipo;
    private double valorPagado;
    private String numero;
    private String proceso;
    private String factura;
    private double porcetntajePagado;
    private double porcetntajeFisisco;
    private double valorCompromisos;
    private double saldoCompromisos;
    private double valorModificaciones;
    private Perfiles perfil;

    private List<Vencimientos> vencimientos;
    @EJB
    private GarantiasFacade ejbGarantias;
    @EJB
    private ContratosFacade ejbContratos;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private InformesFacade ejbInformes;
    @EJB
    private AvanceFacade ejbAvances;
    @EJB
    private DocumentoselectronicosFacade ejbDocElec;
    @EJB
    private RetencionescomprasFacade ejbRetenciones;
    @EJB
    private PagosvencimientosFacade ejbPagos;
    @EJB
    private KardexbancoFacade ejbKardex;

    @EJB
    private VencimientosFacade ejbVencimientos;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource recurso;

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    public String buscar() {
        try {
            Contratos con = null;
            if (numero != null && !numero.trim().isEmpty()) {
                // numero de contrato
                Map parametros = new HashMap();
                parametros.put(";where", "o.numero=:numero and o.padre is null");
                parametros.put("numero", numero);
                List<Contratos> lista = ejbContratos.encontarParametros(parametros);
                for (Contratos c : lista) {
                    con = c;
                }
            }
            if (contrato != null) {
                if (proceso != null && !proceso.trim().isEmpty()) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.proceso=:numero and o.padre is null");
                    parametros.put("numero", proceso);
                    List<Contratos> lista = ejbContratos.encontarParametros(parametros);
                    for (Contratos c : lista) {
                        con = c;
                    }
                }
            }
//            if (con == null) {
//                if (proveedorBean.getProveedor() == null) {
////            MensajesErrores.advertencia("Seleccione una proveedor primero");
////            return null;
//                    if (factura != null) {
//                        Map parametros = new HashMap();
//                        parametros.put(";where", "o.documento=:numero ");
//                        parametros.put("numero", factura);
//                        List<Obligaciones> lista = ejbObligaciones.encontarParametros(parametros);
//                        for (Obligaciones o : lista) {
//                            con = o.getContrato();
//                        }
//                    }
//                }
//            }
            if (con != null) {
                contrato = con;
            }
            if (contrato == null) {
                MensajesErrores.advertencia("Seleccione una contrato primero");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", contrato);
//        parametros.put(";orden", "o.fin desc");

            garantias = ejbGarantias.encontarParametros(parametros);
            informes = ejbInformes.encontarParametros(parametros);
            anticipos = ejbAnticipos.encontarParametros(parametros);
            obligaciones = ejbObligaciones.encontarParametros(parametros);
            avances = ejbAvances.encontarParametros(parametros);
            compromisos = ejbCompromisos.encontarParametros(parametros);
            //2018-03-19 Inclusión de vencimientos
            //traerCertificaciones(compromisos);

            List<Vencimientos> aux = ejbVencimientos.encontarParametros(parametros);
            vencimientos = new LinkedList<>();
            //vencimientos= new LinkedList<>();
            if (!aux.isEmpty()) {

                for (Vencimientos v : aux) {
                    //v.setVencimiento(fechaVencimiento(v.getFecha(), v.getNumerodias()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(v.getFecha());
                    calendar.add(Calendar.DAY_OF_YEAR, v.getNumerodias());
                    v.setVencimiento(calendar.getTime());
                    vencimientos.add(v);
                }
            }
            // cambiar el tipo 
            parametros = new HashMap();
            parametros.put(";where", "o.padre=:contrato");
            parametros.put("contrato", contrato);
            modificaciones = ejbContratos.encontarParametros(parametros);
            pagosKardex = new LinkedList<>();
            Kardexbanco pagoTotalKardex = new Kardexbanco();
            pagoTotalKardex.setObservaciones("TOTAL");
            pagoTotalKardex.setValor(BigDecimal.ZERO);
            double valorPagoTotalKardex = 0;
            valorAnticipo = 0;
            for (Anticipos a : anticipos) {
                valorAnticipo += a.getValor().doubleValue();
                parametros = new HashMap();
                parametros.put(";where", "o.anticipo=:anticipo");
                parametros.put("anticipo", a);
                List<Kardexbanco> kl = ejbKardex.encontarParametros(parametros);
                for (Kardexbanco k : kl) {
                    double valort = pagoTotalKardex.getValor().doubleValue() + k.getValor().doubleValue();
                    pagoTotalKardex.setValor(new BigDecimal(valort));
                    pagosKardex.add(k);
                    valorPagoTotalKardex += valort;
                }

            }
            valorPagado = 0;
            pagos = new LinkedList<>();
            Obligaciones oTotal = new Obligaciones();
            oTotal.setConcepto("Total");
            oTotal.setMonto(0);
            oTotal.setValorRetenciones(0);
            oTotal.setIva(0);
            Pagosvencimientos pagoTotal = new Pagosvencimientos();
            pagoTotal.setValor(BigDecimal.ZERO);
            valorModificaciones = 0;
            for (Contratos m : modificaciones) {
                valorModificaciones += m.getValor().doubleValue();
            }
            for (Obligaciones o : obligaciones) {
                valorPagado += o.getApagar().doubleValue();
                o.setMonto(getValorObligacion(o));

                o.setValorRetenciones(getValorRetencion(o));
                o.setNoCompromiso(getNumeroCompromiso(o));
                o.setIva(valorImpuesto(o).doubleValue());
//                o.setIva(valorImpuesto(o).doubleValue());
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", o);
                List<Pagosvencimientos> lp = ejbPagos.encontarParametros(parametros);
                List<Documentoselectronicos> listaDoc = ejbDocElec.encontarParametros(parametros);
                for (Pagosvencimientos p : lp) {
                    if (p.getKardexbanco() != null) {
                        pagos.add(p);
                        double valorp = p.getValor().doubleValue()
                                - (p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue());
                        double totp = pagoTotalKardex.getValor().doubleValue();
                        pagoTotal.setValor(new BigDecimal(totp + valorp));
                        pagoTotalKardex.setValor(new BigDecimal(totp + valorp));
                        pagosKardex.add(p.getKardexbanco());
                        valorPagoTotalKardex += (totp + valorp);

                    }
                }
                for (Documentoselectronicos d : listaDoc) {
                    o.setCabDoc(d.getCabeccera());
                }
                oTotal.setMonto(o.getMonto() + oTotal.getMonto());
                oTotal.setValorRetenciones(o.getValorRetenciones() + oTotal.getValorRetenciones());
                oTotal.setIva(o.getIva() + oTotal.getIva());
            }
            obligaciones.add(oTotal);
            pagos.add(pagoTotal);
            pagoTotalKardex.setValor(new BigDecimal(valorPagoTotalKardex));
            pagosKardex.add(pagoTotalKardex);
            valorCompromisos = 0;
            saldoCompromisos = 0;
            certificaciones = new LinkedList<>();
            Compromisos cTotal = new Compromisos();
            for (Compromisos c : compromisos) {

                parametros = new HashMap();
                parametros.put(";where", "o.compromiso=:compromiso");
                parametros.put("compromiso", c);
                List<Detallecompromiso> dlComp = ejbDetComp.encontarParametros(parametros);
                double total = 0;
                double saldo = 0;
                for (Detallecompromiso d : dlComp) {
                    valorCompromisos += d.getValor().doubleValue();
                    total += d.getValor().doubleValue();
                    saldoCompromisos += d.getSaldo().doubleValue();
                    saldo += d.getSaldo().doubleValue();
                    //ponerCertificacion(d.getCompromiso().getCertificacion());
//                    ES-2018-03-26 CONTROL PARA CONTRATOS QUE NO TENGAN CERTIFICACION
                    if (d.getDetallecertificacion() != null) {
                        ponerCertificacion(d.getDetallecertificacion().getCertificacion());
                    }
                }
                c.setTotal(total);
                c.setSaldo(saldo);
            }
            cTotal.setTotal(valorCompromisos);
            cTotal.setSaldo(saldoCompromisos);
            cTotal.setMotivo("Total");
            compromisos.add(cTotal);
            porcetntajePagado = (valorAnticipo + valorPagado) / contrato.getValor().doubleValue() * 100;
            porcetntajeFisisco = 0;
            for (Avance a : avances) {

                porcetntajeFisisco += a.getPorcentaje().doubleValue();
            }
            DecimalFormat df = new DecimalFormat("0");
            List<String> lista = new LinkedList<>();
            lista.add("Código : " + proveedorBean.getProveedor().getCodigo());
            lista.add("Nombre : " + proveedorBean.getProveedor().getEmpresa().toString());
            lista.add("RUC : " + proveedorBean.getProveedor().getEmpresa().getRuc());
            // es el reporte
            DocumentoPDF pdf = new DocumentoPDF("REPORTE DE CONTRATOS", lista, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, proveedorBean.getProveedor().getCodigo()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre del Proveedor"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, proveedorBean.getProveedor().getEmpresa().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "RUC"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, proveedorBean.getProveedor().getEmpresa().getRuc()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Titulo"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getTitulo()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Dirección"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getDireccion() == null ? "" : contrato.getDireccion().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Número"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getNumero()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Número Proceso"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getProceso()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha de Inicio :"));
            columnas.add(new AuxiliarReporte("Date", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getInicio()));

            String si = "NO";
            if (contrato.getTieneiva() != null) {
                si = contrato.getTieneiva() ? "SI" : "NO";
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor:"));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getValor().doubleValue()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Incluye IVA:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, si));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha de Fin :"));
            columnas.add(new AuxiliarReporte("Date", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getFin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor Modificaciones:"));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valorModificaciones));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Número de Días:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(getDias())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total Contratos :"));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valorModificaciones + contrato.getValor().doubleValue()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Forma de pago :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getFormapago()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor Anticipado :"));
            columnas.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, getValorAnticipo()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha de Firma:"));
            columnas.add(new AuxiliarReporte("Date", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, contrato.getFirma()));

            String estado = "";

            if (null != contrato.getEstado()) {
                switch (contrato.getEstado()) {
                    case 0:
                        estado = "INGRESADO";
                        break;
                    case 1:
                        estado = "ENTREGA ANTICIPO";
                        break;
                    case 2:
                        estado = "EN PROCESO";
                        break;
                    case 3:
                        estado = "LIQUIDADO";
                        break;
                    case 4:
                        estado = "T. MUTUO ACUERDO";
                        break;
                    case 5:
                        estado = "T. UNILATERAL";
                        break;
                    default:
                        break;
                }
            }
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Valor pagado :"));
            columnas.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, getValorPagado()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Estado :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, estado));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Administrador :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, contrato.getAdministrador().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "% Avance Económico :"));
            columnas.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, getPorcetntajePagado()));

            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "% Avance Físico :"));
            columnas.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, getPorcetntajeFisisco()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Es Obra :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, (contrato.getObra() ? "SI" : "NO")));

//            columnas.add(new AuxiliarReporte("String", ""));
//            columnas.add(new AuxiliarReporte("String", ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            // un parafo
            pdf.agregaParrafo("Objeto : " + contrato.getObjeto() + "\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Objeto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha Modificación"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha Fin Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
//            valorModificaciones=0;
            for (Contratos m : modificaciones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, m.getObjeto()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, m.getInicio()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, m.getFin()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, m.getTipo().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, m.getValor().doubleValue()));
//                valorModificaciones+=m.getValor().doubleValue();
            }

            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "MODIFICATORIOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Objeto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Multa"));

            columnas = new LinkedList<>();
            for (Informes i : informes) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, i.getTexto()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, i.getMulta().doubleValue()));
            }

            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "MULTAS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Descripción"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. Inicio"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. Vencimiento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aseguradora"));

            columnas = new LinkedList<>();
            for (Garantias g : garantias) {
                if (g != null) {
                    if (g.getAseguradora() != null) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getTipo().getNombre()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getTipo().getDescripcion()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getTipo().getId().toString()));
                        columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getDesde()));
                        columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getVencimiento()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, g.getMonto().doubleValue()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, g.getAseguradora().getNombre()));
                    }
                }
            }

            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "GARANTÍAS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valores"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. emisión"));

            columnas = new LinkedList<>();
            for (Anticipos a : anticipos) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getReferencia()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getValor().doubleValue()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getFechaemision()));
            }
            pdf.agregarTabla(lista, columnas, 3, 100, "ANTICIPOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Utilizado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo"));

            columnas = new LinkedList<>();
            for (Certificaciones c : certificaciones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getId().toString()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getFecha()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getMotivo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, c.getMonto()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, c.getSaldo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, c.getMonto() - c.getSaldo()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CERTIFICACIONES");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo"));

            columnas = new LinkedList<>();
            for (Compromisos com : compromisos) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, (com.getId() == null ? "SN" : com.getId().toString())));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, com.getFecha()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, com.getMotivo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, com.getTotal()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, com.getSaldo()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "COMPROMISOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No. Comp"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "T. Doc"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Monto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "I.V.A."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "No. Rete."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Val Ret."));

            columnas = new LinkedList<>();
            for (Obligaciones o : obligaciones) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getFechaemision()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getConcepto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getNoCompromiso()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getTipodocumento() == null ? "" : o.getTipodocumento().getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, o.getDocumento() == null ? "" : o.getDocumento().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getMonto()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getIva()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getNumeror() == null ? "" : o.getNumeror().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, o.getValorRetenciones()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, "DETALLE FACTURAS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha C. E."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No. Comp. Eg."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha SPI"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No SPI"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            for (Kardexbanco k : pagosKardex) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getEgreso() == null ? "" : k.getEgreso().toString()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSpi() == null ? null : k.getSpi().getFecha()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getSpi() == null ? "" : k.getSpi().getId().toString()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, k.getValor().doubleValue()));
            }
//            for (Pagosvencimientos p : pagos) {
//                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco() == null ? null : p.getKardexbanco().getFechamov()));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco() == null ? "" : p.getKardexbanco().getEgreso().toString()));
//                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco() == null ? null : p.getKardexbanco().getSpi() == null ? null : p.getKardexbanco().getSpi().getFecha()));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco() == null ? "" : p.getKardexbanco().getSpi() == null ? "" : p.getKardexbanco().getSpi().getId().toString()));
//                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getKardexbanco() == null ? "" : p.getKardexbanco().getObservaciones()));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, p.getValor().doubleValue()));
//            }
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "PAGOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. Modificación"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "%Avance Físico"));

            columnas = new LinkedList<>();
            for (Avance a : avances) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getFecha()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getPorcentaje().doubleValue()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "AVANCE FÍSICO");
            //recurso = pdf.traerRecurso();

            //VENCIMIENTOS
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. Inicio"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Descripción"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Nro. Días"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "F. Planificación"));
            columnas = new LinkedList<>();
            for (Vencimientos v : vencimientos) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getFecha()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getTexto()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, v.getNumerodias().doubleValue()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getVencimiento()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "VENCIMIENTOS");

            recurso = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void ponerCertificacion(Certificaciones c) {
        for (Certificaciones c1 : certificaciones) {
            if (c.equals(c1)) {
                return;
            }
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put(";campo", "o.valor");
        parametros.put("certificacion", c);
        double valor = 0;
        double saldo = 0;
        try {
            valor = ejbDetCert.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";where", "o.detallecertificacion.certificacion=:certificacion");
            parametros.put(";campo", "o.valor");
            parametros.put("certificacion", c);
            if (ejbDetComp.count() > 0) {
                saldo = ejbDetComp.sumarCampo(parametros).doubleValue();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(ReporteContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        c.setMonto(valor);
        c.setSaldo(saldo);
        certificaciones.add(c);
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
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ReporteContratosVista";
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
     * @return the modificaciones
     */
    public List<Contratos> getModificaciones() {
        return modificaciones;
    }

    /**
     * @param modificaciones the modificaciones to set
     */
    public void setModificaciones(List<Contratos> modificaciones) {
        this.modificaciones = modificaciones;
    }

    /**
     * @return the garantias
     */
    public List<Garantias> getGarantias() {
        return garantias;
    }

    /**
     * @param garantias the garantias to set
     */
    public void setGarantias(List<Garantias> garantias) {
        this.garantias = garantias;
    }

    /**
     * @return the anticipos
     */
    public List<Anticipos> getAnticipos() {
        return anticipos;
    }

    /**
     * @param anticipos the anticipos to set
     */
    public void setAnticipos(List<Anticipos> anticipos) {
        this.anticipos = anticipos;
    }

    /**
     * @return the obligaciones
     */
    public List<Obligaciones> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(List<Obligaciones> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the informes
     */
    public List<Informes> getInformes() {
        return informes;
    }

    /**
     * @param informes the informes to set
     */
    public void setInformes(List<Informes> informes) {
        this.informes = informes;
    }

    /**
     * @return the avances
     */
    public List<Avance> getAvances() {
        return avances;
    }

    /**
     * @param avances the avances to set
     */
    public void setAvances(List<Avance> avances) {
        this.avances = avances;
    }

    /**
     * @return the valorAnticipo
     */
    public double getValorAnticipo() {
        return valorAnticipo;
    }

    /**
     * @param valorAnticipo the valorAnticipo to set
     */
    public void setValorAnticipo(double valorAnticipo) {
        this.valorAnticipo = valorAnticipo;
    }

    /**
     * @return the valorPagado
     */
    public double getValorPagado() {
        return valorPagado;
    }

    /**
     * @param valorPagado the valorPagado to set
     */
    public void setValorPagado(double valorPagado) {
        this.valorPagado = valorPagado;
    }

    /**
     * @return the porcetntajePagado
     */
    public double getPorcetntajePagado() {
        return porcetntajePagado;
    }

    /**
     * @param porcetntajePagado the porcetntajePagado to set
     */
    public void setPorcetntajePagado(double porcetntajePagado) {
        this.porcetntajePagado = porcetntajePagado;
    }

    /**
     * @return the porcetntajeFisisco
     */
    public double getPorcetntajeFisisco() {
        return porcetntajeFisisco;
    }

    /**
     * @param porcetntajeFisisco the porcetntajeFisisco to set
     */
    public void setPorcetntajeFisisco(double porcetntajeFisisco) {
        this.porcetntajeFisisco = porcetntajeFisisco;
    }

    /**
     * @return the compromisos
     */
    public List<Compromisos> getCompromisos() {
        return compromisos;
    }

    /**
     * @param compromisos the compromisos to set
     */
    public void setCompromisos(List<Compromisos> compromisos) {
        this.compromisos = compromisos;
    }

    /**
     * @return the valorCompromisos
     */
    public double getValorCompromisos() {
        return valorCompromisos;
    }

    /**
     * @param valorCompromisos the valorCompromisos to set
     */
    public void setValorCompromisos(double valorCompromisos) {
        this.valorCompromisos = valorCompromisos;
    }

    /**
     * @return the saldoCompromisos
     */
    public double getSaldoCompromisos() {
        return saldoCompromisos;
    }

    /**
     * @param saldoCompromisos the saldoCompromisos to set
     */
    public void setSaldoCompromisos(double saldoCompromisos) {
        this.saldoCompromisos = saldoCompromisos;
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

    public double getDias() {
        if (contrato == null) {
            return 0;
        }
        Calendar cInicio = Calendar.getInstance();
        Calendar cFin = Calendar.getInstance();
        if (contrato.getInicio() != null) {
            cInicio.setTime(contrato.getInicio());
        }
        if (contrato.getFin() != null) {
            cFin.setTime(contrato.getFin());
        }
        return (cFin.getTimeInMillis() - cInicio.getTimeInMillis()) / 86400000;
    }

    public double getValorObligacion(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.baseimponible+o.baseimponible0");

        try {
            retorno = ejbRetenciones.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getValorRetencion(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor+o.valoriva");
        try {
            retorno = ejbRetenciones.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String getNumeroCompromiso(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        String retorno = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        try {
            List<Documentoselectronicos> rl = ejbDocElec.encontarParametros(parametros);
            for (Documentoselectronicos r : rl) {
                if (r.getCabeccera() != null) {
                    String noCompromiso = "[" + r.getCabeccera().getCompromiso().getId().toString() + "]";
                    int respuesta = retorno.indexOf(noCompromiso);
                    if (respuesta == -1) {
                        retorno += noCompromiso;
                    }
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    /**
     * @return the certificaciones
     */
    public List<Certificaciones> getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(List<Certificaciones> certificaciones) {
        this.certificaciones = certificaciones;
    }

    /**
     * @return the pagos
     */
    public List<Pagosvencimientos> getPagos() {
        return pagos;
    }

    /**
     * @param pagos the pagos to set
     */
    public void setPagos(List<Pagosvencimientos> pagos) {
        this.pagos = pagos;
    }

    public BigDecimal valorImpuesto(Obligaciones o) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.iva");
        try {
            return ejbRetenciones.sumarCampo(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteContratosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the valorModificaciones
     */
    public double getValorModificaciones() {
        return valorModificaciones;
    }

    /**
     * @param valorModificaciones the valorModificaciones to set
     */
    public void setValorModificaciones(double valorModificaciones) {
        this.valorModificaciones = valorModificaciones;
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * @param proceso the proceso to set
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * @return the factura
     */
    public String getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(String factura) {
        this.factura = factura;
    }

    /**
     * @return the pagosKardex
     */
    public List<Kardexbanco> getPagosKardex() {
        return pagosKardex;
    }

    /**
     * @param pagosKardex the pagosKardex to set
     */
    public void setPagosKardex(List<Kardexbanco> pagosKardex) {
        this.pagosKardex = pagosKardex;
    }

    /**
     * @return the vencimientos
     */
    public List<Vencimientos> getVencimientos() {
        return vencimientos;
    }

    /**
     * @param vencimientos the vencimientos to set
     */
    public void setVencimientos(List<Vencimientos> vencimientos) {
        this.vencimientos = vencimientos;
    }

    public void traerCertificaciones(List<Compromisos> lc) {
        certificaciones = new LinkedList<>();
        if (!lc.isEmpty()) {
            for (Compromisos com : lc) {
                if (com.getCertificacion() != null) {
                    certificaciones.add(com.getCertificacion());
                }
            }
        }
    }
}
