/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.util.List;

/**
 *
 * @author edwin
 */
public class Rdep {

    private String anio;

    private RetRelDep retRelDep;

    private String numRuc;

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public RetRelDep getRetRelDep() {
        return retRelDep;
    }

    public void setRetRelDep(RetRelDep retRelDep) {
        this.retRelDep = retRelDep;
    }

    public String getNumRuc() {
        return numRuc;
    }

    public void setNumRuc(String numRuc) {
        this.numRuc = numRuc;
    }

    @Override
    public String toString() {
        return "ClassPojo [anio = " + anio + ", retRelDep = " + retRelDep + ", numRuc = " + numRuc + "]";
    }

    public class RetRelDep {

        private List<DatRetRelDep> datRetRelDep;

        public List<DatRetRelDep> getDatRetRelDep() {
            return datRetRelDep;
        }

        public void setDatRetRelDep(List<DatRetRelDep> datRetRelDep) {
            this.datRetRelDep = datRetRelDep;
        }

        @Override
        public String toString() {
            return "ClassPojo [datRetRelDep = " + datRetRelDep + "]";
        }
    }

    public class DatRetRelDep {

        private String decimCuar;

        private String deducEduca;

        private String partUtil;

        private String impRentEmpl;

        private String apoPerIess;

        private EmpleadoSRI empleado;

        private String decimTer;

        private String otrosIngRenGrav;

        private String basImp;

        private String deducAliement;

        private String suelSal;

        private String intGrabGen;

        private String deducVestim;

        private String valImpAsuEsteEmpl;

        private String valRetAsuOtrosEmpls;

        private String sobSuelComRemu;

        private String deducSalud;

        private String exoTerEd;

        private String aporPerIessConOtrosEmpls;

        private String sisSalNet;

        private String impRentCaus;

        private String deducVivienda;

        private String exoDiscap;

        private String salarioDigno;

        private String ingGravConEsteEmpl;

        private String valRet;

        private String fondoReserva;

        public String getDecimCuar() {
            return decimCuar;
        }

        public void setDecimCuar(String decimCuar) {
            this.decimCuar = decimCuar;
        }

        public String getDeducEduca() {
            return deducEduca;
        }

        public void setDeducEduca(String deducEduca) {
            this.deducEduca = deducEduca;
        }

        public String getPartUtil() {
            return partUtil;
        }

        public void setPartUtil(String partUtil) {
            this.partUtil = partUtil;
        }

        public String getImpRentEmpl() {
            return impRentEmpl;
        }

        public void setImpRentEmpl(String impRentEmpl) {
            this.impRentEmpl = impRentEmpl;
        }

        public String getApoPerIess() {
            return apoPerIess;
        }

        public void setApoPerIess(String apoPerIess) {
            this.apoPerIess = apoPerIess;
        }

        public EmpleadoSRI getEmpleado() {
            return empleado;
        }

        public void setEmpleado(EmpleadoSRI empleado) {
            this.empleado = empleado;
        }

        public String getDecimTer() {
            return decimTer;
        }

        public void setDecimTer(String decimTer) {
            this.decimTer = decimTer;
        }

        public String getOtrosIngRenGrav() {
            return otrosIngRenGrav;
        }

        public void setOtrosIngRenGrav(String otrosIngRenGrav) {
            this.otrosIngRenGrav = otrosIngRenGrav;
        }

        public String getBasImp() {
            return basImp;
        }

        public void setBasImp(String basImp) {
            this.basImp = basImp;
        }

        public String getDeducAliement() {
            return deducAliement;
        }

        public void setDeducAliement(String deducAliement) {
            this.deducAliement = deducAliement;
        }

        public String getSuelSal() {
            return suelSal;
        }

        public void setSuelSal(String suelSal) {
            this.suelSal = suelSal;
        }

        public String getIntGrabGen() {
            return intGrabGen;
        }

        public void setIntGrabGen(String intGrabGen) {
            this.intGrabGen = intGrabGen;
        }

        public String getDeducVestim() {
            return deducVestim;
        }

        public void setDeducVestim(String deducVestim) {
            this.deducVestim = deducVestim;
        }

        public String getValImpAsuEsteEmpl() {
            return valImpAsuEsteEmpl;
        }

        public void setValImpAsuEsteEmpl(String valImpAsuEsteEmpl) {
            this.valImpAsuEsteEmpl = valImpAsuEsteEmpl;
        }

        public String getValRetAsuOtrosEmpls() {
            return valRetAsuOtrosEmpls;
        }

        public void setValRetAsuOtrosEmpls(String valRetAsuOtrosEmpls) {
            this.valRetAsuOtrosEmpls = valRetAsuOtrosEmpls;
        }

        public String getSobSuelComRemu() {
            return sobSuelComRemu;
        }

        public void setSobSuelComRemu(String sobSuelComRemu) {
            this.sobSuelComRemu = sobSuelComRemu;
        }

        public String getDeducSalud() {
            return deducSalud;
        }

        public void setDeducSalud(String deducSalud) {
            this.deducSalud = deducSalud;
        }

        public String getExoTerEd() {
            return exoTerEd;
        }

        public void setExoTerEd(String exoTerEd) {
            this.exoTerEd = exoTerEd;
        }

        public String getAporPerIessConOtrosEmpls() {
            return aporPerIessConOtrosEmpls;
        }

        public void setAporPerIessConOtrosEmpls(String aporPerIessConOtrosEmpls) {
            this.aporPerIessConOtrosEmpls = aporPerIessConOtrosEmpls;
        }

        public String getSisSalNet() {
            return sisSalNet;
        }

        public void setSisSalNet(String sisSalNet) {
            this.sisSalNet = sisSalNet;
        }

        public String getImpRentCaus() {
            return impRentCaus;
        }

