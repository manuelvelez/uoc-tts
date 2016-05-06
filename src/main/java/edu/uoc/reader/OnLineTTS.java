package edu.uoc.reader;

import it.sauronsoftware.jave.EncoderException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by mvelezm on 13/04/16.
 */
public class OnLineTTS extends TTS {
    private String onlineTTSService;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) " +
                    "Gecko/20100101 Firefox/11.0";

    OnLineTTS(String url) {
        this.onlineTTSService = url;
    }

    public void generateAudio(String language, String text, String filePath, String fileName) throws IOException, EncoderException {

        String[] textSplitted = text.replaceAll("(.{0,"+ 100+"})\\b", "$1\n").split("\n");

        File output = new File(filePath+"/"+ fileName+".mp3");
        File pathDirectory = new File(filePath);
        pathDirectory.mkdirs();
        BufferedOutputStream out = null;
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream();

        for (String subText: textSplitted) {
            System.out.println(subText);

            URL url = new URL(onlineTTSService + "?" +
                    "tl=" + language + "&q=" + URLEncoder.encode(subText, "utf-8") + "&client=tw-ob");

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

        new AudioManager().generateOggFile(output.getPath());
    }
}
