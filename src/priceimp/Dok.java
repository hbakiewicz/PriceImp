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
public class Dok {
    String dokName,Dokid;
    List<PozDok> pozdok;

    public Dok(String dokName, String Dokid) {
        this.dokName = dokName;
        this.Dokid = Dokid;
        pozdok = new ArrayList<>();
    }

    public String getDokName() {
        return dokName;
    }

    public String getDokid() {
        return Dokid;
    }

    public List<PozDok> getPozdok() {
        return pozdok;
    }

    public void setPozdok(List<PozDok> pozdok) {
        this.pozdok = pozdok;
    }

    public void setDokName(String dokName) {
        this.dokName = dokName;
    }
    
    
}
