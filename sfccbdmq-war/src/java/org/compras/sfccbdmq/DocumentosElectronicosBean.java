/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "documentosElectronicosSfccbdmq")
@ViewScoped
public class DocumentosElectronicosBean {

    /**
     * Creates a new instance of TipoajusteBean
     */
    public DocumentosElectronicosBean() {
    }
    @ManagedProperty(value = "#{facturaSri}")
    private FacturaSriBean facturaBean;
    @EJB
    private DocumentoselectronicosFacade ejbDocumentos;
    private String proveedor;
    private Documentoselectronicos documento;

    public SelectItem[] getComboDocumentos() {
        if ((proveedor == null) || (proveedor.isEmpty())) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fechaemision desc");
        parametros.put(";where", "o.utilizada=false");
        try {
            return Combos.getSelectItems(ejbDocumentos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosElectronicosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void cargarDocumento(){
        if (facturaBean.getFactura()!=null){
            // buscar
            Map parametros=new HashMap();
            parametros.put(";where", "o.autorizacion=:autorizacion and o.ruc=:ruc");
            parametros.put("autorizacion", facturaBean.getFactura().getAutorizacion().getNumeroAutorizacion());
            parametros.put("ruc", facturaBean.getFactura().getInfoTributaria().getRuc());
            try {
                List<Documentoselectronicos> dl=ejbDocumentos.encontarParametros(parametros);
                for (Documentoselectronicos d:dl){
                    documento=d;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(DocumentosElectronicosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public Documentoselectronicos traer(Integer id) throws ConsultarException {
        return ejbDocumentos.find(id);
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
     * @return the documento
     */
    public Documentoselectronicos getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(Documentoselectronicos documento) {
        this.documento = documento;
        if (documento != null) {
            facturaBean.cargar(documento.getXml());
        }
    }

    public String grabarUtilizado(String usuario) throws GrabarException {
        if (documento == null) {
            return null;
        }
        documento.setUtilizada(Boolean.TRUE);
        ejbDocumentos.edit(documento, usuario);
        return null;
    }

    /**
     * @return the facturaBean
     */
    public FacturaSriBean getFacturaBean() {
        return facturaBean;
    }

    /**
     * @param facturaBean the facturaBean to set
     */
    public void setFacturaBean(FacturaSriBean facturaBean) {
        this.facturaBean = facturaBean;
    }
}
