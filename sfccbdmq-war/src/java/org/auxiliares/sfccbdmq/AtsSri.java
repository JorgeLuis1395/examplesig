/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Reembolsos;

/**
 *
 * @author edwin
 */
public class AtsSri {

    private String idInformante;
    private String anio;
    private String tipoIDInformante;
    private String razonSocial;
    private String codigoOperativo;
    private String mes;
    private String totalVentas;
    private List<DetalleAnulados> anulados;
    private List<DetalleCompras> compras;
    private List<Reembolsos> reembolsos = new LinkedList<>();
    private List<Reembolsos> reembolsosAts = new LinkedList<>();

//    private String numEstabRuc;
    public String getIdInformante() {
        return idInformante;
    }

    public void setIdInformante(String IdInformante) {
        this.idInformante = IdInformante;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getTipoIDInformante() {
        return tipoIDInformante;
    }

    public void setTipoIDInformante(String tipoIDInformante) {
        this.tipoIDInformante = tipoIDInformante;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCodigoOperativo() {
        return codigoOperativo;
    }

    public void setCodigoOperativo(String codigoOperativo) {
        this.codigoOperativo = codigoOperativo;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(String totalVentas) {
        this.totalVentas = totalVentas;
    }

    public List<DetalleAnulados> getAnulados() {
        return anulados;
    }

    public void setAnulados(List<DetalleAnulados> anulados) {
        this.anulados = anulados;
    }

    public List<DetalleCompras> getCompras() {
        return compras;
    }

    public void setCompras(List<DetalleCompras> compras) {
        this.compras = compras;
    }

    /**
     * @return the reembolsos
     */
    public List<Reembolsos> getReembolsos() {
        return reembolsos;
    }

    /**
     * @param reembolsos the reembolsos to set
     */
    public void setReembolsos(List<Reembolsos> reembolsos) {
        this.reembolsos = reembolsos;
    }

//    public String getNumEstabRuc() {
//        return numEstabRuc;
//    }
//
//    public void setNumEstabRuc(String numEstabRuc) {
//        this.numEstabRuc = numEstabRuc;
//    }
    public DetalleAir cargaDetalle(String valRetAir, String baseImpAir, String codRetAir, String porcentajeAir) {
        DetalleAir d = new DetalleAir(valRetAir, baseImpAir, codRetAir, porcentajeAir);
        return d;
    }

    public void cargaCompras(double valRetBien10, String codSustento,
            String secuencial, String idProv, Date fechaEmiRet1,
            String secRetencion1, double valorRetBienes, String puntoEmision,
            Date fechaRegistro, String establecimiento, double baseImpExe,
            double montoIva, String autorizacion, double baseImponible, double valRetServ100,
            String parteRel, double valorRetServicios,
            double valRetServ20, String tpIdProv, double baseNoGraIva,
            String estabRetencion1, String ptoEmiRetencion1, FormasDePago formasDePago,
            String tipoComprobante, double montoIce, double totbasesImpReemb,
            Date fechaEmision, String autRetencion1, List<DetalleAir> air, double baseImpGrav,
            //            double valRetServ50) {
            double valRetServ50, List<Reembolsos> lista, Obligaciones o) {
        if (compras == null) {
            compras = new LinkedList<>();
        }
        DetalleCompras d = new DetalleCompras();
        DecimalFormat df = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.US));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        d.setAir(air);
        d.setAutRetencion1(autRetencion1);
        d.setAutorizacion(autorizacion);
        d.setBaseImpExe(df.format(baseImpExe));
        d.setBaseImpGrav(df.format(baseImpGrav));
        d.setBaseImponible(df.format(baseImponible));
        d.setBaseNoGraIva(df.format(baseNoGraIva));
        d.setCodSustento(codSustento);
        d.setEstabRetencion1(estabRetencion1);
        d.setEstablecimiento(establecimiento);
        d.setFechaEmiRet1(sdf.format(fechaEmiRet1));
        d.setFechaEmision(sdf.format(fechaEmision));
        d.setFechaRegistro(sdf.format(fechaRegistro));
        d.setIdProv(idProv);
        d.setMontoIce(df.format(montoIce));
        d.setMontoIva(df.format(montoIva));
        d.setParteRel(parteRel);
        d.setPtoEmiRetencion1(ptoEmiRetencion1);
        d.setPuntoEmision(puntoEmision);
        d.setSecRetencion1(secRetencion1);
        d.setSecuencial(secuencial);
        d.setTipoComprobante(tipoComprobante);
        d.setTotbasesImpReemb(df.format(totbasesImpReemb));
        d.setTpIdProv(tpIdProv);
        d.setValRetBien10(df.format(valRetBien10));
        d.setValRetServ100(df.format(valRetServ100));
        d.setValRetServ20(df.format(valRetServ20));
        d.setValRetServ50(df.format(valRetServ50));
        d.setValorRetBienes(df.format(valorRetBienes));
        d.setValorRetServicios(df.format(valorRetServicios));
        PagoExterior p = new PagoExterior();
        p.setAplicConvDobTrib("NA");
        p.setPaisEfecPago("NA");
        p.setPagExtSujRetNorLeg("NA");
        p.setPagoRegFis("NA");
        p.setPagoLocExt("01");
        d.setPagoExterior(p);
        d.setFormasDePago(formasDePago);
        d.setObligacion(o);
        compras.add(d);
        if (!lista.isEmpty()) {
            for (Reembolsos r : lista) {
                reembolsos.add(r);
            }
        }

    }

    @Override
    public String toString() {
        String anuladosStr = "";
        if (anulados == null) {
            anulados = new LinkedList<>();
        }
        for (DetalleAnulados d : anulados) {
            anuladosStr += d.toString();
        }
        String comprasStr = "";
        for (DetalleCompras d : compras) {
            for (Reembolsos r : reembolsos) {
                if (r.getObligacion().equals(d.getObligacion())) {
                    reembolsosAts.add(r);
                }
            }
            comprasStr += d.toString();
        }
//        DecimalFormat df = new DecimalFormat("00");
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<iva>\n"
                + "<TipoIDInformante>" + tipoIDInformante + "</TipoIDInformante>\n"
                + "<IdInformante>" + idInformante + "</IdInformante>\n"
                + "<razonSocial>" + razonSocial + "</razonSocial>\n"
                + "<Anio>" + anio + "</Anio>\n"
                + "<Mes>" + mes + "</Mes>\n"
                //                + "<numEstabRuc>" + numEstabRuc + "</numEstabRuc>\n"
                + "<totalVentas>" + totalVentas + "</totalVentas>\n"
                + "<codigoOperativo>" + codigoOperativo + "</codigoOperativo>\n"
                + "<compras>" + comprasStr + "</compras>\n"
                //                + "<ventas>" + "<detalleVentas></detalleVentas>" + "</ventas>\n"
                //                + "<ventasEstablecimiento>\n" + "<ventaEst>\n<codEstab>001</codEstab>\n<ventasEstab>0.00</ventasEstab>\n</ventaEst>" + "</ventasEstablecimiento>\n"
                //                + "<exportaciones>" + "" + "</exportaciones>\n"
                //                + "<recap>" + "" + "</recap>\n"
                //                + "<fideicomisos>" + "" + "</fideicomisos>\n"
                + "<anulados>" + anuladosStr + "</anulados>\n"
                //                + "<rendFinancieros>" + "" + "</rendFinancieros>\n"
                + "</iva>";

    }

    /**
     * @return the reembolsosAts
     */
    public List<Reembolsos> getReembolsosAts() {
        return reembolsosAts;
    }

    /**
     * @param reembolsosAts the reembolsosAts to set
     */
    public void setReembolsosAts(List<Reembolsos> reembolsosAts) {
        this.reembolsosAts = reembolsosAts;
    }

//    public class Anulados {
//
//        private List<DetalleAnulados> detalleAnulados;
//
//        public List<DetalleAnulados> getDetalleAnulados() {
//            return detalleAnulados;
//        }
//
//        public void setDetalleAnulados(List<DetalleAnulados> detalleAnulados) {
//            this.detalleAnulados = detalleAnulados;
//        }
//
//        @Override
//        public String toString() {
//            return "ClassPojo [detalleAnulados = " + detalleAnulados + "]";
//        }
//    }
    public class DetalleAnulados {

        private String establecimiento;
        private String secuencialFin;
        private String tipoComprobante;
        private String secuencialInicio;
        private String puntoEmision;
        private String autorizacion;

        public String getEstablecimiento() {
            return establecimiento;
        }

        public void setEstablecimiento(String establecimiento) {
            this.establecimiento = establecimiento;
        }

        public String getSecuencialFin() {
            return secuencialFin;
        }

        public void setSecuencialFin(String secuencialFin) {
            this.secuencialFin = secuencialFin;
        }

        public String getTipoComprobante() {
            return tipoComprobante;
        }

        public void setTipoComprobante(String tipoComprobante) {
            this.tipoComprobante = tipoComprobante;
        }

        public String getSecuencialInicio() {
            return secuencialInicio;
        }

        public void setSecuencialInicio(String secuencialInicio) {
            this.secuencialInicio = secuencialInicio;
        }

        public String getPuntoEmision() {
            return puntoEmision;
        }

        public void setPuntoEmision(String puntoEmision) {
            this.puntoEmision = puntoEmision;
        }

        public String getAutorizacion() {
            return autorizacion;
        }

        public void setAutorizacion(String autorizacion) {
            this.autorizacion = autorizacion;
        }

        @Override
        public String toString() {
            String retorno = "<establecimiento>" + establecimiento + "</establecimiento>\n";
            retorno += "<secuencialFin>" + secuencialFin + "</secuencialFin>\n";
            retorno += "<tipoComprobante>" + tipoComprobante + "</tipoComprobante>\n";
            retorno += "<secuencialInicio>" + secuencialInicio + "</secuencialInicio>\n";
            retorno += "<puntoEmision>" + puntoEmision + "</puntoEmision>\n";
            retorno += "<autorizacion>" + autorizacion + "</autorizacion>\n";
            return retorno;
        }

    }

//    public class Compras {
//
//        private List<DetalleCompras> detalleCompras;
//
//        public List<DetalleCompras> getDetalleCompras() {
//            return detalleCompras;
//        }
//
//        public void setDetalleCompras(List<DetalleCompras> detalleCompras) {
//            this.detalleCompras = detalleCompras;
//        }
//
//        @Override
//        public String toString() {
//            return "ClassPojo [detalleCompras = " + detalleCompras + "]";
//        }
//    }
    public class DetalleCompras {

        private String valRetBien10;
        private String codSustento;
        private String secuencial;
        private String idProv;
        private String fechaEmiRet1;
        private String secRetencion1;
        private String valorRetBienes;
        private String puntoEmision;
        private String fechaRegistro;
        private String establecimiento;
        private String baseImpExe;
        private String montoIva;
        private String autorizacion;
        private String baseImponible;
        private String valRetServ100;
        private String parteRel;
        private String valorRetServicios;
        private String valRetServ20;
        private String valRetServ50;
        private PagoExterior pagoExterior;
        private String tpIdProv;
        private String baseNoGraIva;
        private String estabRetencion1;
        private String ptoEmiRetencion1;
        private FormasDePago formasDePago;
        private String tipoComprobante;
        private String montoIce;
        private String totbasesImpReemb;
        private String fechaEmision;
        private String autRetencion1;
        private List<DetalleAir> air;
        private String baseImpGrav;
        private Obligaciones obligacion;

        public String getValRetBien10() {
            return valRetBien10;
        }

        public void setValRetBien10(String valRetBien10) {
            this.valRetBien10 = valRetBien10;
        }

        public String getCodSustento() {
            return codSustento;
        }

        public void setCodSustento(String codSustento) {
            this.codSustento = codSustento;
        }

        public String getSecuencial() {
            return secuencial;
        }

        public void setSecuencial(String secuencial) {
            this.secuencial = secuencial;
        }

        public String getIdProv() {
            return idProv;
        }

        public void setIdProv(String idProv) {
            this.idProv = idProv;
        }

        public String getFechaEmiRet1() {
            return fechaEmiRet1;
        }

        public void setFechaEmiRet1(String fechaEmiRet1) {
            this.fechaEmiRet1 = fechaEmiRet1;
        }

        public String getSecRetencion1() {
            return secRetencion1;
        }

        public void setSecRetencion1(String secRetencion1) {
            this.secRetencion1 = secRetencion1;
        }

        public String getValorRetBienes() {
            return valorRetBienes;
        }

        public void setValorRetBienes(String valorRetBienes) {
            this.valorRetBienes = valorRetBienes;
        }

        public String getPuntoEmision() {
            return puntoEmision;
        }

        public void setPuntoEmision(String puntoEmision) {
            this.puntoEmision = puntoEmision;
        }

        public String getFechaRegistro() {
            return fechaRegistro;
        }

        public void setFechaRegistro(String fechaRegistro) {
            this.fechaRegistro = fechaRegistro;
        }

        public String getEstablecimiento() {
            return establecimiento;
        }

        public void setEstablecimiento(String establecimiento) {
            this.establecimiento = establecimiento;
        }

        public String getBaseImpExe() {
            return baseImpExe;
        }

        public void setBaseImpExe(String baseImpExe) {
            this.baseImpExe = baseImpExe;
        }

        public String getMontoIva() {
            return montoIva;
        }

        public void setMontoIva(String montoIva) {
            this.montoIva = montoIva;
        }

        public String getAutorizacion() {
            return autorizacion;
        }

        public void setAutorizacion(String autorizacion) {
            this.autorizacion = autorizacion;
        }

        public String getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(String baseImponible) {
            this.baseImponible = baseImponible;
        }

        public String getValRetServ100() {
            return valRetServ100;
        }

        public void setValRetServ100(String valRetServ100) {
            this.valRetServ100 = valRetServ100;
        }

        public String getParteRel() {
            return parteRel;
        }

        public void setParteRel(String parteRel) {
            this.parteRel = parteRel;
        }

        public String getValorRetServicios() {
            return valorRetServicios;
        }

        public void setValorRetServicios(String valorRetServicios) {
            this.valorRetServicios = valorRetServicios;
        }

        public String getValRetServ20() {
            return valRetServ20;
        }

        public void setValRetServ20(String valRetServ20) {
            this.valRetServ20 = valRetServ20;
        }

        public PagoExterior getPagoExterior() {
            return pagoExterior;
        }

        public void setPagoExterior(PagoExterior pagoExterior) {
            this.pagoExterior = pagoExterior;
        }

        public String getTpIdProv() {
            return tpIdProv;
        }

        public void setTpIdProv(String tpIdProv) {
            this.tpIdProv = tpIdProv;
        }

        public String getBaseNoGraIva() {
            return baseNoGraIva;
        }

        public void setBaseNoGraIva(String baseNoGraIva) {
            this.baseNoGraIva = baseNoGraIva;
        }

        public String getEstabRetencion1() {
            return estabRetencion1;
        }

        public void setEstabRetencion1(String estabRetencion1) {
            this.estabRetencion1 = estabRetencion1;
        }

        public String getPtoEmiRetencion1() {
            return ptoEmiRetencion1;
        }

        public void setPtoEmiRetencion1(String ptoEmiRetencion1) {
            this.ptoEmiRetencion1 = ptoEmiRetencion1;
        }

        public FormasDePago getFormasDePago() {
            return formasDePago;
        }

        public void setFormasDePago(FormasDePago formasDePago) {
            this.formasDePago = formasDePago;
        }

        public String getTipoComprobante() {
            return tipoComprobante;
        }

        public void setTipoComprobante(String tipoComprobante) {
            this.tipoComprobante = tipoComprobante;
        }

        public String getMontoIce() {
            return montoIce;
        }

        public void setMontoIce(String montoIce) {
            this.montoIce = montoIce;
        }

        public String getTotbasesImpReemb() {
            return totbasesImpReemb;
        }

        public void setTotbasesImpReemb(String totbasesImpReemb) {
            this.totbasesImpReemb = totbasesImpReemb;
        }

        public String getFechaEmision() {
            return fechaEmision;
        }

        public void setFechaEmision(String fechaEmision) {
            this.fechaEmision = fechaEmision;
        }

        public String getAutRetencion1() {
            return autRetencion1;
        }

        public void setAutRetencion1(String autRetencion1) {
            this.autRetencion1 = autRetencion1;
        }

        public List<DetalleAir> getAir() {
            return air;
        }

        public void setAir(List<DetalleAir> air) {
            this.air = air;
        }

        public String getBaseImpGrav() {
            return baseImpGrav;
        }

        public void setBaseImpGrav(String baseImpGrav) {
            this.baseImpGrav = baseImpGrav;
        }

        @Override
        public String toString() {
            if (formasDePago == null) {
                formasDePago = new FormasDePago();
            }
            String airStr = "";
            for (DetalleAir d : air) {
                airStr += d.toString();
            }
            DecimalFormat df = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.US));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String retorno = "<detalleCompras>\n";
            retorno += "<codSustento>" + codSustento + "</codSustento>\n";
            retorno += "<tpIdProv>" + tpIdProv + "</tpIdProv>\n";
            retorno += "<idProv>" + idProv + "</idProv>\n";
            retorno += "<tipoComprobante>" + tipoComprobante + "</tipoComprobante>\n";
            retorno += "<parteRel>" + parteRel + "</parteRel>\n";
            retorno += "<fechaRegistro>" + fechaRegistro + "</fechaRegistro>\n";
            retorno += "<establecimiento>" + establecimiento + "</establecimiento>\n";
            retorno += "<puntoEmision>" + puntoEmision + "</puntoEmision>\n";
            retorno += "<secuencial>" + secuencial + "</secuencial>\n";
            retorno += "<fechaEmision>" + fechaEmision + "</fechaEmision>\n";
            retorno += "<autorizacion>" + autorizacion + "</autorizacion>\n";
            retorno += "<baseNoGraIva>" + baseNoGraIva + "</baseNoGraIva>\n";
            retorno += "<baseImponible>" + baseImponible + "</baseImponible>\n";
            retorno += "<baseImpGrav>" + baseImpGrav + "</baseImpGrav>\n";
            retorno += "<baseImpExe>" + baseImpExe + "</baseImpExe>\n";
            retorno += "<montoIce>" + montoIce + "</montoIce>\n";
            retorno += "<montoIva>" + montoIva + "</montoIva>\n";
            retorno += "<valRetBien10>" + valRetBien10 + "</valRetBien10>\n";
            retorno += "<valRetServ20>" + valRetServ20 + "</valRetServ20>\n";
            retorno += "<valorRetBienes>" + valorRetBienes + "</valorRetBienes>\n";
            retorno += "<valRetServ50>" + valRetServ50 + "</valRetServ50>\n";
            retorno += "<valorRetServicios>" + valorRetServicios + "</valorRetServicios>\n";
            retorno += "<valRetServ100>" + valRetServ100 + "</valRetServ100>\n";
            retorno += "<totbasesImpReemb>" + totbasesImpReemb + "</totbasesImpReemb>\n";
            //Saber si es exterior 
            retorno += "<pagoExterior>" + pagoExterior.toString() + "</pagoExterior>\n";
//            double mil=Double.parseDouble(baseImpGrav)+Double.parseDouble(montoIva)+Double.parseDouble(montoIce);
            double mil = Double.parseDouble(baseImpGrav) + Double.parseDouble(baseImponible) + Double.parseDouble(montoIva);
            if (mil > 1000) {
                retorno += "<formasDePago>" + formasDePago.toString() + "</formasDePago>\n";
            }
            retorno += "<air>" + airStr + "</air>\n";
            retorno += "<estabRetencion1>" + estabRetencion1 + "</estabRetencion1>\n";
            retorno += "<ptoEmiRetencion1>" + ptoEmiRetencion1 + "</ptoEmiRetencion1>\n";
            retorno += "<secRetencion1>" + secRetencion1 + "</secRetencion1>\n";
            retorno += "<autRetencion1>" + autRetencion1 + "</autRetencion1>\n";
            retorno += "<fechaEmiRet1>" + fechaEmiRet1 + "</fechaEmiRet1>\n";
            if (!reembolsosAts.isEmpty()) {
                retorno += "<reembolsos>\n";

                for (Reembolsos r : reembolsosAts) {
                    int longitudRuc = r.getRuc().trim().length();
                    String tipoComprobante = "";
                    if (longitudRuc == 10) {
                        tipoComprobante = "02";
                    } else if (longitudRuc == 13) {
                        tipoComprobante = "01";
                    } else {
                        tipoComprobante = "03";
                    }
                    if(r.getAutorizacion() == null || r.getAutorizacion().equals("0")){
                        r.setAutorizacion("9999999999");
                    }
                    
                    retorno += "<reembolso>\n";
                    retorno += "<tipoComprobanteReemb>" + r.getComprobante().getParametros() + "</tipoComprobanteReemb>\n";
                    retorno += "<tpIdProvReemb>" + tipoComprobante + "</tpIdProvReemb>\n";
                    retorno += "<idProvReemb>" + r.getRuc() + "</idProvReemb>\n";
                    retorno += "<establecimientoReemb>" + r.getEstablecimiento() + "</establecimientoReemb>\n";
                    retorno += "<puntoEmisionReemb>" + r.getPunto() + "</puntoEmisionReemb>\n";
                    retorno += "<secuencialReemb>" + r.getNumero().substring(4, 13) + "</secuencialReemb>\n";
                    retorno += "<fechaEmisionReemb>" + sdf.format(r.getFechaemision()) + "</fechaEmisionReemb>\n";
                    retorno += "<autorizacionReemb>" + r.getAutorizacion() + "</autorizacionReemb>\n";
                    retorno += "<baseImponibleReemb>" + df.format(r.getBaseimponible0()) + "</baseImponibleReemb>\n";
                    retorno += "<baseImpGravReemb>" + df.format(r.getBaseimponible()) + "</baseImpGravReemb>\n";
                    retorno += "<baseNoGraIvaReemb>" + "0.00" + "</baseNoGraIvaReemb>\n";
                    retorno += "<baseImpExeReemb>" + "0.00" + "</baseImpExeReemb>\n";
                    retorno += "<montoIceRemb>" + "0.00" + "</montoIceRemb>\n";
                    retorno += "<montoIvaRemb>" + df.format(r.getValoriva()) + "</montoIvaRemb>\n";
                    retorno += "</reembolso>\n";
                }
                retorno += "</reembolsos>\n";
            }
            retorno += "</detalleCompras>\n";
            return retorno;

        }

