/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

/**
 *
 * @author hbakiewicz
 */
/*
Linia:Nazwa{8in1 Black Pearl Shampoo 250 ml}Kod{4048422101659}Vat{23}Jm{szt}Asortyment{Domylny}Sww{1466}PKWiU{T101659}Ilosc{1}Cena{n45.00}Wartosc{n45.00}IleWOpak{1}CenaSp{b55.35}TowId{2}
 */
public class StdEdiObjLine {

    private final String Nazwa;
    private final String Kod;
    private String Vat;
    private String JM;
    private String Asort;
    private String Sww;
    private String PKWiU;
    private final Double ilosc;
    private String CenaN;
    private String CenaB;
    private String WartoscN;
    private String WartoscB;
    private String IleWOpak;
    private String CenaSpN;
    private String CenaSpB;

    public StdEdiObjLine(String Nazwa, String Kod, Double ilosc) {
        this.Nazwa = Nazwa;
        this.Kod = Kod;
        this.ilosc = ilosc;
        Vat = "";
        JM = "";
        Asort = "";
        Sww = "";
        PKWiU = "";
        CenaN = "0.00";
        CenaB = "0.00";
        WartoscN = "0.00";
        WartoscB = "0.00";
        IleWOpak = "0.00";
        CenaSpN = "0.00";
        CenaSpB = "0.00";

    }

    public String getVat() {
        return Vat;
    }

    public void setVat(String Vat) {
        this.Vat = Vat;
    }

    public String getJM() {
        return JM;
    }

    public void setJM(String JM) {
        this.JM = JM;
    }

    public String getAsort() {
        return Asort;
    }

    public void setAsort(String Asort) {
        this.Asort = Asort;
    }

    public String getSww() {
        return Sww;
    }

    public void setSww(String Sww) {
        this.Sww = Sww;
    }

    public String getPKWiU() {
        return PKWiU;
    }

    public void setPKWiU(String PKWiU) {
        this.PKWiU = PKWiU;
    }

    public String getCenaN() {
        return CenaN;
    }

    public void setCenaN(String CenaN) {
        this.CenaN = CenaN;
    }

    public String getCenaB() {
        return CenaB;
    }

    public void setCenaB(String CenaB) {
        this.CenaB = CenaB;
    }

    public String getWartoscN() {
        return WartoscN;
    }

    public void setWartoscN(String WartoscN) {
        this.WartoscN = WartoscN;
    }

    public String getWartoscB() {
        return WartoscB;
    }

    public void setWartoscB(String WartoscB) {
        this.WartoscB = WartoscB;
    }

    public String getIleWOpak() {
        return IleWOpak;
    }

    public void setIleWOpak(String IleWOpak) {
        this.IleWOpak = IleWOpak;
    }

    public String getCenaSpN() {
        return CenaSpN;
    }

    public void setCenaSpN(String CenaSpN) {
        this.CenaSpN = CenaSpN;
    }

    public String getCenaSpB() {
        return CenaSpB;
    }

    public void setCenaSpB(String CenaSpB) {
        this.CenaSpB = CenaSpB;
    }

    public String getNazwa() {
        return Nazwa;
    }

    public String getKod() {
        return Kod;
    }

    public Double getIlosc() {
        
        return ilosc;
    }

}
