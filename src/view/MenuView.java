package view;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import view.components.Button;
import view.components.ButtonListener;
import view.rendering.Batch;
import view.rendering.Particle;
import view.rendering.ParticleEffect;
import view.rendering.ParticleManager;

import java.util.Random;

public class MenuView extends View{

    private Button startButton;
    private Button optionsButton;
    private Button exitButton;

    private float timer = 0f;

    public MenuView(float width, float height, final ViewManager viewManager) {
        super(width, height, viewManager);
        startButton = new Button((width / 2) - (width / 6 / 2), (height / 2) - (height / 8 / 2) - (height / 6), width / 6, height / 8, this, "Start", ViewManager.buttonMainTexture,ViewManager.buttonPressedTexture);
        optionsButton = new Button((width / 2) - (width / 6 / 2), (height / 2) - (height / 8 / 2), width / 6, height / 8, this, "Options", ViewManager.buttonMainTexture,ViewManager.buttonPressedTexture);
        exitButton = new Button((width / 2) - (width / 6 / 2), (height / 2) - (height / 8 / 2) + (height / 6), width / 6, height / 8, this, "Exit", ViewManager.buttonMainTexture,ViewManager.buttonPressedTexture);
        this.components.add(startButton);
        this.components.add(optionsButton);
        this.components.add(exitButton);

        startButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                viewManager.setCurrentView(new GameView(originWidth,originHeight, viewManager, viewManager.getCtrl().createNewWorld() ));
            }});

        optionsButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
            }});

        exitButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                System.exit(0);
            }});
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);

        startButton.setX((width / 2) - (width / 6 / 2));
        startButton.setY((height / 2) - (height / 8 / 2) - (height / 6));
        startButton.setWidth(width / 6);
        startButton.setHeight(height / 8);

        optionsButton.setX((width / 2) - (width / 6 / 2));
        optionsButton.setY((height / 2) - (height / 8 / 2));
        optionsButton.setWidth(width / 6);
        optionsButton.setHeight(height / 8);

        exitButton.setX((width / 2) - (width / 6 / 2));
        exitButton.setY((height / 2) - (height / 8 / 2) + (height / 6));
        exitButton.setWidth(width / 6);
        exitButton.setHeight(height / 8);
    }

    @Override
    public void render(float deltaTime, Batch batch) {
        super.render(deltaTime, batch);

        timer += deltaTime;

        while(timer > 0.1f) {
            timer -= 0.1f;

            ParticleManager manager = this.getViewManager().getParticleManager();

            Particle.ParticleRandomizer randomizer = new Particle.ParticleRandomizer() {
                Random random = new Random();

                @Override
                public float randomizeDir(float dir) {
                    return (float) random.nextInt(360);
                }
                @Override
                public float randomizeSpeed(float speed) {
                    return speed + random.nextFloat() * 10f - 5f;
                }
                @Override
                public float randomizeSize(float size) {
                    return size;
                }
                @Override
                public float randomizeLifeTime(float lifeTime) {
                    return lifeTime + random.nextFloat() * 3f - 1f;
                }
                @Override
                public float[] randomizeColor(float[] color) {
                    return new float[] {random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.9f};
                }
            };

            int mx = Mouse.getX();
            int my = Display.getHeight() - Mouse.getY();
            ParticleEffect effect = new ParticleEffect(15, mx, my, 0f, 50f, 4f, 4.0f, new float[] {1.0f, 1.0f, 1.0f, 1.0f}, randomizer);

            manager.emit(effect);
        }
    }

}
