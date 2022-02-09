/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.BufferedWriter;
import org.utilitarios.sfccbdmq.NotaCreditoSri;
import org.utilitarios.sfccbdmq.FacturaSri;
import org.contabilidad.sfccbdmq.RetencionesBean;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.compras.sfccbdmq.ObligacionesBean;
import org.delectronicos.sfccbdmq.Factura;
import org.delectronicos.sfccbdmq.Impuesto;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "facturaSri")
@ViewScoped
public class FacturaSriBean {

    /**
     * Creates a new instance of FacturaSriBean
     */
    public FacturaSriBean() {
    }
    private FacturaSri factura = new FacturaSri();
    private NotaCreditoSri notaCr = new NotaCreditoSri();

    /**
     * @return the factura
     */
    public FacturaSri getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(FacturaSri factura) {
        this.factura = factura;
    }

    public String traerValor(String xml, String etiqueta) {
        String retorno = "";
        int donde = xml.indexOf("<" + etiqueta + ">");
//        int hasta = xml.indexOf("</");
        int hasta = xml.indexOf("</" + etiqueta + ">");
        if (hasta - donde > 0) {
            retorno = xml.substring(donde, hasta);
            retorno = retorno.replace("<" + etiqueta + ">", "");
        }
        return retorno;
    }

