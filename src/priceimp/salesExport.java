/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hbakiewicz
 */
public class salesExport {

    dbManager dbm;
    String store_code, liv_path, liv_output, current_path, exportDate;
    //IEventJournal log;
    List<SprObj> spr = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    public String edi_date = sdf.format(new Date());
    public String shortDate;

    public salesExport(String _exportDate) {
        String expFile = "";
        this.exportDate = _exportDate;
        if (exportDate.length() == 0) {
            exportDate = edi_date;

        }
        shortDate = exportDate.replace("-", "");
        try {
            this.spr = AppMain.dbm.getSale(exportDate);
            for (SprObj sprObj : spr) {
                System.out.println(sprObj.getIlosc());
                expFile = expFile + AppMain.store_id
                        + shortDate
                        + sprObj.getKod()
                        + sprObj.getReceipt()
                        + sprObj.getIlosc()
                        + sprObj.getCenaId()
                        + sprObj.getKasaID() + "\n";

            }
            try (PrintWriter out = new PrintWriter(AppMain.ven_out + "\\Ven_" + shortDate + ".dat")) {
                out.print(expFile);
                out.flush();

                if (expFile.length() > 1) {
                    FTPClientJac ftpserev = new FTPClientJac();
                    ftpserev.UploadFile(AppMain.ven_out + "\\Ven_" + shortDate + ".dat", "Ven_" + shortDate + ".dat", "/");
                } else {
                    AppMain.log.logEvent(Level.INFO, "Pusty plik nie wysyałm na ftp  ");
                }
            } catch (Exception ex) {
                AppMain.log.logEvent(Level.INFO, "Błąd podczas wysłania pliku  ", ex);

            }
        } catch (SQLException ex) {
            AppMain.log.logEvent(Level.INFO, "Błąd podczas odczytu z bazy ", ex);
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

    private String checkQua(List<PozDok> li, String ilosc, String kod, int lineNumber) {
        int counter = 0;
        // if (kod.equals("3603651605869")) {
        // log.logEvent(Level.FINER, "Returned : ");
        // }
        AppMain.log.logEvent(Level.FINER, "Sprawdzam pozycję o kodzie: " + kod + "; ilość wejscie" + ilosc + "; ilość returned:" + ilosc + " ; LineNumber: " + lineNumber);
        for (PozDok pozDok : li) {
            counter++;
            //System.out.println("Kod plik :"+kod+" Kod baza: " + pozDok.getKod());
            //dodałem counter aby wybierać linie z dokumentu a nie pierwszą która zostanie napotkana, problem występował jak towar było w kilku liniach 
            //jak będzie dalej problem trzeba będzie dodać jakąś agregację kodów kreskowych z pliku i z bazy 
            if (pozDok.getKod().equals(kod) & lineNumber <= counter) {
                AppMain.log.logEvent(Level.FINER, "Returned : " + pozDok.getIlosc() + " counter " + counter);
                return pozDok.getIlosc();
            }

        }
        AppMain.log.logEvent(Level.FINER, "brak takiej pozycji w dokumencie  : " + kod + "; ilość " + ilosc);
        return null;
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
