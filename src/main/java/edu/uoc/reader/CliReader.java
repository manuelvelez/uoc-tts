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

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Created by Manuel Vélez on 26/04/16.
 * This class manages the execution of the application in command line mode.
 */

public class CliReader {
    private static final Logger log= Logger.getLogger( CliReader.class.getName());
    private String configFileName;
    private String odfFileName;
    private String language;
    private String filePattern;
    private String filePath;
    private String onlineTTSServiceUrl;
    private String splitMode;

    /**
     * Recives a text string and convert it to audio using the online provider
     * @param text
     * @throws IOException
     * @throws EncoderException
     */
    public void doOnLineConversion (String text) throws IOException, EncoderException {
        new OnLineTTS(onlineTTSServiceUrl).generateAudio(language, text, filePath, filePattern);
    }

    /**
     * Recives an array of text strings and convert them to several audios using the online provider
     * @param text
     * @throws IOException
     * @throws EncoderException
     */
    public void doOnLineConversion (String[] text) throws IOException, EncoderException {
        Integer i = 0;
        for (String subText: text) {
            i++;
            new OnLineTTS(onlineTTSServiceUrl).generateAudio(language, subText, filePath, filePattern + '_' + String.format("%03d", i));
        }
    }

    /**
     * Recives a text string and convert it to audio using the offline provider
     * @param text
     * @throws IOException
     * @throws EncoderException
     * @throws InterruptedException
     */
    public void doOfflineConversion (String text) throws IOException, EncoderException, InterruptedException {
        new EspeakTTS().generateAudio(language, text, filePath, filePattern);
    }

    /**
     * Recives an array of text strings and convert them to several audios using the online provider
     * @param text
     * @throws IOException
     * @throws EncoderException
     * @throws InterruptedException
     */
    public void doOfflineConversion (String[] text) throws IOException, EncoderException, InterruptedException {
        Integer i = 0;
        for (String subText: text) {
            i++;
            new EspeakTTS().generateAudio(language, subText, filePath, filePattern + '_' + String.format("%03d", i));
        }
    }

    /**
     * Creates an array of strings from a string that contains PAGE-BREAK marks
     * @param text
     * @return
     */
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

    /**
     * Constructor of the class, execute the conversion in command line mode using the options and the command line
     * arguments
     * @param args
     * @param options
     */

    CliReader(String[] args, Options options) {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;

        //Parse command line arguments
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("config") && cmd.hasOption("doc")) {
                this.configFileName = cmd.getOptionValue("config");
                this.odfFileName = cmd.getOptionValue("doc");
            }
            else {
                log.log(Level.ERROR,"Invalid command line parameters");
                log.log(Level.INFO,"Command line execution needs 2 parameters: --config pathToXmlConfigFile --doc pathToOdfFile");
                log.log(Level.INFO,"Gui execution accepts no parameter.");
                System.exit(255);
            }
        } catch (ParseException e) {
            log.log(Level.ERROR, e.getMessage());
            System.exit(1);
        }

        //Parse xml config file
        Config setup = null;
        try {
            setup = new Config(configFileName);
        } catch (IOException e) {
            log.log(Level.ERROR, e.getMessage());
            System.exit(1);
        } catch (SAXException e) {
            log.log(Level.ERROR, e.getMessage());
            System.exit(1);
        } catch (ParserConfigurationException e) {
            log.log(Level.ERROR, e.getMessage());
            System.exit(1);
        }

        //Load odf file
        OdfDocument odfDocument = null;
        try {
            odfDocument = OdfDocument.loadDocument(odfFileName);
        } catch (NullPointerException e1) {
            log.log(Level.ERROR, "Null filename");
            System.exit(1);
        } catch (Exception e1) {
            log.log(Level.ERROR, e1.getMessage());
            System.exit(1);
        }

        this.language = setup.getLanguage();
        String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        this.filePattern = setup.getOutputAudioPattern()  + '_' + date;
        this.filePath = setup.getOutputAudioPath();
        this.onlineTTSServiceUrl = setup.getTtsServiceUrl();
        this.splitMode = setup.getSplitMode();


        //Call the suitable parser from ODFParser factory
        String text = null;
        //ODTParser
        if (odfDocument instanceof OdfTextDocument) {
            ODTParser docParser = null;
            try {
                docParser = new ODTParser(odfDocument, language);
            } catch (Exception e) {
                e.printStackTrace();
            }
            text = docParser.getText();
        }
        //ODSParser
        else if (odfDocument instanceof OdfSpreadsheetDocument) {
            ODSParser docParser = null;
            try {
                docParser = new ODSParser(odfDocument, language);
            } catch (Exception e) {
                e.printStackTrace();
            }
            text = docParser.getText();
        }
        //ODPParser
        else if (odfDocument instanceof OdfPresentationDocument) {
            ODPParser docParser = null;
            try {
                docParser = new ODPParser(odfDocument, language);
            } catch (Exception e) {
                e.printStackTrace();
            }
            text = docParser.getText();
        }
        //Unsuported file type
        else {
            log.log(Level.ERROR, "Unsupported file type: " + odfDocument.getClass());
            System.exit(1);
        }

        String[] pages = processText(text);

        log.log(Level.INFO, "Document language: " + this.language);
        log.log(Level.INFO, "Output folder: " + this.filePath);
        log.log(Level.INFO, "Output file name: " + this.filePath);
        log.log(Level.INFO, "Split file mode: " + this.splitMode);
        log.log(Level.INFO, "Output file number: " + pages.length);
        log.log(Level.INFO, "Is online?: " + setup.getIsOnline());
        log.log(Level.INFO, "Start of the conversion process");

        //Conversion of text depending on the online tag.
        if (setup.getIsOnline()) {
            try {
                if (pages.length == 1 )
                    doOnLineConversion(pages[0]);
                else
                    doOnLineConversion(pages);
            } catch (IOException e) {
                log.log(Level.ERROR, e.getMessage());
                System.exit(2);
            } catch (EncoderException e) {
                log.log(Level.ERROR, e.getMessage());
                System.exit(2);
            }
        } else {
            try {
                if (pages.length == 1 )
                    doOfflineConversion(pages[0]);
                else
                    doOfflineConversion(pages);
            } catch (IOException e) {
                log.log(Level.ERROR, e.getMessage());
                System.exit(2);
            } catch (EncoderException e) {
                log.log(Level.ERROR, e.getMessage());
                System.exit(2);
            } catch (InterruptedException e) {
                log.log(Level.ERROR, e.getMessage());
                System.exit(2);
            }
        }
        log.log(Level.INFO, "Cleaning the house (removing temporal files)");
        File tempDir = new File(filePath + "/temp/");
        //Deletion of temporary files
        tempDir.delete();
        log.log(Level.INFO, "Process finished");
    }
}
