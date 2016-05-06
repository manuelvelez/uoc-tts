package edu.uoc.reader;

import it.sauronsoftware.jave.EncoderException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mvelezm on 20/04/16.
 */
public class espeakTTS extends TTS{

    private static final Map<String, String> languages;
    static {
        Map<String, String> languagesMap = new HashMap<String, String>();
        languagesMap.put("ES", "spanish");
        languagesMap.put("EN", "english");
        languagesMap.put("CA", "catalan");
        languages = Collections.unmodifiableMap(languagesMap);
    }

    @Override
    public void generateAudio(String language, String text, String filePath, String fileName) throws IOException, EncoderException {
        String audioFileName = filePath + "/" + fileName + ".wav";
        File pathDirectory = new File(filePath);
        pathDirectory.mkdirs();
        String [] cmd = {"espeak", "-v"+languages.get(language), "-s150", "-p0", text, "-w" + audioFileName};
        Runtime.getRuntime().exec(cmd);

        new AudioManager().generateOggFile(audioFileName);
    }
}
