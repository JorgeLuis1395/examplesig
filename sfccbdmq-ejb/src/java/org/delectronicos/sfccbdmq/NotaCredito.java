// 
// Decompiled by Procyon v0.5.30
// 

package org.delectronicos.sfccbdmq;

import javax.xml.bind.annotation.XmlValue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.delectronicos.sfccbdmq.Factura.InfoFactura.TotalConImpuestos;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "infoTributaria", "infoNotaCredito", "detalles", "infoAdicional" })
@XmlRootElement(name = "notaCredito")
public class NotaCredito
{
    @XmlElement(required = true)
    protected InfoTributaria infoTributaria;
    @XmlElement(required = true)
    protected InfoNotaCredito infoNotaCredito;
    @XmlElement(required = true)
    protected Detalles detalles;
    protected InfoAdicional infoAdicional;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String version;
    
    public InfoTributaria getInfoTributaria() {
        return this.infoTributaria;
    }
    
    public void setInfoTributaria(final InfoTributaria value) {
        this.infoTributaria = value;
    }
    
    public InfoNotaCredito getInfoNotaCredito() {
        return this.infoNotaCredito;
    }
    
    public void setInfoNotaCredito(final InfoNotaCredito value) {
        this.infoNotaCredito = value;
    }
    
    public Detalles getDetalles() {
        return this.detalles;
    }
    
    public void setDetalles(final Detalles value) {
        this.detalles = value;
    }
    
    public InfoAdicional getInfoAdicional() {
        return this.infoAdicional;
    }
    
    public void setInfoAdicional(final InfoAdicional value) {
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
    @XmlType(name = "", propOrder = { "detalle" })
    public static class Detalles
    {
        @XmlElement(required = true)
        protected List<Detalle> detalle;
        
        public List<Detalle> getDetalle() {
            if (this.detalle == null) {
                this.detalle = new ArrayList<Detalle>();
            }
            return this.detalle;
        }
        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "codigoInterno", "codigoAdicional", "descripcion", "cantidad", "precioUnitario", "descuento", "precioTotalSinImpuesto", "detallesAdicionales", "impuestos" })
        public static class Detalle
        {
            @XmlElement(required = true)
            protected String codigoInterno;
            protected String codigoAdicional;
            @XmlElement(required = true)
            protected String descripcion;
            @XmlElement(required = true)
            protected BigDecimal cantidad;
            @XmlElement(required = true)
            protected BigDecimal precioUnitario;
            protected BigDecimal descuento;
            @XmlElement(required = true)
            protected BigDecimal precioTotalSinImpuesto;
            protected DetallesAdicionales detallesAdicionales;
            @XmlElement(required = true)
            protected Impuestos impuestos;
            
            public String getCodigoInterno() {
                return this.codigoInterno;
            }
            
            public void setCodigoInterno(final String value) {
                this.codigoInterno = value;
            }
            
            public String getCodigoAdicional() {
                return this.codigoAdicional;
            }
            
            public void setCodigoAdicional(final String value) {
                this.codigoAdicional = value;
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
            
            public DetallesAdicionales getDetallesAdicionales() {
                return this.detallesAdicionales;
            }
            
            public void setDetallesAdicionales(final DetallesAdicionales value) {
                this.detallesAdicionales = value;
            }
            
            public Impuestos getImpuestos() {
                return this.impuestos;
            }
            
            public void setImpuestos(final Impuestos value) {
                this.impuestos = value;
            }
            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = { "detAdicional" })
            public static class DetallesAdicionales
            {
                @XmlElement(required = true)
                protected List<DetAdicional> detAdicional;
                
                public List<DetAdicional> getDetAdicional() {
                    if (this.detAdicional == null) {
                        this.detAdicional = new ArrayList<DetAdicional>();
                    }
                    return this.detAdicional;
                }
                
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class DetAdicional
                {
                    @XmlAttribute
                    protected String nombre;
                    @XmlAttribute
                    protected String valor;
                    
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
            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = { "impuesto" })
            public static class Impuestos
            {
                protected List<Impuesto> impuesto;
                
                public List<Impuesto> getImpuesto() {
                    if (this.impuesto == null) {
                        this.impuesto = new ArrayList<Impuesto>();
                    }
                    return this.impuesto;
                }
            }
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "campoAdicional" })
    public static class InfoAdicional
    {
        @XmlElement(required = true)
        protected List<CampoAdicional> campoAdicional;
        
