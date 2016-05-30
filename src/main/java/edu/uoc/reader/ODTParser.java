package edu.uoc.reader;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.TextImpl;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.number.NumberTextStyleElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.*;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by mvelezm on 14/04/16.
 */
public class ODTParser extends ODFParser {
    private static final Logger log= Logger.getLogger( ODTParser.class.getName());
    private static final String BREAK_MARK = "PAGE-BREAK-MARK";
    private String documentName;
    private OdfDocument odfDocument;
    private String text;
    private String[] textSplitted;
    private List<String> styleList;

    private static final Map<String, String> tableMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ES", "tabla, ");
        aMap.put("EN", "table, ");
        aMap.put("CA", "taula, ");
        tableMap = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, String> rowMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ES", "fila, ");
        aMap.put("EN", "row, ");
        aMap.put("CA", "fila, ");
        rowMap = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, String> cellMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ES", "celda, ");
        aMap.put("EN", "cell, ");
        aMap.put("CA", "cel·la, ");
        cellMap = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, String> headerMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ES", "cabecera, ");
        aMap.put("EN", "header, ");
        aMap.put("CA", "capçalera, ");
        headerMap = Collections.unmodifiableMap(aMap);
    }

    private String languageDependentTableName;
    private String languageDependentRowName;
    private String languageDependentCellName;
    private String languageDependentHeaderName;

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

    public ODTParser(OdfDocument document, String language) throws Exception {
        OdfContentDom content;
        text="";
        content = document.getContentDom();

        String documentLanguage = language;
        if(!rowMap.containsKey(language))
            documentLanguage="EN";

        this.languageDependentTableName=tableMap.get(documentLanguage);
        this.languageDependentRowName=rowMap.get(documentLanguage);
        this.languageDependentCellName=cellMap.get(documentLanguage);
        this.languageDependentHeaderName=headerMap.get(documentLanguage);

        List<String> styleList = new ArrayList<String>();
        NodeList styles= content.getElementsByTagName("style:paragraph-properties");
        System.out.println(styles.getLength());
        for (int j = 0; j<styles.getLength(); j++){
            Node singleNode = styles.item(j);
            if (singleNode.getAttributes().getNamedItem("fo:break-before") != null ){
                styleList.add(singleNode.getParentNode().getAttributes().getNamedItem("style:name").getNodeValue());
            }
        }
        this.styleList = styleList;

        NodeList list = content.getRootElement().getElementsByTagName("office:text");
        String lastStyle = "";

        for (int j = 0; j<list.item(0).getChildNodes().getLength(); j++){
            Node singleNode = list.item(0).getChildNodes().item(j);
            text = text + this.recurse(list.item(0).getChildNodes().item(j));
        }
        textSplitted = this.text.replaceAll("(.{0,"+ 100+"})\\b", "$1\n").split("\n");
        System.out.println(text);
    }

    public String recurse(Node node) {
        StringBuilder innerText = new StringBuilder("");
        final String ENDLN = "\n";

        if (node instanceof TextIndexBodyElement) {
            for (int j = 0; j<node.getChildNodes().getLength(); j++){
                innerText.append(this.recurse(node.getChildNodes().item(j)));
            }
            innerText.append(BREAK_MARK);
        }

        else if ((node instanceof OdfTextHeading || node instanceof OdfTextParagraph) && this.styleList.contains(node.getAttributes().getNamedItem("text:style-name").getNodeValue()))
            innerText.append(BREAK_MARK);

        else if (node instanceof TextImpl){
            innerText.append(node.getTextContent() + "." + ENDLN);
        }

        else if (node instanceof TextAElement)
            innerText.append(ENDLN);
        else if (node instanceof TextTabElement)
            innerText.append(ENDLN);

        else if(node instanceof TextLineBreakElement)
            innerText.append(ENDLN);

        else if(node instanceof TextSoftPageBreakElement)
            innerText.append(BREAK_MARK);

        else if (node instanceof TableTableElement)
            innerText.append(this.languageDependentTableName);
        else if (node instanceof TableTableHeaderRowsElement)
            innerText.append(this.languageDependentHeaderName);
        else if (node instanceof TableTableRowElement)
            innerText.append(ENDLN + this.languageDependentRowName);
        else if (node instanceof TableTableCellElement)
            innerText.append(this.languageDependentCellName);

        if (node.hasChildNodes() && !(node instanceof TextIndexBodyElement)) {
            for (int j = 0; j<node.getChildNodes().getLength(); j++){
                innerText.append(this.recurse(node.getChildNodes().item(j)));
            }
        }
        return innerText.toString();
    }
}
