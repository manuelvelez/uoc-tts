package edu.uoc.reader;

import org.apache.commons.cli.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

/**
 * Created by mvelez on 14/03/16.
 */
public class Reader {

    public static void main(String[] args) throws InterruptedException {
        String configFile = null;
        String docFile = null;

        Options options = new Options();

        options.addOption("config", "config path", true, "set the path for the config file");
        options.addOption("doc", "doc path", true, "set the path for the document to be parsed");

        if (args.length == 0) {
            guiReader reader = new guiReader();
        } else {
            cliReader reader = new cliReader(args, options);
        }
    }
}