    public void cargarMarshal(File xmlStr) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Factura.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Factura factura = (Factura) jaxbUnmarshaller.unmarshal(xmlStr);
        cargar(factura);
        System.err.println("");
    }

    public void cargar(String xmlStr) {
        // crear el archivo en base al string
//        InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
//        cargar(is);
        try {
            xmlStr = xmlStr.replace("&lt;", "<");
            xmlStr = xmlStr.replace("&gt;", ">");
            xmlStr = xmlStr.replace("&quot;", "'");
            FacturaSri facturaSri = new FacturaSri();
            String numeroAutorizacion = traerValor(xmlStr, "numeroAutorizacion");
            // Información Autorización
            FacturaSri.Autorizacion aut;
            aut = facturaSri.getAutorizacion();

            String estado = traerValor(xmlStr, "estado");
            aut.setNumeroAutorizacion(numeroAutorizacion);
            aut.setEstado(estado);
            String fechaAutorizacion = traerValor(xmlStr, "fechaAutorizacion");
            //retirar si hay <> </>
//            if (fechaAutorizacion.substring(0, 1).equals("<")) {
//
//            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
            fechaAutorizacion = fechaAutorizacion.replace("-", "/");
            fechaAutorizacion = fechaAutorizacion.replace("T", " ");
            fechaAutorizacion = fechaAutorizacion.replace("PM", " ");
//                if (fechaAutorizacion.length() > 18) {
//                    fechaAutorizacion = fechaAutorizacion.substring(0, 18);
//                }
            if (fechaAutorizacion.length() > 10) {
                fechaAutorizacion = fechaAutorizacion.substring(0, 10);
            }
            String[] fechaDivididaAutorizacion = fechaAutorizacion.split("/");
            if (fechaDivididaAutorizacion[0].length() == 4) {
                // es el año
                sdf = new SimpleDateFormat("yyyy/MM/dd");
            }
            Date fAutoriz = sdf.parse(fechaAutorizacion);
            aut.setFechaAutorizacion(fAutoriz);
            facturaSri.setAutorizacion(aut);
//                raiz = document.getRootElement();

            // Información Tributaria
            FacturaSri.InfoTributaria it;
            it = facturaSri.getInfoTributaria();
            String claveAcceso = traerValor(xmlStr, "claveAcceso");
            String punto = traerValor(xmlStr, "ptoEmi");
            String sucursal = traerValor(xmlStr, "estab");
            String secuencial = traerValor(xmlStr, "secuencial");
            String ambiente = traerValor(xmlStr, "ambiente");
            String codDocSustento = traerValor(xmlStr, "codDoc");
            it.setPtoEmi(punto);
            it.setCodDoc(codDocSustento);
            it.setEstab(sucursal);
            it.setSecuencial(secuencial);
            it.setClaveAcceso(claveAcceso);
            it.setAmbiente(ambiente);
            it.setRuc(traerValor(xmlStr, "ruc"));
            it.setRazonSocial(traerValor(xmlStr, "razonSocial"));
            it.setDirMatriz(traerValor(xmlStr, "dirMatriz"));
            // Info factura
            FacturaSri.InfoFactura ifac;
            ifac = facturaSri.getInfoFactura();
            String fechaEmision = traerValor(xmlStr, "fechaEmision");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");
            fechaEmision = fechaEmision.replace("-", "/");
            if (fechaEmision.length() > 10) {
                fechaEmision = fechaEmision.substring(0, 10);
            }
            String[] fechaDividida = fechaEmision.split("/");
            if (fechaDividida[0].length() == 4) {
                // es el año
                sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            }
            Date fEmision = sdf1.parse(fechaEmision);
            facturaSri.setFechaEmi(fEmision);
            ifac.setFechaEmision(fEmision);
            ifac.setContribuyenteEspecial(traerValor(xmlStr, "contribuyenteEspecial"));
            ifac.setDirEstablecimiento(traerValor(xmlStr, "dirEstablecimiento"));
            ifac.setObligadoContabilidad(traerValor(xmlStr, "obligadoContabilidad"));
            ifac.setTipoIdentificacionComprador(traerValor(xmlStr, "tipoIdentificacionComprador"));
            ifac.setRazonSocialComprador(traerValor(xmlStr, "razonSocialComprador"));
            ifac.setIdentificacionComprador(traerValor(xmlStr, "identificacionComprador"));
            ifac.setTotalSinImpuestos(traerValor(xmlStr, "totalSinImpuestos"));
            ifac.setTotalDescuento(traerValor(xmlStr, "totalDescuento"));
            ifac.setPropina(traerValor(xmlStr, "propina"));
            ifac.setImporteTotal(traerValor(xmlStr, "importeTotal"));
            ifac.setMoneda(traerValor(xmlStr, "moneda"));
            String totalComImp = traerValor(xmlStr, "totalConImpuestos");
            String[] totalConImpuestos = totalComImp.split("</totalImpuesto>");
//            List list = totalConImpuestos.getChildren();
            for (int k = 0; k < totalConImpuestos.length; k++) {
                String tarifa = traerValor(totalConImpuestos[k], "tarifa");
                if ((tarifa == null) || (tarifa.isEmpty())) {
                    tarifa = "0";
                }
                String codigo = traerValor(totalConImpuestos[k], "codigo");
                if ((codigo == null) || (codigo.isEmpty())) {
                    codigo = "0";
                }
                String valor = traerValor(totalConImpuestos[k], "valor");
                if ((valor == null) || (valor.isEmpty())) {
                    valor = "0";
                }
                String baseImponible = traerValor(totalConImpuestos[k], "baseImponible");
                if ((baseImponible == null) || (baseImponible.isEmpty())) {
                    baseImponible = "0";
                }
                String descuentoAdicional = traerValor(totalConImpuestos[k], "descuentoAdicional");
                if ((descuentoAdicional == null) || (descuentoAdicional.isEmpty())) {
                    descuentoAdicional = "0";
                }
                ifac.agregarTotalConImpuestos(
                        codigo, valor, tarifa, descuentoAdicional,
                        traerValor(totalConImpuestos[k], "codigoPorcentaje"),
                        traerValor(totalConImpuestos[k], "baseImponible"));
            }

            facturaSri.setInfoFactura(ifac);
            String detallesStr = traerValor(xmlStr, "detalles");
            String[] detalles = detallesStr.split("</detalle>");
//            list = dEtalles.getChildren();
            for (int k = 0; k < detalles.length; k++) {
                String detalle = detalles[k];
                String impuestosStr = traerValor(detalle, "impuestos");
                String[] impuestos = impuestosStr.split("</impuesto>");
                List impLista = new LinkedList();
//                Element tabla = (Element) list.get(k);
//                List list1 = tabla.getChildren();
                for (int l = 0; l < impuestos.length; l++) {
//                    Element tablaDetalle = (Element) list1.get(l);
                    impLista.add(facturaSri.cargaImpuesto(traerValor(impuestos[l], "codigo"),
                            traerValor(impuestos[l], "valor"),
                            traerValor(impuestos[l], "codigoPorcentaje"),
                            traerValor(impuestos[l], "baseImponible"),
                            traerValor(impuestos[l], "tarifa")));
                }
                facturaSri.cargaDetalle(traerValor(detalle, "precioUnitario"),
                        traerValor(detalle, "descuento"),
                        traerValor(detalle, "codigoPrincipal"),
                        impLista,
                        traerValor(detalle, "cantidad"),
                        traerValor(detalle, "descripcion"),
                        traerValor(detalle, "precioTotalSinImpuesto"));
            }
//            Element infoAdicional = raiz1.getChild("infoAdicional");
            String infoAdicional = traerValor(xmlStr, "infoAdicional");
            if (!((infoAdicional == null) || (infoAdicional.isEmpty()))) {
                String[] info = infoAdicional.split("</campoAdicional>");
                //listaAdicionales = new LinkedList<>();
                for (int m = 0; m < info.length; m++) {
                    //Se obtiene el elemento 'tabla'

                    String campo = info[m];
                    if (campo.indexOf(">") > 0) {
                        int desdeCampo = campo.indexOf("nombre=");
                        int hastaCampo = campo.indexOf(">");
                        desdeCampo=desdeCampo<0?(desdeCampo*-1+hastaCampo):desdeCampo; 
                        if (desdeCampo <= hastaCampo) {
                            String nombreCampo = campo.substring(campo.indexOf("nombre="), campo.indexOf(">"));
                            nombreCampo = nombreCampo.replace("nombre=", "");
                            int donde = campo.indexOf(">");
                            int hasta = campo.length();
//                    int hasta = campo.indexOf("</");

                            String valor = campo.substring(donde, hasta);
                            valor = valor.replace(">", "");
                            facturaSri.cargaAdicional(nombreCampo, valor);
                        }
                    }
                }
            }
            setFactura(facturaSri);
            //
        } catch (ParseException ex) {
//            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cargar(Factura fact) {
        // crear el archivo en base al string
//        InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
//        cargar(is);
        try {

            FacturaSri facturaSri = new FacturaSri();
            String numeroAutorizacion = fact.getInfoTributaria().getClaveAcceso();
            // Información Autorización
            FacturaSri.Autorizacion aut;
            aut = facturaSri.getAutorizacion();

            String estado = "";
            aut.setNumeroAutorizacion(numeroAutorizacion);
            aut.setEstado(estado);
            String fechaAutorizacion = fact.getInfoFactura().getFechaEmision();
            //retirar si hay <> </>
//            if (fechaAutorizacion.substring(0, 1).equals("<")) {
//
//            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
            fechaAutorizacion = fechaAutorizacion.replace("-", "/");
            fechaAutorizacion = fechaAutorizacion.replace("T", " ");
            fechaAutorizacion = fechaAutorizacion.replace("PM", " ");
//                if (fechaAutorizacion.length() > 18) {
//                    fechaAutorizacion = fechaAutorizacion.substring(0, 18);
//                }
            if (fechaAutorizacion.length() > 10) {
                fechaAutorizacion = fechaAutorizacion.substring(0, 10);
            }
            String[] fechaDivididaAutorizacion = fechaAutorizacion.split("/");
            if (fechaDivididaAutorizacion[0].length() == 4) {
                // es el año
                sdf = new SimpleDateFormat("yyyy/MM/dd");
            }
            Date fAutoriz = sdf.parse(fechaAutorizacion);
            aut.setFechaAutorizacion(fAutoriz);
            facturaSri.setAutorizacion(aut);
//                raiz = document.getRootElement();

            // Información Tributaria
            FacturaSri.InfoTributaria it;
            it = facturaSri.getInfoTributaria();
            String claveAcceso = fact.getInfoTributaria().getClaveAcceso();
            String punto = fact.getInfoTributaria().getPtoEmi();
            String sucursal = fact.getInfoTributaria().getEstab();
            String secuencial = fact.getInfoTributaria().getSecuencial();
            String ambiente = fact.getInfoTributaria().getAmbiente();
            String codDocSustento = fact.getInfoTributaria().getCodDoc();
            it.setPtoEmi(punto);
            it.setCodDoc(codDocSustento);
            it.setEstab(sucursal);
            it.setSecuencial(secuencial);
            it.setClaveAcceso(claveAcceso);
            it.setAmbiente(ambiente);
            it.setRuc(fact.getInfoTributaria().getRuc());
            it.setRazonSocial(fact.getInfoTributaria().getRazonSocial());
            it.setDirMatriz(fact.getInfoTributaria().getDirMatriz());
            // Info factura
            FacturaSri.InfoFactura ifac;
            ifac = facturaSri.getInfoFactura();
            String fechaEmision = fact.getInfoFactura().getFechaEmision();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");
            fechaEmision = fechaEmision.replace("-", "/");
            if (fechaEmision.length() > 10) {
                fechaEmision = fechaEmision.substring(0, 10);
            }
            String[] fechaDividida = fechaEmision.split("/");
            if (fechaDividida[0].length() == 4) {
                // es el año
                sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            }
            Date fEmision = sdf1.parse(fechaEmision);
            facturaSri.setFechaEmi(fEmision);
            ifac.setFechaEmision(fEmision);
            ifac.setContribuyenteEspecial(fact.getInfoFactura().getContribuyenteEspecial());
            ifac.setDirEstablecimiento(fact.getInfoFactura().getDirEstablecimiento());
            ifac.setObligadoContabilidad(fact.getInfoFactura().getObligadoContabilidad());
            ifac.setTipoIdentificacionComprador(fact.getInfoFactura().getTipoIdentificacionComprador());
            ifac.setRazonSocialComprador(fact.getInfoFactura().getRazonSocialComprador());
            ifac.setIdentificacionComprador(fact.getInfoFactura().getIDireccionComprador());
            ifac.setTotalSinImpuestos(fact.getInfoFactura().getTotalSinImpuestos().toString());
            ifac.setTotalDescuento(fact.getInfoFactura().getTotalDescuento().toString());
            ifac.setPropina(fact.getInfoFactura().getPropina().toString());
            ifac.setImporteTotal(fact.getInfoFactura().getImporteTotal().toString());
            ifac.setMoneda(fact.getInfoFactura().getMoneda());
            for (Factura.InfoFactura.TotalConImpuestos.TotalImpuesto ti
                    : fact.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()) {
                ifac.agregarTotalConImpuestos(
                        ti.getCodigo(), ti.getValor() == null ? "0" : ti.getValor().toString(),
                        ti.getTarifa() == null ? "" : ti.getTarifa().toString(), "",
                        ti.getCodigoPorcentaje(),
                        ti.getBaseImponible() == null ? "0" : ti.getBaseImponible().toString());
            }

            facturaSri.setInfoFactura(ifac);
            for (Factura.Detalles.Detalle d : fact.getDetalles().getDetalle()) {
                List impLista = new LinkedList();
                for (Impuesto i : d.getImpuestos().getImpuesto()) {
//                    Element tablaDetalle = (Element) list1.get(l);
                    impLista.add(
                            facturaSri.cargaImpuesto(
                                    i.getCodigo(),
                                    i.getValor().toString(),
                                    i.getCodigoPorcentaje().toString(),
                                    i.getBaseImponible().toString(),
                                    i.getBaseImponible().toString()));
                }
                facturaSri.cargaDetalle(d.getPrecioUnitario().toString(),
                        d.getDescuento().toString(),
                        d.getCodigoPrincipal(),
                        impLista,
                        d.getCantidad().toString(),
                        d.getDescripcion(),
                        d.getPrecioTotalSinImpuesto().toString());
            }

//            Element infoAdicional = raiz1.getChild("infoAdicional");
            for (Factura.InfoAdicional.CampoAdicional ca : fact.getInfoAdicional().getCampoAdicional()) {
                facturaSri.cargaAdicional(ca.getNombre(), ca.getValue());
            }

            setFactura(facturaSri);
            //
        } catch (ParseException ex) {
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cargarX(String xmlStr) {
        // crear el archivo en base al string
//        InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
//        cargar(is);
    }

    public void cargarNc(String xmlStr) {
        // crear el archivo en base al string
        InputStream is = new ByteArrayInputStream(xmlStr.getBytes());

    }

    /**
     * @return the notaCr
     */
    public NotaCreditoSri getNotaCr() {
        return notaCr;
    }

    /**
     * @param notaCr the notaCr to set
     */
    public void setNotaCr(NotaCreditoSri notaCr) {
        this.notaCr = notaCr;
    }

    public File grabarXml(String nombre, String xml) {
//    private File grabarXml(String directorio, String nombre, String xml) {

        BufferedWriter writer = null;
        File archivoRetorno = null;
        try {
//            archivoRetorno = File.createTempFile(nombre , ".xml");
            String directorio = System.getProperty("java.io.tmpdir");
            archivoRetorno = new File(directorio + "/" + nombre + ".xml");
//            archivoRetorno = new File(directorio + nombre + ".xml");
//            archivoRetorno = new File(directorio + "/xml/" + comprobante.getInfoTributaria().getClaveAcceso() + ".xml");
            writer = new BufferedWriter(new FileWriter(archivoRetorno));
            writer.write(xml);
        } catch (IOException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
//                        xmlFile=outFile; 
                writer.close();

            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return archivoRetorno;
    }
}