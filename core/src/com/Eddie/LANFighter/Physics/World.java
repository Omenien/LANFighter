package com.Eddie.LANFighter.Physics;

import com.Eddie.LANFighter.Utils.WorldManager;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class World
{
    public final Vector2 gravity;
    public final ArrayList<Body> bodies;

    public World(Vector2 gravity)
    {
        this.gravity = gravity;
        bodies = new ArrayList<>();
    }

    public void step(float delta, int iterations, WorldManager worldManager)
    {
        int i = 0;
        
        while(i < bodies.size())
        {
            Body body = bodies.get(i);
            
            if(body.toDestroy)
            {
                worldManager.destroyBody(body);
                bodies.remove(i);
            }
            
            i++;
        }
        
        for(i = 0; i < iterations; i++)
        {
            for(Body body : bodies)
            {
                if(body.bodyType == Body.BodyType.DynamicBody)
                {
                    body.update(delta / (float) iterations);
                }
            }
        }
    }

    public void addBody(Body body)
    {
        bodies.add(body);
    }
}
