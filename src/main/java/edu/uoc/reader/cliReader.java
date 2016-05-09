package edu.uoc.reader;

import it.sauronsoftware.jave.EncoderException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
import org.apache.log4j.Level;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 * Created by mvelezm on 26/04/16.
 */

public class cliReader {
    private static final Logger log= Logger.getLogger( cliReader.class.getName());
    private String configFileName;
    private String odfFileName;
    private String language;
    private String filePattern;
    private String filePath;
    private String onlineTTSServiceUrl;
    private String splitMode;

    public void doOnLineConversion (String text) throws IOException, EncoderException {
        new OnLineTTS(onlineTTSServiceUrl).generateAudio(language, text, filePath, filePattern);
    }

    public void doOnLineConversion (String[] text) throws IOException, EncoderException {
        Integer i = 0;
        for (String subText: text) {
            i++;
            new OnLineTTS(onlineTTSServiceUrl).generateAudio(language, subText, filePath, filePattern+String.format("%03d", i));
            doOnLineConversion(text);
        }
    }

    public void doOfflineConversion (String text) throws IOException, EncoderException, InterruptedException {
        new espeakTTS().generateAudio(language, text, filePath, filePattern);
    }

    public void doOfflineConversion (String[] text) throws IOException, EncoderException, InterruptedException {
        Integer i = 0;
        for (String subText: text) {
            i++;
            new espeakTTS().generateAudio(language, subText, filePath, filePattern+String.format("%03d", i));
        }
    }

    public String[] processText(String text) {
        String[] pages = new String [] {"Empty"};
        if (splitMode.equals("PAGE-BREAK")){
            pages = text.split("PAGE-BREAK-MARK");
        }
        else {
            String result = text.replaceAll("PAGE-BREAK-MARK","");
            pages[0] = result;
        }
        return pages;
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
            System.out.println(e.getMessage());
        }

        Config setup = null;
        try {
            setup = new Config(configFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        OdfDocument odfDocument = null;
        try {
            odfDocument = OdfDocument.loadDocument(odfFileName);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        String text = null;
        if (odfDocument instanceof OdfTextDocument) {
            ODTParser docParser = null;
            try {
                docParser = new ODTParser(odfDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }
            text = docParser.getText();
        }
        else if (odfDocument instanceof OdfSpreadsheetDocument) {
            ODSParser docParser = null;
            try {
                docParser = new ODSParser(odfDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }
            text = docParser.getText();
        }
        else if (odfDocument instanceof OdfPresentationDocument) {
            ODPParser docParser = null;
            try {
                docParser = new ODPParser(odfDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }
            text = docParser.getText();
        }

        this.language = setup.getLanguage();
        this.filePattern = setup.getOutputAudioPattern();
        this.filePath = setup.getOutputAudioPath();
        this.onlineTTSServiceUrl = setup.getTtsServiceUrl();
        this.splitMode = setup.getSplitMode();
        log.log(Level.INFO, text);
        String[] pages = processText(text);
        log.log(Level.INFO,"Document language:\t" + this.language);
        log.log(Level.INFO, "Output folder:\t\t" + this.filePath);
        log.log(Level.INFO, "Output file name:\t" + this.filePath);
        log.log(Level.INFO, "Split file mode:\t" + this.splitMode);
        log.log(Level.INFO, "Output file number:\t" + pages.length);
        log.log(Level.INFO, "Start of the conversion process");

        for (String g: pages)
            log.log(Level.INFO, g);


            if (setup.getIsOnline()) {
                try {
                    if (pages.length == 1 )
                        doOnLineConversion(pages[0]);
                    else
                        doOnLineConversion(pages);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncoderException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (pages.length == 1 )
                        doOfflineConversion(pages[0]);
                    else
                        doOfflineConversion(pages);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncoderException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
}
