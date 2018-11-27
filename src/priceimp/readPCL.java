/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author HUBERTBAKIEWICZ
 */
public class readPCL {

    public readPCL() throws FileNotFoundException, IOException {
        /*
        StringBuilder sb = new StringBuilder();
        File folder = new File("d:\\Jacadi\\log_PCL\\");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (!file.getName().contains("ALL_PCL.txt") && file.isFile()) {
                System.out.println("czytam plik  " + file.getName());
                BufferedReader br = new BufferedReader(new FileReader(file));
                System.out.println("odczytałem  ");
                try {

                    String line = br.readLine();

                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    System.out.println("koniec czytania pliku " + file.getName());

                } catch (IOException e) {
                    System.err.println("blad " + e);
                }

            }
        }

        PrintWriter out;
        out = new PrintWriter("d:\\Jacadi\\log_PCL\\ALL_PCL.txt");

        out.print(sb.toString());

        out.flush();
         */

    }

    public void readCn() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        StringBuilder sb = new StringBuilder();
        boolean red = false;
        boolean red2 = false;
        String prev = "";
        int count = 0;
        int liczb = 0;
        String data, cupCode = "", nom = "";
        Double sumCup = 0.0;
        //Calendar dataTran;
        //ateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        //DateTime dt = formatter.parseDateTime(string);
        //DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String data07 = "2018-07-01 23:24:37.076";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime ldt = LocalDateTime.parse(data07, f);
        LocalDateTime datatrans;
        System.out.println(ldt.toString());
        //Calendar data07 = parseDateTime("2018-07-12 16:20:46");
        File folder = new File("d:\\Jacadi\\log_PCL\\ALL_PCL.txt");
        BufferedReader br = new BufferedReader(new FileReader(folder));
        sb.append("<pcb>");

