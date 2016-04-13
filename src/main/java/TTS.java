/**
 * Created by mvelezm on 22/03/16.
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class TTS {
    public abstract void generateAudio(String language, String text) throws IOException;
}
