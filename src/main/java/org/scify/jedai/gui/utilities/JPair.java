package org.scify.jedai.gui.utilities;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Mutable Pair, with custom toString method.
 */
public class JPair<L, R> extends MutablePair {
    public JPair() {
        super();
    }

    public JPair(Object left, Object right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return this.getLeft() + ": " + this.getRight();
    }
}
