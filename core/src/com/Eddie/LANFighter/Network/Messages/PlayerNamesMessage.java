package com.Eddie.LANFighter.Network.Messages;

import java.util.HashMap;

public class PlayerNamesMessage
{
    public HashMap<Short, String> players;

    public PlayerNamesMessage()
    {
        players = new HashMap<>();
    }
}
