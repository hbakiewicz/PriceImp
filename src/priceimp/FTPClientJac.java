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
import eventlog.IEventJournal;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPClientJac {

    FTPClient ftp = null;
    private IEventJournal log = AppMain.log;
    //private String liv_locat, liv_out,liv_header;

    public FTPClientJac() throws Exception {
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter("ftp.log")));
        int reply;

        ftp.connect(AppMain.ftpServer);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftp.login(AppMain.ftpUser, AppMain.ftpPass);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    public void uploadFile(String localFileFullName, String fileName, String hostDir)
            throws Exception {
        try (InputStream input = new FileInputStream(new File(localFileFullName))) {
            this.ftp.storeFile(hostDir + fileName, input);
        }
    }

    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                log.logEvent(Level.SEVERE, "błąd podczas rozłaczania ", f);
            }
        }
    }

    public void dwonloadFile() throws Exception {
        // lists files and directories in the current working directory
        FTPFile[] files = ftp.listFiles();
        log.logEvent(Level.INFO, "pobieram listę plików z serwera  ");
// iterates over the files and prints details for each
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (FTPFile file : files) {
            String details = file.getName();
            if (file.isDirectory()) {
                details = "[" + details + "]";
            }
            details += "\t\t" + file.getSize();
            //details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());
            log.logEvent(Level.INFO, details);
            String remoteFile1 = file.getName();
            if (!file.isDirectory()) {
                File downloadFile1 = new File(AppMain.liv_location + "\\" + remoteFile1);
                boolean success;
                try (OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1))) {
                    success = ftp.retrieveFile(remoteFile1, outputStream1);
                }
                if (success) {
                    log.logEvent(Level.INFO, "Pobrano poprawnie plik : " + file.getName());
                    if (file.getName().contains("Liv")) {
                        log.logEvent(Level.INFO, "znalazłem dostawę, przeliczam  " + file.getName());
                        calcLiv(file.getName());
                    }
                    log.logEvent(Level.INFO, "Sprawdzam czy plik nie jest sprzedażą lub walidacją dostawy: " + file.getName());
                    if (!file.getName().toUpperCase().contains("Ven") || !file.getName().toUpperCase().contains("Val") ) {
                        if (ftp.deleteFile(file.getName())) {

                            log.logEvent(Level.INFO, "Skasowano plik  : " + file.getName());
                        } else {
                            log.logEvent(Level.INFO, "Nie skasowano pliku : " + file.getName());
                        }
                    } else {
                        log.logEvent(Level.INFO, "Plik sprzedaży, nie kasuję : " + file.getName());
                    }
                } else {
                    log.logEvent(Level.INFO, "bład pobierania pliku : " + file.getName());
                }

                System.out.println(details);
            }
        }
        ftp.logout();
        ftp.disconnect();

    }

    private boolean calcLiv(String filename) {
        String converter = AppMain.liv_header.replace("##Nrdok@@", filename);
        converter = converter.replace(".dat", ".txt");
        Integer ii = 0;
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(AppMain.liv_location + "\\" + filename))) {
                String line;
                while ((line = br.readLine()) != null) {
                    //'Linia:Kod{'+kod+'}Ilosc{'+ilosc+'}'
                    converter = converter + "Linia:Kod{" + line.substring(34, 47) + "}Ilosc{" + line.substring(48, 52) + "}\n";
                    ii++;
                }
            }
            converter = converter.replace("##LineCount$$", Integer.toString(ii));
            try (PrintWriter out = new PrintWriter(AppMain.liv_output + "\\" + filename.replace("dat", "txt"))) {
                out.println(converter);
                out.flush();
            }

        } catch (IOException e) {
            log.logEvent(Level.INFO, "bład na pliku " + filename, e);
        }
        return true;
    }

    public void UploadFile(String localFileFullName, String fileName, String hostDir) throws Exception {
        // lists files and directories in the current working directory

        log.logEvent(Level.INFO, "wysyłam plik " + fileName + " na serwer z " + localFileFullName + ", zdalny katalog " + hostDir);
        // iterates over the files and prints details for each
        //DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (InputStream input = new FileInputStream(new File(localFileFullName))) {
            this.ftp.storeFile(hostDir + fileName, input);
        }
        log.logEvent(Level.INFO, "wysyłano  plik ");
        ftp.logout();
        ftp.disconnect();

    }

}
