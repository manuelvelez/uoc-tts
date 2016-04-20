package edu.uoc.reader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Created by mvelez on 14/03/16.
 */
public class Reader {


    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }

        System.out.println(args);
        Options options = new Options();

        options.addOption("h", "help", false, "show help.");
        options.addOption("v", "var", true, "Here you can set parameter .");
        options.addOption("config", "config path", true, "set the path for the config file");
        options.addOption("doc", "doc path", true, "set the path for the document to be parsed");
        System.out.println(options.toString());

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;

        String configFile = null;
        String docFile = null;

        try {
            cmd = parser.parse(options, args);
            Iterator commandIterator = cmd.iterator();
            if (cmd.hasOption("h"))
                System.out.println("HELP");

            if (cmd.hasOption("v"))
                System.out.println("VERSION");

            if (cmd.hasOption("config"))
                configFile = cmd.getOptionValue("config");

            if (cmd.hasOption("doc"))
                docFile = cmd.getOptionValue("doc");
        } catch (ParseException e) {
            System.out.println("EXCEPCIÓN DE PARSEO");
        }
        System.out.println(configFile);
        System.out.println(docFile);
        Config setup = new Config(configFile);
        ODTParser docParser;
        docParser = new ODTParser(docFile);
        String language = "INVENT";
        String text = docParser.getText();
        /*
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        try {
            new espeakTTS().generateAudio(language, text);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] Connection not available when online conversion selected");
            System.exit(1);
        }


    }
}
