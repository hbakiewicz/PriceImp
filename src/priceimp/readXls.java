/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import eventlog.IEventJournal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author hbakiewicz
 */
public class readXls {

    private final List<StdEdiObjLine> ediList = new ArrayList<>();
    Integer Product_code, Item, Season, Univers, Department, Family_code, Family_name, Sub_family_code, Sub_family, Color_code, Color_name, Size,
            Barcode, Target_code, Target, Age_code, Age, Product_line_code, Product_line, Theme_code, Theme, Cost_price, Retail_priceFrance_EUR, AdviseRetailPrice,
            SizeGridcode, Sizecode, ItemCode;

    private final File file;
    private final Double prze;
    private final IEventJournal log;
    private dbManager man;
    private int mode;

    public readXls(File file, Double _przel, IEventJournal _log, int mode, dbManager _man) {
        this.file = file;
        this.prze = _przel;
        this.log = _log;
        this.man = _man;
        this.mode = mode;
    }

    public StdEdiObjHZC read() throws IOException, InvalidFormatException {
        StdEdiObjHZC hed = new StdEdiObjHZC();
        hed.setTypDok("HZC_ZMIANA");
        hed.setNazwaWystawcy("Zmaiana Cen SP/ZAK z pliku excel :" + file.getName());
        hed.setData("01.01.2050");
        hed.setDataOdCzas("09:00");

        //Workbook wb = WorkbookFactory.create(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(2);
        Cell cell = row.getCell(3);

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        //int cols = 0; // No of columns
        int cols = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < cols; i++) {
            Cell cl = sheet.getRow(0).getCell(i);
            //version 0 is for old excel file 
            decodeCell(cl, i, 0);
        }
        int tmp;

