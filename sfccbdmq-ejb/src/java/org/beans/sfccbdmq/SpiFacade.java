/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.entidades.sfccbdmq.Spi;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Kardexbanco;
import org.errores.sfccbdmq.ConsultarException;
import org.procesos.sfccbdmq.AlertaGarantiasSingleton;
import org.utilitarios.sfccbdmq.AuxiliaresCorreos;

/**
 *
 * @author edwin
 */
@Stateless
public class SpiFacade extends AbstractFacade<Spi> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SpiFacade() {
        super(Spi.class);
    }

    @Override
    protected String modificarObjetos(Spi nuevo) {
        String retorno = "";
        retorno += "<banco>" + nuevo.getBanco() + "</banco>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

    public void alertaSpi(List<AuxiliaresCorreos> listaKardex) {
        Date hoy = new Date();
        Calendar fecha = Calendar.getInstance();

        String texto = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:ss");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            Codigos mailSpi = ejbCodigos.traerCodigo("MAILSPI", "MAILSPI");
            if (mailSpi == null) {
                return;
            }
            String textoMail = mailSpi.getParametros();

            for (AuxiliaresCorreos k : listaKardex) {
                texto = textoMail;
                texto = texto.replace("#nombre", k.getNumbre());
                texto = texto.replace("#valor", df.format(k.getValor()));
                texto = texto.replace("#cuenta", k.getCuenta());
                texto = texto.replace("#banco", k.getBanco());
                texto = texto.replace("#fechahora", sdf.format(new Date()));
                if (k.getCorreo() == null || k.getCorreo().isEmpty()) {
                } else {
                    ejbCorreos.enviarCorreo(k.getCorreo(), mailSpi.getDescripcion(), texto);
                }
            }
        } catch (ConsultarException | MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(AlertaGarantiasSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
