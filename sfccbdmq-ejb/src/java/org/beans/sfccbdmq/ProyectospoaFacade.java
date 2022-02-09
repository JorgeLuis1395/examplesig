/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Proyectospoa;

/**
 *
 * @author luis
 */
@Stateless
public class ProyectospoaFacade extends AbstractFacade<Proyectospoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProyectospoaFacade() {
        super(Proyectospoa.class);
    }

    @Override
    protected String modificarObjetos(Proyectospoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<nivel>" + nuevo.getNivel() + "</nivel>";
        retorno += "<padre>" + nuevo.getPadre() + "</padre>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<definitivo>" + nuevo.getDefinitivo() + "</definitivo>";
        retorno += "<alineado>" + nuevo.getAlineado() + "</alineado>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";
        retorno += "<imputable>" + nuevo.getImputable() + "</imputable>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<inicio>" + nuevo.getInicio() + "</inicio>";
        retorno += "<termino>" + nuevo.getTermino() + "</termino>";
        retorno += "<direccion>" + nuevo.getDireccion() + "</direccion>";
        retorno += "<cpc>" + nuevo.getCpc() + "</cpc>";
        retorno += "<tipocompra>" + nuevo.getTipocompra() + "</tipocompra>";
        retorno += "<detalle>" + nuevo.getDetalle() + "</detalle>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        retorno += "<unidad>" + nuevo.getUnidad() + "</unidad>";
        retorno += "<valoriva>" + nuevo.getValoriva() + "</valoriva>";
        retorno += "<impuesto>" + nuevo.getImpuesto() + "</impuesto>";
        retorno += "<cuatrimestre1>" + nuevo.getCuatrimestre1() + "</cuatrimestre1>";
        retorno += "<cuatrimestre2>" + nuevo.getCuatrimestre2() + "</cuatrimestre2>";
        retorno += "<cuatrimestre3>" + nuevo.getCuatrimestre3() + "</cuatrimestre3>";
        retorno += "<tipoproducto>" + nuevo.getTipoproducto() + "</tipoproducto>";
        retorno += "<catalogoelectronico>" + nuevo.getCatalogoelectronico() + "</catalogoelectronico>";
        retorno += "<procedimientocontratacion>" + nuevo.getProcedimientocontratacion() + "</procedimientocontratacion>";
        retorno += "<numerooperacion>" + nuevo.getNumerooperacion() + "</numerooperacion>";
        retorno += "<codigooperacion>" + nuevo.getNumerooperacion() + "</codigooperacion>";
        retorno += "<regimen>" + nuevo.getRegimen() + "</regimen>";
        retorno += "<presupuesto>" + nuevo.getPresupuesto() + "</presupuesto>";
        retorno += "<fechautilizacion>" + nuevo.getFechautilizacion() + "</fechautilizacion>";
        retorno += "<pac>" + nuevo.getPac()+ "</pac>";
        retorno += "<fechapac>" + nuevo.getFechapac() + "</fechapac>";
        retorno += "<fechacreacion>" + nuevo.getFechacreacion() + "</fechacreacion>";
        retorno += "<fechareforma>" + nuevo.getFechacreacion() + "</fechareforma>";
        retorno += "<modificacion>" + nuevo.getModificacion() + "</modificacion>";
        retorno += "<responsable>" + nuevo.getResponsable()+ "</responsable>";
        retorno += "<documento>" + nuevo.getDocumento()+ "</documento>";
        retorno += "<direccionant>" + nuevo.getDireccionant()+ "</direccionant>";
        retorno += "<reformapac>" + nuevo.getReformapac()+ "</reformapac>";
//        retorno += "<nombremc>" + nuevo.getNombre()+ "</nombremc>";

        return retorno;
    }

}
