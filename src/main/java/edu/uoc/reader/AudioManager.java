package edu.uoc.reader;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Created by mvelezm on 1/05/16.
 */
public class AudioManager {

    public void generateWavFile(String audioPath){
        File file = new File(audioPath);
        AudioInputStream in= null;
        try {
            in = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudioInputStream din = null;
        AudioFormat baseFormat = in.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);
        din = AudioSystem.getAudioInputStream(decodedFormat, in);

        AudioInputStream pcm;
        pcm = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, din);
        File out = new File("shit.wav");
        try {
            AudioSystem.write(pcm, AudioFileFormat.Type.WAVE, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(din.getFormat().toString());
        this.generateOggFile("shit.wav");
    }

    void generateOggFile(String audioPath){
        File source = new File(audioPath);
        File target = new File("target.ogg");
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
    }

    AudioManager(){

    }
}
