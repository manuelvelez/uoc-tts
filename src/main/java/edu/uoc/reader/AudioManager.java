package edu.uoc.reader;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.*;

/**
 * Created by mvelezm on 1/05/16.
 */
public class AudioManager {

    public void generateOggFile(String audioPath){
        File source = new File(audioPath);
        String targetPath = audioPath.substring(0, audioPath.lastIndexOf(".")) + ".ogg";
        File target = new File(targetPath);
        target.delete();

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("vorbis");
        audio.setBitRate(new Integer(128000));
        audio.setChannels(new Integer(2));
        audio.setSamplingRate(new Integer(44100));
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("ogg");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        try {
            encoder.encode(source, target, attrs);
        } catch (EncoderException e) {
            e.printStackTrace();
        }

        source.delete();
    }

    AudioManager(){

    }
}
