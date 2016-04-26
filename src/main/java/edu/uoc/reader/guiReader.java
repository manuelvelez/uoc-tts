package edu.uoc.reader;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by mvelezm on 26/04/16.
 */
public class guiReader {

    guiReader() {
        showWindow();
    }

    public static void showWindow() {
        JFrame frame = new JFrame("Demo application");
        frame.setSize(1000, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {

        panel.setLayout(null);

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
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(odfButton);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    odfTextField.setText(selectedFile.getAbsolutePath());
                }
                System.out.println("You clicked the button");
            }
        });
        odfButton.setBounds(500, 10, 80, 25);
        panel.add(odfButton);

        JLabel configLabel = new JLabel("Config File");
        configLabel.setBounds(10, 40, 80, 25);
        panel.add(configLabel);

        JTextField configTexField = new JPasswordField(20);
        configTexField.setBounds(100, 40, 400, 25);
        panel.add(configTexField);

        JButton configButton = new JButton("Open");
        configButton.setBounds(500, 40, 80, 25);
        panel.add(configButton);
    }
}
