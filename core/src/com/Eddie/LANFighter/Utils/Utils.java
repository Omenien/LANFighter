package com.Eddie.LANFighter.Utils;

import com.badlogic.gdx.math.Vector2;

public class Utils
{
    public static boolean wrapBody(Vector2 position)
    {
        boolean wrap = false;
 
        if(position.x > WorldRenderer.VIEWPORT_WIDTH)
        {
            wrap = true;
            position.x -= WorldRenderer.VIEWPORT_WIDTH;
        }
        else if(position.x < 0)
        {
            wrap = true;
            position.x += WorldRenderer.VIEWPORT_WIDTH;
        }
 
        if(position.y > WorldRenderer.VIEWPORT_HEIGHT)
        {
            wrap = true;
            position.y -= WorldRenderer.VIEWPORT_HEIGHT;
        }
        else if(position.y < 0)
        {
            wrap = true;
            position.y += WorldRenderer.VIEWPORT_HEIGHT;
        }
 
        return wrap;
    }
}
