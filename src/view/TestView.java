package view;


import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import utility.Utility;
import view.components.Button;
import view.components.ViewComponent;
import view.rendering.Batch;

import java.util.*;

public class TestView extends View {

    private float rotationRadians = 0;

    private Button testButton;

    private float testX1,testY1,testX2,testY2;



    public TestView(float width, float height){
        super(width, height);

        testButton=new Button(300,300,50,50,this, "Test");
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

        if(testButton.buttonPressed()) {
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



        super.render(deltaTime, batch);
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }




}
