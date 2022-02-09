/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.sfccbdmq;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author edwin
 */
public class NotaCreditoSri {

    private String id;
    private InfoTributaria infoTributaria;
    private Autorizacion autorizacion;
//    private List<Retencion> retenciones;
    private List<CampoAdicional> infoAdicional;
    private InfoNotaCredito infoFactura;
    private List<Detalle> detalles;
    private String version;
    private Date fechaEmi;

    public NotaCreditoSri() {
        infoTributaria = new InfoTributaria();
        infoFactura = new InfoNotaCredito();
        autorizacion = new Autorizacion();
//        retenciones = new LinkedList<>();
        infoAdicional = new LinkedList<>();
        detalles = new LinkedList<>();
    }

    /**
     * @return the infoTributaria
     */
    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    /**
     * @param infoTributaria the infoTributaria to set
     */
    public void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    /**
     * @return the infoAdicional
     */
    public List<CampoAdicional> getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * @param infoAdicional the infoAdicional to set
     */
    public void setInfoAdicional(List<CampoAdicional> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    /**
     * @return the detalles
     */
    public List<Detalle> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the infoFactura
     */
    public InfoNotaCredito getInfoNotaCredito() {
        return infoFactura;
    }

    /**
     * @param infoNotaCredito
     */
    public void setInfoNotaCredito(InfoNotaCredito infoNotaCredito) {
        this.infoFactura = infoNotaCredito;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the autorizacion
     */
    public Autorizacion getAutorizacion() {
        return autorizacion;
    }

    public Impuesto cargaImpuesto(String codigo,
            String valor,
            String codigoPorcentaje,
            String baseImponible,
            String tarifa) {
        Impuesto i = new Impuesto();
        i.setValor(valor);
        i.setCodigo(codigo);
        i.setCodigoPorcentaje(codigoPorcentaje);
        i.setTarifa(tarifa);
        i.setBaseImponible(baseImponible);
        return i;
    }

    public void cargaDetalle(String precioUnitario,
            String descuento,
            String codigoPrincipal,
            List<Impuesto> impuestos,
            String cantidad,
            String descripcion,
            String precioTotalSinImpuesto) {
        if (detalles == null) {
            detalles = new LinkedList<>();
        }
        Detalle d = new Detalle();
        d.setCantidad(cantidad);
        d.setCodigoPrincipal(codigoPrincipal);
        d.setDescripcion(descripcion);
        d.setImpuestos(impuestos);
        d.setPrecioTotalSinImpuesto(precioTotalSinImpuesto);
        d.setPrecioUnitario(precioUnitario);
        detalles.add(d);
    }

    public void cargaAdicional(String nombre, String content) {
        CampoAdicional c = new CampoAdicional();
        c.setNombre(nombre);
        c.setContent(content);
        if (infoAdicional == null) {
            infoAdicional = new LinkedList<>();
        }
        infoAdicional.add(c);
    }

    /**
     * @param autorizacion the autorizacion to set
     */
    public void setAutorizacion(Autorizacion autorizacion) {
        this.autorizacion = autorizacion;
    }

    /**
     * @return the fechaEmi
     */
    public Date getFechaEmi() {
        return fechaEmi;
    }

    /**
     * @param fechaEmi the fechaEmi to set
     */
    public void setFechaEmi(Date fechaEmi) {
        this.fechaEmi = fechaEmi;
    }

    public class Detalle {

        private String precioUnitario;

        private String descuento;

        private String codigoPrincipal;

        private List<Impuesto> impuestos;

        private String cantidad;

        private String descripcion;

        private String precioTotalSinImpuesto;

        public String getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(String precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public String getDescuento() {
            return descuento;
        }

        public void setDescuento(String descuento) {
            this.descuento = descuento;
        }

        public String getCodigoPrincipal() {
            return codigoPrincipal;
        }

        public void setCodigoPrincipal(String codigoPrincipal) {
            this.codigoPrincipal = codigoPrincipal;
        }

        public List<Impuesto> getImpuestos() {
            return impuestos;
        }

        public void setImpuestos(List<Impuesto> impuestos) {
            this.impuestos = impuestos;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getPrecioTotalSinImpuesto() {
            return precioTotalSinImpuesto;
        }

        public void setPrecioTotalSinImpuesto(String precioTotalSinImpuesto) {
            this.precioTotalSinImpuesto = precioTotalSinImpuesto;
        }

        @Override
        public String toString() {
            return "ClassPojo [precioUnitario = " + precioUnitario + ", descuento = " + descuento + ", codigoPrincipal = " + codigoPrincipal + ", impuestos = " + impuestos + ", cantidad = " + cantidad + ", descripcion = " + descripcion + ", precioTotalSinImpuesto = " + precioTotalSinImpuesto + "]";
        }
    }

    public class Impuesto {

        private String codigo;

        private String valor;

        private String codigoPorcentaje;

        private String baseImponible;

        private String tarifa;

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getCodigoPorcentaje() {
            return codigoPorcentaje;
        }

        public void setCodigoPorcentaje(String codigoPorcentaje) {
            this.codigoPorcentaje = codigoPorcentaje;
        }

        public String getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(String baseImponible) {
            this.baseImponible = baseImponible;
        }

        public String getTarifa() {
            return tarifa;
        }

        public void setTarifa(String tarifa) {
            this.tarifa = tarifa;
        }

        @Override
        public String toString() {
            return "ClassPojo [codigo = " + codigo + ", valor = " + valor + ", codigoPorcentaje = " + codigoPorcentaje + ", baseImponible = " + baseImponible + ", tarifa = " + tarifa + "]";
        }
    }

    public class InfoNotaCredito {

        private String totalSinImpuestos;

        private String contribuyenteEspecial;

        private String numDocModificado;

        private String valorModificacion;

        private String dirEstablecimiento;

        private List<TotalImpuesto> totalConImpuestos;

        private String moneda;

        private String obligadoContabilidad;

        private Date fechaEmision;

        private String razonSocialComprador;

        private String tipoIdentificacionComprador;

        private String identificacionComprador;

        private String importeTotal;

        public String getTotalSinImpuestos() {
            return totalSinImpuestos;
        }

        public void setTotalSinImpuestos(String totalSinImpuestos) {
            this.totalSinImpuestos = totalSinImpuestos;
        }

        public String getContribuyenteEspecial() {
            return contribuyenteEspecial;
        }

        public void setContribuyenteEspecial(String contribuyenteEspecial) {
            this.contribuyenteEspecial = contribuyenteEspecial;
        }


        public String getDirEstablecimiento() {
            return dirEstablecimiento;
        }

        public void setDirEstablecimiento(String dirEstablecimiento) {
            this.dirEstablecimiento = dirEstablecimiento;
        }

        public List<TotalImpuesto> getTotalConImpuestos() {
            return totalConImpuestos;
        }

        public void setTotalConImpuestos(List<TotalImpuesto> totalConImpuestos) {
            this.totalConImpuestos = totalConImpuestos;
        }

        public void agregarTotalConImpuestos(String codigo, String valor,
                String descuentoAdicional,
                String codigoPorcentaje, String baseImponible) {
            TotalImpuesto t = new TotalImpuesto();
            t.setBaseImponible(baseImponible);
            t.setCodigo(codigo);
            t.setCodigoPorcentaje(codigoPorcentaje);
            t.setDescuentoAdicional(descuentoAdicional);
            t.setValor(valor);
            if (totalConImpuestos == null) {
                totalConImpuestos = new LinkedList<>();
            }
            totalConImpuestos.add(t);
        }

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
        }

        public String getObligadoContabilidad() {
            return obligadoContabilidad;
        }

        public void setObligadoContabilidad(String obligadoContabilidad) {
            this.obligadoContabilidad = obligadoContabilidad;
        }

        public Date getFechaEmision() {
            return fechaEmision;
        }

        public void setFechaEmision(Date fechaEmision) {
            this.fechaEmision = fechaEmision;
        }

        public String getRazonSocialComprador() {
            return razonSocialComprador;
        }

        public void setRazonSocialComprador(String razonSocialComprador) {
            this.razonSocialComprador = razonSocialComprador;
        }

        public String getTipoIdentificacionComprador() {
            return tipoIdentificacionComprador;
        }

        public void setTipoIdentificacionComprador(String tipoIdentificacionComprador) {
            this.tipoIdentificacionComprador = tipoIdentificacionComprador;
        }

        public String getIdentificacionComprador() {
            return identificacionComprador;
        }

        public void setIdentificacionComprador(String identificacionComprador) {
            this.identificacionComprador = identificacionComprador;
        }

        public String getImporteTotal() {
            return importeTotal;
        }

        public void setImporteTotal(String importeTotal) {
            this.importeTotal = importeTotal;
        }

        @Override
        public String toString() {
            return "InfoFactura [totalSinImpuestos = " + totalSinImpuestos + ", contribuyenteEspecial = " +
                    contribuyenteEspecial + ", numDocModificado = " + numDocModificado + ", valorModificacion = " +
                    valorModificacion + ", dirEstablecimiento = " + dirEstablecimiento + ", totalConImpuestos = " + 
                    totalConImpuestos + ", moneda = " + moneda + ", obligadoContabilidad = " + obligadoContabilidad + 
                    ", fechaEmision = " + fechaEmision + ", razonSocialComprador = " + razonSocialComprador + 
                    ", tipoIdentificacionComprador = " + tipoIdentificacionComprador + ", identificacionComprador = " + 
                    identificacionComprador + ", importeTotal = " + importeTotal + "]";
        }

        /**
         * @return the numDocModificado
         */
        public String getNumDocModificado() {
            return numDocModificado;
        }

        /**
         * @param numDocModificado the numDocModificado to set
         */
        public void setNumDocModificado(String numDocModificado) {
            this.numDocModificado = numDocModificado;
        }

        /**
         * @return the valorModificacion
         */
        public String getValorModificacion() {
            return valorModificacion;
        }

        /**
         * @param valorModificacion the valorModificacion to set
         */
        public void setValorModificacion(String valorModificacion) {
            this.valorModificacion = valorModificacion;
        }
    }

    public class TotalImpuesto {

        private String codigo;

        private String valor;

        private String descuentoAdicional;

        private String codigoPorcentaje;

        private String baseImponible;

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getDescuentoAdicional() {
            return descuentoAdicional;
        }

        public void setDescuentoAdicional(String descuentoAdicional) {
            this.descuentoAdicional = descuentoAdicional;
        }

        public String getCodigoPorcentaje() {
            return codigoPorcentaje;
        }

        public void setCodigoPorcentaje(String codigoPorcentaje) {
            this.codigoPorcentaje = codigoPorcentaje;
        }

        public String getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(String baseImponible) {
            this.baseImponible = baseImponible;
        }

        @Override
        public String toString() {
            return "TotalImpuesto [codigo = " + codigo + ", valor = " + valor + ", descuentoAdicional = " + descuentoAdicional + ", codigoPorcentaje = " + codigoPorcentaje + ", baseImponible = " + baseImponible + "]";
        }
    }

    public class CampoAdicional {

        private String nombre;

        private String content;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return " " + nombre + " = " + content + "";
        }
    }

    public class Retencion {

        private String codigo;

        private String valor;

        private String codigoPorcentaje;

        private String tarifa;

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getCodigoPorcentaje() {
            return codigoPorcentaje;
        }

        public void setCodigoPorcentaje(String codigoPorcentaje) {
            this.codigoPorcentaje = codigoPorcentaje;
        }

        public String getTarifa() {
            return tarifa;
        }

        public void setTarifa(String tarifa) {
            this.tarifa = tarifa;
        }

        @Override
        public String toString() {
            return "Retencion [codigo = " + codigo + ", valor = " + valor + ", codigoPorcentaje = " + codigoPorcentaje + ", tarifa = " + tarifa + "]";
        }
    }

    public class InfoTributaria {

        private String nombreComercial;
        private String secuencial;
        private String claveAcceso;
        private String ptoEmi;
        private String razonSocial;
        private String tipoEmision;
        private String ruc;
        private String codDoc;
        private String ambiente;
        private String dirMatriz;
        private String estab;

        public String getNombreComercial() {
            return nombreComercial;
        }

        public void setNombreComercial(String nombreComercial) {
            this.nombreComercial = nombreComercial;
        }

        public String getSecuencial() {
            return secuencial;
        }

        public void setSecuencial(String secuencial) {
            this.secuencial = secuencial;
        }

        public String getClaveAcceso() {
            return claveAcceso;
        }

        public void setClaveAcceso(String claveAcceso) {
            this.claveAcceso = claveAcceso;
        }

        public String getPtoEmi() {
            return ptoEmi;
        }

        public void setPtoEmi(String ptoEmi) {
            this.ptoEmi = ptoEmi;
        }

        public String getRazonSocial() {
            return razonSocial;
        }

        public void setRazonSocial(String razonSocial) {
            this.razonSocial = razonSocial;
        }

        public String getTipoEmision() {
            return tipoEmision;
        }

        public void setTipoEmision(String tipoEmision) {
            this.tipoEmision = tipoEmision;
        }

        public String getRuc() {
            return ruc;
        }

        public void setRuc(String ruc) {
            this.ruc = ruc;
        }

        public String getCodDoc() {
            return codDoc;
        }

        public void setCodDoc(String codDoc) {
            this.codDoc = codDoc;
        }

        public String getAmbiente() {
            return ambiente;
        }

        public void setAmbiente(String ambiente) {
            this.ambiente = ambiente;
        }

        public String getDirMatriz() {
            return dirMatriz;
        }

        public void setDirMatriz(String dirMatriz) {
            this.dirMatriz = dirMatriz;
        }

        public String getEstab() {
            return estab;
        }

        public void setEstab(String estab) {
            this.estab = estab;
        }

        @Override
        public String toString() {
            return "InfoTributaria [nombreComercial = " + nombreComercial + ", secuencial = " + secuencial + ", claveAcceso = " + claveAcceso + ", ptoEmi = " + ptoEmi + ", razonSocial = " + razonSocial + ", tipoEmision = " + tipoEmision + ", ruc = " + ruc + ", codDoc = " + codDoc + ", ambiente = " + ambiente + ", dirMatriz = " + dirMatriz + ", estab = " + estab + "]";
        }
    }

    public class Autorizacion {

        private String estado;
        private String numeroAutorizacion;
        private Date fechaAutorizacion;

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
         * @return the numeroAutorizacion
         */
        public String getNumeroAutorizacion() {
            return numeroAutorizacion;
        }

        /**
         * @param numeroAutorizacion the numeroAutorizacion to set
         */
        public void setNumeroAutorizacion(String numeroAutorizacion) {
            this.numeroAutorizacion = numeroAutorizacion;
        }

        /**
         * @return the fechaAutorizacion
         */
        public Date getFechaAutorizacion() {
            return fechaAutorizacion;
        }

        /**
         * @param fechaAutorizacion the fechaAutorizacion to set
         */
        public void setFechaAutorizacion(Date fechaAutorizacion) {
            this.fechaAutorizacion = fechaAutorizacion;
        }
    }
}
