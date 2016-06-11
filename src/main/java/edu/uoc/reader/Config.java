package edu.uoc.reader;
/**
 * Created by Manuel Vélez on 23/03/16.
 * This class manages the configuration used in the app running
 */
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
    private static final Logger log= Logger.getLogger( Config.class.getName());
    private String configFile;

    private String ttsServiceUrl;
    private Boolean isOnline;
    private String language;

    private String splitMode;
    private Boolean alternatives;
    private String outputAudioPath;
    private String outputAudioPattern;

    private final String xsdPath = "./config/reader.xsd";

    private void validate() throws IOException, SAXException {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(this.configFile)));
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

    /**
     * configFile getter
     * @return
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * configFile setter
     * @param configFile
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    /**
     * ttsServiceUrl getter
     * @return
     */
    public String getTtsServiceUrl() {
        return ttsServiceUrl;
    }

    /**
     * ttsServiceUrl setter
     * @param ttsServiceUrl
     */
    public void setTtsServiceUrl(String ttsServiceUrl) {
        this.ttsServiceUrl = ttsServiceUrl;
    }

    /**
     * isOnline getter
     * @return
     */
    public Boolean getIsOnline() {
        return isOnline;
    }

    /**
     * isOnline setter
     * @param isOnline
     */
    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * language getter
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * language setter
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * splitMode getter
     * @return
     */
    public String getSplitMode() {
        return splitMode;
    }

    /**
     * splitMode setter
     * @param splitMode
     */
    public void setSplitMode(String splitMode) {
        this.splitMode = splitMode;
    }

    /**
     * alternatives getter
     * @return
     */
    public Boolean getAlternatives() {
        return alternatives;
    }

    /**
     * Alternatives setter
     * @param alternatives
     */
    public void setAlternatives(Boolean alternatives) {
        this.alternatives = alternatives;
    }

    /**
     * outputAudioPath getter
     * @return
     */
    public String getOutputAudioPath() {
        return outputAudioPath;
    }

    /**
     * outputAudioPath setter
     * @param outputAudioPath
     */
    public void setOutputAudioPath(String outputAudioPath) {
        this.outputAudioPath = outputAudioPath;
    }

    /**
     * outputAudioPattern getter
     * @return
     */
    public String getOutputAudioPattern() {
        return outputAudioPattern;
    }

    /**
     * outputAudioPattern setter
     * @param outputAudioPattern
     */
    public void setOutputAudioPattern(String outputAudioPattern) {
        this.outputAudioPattern = outputAudioPattern;
    }

    /**
     * xsdPath getter
     * @return
     */
    public String getXsdPath() {
        return xsdPath;
    }

    /**
     * Config constructor. Parses the file and set the config
     * @param configFile
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws NullPointerException
     */
    public Config(String configFile) throws IOException, SAXException, ParserConfigurationException, NullPointerException {
        this.configFile = configFile;
        Document configDocument = null;

        configDocument = useDOMParser(configFile);

        this.validate();

        Element elConfig = configDocument.getRootElement();
        Element ttsElement = elConfig.getChild("tts");
        Element readerElement = elConfig.getChild("reader");

        //Elements from tts node
        this.setTtsServiceUrl(ttsElement.getChildText("url"));
        this.setIsOnline(Boolean.valueOf(ttsElement.getChildText("online")));
        this.setLanguage(ttsElement.getChildText("language"));


        //Elements form reader node
        this.setSplitMode(readerElement.getChildText("split-by"));
        this.setAlternatives(Boolean.valueOf(ttsElement.getChildText("alternatives")));
        this.setOutputAudioPath(readerElement.getChildText("output-path"));
        this.setOutputAudioPattern(readerElement.getChildText("output-pattern"));

        log.log(Level.INFO,this.toString());
    }

    //Get JDOM document from DOM Parser
    private static org.jdom2.Document useDOMParser(String fileName)
            throws ParserConfigurationException, SAXException, IOException, NullPointerException {
        //creating DOM Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(new File(fileName));
        DOMBuilder domBuilder = new DOMBuilder();
        return domBuilder.build(doc);

    }
}
