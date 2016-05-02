package edu.uoc.reader;

import java.io.*;
import java.net.HttpURLConnection;
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

    public void generateAudio(String language, String text, String filePath) throws IOException {

        String[] textSplitted = text.replaceAll("(.{0,"+ 100+"})\\b", "$1\n").split("\n");
        File output = new File("output.mp3");
        BufferedOutputStream out = null;
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream();

        for (String subText: textSplitted) {
            System.out.println(subText);

            String strUrl = TEXT_TO_SPEECH_SERVICE + "?" +
                    "tl=" + language + "&q=" + subText + "&client=tw-ob";
            URL url = new URL(strUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", USER_AGENT);
            connection.connect();

            BufferedInputStream bufIn =
                    new BufferedInputStream(connection.getInputStream());
            byte[] buffer = new byte[1024];
            int n;
            //ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
            ByteArrayOutputStream bufAux = new ByteArrayOutputStream();
            while ((n = bufIn.read(buffer)) > 0) {
                bufAux.write(buffer, 0, n);
            }

            bufOut.write(bufAux.toByteArray());

            out = new BufferedOutputStream(new FileOutputStream(output));
            out.write(bufOut.toByteArray());
            out.flush();
        }
        out.close();

        new AudioManager().generateOggFile("output.mp3");
    }
}
