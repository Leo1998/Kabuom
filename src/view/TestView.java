package view;


import view.components.Button;
import view.components.ViewComponent;
import view.rendering.Batch;

import java.util.*;

public class TestView extends View {

    private float rotationRadians = 0;

    private Button testButton;

    public TestView(){
        super();

        testButton=new Button(300,300,50,50,this, "Test");
        components.add(testButton);

    }

    @Override
    public void onKeyDown(int key, char c) {

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

        batch.draw(ViewManager.test0, 0, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);
        batch.draw(ViewManager.test1, 200, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);
        batch.draw(ViewManager.test2, 400, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);

        super.render(deltaTime, batch);
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }
}
