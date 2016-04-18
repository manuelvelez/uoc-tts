package edu.uoc.reader;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import java.io.IOException;

/**
 * Created by mvelezm on 18/04/16.
 */
public class OffLineTTS extends TTS {
    @Override
    public void generateAudio(String language, String text) throws IOException {
        String VOICENAME_kevin = "kevin";
        String speech = text; // string to speech
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(VOICENAME_kevin);
        voice.allocate();
        voice.speak(text);
    }
}
