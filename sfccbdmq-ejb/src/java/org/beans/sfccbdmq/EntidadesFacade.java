/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Entidades;
import org.errores.sfccbdmq.ConsultarException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Codigos;
import org.utilitarios.sfccbdmq.ActiveDirectory;
import org.wscliente.sfccbdmq.AuxiliarEntidad;

/**
 *
 * @author edwin
 */
@Stateless
public class EntidadesFacade extends AbstractFacade<Entidades> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB private CodigosFacade ejbCodigos;
    @EJB private EntidadesFacade ejbRef;
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadesFacade() {
        super(Entidades.class);
    }

    @Override
    protected String modificarObjetos(Entidades nuevo) {
        String retorno = "";
        retorno += "<rol>" + nuevo.getRol() + "</rol>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombres>" + nuevo.getNombres() + "</nombres>";
        retorno += "<apellidos>" + nuevo.getApellidos() + "</apellidos>";
        retorno += "<email>" + nuevo.getEmail() + "</email>";
        retorno += "<userid>" + nuevo.getUserid() + "</userid>";
        retorno += "<pwd>" + nuevo.getPwd() + "</pwd>";
        retorno += "<pin>" + nuevo.getPin() + "</pin>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<pregunta>" + nuevo.getPregunta() + "</pregunta>";
        retorno += "<genero>" + nuevo.getGenero() + "</genero>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
//        retorno += "<estadocivil>" + nuevo.getEstadocivil()+ "</estadocivil>";
        retorno += "<grupocontable>" + nuevo.getGrupocontable() + "</grupocontable>";

        return retorno;
    }

    public Entidades login(String usuario, String clave)
            throws ConsultarException {
        Entidades retorno = null;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.userid=:userid and o.pwd=:pwd and o.activo=true");
            parametros.put("userid", usuario);
            parametros.put("pwd", clave);
            List<Entidades> lista = super.encontarParametros(parametros);
            if ((lista == null) || (lista.isEmpty())) {
                return null;
            }
            retorno = lista.get(0);
        } catch (Exception e) {
            throw new ConsultarException("ERROR LOGIN", e);
        }
        return retorno;
    }

    public Entidades loginAD(String usuario,
            String clave)
            throws ConsultarException {

        LdapContext ctx;
        String error = "";
        try {
            List<Codigos> lista = ejbCodigos.traerCodigos("ACTDIR");
            String dominio = null;
            String servidor = null;

            for (Codigos c : lista) {
                if (c.getCodigo().equals("DOM")) {
                    dominio = c.getParametros();
                }
                if (c.getCodigo().equals("SRV")) {
                    servidor = c.getParametros();
                }
            }
            ctx = ActiveDirectory.getConnection(usuario, clave, dominio, servidor);
            ctx.close();
        } catch (NamingException ex) {
            error = ex.getMessage();
        } catch (ConsultarException ex) {
            return null;
        }
        if (!error.isEmpty()) {
            
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.userid=:usuarioad");
        parametros.put("usuarioad", usuario);
        List<Entidades> lista = ejbRef.encontarParametros(parametros);
        AuxiliarEntidad a = new AuxiliarEntidad();
        for (Entidades e : lista) {
            return e;
        }

        return null;
    }

    private String getErrorCode(String exceptionMsg) {
        String pattern = "^\\p{XDigit}+$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(exceptionMsg);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    private String getDescripcion(String error) {
        switch (error.trim()) {
            case "525":
                return "Usuario no encontrado!";
            case "52e":
                return "Credenciales no son válidas!";
            case "530":
                return "No permitido para iniciar la sesión en este momento!";
            case "531":
                return "No permitido para iniciar sesión en esta estación de trabajo!";
            case "532":
                return "Contraseña caducada!";
            case "533":
                return "Cuenta deshabilitada!";
            case "701":
                return "Cuenta caducada!";
            case "773":
                return "Debe restablecer la contraseña!";
            case "775":
                return "Cuenta de usuario bloqueada!";
            default:
                return error;
        }
    }
    public Entidades traerUserId(String userid) throws ConsultarException {
        try {

            String sql = "Select object(o) from Entidades as o where  o.userid=:userid";
            Query q = em.createQuery(sql);
            q.setParameter("userid", userid);
            List<Entidades> resultado = q.getResultList();
            for (Entidades o : resultado) {
                return o;
            }
        } catch (Exception e) {
            throw new ConsultarException("", e);
        }
        return null;
    }
}