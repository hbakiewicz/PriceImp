/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcin
 */
public class dbManager {

    private final String connect_string;
    private final String user;
    private final String password;
    private final String baza;
    private Connection conn;
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat dateShort = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat rok_2 = new SimpleDateFormat("YY");
    public static DateFormat rok_4 = new SimpleDateFormat("yyyy");
    public static DateFormat miesi = new SimpleDateFormat("MM");
    public static DateFormat jacDateformat = new SimpleDateFormat("yyyyMMdd");
    private long aktDokID;

    public dbManager(String connect_string, String user, String password, String baza) {
        this.connect_string = connect_string;
        this.user = user;
        this.password = password;
        this.baza = baza;

        //tworze połącznie do bazy danych 
        try {
            this.conn = dbConnect(connect_string + ";databaseName=" + baza, user, password);
            //aktDokID = new aktDokId(conn).wardok(); //pobieram aktualny DokId na jakim będę pracował 
        } catch (SQLException ex) {
            Logger.getLogger(dbManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Connection dbConnect(String db_connect_string,
            String db_userid,
            String db_password
    ) throws SQLException {

        Connection lacze = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            lacze = DriverManager.getConnection(db_connect_string,
                    db_userid, db_password);
            System.out.println("connected");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return lacze;

    }

    public void updateSql(String ss) throws SQLException {

        try {
            Statement st = this.conn.createStatement();
            //System.out.println(ss);
            st.executeUpdate(ss);
        } catch (SQLException ex) {

            Logger.getLogger(dbManager.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public ResultSet zapySql(String ss) throws SQLException {

        Statement st;
        try {
            st = this.conn.createStatement();
        } catch (SQLException ex) {

            Logger.getLogger(dbManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        //System.out.println(ss);
        return st.executeQuery(ss);

    }

    //funkcja cene towarów 
    public String get_price(String towid) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select "
                + " Round(CenaDet*(1+(CAST( Stawka AS DECIMAL))/10000),2) as CenaDet,"
                + "CenaEw,CenaHurt "
                + "from Towar where TowId = " + towid);
        while (n.next()) {
            p = p + n.getString("CenaEw") + ";"
                    + n.getString("CenaDet") + ";"
                    + n.getString("CenaHurt") + ";\n";

        }

        byte ptext[] = p.getBytes(ISO_8859_1);
        String value = new String(ptext, UTF_8);
        return value;
    }

    public boolean check_price(String kod_kreskowy, String cena_det) throws SQLException {
        String p;
        ResultSet n;
        n = zapySql("use " + baza + " select "
                + " Round(CenaDet*(1+(CAST( Stawka AS DECIMAL))/10000),2) as CenaDet  "
                + "from Towar where Kod = '" + kod_kreskowy + "'");
        if (n.next()) {
            p = n.getString("CenaDet").replace(".0000000", "");
            System.out.println(p + " | " + cena_det);
            if (p.equals(cena_det)) {
                return true;
            }

        } else {
            System.out.println("brak towaru o kodzie " + kod_kreskowy);
            return true;
        }
        return false;
    }

    public boolean check_price_grater_0(String kod_kreskowy, String cena_det) throws SQLException {
        String p;
        ResultSet n;
        n = zapySql("use " + baza + " select "
                + " Round(T.CenaDet*(1+(CAST( T.Stawka AS DECIMAL))/10000),2) as CenaDet "
                + " from Towar T, Istw I where T.Kod = '" + kod_kreskowy + "' and I.StanMag > 0 and T.TowId = I.TowId ");
        if (n.next()) {
            p = n.getString("CenaDet").replace(".0000000", "");
            System.out.println(p + " | " + cena_det);
            if (p.equals(cena_det)) {
                return true;
            }

        } else {
            System.out.println("brak stanu lub inna cenna towaru o kodzie : " + kod_kreskowy);
            return true;
        }
        return false;
    }

    //funkcja zwraca listę kontrahentów 
    public String get_kontra(String lastUP) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select * from Kontrahent " + lastUP);
        while (n.next()) {
            p = p + n.getString("Nazwa") + ";" + n.getString("Ulica") + ";\n";

        }

        byte ptext[] = p.getBytes(ISO_8859_1);
        String value = new String(ptext, UTF_8);
        return value;
    }

    public boolean add_asort(String asort_name) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select nazwa  from asort  where nazwa = '" + asort_name.replace("'", "''") + "'");
        if (n.next()) {
            p = n.getString("Nazwa");

        } else {
            updateSql(" insert into asort(Nazwa,Marza,OpcjaMarzy,HurtRabat,OpcjaRabatu,NocNarzut,OpcjaNarzutu) values ('" + asort_name.replace("'", "''") + "',0,1,0,0,0,1)");
            return true;
        }
        return false;
    }

    //funkcja zwraca listę listę pozycji do walidacji  
    public Dok getValDoc() throws SQLException {

        ResultSet n;
        n = zapySql("use " + baza + " select substring(Tekst,0,13) as dokName,dokid from tekstdok where znaczenie = 28 and dokid =   (select top 1 dokid from dok where typdok =2 and Opcja4 = 0 order by dokid desc)");
        if (n.next()) {

            Dok _dok = new Dok(n.getString("dokName"), n.getString("dokid"));

            if (_dok.dokName.contains("Liv")) {

                n = zapySql("use " + baza + " select p.IloscPlus,k.kod from pozdok p,  towar k  where dokid = " + _dok.getDokid() + "  and  p.towid = k.towid");

                while (n.next()) {

                    PozDok pp = new PozDok(n.getString("Kod"), n.getString("IloscPlus"));

                    _dok.pozdok.add(pp);
                }
            } else {
                _dok.setDokName("@Err");
            }
            return _dok;
        } else {
            return new Dok("@Err", "Empty");
        }

    }

    public void markAsValidated(String dokid) throws SQLException {

        updateSql("update dok set Opcja4 = 9 where dokid = " + dokid);
    }

    public void upOpisByKod(String kod, String O1, String O2, String O3, String O4) throws SQLException {

        updateSql("update Towar set Opis1 = '" + O1
                + "',Opis2 = '" + O2
                + "',Opis3 = '" + O3
                + "',Opis4 = '" + O4
                + "',Zmiana = getdate() where kod = '" + kod + "'");
    }

    public boolean check_tow(String kod) throws SQLException {
        String p = "";
        ResultSet n;
        if (kod.contains("3603650451443")) {
            System.out.println("tttt");
        }
        n = zapySql("use " + baza + " select nazwa  from towar  where kod = '" + kod + "'");
        if (n.next()) {
            p = n.getString("Nazwa");
            return true;

        }
        return false;
    }

    public boolean check_tow_Stan_greater(String kod) throws SQLException {
        String p = "";
        ResultSet n;
        n = zapySql("use " + baza + " select t.nazwa,i.stanmag  from towar t,Istw I where t.kod = '" + kod + "' and I.StanMag > 0 and t.TowId = i.TowId");
        if (n.next()) {
            p = n.getString("Nazwa");
            return true;

        }
        return false;
    }

    public String getStockByCode(String kod) throws SQLException {
        String p;
        ResultSet n;
        n = zapySql("use " + baza + " select t.nazwa,i.stanmag  from towar t,Istw I where t.kod = '" + kod + "'  and t.TowId = i.TowId");
        if (n.next()) {
            p = n.getString("stanmag");
            return p;

        }
        return "";
    }

    public List<SprObj> getSale(String date) throws SQLException {

        /*
        'select  T.Nazwa as Nazwa_Towaru, CONVERT(VARCHAR(10), (D.Data), 120) as Data, T.Kod,P.IloscPlus as Ilosc,Dk.KasaId as KasId,D.Param1 as Receipt'+
                ',ROUND(P.CenaPoRab*(1+(CAST( P.Stawka AS decimal))/10000),2) as CenaD '+
                'from Dok D, PozDok P, Towar T, DokKasa  Dk '+
                'where D.TypDok = 21 '+
                'and P.DokId=D.DokId '+
                'and P.TowId=T.TowId  '+
                'and D.DokId = Dk.DokId '+
                'and D.Data = '+#39+Data+#39,q);*/
        List<SprObj> spr = new ArrayList<>();

        // zapytanie o sprzedaż 
        ResultSet n;
        n = zapySql("select  T.Nazwa as Nazwa_Towaru, CONVERT(VARCHAR(10), (D.Data), 120) as Data, T.Kod,P.IloscPlus as Ilosc,Dk.KasaId as KasId,D.Param1 as Receipt"
                + ",ROUND(P.CenaPoRab*(1+(CAST( P.Stawka AS decimal))/10000),2) as CenaD "
                + "from Dok D, PozDok P, Towar T, DokKasa  Dk "
                + "where D.TypDok = 21 "
                + "and P.DokId=D.DokId "
                + "and P.TowId=T.TowId  "
                + "and D.DokId = Dk.DokId "
                + "and D.Data = '" + date + "'");
        while (n.next()) {

            spr.add(new SprObj(n.getString("Kod"), n.getString("Receipt"), n.getString("Ilosc"), n.getString("CenaD"), n.getString("KasId")));

        }
        // zapytanie o zwroty 
        n = zapySql("select  T.Nazwa as Nazwa_Towaru, CONVERT(VARCHAR(10), (D.Data), 120) as Data, T.Kod,P.IloscPlus as Ilosc,Dk.KasaId as KasId,D.DokId as Receipt "
                + ",ROUND(P.CenaPoRab*(1+(CAST( P.Stawka AS decimal))/10000),2) as CenaD "
                + "from Dok D, PozDok P, Towar T, DokKasa  Dk "
                + "where D.TypDok = 8 "
                + "and P.DokId=D.DokId "
                + "and P.TowId=T.TowId "
                + "and D.DokId = Dk.DokId "
                + " and D.Data = '" + date + "'");
        while (n.next()) {

            spr.add(new SprObj(n.getString("Kod"), n.getString("Receipt"), "-" + n.getString("Ilosc"), n.getString("CenaD"), n.getString("KasId")));

        }

        return spr;

    }

    public String[] getInwentList() throws SQLException {
        String[] p = new String[15];
        int c = 0;
        ResultSet n;
        n = zapySql("use " + baza + " select top (15) dokid,data,NrDok from dok where TypDok = 16 and aktywny = 1 order by DokId desc");
        while (n.next()) {

            p[c] = n.getString("dokid") + ";" + n.getString("data").substring(0, 10) + ";" + n.getString("NrDok");
            c++;

        }
        return p;
    }

    public String[] getInwentPoz(String dokid) throws SQLException {

        int c = 0;
        ResultSet n;

        n = zapySql("use " + baza + " select count(dokid) as count from PozDok  where  IloscPlus > 0 and  DokId =" + dokid);
        int rowcount = 0;
        if (n.next()) {
            rowcount = n.getInt("count");
        }

        //n = zapySql("use " + baza + " select RIGHT('0000'+CAST(P.NrPozycji AS VARCHAR(4)),4) as NrPozycji ,P.IloscPlus,P.IloscMinus, T.Kod  from PozDok P, Towar T where T.Towid = P.TowId and  DokId =" + dokid);
        n = zapySql("use " + baza + " select P.NrPozycji ,P.IloscPlus,P.IloscMinus, T.Kod  from PozDok P, Towar T where T.Towid = P.TowId and  IloscPlus > 0 and  DokId =" + dokid + "  order by nrpozycji ");

        String[] p = new String[rowcount];
        while (n.next()) {

            p[c] = n.getString("NrPozycji") + ";"
                    + addZero(n.getString("IloscMinus").substring(0, n.getString("IloscMinus").indexOf(".")), 3) + ";"
                    + addZero(n.getString("IloscPlus").substring(0, n.getString("IloscPlus").indexOf(".")), 3) + ";"
                    + n.getString("Kod");
            c++;

        }
        return p;
    }

    private String addZero(String txt, Integer cunt) {

        while (txt.length() < cunt) {
            txt = "0" + txt;
        }

        return txt;
    }

    public String getDateDok(String dokid) throws SQLException {
        Date p = null;

        ResultSet n;
        n = zapySql("use " + baza + " select Data from dok where dokid = " + dokid);
        while (n.next()) {

            p = n.getDate("Data");

        }
        return jacDateformat.format(p);
    }

}
