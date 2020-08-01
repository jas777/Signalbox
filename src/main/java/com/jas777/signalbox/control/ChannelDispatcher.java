package com.jas777.signalbox.control;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class ChannelDispatcher {

    private final HashMap<Integer, ControlChannel> channels;

    public ChannelDispatcher() {
        this.channels = new HashMap<Integer, ControlChannel>();
    }

    public HashMap<Integer, ControlChannel> getChannels() {
        return channels;
    }

    public ControlChannel getChannel(int frequency) {
        if (!channels.containsKey(frequency)) return null;
        return channels.get(frequency);
    }

    public ControlChannel addChannel(int frequency) {

        if (channels.containsKey(frequency)) return getChannel(frequency);

        channels.put(frequency, new ControlChannel(frequency));

        return channels.get(frequency);

    }

//    public <T extends Controllable<?>> void sendMessage(int frequency, ChannelMessage<T> message) {
//        channels.get(frequency).getTuned().forEach((subFreq, device) -> device.handleMessage(message));
//    }
//
//    public <T extends Controllable<?>> void sendMessage(ControlChannel channel, ChannelMessage<T> message) {
//        channel.getTuned().forEach((subFreq, device) -> device.handleMessage(message));
//    }

    public ControlChannel getChannelFromFrequency(String frequency) {
        if (StringUtils.split(frequency, '.').length < 1) return null;
        int freq = Integer.parseInt(StringUtils.split(frequency, '.')[0]);
        return freq > 0 ? addChannel(freq) : null;
    }

}
