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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manuel Vélez on 14/04/16.
 * Concrete implementation of ODFParser class for Presentation files
 */
public class ODPParser extends ODFParser {
    private static final Logger log= Logger.getLogger( ODSParser.class.getName());
    private String documentName;
    private OdfDocument odfDocument;
    private String text = "";
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

    private static final String PAGE_BREAK_MARK = "PAGE-BREAK-MARK";

    //Static map with the word for table in each supported language
    private static final Map<String, String> tableMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ES", "tabla, ");
        aMap.put("EN", "table, ");
        aMap.put("CA", "taula, ");
        tableMap = Collections.unmodifiableMap(aMap);
    }

    //Static map with the word for row in each supported language
    private static final Map<String, String> rowMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ES", "fila, ");
        aMap.put("EN", "row, ");
        aMap.put("CA", "fila, ");
        rowMap = Collections.unmodifiableMap(aMap);
    }

    //Static map with the word for cell in each supported language
    private static final Map<String, String> cellMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ES", "celda, ");
        aMap.put("EN", "cell, ");
        aMap.put("CA", "cel·la, ");
        cellMap = Collections.unmodifiableMap(aMap);
    }

    //Static map with the word for header in each supported language
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

    @Override
    public String toString() {

        return "ODFParser{" +
                "documentName='" + documentName + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public ODPParser(OdfDocument document, String language) throws Exception {
        OdfContentDom content = null;

        content = document.getContentDom();

        String documentLanguage = language;
        if(!rowMap.containsKey(language))
            documentLanguage="EN";

        this.languageDependentTableName=tableMap.get(documentLanguage);
        this.languageDependentRowName=rowMap.get(documentLanguage);
        this.languageDependentCellName=cellMap.get(documentLanguage);
        this.languageDependentHeaderName=headerMap.get(documentLanguage);

        NodeList list = content.getRootElement().getElementsByTagName("draw:page");
        String lastStyle = "";

        for (int k = 0; k < list.getLength(); k ++){
            text = text + this.recurse(list.item(k)) + PAGE_BREAK_MARK;
        }
        textSplitted = this.text.replaceAll("(.{0,"+ 100+"})\\b", "$1\n").split("\n");
    }

    public String recurse(Node node) {
        StringBuilder innerText = new StringBuilder("");
        final String ENDLN = "\n";

        if (node instanceof TextImpl) {
            innerText.append(node.getTextContent() + ENDLN);
        }

        else if(node instanceof TextSoftPageBreakElement) {
            innerText.append(PAGE_BREAK_MARK);
        }

        else if (node instanceof TableTableElement)
            innerText.append(this.languageDependentTableName);
        else if (node instanceof TableTableHeaderRowsElement)
            innerText.append(this.languageDependentHeaderName);
        else if (node instanceof TableTableRowElement)
            innerText.append(ENDLN + this.languageDependentRowName);
        else if (node instanceof TableTableCellElement)
            innerText.append(this.languageDependentCellName);

        if (node.hasChildNodes()) {
            for (int j = 0; j<node.getChildNodes().getLength(); j++){
                innerText.append(this.recurse(node.getChildNodes().item(j)));
            }
        }
        return innerText.toString();
    }
}