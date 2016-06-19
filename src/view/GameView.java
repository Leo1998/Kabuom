package view;

import enemy.Enemy;
import graph.Vertex;
import model.GameObject;
import org.lwjgl.input.Mouse;
import projectile.Projectile;
import tower.Tower;
import tower.TowerType;
import utility.Utility;
import view.components.Button;
import view.components.TowerButton;
import view.components.ViewComponent;
import view.rendering.Batch;
import world.World;

import java.util.List;

public class GameView extends View{

    private Utility u;

    private World world;
    private TowerButton[] towerButtons;
    private Tower setTower;

    public GameView(float width, float height, ViewManager viewManager, World world){
        super(width,height, viewManager);
        this.world = world;
        towerButtons = new TowerButton[TowerType.values().length];
        for(int i= 0 ; i < TowerType.values().length; i++){
           towerButtons[i] = new TowerButton(width * 7/8, i * height/TowerType.values().length,width* 1/8,height/TowerType.values().length, this, null, viewManager.mgTurretGreen,viewManager.mgTurretRed,TowerType.values()[i]);
        }
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
        batch.draw(ViewManager.test1,originWidth*7/8,0,originWidth*1/8,originHeight);
        if(world!=null && world.getObjects()!= null) {
            for (int i = 0; i < world.getObjects().size(); i++) {
                drawGameObject(world.getObjects().get(i), batch);
            }
        }
        float h2,w2;
        if(originHeight < originWidth * 7/8) {
            h2 = originHeight;
            w2 = h2;
        }else{
            w2 = originWidth*7/8;
            h2 = w2;
        }
        batch.draw(ViewManager.world1,originWidth * 7 / 8 / 2- w2 / 2, 0, w2, h2);

        for(int i = 0; i < TowerType.values().length; i++){
            towerButtons[i].draw(batch);
            if(setTower == null){
                if(towerButtons[i].buttonPressed()){
                    setTower = new Tower(towerButtons[i].getTowerType(), 0, "Tower" + i,Mouse.getX(),originHeight- Mouse.getY(),100);
                }
            }
        }


        if(setTower != null){
            batch.draw(ViewManager.mgTurret,Mouse.getX()-setTower.getRadius()/2,originHeight-Mouse.getY()-setTower.getRadius()/2,setTower.getRadius(),setTower.getRadius());
            if(Mouse.isButtonDown(1))
                setTower = null;
        }
    }



    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
        for (int i = 0; i <towerButtons.length ; i++){
            towerButtons[i].setX(width * 7/8);
            towerButtons[i].setY( i * height/towerButtons.length);
            towerButtons[i].setWidth(width* 1/8);
            towerButtons[i].setHeight(height/towerButtons.length);
        }
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
