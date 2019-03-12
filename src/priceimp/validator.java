/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import eventlog.IEventJournal;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author hbakiewicz
 */
public class validator {

    dbManager dbm;
    String store_code, liv_path, liv_output, current_path;
    IEventJournal log;
    Dok _dok;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public String edi_date = sdf.format(new Date());

    public validator(dbManager dbm, String store_code, String liv_path, String liv_output, IEventJournal log, String _currentPath) {
        this.dbm = dbm;
        this.store_code = store_code;
        this.liv_path = liv_path;
        this.liv_output = liv_output;
        this.log = log;
        this.current_path = _currentPath;
        try {
            this._dok = this.dbm.getValDoc();
            if (this._dok.dokName.equals("@Err")) {
                log.logEvent(Level.INFO, "Tekst dokumentu nie zawiera nazyw dokumentu , DokId : " + this._dok.getDokid());

                if (!_dok.getDokid().equals("Empty")) {
                    log.logEvent(Level.INFO, "Oznaczam w bazie dokument jako zwalidowany: " + _dok.getDokid());
                    this.dbm.markAsValidated(_dok.getDokid());
                } else {
                    log.logEvent(Level.INFO, "Brak dokumentów do walidacji  , DokId : " + this._dok.getDokid());
                }
                //System.exit(0);
            }
            val_doc();
        } catch (SQLException | UnsupportedEncodingException ex) {
            log.logEvent(Level.INFO, "Błąd podczas odczytu z bazy ", ex);
        }
    }

    private void val_doc() throws UnsupportedEncodingException {
        String lines = "";
        int linnum =1;
        log.logEvent(Level.INFO, "Start metody do validacji val_doc :" + _dok.getDokName());
        try {
            String ft = liv_path + "\\" + _dok.getDokName() + ".dat";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(ft), "Windows-1250"));
            log.logEvent(Level.FINER, "odczytano plik " + ft);

            String line = br.readLine();
            while (line != null) {
                //String eee = getEan(line);
                lines = lines + store_code + getPercel(line) + getEan(line) + forQ(checkQua(this._dok.getPozdok(), getQua(line), getEan(line),linnum)) + edi_date + "\n";
                line = br.readLine();
                linnum++;
            }
            String sav = liv_output + "\\Val_" + edi_date + ".dat";
            log.logEvent(Level.INFO, "zapisano plik : " + sav);
            saveString(sav, lines);

            sav = current_path + "\\Val_" + edi_date + ".dat";
            log.logEvent(Level.INFO, "zapisano plik : " + sav);
            saveString(sav, lines);
            log.logEvent(Level.INFO, "Oznaczam w bazie dokument jako zwalidowany: " + _dok.getDokid());
            this.dbm.markAsValidated(_dok.getDokid());
       
           

            
            
           // System.exit(0);

        } catch (FileNotFoundException ex) {
            log.logEvent(Level.INFO, "Brak pliku  :" + liv_path, ex);
            //System.exit(1);
        } catch (IOException ex) {
            log.logEvent(Level.INFO, "I/O Exception  ", ex);
            //System.exit(1);
        } catch (SQLException ex) {
            log.logEvent(Level.INFO, "SQLException   ", ex);
            //System.exit(1);

        } catch (NullPointerException ee) {
            log.logEvent(Level.INFO, "NullPointerException   ", ee);
            //System.exit(1);
        }
    }

    private String getPercel(String s) {

        return s.substring(6, 26);
    }

    private String getEan(String s) {
        return s.substring(34, 47);
    }

    private String getQua(String s) {
        return s.substring(47, 52);
    }

    public void saveString(String path_loc, String text) throws IOException {
        Path path = Paths.get(path_loc);
        Charset charset = StandardCharsets.UTF_8;

        Files.write(path, text.getBytes(charset));
    }

    private String checkQua(List<PozDok> li, String ilosc, String kod,int lineNumber) {
        int counter = 0;
       // if (kod.equals("3603651605869")) {
       // log.logEvent(Level.FINER, "Returned : ");
       // }
        log.logEvent(Level.FINER, "Sprawdzam pozycję o kodzie: " + kod + "; ilość wejscie" + ilosc+ "; ilość returned:" + ilosc + " ; LineNumber: "+lineNumber);
        for (PozDok pozDok : li) {
            counter++;
            //System.out.println("Kod plik :"+kod+" Kod baza: " + pozDok.getKod());
            //dodałem counter aby wybierać linie z dokumentu a nie pierwszą która zostanie napotkana, problem występował jak towar było w kilku liniach 
            //jak będzie dalej problem trzeba będzie dodać jakąś agregację kodów kreskowych z pliku i z bazy 
            if (pozDok.getKod().equals(kod) & lineNumber <= counter ) {
                log.logEvent(Level.FINER, "Returned : " + pozDok.getIlosc() + " counter "+counter);
                return pozDok.getIlosc();
            }

        }
        log.logEvent(Level.FINER, "brak takiej pozycji w dokumencie  : " + kod + "; ilość " + ilosc);
        return "0.0";
    }

    private String forQ(String s) {

        String[] split = s.split("\\.");
        return addZe(split[0], 5);

    }

    private String addZe(String in, int cout) {
        while (in.length() <= cout - 1) {
            in = "0" + in;
        }
        return in;
    }
}
