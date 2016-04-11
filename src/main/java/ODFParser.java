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
    private String text;
    private String[] textSplitted;

    public String[] getTextSplitted() {
        return textSplitted;
    }

    public void setTextSplitted(String[] textSplitted) {
        this.textSplitted = textSplitted;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {

        return "ODFParser{" +
                "documentName='" + documentName + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

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
        text = this.recurse(list.item(0));

        textSplitted = this.text.replaceAll("(.{0,"+ 100+"})\\b", "$1\n").split("\n");
    }

    public String recurse(Node node){
        String innerText = "";
        final String ENDLN = "\n";
        //System.out.println(node.getClass());
        if(node instanceof OdfTextParagraph){
            System.out.println(node.getTextContent());
            innerText = innerText + node.getTextContent() + ENDLN;
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
            innerText = innerText + recurse(currentNode);
        }
        //System.out.println(text);
        return innerText;
    }

}
