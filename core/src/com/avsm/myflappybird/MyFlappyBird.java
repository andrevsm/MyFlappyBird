package com.avsm.myflappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class MyFlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Random numeroRandomico;
    private BitmapFont fonte;

    //Atributos de configuração
    private int larguraTela;
    private int alturaTela;

    private int pontuacao = 0;

    //Estado 0 = jogo não iniciado.
    //Estado 1 = jogo iniciado.
    private int estadoDoJogo = 0;

    private float posicaoInicialVertical;
    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto;

    @Override
    public void create() {
        Gdx.app.log("ANDRE", "Entrou no create.");
        batch = new SpriteBatch();

        larguraTela = Gdx.graphics.getWidth();
        alturaTela = Gdx.graphics.getHeight();

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");

        posicaoInicialVertical = alturaTela / 2;
        posicaoMovimentoCanoHorizontal = larguraTela - 100;
        espacoEntreCanos = 300;

        numeroRandomico = new Random();
    }

    @Override
    public void render() {
        deltaTime = Gdx.graphics.getDeltaTime();

        //Bater de asas do passaro
        variacao += deltaTime * 10;
        if (variacao > 2) variacao = 0;

        //Inicio do jogo
        if (estadoDoJogo == 0) {
            if (Gdx.input.justTouched()) {
                estadoDoJogo = 1;
            }
        } else {
            //Velocidade de queda
            velocidadeQueda++;

            //Touch com o Gdx
            if (Gdx.input.justTouched()) {
                velocidadeQueda = -15;
            }

            if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
            }

            //Movimentação do Cano
            //Verifica se o cano saiu da tela
            posicaoMovimentoCanoHorizontal -= deltaTime * 300;
            if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                posicaoMovimentoCanoHorizontal = larguraTela;
                alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                marcouPonto = false;
            }


            //Verifica pontuação
            if (posicaoMovimentoCanoHorizontal < 120) {
                if(!marcouPonto) {
                    pontuacao++;
                    marcouPonto = true;
                }
            }

        }

        batch.begin();

        batch.draw(fundo, 0, 0, larguraTela, alturaTela);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaTela / 2
                + espacoEntreCanos / 2 + alturaEntreCanosRandomica);

        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaTela / 2
                - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraTela / 2, alturaTela - 50);

        batch.end();

    }
}
