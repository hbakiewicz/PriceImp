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
public class PozDok {
    String kod,ilosc;

    public PozDok(String kod, String ilosc) {
        this.kod = kod;
        this.ilosc = ilosc;
    }

    public String getKod() {
        return kod;
    }

    public String getIlosc() {
        return ilosc;
    }
    
    
}
