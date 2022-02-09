/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.utilitarios.sfccbdmq;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author edwin
 */
public class ComprobanteRetencion {

    private String id;

    private InfoTributaria infoTributaria;

    private List<Impuesto> impuestos;

    private InfoCompRetencion infoCompRetencion;

    private List<CampoAdicional> infoAdicional;

    private String version;

    public ComprobanteRetencion() {
        impuestos = new LinkedList<>();
        infoAdicional = new LinkedList<>();
        infoTributaria = new InfoTributaria();
        infoCompRetencion = new InfoCompRetencion();
    }

    public Impuesto cargaImpuesto(String codigo,
            String codigoRetencion,
            String fechaEmisionDocSustento,
            Integer porcentajeRetener,
            String codDocSustento,
            String baseImponible,
            String numDocSustento,
            String valorRetenido) {
        Impuesto i = new Impuesto();
        i.setCodigo(codigo);
        i.setCodDocSustento(codDocSustento);
        i.setBaseImponible(baseImponible);
        i.setCodigoRetencion(codigoRetencion);
        i.setFechaEmisionDocSustento(fechaEmisionDocSustento);
        i.setNumDocSustento(numDocSustento);
        i.setPorcentajeRetener(porcentajeRetener.toString());
        i.setValorRetenido(valorRetenido);
        if (impuestos == null) {
            impuestos = new LinkedList<>();
        }
        impuestos.add(i);
        return i;
    }

    public void calculaClave(String campoAdicional) {
        Calendar c = Calendar.getInstance();
        if (infoTributaria.getSecuencial() == null) {
            infoTributaria.setSecuencial(String.valueOf(c.getTimeInMillis()));
        }

        String fecha = infoCompRetencion.fechaEmision.replace("/", "");
        String clave = fecha + infoTributaria.codDoc + infoTributaria.ruc
                + (infoTributaria.ambiente == null ? "1" : infoTributaria.ambiente)
                + (infoTributaria.estab == null ? "001" : infoTributaria.estab)
                + (infoTributaria.ptoEmi == null ? "001" : infoTributaria.ptoEmi)
                + infoTributaria.getSecuencial()
                //                + (infoTributaria.secuencial == null ? c.getTimeInMillis() : infoTributaria.secuencial)
                + campoAdicional + infoTributaria.tipoEmision;
//                + "12345678" + infoTributaria.tipoEmision;
        String digitoVerificador = String.valueOf(obtenerSumaPorDigitos(invertirCadena(clave)));
        infoTributaria.setClaveAcceso(clave + digitoVerificador);
    }

    private static String invertirCadena(String cadena) {
        String cadenaInvertida = "";
        for (int x = cadena.length() - 1; x >= 0; x--) {
            cadenaInvertida = cadenaInvertida + cadena.charAt(x);
        }
        return cadenaInvertida;
    }

    private int obtenerSumaPorDigitos(String cadena) {
        int pivote = 2;
        int longitudCadena = cadena.length();
        int cantidadTotal = 0;
        int b = 1;
        for (int i = 0; i < longitudCadena; i++) {
            if (pivote == 8) {
                pivote = 2;
            }
            int temporal = Integer.parseInt("" + cadena.substring(i, b));
            b++;
            temporal *= pivote;
            pivote++;
            cantidadTotal += temporal;
        }
        cantidadTotal = 11 - cantidadTotal % 11;
        switch (cantidadTotal) {
            case 10:
                cantidadTotal = 1;
                break;
            case 11:
                cantidadTotal = 0;
                break;
        }
        return cantidadTotal;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    public List<Impuesto> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<Impuesto> impuestos) {
        this.impuestos = impuestos;
    }

    public InfoCompRetencion getInfoCompRetencion() {
        return infoCompRetencion;
    }

    public void setInfoCompRetencion(InfoCompRetencion infoCompRetencion) {
        this.infoCompRetencion = infoCompRetencion;
    }

