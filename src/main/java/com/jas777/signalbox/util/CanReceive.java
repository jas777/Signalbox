package com.jas777.signalbox.util;

import io.netty.buffer.ByteBuf;

public interface CanReceive {

    public int getChannel();
    public int getId();

    public void setChannel(int channel);
    public void setId(int id);

    public void updateBlock();

    public void setData(ByteBuf data);

}
