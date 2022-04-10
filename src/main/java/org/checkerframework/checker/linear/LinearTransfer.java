package org.checkerframework.checker.linear;

import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFTransfer;

public class LinearTransfer extends CFTransfer {
    public LinearTransfer(CFAnalysis analysis) {
        super(analysis);
    }
}
