/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delectronicos.sfccbdmq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author edwin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"infoTributaria", "infoFactura", "detalles", "infoAdicional"})
@XmlRootElement(name = "factura")
public class Factura {

    @XmlElement(required = true)
    protected InfoTributaria infoTributaria;
    @XmlElement(required = true)
    protected Factura.InfoFactura infoFactura;
    @XmlElement(required = true)
    protected Factura.Detalles detalles;
    protected Factura.InfoAdicional infoAdicional;
    @XmlAttribute
    protected String id;
    @XmlAttribute
    @XmlSchemaType(name = "anySimpleType")
    protected String version;
    public Factura() {
        super();
    }

    public InfoTributaria getInfoTributaria() {
        return this.infoTributaria;
    }

    public void setInfoTributaria(final InfoTributaria value) {
        this.infoTributaria = value;
    }

    public Factura.InfoFactura getInfoFactura() {
        return this.infoFactura;
    }

    public void setInfoFactura(final Factura.InfoFactura value) {
        this.infoFactura = value;
    }

    public Factura.Detalles getDetalles() {
        return this.detalles;
    }

    public void setDetalles(final Factura.Detalles value) {
        this.detalles = value;
    }

    public Factura.InfoAdicional getInfoAdicional() {
        return this.infoAdicional;
    }