        /**
         * @return the valRetServ50
         */
        public String getValRetServ50() {
            return valRetServ50;
        }

        /**
         * @param valRetServ50 the valRetServ50 to set
         */
        public void setValRetServ50(String valRetServ50) {
            this.valRetServ50 = valRetServ50;
        }

        /**
         * @return the obligacion
         */
        public Obligaciones getObligacion() {
            return obligacion;
        }

        /**
         * @param obligacion the obligacion to set
         */
        public void setObligacion(Obligaciones obligacion) {
            this.obligacion = obligacion;
        }
    }

    public class PagoExterior {

        private String paisEfecPago;
        private String pagoLocExt;
        private String aplicConvDobTrib;
        private String pagExtSujRetNorLeg;
        private String pagoRegFis;

        public String getPaisEfecPago() {
            return paisEfecPago;
        }

        public void setPaisEfecPago(String paisEfecPago) {
            this.paisEfecPago = paisEfecPago;
        }

        public String getPagoLocExt() {
            return pagoLocExt;
        }

        public void setPagoLocExt(String pagoLocExt) {
            this.pagoLocExt = pagoLocExt;
        }

        public String getAplicConvDobTrib() {
            return aplicConvDobTrib;
        }

        public void setAplicConvDobTrib(String aplicConvDobTrib) {
            this.aplicConvDobTrib = aplicConvDobTrib;
        }

        public String getPagExtSujRetNorLeg() {
            return pagExtSujRetNorLeg;
        }

        public void setPagExtSujRetNorLeg(String pagExtSujRetNorLeg) {
            this.pagExtSujRetNorLeg = pagExtSujRetNorLeg;
        }

        public String getPagoRegFis() {
            return pagoRegFis;
        }

        public void setPagoRegFis(String pagoRegFis) {
            this.pagoRegFis = pagoRegFis;
        }

        @Override
        public String toString() {
            String retorno = "<pagoLocExt>" + pagoLocExt + "</pagoLocExt>\n";
            retorno += "<paisEfecPago>" + paisEfecPago + "</paisEfecPago>\n";
            retorno += "<aplicConvDobTrib>" + aplicConvDobTrib + "</aplicConvDobTrib>\n";
            retorno += "<pagExtSujRetNorLeg>" + "NA" + "</pagExtSujRetNorLeg>\n";
            retorno += "<pagoRegFis>" + pagoRegFis + "</pagoRegFis>\n";
            return retorno;
        }
    }

