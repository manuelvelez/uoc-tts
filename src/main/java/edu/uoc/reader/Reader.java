package edu.uoc.reader;

import org.apache.commons.cli.*;

public class Reader {

    public static void main(String[] args) throws InterruptedException {

        Options options = new Options();

        options.addOption("config", "config path", true, "set the path for the config file");
        options.addOption("doc", "document path", true, "set the path for the document to be parsed");

        if (args.length == 0) {
            new guiReader();
        } else {
            new cliReader(args, options);
        }
    }
}
