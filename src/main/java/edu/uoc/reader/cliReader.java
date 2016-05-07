package edu.uoc.reader;

import it.sauronsoftware.jave.EncoderException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
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

/**
 * Created by mvelezm on 26/04/16.
 */

public class cliReader {
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

    public void doOfflineConversion (String text) throws IOException, EncoderException {
        new espeakTTS().generateAudio(language, text, filePath, filePattern);
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
            System.out.println("EXCEPCIÓN DE PARSEO");
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

        String[] pages = processText(text);
        System.out.println("PAGE NUMBER: " + pages.length);
        int i = 0;
        for (String page: pages) {
            System.out.println("PAGE NUMBER: " + i);
            System.out.println(page);
            i++;
        }

        try {
            if (setup.getIsOnline()) {
                doOnLineConversion(text);
            } else {
                doOfflineConversion(text);
            }
        } catch (Exception exception)
        {
            System.out.println(exception.getMessage());
        }
    }
}
