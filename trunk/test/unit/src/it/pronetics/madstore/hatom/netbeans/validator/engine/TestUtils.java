package it.pronetics.madstore.hatom.netbeans.validator.engine;

import java.io.*;
import java.util.List;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Convenience methods used in tests.
 * 
 * @author Andrea Castello
 * @version 1.1
 */
public class TestUtils {

    public static Document stringToDom(String xmlSource) 
            throws SAXException, ParserConfigurationException, IOException {
     
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
    
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    
    }
    
    public static void dumpReports(List<Report> reportList){
        for (Report report : reportList) {
            System.out.println("-----------------------------");
            System.out.println(report.asString());
            System.out.println("-----------------------------");
        }
    }    
}