        public void setImpRentCaus(String impRentCaus) {
            this.impRentCaus = impRentCaus;
        }

        public String getDeducVivienda() {
            return deducVivienda;
        }

        public void setDeducVivienda(String deducVivienda) {
            this.deducVivienda = deducVivienda;
        }

        public String getExoDiscap() {
            return exoDiscap;
        }

        public void setExoDiscap(String exoDiscap) {
            this.exoDiscap = exoDiscap;
        }

        public String getSalarioDigno() {
            return salarioDigno;
        }

        public void setSalarioDigno(String salarioDigno) {
            this.salarioDigno = salarioDigno;
        }

        public String getIngGravConEsteEmpl() {
            return ingGravConEsteEmpl;
        }

        public void setIngGravConEsteEmpl(String ingGravConEsteEmpl) {
            this.ingGravConEsteEmpl = ingGravConEsteEmpl;
        }

        public String getValRet() {
            return valRet;
        }

        public void setValRet(String valRet) {
            this.valRet = valRet;
        }

        public String getFondoReserva() {
            return fondoReserva;
        }

        public void setFondoReserva(String fondoReserva) {
            this.fondoReserva = fondoReserva;
        }

        @Override
        public String toString() {
            return "ClassPojo [decimCuar = " + decimCuar + ", deducEduca = " + deducEduca + ", partUtil = " + partUtil + ", impRentEmpl = " + impRentEmpl + ", apoPerIess = " + apoPerIess + ", empleado = " + empleado + ", decimTer = " + decimTer + ", otrosIngRenGrav = " + otrosIngRenGrav + ", basImp = " + basImp + ", deducAliement = " + deducAliement + ", suelSal = " + suelSal + ", intGrabGen = " + intGrabGen + ", deducVestim = " + deducVestim + ", valImpAsuEsteEmpl = " + valImpAsuEsteEmpl + ", valRetAsuOtrosEmpls = " + valRetAsuOtrosEmpls + ", sobSuelComRemu = " + sobSuelComRemu + ", deducSalud = " + deducSalud + ", exoTerEd = " + exoTerEd + ", aporPerIessConOtrosEmpls = " + aporPerIessConOtrosEmpls + ", sisSalNet = " + sisSalNet + ", impRentCaus = " + impRentCaus + ", deducVivienda = " + deducVivienda + ", exoDiscap = " + exoDiscap + ", salarioDigno = " + salarioDigno + ", ingGravConEsteEmpl = " + ingGravConEsteEmpl + ", valRet = " + valRet + ", fondoReserva = " + fondoReserva + "]";
        }
    }

    public class EmpleadoSRI {

        private String paisResidencia;

        private String aplicaConvenio;

        private String apellidoTrab;

        private String residenciaTrab;

        private String tipoTrabajDiscap;

        private String idDiscap;

        private String tipIdRet;

        private String nombreTrab;

        private String idRet;

        private String tipIdDiscap;

        private String porcentajeDiscap;

        private String estab;

        public String getPaisResidencia() {
            return paisResidencia;
        }

        public void setPaisResidencia(String paisResidencia) {
            this.paisResidencia = paisResidencia;
        }

        public String getAplicaConvenio() {
            return aplicaConvenio;
        }

        public void setAplicaConvenio(String aplicaConvenio) {
            this.aplicaConvenio = aplicaConvenio;
        }

        public String getApellidoTrab() {
            return apellidoTrab;
        }

        public void setApellidoTrab(String apellidoTrab) {
            this.apellidoTrab = apellidoTrab;
        }

        public String getResidenciaTrab() {
            return residenciaTrab;
        }

        public void setResidenciaTrab(String residenciaTrab) {
            this.residenciaTrab = residenciaTrab;
        }

        public String getTipoTrabajDiscap() {
            return tipoTrabajDiscap;
        }

        public void setTipoTrabajDiscap(String tipoTrabajDiscap) {
            this.tipoTrabajDiscap = tipoTrabajDiscap;
        }

        public String getIdDiscap() {
            return idDiscap;
        }

        public void setIdDiscap(String idDiscap) {
            this.idDiscap = idDiscap;
        }

        public String getTipIdRet() {
            return tipIdRet;
        }

        public void setTipIdRet(String tipIdRet) {
            this.tipIdRet = tipIdRet;
        }

        public String getNombreTrab() {
            return nombreTrab;
        }

        public void setNombreTrab(String nombreTrab) {
            this.nombreTrab = nombreTrab;
        }

        public String getIdRet() {
            return idRet;
        }

        public void setIdRet(String idRet) {
            this.idRet = idRet;
        }

        public String getTipIdDiscap() {
            return tipIdDiscap;
        }

        public void setTipIdDiscap(String tipIdDiscap) {
            this.tipIdDiscap = tipIdDiscap;
        }

        public String getPorcentajeDiscap() {
            return porcentajeDiscap;
        }

        public void setPorcentajeDiscap(String porcentajeDiscap) {
            this.porcentajeDiscap = porcentajeDiscap;
        }

        public String getEstab() {
            return estab;
        }

        public void setEstab(String estab) {
            this.estab = estab;
        }

        @Override
        public String toString() {
            return "ClassPojo [paisResidencia = " + paisResidencia + ", aplicaConvenio = " + aplicaConvenio + ", apellidoTrab = " + apellidoTrab + ", residenciaTrab = " + residenciaTrab + ", tipoTrabajDiscap = " + tipoTrabajDiscap + ", idDiscap = " + idDiscap + ", tipIdRet = " + tipIdRet + ", nombreTrab = " + nombreTrab + ", idRet = " + idRet + ", tipIdDiscap = " + tipIdDiscap + ", porcentajeDiscap = " + porcentajeDiscap + ", estab = " + estab + "]";
        }
    }

}
