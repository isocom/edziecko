package name.prokop.bart.gae.edziecko.tests;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class Import23 {

    private static Map<String, String> map() throws Exception {
        HashMap<String, String> retVal = new HashMap<String, String>();

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        XPath xpath = XPathFactory.newInstance().newXPath();

        Document document = docBuilder.parse(Import23.class.getResourceAsStream("/dbCards.xml"));
        NodeList entries = (NodeList) xpath.evaluate("/tibboCards/card", document, XPathConstants.NODESET);

        for (int i = 0; i < entries.getLength(); i++) {
            Node entry = entries.item(i);
            String posI = (String) xpath.evaluate("@pos", entry, XPathConstants.STRING);
            String serialNumber = (String) xpath.evaluate("serialNumber", entry, XPathConstants.STRING);
            serialNumber = "M1" + serialNumber;
//            System.out.println(pos + " : " + serialNumber);
            retVal.put(posI, serialNumber);
        }
        return retVal;
    }

    private static void x() throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        XPath xpath = XPathFactory.newInstance().newXPath();

        Document document = docBuilder.parse(Import23.class.getResourceAsStream("/dbCardLog.xml"));
        NodeList entries = (NodeList) xpath.evaluate("/tibboCardLog/entry", document, XPathConstants.NODESET);

        Map<String, String> map = map();
        System.out.println(map);

        Set<JSONObject> requests = new LinkedHashSet<JSONObject>();
        int recordCount = 0;

        JSONArray log = new JSONArray();

        for (int i = 0; i < entries.getLength(); i++) {
            Node entry = entries.item(i);
            String cardRefS = (String) xpath.evaluate("cardRef", entry, XPathConstants.STRING);
            String timestampS = (String) xpath.evaluate("timestamp", entry, XPathConstants.STRING);

            String card = map.get(cardRefS);
            if (card == null) {
                System.err.println("nieznana...");
                continue;
            }
            Date date = DateToolbox.parseDateChecked("yyyy-M-d H:m:s", timestampS);

            JSONObject logEntry = new JSONObject();
            logEntry.putOpt("t", date.getTime());
            logEntry.putOpt("c", card);
            System.err.println(date + " : " + date.getTime());
            log.put(logEntry);
            recordCount++;

            if (recordCount++ % 25 == 0) {
                JSONObject database = new JSONObject();
                database.putOpt("log", log);
                requests.add(database);
                log = new JSONArray();
            }
        }
        System.out.println(log);

        for (JSONObject jo : requests) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("requestType", "UploadDatabase");
            params.put("przedszkoleId", "124761");
            params.put("tibboDatabase", jo.toString());
            System.out.println(jo);
            String response = PostClient.postClient("http://e-dziecko.appspot.com/appletsrv", params);
            System.out.println(response);
        }
    }

    public static void main(String... args) throws Exception {
        x();
    }
}