    public List<CampoAdicional> getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(List<CampoAdicional> infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        String impStr = "<impuestos>\n";
        for (Impuesto i : impuestos) {
            impStr += i.toString();
        }
        impStr += "</impuestos>\n";
        String infoAdStr = "<infoAdicional>\n";
        for (CampoAdicional c : infoAdicional) {
            infoAdStr += c.toString();
        }
        infoAdStr += "</infoAdicional>\n";
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                + "<comprobanteRetencion  id=\"comprobante\" version=\"1.0.0\">\n"
                + "<comprobanteRetencion xmlns:ns1=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"C:\\Documents and Settings\\mfsalazar\\Escritorio\\facturación electronica\\formato xsd xml 07-03\\comprobanteRetencion1.xsd\" id=\"comprobante\" version=\"1.0.0\">"
                + infoTributaria.toString()
                + infoCompRetencion.toString()
                + impStr
                + infoAdStr
                + "</comprobanteRetencion>";

    }

    // Info tributaria 
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
            if (secuencial == null) {
                secuencial = "";
            }
            if (secuencial.length() < 9) {
                int sec = Integer.valueOf(secuencial);
                DecimalFormat df = new DecimalFormat("000000000");
                secuencial = df.format(sec);
            } else if (secuencial.length() > 9) {
                secuencial = secuencial.substring(0, 9);
            }
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