        // This trick ensures that we get the data properly even if it doesn't start from first few rows
        for (int i = 0; i < 10 || i < rows; i++) {
            row = sheet.getRow(i);
            if (row != null) {
                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if (tmp > cols) {
                    cols = tmp;
                }
            }
        }
        for (int r = 0; r < rows; r++) {
            row = sheet.getRow(r + 1);
            if (row != null) {

                Double ilosc = 0.0;

                String nazwa = row.getCell(Item).getStringCellValue();
                nazwa = nazwa + " " + row.getCell(Size).getStringCellValue();
                nazwa = nazwa + " " + row.getCell(Color_name).getStringCellValue();
                String barcode = row.getCell(Barcode).getStringCellValue();

                StdEdiObjLine std = new StdEdiObjLine(nazwa, barcode, ilosc);
                std.setJM("szt");
                Double cen_tmp  = 0.0;
                try {
                    switch (row.getCell(Cost_price).getCellType()) {
                        case 1: {
                            String tmp_cena = row.getCell(Cost_price).getStringCellValue().replace(",", ".");
                            cen_tmp = Double.valueOf(tmp_cena);
                            break;
                        }
                        case 0: {
                            cen_tmp = row.getCell(Cost_price).getNumericCellValue();
                            break;
                        }
                        
                    }

                    //String tmp_cena = row.getCell(Cost_price).getStringCellValue().replace(",", ".");
                    //Double cen_tmp = Double.valueOf(tmp_cena);
                    
                    std.setCenaN(Double.toString(_round(cen_tmp * prze, 4)));
                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak ceny zakupu w pliku  dla kodu " + barcode + " w lini " + r);
                    std.setCenaN("0.0");
                }
                std.setVat("23");

                try {
                    std.setPKWiU(row.getCell(Color_name).getStringCellValue());

                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak PKWiU w pliku (Color name) dla kodu " + barcode + " w lini " + r);
                    std.setPKWiU("");
                }

                try {
                    std.setCenaSpB(row.getCell(AdviseRetailPrice).getStringCellValue());
                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak ceny sprzedaży  w pliku  dla kodu " + barcode + " w lini " + r);
                    std.setCenaSpB("0.0");
                }

                try {
                    std.setSww(row.getCell(Product_code).getStringCellValue());
                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak SWW w pliku (Product code) dla kodu " + barcode + " w lini " + r);
                    std.setSww("");
                }

                try {
                    String f = row.getCell(Theme).getStringCellValue().replace("è", "e");
                    std.setAsort(f);
                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak asortymentu w pliku (Theme ) dla kodu " + barcode + " w lini " + r);
                    std.setAsort("Domyślny");
                }
                /*
                0 - read all items 
                1 - read  tow where price is diffrent 
                 */
                switch (mode) {
                    case 0: {
                        ediList.add(std);
                        break;
                    }
                    case 1: {
                        try {
                            if (!man.check_price(barcode, row.getCell(AdviseRetailPrice).getStringCellValue())) {
                                ediList.add(std);
                            }
                        } catch (SQLException ex) {
                            log.logEvent(Level.SEVERE, null, ex);
                        }
                        break;
                    }
                    case 2: {
                        try {
                            if (!man.check_tow(barcode)) {
                                ediList.add(std);
                            }
                        } catch (SQLException ex) {
                            log.logEvent(Level.SEVERE, null, ex);
                        }
                        break;
                    }
                    case 3: {
                        try {
                            if (!man.check_price_grater_0(barcode, row.getCell(AdviseRetailPrice).getStringCellValue())) {
                                ediList.add(std);
                            }
                        } catch (SQLException ex) {
                            log.logEvent(Level.SEVERE, null, ex);
                        }
                        break;
                    }

                }
                System.err.println("Nazwa | " + row.getCell(Item).getStringCellValue() + " | " + "Kod " + row.getCell(Barcode).getStringCellValue());
            }
        }
        hed.setEdiList(ediList);
        return hed;
    }

    public StdEdiObjHZC read_2007Plus() throws IOException, InvalidFormatException {
        StdEdiObjHZC hed = new StdEdiObjHZC();
        hed.setTypDok("HZC_ZMIANA");
        hed.setNazwaWystawcy("Zmiana Cen SP/ZAK z pliku excel :" + file.getName());
        hed.setData("01.01.2050");
        hed.setDataOdCzas("09:00");

        //Workbook wb = WorkbookFactory.create(new FileInputStream(file));
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(2);
        Cell cell = row.getCell(3);

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        //int cols = 0; // No of columns
        int cols = sheet.getRow(5).getPhysicalNumberOfCells();
        for (int i = 0; i < cols; i++) {
            Cell cl = sheet.getRow(2).getCell(i);
            //version 0 is for old excel file
            decodeCell(cl, i, 1);
        }
        int tmp;

        // This trick ensures that we get the data properly even if it doesn't start from first few rows
        for (int i = 0; i < 10 || i < rows; i++) {
            row = sheet.getRow(i);
            if (row != null) {
                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if (tmp > cols) {
                    cols = tmp;
                }
            }
        }
        for (int r = 0; r < rows; r++) {
            row = sheet.getRow(r + 3);
            if (row != null) {

                Double ilosc = 0.0;

                String nazwa = row.getCell(Item).getStringCellValue();
                nazwa = nazwa + " " + row.getCell(Size).getStringCellValue();
                nazwa = nazwa + " " + row.getCell(Color_name).getStringCellValue();

                String barcode = row.getCell(Barcode).getStringCellValue();

                StdEdiObjLine std = new StdEdiObjLine(nazwa, barcode, ilosc);
                std.setJM("szt");
                try {

                    if (prze != 0) {
                        String tmp_cena = String.valueOf(row.getCell(Cost_price).getNumericCellValue()).replace(",", ".");

                        Double cen_tmp = Double.valueOf(tmp_cena);

                        std.setCenaN(Double.toString(_round(cen_tmp * prze, 4)));
                    } else {

                        String tmp_cena = String.valueOf(row.getCell(Cost_price).getNumericCellValue()).replace(",", ".");

                        Double cen_tmp = Double.valueOf(tmp_cena);

                        std.setCenaN(Double.toString(_round(cen_tmp, 2)));
                    }

                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak ceny zakupu w pliku  dla kodu " + barcode + " w lini " + r);
                    std.setCenaN("0.0");
                }
                std.setVat("23");

                try {
                    std.setPKWiU(row.getCell(Color_name).getStringCellValue());

                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak PKWiU w pliku (Color name) dla kodu " + barcode + " w lini " + r);
                    std.setPKWiU("");
                }

                try {
                    std.setCenaSpB(row.getCell(AdviseRetailPrice).getStringCellValue());
                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak ceny sprzedaży  w pliku  dla kodu " + barcode + " w lini " + r);
                    std.setCenaSpB("0.0");
                }

                try {
                    std.setSww(row.getCell(Product_code).getStringCellValue());
                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak SWW w pliku (Product code) dla kodu " + barcode + " w lini " + r);
                    std.setSww("");
                }

                try {
                    String f = row.getCell(Theme).getStringCellValue().replace("è", "e");
                    std.setAsort(f);
                    /*if (row.getCell(Theme).getStringCellValue().contains("CHAUSSURES (hors")) {
                        System.out.println(row.getCell(Theme).getStringCellValue());
                    }*/
                } catch (NullPointerException e) {
                    log.logEvent(Level.INFO, "brak asortymentu w pliku (Theme ) dla kodu " + barcode + " w lini " + r);
                    std.setAsort("Domyślny");
                }
                /*
                0 - read all items 
                1 - read  tow where price is diffrent 
                 */
                switch (mode) {
                    case 0: {
                        ediList.add(std);
                        break;
                    }
                    case 1: {
                        try {
                            if (!man.check_price(barcode, row.getCell(AdviseRetailPrice).getStringCellValue())) {
                                ediList.add(std);
                            }
                        } catch (SQLException ex) {
                            log.logEvent(Level.SEVERE, null, ex);
                        }
                        break;
                    }
                    case 2: {
                        try {
                            if (!man.check_tow(barcode)) {
                                ediList.add(std);
                            }
                        } catch (SQLException ex) {
                            log.logEvent(Level.SEVERE, null, ex);
                        }
                        break;
                    }
                    case 3: {
                        try {
                            if (!man.check_price_grater_0(barcode, row.getCell(AdviseRetailPrice).getStringCellValue())) {
                                ediList.add(std);
                            }
                        } catch (SQLException ex) {
                            log.logEvent(Level.SEVERE, null, ex);
                        }

                    }

                }
                System.err.println("Nazwa | " + row.getCell(Item).getStringCellValue() + " | " + "Kod " + row.getCell(Barcode).getStringCellValue());
            }
        }
        hed.setEdiList(ediList);
        return hed;
    }

    @SuppressWarnings("null")
    public boolean add_asort() throws FileNotFoundException, IOException {

        //Workbook wb = WorkbookFactory.create(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(2);
        Cell cell = row.getCell(3);
        int add_asort = 0;

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        //int cols = 0; // No of columns
        int cols = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < cols; i++) {
            Cell cl = sheet.getRow(0).getCell(i);
            //version 0 is for old excel file 
            decodeCell(cl, i, 0);
        }
        String f = "";
        for (int r = 0; r < rows; r++) {

            row = sheet.getRow(r + 1);
            if (row != null) {
                if (row != null) {
                    try {
                        f = row.getCell(Theme).getStringCellValue().replace("è", "e");
                    } catch (NullPointerException e) {
                    }
                    try {
                        if (man.add_asort(f)) {
                            add_asort++;
                        }
                        //System.out.println(f+ " " + add_asort);
                    } catch (SQLException ex) {
                        //System.err.println(f);
                        log.logEvent(Level.SEVERE, null, ex);
                    }

                }
            }
        }
        log.logEvent(Level.INFO, "Dodano " + add_asort + " asortymentów");
        return false;
    }

    public boolean add_asort2007() throws FileNotFoundException, IOException {

        //Workbook wb = WorkbookFactory.create(new FileInputStream(file));
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(2);
        Cell cell = row.getCell(3);
        int add_asort = 0;

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        //int cols = 0; // No of columns
        int cols = sheet.getRow(5).getPhysicalNumberOfCells();
        for (int i = 0; i < cols; i++) {
            Cell cl = sheet.getRow(2).getCell(i);
            //version 0 is for old excel file
            decodeCell(cl, i, 1);
        }
        String f = "";
        for (int r = 0; r < rows - 3; r++) {
            row = sheet.getRow(r + 3);

            if (row != null) {
                try {
                    f = row.getCell(Theme).getStringCellValue().replace("è", "e");
                } catch (NullPointerException e) {
                }
                try {
                    if (man.add_asort(f)) {
                        add_asort++;
                    }
                    //System.out.println(f+ " " + add_asort);
                } catch (SQLException ex) {
                    //System.err.println(f);
                    log.logEvent(Level.SEVERE, null, ex);
                }

            }
        }
        log.logEvent(Level.INFO, "Dodano " + add_asort + " asortymentów");
        return false;
    }

    public boolean checkStock2007() throws FileNotFoundException, IOException {

        //Workbook wb = WorkbookFactory.create(new FileInputStream(file));
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet_1 = null;
        try {
            wb = new XSSFWorkbook(new FileInputStream(file));
            sheet_1 = wb.getSheetAt(0);

        } catch (IOException e) {
            log.logEvent(Level.INFO, "Bład wczytywania pliku ", e);
        }

        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(2);
        @SuppressWarnings("UnusedAssignment")
        Cell cell = row.getCell(3);
        int add_asort = 0;

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        //int cols = 0; // No of columns
        int cols = sheet.getRow(5).getPhysicalNumberOfCells();
        /*for (int i = 0; i < cols; i++) {
            Cell cl = sheet.getRow(2).getCell(i);
            //version 0 is for old excel file
            decodeCell(cl, i, 1);
        }*/
        String f = "";
        String q;
        double q_ = 0;
        double q_db = 0;

        for (int r = 0; r < rows; r++) {
            row = sheet.getRow(r + 1);

            if (row != null) {
                try {
                    f = row.getCell(8).getStringCellValue();
                    q_ = row.getCell(10).getNumericCellValue();
                } catch (NullPointerException e) {
                }
                try {
                    q = man.getStockByCode(f).replace(".0000", "");
                    q_ = Double.parseDouble(f);
                    if (!q.isEmpty()) {
                        q_db = Double.parseDouble(q);

                        @SuppressWarnings("null")
                        XSSFRow sheetrow = sheet_1.getRow(r);
                        if (sheetrow == null) {
                            sheetrow = sheet_1.createRow(r);
                        }
                        cell = sheetrow.getCell(11);
                        if (cell == null) {
                            cell = sheetrow.createCell(11);
                        }
                        cell.setCellValue(q);

                    }

                    //System.out.println(f+ " " + add_asort);
                } catch (SQLException ex) {
                    //System.err.println(f);
                    log.logEvent(Level.SEVERE, null, ex);
                }

            }
        }

        String fileName = file.getName().replace(".xlsx", "");
        String path = file.getParent();
        try (FileOutputStream outFile = new FileOutputStream(new File(path + "\\ " + fileName + "_PCM.xlsx"))) {
            wb.write(outFile);
        }

        log.logEvent(Level.INFO, "Sprawdzone  " + rows + " pozycje ");
        return false;
    }

    public boolean upOpis2007() throws FileNotFoundException, IOException {

        //Workbook wb = WorkbookFactory.create(new FileInputStream(file));
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(2);
        Cell cell = row.getCell(3);
        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        //int cols = 0; // No of columns
        int cols = sheet.getRow(5).getPhysicalNumberOfCells();
        for (int i = 0; i < cols; i++) {
            Cell cl = sheet.getRow(2).getCell(i);
            //version 0 is for old excel file
            decodeCell(cl, i, 1);
        }

        /*
        Opis1 = Family Name
        Opis2 = Size
        Opis3 = Product code
        Opis4 = Item code
         */
        String f = "";
        for (int r = 0; r < rows - 3; r++) {
            row = sheet.getRow(r + 3);

            if (row != null) {

                try {
                    man.upOpisByKod(row.getCell(Barcode).getStringCellValue(),
                            row.getCell(Family_name).getStringCellValue(),
                            row.getCell(Size).getStringCellValue(),
                            row.getCell(Product_code).getStringCellValue(),
                            row.getCell(ItemCode).getStringCellValue());

                } catch (SQLException ex) {

                    log.logEvent(Level.SEVERE, null, ex);
                }

            }
        }

        return false;
    }
    
        public boolean upOpisXls() throws FileNotFoundException, IOException {

        //Workbook wb = WorkbookFactory.create(new FileInputStream(file));
        //XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(2);
        Cell cell = row.getCell(3);
        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        //int cols = 0; // No of columns
        int cols = sheet.getRow(5).getPhysicalNumberOfCells();
        for (int i = 0; i < cols; i++) {
            Cell cl = sheet.getRow(0).getCell(i);
            //version 0 is for old excel file
            decodeCell(cl, i, 1);
        }

        /*
        Opis1 = Family Name
        Opis2 = Size
        Opis3 = Product code
        Opis4 = Item code
         */
        String f = "";
        for (int r = 0; r < rows - 3; r++) {
            row = sheet.getRow(r + 3);

            if (row != null) {

                try {
                    man.upOpisByKod(row.getCell(Barcode).getStringCellValue(),
                            row.getCell(Family_name).getStringCellValue(),
                            row.getCell(Size).getStringCellValue(),
                            row.getCell(Product_code).getStringCellValue(),
                            row.getCell(ItemCode).getStringCellValue());

                } catch (SQLException ex) {

                    log.logEvent(Level.SEVERE, null, ex);
                }

            }
        }
        log.logEvent(Level.INFO, "Koniec dodawania opisów ");
        return false;
    }

    public String crete_edi(StdEdiObj stb) {

        StringBuilder str_ = new StringBuilder();

        str_.append("TypPolskichLiter:").append(stb.getTypPolskichLiter()).append("\n");
        str_.append("TypDok:").append(stb.getTypDok()).append("\n");
        str_.append("NrDok:").append(stb.getNrDok()).append("\n");
        str_.append("Data:").append(stb.getData()).append("\n");
        str_.append("Magazyn:").append(stb.getMagazyn()).append("\n");
        str_.append("SposobPlatn:").append(stb.getSposobPlatn()).append("\n");
        str_.append("TerminPlatn:").append(stb.getTerminPlatn()).append("\n");
        str_.append("IndeksCentralny:").append(stb.getIndeksCentralny()).append("\n");
        str_.append("NazwaWystawcy:").append(stb.getNazwaWystawcy()).append("\n");
        str_.append("AdresWystawcy:").append(stb.getAdresWystawcy()).append("\n");
        str_.append("KodWystawcy:").append(stb.getKodWystawcy()).append("\n");
        str_.append("MiastoWystawcy:").append(stb.getMiastoWystawcy()).append("\n");
        str_.append("UlicaWystawcy:").append(stb.getUlicaWystawcy()).append("\n");
        str_.append("NIPWystawcy:").append(stb.getNIPWystawcy()).append("\n");
        str_.append("BankWystawcy:").append(stb.getBankWystawcy()).append("\n");
        str_.append("KontoWystawcy:").append(stb.getKontoWystawcy()).append("\n");
        str_.append("TelefonWystawcy:").append(stb.getTelefonWystawcy()).append("\n");
        str_.append("NrWystawcyWSieciSklepow:").append(stb.getNrWystawcyWSieciSklepow()).append("\n");
        str_.append("WystawcaToCentralaSieci:").append(stb.getWystawcaToCentralaSieci()).append("\n");
        str_.append("NrWystawcyObcyWSieciSklepow:").append(stb.getNrWystawcyObcyWSieciSklepow()).append("\n");
        str_.append("NazwaOdbiorcy:").append(stb.getNazwaOdbiorcy()).append("\n");
        str_.append("AdresOdbiorcy:").append(stb.getAdresOdbiorcy()).append("\n");
        str_.append("KodOdbiorcy:").append(stb.getKodOdbiorcy()).append("\n");
        str_.append("MiastoOdbiorcy:").append(stb.getMiastoOdbiorcy()).append("\n");
        str_.append("UlicaOdbiorcy:").append(stb.getUlicaOdbiorcy()).append("\n");
        str_.append("NIPOdbiorcy:").append(stb.getNIPOdbiorcy()).append("\n");
        str_.append("BankOdbiorcy:").append(stb.getBankOdbiorcy()).append("\n");
        str_.append("KontoOdbiorcy:").append(stb.getKontoOdbiorcy()).append("\n");
        str_.append("TelefonOdbiorcy:").append(stb.getTelefonOdbiorcy()).append("\n");
        str_.append("NrOdbiorcyWSieciSklepow:").append(stb.getNrOdbiorcyWSieciSklepow()).append("\n");
        str_.append("OdbiorcaToCentralaSieci:").append(stb.getOdbiorcaToCentralaSieci()).append("\n");
        str_.append("NrOdbiorcyObcyWSieciSklepow:").append(stb.getNrOdbiorcyWSieciSklepow()).append("\n");
        str_.append("IloscLinii:").append(stb.getEdiList().size()).append("\n");

        stb.getEdiList().stream().map((stdEdiObjLine) -> {
            str_.append("Linia:Nazwa{").append(stdEdiObjLine.getNazwa()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Kod{").append(stdEdiObjLine.getKod()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Vat{").append(stdEdiObjLine.getVat()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Jm{").append(stdEdiObjLine.getJM()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Asortyment{").append(stdEdiObjLine.getAsort()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Sww{").append(stdEdiObjLine.getSww()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("PKWiU{").append(stdEdiObjLine.getPKWiU()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Ilosc{").append(Double.toString(stdEdiObjLine.getIlosc())).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Cena{n").append(stdEdiObjLine.getCenaN()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Wartosc{n").append(stdEdiObjLine.getWartoscN()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("IleWOpak{").append(stdEdiObjLine.getIleWOpak()).append("}");
            return stdEdiObjLine;
        }).forEach((stdEdiObjLine) -> {
            str_.append("CenaSp{b").append(stdEdiObjLine.getCenaSpB()).append("}\n");
        });

        return str_.toString();
    }

    private void decodeCell(Cell c, int col, int ver) {
        String co;
        //version 0 is for old excel file
        if (ver == 0) {
            co = "Cost price";
        } else {
            co = "PRIX DE CESSION DEV FACT";
        }
        String t = c.getStringCellValue();
        System.out.println(t);
        switch (c.getStringCellValue()) {
            case "Product code":
                Product_code = col;
                break;
            case "Item":
                Item = col;
                break;
            case "Season":
                Season = col;
                break;
            case "Univers":
                Univers = col;
                break;
            case "Department":
                Department = col;
                break;
            case "Family code":
                Family_code = col;
                break;
            case "Family name":
                Family_name = col;
                break;
            case "Sub family code":
                Sub_family_code = col;
                break;
            case "Sub family":
                Sub_family = col;
                break;
            case "Color code":
                Color_code = col;
                break;
            case "Color name":
                Color_name = col;
                break;
            case "Size":
                Size = col;
                break;
            case "Barcode":
                Barcode = col;
                break;
            case "Target code":
                Target_code = col;
                break;
            case "Target":
                Target = col;
                break;
            case "Age code":
                Age_code = col;
                break;
            case "Age":
                Age = col;
                break;
            case "Product line code":
                Product_line_code = col;
                break;
            case "Product line":
                Product_line = col;
                break;
            case "Theme code":
                Theme_code = col;
                break;
            case "Theme":
                Theme = col;
                break;
            case "Cost price":
                Cost_price = col;
                break;
            case "PRIX DE CESSION DEV LOC":
                Cost_price = col;
                break;
            case "Retail price France EUR":
                Retail_priceFrance_EUR = col;
                break;
            case "Local retail price":
                AdviseRetailPrice = col;
                break;
            case "Size grid code":
                SizeGridcode = col;
                break;
            case "Size code":
                Sizecode = col;
                break;
            case "Item code":
                ItemCode = col;
                break;
        }

    }

    public String crete_ediHZC(StdEdiObjHZC stb) {

        /*
        TypPolskichLiter:LA
TypDok:HZC_ZMIANA
NazwaHZC:Zmiana cen
Data:19.10.2016
DataOdCzas:23.55
Data3:19.10.2016
Data3OdCzas:23.55
SklepyHZC:0
NazwaWystawcy:
AdresWystawcy:
KodWystawcy:
MiastoWystawcy:
UlicaWystawcy:
NIPWystawcy:
BankWystawcy:
KontoWystawcy:
TelefonWystawcy:
NrWystawcyWSieciSklepow:17
WystawcaToCentralaSieci:0
NrWystawcyObcyWSieciSklepow:
IloscLinii:5*/
        StringBuilder str_ = new StringBuilder();

        str_.append(stb.getNag());

        stb.getEdiList().stream().map((stdEdiObjLine) -> {
            str_.append("Linia:Nazwa{").append(stdEdiObjLine.getNazwa()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Kod{").append(stdEdiObjLine.getKod()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Vat{").append(stdEdiObjLine.getVat()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Jm{").append(stdEdiObjLine.getJM()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Asortyment{").append(stdEdiObjLine.getAsort()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Sww{").append(stdEdiObjLine.getSww()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("PKWiU{").append(stdEdiObjLine.getPKWiU()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Ilosc{").append(Double.toString(stdEdiObjLine.getIlosc())).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Cena{n").append(stdEdiObjLine.getCenaN()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("Wartosc{n").append(stdEdiObjLine.getWartoscN()).append("}");
            return stdEdiObjLine;
        }).map((stdEdiObjLine) -> {
            str_.append("IleWOpak{").append(stdEdiObjLine.getIleWOpak()).append("}");
            return stdEdiObjLine;
        }).forEach((stdEdiObjLine) -> {
            str_.append("CenaSp{b").append(stdEdiObjLine.getCenaSpB()).append("}\n");
        });

        return str_.toString();
    }

    public static double _round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private String brt_nett(String cen, String stawka) {
        Double _cen, _stawk;
        _cen = Double.valueOf(cen);
        _stawk = Double.valueOf(stawka);
        return String.valueOf(_round(_cen / _stawk, 4));

    }

}
