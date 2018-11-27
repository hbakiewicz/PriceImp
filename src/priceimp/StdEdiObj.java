/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hbakiewicz
 */
public class StdEdiObj {

    private String TypPolskichLiter = "LA";
    private String TypDok;
    private String NrDok;
    private String Data;
    private String Magazyn;
    private String SposobPlatn;
    private String TerminPlatn;
    private String IndeksCentralny = "NIE";
    private String NazwaWystawcy;
    private String AdresWystawcy;
    private String KodWystawcy;
    private String MiastoWystawcy;
    private String UlicaWystawcy;
    private String NIPWystawcy;
    private String BankWystawcy;
    private String KontoWystawcy;
    private String TelefonWystawcy;
    private String NrWystawcyWSieciSklepow = "0";
    private String WystawcaToCentralaSieci = "0";
    private String NrWystawcyObcyWSieciSklepow;
    private String NazwaOdbiorcy;
    private String AdresOdbiorcy;
    private String KodOdbiorcy;
    private String MiastoOdbiorcy;
    private String UlicaOdbiorcy;
    private String NIPOdbiorcy;
    private String BankOdbiorcy;
    private String KontoOdbiorcy;
    private String TelefonOdbiorcy;
    private String NrOdbiorcyWSieciSklepow = "0";
    private String OdbiorcaToCentralaSieci = "0";
    private String NrOdbiorcyObcyWSieciSklepowH;
    private List<StdEdiObjLine> ediList = new ArrayList<>();

    public StdEdiObj() {
        TypPolskichLiter = "LA";
        TypDok = "";
        NrDok = "";
        Data = "";
        Magazyn = "";
        SposobPlatn = "";
        TerminPlatn = "";
        IndeksCentralny = "NIE";
        NazwaWystawcy = "";
        AdresWystawcy = "";
        KodWystawcy = "";
        MiastoWystawcy = "";
        UlicaWystawcy = "";
        NIPWystawcy = "";
        BankWystawcy = "";
        KontoWystawcy = "";
        TelefonWystawcy = "";
        NrWystawcyWSieciSklepow = "0";
        WystawcaToCentralaSieci = "0";
        NrWystawcyObcyWSieciSklepow = "";
        NazwaOdbiorcy = "";
        AdresOdbiorcy = "";
        KodOdbiorcy = "";
        MiastoOdbiorcy = "";
        UlicaOdbiorcy = "";
        NIPOdbiorcy = "";
        BankOdbiorcy = "";
        KontoOdbiorcy = "";
        TelefonOdbiorcy = "";
        NrOdbiorcyWSieciSklepow = "0";
        OdbiorcaToCentralaSieci = "0";
        NrOdbiorcyObcyWSieciSklepowH = "";

    }

    public List<StdEdiObjLine> getEdiList() {
        return ediList;
    }

    public void setEdiList(List<StdEdiObjLine> ediList) {
        this.ediList = ediList;
    }

    public void setTypDok(String TypDok) {
        this.TypDok = TypDok;
    }

