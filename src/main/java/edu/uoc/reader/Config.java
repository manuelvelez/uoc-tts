package edu.uoc.reader;
/**
 * Created by mvelezm on 23/03/16.
 */

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;

public class Config {
    private String ttsServiceUrl;
    private String configFile;
    private String outputAudioPattern;
    private String splitMode;

    @Override
    public String toString() {
        return "Config{" +
                "ttsServiceUrl='" + ttsServiceUrl + '\'' +
                ", configFile='" + configFile + '\'' +
                ", outputAudioPattern='" + outputAudioPattern + '\'' +
                ", splitMode='" + splitMode + '\'' +
                '}';
    }

    public Config(String configFile) {
        this.configFile = configFile;
        System.out.println(this.configFile);
        Document configDocument = null;
        try {
            configDocument = useDOMParser(configFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element elConfig = configDocument.getRootElement();
        Element ttsElement = elConfig.getChild("tts");
        Element readerElement = elConfig.getChild("reader");

        String ttsServiceUrl = ttsElement.getChildText("url");

        this.setTtsServiceUrl(ttsServiceUrl);

        String outputAudioPattern = readerElement.getChildText("output-pattern");
        String splitMode = readerElement.getChild("split-mode").getText();

        this.setOutputAudioPattern(outputAudioPattern);
        this.setSplitMode(splitMode);
    }

    //Get JDOM document from DOM Parser
    private static org.jdom2.Document useDOMParser(String fileName)
            throws ParserConfigurationException, SAXException, IOException {
        //creating DOM Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(new File(fileName));
        DOMBuilder domBuilder = new DOMBuilder();
        return domBuilder.build(doc);

    }

    public String getSplitMode() {
        return splitMode;
    }

    public String getOutputAudioPattern() {
        return outputAudioPattern;
    }

    public void setSplitMode(String splitMode) {
        this.splitMode = splitMode;
    }
    public void setOutputAudioPattern(String outputAudioPattern) {
        this.outputAudioPattern = outputAudioPattern;
    }

    public String getTtsServiceUrl() {

        return ttsServiceUrl;
    }

    public void setTtsServiceUrl(String ttsServiceUrl) {
        this.ttsServiceUrl = ttsServiceUrl;
    }
}
