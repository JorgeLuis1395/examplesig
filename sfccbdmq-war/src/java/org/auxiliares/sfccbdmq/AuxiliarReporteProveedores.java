/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author edwin
 */
public class AuxiliarReporteProveedores implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ref;
    private String cempresa;
    private Date fechaemi;
    private Date fechareg;
    private String tipoide;
    private String cproveedor;
    private String proveedor;
    private String numeroide;
    private String cdocumento;
    private String serie;
    private String autsri;
    private String secuencia;
    private String rtipoide;
    private String rnumeroide;
    private String rcdocument;
    private String rserie;
    private String rsecuencia;
    private String rautsri;
    private Date rfechaemi;
    private double rbasimpng;
    private double rbasimpsg;
    private double rvaliva;
    private double rbasimpno;
    private double rvalice;
    private double rbasimpex;
    
    private boolean relacionad;
    private String tipoclie;
    private double ret_ir;
    private double ret_iva;
    private double liquida;
    private double compsolida;
    private double compdinele;
    private boolean docelectro;
    private boolean anulada;
    
    private String reforg;
    private String refnum;
    private String tipoasi;
    private String nasiento;
    private String fechavencimientofactura;
    private String conceptfac;
    private double poriva;
    private String ideiva;
    private String deviva;
    private String auxiliar;
    
    private double retiva0;
    private double retiva10;
    private double retiva20;
    private double retiva30;
    private double retiva50;
    private double retiva70;
    private double retiva100;
    private String codret;
    private double basret;
    private double porret;
    private double valret;
    private String paglocext;
    private double regfispre;
    private double aplconvdob;
    private double pesretnleg;
    private String paisefecpa;
    private String tiporegi;
    private String paisefpgge;
    private String paisefpgpf;
    private String denopago;
    private String retser;
    private String retaut;
    private String nretencion;
    private String periodofec;
    private String conceptret;
    private String facturas;
    private String docelectror;
    private String ambiente;
    private String estado;
    private String tpemision;
    private String clacceso;
    private String fechaaut;
    private String rusugre;
    private String rfecagr;
    private String rusumod;
    private String rfecmod;
    
    private boolean relacionado;
    private String tipoProveedor;
    private String liquidar;
    private String tipoPagoSRI;
    
    
    
    

    /**
     * @return the ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * @return the cempresa
     */
    public String getCempresa() {
        return cempresa;
    }

    /**
     * @param cempresa the cempresa to set
     */
    public void setCempresa(String cempresa) {
        this.cempresa = cempresa;
    }

    /**
     * @return the fechaemi
     */
    public Date getFechaemi() {
        return fechaemi;
    }

    /**
     * @param fechaemi the fechaemi to set
     */
    public void setFechaemi(Date fechaemi) {
        this.fechaemi = fechaemi;
    }

    /**
     * @return the fechareg
     */
    public Date getFechareg() {
        return fechareg;
    }

    /**
     * @param fechareg the fechareg to set
     */
    public void setFechareg(Date fechareg) {
        this.fechareg = fechareg;
    }

    /**
     * @return the tipoide
     */
    public String getTipoide() {
        return tipoide;
    }

    /**
     * @param tipoide the tipoide to set
     */
    public void setTipoide(String tipoide) {
        this.tipoide = tipoide;
    }

    /**
     * @return the cproveedor
     */
    public String getCproveedor() {
        return cproveedor;
    }

    /**
     * @param cproveedor the cproveedor to set
     */
    public void setCproveedor(String cproveedor) {
        this.cproveedor = cproveedor;
    }

    /**
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @return the numeroide
     */
    public String getNumeroide() {
        return numeroide;
    }

    /**
     * @param numeroide the numeroide to set
     */
    public void setNumeroide(String numeroide) {
        this.numeroide = numeroide;
    }

    /**
     * @return the cdocumento
     */
    public String getCdocumento() {
        return cdocumento;
    }

    /**
     * @param cdocumento the cdocumento to set
     */
    public void setCdocumento(String cdocumento) {
        this.cdocumento = cdocumento;
    }

    /**
     * @return the serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * @return the autsri
     */
    public String getAutsri() {
        return autsri;
    }

    /**
     * @param autsri the autsri to set
     */
    public void setAutsri(String autsri) {
        this.autsri = autsri;
    }

    /**
     * @return the secuencia
     */
    public String getSecuencia() {
        return secuencia;
    }

    /**
     * @param secuencia the secuencia to set
     */
    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    /**
     * @return the rtipoide
     */
    public String getRtipoide() {
        return rtipoide;
    }

    /**
     * @param rtipoide the rtipoide to set
     */
    public void setRtipoide(String rtipoide) {
        this.rtipoide = rtipoide;
    }

    /**
     * @return the rnumeroide
     */
    public String getRnumeroide() {
        return rnumeroide;
    }

    /**
     * @param rnumeroide the rnumeroide to set
     */
    public void setRnumeroide(String rnumeroide) {
        this.rnumeroide = rnumeroide;
    }

    /**
     * @return the rcdocument
     */
    public String getRcdocument() {
        return rcdocument;
    }

    /**
     * @param rcdocument the rcdocument to set
     */
    public void setRcdocument(String rcdocument) {
        this.rcdocument = rcdocument;
    }

    /**
     * @return the rserie
     */
    public String getRserie() {
        return rserie;
    }

    /**
     * @param rserie the rserie to set
     */
    public void setRserie(String rserie) {
        this.rserie = rserie;
    }

    /**
     * @return the rsecuencia
     */
    public String getRsecuencia() {
        return rsecuencia;
    }

    /**
     * @param rsecuencia the rsecuencia to set
     */
    public void setRsecuencia(String rsecuencia) {
        this.rsecuencia = rsecuencia;
    }

    /**
     * @return the rautsri
     */
    public String getRautsri() {
        return rautsri;
    }

    /**
     * @param rautsri the rautsri to set
     */
    public void setRautsri(String rautsri) {
        this.rautsri = rautsri;
    }

    /**
     * @return the rfechaemi
     */
    public Date getRfechaemi() {
        return rfechaemi;
    }

    /**
     * @param rfechaemi the rfechaemi to set
     */
    public void setRfechaemi(Date rfechaemi) {
        this.rfechaemi = rfechaemi;
    }

    /**
     * @return the rbasimpng
     */
    public double getRbasimpng() {
        return rbasimpng;
    }

    /**
     * @param rbasimpng the rbasimpng to set
     */
    public void setRbasimpng(double rbasimpng) {
        this.rbasimpng = rbasimpng;
    }

    /**
     * @return the rbasimpsg
     */
    public double getRbasimpsg() {
        return rbasimpsg;
    }

    /**
     * @param rbasimpsg the rbasimpsg to set
     */
    public void setRbasimpsg(double rbasimpsg) {
        this.rbasimpsg = rbasimpsg;
    }

    /**
     * @return the rvaliva
     */
    public double getRvaliva() {
        return rvaliva;
    }

    /**
     * @param rvaliva the rvaliva to set
     */
    public void setRvaliva(double rvaliva) {
        this.rvaliva = rvaliva;
    }

    /**
     * @return the rbasimpno
     */
    public double getRbasimpno() {
        return rbasimpno;
    }

    /**
     * @param rbasimpno the rbasimpno to set
     */
    public void setRbasimpno(double rbasimpno) {
        this.rbasimpno = rbasimpno;
    }

    /**
     * @return the rvalice
     */
    public double getRvalice() {
        return rvalice;
    }

    /**
     * @param rvalice the rvalice to set
     */
    public void setRvalice(double rvalice) {
        this.rvalice = rvalice;
    }

    /**
     * @return the rbasimpex
     */
    public double getRbasimpex() {
        return rbasimpex;
    }

    /**
     * @param rbasimpex the rbasimpex to set
     */
    public void setRbasimpex(double rbasimpex) {
        this.rbasimpex = rbasimpex;
    }

    /**
     * @return the relacionad
     */
    public boolean isRelacionad() {
        return relacionad;
    }

    /**
     * @param relacionad the relacionad to set
     */
    public void setRelacionad(boolean relacionad) {
        this.relacionad = relacionad;
    }

    /**
     * @return the tipoclie
     */
    public String getTipoclie() {
        return tipoclie;
    }

    /**
     * @param tipoclie the tipoclie to set
     */
    public void setTipoclie(String tipoclie) {
        this.tipoclie = tipoclie;
    }

    /**
     * @return the ret_ir
     */
    public double getRet_ir() {
        return ret_ir;
    }

    /**
     * @param ret_ir the ret_ir to set
     */
    public void setRet_ir(double ret_ir) {
        this.ret_ir = ret_ir;
    }

    /**
     * @return the ret_iva
     */
    public double getRet_iva() {
        return ret_iva;
    }

    /**
     * @param ret_iva the ret_iva to set
     */
    public void setRet_iva(double ret_iva) {
        this.ret_iva = ret_iva;
    }

    /**
     * @return the liquida
     */
    public double getLiquida() {
        return liquida;
    }

    /**
     * @param liquida the liquida to set
     */
    public void setLiquida(double liquida) {
        this.liquida = liquida;
    }

    /**
     * @return the compsolida
     */
    public double getCompsolida() {
        return compsolida;
    }

    /**
     * @param compsolida the compsolida to set
     */
    public void setCompsolida(double compsolida) {
        this.compsolida = compsolida;
    }

    /**
     * @return the compdinele
     */
    public double getCompdinele() {
        return compdinele;
    }

    /**
     * @param compdinele the compdinele to set
     */
    public void setCompdinele(double compdinele) {
        this.compdinele = compdinele;
    }

    /**
     * @return the docelectro
     */
    public boolean isDocelectro() {
        return docelectro;
    }

    /**
     * @param docelectro the docelectro to set
     */
    public void setDocelectro(boolean docelectro) {
        this.docelectro = docelectro;
    }

    /**
     * @return the anulada
     */
    public boolean isAnulada() {
        return anulada;
    }

    /**
     * @param anulada the anulada to set
     */
    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }
    
    
    /**
     * @return the reforg
     */
    public String getReforg() {
        return reforg;
    }

    /**
     * @param reforg the reforg to set
     */
    public void setReforg(String reforg) {
        this.reforg = reforg;
    }

    /**
     * @return the refnum
     */
    public String getRefnum() {
        return refnum;
    }

    /**
     * @param refnum the refnum to set
     */
    public void setRefnum(String refnum) {
        this.refnum = refnum;
    }

    /**
     * @return the tipoasi
     */
    public String getTipoasi() {
        return tipoasi;
    }

    /**
     * @param tipoasi the tipoasi to set
     */
    public void setTipoasi(String tipoasi) {
        this.tipoasi = tipoasi;
    }

    /**
     * @return the nasiento
     */
    public String getNasiento() {
        return nasiento;
    }

    /**
     * @param nasiento the nasiento to set
     */
    public void setNasiento(String nasiento) {
        this.nasiento = nasiento;
    }

    /**
     * @return the fechavencimientofactura
     */
    public String getFechavencimientofactura() {
        return fechavencimientofactura;
    }

    /**
     * @param fechavencimientofactura the fechavencimientofactura to set
     */
    public void setFechavencimientofactura(String fechavencimientofactura) {
        this.fechavencimientofactura = fechavencimientofactura;
    }

    /**
     * @return the conceptfac
     */
    public String getConceptfac() {
        return conceptfac;
    }

    /**
     * @param conceptfac the conceptfac to set
     */
    public void setConceptfac(String conceptfac) {
        this.conceptfac = conceptfac;
    }

    /**
     * @return the poriva
     */
    public double getPoriva() {
        return poriva;
    }

    /**
     * @param poriva the poriva to set
     */
    public void setPoriva(double poriva) {
        this.poriva = poriva;
    }

    /**
     * @return the ideiva
     */
    public String getIdeiva() {
        return ideiva;
    }

    /**
     * @param ideiva the ideiva to set
     */
    public void setIdeiva(String ideiva) {
        this.ideiva = ideiva;
    }

    /**
     * @return the deviva
     */
    public String getDeviva() {
        return deviva;
    }

    /**
     * @param deviva the deviva to set
     */
    public void setDeviva(String deviva) {
        this.deviva = deviva;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the retiva0
     */
    public double getRetiva0() {
        return retiva0;
    }

    /**
     * @param retiva0 the retiva0 to set
     */
    public void setRetiva0(double retiva0) {
        this.retiva0 = retiva0;
    }

    /**
     * @return the retiva10
     */
    public double getRetiva10() {
        return retiva10;
    }

    /**
     * @param retiva10 the retiva10 to set
     */
    public void setRetiva10(double retiva10) {
        this.retiva10 = retiva10;
    }

    /**
     * @return the retiva20
     */
    public double getRetiva20() {
        return retiva20;
    }

    /**
     * @param retiva20 the retiva20 to set
     */
    public void setRetiva20(double retiva20) {
        this.retiva20 = retiva20;
    }

    /**
     * @return the retiva30
     */
    public double getRetiva30() {
        return retiva30;
    }

    /**
     * @param retiva30 the retiva30 to set
     */
    public void setRetiva30(double retiva30) {
        this.retiva30 = retiva30;
    }

    /**
     * @return the retiva50
     */
    public double getRetiva50() {
        return retiva50;
    }

    /**
     * @param retiva50 the retiva50 to set
     */
    public void setRetiva50(double retiva50) {
        this.retiva50 = retiva50;
    }

    /**
     * @return the retiva100
     */
    public double getRetiva100() {
        return retiva100;
    }

    /**
     * @param retiva100 the retiva100 to set
     */
    public void setRetiva100(double retiva100) {
        this.retiva100 = retiva100;
    }

    /**
     * @return the retiva70
     */
    public double getRetiva70() {
        return retiva70;
    }

    /**
     * @param retiva70 the retiva70 to set
     */
    public void setRetiva70(double retiva70) {
        this.retiva70 = retiva70;
    }

    /**
     * @return the codret
     */
    public String getCodret() {
        return codret;
    }

    /**
     * @param codret the codret to set
     */
    public void setCodret(String codret) {
        this.codret = codret;
    }

    /**
     * @return the basret
     */
    public double getBasret() {
        return basret;
    }

    /**
     * @param basret the basret to set
     */
    public void setBasret(double basret) {
        this.basret = basret;
    }

    /**
     * @return the porret
     */
    public double getPorret() {
        return porret;
    }

    /**
     * @param porret the porret to set
     */
    public void setPorret(double porret) {
        this.porret = porret;
    }

    /**
     * @return the valret
     */
    public double getValret() {
        return valret;
    }

    /**
     * @param valret the valret to set
     */
    public void setValret(double valret) {
        this.valret = valret;
    }

    /**
     * @return the paglocext
     */
    public String getPaglocext() {
        return paglocext;
    }

    /**
     * @param paglocext the paglocext to set
     */
    public void setPaglocext(String paglocext) {
        this.paglocext = paglocext;
    }

    /**
     * @return the regfispre
     */
    public double getRegfispre() {
        return regfispre;
    }

    /**
     * @param regfispre the regfispre to set
     */
    public void setRegfispre(double regfispre) {
        this.regfispre = regfispre;
    }

    /**
     * @return the aplconvdob
     */
    public double getAplconvdob() {
        return aplconvdob;
    }

    /**
     * @param aplconvdob the aplconvdob to set
     */
    public void setAplconvdob(double aplconvdob) {
        this.aplconvdob = aplconvdob;
    }

    /**
     * @return the pesretnleg
     */
    public double getPesretnleg() {
        return pesretnleg;
    }

    /**
     * @param pesretnleg the pesretnleg to set
     */
    public void setPesretnleg(double pesretnleg) {
        this.pesretnleg = pesretnleg;
    }

    /**
     * @return the paisefecpa
     */
    public String getPaisefecpa() {
        return paisefecpa;
    }

    /**
     * @param paisefecpa the paisefecpa to set
     */
    public void setPaisefecpa(String paisefecpa) {
        this.paisefecpa = paisefecpa;
    }

    /**
     * @return the tiporegi
     */
    public String getTiporegi() {
        return tiporegi;
    }

    /**
     * @param tiporegi the tiporegi to set
     */
    public void setTiporegi(String tiporegi) {
        this.tiporegi = tiporegi;
    }

    /**
     * @return the paisefpgge
     */
    public String getPaisefpgge() {
        return paisefpgge;
    }

    /**
     * @param paisefpgge the paisefpgge to set
     */
    public void setPaisefpgge(String paisefpgge) {
        this.paisefpgge = paisefpgge;
    }

    /**
     * @return the paisefpgpf
     */
    public String getPaisefpgpf() {
        return paisefpgpf;
    }

    /**
     * @param paisefpgpf the paisefpgpf to set
     */
    public void setPaisefpgpf(String paisefpgpf) {
        this.paisefpgpf = paisefpgpf;
    }

    /**
     * @return the denopago
     */
    public String getDenopago() {
        return denopago;
    }

    /**
     * @param denopago the denopago to set
     */
    public void setDenopago(String denopago) {
        this.denopago = denopago;
    }

    /**
     * @return the retser
     */
    public String getRetser() {
        return retser;
    }

    /**
     * @param retser the retser to set
     */
    public void setRetser(String retser) {
        this.retser = retser;
    }

    /**
     * @return the retaut
     */
    public String getRetaut() {
        return retaut;
    }

    /**
     * @param retaut the retaut to set
     */
    public void setRetaut(String retaut) {
        this.retaut = retaut;
    }

    /**
     * @return the nretencion
     */
    public String getNretencion() {
        return nretencion;
    }

    /**
     * @param nretencion the nretencion to set
     */
    public void setNretencion(String nretencion) {
        this.nretencion = nretencion;
    }

    /**
     * @return the periodofec
     */
    public String getPeriodofec() {
        return periodofec;
    }

    /**
     * @param periodofec the periodofec to set
     */
    public void setPeriodofec(String periodofec) {
        this.periodofec = periodofec;
    }

    /**
     * @return the conceptret
     */
    public String getConceptret() {
        return conceptret;
    }

    /**
     * @param conceptret the conceptret to set
     */
    public void setConceptret(String conceptret) {
        this.conceptret = conceptret;
    }

    /**
     * @return the facturas
     */
    public String getFacturas() {
        return facturas;
    }

    /**
     * @param facturas the facturas to set
     */
    public void setFacturas(String facturas) {
        this.facturas = facturas;
    }

    /**
     * @return the ambiente
     */
    public String getAmbiente() {
        return ambiente;
    }

    /**
     * @param ambiente the ambiente to set
     */
    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the tpemision
     */
    public String getTpemision() {
        return tpemision;
    }

    /**
     * @param tpemision the tpemision to set
     */
    public void setTpemision(String tpemision) {
        this.tpemision = tpemision;
    }

    /**
     * @return the clacceso
     */
    public String getClacceso() {
        return clacceso;
    }

    /**
     * @param clacceso the clacceso to set
     */
    public void setClacceso(String clacceso) {
        this.clacceso = clacceso;
    }

    /**
     * @return the fechaaut
     */
    public String getFechaaut() {
        return fechaaut;
    }

    /**
     * @param fechaaut the fechaaut to set
     */
    public void setFechaaut(String fechaaut) {
        this.fechaaut = fechaaut;
    }

    /**
     * @return the rusugre
     */
    public String getRusugre() {
        return rusugre;
    }

    /**
     * @param rusugre the rusugre to set
     */
    public void setRusugre(String rusugre) {
        this.rusugre = rusugre;
    }

    /**
     * @return the rfecagr
     */
    public String getRfecagr() {
        return rfecagr;
    }

    /**
     * @param rfecagr the rfecagr to set
     */
    public void setRfecagr(String rfecagr) {
        this.rfecagr = rfecagr;
    }

    /**
     * @return the rusumod
     */
    public String getRusumod() {
        return rusumod;
    }

    /**
     * @param rusumod the rusumod to set
     */
    public void setRusumod(String rusumod) {
        this.rusumod = rusumod;
    }

    /**
     * @return the rfecmod
     */
    public String getRfecmod() {
        return rfecmod;
    }

    /**
     * @param rfecmod the rfecmod to set
     */
    public void setRfecmod(String rfecmod) {
        this.rfecmod = rfecmod;
    }

    /**
     * @return the docelectror
     */
    public String getDocelectror() {
        return docelectror;
    }

    /**
     * @param docelectror the docelectror to set
     */
    public void setDocelectror(String docelectror) {
        this.docelectror = docelectror;
    }

    /**
     * @return the relacionado
     */
    public boolean isRelacionado() {
        return relacionado;
    }

    /**
     * @param relacionado the relacionado to set
     */
    public void setRelacionado(boolean relacionado) {
        this.relacionado = relacionado;
    }

    /**
     * @return the tipoProveedor
     */
    public String getTipoProveedor() {
        return tipoProveedor;
    }

    /**
     * @param tipoProveedor the tipoProveedor to set
     */
    public void setTipoProveedor(String tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }

    /**
     * @return the liquidar
     */
    public String getLiquidar() {
        return liquidar;
    }

    /**
     * @param liquidar the liquidar to set
     */
    public void setLiquidar(String liquidar) {
        this.liquidar = liquidar;
    }

    /**
     * @return the tipoPagoSRI
     */
    public String getTipoPagoSRI() {
        return tipoPagoSRI;
    }

    /**
     * @param tipoPagoSRI the tipoPagoSRI to set
     */
    public void setTipoPagoSRI(String tipoPagoSRI) {
        this.tipoPagoSRI = tipoPagoSRI;
    }


}
