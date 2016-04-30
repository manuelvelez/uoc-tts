package edu.uoc.reader;

import org.apache.commons.cli.*;

public class Reader {

    public static void main(String[] args) throws InterruptedException {

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
