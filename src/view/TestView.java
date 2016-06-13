package view;


import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import utility.Utility;
import view.components.Button;
import view.components.ViewComponent;
import view.rendering.Batch;

import java.util.*;

public class TestView extends View {

    private float rotationRadians = 0;

    private Button testButton;

    private int testX1,testY1,testX2,testY2;

    public TestView(){
        super();

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
        if(testButton.buttonPressed()) {
            for (int i = 100; i < 600; i += 50) {
                batch.draw(ViewManager.test0, i, 450, 25, 25, 12, 12, rotationRadians, 1f, 1f, 1f, 1f);
            }
            ViewManager.font.drawText(batch, "Hallo Kabuom!     abcdefghijklmnopqrstuvwxyzß", 100, 400);
        }

        rotationRadians += Math.toRadians(deltaTime * 50);

        batch.draw(ViewManager.test1, testX2, testY2, 200, 200, 100, 100,(float) (new Utility().calculateAngleBetweenTwoPoints(testX2+100,testY2+100, testX1,testY1)), 1f, 1f, 1f, 1f);
        //batch.draw(ViewManager.test2, 400, 0, 200, 200, 100, 100, (float) 3, 1f, 1f, 1f, 1f);
        batch.draw(ViewManager.test0, testX1, testY1, 5, 5, 100, 100, (float) 0, 1f, 1f, 1f, 1f);
        System.out.println(new Utility().calculateAngleBetweenTwoPoints(testX2+100,testY2+100, testX1,testY1));



        super.render(deltaTime, batch);
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }
}
