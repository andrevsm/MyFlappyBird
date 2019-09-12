package com.avsm.myflappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyFlappyBird extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture passaro;
    private Texture fundo;
    private int movimento = 0;
    private int meioDaTela;

    @Override
    public void create() {
        Gdx.app.log("ANDRE", "Entrou no create.");
        batch = new SpriteBatch();
        passaro = new Texture("passaro1.png");
        fundo = new Texture("fundo.png");
    }

    @Override
    public void render() {
        movimento++;

        batch.begin();

        batch.draw(fundo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        meioDaTela = Gdx.graphics.getHeight() / 2;
        batch.draw(passaro, movimento, meioDaTela, 100, 77);

        batch.end();
    }
}
