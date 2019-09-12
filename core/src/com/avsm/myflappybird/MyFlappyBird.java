package com.avsm.myflappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyFlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;

    //Atributos de configuração
    private int larguraTela;
    private int alturaTela;

    private float posicaoInicialVertical;
    private float variacao = 0;
    private float velocidadeQueda = 0;

    @Override
    public void create() {
        Gdx.app.log("ANDRE", "Entrou no create.");
        batch = new SpriteBatch();

        larguraTela = Gdx.graphics.getWidth();
        alturaTela = Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaTela / 2;

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
    }

    @Override
    public void render() {
        //Bater de asas do passaro
        variacao += (Gdx.graphics.getDeltaTime() * 10);
        if (variacao > 2) variacao = 0;

        //Velocidade de queda
        velocidadeQueda++;

        //Touch com o Gdx
        if (Gdx.input.justTouched()) {
            velocidadeQueda = -30;
        }


        if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
            posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
        }

        batch.begin();

        batch.draw(fundo, 0, 0, larguraTela, alturaTela);
        batch.draw(passaros[(int) variacao], 50, posicaoInicialVertical, 100, 68);

        batch.end();
    }
}
