package com.Eddie.LANFighter.Entities.Client;

import com.Eddie.LANFighter.Entities.Server.ServerBomb;
import com.Eddie.LANFighter.Utils.AssetLoader;
import com.Eddie.LANFighter.Utils.WorldRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ClientBomb extends ClientEntity
{
    private Sprite sprite;
    boolean markForDispose;
    private float deadTimer;
    private Animation explode;

    public ClientBomb(short id, float x, float y, WorldRenderer renderer)
    {
        super(id, x, y, renderer);
        
        markForDispose = false;
        
        sprite = new Sprite(AssetLoader.getInstance().getTexture("Sprites/Bomb.png"));
        sprite.setSize(ServerBomb.RADIUS + 5, ServerBomb.RADIUS + 5);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        
        deadTimer = 0f;
        
        Texture explodeTexture = AssetLoader.getInstance().getTexture("Sprites/Explosion.png");
        explode = new Animation(0.03f, TextureRegion.split(explodeTexture, explodeTexture.getWidth() / 7, explodeTexture.getHeight())[0]);
    }

    @Override
    public void render(float delta, SpriteBatch batch)
    {
        float x = position.x - sprite.getWidth() / 2;
        float y = position.y - sprite.getHeight() / 2;
        
        if(destroy)
        {
            deadTimer += delta;
        
            if(deadTimer >= 0.21f)
            {
                remove = true;
            }
        
            sprite.setSize(70, 70);
            sprite.setRegion(explode.getKeyFrame(deadTimer));
            sprite.flip(false, false);
        }
     
        drawAll(sprite, batch, x, y);
    }

    @Override
    public void dispose()
    {
    }
}
