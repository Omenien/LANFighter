package com.Eddie.LANFighter.Entities.Client;

import com.Eddie.LANFighter.Entities.Server.ServerAimArrow;
import com.Eddie.LANFighter.Utils.AssetLoader;
import com.Eddie.LANFighter.Utils.WorldRenderer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ClientAimArrow extends ClientEntity
{
    private Sprite sprite;
    boolean markForDispose;

    public ClientAimArrow(short id, float x, float y, WorldRenderer renderer)
    {
        super(id, x, y, renderer);
        
        markForDispose = false;
        
        sprite = new Sprite(AssetLoader.getInstance().getTexture("Sprites/Arrow.png"));
        sprite.setSize(ServerAimArrow.RADIUS * 10, ServerAimArrow.RADIUS * 2.5f);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
    }

    @Override
    public void render(float delta, SpriteBatch batch)
    {
        sprite.setRotation(angle * MathUtils.radiansToDegrees);

        float x = position.x - sprite.getWidth() / 2;
        float y = position.y - sprite.getHeight() / 2;
        
        drawAll(sprite, batch, x, y);
    }

    @Override
    public void dispose()
    {
    }
}