            return "<infoTributaria> \n"
                    + "<ambiente>" + ambiente + "</ambiente> \n"
                    + "<tipoEmision>" + tipoEmision + "</tipoEmision> \n"
                    + "<razonSocial>" + razonSocial + "</razonSocial> \n"
                    + "<nombreComercial>" + nombreComercial + "</nombreComercial>\n"
                    + "<ruc>" + ruc + "</ruc> \n"
                    + "<claveAcceso>" + claveAcceso + "</claveAcceso>\n "
                    + "<codDoc>" + codDoc + "</codDoc> \n"
                    + "<estab>" + estab + "</estab>\n"
                    + "<ptoEmi>" + ptoEmi + "</ptoEmi> \n"
                    + "<secuencial>" + secuencial + "</secuencial> \n"
                    + "<dirMatriz>" + dirMatriz + "</dirMatriz>\n "
                    + "</infoTributaria>\n";
        }
    }
    // Fin Info tributaria 
    // Inofo Comprobante de REtencion

    public class InfoCompRetencion {

        private String contribuyenteEspecial;

        private String periodoFiscal;

        private String tipoIdentificacionSujetoRetenido;

        private String razonSocialSujetoRetenido;

        private String dirEstablecimiento;

        private String fechaEmision;

        private String obligadoContabilidad;

        private String identificacionSujetoRetenido;

        public String getContribuyenteEspecial() {
            return contribuyenteEspecial;
        }

        public void setContribuyenteEspecial(String contribuyenteEspecial) {
            if (contribuyenteEspecial != null) {
                if (contribuyenteEspecial.isEmpty()) {
                    contribuyenteEspecial = null;
                } else if (contribuyenteEspecial.length() < 4) {
                    contribuyenteEspecial = null;
                } else if (contribuyenteEspecial.length() > 4) {
                    contribuyenteEspecial = null;
                }
            }
            this.contribuyenteEspecial = contribuyenteEspecial;
        }

        public String getPeriodoFiscal() {
            return periodoFiscal;
        }

        public void setPeriodoFiscal(String periodoFiscal) {
            this.periodoFiscal = periodoFiscal;
        }

        public String getTipoIdentificacionSujetoRetenido() {
            return tipoIdentificacionSujetoRetenido;
        }

        public void setTipoIdentificacionSujetoRetenido(String tipoIdentificacionSujetoRetenido) {
            this.tipoIdentificacionSujetoRetenido = tipoIdentificacionSujetoRetenido;
        }

        public String getRazonSocialSujetoRetenido() {
            return razonSocialSujetoRetenido;
        }

        public void setRazonSocialSujetoRetenido(String razonSocialSujetoRetenido) {
            this.razonSocialSujetoRetenido = razonSocialSujetoRetenido;
        }

        public String getDirEstablecimiento() {
            return dirEstablecimiento;
        }

        public void setDirEstablecimiento(String dirEstablecimiento) {
            this.dirEstablecimiento = dirEstablecimiento;
        }

        public String getFechaEmision() {
            return fechaEmision;
        }

        public void setFechaEmision(String fechaEmision) {
            this.fechaEmision = fechaEmision;
        }

        public String getObligadoContabilidad() {
            return obligadoContabilidad;
        }

        public void setObligadoContabilidad(String obligadoContabilidad) {
            this.obligadoContabilidad = obligadoContabilidad;
        }

        public String getIdentificacionSujetoRetenido() {
            return identificacionSujetoRetenido;
        }

        public void setIdentificacionSujetoRetenido(String identificacionSujetoRetenido) {
            this.identificacionSujetoRetenido = identificacionSujetoRetenido;
        }

        @Override
        public String toString() {
            return "<infoCompRetencion> \n"
                    + "<fechaEmision>" + fechaEmision + "</fechaEmision> \n"
                    + "<dirEstablecimiento>" + dirEstablecimiento + "</dirEstablecimiento> \n"
                    + (contribuyenteEspecial==null?"":"<contribuyenteEspecial>" + contribuyenteEspecial + "</contribuyenteEspecial> \n")
                    + "<obligadoContabilidad>" + obligadoContabilidad + "</obligadoContabilidad> \n"
                    + "<tipoIdentificacionSujetoRetenido>" + tipoIdentificacionSujetoRetenido + "</tipoIdentificacionSujetoRetenido> \n"
                    + "<razonSocialSujetoRetenido>" + razonSocialSujetoRetenido + "</razonSocialSujetoRetenido>\n"
                    + "<identificacionSujetoRetenido>" + identificacionSujetoRetenido + "</identificacionSujetoRetenido>\n"
                    + "<periodoFiscal>" + periodoFiscal + "</periodoFiscal> \n"
                    + "</infoCompRetencion>\n";
        }
    }

    // fin Inofo Comprobante de REtencion
    // Adicionales
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
            return "<campoAdicional nombre = '" + nombre + "'>" + content + "</campoAdicional>\n";
        }
    }

    // fin adicionales
    // Impuestos
    public class Impuesto {

        private String codigo;
        private String codigoRetencion;
        private String fechaEmisionDocSustento;
        private String porcentajeRetener;
        private String codDocSustento;
        private String baseImponible;
        private String numDocSustento;
        private String valorRetenido;

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigoRetencion() {
            return codigoRetencion;
        }

        public void setCodigoRetencion(String codigoRetencion) {
            this.codigoRetencion = codigoRetencion;
        }

        public String getFechaEmisionDocSustento() {
            return fechaEmisionDocSustento;
        }

        public void setFechaEmisionDocSustento(String fechaEmisionDocSustento) {
            this.fechaEmisionDocSustento = fechaEmisionDocSustento;
        }

        public String getPorcentajeRetener() {
            return porcentajeRetener;
        }

        public void setPorcentajeRetener(String porcentajeRetener) {
            this.porcentajeRetener = porcentajeRetener;
        }

        public String getCodDocSustento() {
            return codDocSustento;
        }

        public void setCodDocSustento(String codDocSustento) {
            this.codDocSustento = codDocSustento;
        }

        public String getBaseImponible() {
            return baseImponible;
        }

        public void setBaseImponible(String baseImponible) {
            this.baseImponible = baseImponible;
        }

        public String getNumDocSustento() {
            return numDocSustento;
        }

        public void setNumDocSustento(String numDocSustento) {
            this.numDocSustento = numDocSustento;
        }

        public String getValorRetenido() {
            return valorRetenido;
        }

        public void setValorRetenido(String valorRetenido) {
            this.valorRetenido = valorRetenido;
        }

        @Override
        public String toString() {
            return "<impuesto>\n "
                    + "<codigo>" + codigo.trim() + "</codigo> \n"
                    + "<codigoRetencion>" + codigoRetencion.trim() + "</codigoRetencion> \n"
                    + "<baseImponible>" + baseImponible + "</baseImponible> \n"
                    + "<porcentajeRetener>" + porcentajeRetener + "</porcentajeRetener> \n"
                    + "<valorRetenido>" + valorRetenido + "</valorRetenido>\n"
                    + "<codDocSustento>" + codDocSustento + "</codDocSustento>\n"
                    + "<numDocSustento>" + numDocSustento + "</numDocSustento> \n"
                    + "<fechaEmisionDocSustento>" + fechaEmisionDocSustento + "</fechaEmisionDocSustento> \n"
                    + "</impuesto>\n";
        }
    }
    // fin Impuestos
}