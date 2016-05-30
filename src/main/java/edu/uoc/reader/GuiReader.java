package edu.uoc.reader;


import it.sauronsoftware.jave.EncoderException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by mvelezm on 26/04/16.
 */
public class GuiReader {

    private static final Logger log= Logger.getLogger( GuiReader.class.getName());

    private static String configFileName;
    private static String odfFileName;

    private static String language;
    private static String filePattern;
    private static String filePath;

    private static String onlineTTSServiceUrl;
    private static String splitMode;

    private static File currentDir;
    private static File configDir;

    private static Config guiConfig;


    public static void doOnLineConversion (String text) throws IOException, EncoderException {
        System.out.println(guiConfig.toString());
        new OnLineTTS(onlineTTSServiceUrl).generateAudio(guiConfig.getLanguage(), text, guiConfig.getOutputAudioPath(), guiConfig.getOutputAudioPattern());
    }

    public static void doOnLineConversion (String[] text) throws IOException, EncoderException {
        System.out.println(guiConfig.toString());
        Integer i = 0;
        for (String subText: text) {
            i++;
            new OnLineTTS(onlineTTSServiceUrl).generateAudio(guiConfig.getLanguage(), subText, guiConfig.getOutputAudioPath(), guiConfig.getOutputAudioPattern() + '_' + String.format("%03d", i));
            //doOnLineConversion(text);
        }
    }

    public static void doOfflineConversion (String text) throws IOException, EncoderException, InterruptedException {
        System.out.println(guiConfig.toString());
        new EspeakTTS().generateAudio(guiConfig.getLanguage(), text, guiConfig.getOutputAudioPath(), guiConfig.getOutputAudioPattern());
    }

    public static void doOfflineConversion (String[] text) throws IOException, EncoderException, InterruptedException {
        System.out.println(guiConfig.toString());
        Integer i = 0;
        for (String subText: text) {
            i++;
            new EspeakTTS().generateAudio(guiConfig.getLanguage(), subText, guiConfig.getOutputAudioPath(), guiConfig.getOutputAudioPattern() + '_' + String.format("%03d", i));
        }
    }

    public static String[] processText(String text) {
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

    GuiReader() {
        showWindow();
    }
    public void doTheConversion() {

    }
    public static void showWindow() {
        JFrame frame = new JFrame("uoc-reader");
        frame.setSize(600, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {

        panel.setLayout(null);
        if (System.getProperty("os.name").contains("Windows"))
            currentDir = new File(System.getenv("USERPROFILE"));
        else
            currentDir = new File(System.getProperty("user.home"));

        JLabel odfFileLabel = new JLabel("Odf File:");
        odfFileLabel.setBounds(10, 10, 80, 25);
        panel.add(odfFileLabel);

        final JTextField odfTextField = new JTextField(200);
        odfTextField.setBounds(100, 10, 400, 25);
        panel.add(odfTextField);

        final JButton odfButton = new JButton("Open");
        odfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(currentDir);
                int result = fileChooser.showOpenDialog(odfButton);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    currentDir=new File(selectedFile.getParent());
                    odfTextField.setText(selectedFile.getAbsolutePath());
                    odfFileName = selectedFile.getAbsolutePath();
                }
            }
        });
        odfButton.setBounds(500, 10, 80, 25);
        panel.add(odfButton);


        final JCheckBox onlineCheckBox = new JCheckBox("Online processing");
        onlineCheckBox.setBounds(5, 70, 200, 25);
        onlineCheckBox.setEnabled(false);
        panel.add(onlineCheckBox);

        final JLabel languageLabel = new JLabel("Choose Language");

        final JRadioButton esRadioButton = new JRadioButton("Spanish");
        esRadioButton.setEnabled(false);

        final JRadioButton enRadioButton = new JRadioButton("English");
        enRadioButton.setEnabled(false);

        final JRadioButton caRadioButton = new JRadioButton("Catalonian");
        caRadioButton.setEnabled(false);

        ButtonGroup languageGroup = new ButtonGroup();
        languageGroup.add(esRadioButton);
        languageGroup.add(enRadioButton);
        languageGroup.add(caRadioButton);

        final JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.setBounds(5, 100, 150, 110);

        languagePanel.add(languageLabel);
        languagePanel.add(esRadioButton);
        languagePanel.add(caRadioButton);
        languagePanel.add(enRadioButton);

        languagePanel.setBorder(BorderFactory.createLineBorder(Color.gray));

        panel.add(languagePanel);

        final JLabel splitLabel = new JLabel("Split by strategy");

        final JRadioButton pageBreakRadioButton = new JRadioButton("Split by Pages");
        pageBreakRadioButton.setBounds(205, 110, 150, 25);
        pageBreakRadioButton.setEnabled(false);
        panel.add(pageBreakRadioButton);

        final JRadioButton singlePageRadiobutton = new JRadioButton("single audio file");
        singlePageRadiobutton.setBounds(205, 130, 150, 25);
        singlePageRadiobutton.setEnabled(false);
        panel.add(singlePageRadiobutton);

        ButtonGroup pageGroup = new ButtonGroup();
        pageGroup.add(pageBreakRadioButton);
        pageGroup.add(singlePageRadiobutton);

