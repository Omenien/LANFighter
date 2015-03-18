package com.Eddie.LANFighter.Network.Messages;

import com.Eddie.LANFighter.Pools.Poolable;

import java.util.ArrayList;

public class GameStateMessage implements Poolable
{
    public ArrayList<EntityState> states;
    public long time;

    public GameStateMessage()
    {
        states = new ArrayList<>();
    }

    public void addNewState(EntityState state)
    {
        states.add(state);
    }

    @Override
    public void reset()
    {
        states.clear();
    }
}
