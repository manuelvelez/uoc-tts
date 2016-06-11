package edu.uoc.reader;

/**
 * Created by Manuel Vélez on 22/03/16.
 * Abstract class that manages the TTS factory. This class is redefined in each implementation
 */

import it.sauronsoftware.jave.EncoderException;
import java.io.*;

public abstract class TTS {
    public abstract void generateAudio(String language, String text, String filePath, String fileName) throws IOException, EncoderException, InterruptedException;
}
