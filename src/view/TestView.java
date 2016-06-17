package view;


import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import utility.Utility;
import view.components.Button;
import view.components.ViewComponent;
import view.rendering.Batch;
import view.rendering.Particle;
import view.rendering.ParticleEffect;

import java.awt.*;
import java.util.*;

public class TestView extends View {

    private float rotationRadians = 0;

    private Button testButton;

    private float testX1,testY1,testX2,testY2;



    public TestView(float width, float height) {
        super(width, height);
    }

    public TestView(float width, float height, ViewManager viewManager){
        super(width, height, viewManager);

        testButton=new Button(300, 300, 50, 50, this, "Test");

        components.add(testButton);
    }

    @Override
    public void onKeyDown(int key, char c) {
        if(key== Keyboard.KEY_S){
            testY1 = testY1+20;
        }else
        if(key== Keyboard.KEY_W){
            testY1 = testY1-20;
        }else
        if(key== Keyboard.KEY_A){
            testX1 = testX1 -20;
        }else
        if(key== Keyboard.KEY_D){
            testX1 = testX1 +20;
        }else
        if(key == Keyboard.KEY_DOWN){
            testY2 = testY2 +20;
        }else
        if(key == Keyboard.KEY_UP){
            testY2 = testY2 -20;
        }else
        if(key == Keyboard.KEY_LEFT){
            testX2 = testX2 -20;
        }else
        if(key == Keyboard.KEY_RIGHT){
            testX2 = testX2 +20;
        }
    }

    @Override
    public void render(float deltaTime, Batch batch) {
        float h2,w2;
        if(originHeight < originWidth * 7/8) {
            h2 = originHeight;
            w2 = h2;
        }else{
            w2 = originWidth*7/8;
            h2 = w2;
        }
        //batch.draw(ViewManager.mgTurret,0,0,200,200);
        batch.draw(ViewManager.world1,originWidth*7/8/2-w2/2,0,w2,h2);

        if(Mouse.isButtonDown(0)) {
            flame();

            for (int i = 100; i < 600; i += 50) {
                batch.draw(ViewManager.test0,(i), (450), (25), (25), 12, 12, rotationRadians, 1f, 1f, 1f, 1f);
            }
            ViewManager.font.drawText(batch, "Hallo Kabuom!     abcdefghijklmnopqrstuvwxyzß", (int)(100), (int) (400));
        }

        rotationRadians += Math.toRadians(deltaTime * 50);

        //batch.draw(ViewManager.test1, (testX2), (testY2), (200), (200), (200/2), (200/2),(float) (Utility.calculateAngleBetweenTwoPoints(testX2+200/2,testY2+200/2, testX1,testY1)+ Math.PI), 1f, 1f, 1f, 1f);
        //batch.draw(ViewManager.test2, 400, 0, 200, 200, 100, 100, (float) 3, 1f, 1f, 1f, 1f);

        //batch.draw(ViewManager.test0, (testX1), (testY1), 5, 5, 100, 100, (float) 0, 1f, 1f, 1f, 1f);
        //System.out.println("1 : " +testX1 + " / " + testY1);


        //batch.draw(ViewManager.test0, Utility.layoutX(testX1), Utility.layoutY(testY1), 5, 5, 100, 100, (float) 0, 1f, 1f, 1f, 1f);
        //System.out.println(new Utility().calculateAngleBetweenTwoPoints(testX2+100,testY2+100, testX1,testY1));


        super.render(deltaTime, batch);
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }

    public void flame() {
        Particle.ParticleRandomizer randomizer = new Particle.ParticleRandomizer() {
            private Random random = new Random();
            @Override
            public float randomizeDir(float dir) {
                return dir + random.nextInt(60) - 30;
            }
            @Override
            public float randomizeSpeed(float speed) {
                return speed + random.nextFloat() * 10 - 5;
            }
            @Override
            public float randomizeSize(float size) {
                return size;
            }
            @Override
            public float randomizeLifeTime(float lifeTime) {
                return lifeTime + random.nextFloat() * 3 - 1;
            }
            @Override
            public float[] randomizeColor(float[] color) {
                float r = 1.0f;
                float g = random.nextFloat() * 0.8f;
                float b = 0.0f;

                return new float[] {r, g, b, 0.8f};
            }
        };
        ParticleEffect effect = new ParticleEffect(15, 400, 400, 270, 160f, 4, 1.5f, new float[] {1.0f, 1.0f, 1.0f, 1.0f}, randomizer);
        viewManager.getParticleManager().emit(effect);
    }

}
