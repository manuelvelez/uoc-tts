import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.*;
/**
 * Created by mvelez on 14/03/16.
 */
public class Reader {


    public static void main(String[] args) {
        Config setup = new Config("config/config.xml");

        System.out.println(setup.toString());

        ODFParser parser = new ODFParser("formula.odt");
        //System.out.println(parser.toString());

        System.out.println(parser.getText());

        String test[] = parser.getTextSplitted();
        for (String s : test) {
            System.out.println(s.length());
        }

        String language = "ES";
        String text = "Vane bonita";
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            new OnLineTTS().generateAudio(language, text);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
