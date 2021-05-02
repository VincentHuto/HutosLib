package com.hutoslib.client.models;

public class Animation
{
    private final int duration;

    public Animation(int duration) { this.duration = duration; }

    public int getDuration() { return duration; }

    @Override
    public String toString() { return "Animation{duration=" + duration + '}'; }
}