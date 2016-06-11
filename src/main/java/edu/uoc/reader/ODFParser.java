package edu.uoc.reader;

import org.w3c.dom.Node;

/**
 * Created by Manuel Vélez on 23/03/16.
 * Abstract class of the ODFParser factroy
 */
public abstract class ODFParser {
    /**
     * Abstract method for getting text from ODF files. redefined in every concrete class of the factory.
     * @param node
     * @return
     */
    public abstract String recurse(Node node);
}
