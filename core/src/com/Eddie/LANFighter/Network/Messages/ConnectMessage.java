package com.Eddie.LANFighter.Network.Messages;

import java.util.ArrayList;

public class ConnectMessage
{
    public ArrayList<String> hosts;

    public ConnectMessage()
    {
        hosts = new ArrayList<>();
    }

    public void insertNewHost(String host)
    {
        hosts.add(host);
    }
}