    public void setNrDok(String NrDok) {
        this.NrDok = NrDok;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    public void setMagazyn(String Magazyn) {
        this.Magazyn = Magazyn;
    }

    public void setSposobPlatn(String SposobPlatn) {
        this.SposobPlatn = SposobPlatn;
    }

    public void setTerminPlatn(String TerminPlatn) {
        this.TerminPlatn = TerminPlatn;
    }

    public void setNazwaWystawcy(String NazwaWystawcy) {
        this.NazwaWystawcy = NazwaWystawcy;
    }

    public void setAdresWystawcy(String AdresWystawcy) {
        this.AdresWystawcy = AdresWystawcy;
    }

    public void setKodWystawcy(String KodWystawcy) {
        this.KodWystawcy = KodWystawcy;
    }

    public void setMiastoWystawcy(String MiastoWystawcy) {
        this.MiastoWystawcy = MiastoWystawcy;
    }

    public void setUlicaWystawcy(String UlicaWystawcy) {
        this.UlicaWystawcy = UlicaWystawcy;
    }

    public void setNIPWystawcy(String NIPWystawcy) {
        this.NIPWystawcy = NIPWystawcy;
    }

    public void setBankWystawcy(String BankWystawcy) {
        this.BankWystawcy = BankWystawcy;
    }

    public void setKontoWystawcy(String KontoWystawcy) {
        this.KontoWystawcy = KontoWystawcy;
    }

    public void setTelefonWystawcy(String TelefonWystawcy) {
        this.TelefonWystawcy = TelefonWystawcy;
    }

    public void setNrWystawcyObcyWSieciSklepow(String NrWystawcyObcyWSieciSklepow) {
        this.NrWystawcyObcyWSieciSklepow = NrWystawcyObcyWSieciSklepow;
    }

    public void setNazwaOdbiorcy(String NazwaOdbiorcy) {
        this.NazwaOdbiorcy = NazwaOdbiorcy;
    }

    public void setAdresOdbiorcy(String AdresOdbiorcy) {
        this.AdresOdbiorcy = AdresOdbiorcy;
    }

    public void setKodOdbiorcy(String KodOdbiorcy) {
        this.KodOdbiorcy = KodOdbiorcy;
    }

    public void setMiastoOdbiorcy(String MiastoOdbiorcy) {
        this.MiastoOdbiorcy = MiastoOdbiorcy;
    }

    public void setUlicaOdbiorcy(String UlicaOdbiorcy) {
        this.UlicaOdbiorcy = UlicaOdbiorcy;
    }

    public void setNIPOdbiorcy(String NIPOdbiorcy) {
        this.NIPOdbiorcy = NIPOdbiorcy;
    }

    public void setBankOdbiorcy(String BankOdbiorcy) {
        this.BankOdbiorcy = BankOdbiorcy;
    }

    public void setKontoOdbiorcy(String KontoOdbiorcy) {
        this.KontoOdbiorcy = KontoOdbiorcy;
    }

    public void setTelefonOdbiorcy(String TelefonOdbiorcy) {
        this.TelefonOdbiorcy = TelefonOdbiorcy;
    }

    public void setNrOdbiorcyObcyWSieciSklepowH(String NrOdbiorcyObcyWSieciSklepowH) {
        this.NrOdbiorcyObcyWSieciSklepowH = NrOdbiorcyObcyWSieciSklepowH;
    }

    public String getTypPolskichLiter() {
        return TypPolskichLiter;
    }

    public String getTypDok() {
        return TypDok;
    }

    public String getNrDok() {
        return NrDok;
    }

    public String getData() {
        return Data;
    }

    public String getMagazyn() {
        return Magazyn;
    }

    public String getSposobPlatn() {
        return SposobPlatn;
    }

    public String getTerminPlatn() {
        return TerminPlatn;
    }

    public String getIndeksCentralny() {
        return IndeksCentralny;
    }

    public String getNazwaWystawcy() {
        return NazwaWystawcy;
    }

    public String getAdresWystawcy() {
        return AdresWystawcy;
    }

    public String getKodWystawcy() {
        return KodWystawcy;
    }

    public String getMiastoWystawcy() {
        return MiastoWystawcy;
    }

    public String getUlicaWystawcy() {
        return UlicaWystawcy;
    }

    public String getNIPWystawcy() {
        return NIPWystawcy;
    }

    public String getBankWystawcy() {
        return BankWystawcy;
    }

    public String getKontoWystawcy() {
        return KontoWystawcy;
    }

    public String getTelefonWystawcy() {
        return TelefonWystawcy;
    }

    public String getNrWystawcyWSieciSklepow() {
        return NrWystawcyWSieciSklepow;
    }

    public String getWystawcaToCentralaSieci() {
        return WystawcaToCentralaSieci;
    }

    public String getNrWystawcyObcyWSieciSklepow() {
        return NrWystawcyObcyWSieciSklepow;
    }

    public String getNazwaOdbiorcy() {
        return NazwaOdbiorcy;
    }

    public String getAdresOdbiorcy() {
        return AdresOdbiorcy;
    }

    public String getKodOdbiorcy() {
        return KodOdbiorcy;
    }

    public String getMiastoOdbiorcy() {
        return MiastoOdbiorcy;
    }

    public String getUlicaOdbiorcy() {
        return UlicaOdbiorcy;
    }

    public String getNIPOdbiorcy() {
        return NIPOdbiorcy;
    }

    public String getBankOdbiorcy() {
        return BankOdbiorcy;
    }

    public String getKontoOdbiorcy() {
        return KontoOdbiorcy;
    }

    public String getTelefonOdbiorcy() {
        return TelefonOdbiorcy;
    }

    public String getNrOdbiorcyWSieciSklepow() {
        return NrOdbiorcyWSieciSklepow;
    }

    public String getOdbiorcaToCentralaSieci() {
        return OdbiorcaToCentralaSieci;
    }

    public String getNrOdbiorcyObcyWSieciSklepowH() {
        return NrOdbiorcyObcyWSieciSklepowH;
    }

}
