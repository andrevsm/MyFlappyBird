package com.avsm.myflappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyFlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture canoBaixo2;
    private Texture canoTopo2;
    private Texture gameOver;
    private ImageButton playImageButton;
    private ImageButton refreshImageButton;
    private Random numeroRandomico;
    private BitmapFont placar;
    private BitmapFont mensagemGameOver;
    private BitmapFont tituloGame;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    private Rectangle retanguloCanoTopo2;
    private Rectangle retanguloCanoBaixo2;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Sound jumpSound;
    private Sound gameOverSound;

    private final float VIRTUAL_WIDTH = 720;
    private final float VIRTUAL_HEIGHT = 1440;
    private float larguraTela;
    private float alturaTela;
    private int pontuacao = 0;
    private int rotacaoPassaro = 0;

    //Estado 0 = jogo não iniciado.
    //Estado 1 = jogo iniciado.
    //Estado 2 = game over.
    private int estadoDoJogo = 0;
    private float posicaoInicialVerticalDoPassaro;
    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoMovimentoCanoHorizontal;
    private float posicaoMovimentoCano2Horizontal;
    private float espacoEntreCanos;
    private float alturaEntreCanosRandomica;
    private float alturaEntreCanosRandomica2;
    private boolean marcouPonto;

    @Override
    public void create() {
        inicializandoObjetos();
        configInicial();
    }

    @Override
    public void render() {
        //Atualizar visualização da camera
        camera.update();

        //Limpar frames de execuções anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        //Bater de asas do passaro
        variacao += deltaTime * 10;
        if (variacao > 2) variacao = 0;

        //Inicio do jogo
        if (estadoDoJogo == 0) {
            inicioJogo();
        } else {
            //Velocidade de queda do passaro
            velocidadeQueda++;
            rotacaoPassaro -= deltaTime * 60;
            //Queda do Passaro na posição vertical
            if (posicaoInicialVerticalDoPassaro > 0 || velocidadeQueda < 0) {
                posicaoInicialVerticalDoPassaro = posicaoInicialVerticalDoPassaro - velocidadeQueda;
            }

            if (estadoDoJogo == 1) {
                //Velocidade de movimentação dos Canos
                posicaoMovimentoCanoHorizontal -= deltaTime * 400;
                posicaoMovimentoCano2Horizontal -= deltaTime * 400;

                //Touch do pulo do passaro
                if (Gdx.input.justTouched()) {
                    rotacaoPassaro -= rotacaoPassaro - 20;
                    velocidadeQueda = -15;
                    jumpSound.play(1.0f);
                }

                //Verifica se o cano 1 saiu da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraTela;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(500) - 250;
                    marcouPonto = false;
                }

                //Verifica se o cano 2 saiu da tela
                if (posicaoMovimentoCano2Horizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCano2Horizontal = posicaoMovimentoCanoHorizontal + larguraTela / 2;
                    alturaEntreCanosRandomica2 = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //Verifica se marcou ponto
                if (posicaoMovimentoCanoHorizontal < 120 ||
                        posicaoMovimentoCano2Horizontal < 120) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }

            } else {
                //FimDeJogo
                reiniciarJogo();
            }
        }

        configDoBatch();

        instanciandoAsFormasDeColisao();

        verificaColisao();
    }

    private void inicializandoObjetos() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        //Numero randomico de movimentação dos canos
        numeroRandomico = new Random();

        //Placar de Pontos
        placar = new BitmapFont();
        placar.setColor(Color.WHITE);
        placar.getData().setScale(6);

        //Titulo Game
        tituloGame = new BitmapFont();
        tituloGame.setColor(Color.WHITE);
        tituloGame.getData().setScale(6);

        //Mensagem de GameOver
        mensagemGameOver = new BitmapFont();
        mensagemGameOver.setColor(Color.WHITE);
        mensagemGameOver.getData().setScale(3);

        //Forma do Passaro para colisão
        passaroCirculo = new Circle();

        //Textura do play button
        playImageButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("play_button.png"))));

        //Textura do refresh button
        refreshImageButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("refresh_button.png"))));

        //Texturas do passaro
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        //Textura do plano de fundo
        fundo = new Texture("fundo.png");

        //Textura dos canos
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");
        canoBaixo2 = new Texture("cano_baixo_maior.png");
        canoTopo2 = new Texture("cano_topo_maior.png");

        //Textura de GameOver
        gameOver = new Texture("game_over.png");

        //Som ao pular
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump_sound.wav"));

        //Som de gameover
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("gameOver.wav"));
    }

    private void configInicial() {
        //Configuração de posicionamento da camera
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        //Config de layout
        larguraTela = VIRTUAL_WIDTH;
        alturaTela = VIRTUAL_HEIGHT;

        //Config do stage
        stage = new Stage(new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        //Posição do botao play na tela
        playImageButton.setPosition(larguraTela / 2 - 70, alturaTela / 2 - 50);

        //Posição do botao refresh na tela
        refreshImageButton.setPosition(larguraTela / 2 - 70, alturaTela / 2 - 250);

        //Posição que o Passaro começa
        posicaoInicialVerticalDoPassaro = alturaTela / 2;

        //Posição que os Canos começam e espaçamento
        posicaoMovimentoCanoHorizontal = larguraTela;
        posicaoMovimentoCano2Horizontal = posicaoMovimentoCanoHorizontal + larguraTela / 2;
        espacoEntreCanos = 250;
    }

    private void inicioJogo() {
        //Tocou no botão para começar
        playImageButton.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                estadoDoJogo = 1;
                return false;
            }
        });
    }

    private void configDoBatch() {
        //Configurar dados de projeção da camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        //Desenhando plano de fundo
        batch.draw(fundo, 0, 0, larguraTela, alturaTela);

        //Desenhando titulo do game
        if (estadoDoJogo == 0) {
            tituloGame.draw(batch, "Flappy Bird", larguraTela / 2 - 200, alturaTela - 100);
        }

        //Desenhando os Canos
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaTela / 2
                + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaTela / 2
                - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
        batch.draw(canoTopo2, posicaoMovimentoCano2Horizontal, alturaTela / 2
                + espacoEntreCanos / 2 + alturaEntreCanosRandomica2);
        batch.draw(canoBaixo2, posicaoMovimentoCano2Horizontal, alturaTela / 2
                - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica2);

        //Desenhando array de passaros
//        batch.draw(passaros[(int) variacao], 120, posicaoInicialVerticalDoPassaro);
        Sprite sprite = new Sprite(passaros[(int) variacao]);
        sprite.rotate(rotacaoPassaro);
        sprite.setPosition(120, posicaoInicialVerticalDoPassaro);
        sprite.draw(batch);

        //Desenhando pontuação
        if (estadoDoJogo == 1 || estadoDoJogo == 2) {
            placar.draw(batch, String.valueOf(pontuacao), larguraTela / 2, alturaTela - 50);
        }

        //Tela de fim de jogo
        if (estadoDoJogo == 2) {
            batch.draw(gameOver, larguraTela / 2 - gameOver.getWidth() / 2, alturaTela / 2);
            mensagemGameOver.draw(batch, "Toque para reiniciar!",
                    larguraTela / 2 - 200, alturaTela / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

        //Desenhando stage do play button
        if (estadoDoJogo == 0) {
            stage.clear();
            stage.addActor(playImageButton);
            stage.act();
            stage.draw();
        }

        //Desenhando stage do refresh button
        if (estadoDoJogo == 2) {
            stage.clear();
            stage.addActor(refreshImageButton);
            stage.act();
            stage.draw();
        }
    }

    private void reiniciarJogo() {
        rotacaoPassaro = 0;

        //Tocou no botão refresh
        refreshImageButton.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                pontuacao = 0;
                estadoDoJogo = 0;
                velocidadeQueda = 0;
                marcouPonto = false;
                posicaoInicialVerticalDoPassaro = alturaTela / 2;
                posicaoMovimentoCanoHorizontal = larguraTela;
                posicaoMovimentoCano2Horizontal = posicaoMovimentoCanoHorizontal + larguraTela / 2;
                return false;
            }
        });
    }

    private void instanciandoAsFormasDeColisao() {
        passaroCirculo.set(120 + passaros[0].getWidth() / 2,
                posicaoInicialVerticalDoPassaro + passaros[0].getHeight() / 2,
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

        retanguloCanoBaixo2 = new Rectangle(
                posicaoMovimentoCano2Horizontal, alturaTela / 2 - canoBaixo.getHeight()
                - espacoEntreCanos / 2 + alturaEntreCanosRandomica2,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        retanguloCanoTopo2 = new Rectangle(
                posicaoMovimentoCano2Horizontal, alturaTela / 2
                + espacoEntreCanos / 2 + alturaEntreCanosRandomica2,
                canoTopo.getWidth(), canoTopo.getHeight()
        );
    }

    private void verificaColisao() {
        //Verifica se houve colisão
        if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo)
                || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
                || Intersector.overlaps(passaroCirculo, retanguloCanoTopo2)
                || Intersector.overlaps(passaroCirculo, retanguloCanoBaixo2)
                || posicaoInicialVerticalDoPassaro <= 0
                || posicaoInicialVerticalDoPassaro >= alturaTela) {
            if(estadoDoJogo == 1) {
                gameOverSound.play(1.0f);
            }
            estadoDoJogo = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}

