import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by mvelezm on 23/03/16.
 */
public class ODFParser {
    private String documentName;
    private OdfDocument odfDocument;
    public ODFParser(String documentName) {
        this.documentName = documentName;

        try {
            this.odfDocument = OdfDocument.loadDocument(this.documentName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        OdfContentDom content = null;
        try {
            content = this.odfDocument.getContentDom();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NodeList list = content.getRootElement().getElementsByTagName("office:text");
        this.recurse(list.item(0));
    }

    public static void recurse(Node node){
        String text = "";
        //System.out.println(node.getClass());
        if(node instanceof OdfTextParagraph){
            System.out.println(node.getTextContent());
            text = text + node.getTextContent();
        }


        else if(node instanceof OdfDrawFrame){
            System.out.println(((OdfDrawFrame) node).getFirstChild().getClass());
        }

        else if(node instanceof DrawObjectElement){
            System.out.println("link: " + ((DrawObjectElement) node).getXlinkHrefAttribute());
        }
        else if(node instanceof OdfDrawImage){
            System.out.println(((OdfDrawImage) node).getImageUri());
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            recurse(currentNode);
        }
    }
}
