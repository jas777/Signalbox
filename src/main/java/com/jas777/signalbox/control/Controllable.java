package com.jas777.signalbox.control;

public interface Controllable {

    ControlChannel getChannel();

    void handleMessage(ChannelMessage message);

    void sendMessage(ChannelMessage message);

}
