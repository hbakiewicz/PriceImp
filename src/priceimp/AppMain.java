/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import eventlog.EEventLogException;
import eventlog.EventJournalFactory;
import eventlog.IEventJournal;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.xml.sax.SAXException;

/**
 *
 * @author hbakiewicz
 */
public class AppMain extends javax.swing.JFrame {

    public static IEventJournal log;
    public static String version = "PriceImp - 0.5";
    public String current_path;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public String edi_date = sdf.format(new Date());
    public static String dbname, dbuser, dbpassword, dbport, dbconnectstring, log_lvl, przel, edi_pref, store_code, liv_location, liv_output,inv_output,
            ftpServer, ftpPass, ftpUser, ven_out, liv_header, store_id, ven_location;
    public String output_path;
    public String invetn_output;
    public static dbManager dbm;
    public File[] listOfFiles;

    public AppMain(String[] aar) throws Exception {

        initComponents();

        Path currentRelativePath = Paths.get("");
        current_path = currentRelativePath.toAbsolutePath().toString();

        Properties prop = new Properties();

        try {

            InputStream input = new FileInputStream("config.properties");

            prop.load(input);
            dbname = prop.getProperty("dbname", "pcmarket");
            dbconnectstring = prop.getProperty("dbconnectstring", "jdbc:sqlserver://localhost");
            dbuser = prop.getProperty("dbuser", "sa");
            dbpassword = prop.getProperty("dbpassword", "8100736");
            dbport = prop.getProperty("dbport", "1433");
            log_lvl = prop.getProperty("log_level", "INFO");
            przel = prop.getProperty("przelicznik", "4").replace(",", ".");
            edi_pref = prop.getProperty("edi_prefix", "Doc");
            store_code = prop.getProperty("store_id", "001351");
            liv_location = prop.getProperty("liv_location", "c:\\");
            liv_output = prop.getProperty("liv_output", "c:\\");
            invetn_output = prop.getProperty("out_inwent", "c:\\");
            ftpServer = prop.getProperty("ftpServer", "ftp01.idgroup.com");
            ftpPass = prop.getProperty("ftpPass", "");
            ftpUser = prop.getProperty("ftpUser", "");
            ven_out = prop.getProperty("ven_out", "c:\\");
            liv_header = prop.getProperty("liv_header", "brak");
            store_id = prop.getProperty("store_id", "001351");
            inv_output = prop.getProperty("inv_output", "c:\\");
            //ven_location = prop.getProperty("ven_location", "c:\\");

            log = EventJournalFactory.createEventJournal("T", current_path + "\\prcimp_log", 2000000, 64, Level.parse(log_lvl));
            dbm = new dbManager(dbconnectstring, dbuser, dbpassword, dbname);
            File folder = new File(current_path);
            listOfFiles = folder.listFiles();
        } catch (EEventLogException | FileNotFoundException | NullPointerException ex) {
            System.out.println("brak pliku config.properties");
            System.exit(1);

        } catch (IOException ex) {
            System.out.println("brak pliku config.properties");
            System.exit(1);
        }
        log.logEvent(Level.INFO, "Current relative path is: " + current_path);
        log.logEvent(Level.INFO, "wersja programu  " + version);

        System.out.println(aar.length);
        boolean exit_ = false;

        for (String aar1 : aar) {
            try {
                if (aar1.contains("v")) {
                    log.logEvent(Level.INFO, "program uruchumiono z opcją 'v'");
                    validator validator = new validator(dbm, store_code, liv_location, liv_output, log, current_path);
                    exit_ = true;
                }
                if (aar1.contains("f")) {
                    log.logEvent(Level.INFO, "program uruchumiono z opcją 'f'");
                    try {
                        FTPClientJac ftpJac = new FTPClientJac();
                        ftpJac.dwonloadFile();
                    } catch (Exception ex) {
                        log.logEvent(Level.SEVERE, "ftpTest", ex);
                    }
                    exit_ = true;
                }
                if (aar1.contains("e")) {
                    log.logEvent(Level.INFO, "program uruchumiono z opcją 'e'");
                    salesExport salesExport = new salesExport(edi_date);
                    exit_ = true;
                }
            }catch (ArrayIndexOutOfBoundsException e) {
                
            }
            
        }

        //send out all validation files which are located in current direcotry 
        FTPClientJac ftpserev = new FTPClientJac();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains("Val")) {

                try {

                    ftpserev.UploadFile(current_path + "\\" + listOfFiles[i].getName(), listOfFiles[i].getName(), "/");

                } catch (Exception ex) {
                    AppMain.log.logEvent(Level.INFO, "Błąd podczas wysłania pliku  ", ex);

    }
            }
            
        }
        ftpserev.log_out();
            
        if (exit_) {
            System.exit(0);
        }

    }

    private void convert() {
        JFileChooser c = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Xcel file", "xls", "xlsx");
        c.setFileFilter(filter);
        int returnVal = c.showOpenDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + c.getSelectedFile().getName());
        }
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

                String edistri = is.crete_ediHZC(is.read());
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());
                output_path = output_path + "\\" + edi_pref + "_" + edi_date + ".txt";
                FileWriter fstream = new FileWriter(output_path);
                System.out.println("Plik zapisano: " + output_path);
                try (BufferedWriter sa = new BufferedWriter(fstream)) {
                    sa.write(edistri);
                    log.logEvent(Level.INFO, "Plik zapisano w : " + output_path);

                }

            } catch (IOException | InvalidFormatException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }

    private void convert2007() {
        JFileChooser c = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Xcel file", "xls", "xlsx");
        c.setFileFilter(filter);
        int returnVal = c.showOpenDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + c.getSelectedFile().getName());
        }
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

                String edistri = is.crete_ediHZC(is.read_2007Plus());
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());
                output_path = output_path + "\\" + edi_pref + "_" + edi_date + ".txt";
                FileWriter fstream = new FileWriter(output_path);
                System.out.println("Plik zapisano: " + output_path);
                try (BufferedWriter sa = new BufferedWriter(fstream)) {
                    sa.write(edistri);
                    sa.flush();
                    log.logEvent(Level.INFO, "Plik zapisano w : " + output_path);
                    System.exit(0);

                }

            } catch (IOException | InvalidFormatException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }

    private void CompareStock2007() {
        JFileChooser c = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Xcel file", "xls", "xlsx");
        c.setFileFilter(filter);
        int returnVal = c.showOpenDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + c.getSelectedFile().getName());
        }
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                if (is.checkStock2007()) {
                    log.logEvent(Level.FINE, "dodałem asortymenty    " + c.getSelectedFile().getName());
                }
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

            } catch (IOException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }

    private void addAsort2007() {
        JFileChooser c = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Xcel file", "xls", "xlsx");
        c.setFileFilter(filter);
        int returnVal = c.showOpenDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + c.getSelectedFile().getName());
        }
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                if (is.add_asort2007()) {
                    log.logEvent(Level.FINE, "dodałem asortymenty    " + c.getSelectedFile().getName());
                }
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

            } catch (IOException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }

    private void upOpsis2007() {
        JFileChooser c = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Xcel file", "xls", "xlsx");
        c.setFileFilter(filter);
        int returnVal = c.showOpenDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + c.getSelectedFile().getName());
        }
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                if (is.upOpis2007()) {
                    log.logEvent(Level.FINE, "zakualizowano opisy    " + c.getSelectedFile().getName());
                }
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

            } catch (IOException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }

    private void upOpsisXls() {
        JFileChooser c = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Xcel file", "xls", "xlsx");
        c.setFileFilter(filter);
        int returnVal = c.showOpenDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + c.getSelectedFile().getName());
        }
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                if (is.upOpisXls()) {
                    log.logEvent(Level.FINE, "zakualizowano opisy    " + c.getSelectedFile().getName());
                }
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

            } catch (IOException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }

    private void addAsort() {
        JFileChooser c = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Xcel file", "xls", "xlsx");
        c.setFileFilter(filter);
        int returnVal = c.showOpenDialog(c);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: "
                    + c.getSelectedFile().getName());
        }
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                if (is.add_asort()) {
                    log.logEvent(Level.FINE, "dodałem asortymenty    " + c.getSelectedFile().getName());
                }
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

            } catch (IOException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }

    /*  
     private void GenInvent() {
        
        output_path = current_path;
        if (c.getSelectedFile() != null) {
            log.logEvent(Level.FINE, "Towrzę obiekt do odczytu excel: " + c.getSelectedFile().getName());
            readXls is = new readXls(c.getSelectedFile(), Double.valueOf(przel), log, getSelectedButton(), dbm);
            log.logEvent(Level.FINE, "Utworzono obiekt excel : " + c.getSelectedFile().getName());
            try {
                log.logEvent(Level.FINE, "Uruchamiama metodę odczytu plik  " + c.getSelectedFile().getName());

                if (is.add_asort2007()) {
                    log.logEvent(Level.FINE, "dodałem asortymenty    " + c.getSelectedFile().getName());
                }
                log.logEvent(Level.FINE, "Przeczytałem i przeliczyłem plik   " + c.getSelectedFile().getName());

            } catch (IOException ex) {
                log.logEvent(Level.SEVERE, "convert", ex);
            }
        }

    }    
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        read_all = new javax.swing.JRadioButton();
        read_price_up = new javax.swing.JRadioButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        read_TowNotInDn = new javax.swing.JRadioButton();
        jButton6 = new javax.swing.JButton();
        read_TowNotInDn1 = new javax.swing.JRadioButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton10 = new javax.swing.JButton();
        dateChooserPanel1 = new datechooser.beans.DateChooserPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        ftpTestMenuItems = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Jacadi Converter 1.0");

        jButton1.setText("Wczytaj plik z cenami (xls)");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Walidacja Dostawy");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Wczytaj plik z cenami 2007 (xlsx)");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(read_all);
        read_all.setSelected(true);
        read_all.setText("Przelicz wszstkie towary ");

        buttonGroup1.add(read_price_up);
        read_price_up.setText("Przelicz towary z inna ceną detaliczna w bazie ");

        jButton4.setText("Dodaj asortymenty z 2007 xlsx");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Dodaj asortymenty xls");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        buttonGroup1.add(read_TowNotInDn);
        read_TowNotInDn.setText("Lista towarów których nie ma w bazie ");

        jButton6.setText("Aktualizacja  opisy z 2007 xlsx ");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        buttonGroup1.add(read_TowNotInDn1);
        read_TowNotInDn1.setText("Przelicz towary z inna ceną detaliczna w bazie & Stan > 0");

        jButton7.setText("Generuj Inwentaryzacje");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Sprawdź stany z pliku excel");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Aktualizacja  opisy z xls ");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Local retail price -- Cena sprzedaży \n\"PRIX DE CESSION DEV LOC\"  - Cena zakupu --, przy przeliczniku 0 cena brana z tej kolumny ");
        jScrollPane2.setViewportView(jTextArea2);

        jButton10.setBackground(new java.awt.Color(255, 0, 204));
        jButton10.setText("Export Sprzedaży");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        ftpTestMenuItems.setMnemonic('a');
        ftpTestMenuItems.setText("FtpTest");
        ftpTestMenuItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftpTestMenuItemsActionPerformed(evt);
            }
        });
        fileMenu.add(ftpTestMenuItems);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("ReadPCL");
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        contentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(read_all)
                            .addComponent(read_price_up)
                            .addComponent(read_TowNotInDn)
                            .addComponent(read_TowNotInDn1)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton10)
                            .addComponent(dateChooserPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(read_all)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(read_price_up)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(read_TowNotInDn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(read_TowNotInDn1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7))
                    .addComponent(dateChooserPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                             
        System.exit(0);
    }                                            

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        convert();
    }                                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        validator validator = new validator(dbm, store_code, liv_location, liv_output, log, current_path);
    }                                        

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        convert2007();
    }                                        

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        addAsort2007();
    }                                        

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        addAsort();
    }                                        

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        upOpsis2007();
    }                                        

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {
            inventList inl =   new inventList(this, rootPaneCheckingEnabled);
            //inl.setData_j(dbm.getInwentList());
            inl.setColu(dbm.getInwentList());
            inl.setVisible(true);
            
        } catch (SQLException ex) {
            log.logEvent(Level.SEVERE, "Błąd podczas wyświetlania listy inwentaryzacji", ex);
        }
    }                                        

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        CompareStock2007();
    }                                        

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        upOpsisXls();
    }                                        

    private void ftpTestMenuItemsActionPerformed(java.awt.event.ActionEvent evt) {                                                 

        try {
            FTPClientJac ftpJac = new FTPClientJac();
            ftpJac.dwonloadFile();
        } catch (Exception ex) {
            log.logEvent(Level.SEVERE, "ftpTest", ex);
        }

    }                                                

    private void contentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:
    }                                                

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {                                          
        Calendar dd = dateChooserPanel1.getSelectedDate();
        dd.getTime();
        System.out.println(sdf.format(dd.getTime()));
        //new salesExport("2018-04-07");
        new salesExport(sdf.format(dd.getTime()));
    }                                         

    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {                                               
        try {
           new readPCL().readCn();
        } catch (IOException ex) {
            Logger.getLogger(AppMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AppMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(AppMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }                                              

    int getSelectedButton() {
        for (Enumeration<AbstractButton> buttons = buttonGroup1.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                String s = button.getText();
                switch (button.getText()) {
                    case "Przelicz wszstkie towary ":
                        return 0;
                    case "Przelicz towary z inna ceną detaliczna w bazie ":
                        return 1;
                    case "Lista towarów których nie ma w bazie ":
                        return 2;
                    case "Przelicz towary z inna ceną detaliczna w bazie & Stan > 0":
                        return 3;
                    case "Generuj Inwentaryzacje":
                        return 4;
                    case "Sprawdź stany z pliku excel":
                        return 4;
                    //Sprawdź stany z pliku excel 

                }
            }
        }
        return 0;
        //Przelicz towary z inna ceną detaliczna w bazie & Stan > 0
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AppMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
            new AppMain(args).setVisible(true);
            } catch (Exception ex) {
                AppMain.log.logEvent(Level.SEVERE,"exception in main ", ex);
            }
        });

    }

    // Variables declaration - do not modify                     
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private datechooser.beans.DateChooserPanel dateChooserPanel1;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem ftpTestMenuItems;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JRadioButton read_TowNotInDn;
    private javax.swing.JRadioButton read_TowNotInDn1;
    private javax.swing.JRadioButton read_all;
    private javax.swing.JRadioButton read_price_up;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration                   

}
