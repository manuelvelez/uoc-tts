package edu.uoc.reader;

import it.sauronsoftware.jave.EncoderException;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the conversion of text to audio using eSpeak binaries
 * Created by Manuel Vélez on 20/04/16.
 */
public class EspeakTTS extends TTS{
    private static final Logger log= Logger.getLogger( EspeakTTS.class.getName());
    private static final Map<String, String> languages;
    static {
        Map<String, String> languagesMap = new HashMap<String, String>();
        languagesMap.put("ES", "spanish");
        languagesMap.put("EN", "english");
        languagesMap.put("CA", "catalan");
        languages = Collections.unmodifiableMap(languagesMap);
    }

    /**
     * Class constructor Run the eSpeak command and call generateOgg from AudioManager class
     * @param language
     * @param text
     * @param filePath
     * @param fileName
     * @throws IOException
     * @throws EncoderException
     * @throws InterruptedException
     */
    @Override
    public void generateAudio(String language, String text, String filePath, String fileName) throws IOException, EncoderException, InterruptedException {

        String audioFileName = filePath + "/temp/" + fileName + ".wav";
        File pathDirectory = new File(filePath + "/temp/");
        pathDirectory.mkdirs();

        log.debug(text);

        String espeakDir="";
        String espeakDataPath="";

        if (System.getProperty("os.name").contains("Windows"))
            espeakDir = ".\\espeak-win";
        else
            espeakDir = "./espeak-linux";

        String [] cmd = {espeakDir + "/espeak", "--path=" + espeakDir ,  "-w" + audioFileName, "-v"+languages.get(language), "-s125", "-p0", text };
        Process espeak = Runtime.getRuntime().exec(cmd);
        espeak.waitFor();

        String targetPath = filePath + "/" + fileName + ".ogg";
        new AudioManager().generateOggFile(audioFileName, targetPath);
    }
}
