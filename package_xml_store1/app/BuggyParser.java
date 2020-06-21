import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BuggyParser extends DefaultHandler {

    boolean stocks = false;
    boolean productID = false;

    String productID_content = "";

    @Override
    public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("stocks")) {
            stocks = true;
        }
        else {
            if (qName.equalsIgnoreCase("productID")) {
                productID = true;
            } else {
                productID_content += "<" + qName + ">";
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("stocks")) {
            stocks = false;
        }
        else {
            if (qName.equalsIgnoreCase("productID")) {
                productID = false;
            } else {
                productID_content += "</" + qName + ">";
            }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (productID) {
            productID_content += new String(ch, start, length);
        } else {
            if (stocks)
                stocks = false;
        }
    }

    public String getParsed() { return productID_content; }

}