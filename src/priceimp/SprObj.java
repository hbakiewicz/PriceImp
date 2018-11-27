/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

/**
 *
 * @author HUBERTBAKIEWICZ
 */
public class SprObj {

    /*
  date_plik+
                fnDodZero(Q.FieldByName('kod').AsString,13)+
                fnDodZero(Q.FieldByName('Receipt').AsString,10)+
                fnDodZero(Q.FieldByName('Ilosc').AsString,5)+
                fnDodZero(fnPrCe(Q.FieldByName('CenaD').AsString),9)+
                fnDodZero(Q.FieldByName('KasId').AsString,1));
    
     */
    String kod, Receipt, ilosc, CenaId, KasaID;

    public SprObj(String kod, String Receipt, String ilosc, String CenaId, String KasaID) {
        this.kod = addZero(kod, 13);
        this.Receipt = addZero(Receipt, 10);
        if (ilosc.contains("-")) {
            ilosc = ilosc.replace("-", "");
            this.ilosc = "-" + addZero(calcIlosc(ilosc), 4);
        } else {
            this.ilosc = addZero(calcIlosc(ilosc), 5);
        }

        this.CenaId = addZero(calcCena(CenaId), 9);
        this.KasaID = addZero(KasaID, 1);
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = addZero(kod, 13);
    }

    public String getReceipt() {
        return Receipt;
    }

    public void setReceipt(String Receipt) {
        this.Receipt = addZero(Receipt, 10);
    }

    public String getIlosc() {
        return ilosc;
    }

    public void setIlosc(String ilosc) {
        this.ilosc = addZero(ilosc, 5);
    }

    public String getCenaId() {
        return CenaId;
    }

    public void setCenaId(String CenaId) {
        this.CenaId = addZero(CenaId, 9);
    }

    public String getKasaID() {
        return KasaID;
    }

    public void setKasaID(String KasaID) {
        this.KasaID = addZero(KasaID, 1);
    }

    private String addZero(String txt, Integer cunt) {

        while (txt.length() < cunt) {
            txt = "0" + txt;
        }

        return txt;
    }

    private String calcCena(String txt) {

        String[] t = txt.split("\\.");

        return t[0] + t[1].substring(0, 2);
    }

    private String calcIlosc(String txt) {

        String[] t = txt.split("\\.");

        return t[0];
    }

}
