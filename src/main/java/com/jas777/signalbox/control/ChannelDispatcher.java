package com.jas777.signalbox.control;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class ChannelDispatcher {

    private HashMap<Integer, ControlChannel> channels;

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

        ControlChannel channel = new ControlChannel(frequency);

        return channels.put(frequency, channel);

    }

    public void sendMessage(int frequency, ChannelMessage message) {
        channels.get(frequency).getTuned().forEach((subFreq, device) -> device.handleMessage(message));
    }

    public void sendMessage(ControlChannel channel, ChannelMessage message) {
        channel.getTuned().forEach((subFreq, device) -> device.handleMessage(message));
    }

    public ControlChannel getChannelFromFrequency(String frequency) {
        if (StringUtils.split(frequency, '.').length < 1) return null;
        int freq = Integer.parseInt(StringUtils.split(frequency, '.')[0]);
        return getChannel(freq);
    }

}