        public List<CampoAdicional> getCampoAdicional() {
            if (this.campoAdicional == null) {
                this.campoAdicional = new ArrayList<CampoAdicional>();
            }
            return this.campoAdicional;
        }
        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "value" })
        public static class CampoAdicional
        {
            @XmlValue
            protected String value;
            @XmlAttribute
            protected String nombre;
            
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
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador", "razonSocialComprador", "identificacionComprador", "contribuyenteEspecial", "obligadoContabilidad", "rise", "codDocModificado", "numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos", "compensaciones", "valorModificacion", "moneda", "totalConImpuestos", "motivo" })
    public static class InfoNotaCredito
    {
        @XmlElement(required = true)
        protected String fechaEmision;
        protected String dirEstablecimiento;
        @XmlElement(required = true)
        protected String tipoIdentificacionComprador;
        @XmlElement(required = true)
        protected String razonSocialComprador;
        protected String identificacionComprador;
        protected String contribuyenteEspecial;
        protected String obligadoContabilidad;
        protected String rise;
        @XmlElement(required = true)
        protected String codDocModificado;
        @XmlElement(required = true)
        protected String numDocModificado;
        @XmlElement(required = true)
        protected String fechaEmisionDocSustento;
        @XmlElement(required = true)
        protected BigDecimal totalSinImpuestos;
        protected compensacion compensaciones;
        @XmlElement(required = true)
        protected BigDecimal valorModificacion;
        protected String moneda;
        @XmlElement(required = true)
        protected TotalConImpuestos totalConImpuestos;
        @XmlElement(required = true)
        protected String motivo;
        
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
        
        public String getTipoIdentificacionComprador() {
            return this.tipoIdentificacionComprador;
        }
        
        public void setTipoIdentificacionComprador(final String value) {
            this.tipoIdentificacionComprador = value;
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
        
        public String getRise() {
            return this.rise;
        }
        
        public void setRise(final String value) {
            this.rise = value;
        }
        
        public String getCodDocModificado() {
            return this.codDocModificado;
        }
        
        public void setCodDocModificado(final String value) {
            this.codDocModificado = value;
        }
        
        public String getNumDocModificado() {
            return this.numDocModificado;
        }
        
        public void setNumDocModificado(final String value) {
            this.numDocModificado = value;
        }
        
        public String getFechaEmisionDocSustento() {
            return this.fechaEmisionDocSustento;
        }
        
        public void setFechaEmisionDocSustento(final String value) {
            this.fechaEmisionDocSustento = value;
        }
        
        public BigDecimal getTotalSinImpuestos() {
            return this.totalSinImpuestos;
        }
        
        public void setTotalSinImpuestos(final BigDecimal value) {
            this.totalSinImpuestos = value;
        }
        
        public BigDecimal getValorModificacion() {
            return this.valorModificacion;
        }
        
        public void setValorModificacion(final BigDecimal value) {
            this.valorModificacion = value;
        }
        
        public String getMoneda() {
            return this.moneda;
        }
        
        public void setMoneda(final String value) {
            this.moneda = value;
        }
        
        public TotalConImpuestos getTotalConImpuestos() {
            return this.totalConImpuestos;
        }
        
        public void setTotalConImpuestos(final TotalConImpuestos value) {
            this.totalConImpuestos = value;
        }
        
        public String getMotivo() {
            return this.motivo;
        }
        
        public void setMotivo(final String value) {
            this.motivo = value;
        }
        
        public void setCompensaciones(final compensacion compensaciones) {
            this.compensaciones = compensaciones;
        }
        
        public compensacion getCompensaciones() {
            return this.compensaciones;
        }
        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "compensacion" })
        public static class compensacion
        {
            @XmlElement(required = true)
            protected List<detalleCompensaciones> compensacion;
            
            public List<detalleCompensaciones> getCompensaciones() {
                if (this.compensacion == null) {
                    this.compensacion = new ArrayList<detalleCompensaciones>();
                }
                return this.compensacion;
            }
            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = { "codigo", "tarifa", "valor" })
            public static class detalleCompensaciones
            {
                @XmlElement(required = true)
                protected int codigo;
                @XmlElement(required = true)
                protected int tarifa;
                @XmlElement(required = true)
                protected BigDecimal valor;
                
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
    }
}
