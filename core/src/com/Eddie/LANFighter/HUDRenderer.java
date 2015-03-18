package com.Eddie.LANFighter;

import com.Eddie.LANFighter.Entities.Server.ServerPlayer;
import com.Eddie.LANFighter.Utils.AssetLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class HUDRenderer
{
    private Sprite bombSprite;
    private BitmapFont font;

    public HUDRenderer()
    {
        bombSprite = new Sprite(AssetLoader.getInstance().getTexture("Sprites/Bomb.png"));
        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = 7;
        font = new FreeTypeFontGenerator(Gdx.files.internal("Font/HUD Font.ttf")).generateFont(fontParameter);
    }

    public void render(SpriteBatch batch, float x, float y, short extra, int id, String name)
    {
        bombSprite.setSize(5, 5);
        int totalBombs = (extra >> 1) & 0x7;
        String score = name + ": " + Integer.toString((extra >> 4));
        float startX = x + ((ServerPlayer.WIDTH + 6f) / 2) - (font.getBounds(score).width / 2);
        font.draw(batch, score, startX, y + 35);
        for(int i = 0; i < totalBombs; i++)
        {
            bombSprite.setPosition(x + i * 6, y + 21);
            bombSprite.draw(batch);
        }
    }

    public void dispose()
    {
        font.dispose();
    }
}
