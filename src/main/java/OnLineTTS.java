import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mvelezm on 13/04/16.
 */
public class OnLineTTS extends TTS {
    private static final String TEXT_TO_SPEECH_SERVICE =
            "http://translate.google.com/translate_tts";

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) " +
                    "Gecko/20100101 Firefox/11.0";

    public void generateAudio(String language, String text) throws IOException {
        // Create url based on input params
        String strUrl = TEXT_TO_SPEECH_SERVICE + "?" +
                "tl=" + language + "&q=" + text + "&client=tw-ob";
        URL url = new URL(strUrl);

        // Etablish connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // Get method
        connection.setRequestMethod("GET");
        // Set User-Agent to "mimic" the behavior of a web browser. In this
        // example, I used my browser's info
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.connect();

        // Get content
        BufferedInputStream bufIn =
                new BufferedInputStream(connection.getInputStream());
        byte[] buffer = new byte[1024];
        int n;
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
        while ((n = bufIn.read(buffer)) > 0) {
            bufOut.write(buffer, 0, n);
        }

        // Done, save data
        File output = new File("output.mp3");
        BufferedOutputStream out =
                new BufferedOutputStream(new FileOutputStream(output));
        out.write(bufOut.toByteArray());
        out.flush();
        out.close();
    }
}
