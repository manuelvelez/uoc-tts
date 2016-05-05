package edu.uoc.reader;
/**
 * Created by mvelezm on 23/03/16.
 */

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class Config {
    private String configFile;

    private String ttsServiceUrl;
    private Boolean isOnline;
    private String language;

    private String splitMode;
    private String alternatives;
    private String outputAudioPath;
    private String outputAudioPattern;

    private final String xsdPath = "./config/reader.xsd";

    private Boolean validate(){
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(this.configFile)));
        } catch (SAXException e) {
            System.out.println("Exception: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String toString() {
        return "Config{" +
                "configFile='" + configFile + '\'' +
                ", ttsServiceUrl='" + ttsServiceUrl + '\'' +
                ", isOnline='" + isOnline + '\'' +
                ", language='" + language + '\'' +
                ", splitMode='" + splitMode + '\'' +
                ", alternatives='" + alternatives + '\'' +
                ", outputAudioPath='" + outputAudioPath + '\'' +
                ", outputAudioPattern='" + outputAudioPattern + '\'' +
                ", xsdPath='" + xsdPath + '\'' +
                '}';
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getTtsServiceUrl() {
        return ttsServiceUrl;
    }

    public void setTtsServiceUrl(String ttsServiceUrl) {
        this.ttsServiceUrl = ttsServiceUrl;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSplitMode() {
        return splitMode;
    }

    public void setSplitMode(String splitMode) {
        this.splitMode = splitMode;
    }

    public String getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(String alternatives) {
        this.alternatives = alternatives;
    }

    public String getOutputAudioPath() {
        return outputAudioPath;
    }

    public void setOutputAudioPath(String outputAudioPath) {
        this.outputAudioPath = outputAudioPath;
    }

    public String getOutputAudioPattern() {
        return outputAudioPattern;
    }

    public void setOutputAudioPattern(String outputAudioPattern) {
        this.outputAudioPattern = outputAudioPattern;
    }

    public String getXsdPath() {
        return xsdPath;
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

        if (this.validate() == true)
        {
            Element elConfig = configDocument.getRootElement();
            Element ttsElement = elConfig.getChild("tts");
            Element readerElement = elConfig.getChild("reader");

            //Elements from tts node
            this.setTtsServiceUrl(ttsElement.getChildText("url"));
            this.setIsOnline(Boolean.valueOf(ttsElement.getChildText("online")));
            this.setLanguage(ttsElement.getChildText("language"));


            //Elements form reader node
            this.setSplitMode(readerElement.getChildText("split-by"));
            this.setAlternatives(readerElement.getChildText("alternatives"));
            this.setOutputAudioPath(readerElement.getChildText("output-path"));
            this.setOutputAudioPattern(readerElement.getChildText("output-pattern"));

            System.out.println(this.toString());
        }
        else{
            System.exit(1);
        }

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
}
