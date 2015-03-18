package com.Eddie.LANFighter.desktop;

import com.Eddie.LANFighter.LANFighter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        System.setProperty("java.net.preferIPv4Stack" , "true");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "LANFighter";
        config.height = 640;
        config.width = 960;
        config.vSyncEnabled = true;
        new LwjglApplication(new LANFighter(), config);
    }
}
