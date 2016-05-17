package edu.uoc.reader;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.TextImpl;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextSoftPageBreakElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by mvelezm on 14/04/16.
 */
public class ODTParser extends ODFParser {
    private static final Logger log= Logger.getLogger( ODTParser.class.getName());
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

    public ODTParser(OdfDocument document) throws Exception {
        OdfContentDom content;
        text="";
        content = document.getContentDom();
        NodeList list = content.getRootElement().getElementsByTagName("office:text");
        String lastStyle = "";

        for (int j = 0; j<list.item(0).getChildNodes().getLength(); j++){
            Node singleNode = list.item(0).getChildNodes().item(j);
            if ( singleNode instanceof OdfTextParagraph){
                if (lastStyle.equals("")){
                    lastStyle = ((OdfTextParagraph) singleNode).getStyleName();
                }
                else if(!lastStyle.equals(((OdfTextParagraph) singleNode).getStyleName()))
                    text = text + "PAGE-BREAK-MARK";
            }

            text = text + this.recurse(list.item(0).getChildNodes().item(j));
        }
        textSplitted = this.text.replaceAll("(.{0,"+ 100+"})\\b", "$1\n").split("\n");

        System.out.println(text);
    }

    public String recurse(Node node) {
        System.out.println(node.getClass());
        String innerText = "";
        final String ENDLN = "\n";

        if (node instanceof TextImpl) {
            innerText = innerText + node.getTextContent() + ENDLN;
        }

        else if(node instanceof TextSoftPageBreakElement) {
            innerText = innerText + "PAGE-BREAK-MARK";
        }

        else if (node instanceof TableTableElement) {
            innerText = innerText + "TABLE ";
        }
        else if (node instanceof TableTableHeaderRowsElement)
            innerText = innerText + "HEADER: ";
        else if (node instanceof TableTableRowElement)
            innerText = innerText + "ROW ";
        else if (node instanceof TableTableCellElement)
            innerText = innerText + "CELL ";
        if (node.hasChildNodes()) {
            for (int j = 0; j<node.getChildNodes().getLength(); j++){
                innerText = innerText + this.recurse(node.getChildNodes().item(j));
            }
        }
        return innerText;
    }
}
