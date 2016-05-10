package edu.uoc.reader;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Reader {
    private static final Logger log= Logger.getLogger( Reader.class.getName());

    public static void main(String[] args) throws InterruptedException {

        Options options = new Options();

        options.addOption("config", "config path", true, "set the path for the config file");
        options.addOption("doc", "document path", true, "set the path for the document to be parsed");

        if (args.length == 0) {
            log.log(Level.INFO, "Running in Graphical mode");
            new GuiReader();
        } else {
            log.log(Level.INFO, "Running in Command line mode");
            new CliReader(args, options);
        }
    }
}
