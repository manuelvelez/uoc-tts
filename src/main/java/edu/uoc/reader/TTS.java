package edu.uoc.reader;

/**
 * Created by mvelezm on 22/03/16.
 */

import java.io.*;

public abstract class TTS {
    public abstract void generateAudio(String language, String text, String filePath, String fileName) throws IOException;
}
