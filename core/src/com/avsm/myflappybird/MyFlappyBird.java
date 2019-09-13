package com.avsm.myflappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyFlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

//    private ShapeRenderer shape;

    //Atributos de configuração
    private float larguraTela;
    private float alturaTela;

    private int pontuacao = 0;

    //Estado 0 = jogo não iniciado.
    //Estado 1 = jogo iniciado.
    //Estado 2 = game over.
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

        //Configuração de Camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        larguraTela = VIRTUAL_WIDTH;
        alturaTela = VIRTUAL_HEIGHT;

        numeroRandomico = new Random();

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        passaroCirculo = new Circle();

//        retanguloCanoBaixo = new Rectangle();
//        retanguloCanoTopo = new Rectangle();
//        shape = new ShapeRenderer();

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        posicaoInicialVertical = alturaTela / 2;
        posicaoMovimentoCanoHorizontal = larguraTela - 100;
        espacoEntreCanos = 250;

    }

    @Override
    public void render() {
        camera.update();

//      Limpar frames de execuções anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();

        //Bater de asas do passaro
        variacao += deltaTime * 10;
        if (variacao > 2) variacao = 0;

        //Inicio do jogo
        if (estadoDoJogo == 0) {
            if (Gdx.input.justTouched()) estadoDoJogo = 1;
        } else {
            //Velocidade de queda
            velocidadeQueda++;

            if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
            }

            if (estadoDoJogo == 1) {
                posicaoMovimentoCanoHorizontal -= deltaTime * 500;

                //Touch com o Gdx
                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }
                //Verifica se o cano saiu da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraTela;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(500) - 250;
                    marcouPonto = false;
                }

                //Verifica pontuação
                if (posicaoMovimentoCanoHorizontal < 120) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            } else {
                //Tela de game over
                if (Gdx.input.justTouched()) {
                    pontuacao = 0;
                    estadoDoJogo = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaTela / 2;
                    posicaoMovimentoCanoHorizontal = larguraTela;
                }


            }

        }

        //Configurar dados de projeção da camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo, 0, 0, larguraTela, alturaTela);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaTela / 2
                + espacoEntreCanos / 2 + alturaEntreCanosRandomica);

        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaTela / 2
                - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraTela / 2, alturaTela - 50);

        if (estadoDoJogo == 2) {
            batch.draw(gameOver, larguraTela / 2 - gameOver.getWidth() / 2, alturaTela / 2);
            mensagem.draw(batch, "Toque para reiniciar!", larguraTela / 2 - 200, alturaTela / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

        passaroCirculo.set(120 + passaros[0].getWidth() / 2,
                posicaoInicialVertical + passaros[0].getHeight() / 2,
                passaros[0].getWidth() / 2);

        retanguloCanoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaTela / 2 - canoBaixo.getHeight()
                - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        retanguloCanoTopo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaTela / 2
                + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

//        //Desenhando as formas
//        shape.begin(ShapeRenderer.ShapeType.Filled);
//
//        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
//        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
//        shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
//        shape.setColor(Color.RED);
//
//        shape.end();

        //Teste de colisão
        if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo)
                || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
                || posicaoInicialVertical <= 0
                || posicaoInicialVertical >= alturaTela) {
            estadoDoJogo = 2;

        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}

