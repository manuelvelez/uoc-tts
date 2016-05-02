package edu.uoc.reader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by mvelezm on 26/04/16.
 */

public class cliReader {
    private String configFileName;
    private String odfFileName;
    private String language;
    private String filePattern;

    public void doOnLineConversion (String text) {
        try {
            new OnLineTTS().generateAudio(language, URLEncoder.encode(text, "utf-8"), filePattern);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Connection not available when online conversion selected");
        }
    }

    public void doOfflineConversion (String text) {
        try {
            new espeakTTS().generateAudio(language, text, filePattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    cliReader(String[] args, Options options){
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("config"))
                this.configFileName = cmd.getOptionValue("config");
            if (cmd.hasOption("doc"))
                this.odfFileName = cmd.getOptionValue("doc");
        } catch (ParseException e) {
            System.out.println("EXCEPCIÓN DE PARSEO");
        }

        Config setup = new Config(configFileName);
        ODTParser docParser = new ODTParser(odfFileName);

        this.language = setup.getLanguage();
        this.filePattern = setup.getOutputAudioPattern();
        System.out.println(this.filePattern);

        String text = docParser.getText();
        if (setup.getIsOnline()){
            this.doOnLineConversion(text);
        }
        else {
            this.doOfflineConversion(text);
        }
    }
}
