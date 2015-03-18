package com.Eddie.LANFighter.Entities.Server;

import com.Eddie.LANFighter.Network.Messages.EntityState;
import com.Eddie.LANFighter.Physics.Body;
import com.Eddie.LANFighter.Utils.EntityUtils;
import com.Eddie.LANFighter.Utils.EntityUtils.ActorType;
import com.Eddie.LANFighter.Utils.WorldBodyUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class ServerEntity
{
    public ActorType actorType;
    public short id;
    protected boolean toLoadAssets;
    protected final Vector2 position;
    protected WorldBodyUtils world;
    public Body body;

    public ServerEntity(short id, float x, float y, WorldBodyUtils world)
    {
        position = new Vector2(x, y);
        toLoadAssets = true;
        this.id = id;
        this.world = world;
    }

    public abstract void update(float delta);

    public abstract void dispose();

    public void updateState(EntityState state)
    {
        state.id = id;
        state.type = EntityUtils.actorTypeToByte(actorType);
        state.x = body.getPosition().x;
        state.y = body.getPosition().y;
    }

    public abstract float getWidth();

    public void addKill()
    {
    }

    public void reduceKill()
    {
    }
}
