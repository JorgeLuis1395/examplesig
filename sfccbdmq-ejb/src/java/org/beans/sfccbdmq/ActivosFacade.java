/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Empleados;
import org.errores.sfccbdmq.InsertarException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
@Stateless
public class ActivosFacade extends AbstractFacade<Activos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActivosFacade() {
        super(Activos.class);
    }

    @Override
    protected String modificarObjetos(Activos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<marca>" + nuevo.getMarca() + "</marca>";
        retorno += "<modelo>" + nuevo.getModelo() + "</modelo>";
        retorno += "<aniofabricacion>" + nuevo.getAniofabricacion() + "</aniofabricacion>";
        retorno += "<color>" + nuevo.getColor() + "</color>";
        retorno += "<imagen>" + nuevo.getImagen() + "</imagen>";
        retorno += "<depreciable>" + nuevo.getDepreciable() + "</depreciable>";
        retorno += "<control>" + nuevo.getControl() + "</control>";
        retorno += "<localizacion>" + nuevo.getLocalizacion() + "</localizacion>";
        retorno += "<clasificacion>" + nuevo.getClasificacion() + "</clasificacion>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<inventario>" + nuevo.getInventario() + "</inventario>";
        retorno += "<alterno>" + nuevo.getAlterno() + "</alterno>";
        retorno += "<barras>" + nuevo.getBarras() + "</barras>";
        retorno += "<custodio>" + nuevo.getCustodio() + "</custodio>";
        retorno += "<externo>" + nuevo.getExterno() + "</externo>";
        retorno += "<vidautil>" + nuevo.getVidautil() + "</vidautil>";
        retorno += "<valoradquisiscion>" + nuevo.getValoradquisiscion() + "</valoradquisiscion>";
        retorno += "<fechaalta>" + nuevo.getFechaalta() + "</fechaalta>";
        retorno += "<valoralta>" + nuevo.getValoralta() + "</valoralta>";
        retorno += "<valorreposicion>" + nuevo.getValorreposicion() + "</valorreposicion>";
        retorno += "<fechadepreciacion>" + nuevo.getFechadepreciacion() + "</fechadepreciacion>";
        retorno += "<fechabaja>" + nuevo.getFechabaja() + "</fechabaja>";
        retorno += "<causa>" + nuevo.getCausa() + "</causa>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<padre>" + nuevo.getPadre() + "</padre>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<valorresidual>" + nuevo.getValorresidual() + "</valorresidual>";
        retorno += "<numeroserie>" + nuevo.getNumeroserie() + "</numeroserie>";
        retorno += "<grupo>" + nuevo.getGrupo() + "</grupo>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<fechaingreso>" + nuevo.getFechaingreso() + "</fechaingreso>";
        retorno += "<alta>" + nuevo.getAlta() + "</alta>";
        retorno += "<baja>" + nuevo.getBaja() + "</baja>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<solicitante>" + nuevo.getSolicitante() + "</solicitante>";
        retorno += "<fechasolicitud>" + nuevo.getFechasolicitud() + "</fechasolicitud>";
        retorno += "<autoriza>" + nuevo.getAutoriza() + "</autoriza>";
        retorno += "<fvengarantia>" + nuevo.getFvengarantia()+ "</fvengarantia>";
        retorno += "<unigarantia>" + nuevo.getUnigarantia()+ "</unigarantia>";
        retorno += "<garantia>" + nuevo.getGarantia()+ "</garantia>";
        retorno += "<control>" + nuevo.getControl()+ "</control>";
        retorno += "<operativo>" + nuevo.getOperativo()+ "</operativo>";
        retorno += "<subgrupo>" + nuevo.getSubgrupo()+ "</subgrupo>";
        retorno += "<ccosto>" + nuevo.getCcosto()+ "</ccosto>";
        retorno += "<acta>" + nuevo.getActa()+ "</acta>";
        retorno += "<actaultima>" + nuevo.getActaultima()+ "</actaultima>";
        retorno += "<fechapuestaenservicio>" + nuevo.getFechapuestaenservicio()+ "</fechapuestaenservicio>";
        retorno += "<iva>" + nuevo.getIva()+ "</iva>";
        retorno += "<dimensiones>" + nuevo.getDimensiones()+ "</dimensiones>";
        retorno += "<caracteristicas>" + nuevo.getCaracteristicas()+ "</caracteristicas>";
        retorno += "<factura>" + nuevo.getFactura()+ "</factura>";
        retorno += "<usuario>" + nuevo.getUsuario()+ "</usuario>";
        retorno += "<activomarca>" + nuevo.getActivomarca()+ "</activomarca>";
        retorno += "<tipoauxiliar>" + nuevo.getTipoauxiliar()+ "</tipoauxiliar>";
        retorno += "<nominativo>" + nuevo.getNominativo()+ "</nominativo>";

        return retorno;
    }

    public void cambiarCustodio(Empleados custodioOrigen, Empleados custodioDestino) throws InsertarException {
        try {
            Query q = em.createQuery("UPDATE Activos as o SET o.custodio=:custodioOrigen Where o.custodio=:custodioDestino");
            q.setParameter("custodioOrigen", custodioOrigen);
            q.setParameter("custodioDestino", custodioDestino);
            q.executeUpdate();
        } catch (Exception e) {
            throw new InsertarException("Activos", e);
        }
    }
}
