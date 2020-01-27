package com.jas777.signalbox.control;

public interface Controllable<T> {

    int frequency = 0;

    int getFrequency();

    ControlChannel getChannel();

    void handleMessage(ChannelMessage<T> message);

    void sendMessage(ChannelMessage message);

}
