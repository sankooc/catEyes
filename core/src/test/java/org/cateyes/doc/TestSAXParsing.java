package org.cateyes.doc;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

// JAXP
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

// SAX
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TestSAXParsing {
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println ("Usage: java TestSAXParsing [filename]");
                System.exit (1);
            }
            // Get SAX Parser Factory
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // Turn on validation, and turn off namespaces
            factory.setValidating(true);
            factory.setNamespaceAware(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(new File(args[0]), new MyHandler());
        } catch (ParserConfigurationException e) {
            System.out.println("The underlying parser does not support " +
                               " the requested features.");
        } catch (FactoryConfigurationError e) {
            System.out.println("Error occurred obtaining SAX Parser Factory.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MyHandler extends DefaultHandler {
    // SAX callback implementations from DocumentHandler, ErrorHandler, etc.

    private Writer out;

    public MyHandler() throws SAXException {
        try {
            out = new OutputStreamWriter(System.out, "UTF8");
        } catch (IOException e) {
            throw new SAXException("Error getting output handle.", e);
        }
    }

    public void startDocument() throws SAXException {
        print("<?xml version=\"1.0\"?>\n");
    }

    public void startElement(String uri, String localName,
                             String qName, Attributes atts)
        throws SAXException {

        print("<" + qName);
        if (atts != null) {
            for (int i=0, len = atts.getLength(); i<len; i++) {
                print(" " + atts.getQName(i) + 
                      "=\"" + atts.getValue(i) + "\"");
            }
        }
        print(">");
    }

    public void endElement(String uri, String localName, 
                           String qName) throws SAXException {
        print("</" + qName + ">\n");
    }

    public void characters(char[] ch, int start, int len) throws SAXException {
        print(new String(ch, start, len));
    }

    private void print(String s) throws SAXException {
        try {
            out.write(s);
            out.flush();
        } catch (IOException e) {
            throw new SAXException("IO Error Occurred.", e);
        }
    }
}