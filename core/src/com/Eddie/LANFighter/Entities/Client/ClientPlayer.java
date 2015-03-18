package com.Eddie.LANFighter.Entities.Client;

import com.Eddie.LANFighter.Entities.Server.ServerPlayer;
import com.Eddie.LANFighter.Utils.AssetLoader;
import com.Eddie.LANFighter.Utils.WorldRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class ClientPlayer extends ClientEntity
{
    private Sprite playerSprite;
    private Sprite aimArrowSprite;
    private boolean markForDisposal;
    private Animation playerWalkAnimation;
    private float curFrameDuration;
    private boolean prevXDirWasRight;

    public ClientPlayer(short id, float x, float y, WorldRenderer worldRenderer)
    {
        super(id, x, y, worldRenderer);
        markForDisposal = false;
        Texture playerTexture = AssetLoader.getInstance().getTexture("Sprites/Cowboy2.png");
        playerSprite = new Sprite(playerTexture);
        aimArrowSprite = new Sprite(AssetLoader.getInstance().getTexture("Sprites/Arrow.png"));
        playerWalkAnimation = new Animation(0.2f, TextureRegion.split(playerTexture, playerTexture.getWidth() / 4, playerTexture.getHeight())[0]);
        playerWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        curFrameDuration = 0;
        aimArrowSprite.setSize(40, 6);
        aimArrowSprite.setOrigin(aimArrowSprite.getWidth() / 2, aimArrowSprite.getHeight() / 2);
        aimArrowSprite.setAlpha(0.7f);
    }

    @Override
    public void render(float delta, SpriteBatch batch)
    {
        curFrameDuration += delta;
        
        if(markForDisposal)
        {
            dispose();
            return;
        }
        
        renderPlayer(batch);
    }

    private void renderPlayer(SpriteBatch batch)
    {
        angle *= MathUtils.radiansToDegrees;

        if(vY != 0)
        {
            curFrameDuration = 0.49f;
        }
        
        if(Math.abs(vX) > 0.4f)
        {
            playerSprite.setRegion(playerWalkAnimation.getKeyFrame(curFrameDuration));
        }
        else
        {
            playerSprite.setRegion(playerWalkAnimation.getKeyFrame(0.0f));
        }
        
        if((extra & 0x1) == 0)
        {
            playerSprite.setAlpha(0.5f);
        }
        else
        {
            playerSprite.setAlpha(1);
        }

        if(angle < -90.1f || angle > 90.1f)
        {
            prevXDirWasRight = true;
        }
        else if(angle > -89.9f && angle < 89.9f)
        {
            prevXDirWasRight = false;
        }
        
        playerSprite.flip(prevXDirWasRight, false);

        playerSprite.setSize(ServerPlayer.WIDTH + 6f, ServerPlayer.HEIGHT + 1f);
        playerSprite.setOrigin(playerSprite.getWidth() / 2, playerSprite.getHeight() / 2);

        float x = position.x - playerSprite.getWidth() / 2;
        float y = position.y - playerSprite.getHeight() / 2 + ServerPlayer.YOFFSET;

        drawAll(playerSprite, batch, x, y);

        worldRenderer.hudRenderer.render(batch, x, y, extra, id, worldRenderer.stateProcessor.playerNames.players.get(id));

        x = position.x - aimArrowSprite.getWidth() / 2;
        y = position.y - aimArrowSprite.getHeight() / 2 + ServerPlayer.YOFFSET;
        aimArrowSprite.setRotation(angle);
        drawAll(aimArrowSprite, batch, x, y);
    }

    @Override
    public void dispose()
    {
    }
}
