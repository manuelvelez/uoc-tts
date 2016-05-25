package edu.uoc.reader;

import it.sauronsoftware.jave.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by mvelezm on 1/05/16.
 */
public class AudioManager {
    private static final Logger log= Logger.getLogger( AudioManager.class.getName());

    public void generateOggFile(String audioPath) throws EncoderException {

        File source = new File(audioPath);
        String targetPath = audioPath.substring(0, audioPath.lastIndexOf(".")) + ".ogg";
        File target = new File(targetPath);
        try {
            Files.deleteIfExists(target.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.log(Level.INFO,"Creating file: " + targetPath);
        log.log(Level.DEBUG,"Original file: " + audioPath);

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("vorbis");
        audio.setBitRate(new Integer(128000));
        audio.setChannels(new Integer(2));
        audio.setSamplingRate(new Integer(44100));
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("ogg");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        encoder.encode(source, target, attrs);
        //source.delete();
    }

    AudioManager(){
    }
}