        System.out.println("odczytałem  ");
        try {

            String line = br.readLine();

            while (line != null) {

                if (line.contains("TRANSINFO")) {
                    red = true;
                    count = 1;
                    prev = line;
                }
                if (line.contains("REQUEST")) {
                    red2 = true;
                }

                if (red && red2) {
                    if (count == 1) {
                        sb.append(prev);
                        count = 0;
                    }
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                if (line.contains("</pcbPacket>")) {
                    red = false;
                    red2 = false;
                }

                line = br.readLine();
            }
            System.out.println("koniec czytania pliku " + folder.getName());

        } catch (IOException e) {
            System.err.println("blad " + e);
        }
        PrintWriter out1;
        out1 = new PrintWriter("d:\\Jacadi\\log_PCL\\ALL_TRANS.txt");
        sb.append("</pcb>");
        out1.print(sb.toString());

        out1.flush();
        String dss = "";

        String[] scripts = new String[]{"201710301515104040", "2017103020201066", "201710302121104545", "201710311414104242", "201710311616102020", "2017103118181033", "201711021212112626", "201711021919112020", "201711031212111818", "201711031414113636", "201711041010115858", "201711041313111313", "201711041414111818", "201711051212114040", "201711051616114747", "201711061010112929", "201711061212111414", "2017110714141144", "201711081111112626", "2017110899114848", "2017110911111144", "201711091313111313", "201711101010113333", "201711101313113939", "201711101414114141", "201711101414115858", "201711101919113939", "201711101919115353", "201711102020113131", "201711121212112727", "201711121313111515", "201711121515113636", "201711121515115757", "2017111217171199", "201711121818112525", "201711121919111616", "201711121919113535", "2017111313131177", "201711131616113131", "201711132020111414", "201711132020112020", "201711141111114949", "201711141616115555", "201711151313111616", "201711151616114040", "201711151818115151", "201711171515112424", "201711171717115555", "201711181010114949", "2017111815151188", "2017111816161100", "201711181616112020", "201711181616113333", "201711181717115252", "201711191717113030", "201711201010111414", "201711201212113737", "201711201313113636", "2017112014141111", "201711201414112020", "201711201616115454", "201711211515112626", "201711211818115151", "201711221010111515", "201711221111113131", "2017112299113333", "201711241010112323", "201711241111113434", "201711241212114545", "201711241212114646", "201711241313113636", "201711241313113737", "201711241414111717", "201711241414112828", "201711241616112424", "201711241616114343", "201711241818114848", "201711241919114848", "2017112420201177", "201711251212114747", "201711251414111010", "201711251616112626", "2017112519191188", "201711261111114141", "201711261111114646", "201711261515115454", "201711291414111313", "201711291919112121", "201712011212124747", "2017120114141233", "201712011414123838", "201712021414125555", "201712021515125050", "201712021515125151", "201712021616121212", "201712021616121313", "201712021919125757", "201712031414124343", "201712031515121212", "201712031616124444", "201712031818121616", "2017120414141222", "201712042020125252", "201712051313122323", "201712051414124545", "201712051515124949", "201712051919122525", "201712071313123939", "201712071414124545", "201712071515122121", "201712081010121717", "2017120811111222", "201712081717121818", "201712091212124545", "2017120913131200", "201712091717123232", "2017121014141244", "2017121015151211", "2017121015151255", "201712131111125757", "201712131212125050", "201712131313121616", "201712131414124747", "201712132020124444", "201712141111124848", "201712141414124040", "201712141717123737", "2017121417171299", "201712141919124444", "201712151111125555", "201712151212123131", "201712151313123737", "201712151717123333", "201712151818123535", "201712151919124040", "201712152020124646", "201712161313125050", "201712161313125757", "201712161616123737", "2017121619191200", "201712171313121010", "201712171414122323", "201712171414122929", "2017121718181299", "201712181414121616", "201712191111121313", "201712191515123535", "2017121917171277", "201712191818123838", "201712201010123535", "2017122011111299", "201712201919122525", "201712211313125151", "201712211414124949", "201712211616122121", "201712211919123131", "2017122120201200", "201712212020123333", "201712221111123232", "201712221414122424", "201712221717121717", "201712221717123131", "2017122317171288", "201712271212125656", "201712272020124242", "201712281010125252", "201712281616121414", "201712291717122020", "201712301010121313", "201712301717122929", "201801021212011616", "201801021212012828", "201801021212015959", "201801021616014040", "201801021818013939", "201801031717012020", "2018010414140122", "201801041515011515", "2018010420200100", "201801051313012929", "201801051616014242", "201801051616014444", "2018010518180188", "201801051919013737", "201801052020014343", "201801071414015555", "2018010714140177", "201801111313012929", "201801111414015555", "201801121212013131", "201801141414011111", "2018011418180111", "2018011514140111", "2018011516160111", "2018011612120188", "201801161414011313", "201801171616011414", "201801171818012929", "2018011719190199", "2018011812120122", "201801181616012525", "201801191515014949", "2018012418180166", "201801242020014141", "2018012513130100", "201801261414012121", "2018012710100166", "201801271515014545", "201801271818012929", "201801281111014343", "201801281414014545", "201801291111015757", "201801291818014545", "2018013013130199", "201801301414015757", "201801302020011111", "201802011515023131", "201802011919021414", "2018020214140222", "201802031111021616", "201802031111021919", "201802031212022727", "201802031616024242", "201802071212025151", "201802071313022828", "201802071414025050", "201802071515025050", "201802081818021212", "201802091212021515", "201802101111024141", "201802131818021010", "201802141111023535", "201802161919021111", "2018021619190266", "201802171818021010", "201802171818024949", "201802181111021212", "201802181818023131", "201802191313021717", "2018022111110277", "201802241616024242", "201802241818024141", "201802251414025959", "201802251717021818", "201802261818022020", "201802261919025858", "2018022712120255", "201802271414025151", "201802271515022727", "201802281010022828", "201802281313025656", "201803011313032222", "201803031414034242", "201803041313033131", "2018030914140300", "20180309990388", "201803101616031919", "2018031017170399", "2018031218180311", "201803131212034141", "201803151212035454", "201803161717032828", "2018031911110333", "201803212020032525", "201803231010031616", "201803241212033636", "201803242020031919", "201803251212034141", "201803271111034444", "201803281818035656", "201803281919034848", "201803291414031717", "201803301111035050", "201803301717032323", "201804041515042727", "201804051111045151", "201804061313044040", "2018040615150499", "201804101212041313", "201804101212045858", "201804101313041111", "201804101313044747", "201804132020042727", "201804161313045656", "201804161919042424", "201804171717041212", "201804171717044343", "201804191313041717", "2018041914140499", "201804191919041515", "201804201717042929", "201804211313041313", "201804231212043939", "2018042320200400", "201804261212043333", "201804261414045757", "2018042618180488", "201804271010041616", "2018042899045252", "201805021414054848", "2018050299053939", "201805041919055454", "201805061212051010", "201805061313053939", "2018050915150599", "201805092020055959", "201805101111053131", "201805121313055353", "201805121515055858", "201805121717055858", "201805141111052828", "201805141717052727", "201805171212053737", "201805171818054848", "2018051915150511", "2018051918180511", "201805251818055050", "201805261414054141", "201805271414054242", "201805291111051919", "201805291111054141", "201805291414052323", "201806011818061616", "201806021010065151", "201806041515061919", "2018060617170666", "201806111111061515", "201806121414061212", "201806151313064646", "201806191414063232", "201806202020062222", "201806211414063232", "201806231717065050", "201806251717062020", "201806251919065959", "201806261313061313", "201806301111065353", "201807011313071818", "201807011717072121", "201807051919071111", "20180706990733", "201807071212071111", "201807071212074343", "201807071212075858", "201807071313072727", "201807071515073535", "201807071515073636", "201807071616071717", "201807071717075959", "2018070799071717", "201807091313075252", "201807101616072222", "201807111212073333", "201807111616075656", "201807121010072929", "201807121313073333", "201807131010073939", "201807131313074040", "201807131515075959", "201807131818075252", "201807141515074848", "2018071416160777", "2018071615150788", "2018071711110744", "201807171313071717", "201807171919075252", "2018071812120711", "2018071812120799", "201807181717071111", "201807181717071515", "201807181818075151", "2018071911110711", "201807201212072020", "201807201818075858", "2018072020200711", "201807211313073838", "201807211616072323", "201807231212071212", "201807231919074040", "201807241414071212", "201807241414074242", "201807241616072323", "2018072699073939", "201807271313074444", "201807271616072424", "201807271919075656", "201807281212073535", "201807281717073939", "201808011919081212", "201808021212085353", "201808021616083838", "201808021919081717", "201808022020081414", "201808061111085050", "201808061717084949", "201808071818081212", "2018080799082626", "2018080819190822", "201808101414083636", "201808101515084242", "201808101717083636", "2018081117170899", "201808131313083636", "201808131414083838", "201808141616081313", "201808171212082222", "201808171313084040", "2018081718180877", "201808181111085959", "2018082311110855", "2018082499081212", "201808251111082727", "201808251212085252", "201808251414081414", "201808271010081010", "201808271111081212", "201808271414083030", "201808291616083535", "201808301111083737", "201808301515081212", "201808302020083232", "2018083117170877", "201809011212094343", "2018090115150911", "201809021515093636", "201809031212095050", "201809051414095353", "201809051616092626", "201809051818094343", "201809061313095858", "201809071818094646", "201809081212093636", "201809081818094848", "2018091016160900", "201809102020091313", "2018091211110922", "201809121212094646", "201809121919093131", "201809131313094747", "201809131515095757", "201809131717095050", "2018091410100966", "201809141212092525", "201809141616095151", "201809141818095353", "201809151414092929", "201809151717091818", "201809171111095656", "2018091720200999"};

        for (String script : scripts) {
            dss = dss + script+";";
            File file = new File("d:\\Jacadi\\log_PCL\\ALL_TRANS.txt");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            Element HeadElmnt = null;
            NodeList bott = doc.getElementsByTagName("pcbPacket");
            for (int s = 0; s < bott.getLength(); s++) {

                Node fstNode = bott.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    HeadElmnt = (Element) fstNode;
                    NodeList fstNmElmntLst = HeadElmnt.getElementsByTagName("receiptTime");
                    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                    NodeList fstNm = fstNmElmnt.getChildNodes();
                    data = (((Node) fstNm.item(0)).getNodeValue());
                    datatrans = LocalDateTime.parse(data, f);
                    //--------------

                    //dataTran = par
                    int r = ldt.compareTo(datatrans);
                    if (r < 0) {
                        liczb++;

                        ////=============4
                        NodeList nodeLst2 = HeadElmnt.getElementsByTagName("items");

                        //System.out.println("Information of all employees");
                        //System.out.println(nodeLst.getLength());
                        //LineCount = nodeLst.getLength();
                        //System.out.println(nodeLst2.getLength());
                        for (int sr = 0; sr < nodeLst2.getLength(); sr++) {

                            Node fstNode2 = nodeLst2.item(sr);

                            if (fstNode2.getNodeType() == Node.ELEMENT_NODE) {

                                Element fstElmnt = (Element) fstNode2;

                                NodeList fstNmElmntLst2 = fstElmnt.getElementsByTagName("discount");
                                //System.out.println( fstNmElmntLst2.item(0).getAttributes().getNamedItem("code"));
                                if (fstNmElmntLst2.getLength() > 0) {
                                    System.out.println("--------------------------");
                                    String lstCupCode = "";
                                    Double cupNOm = 0.0;
                                    for (int i = 0; i < fstNmElmntLst2.getLength(); i++) {
                                        Element lstNmElmnt = (Element) fstNmElmntLst2.item(i);
                                        //System.out.println("\nCurrent Element :" + lstNmElmnt.getNodeName());
                                        System.out.println(lstNmElmnt.getAttribute("src"));
                                        System.out.println(lstNmElmnt.getAttribute("code"));
                                        System.out.println(lstNmElmnt.getAttribute("points"));
                                        System.out.println(lstNmElmnt.getAttribute("nom"));
                                        cupCode = lstNmElmnt.getAttribute("code");
                                        cupNOm = cupNOm + Double.valueOf(lstNmElmnt.getAttribute("nom"));
                                               
                                        
                                        if (!lstCupCode.equals(cupCode) && lstCupCode.length()> 1) {
                                            cupCode = cupCode+","+lstCupCode;
                                               
                                        }
                                        lstCupCode = lstNmElmnt.getAttribute("code");
                                    }

                                    NodeList nodeCard = HeadElmnt.getElementsByTagName("cardCode");
                                    Element fstElmntCard = (Element) nodeCard.item(0);
                                    NodeList fstCard = fstElmntCard.getChildNodes();
                                    System.out.println(fstCard.item(0).getNodeValue());
                                    /*
                                    Element lstNmElmnt = (Element) fstNmElmntLst2.item(0);
                                    //System.out.println("\nCurrent Element :" + lstNmElmnt.getNodeName());
                                    System.out.println(lstNmElmnt.getAttribute("src"));
                                    System.out.println(lstNmElmnt.getAttribute("code"));
                                    System.out.println(lstNmElmnt.getAttribute("points"));
                                    System.out.println(lstNmElmnt.getAttribute("nom"));*/
                                    System.out.println("=================================");
                                    if (fstCard.item(0).getNodeValue().equals(script)) {
                                        dss = dss  + cupCode + "," + data + "," + cupNOm.toString();
                                        sumCup = sumCup+cupNOm;
                                    }
                                    /*
                        System.out.println(((Node) lstNm2.item(0)).getAttributes());
                                
                                    Element fstNmElmnt2 = (Element) fstNmElmntLst2.item(0);
                                    NodeList fstNmElmntLst3 =   fstNmElmnt2.getElementsByTagName("discount");
                                    Element fstNmElmnt3 = (Element) fstNmElmntLst3.item(0);

                                    NodeList lstNm = fstNmElmnt3.getChildNodes();
                                    System.out.println(lstNm.item(0).getAttributes());   
                                    System.out.println(((Node) lstNm.item(0)).getNodeValue());
                                    
                                
                            
                                //System.out.println((((Node) fstNm2.item(0)).getNodeValue()));

                                //String cupon = (((Node) fstNm.item(0)).getNodeValue());
                                System.out.println(r + " : " + datatrans.toString());
                                     */
                                }
                            }

                        }
                    }

                }

                //--------------------
                data = "";
            }
            System.out.println(liczb + " -  record");
            dss = dss + ";" +sumCup  +"\n";
            sumCup = 0.0;
        }
        PrintWriter out3;
        out3 = new PrintWriter("d:\\Jacadi\\log_PCL\\transakcje.txt");
        ///sb.append("</pcb>");
        out3.print(dss);

        out3.flush();
        
        System.out.println(" Koniec przrtwarzania ");
        //String dss;
    }

}
