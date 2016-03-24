/**
 * Created by mvelezm on 23/03/16.
 */
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;

public class Config {
    private String ttsServiceUrl;
    private String configFile;

    private String outputAudioPattern;

    public String getOutputAudioPattern() {
        return outputAudioPattern;
    }

    public void setOutputAudioPattern(String outputAudioPattern) {
        this.outputAudioPattern = outputAudioPattern;
    }

    public Config(String configFile) {
        this.configFile = configFile;

        SAXBuilder parser = new SAXBuilder();
        Document docConfig = null;
        try {
            docConfig = parser.build(configFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element elConfig = docConfig.getRootElement();
        Element ttsElement = elConfig.getChild("tts");
        Element readerElement = elConfig.getChild("reader");

        String ttsServiceUrl = ttsElement.getChildText("url");
        System.out.println(ttsServiceUrl);
        this.setTtsServiceUrl(ttsServiceUrl);
        
        String outputAudioPattern = readerElement.getChildText("output-pattern");
        System.out.println(outputAudioPattern);
        this.setOutputAudioPattern(outputAudioPattern);
    }

    public String getTtsServiceUrl() {

        return ttsServiceUrl;
    }

    public void setTtsServiceUrl(String ttsServiceUrl) {
        this.ttsServiceUrl = ttsServiceUrl;
    }
}
