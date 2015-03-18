package com.Eddie.LANFighter.Entities.Server;

import com.Eddie.LANFighter.Entities.EntityType.WeaponEntityType;
import com.Eddie.LANFighter.Network.Messages.EntityState;
import com.Eddie.LANFighter.Physics.Body.BodyType;
import com.Eddie.LANFighter.Utils.EntityUtils.ActorType;
import com.Eddie.LANFighter.Utils.Utils;
import com.Eddie.LANFighter.Utils.WorldBodyUtils;
import com.badlogic.gdx.math.MathUtils;

public class ServerBullet extends ServerEntity implements WeaponEntityType
{
    public static final float RADIUS = 5f;
    private float velocity = 0.0f;
    public float destroyTime;
    public ServerEntity shooter;

    public ServerBullet(short id, float x, float y, WorldBodyUtils world)
    {
        super(id, x, y, world);
        actorType = ActorType.BULLET;
        body = world.addBox(RADIUS, RADIUS, position.x, position.y, BodyType.DynamicBody);
        body.setLinearVelocity(velocity, 0);
        body.setGravityScale(0f);
        body.setUserData(this);
        destroyTime = 1f;
    }

    @Override
    public void update(float delta)
    {
        destroyTime -= delta;
        position.set(body.getPosition());

        if(Utils.wrapBody(position))
        {
            body.setTransform(position, 0);
        }

        if(destroyTime < 0)
        {
            dispose();
        }
    }

    @Override
    public void dispose()
    {
        world.destroyBody(body);
    }

    @Override
    public void updateState(EntityState state)
    {
        super.updateState(state);
        
        state.angle = MathUtils.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x);
    }

    @Override
    public float getWidth()
    {
        return RADIUS;
    }

    @Override
    public void addKill()
    {
    }

    @Override
    public ServerEntity getShooter()
    {
        return shooter;
    }
}
