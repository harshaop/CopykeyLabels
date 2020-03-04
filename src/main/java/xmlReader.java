import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class xmlReader {
    private final static Logger log = LoggerFactory.getLogger(CopyKeyValidation.class);

    public static LinkedHashMap<String, String> ReadXML(String xml) throws IOException, SAXException, ParserConfigurationException {
        String path = getPath();
        File fXmlFile = new File(path + "\\GOEP\\Online-R20B\\import\\" + xml);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();
        log.info("Root element :" + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("translation");
        LinkedHashMap<String, String> hmap = new LinkedHashMap<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                if (eElement.getAttribute("system").equals("hybris") & eElement.getAttribute("subsystem").equals("frontend")) {
                    log.debug("key : " + eElement.getAttribute("key"));
                    log.debug("Translation : " + eElement.getTextContent());
                    String translation = eElement.getTextContent().replaceAll("[\r\n]", "");
                    hmap.put(eElement.getAttribute("key"), translation);
                }
            }
        }
        return hmap;
    }

    @NotNull
    private static String getPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString() + "\\";
    }
}