//    public class Air {
//
//        private DetalleAir detalleAir;
//
//        public DetalleAir getDetalleAir() {
//            return detalleAir;
//        }
//
//        public void setDetalleAir(DetalleAir detalleAir) {
//            this.detalleAir = detalleAir;
//        }
//
//        @Override
//        public String toString() {
//            return "ClassPojo [detalleAir = " + detalleAir + "]";
//        }
//    }
    public class DetalleAir {

        private String valRetAir;
        private String baseImpAir;
        private String codRetAir;
        private String porcentajeAir;

        public DetalleAir(String valRetAir, String baseImpAir, String codRetAir, String porcentajeAir) {
            this.valRetAir = valRetAir;
            this.baseImpAir = baseImpAir;
            this.codRetAir = codRetAir;
            this.porcentajeAir = porcentajeAir;
        }

        public String getValRetAir() {
            return valRetAir;
        }

        public void setValRetAir(String valRetAir) {
            this.valRetAir = valRetAir;
        }

        public String getBaseImpAir() {
            return baseImpAir;
        }

        public void setBaseImpAir(String baseImpAir) {
            this.baseImpAir = baseImpAir;
        }

        public String getCodRetAir() {
            return codRetAir;
        }

        public void setCodRetAir(String codRetAir) {
            this.codRetAir = codRetAir;
        }

        public String getPorcentajeAir() {
            return porcentajeAir;
        }

        public void setPorcentajeAir(String porcentajeAir) {
            this.porcentajeAir = porcentajeAir;
        }

        @Override
        public String toString() {
            String retorno = "<detalleAir>\n<codRetAir>" + codRetAir + "</codRetAir>\n";
            retorno += "<baseImpAir>" + baseImpAir + "</baseImpAir>\n";
            retorno += "<porcentajeAir>" + porcentajeAir + "</porcentajeAir>\n";
            retorno += "<valRetAir>" + valRetAir + "</valRetAir>\n</detalleAir>\n";
            return retorno;
        }
    }

    public class FormasDePago {

        private String formaPago;

        public String getFormaPago() {
            return formaPago;
        }

        public void setFormaPago(String formaPago) {
            this.formaPago = formaPago;
        }

        @Override
        public String toString() {
//            verificar si es pago al exterior es 02 y residentes es 01
//            String retorno = "<formaPago>" + "02" + "</formaPago>\n";
            String retorno = "<formaPago>" + "20" + "</formaPago>\n";
            return retorno;
        }
    }
}
