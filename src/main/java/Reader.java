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


//        ODFParser parser = new ODFParser("formula.odt");

//      TTS.Language language = TTS.Language.valueOf("EN".toUpperCase());
        String text = "hello, i'm the man";
//        try {
//            text = URLEncoder.encode(text, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        try {
//            new TTS().go(language, text);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }
}
