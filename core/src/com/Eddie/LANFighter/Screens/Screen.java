package com.Eddie.LANFighter.Screens;

import com.Eddie.LANFighter.LANFighter;

public abstract class Screen implements com.badlogic.gdx.Screen
{
    LANFighter game;

    public Screen(LANFighter game)
    {
        this.game = game;
    }
}
