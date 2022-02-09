/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Certificacionespoa;

/**
 *
 * @author luis
 */
@Stateless
public class CertificacionespoaFacade extends AbstractFacade<Certificacionespoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CertificacionespoaFacade() {
        super(Certificacionespoa.class);
    }

    @Override
    protected String modificarObjetos(Certificacionespoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<impreso>" + nuevo.getImpreso() + "</impreso>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<anulado>" + nuevo.getAnulado() + "</anulado>";
        retorno += "<roles>" + nuevo.getRoles() + "</roles>";
        retorno += "<direccion>" + nuevo.getDireccion() + "</direccion>";
        retorno += "<rechazado>" + nuevo.getRechazado() + "</rechazado>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<apresupuestaria>" + nuevo.getApresupuestaria() + "</apresupuestaria>";
        retorno += "<liquidar>" + nuevo.getLiquidar() + "</liquidar>";
        retorno += "<acargo>" + nuevo.getAcargo() + "</acargo>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<finaciero>" + nuevo.getFinaciero() + "</finaciero>";
        retorno += "<fechaaprobacion>" + nuevo.getFechaaprobacion() + "</fechaaprobacion>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<generapac>" + nuevo.getGenerapac() + "</generapac>";
        retorno += "<fechapac>" + nuevo.getFechaPAC() + "</fechapac>";
        retorno += "<usuariopac>" + nuevo.getUsuariopac() + "</usuariopac>";
        retorno += "<impresopac>" + nuevo.getImpresopac() + "</impresopac>";
        retorno += "<numeropac>" + nuevo.getNumeroPAC() + "</numeropac>";
        retorno += "<archivo>" + nuevo.getArchivo() + "</archivo>";
        retorno += "<fechadocumento>" + nuevo.getFechadocumento() + "</fechadocumento>";
        retorno += "<certificaque>" + nuevo.getCertificaque() + "</certificaque>";
        retorno += "<observacionrechazo>" + nuevo.getObservacionrechazo() + "</observacionrechazo>";
        retorno += "<rechazadopac>" + nuevo.getRechazadopac() + "</rechazadopac>";
        retorno += "<obsrechazadopac>" + nuevo.getObsrechazadopac() + "</obsrechazadopac>";
        retorno += "<infimanoplanificada>" + nuevo.getInfimanoplanificada() + "</infimanoplanificada>";
        retorno += "<aprobardga>" + nuevo.getAprobardga() + "</aprobardga>";
        retorno += "<archivopac>" + nuevo.getArchivopac() + "</archivopac>";
        retorno += "<archivosolicitud>" + nuevo.getArchivosolicitud() + "</archivosolicitud>";
//        retorno += "<dependencia>" + nuevo.getDependencia() + "</dependencia>";

        return retorno;
    }

}
