package edu.uoc.reader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by mvelezm on 26/04/16.
 */

public class cliReader {
    private String configFileName;
    private String odfFileName;

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
        ODTParser docParser;
        docParser = new ODTParser(odfFileName);
        String language = "ES";
        String text = docParser.getText();
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            new espeakTTS().generateAudio(language, text);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] Connection not available when online conversion selected");
            System.exit(1);
        }
    }
}
