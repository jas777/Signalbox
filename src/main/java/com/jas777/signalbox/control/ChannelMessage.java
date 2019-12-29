package com.jas777.signalbox.control;

public interface ChannelMessage<T> {

    void handle(T target);

}
