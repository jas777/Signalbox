package com.jas777.signalbox.control;

public interface Controllable<T, C> {

    int frequency = 0;

    int getFrequency();

    ControlChannel getChannel();

    void handleMessage(ChannelMessage<T> message);

    void sendMessage(ChannelMessage<C> message);

}
