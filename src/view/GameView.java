package view;

import enemy.Enemy;
import graph.Vertex;
import model.GameObject;
import projectile.Projectile;
import tower.Tower;
import utility.Utility;
import view.components.Button;
import view.components.ViewComponent;
import view.rendering.Batch;
import java.util.List;

public class GameView extends View{

    private Utility u;


    private List<GameObject> currentObjects;
    private Button[] towerButtons;

    public GameView(float width, float height, ViewManager viewManager){
        super(width,height, viewManager);
        u = new Utility();

    }

    public void drawGameObject(GameObject o,Batch batch){
        if(o instanceof Tower){
            float angle = u.calculateAngleBetweenTwoPoints(o.getX(),o.getY(),100,100);
           // batch.draw(o.getTexture(),o.getX(),o.getY(),o.getRadius(),o.getRadius(),100,100,Utility.calculateAngleBetweenTwoPoints(o.getX()+o.getRadius()/2,o.getY()+o.getRadius()/2,o.getTarget().getX()+o.getTarget().getRadius()/2,o.getTarget().getY()+o.getTarget.getRadius()/2) ,1f,1f,1f,1f);

        }else if( o instanceof Enemy){
            batch.draw(o.getTexture(), o.getX(),o.getY(),o.getRadius(),o.getRadius());

        }else if( o instanceof Projectile){
            batch.draw(o.getTexture(), o.getX(),o.getY(),o.getRadius(),o.getRadius());
        }
    }


    @Override
    public void render(float deltaTime, Batch batch) {
        super.render(deltaTime, batch);
        for(int i = 0 ; i< currentObjects.size(); i++){
            drawGameObject(currentObjects.get(i),batch);
        }

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