    public void setInfoAdicional(final Factura.InfoAdicional value) {
        this.infoAdicional = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String value) {
        this.id = value;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String value) {
        this.version = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"fechaEmision", "dirEstablecimiento", "contribuyenteEspecial", "obligadoContabilidad", "tipoIdentificacionComprador", "guiaRemision", "razonSocialComprador", "identificacionComprador", "direccionComprador", "totalSinImpuestos", "totalSubsidio", "totalDescuento", "totalConImpuestos", "compensaciones", "propina", "importeTotal", "moneda", "pagos"})
    public static class InfoFactura {

        @XmlElement(required = true)
        protected String fechaEmision;
        @XmlElement(required = true)
        protected String dirEstablecimiento;
        protected String contribuyenteEspecial;
        protected String obligadoContabilidad;
        @XmlElement(required = true)
        protected String tipoIdentificacionComprador;
        protected String guiaRemision;
        @XmlElement(required = true)
        protected String razonSocialComprador;
        @XmlElement(required = true)
        protected String identificacionComprador;
        protected String direccionComprador;
        @XmlElement(required = true)
        protected BigDecimal totalSinImpuestos;
        @XmlElement(required = true)
        protected BigDecimal totalSubsidio;
        @XmlElement(required = true)
        protected BigDecimal totalDescuento;
        @XmlElement(required = true)
        protected InfoFactura.TotalConImpuestos totalConImpuestos;
        protected InfoFactura.compensacion compensaciones;
        @XmlElement(required = true)
        protected BigDecimal propina;
        @XmlElement(required = true)
        protected BigDecimal importeTotal;
        protected String moneda;
        protected InfoFactura.Pago pagos;

        public InfoFactura() {
            super();
        }

        public String getFechaEmision() {
            return this.fechaEmision;
        }

        public void setFechaEmision(final String value) {
            this.fechaEmision = value;
        }

        public String getDirEstablecimiento() {
            return this.dirEstablecimiento;
        }

        public void setDirEstablecimiento(final String value) {
            this.dirEstablecimiento = value;
        }

        public String getContribuyenteEspecial() {
            return this.contribuyenteEspecial;
        }

        public void setContribuyenteEspecial(final String value) {
            this.contribuyenteEspecial = value;
        }

        public String getObligadoContabilidad() {
            return this.obligadoContabilidad;
        }

        public void setObligadoContabilidad(final String value) {
            this.obligadoContabilidad = value;
        }

        public String getTipoIdentificacionComprador() {
            return this.tipoIdentificacionComprador;
        }

        public void setTipoIdentificacionComprador(final String value) {
            this.tipoIdentificacionComprador = value;
        }

        public String getGuiaRemision() {
            return this.guiaRemision;
        }

        public void setGuiaRemision(final String value) {
            this.guiaRemision = value;
        }

        public String getRazonSocialComprador() {
            return this.razonSocialComprador;
        }

        public void setRazonSocialComprador(final String value) {
            this.razonSocialComprador = value;
        }

        public String getIdentificacionComprador() {
            return this.identificacionComprador;
        }

        public void setIdentificacionComprador(final String value) {
            this.identificacionComprador = value;
        }

        public String getIDireccionComprador() {
            return this.direccionComprador;
        }

        public void setDireccionComprador(final String value) {
            this.direccionComprador = value;
        }

        public BigDecimal getTotalSinImpuestos() {
            return this.totalSinImpuestos;
        }

        public void setTotalSinImpuestos(final BigDecimal value) {
            this.totalSinImpuestos = value;
        }

        public BigDecimal getTotalSubsidio() {
            return this.totalSubsidio;
        }

        public void setTotalSubsidio(final BigDecimal value) {
            this.totalSubsidio = value;
        }

        public BigDecimal getTotalDescuento() {
            return this.totalDescuento;
        }

        public void setTotalDescuento(final BigDecimal value) {
            this.totalDescuento = value;
        }

        public InfoFactura.TotalConImpuestos getTotalConImpuestos() {
            return this.totalConImpuestos;
        }

        public void setTotalConImpuestos(final InfoFactura.TotalConImpuestos value) {
            this.totalConImpuestos = value;
        }

        public InfoFactura.Pago getPagos() {
            return this.pagos;
        }

        public void setCompensaciones(final InfoFactura.compensacion compensaciones) {
            this.compensaciones = compensaciones;
        }

        public InfoFactura.compensacion getCompensaciones() {
            return this.compensaciones;
        }

        public void setPagos(final InfoFactura.Pago pagos) {
            this.pagos = pagos;
        }

        public BigDecimal getPropina() {
            return this.propina;
        }

        public void setPropina(final BigDecimal value) {
            this.propina = value;
        }

        public BigDecimal getImporteTotal() {
            return this.importeTotal;
        }

        public void setImporteTotal(final BigDecimal value) {
            this.importeTotal = value;
        }

        public String getMoneda() {
            return this.moneda;
        }

        public void setMoneda(final String value) {
            this.moneda = value;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"compensacion"})
        public static class compensacion {

            @XmlElement(required = true)
            protected List<compensacion.detalleCompensaciones> compensacion;

            public compensacion() {
                super();
            }

            public List<compensacion.detalleCompensaciones> getCompensaciones() {
                if (this.compensacion == null) {
                    this.compensacion = new ArrayList();
                }
                return (List<compensacion.detalleCompensaciones>) this.compensacion;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {"codigo", "tarifa", "valor"})
            public static class detalleCompensaciones {

                @XmlElement(required = true)
                protected int codigo;
                @XmlElement(required = true)
                protected int tarifa;
                @XmlElement(required = true)
                protected BigDecimal valor;

                public detalleCompensaciones() {
                    super();
                }

                public int getCodigo() {
                    return this.codigo;
                }

                public void setCodigo(final int value) {
                    this.codigo = value;
                }

                public int getTarifa() {
                    return this.tarifa;
                }

                public void setTarifa(final int value) {
                    this.tarifa = value;
                }

                public BigDecimal getValor() {
                    return this.valor;
                }

                public void setValor(final BigDecimal valor) {
                    this.valor = valor;
                }
            }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"pago"})
        public static class Pago {

            @XmlElement(required = true)
            protected List<Pago.DetallePago> pago;

            public Pago() {
                super();
            }

            public List<Pago.DetallePago> getPagos() {
                if (this.pago == null) {
                    this.pago = new ArrayList();
                }
                return (List<Pago.DetallePago>) this.pago;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {"formaPago", "total", "plazo", "unidadTiempo"})
            public static class DetallePago {

                @XmlElement(required = true)
                protected String formaPago;
                @XmlElement(required = true)
                protected BigDecimal total;
                protected String plazo;
                protected String unidadTiempo;

                public DetallePago() {
                    super();
                }

                public String getFormaPago() {
                    return this.formaPago;
                }

                public void setFormaPago(final String formaPago) {
                    this.formaPago = formaPago;
                }

                public BigDecimal getTotal() {
                    return this.total;
                }

                public void setTotal(final BigDecimal total) {
                    this.total = total;
                }

                public String getPlazo() {
                    return this.plazo;
                }

                public void setPlazo(final String plazo) {
                    this.plazo = plazo;
                }

                public String getUnidadTiempo() {
                    return this.unidadTiempo;
                }

                public void setUnidadTiempo(final String unidadTiempo) {
                    this.unidadTiempo = unidadTiempo;
                }
            }
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"totalImpuesto"})
        public static class TotalConImpuestos {

            @XmlElement(required = true)
            protected List<TotalConImpuestos.TotalImpuesto> totalImpuesto;

            public TotalConImpuestos() {
                super();
            }

            public List<TotalConImpuestos.TotalImpuesto> getTotalImpuesto() {
                if (this.totalImpuesto == null) {
                    this.totalImpuesto = new ArrayList();
                }
                return (List<TotalConImpuestos.TotalImpuesto>) this.totalImpuesto;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {"codigo", "codigoPorcentaje", "baseImponible", "tarifa", "valor"})
            public static class TotalImpuesto {

                @XmlElement(required = true)
                protected String codigo;
                @XmlElement(required = true)
                protected String codigoPorcentaje;
                @XmlElement(required = true)
                protected BigDecimal baseImponible;
                protected BigDecimal tarifa;
                @XmlElement(required = true)
                protected BigDecimal valor;

                public TotalImpuesto() {
                    super();
                }

                public String getCodigo() {
                    return this.codigo;
                }

                public void setCodigo(final String value) {
                    this.codigo = value;
                }

                public String getCodigoPorcentaje() {
                    return this.codigoPorcentaje;
                }

                public void setCodigoPorcentaje(final String value) {
                    this.codigoPorcentaje = value;
                }

                public BigDecimal getBaseImponible() {
                    return this.baseImponible;
                }

                public void setBaseImponible(final BigDecimal value) {
                    this.baseImponible = value;
                }

                public BigDecimal getTarifa() {
                    return this.tarifa;
                }

                public void setTarifa(final BigDecimal value) {
                    this.tarifa = value;
                }

                public BigDecimal getValor() {
                    return this.valor;
                }

                public void setValor(final BigDecimal value) {
                    this.valor = value;
                }
            }
        }
    } // fin infoFac

    // nueva clase
    // Pago
    // info de
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"campoAdicional"})
    public static class InfoAdicional {

        @XmlElement(required = true)
        protected List<InfoAdicional.CampoAdicional> campoAdicional;

        public InfoAdicional() {
            super();
        }

        public List<InfoAdicional.CampoAdicional> getCampoAdicional() {
            if (this.campoAdicional == null) {
                this.campoAdicional = new ArrayList();
            }
            return (List<InfoAdicional.CampoAdicional>) this.campoAdicional;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"value"})
        public static class CampoAdicional {

            @XmlValue
            protected String value;
            @XmlAttribute
            protected String nombre;

            public CampoAdicional() {
                super();
            }

            public String getValue() {
                return this.value;
            }

            public void setValue(final String value) {
                this.value = value;
            }

            public String getNombre() {
                return this.nombre;
            }

            public void setNombre(final String value) {
                this.nombre = value;
            }
        }
    }

    //Detalles 
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"detalle"})
    public static class Detalles {

        @XmlElement(required = true)
        protected List<Detalles.Detalle> detalle;

        public Detalles() {
            super();
        }

        public List<Detalles.Detalle> getDetalle() {
            if (this.detalle == null) {
                this.detalle = new ArrayList();
            }
            return (List<Detalles.Detalle>) this.detalle;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {"codigoPrincipal", "codigoAuxiliar", "descripcion", "cantidad", "precioUnitario", "precioSinSubsidio", "descuento", "precioTotalSinImpuesto", "detallesAdicionales", "impuestos"})
        public static class Detalle {

            @XmlElement(required = true)
            protected String codigoPrincipal;
            protected String codigoAuxiliar;
            @XmlElement(required = true)
            protected String descripcion;
            @XmlElement(required = true)
            protected BigDecimal cantidad;
            @XmlElement(required = true)
            protected BigDecimal precioUnitario;
            @XmlElement(required = true)
            protected BigDecimal precioSinSubsidio;
            @XmlElement(required = true)
            protected BigDecimal descuento;
            @XmlElement(required = true)
            protected BigDecimal precioTotalSinImpuesto;
            protected Detalle.DetallesAdicionales detallesAdicionales;
            @XmlElement(required = true)
            protected Detalle.Impuestos impuestos;

            public Detalle() {
                super();
            }

            public String getCodigoPrincipal() {
                return this.codigoPrincipal;
            }

            public void setCodigoPrincipal(final String value) {
                this.codigoPrincipal = value;
            }

            public String getCodigoAuxiliar() {
                return this.codigoAuxiliar;
            }

            public void setCodigoAuxiliar(final String value) {
                this.codigoAuxiliar = value;
            }

            public String getDescripcion() {
                return this.descripcion;
            }

            public void setDescripcion(final String value) {
                this.descripcion = value;
            }

            public BigDecimal getCantidad() {
                return this.cantidad;
            }

            public void setCantidad(final BigDecimal value) {
                this.cantidad = value;
            }

            public BigDecimal getPrecioUnitario() {
                return this.precioUnitario;
            }

            public void setPrecioSinSubsidio(final BigDecimal value) {
                this.precioSinSubsidio = value;
            }

            public BigDecimal getPrecioSinSubsidio() {
                return this.precioSinSubsidio;
            }

            public void setPrecioUnitario(final BigDecimal value) {
                this.precioUnitario = value;
            }

            public BigDecimal getDescuento() {
                return this.descuento;
            }

            public void setDescuento(final BigDecimal value) {
                this.descuento = value;
            }

            public BigDecimal getPrecioTotalSinImpuesto() {
                return this.precioTotalSinImpuesto;
            }

            public void setPrecioTotalSinImpuesto(final BigDecimal value) {
                this.precioTotalSinImpuesto = value;
            }

            public Detalle.DetallesAdicionales getDetallesAdicionales() {
                return this.detallesAdicionales;
            }

            public void setDetallesAdicionales(final Detalle.DetallesAdicionales value) {
                this.detallesAdicionales = value;
            }

            public Detalle.Impuestos getImpuestos() {
                return this.impuestos;
            }

            public void setImpuestos(final Detalle.Impuestos value) {
                this.impuestos = value;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {"impuesto"})
            public static class Impuestos {

                @XmlElement(required = true)
                protected List<Impuesto> impuesto;

                public Impuestos() {
                    super();
                }

                public List<Impuesto> getImpuesto() {
                    if (this.impuesto == null) {
                        this.impuesto = new ArrayList();
                    }
                    return (List<Impuesto>) this.impuesto;
                }
                

            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {"detAdicional"})
            public static class DetallesAdicionales {

                @XmlElement(required = true)
                protected List<DetallesAdicionales.DetAdicional> detAdicional;

                public DetallesAdicionales() {
                    super();
                }

                public List<DetallesAdicionales.DetAdicional> getDetAdicional() {
                    if (this.detAdicional == null) {
                        this.detAdicional = new ArrayList();
                    }
                    return (List<DetallesAdicionales.DetAdicional>) this.detAdicional;
                }

                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class DetAdicional {

                    @XmlAttribute
                    protected String nombre;
                    @XmlAttribute
                    protected String valor;

                    public DetAdicional() {
                        super();
                    }

                    public String getNombre() {
                        return this.nombre;
                    }

                    public void setNombre(final String value) {
                        this.nombre = value;
                    }

                    public String getValor() {
                        return this.valor;
                    }

                    public void setValor(final String value) {
                        this.valor = value;
                    }
                }
            }
        }
    }
    //
}
