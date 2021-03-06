package edu.uoc.reader;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import javax.sound.sampled.AudioFileFormat.Type;

import java.io.IOException;

/**
 * Created by Manuel Vélez on 18/04/16.
 * Manages the conversion of text to audio using freetts library
 * This class is deprecated and is not used in the project
 */
@Deprecated
public class OffLineTTS extends TTS {
    @Override
    public void generateAudio(String language, String text, String filePath, String fileName) throws IOException {
        System.setProperty("freetts.voices",
                "de.dfki.lt.freetts.en.us.MbrolaVoiceDirectory");

        System.setProperty("mbrola.base", "/home/mvelezm/voices");

        String VOICENAME_kevin = "mbrola-es1";

        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(VOICENAME_kevin);

        voice.allocate();
        voice.speak(text);

        AudioPlayer audioPlayer = null;
        audioPlayer = new SingleFileAudioPlayer("./output",Type.WAVE);
        voice.setAudioPlayer(audioPlayer);

        voice.speak(text);
        voice.deallocate();
        audioPlayer.close();
    }
}
