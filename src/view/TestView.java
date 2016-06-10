package view;


import view.components.Button;
import view.components.ViewComponent;
import view.rendering.Batch;

import java.util.*;

public class TestView extends View {

    private float rotationRadians = 0;

    public TestView(){
        super();

        components.add(new Button(150,150,50,50,this, "Test"));

    }

    @Override
    public void onKeyDown(int key, char c) {
        super.onKeyDown(key, c);
    }

    @Override
    public void render(float deltaTime, Batch batch) {
        rotationRadians += Math.toRadians(deltaTime * 50);

        batch.draw(ViewManager.test0, 0, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);
        batch.draw(ViewManager.test1, 200, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);
        batch.draw(ViewManager.test2, 400, 0, 200, 200, 100, 100, rotationRadians, 1f, 1f, 1f, 1f);

        ViewManager.font.drawText(batch, "Hallo Kabuom!     abcdefghijklmnopqrstuvwxyzß", 100, 300);

        super.render(deltaTime, batch);
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }
}
