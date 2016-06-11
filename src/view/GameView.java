package view;

import graph.Vertex;
import model.GameObject;
import view.components.Button;
import view.components.ViewComponent;
import view.rendering.Batch;

import java.util.List;

public class GameView extends View{

    private Button turretButton;
    private Button pause;
    private List<GameObject> currentObjects;
    private List<Vertex> currentWay;


    public GameView(){
        super();



    }

    @Override
    public void render(float deltaTime, Batch batch) {
        super.render(deltaTime, batch);

    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }

    @Override
    public List<ViewComponent> getComponents() {
        return super.getComponents();
    }

    @Override
    public void onMouseDown(int button, int mouseX, int mouseY) {
        super.onMouseDown(button, mouseX, mouseY);
    }

    @Override
    public void onKeyDown(int key, char c) {

    }

}