        final JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagePanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        pagePanel.setBounds(160, 100, 150, 110);
        pagePanel.add(splitLabel);
        pagePanel.add(pageBreakRadioButton);
        pagePanel.add(singlePageRadiobutton);

        panel.add(pagePanel);

        JLabel configLabel = new JLabel("Config File:");
        configLabel.setBounds(10, 40, 80, 25);
        panel.add(configLabel);

        final JTextField configTextField = new JTextField(200);
        configTextField.setBounds(100, 40, 400, 25);
        panel.add(configTextField);


        final JButton configButton = new JButton("Open");
        configButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                configDir = new File("config");
                fileChooser.setCurrentDirectory(configDir);
                int result = fileChooser.showOpenDialog(configButton);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    configTextField.setText(selectedFile.getAbsolutePath());

                    Config setup = null;
                    try {
                        setup = new Config(configTextField.getText());
                        configFileName = configTextField.getText();
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "IO Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (SAXException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "SAX Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (ParserConfigurationException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Parse Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (NullPointerException e1) {
                        JOptionPane.showMessageDialog(null, "Config file is not set, please set and retry", "Configuration file name", JOptionPane.ERROR_MESSAGE);
                    }

                    language = setup.getLanguage();
                    filePattern = setup.getOutputAudioPattern();
                    filePath = setup.getOutputAudioPath();
                    onlineTTSServiceUrl = setup.getTtsServiceUrl();
                    splitMode = setup.getSplitMode();

                    onlineCheckBox.setEnabled(true);
                    esRadioButton.setEnabled(true);
                    enRadioButton.setEnabled(true);
                    caRadioButton.setEnabled(true);

                    pageBreakRadioButton.setEnabled(true);
                    singlePageRadiobutton.setEnabled(true);


                    if(setup.getIsOnline())
                        onlineCheckBox.setSelected(true);


                    if(setup.getLanguage().equals("ES"))
                        esRadioButton.setSelected(true);
                    else if(setup.getLanguage().equals("EN"))
                        enRadioButton.setSelected(true);
                    else if(setup.getLanguage().equals("CA"))
                        caRadioButton.setSelected(true);

                    if(setup.getSplitMode().equals("PAGE-BREAK"))
                        pageBreakRadioButton.setSelected(true);
                    else
                        singlePageRadiobutton.setSelected(true);

                    guiConfig = setup;
                }
            }
        });
        configButton.setBounds(500, 40, 80, 25);
        panel.add(configButton);


        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OdfDocument odfDocument = null;

                if(esRadioButton.isSelected())
                    guiConfig.setLanguage("ES");
                else if (caRadioButton.isSelected())
                    guiConfig.setLanguage("CA");
                else if (enRadioButton.isSelected())
                    guiConfig.setLanguage("EN");

                if (onlineCheckBox.isSelected())
                    guiConfig.setIsOnline(true);
                else
                    guiConfig.setIsOnline(false);

                if(pageBreakRadioButton.isSelected())
                    guiConfig.setSplitMode("PAGE-BREAK");
                else
                    guiConfig.setSplitMode("UNIQUE");


                try {
                    odfDocument = OdfDocument.loadDocument(odfTextField.getText());
                    odfFileName = odfTextField.getText();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), "Lang Exception", JOptionPane.ERROR_MESSAGE);
                }

                String text = null;

                if (odfDocument instanceof OdfTextDocument) {
                    ODTParser docParser = null;
                    try {
                        docParser = new ODTParser(odfDocument, guiConfig.getLanguage());
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                    text = docParser.getText();
                }
                else if (odfDocument instanceof OdfSpreadsheetDocument) {
                    ODSParser docParser = null;
                    try {
                        docParser = new ODSParser(odfDocument, guiConfig.getLanguage());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    text = docParser.getText();
                }
                else if (odfDocument instanceof OdfPresentationDocument) {
                    ODPParser docParser = null;
                    try {
                        docParser = new ODPParser(odfDocument, guiConfig.getLanguage());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    text = docParser.getText();
                }


                String[] pages = processText(text);

                log.log(Level.INFO, "Document language: " + guiConfig.getLanguage());
                log.log(Level.INFO, "Output folder: " + guiConfig.getOutputAudioPath());
                log.log(Level.INFO, "Output file name: " + guiConfig.getOutputAudioPattern());
                log.log(Level.INFO, "Split file mode: " + guiConfig.getSplitMode());
                log.log(Level.INFO, "Output file number: " + pages.length);
                log.log(Level.INFO, "Is online?: " + guiConfig.getIsOnline());
                log.log(Level.INFO, "Start of the conversion process");

                if (guiConfig.getIsOnline()) {
                    try {
                        if (pages.length == 1 )
                            doOnLineConversion(pages[0]);
                        else
                            doOnLineConversion(pages);
                    } catch (IOException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Conversion Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (EncoderException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Encoder Exception", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    try {
                        if (pages.length == 1 )
                            doOfflineConversion(pages[0]);
                        else
                            doOfflineConversion(pages);
                    } catch (IOException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Conversion Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (EncoderException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Encoder Exception", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException e1) {
                        log.log(Level.ERROR, e1.getMessage());
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Interrupted Exception", JOptionPane.ERROR_MESSAGE);
                    }
                }
                log.log(Level.INFO, "Process finished");

            }
        });
        startButton.setBounds(500, 70, 80, 25);
        panel.add(startButton);
    }
}
