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
public class StdEdiObjHZC {

    private String TypPolskichLiter = "LA";
    private String TypDok;
    private String NazwaHZC;
    private String Data;
    private String DataDo;
    private String DataOdCzas;
    private String Data3;
    private String Data3OdCzas;
    private String SklepyHZC;
    private String NazwaWystawcy;
    private String AdresWystawcy;
    private String KodWystawcy;
    private String MiastoWystawcy;
    private String UlicaWystawcy;
    private String NIPWystawcy;
    private String BankWystawcy;
    private String KontoWystawcy;
    private String TelefonWystawcy;
    private String NrWystawcyWSieciSklepow;
    private String WystawcaToCentralaSieci;
    private String NrWystawcyObcyWSieciSklepow;
    private List<StdEdiObjLine> ediList = new ArrayList<>();
    

    public StdEdiObjHZC() {
        
        
        
        this.SklepyHZC = "0";
        this.NazwaWystawcy = "";
        this.AdresWystawcy = "";
        this.KodWystawcy = "";
        this.MiastoWystawcy = "";
        this.UlicaWystawcy = "";
        this.NIPWystawcy = "";
        this.BankWystawcy = "";
        this.KontoWystawcy = "";
        this.TelefonWystawcy = "";
        this.NrWystawcyWSieciSklepow = "";
        this.WystawcaToCentralaSieci = "";
        this.NrWystawcyObcyWSieciSklepow = "";
    }

    public String getNag() {
        StringBuilder str_ = new StringBuilder();

        str_.append("TypPolskichLiter:").append(this.TypPolskichLiter).append("\n");
        str_.append("TypDok:").append(this.TypDok).append("\n");
        str_.append("NazwaHZC:").append(this.NazwaHZC).append("\n");
        str_.append("Data:").append(this.Data).append("\n");
        str_.append("DataDo:").append(this.DataDo).append("\n");
        str_.append("DataOdCzas").append(this.DataOdCzas).append("\n");
        str_.append("Data3").append(this.Data3).append("\n");
        str_.append("Data3OdCzas:").append(this.Data3OdCzas).append("\n");
        str_.append("SklepyHZC:").append(this.SklepyHZC).append("\n");
        str_.append("NazwaWystawcy:").append(this.NazwaWystawcy).append("\n");
        str_.append("AdresWystawcy:").append(this.AdresWystawcy).append("\n");
        str_.append("KodWystawcy:").append(this.KodWystawcy).append("\n");
        str_.append("MiastoWystawcy:").append(this.MiastoWystawcy).append("\n");
        str_.append("UlicaWystawcy:").append(this.UlicaWystawcy).append("\n");
        str_.append("NIPWystawcy:").append(this.NIPWystawcy).append("\n");
        str_.append("BankWystawcy:").append(this.BankWystawcy).append("\n");
        str_.append("KontoWystawcy:").append(this.KontoWystawcy).append("\n");
        str_.append("TelefonWystawcy:").append(this.TelefonWystawcy).append("\n");
        str_.append("NrWystawcyWSieciSklepow:").append(this.NrWystawcyWSieciSklepow).append("\n");
        str_.append("WystawcaToCentralaSieci:").append(this.WystawcaToCentralaSieci).append("\n");
        str_.append("NrWystawcyObcyWSieciSklepow:").append(this.NrWystawcyObcyWSieciSklepow).append("\n");

        return str_.toString();
    }

 

    public List<StdEdiObjLine> getEdiList() {
        return ediList;
    }

    public void setEdiList(List<StdEdiObjLine> ediList) {
        this.ediList = ediList;
    }

    public void setTypPolskichLiter(String TypPolskichLiter) {
        this.TypPolskichLiter = TypPolskichLiter;
    }

    public void setTypDok(String TypDok) {
        this.TypDok = TypDok;
    }

    public void setNazwaHZC(String NazwaHZC) {
        this.NazwaHZC = NazwaHZC;
    }

    public void setDataDo(String DataDo) {
        this.DataDo = DataDo;
    }

    public void setDataOdCzas(String DataOdCzas) {
        this.DataOdCzas = DataOdCzas;
    }

    public void setData3(String Data3) {
        this.Data3 = Data3;
    }

    public void setData3OdCzas(String Data3OdCzas) {
        this.Data3OdCzas = Data3OdCzas;
    }

    public void setSklepyHZC(String SklepyHZC) {
        this.SklepyHZC = SklepyHZC;
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

    public void setNrWystawcyWSieciSklepow(String NrWystawcyWSieciSklepow) {
        this.NrWystawcyWSieciSklepow = NrWystawcyWSieciSklepow;
    }

    public void setWystawcaToCentralaSieci(String WystawcaToCentralaSieci) {
        this.WystawcaToCentralaSieci = WystawcaToCentralaSieci;
    }

    public void setNrWystawcyObcyWSieciSklepow(String NrWystawcyObcyWSieciSklepow) {
        this.NrWystawcyObcyWSieciSklepow = NrWystawcyObcyWSieciSklepow;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    
}
