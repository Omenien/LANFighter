package com.Eddie.LANFighter.Utils;

public class Event
{
    public enum State
    {
        CONNECTED, DISCONNECTED, RECEIVED
    }

    public State state;
    public Object object;

    public Event()
    {
        super();
    }

    public Event set(State state, Object object)
    {
        this.state = state;
        this.object = object;
        return this;
    }
}
