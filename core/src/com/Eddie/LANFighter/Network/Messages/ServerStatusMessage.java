package com.Eddie.LANFighter.Network.Messages;

public class ServerStatusMessage
{
    public enum Status
    {
        INFO, DISCONNECT
    }

    public String toastText;
    public Status status;
}
