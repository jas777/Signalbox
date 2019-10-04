package com.jas777.signalbox.signal;

import com.jas777.signalbox.tileentity.SignalTileEntity;

public class SignalBlock {

    private SignalTileEntity entrySignal;
    private SignalTileEntity departSignal;

    public SignalBlock(SignalTileEntity signal) {
        this.entrySignal = signal;
    }

}
