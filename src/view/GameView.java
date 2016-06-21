package view;

import enemy.Enemy;
import model.GameObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import projectile.Projectile;
import tower.Tower;
import tower.TowerType;
import utility.Utility;
import utility.Vector2;
import view.components.TowerButton;
import view.components.ViewComponent;
import view.rendering.Batch;
import view.rendering.PostProcessingManager;
import world.World;

import java.util.List;

public class GameView extends View{

    private Utility u;

    private World world;
    private TowerButton[] towerButtons;
    private Tower setTower;
    private float w2,h2;

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
         if( o instanceof Enemy){
            batch.draw(o.getTexture(), o.getX(),o.getY(),o.getRadius(),o.getRadius());

        }else if( o instanceof Projectile){
            batch.draw(o.getTexture(), o.getX(),o.getY(),o.getRadius(),o.getRadius());
        }
    }


    @Override
    public void render(float deltaTime, Batch batch) {
        super.render(deltaTime, batch);
        if(originHeight < originWidth * 7/8) {
            h2 = originHeight;
            w2 = h2;
        }else{
            w2 = originWidth*7/8;
            h2 = w2;
        }
        for(int i = 0; i < world.getBlocks().length; i++){
            for(int j = 0; j < world.getBlocks()[i].length; j++){
                    batch.draw(ViewManager.test0,blockCoordToViewCoordX(i), blockCoordToViewCoordY(j), w2/world.getBlocks().length, h2/world.getBlocks()[i].length);
            }
        }
        batch.draw(ViewManager.test1,originWidth*7/8,(originHeight-h2)/2,originWidth*1/8,h2);
        if(world!=null && world.getObjects()!= null) {
            for (int i = 0; i < world.getObjects().size(); i++) {
                drawGameObject(world.getObjects().get(i), batch);
            }
            for(int i = 0; i < world.getBlocks().length; i++){
                for(int j = 0 ; j< world.getBlocks()[i].length ; j++ ) {
                    System.out.println(world);
                    System.out.println(world.getBlocks()[i][j].getContent());
                    if(world.getBlocks()[i][j].getContent().getType() == TowerType.DUMMY){

                    }else{
                        GameObject o = world.getBlocks()[i][j].getContent();
                        float angle = 0;
                        if(((Tower)o).getTarget() != null)
                            angle = Utility.calculateAngleBetweenTwoPoints(o.getX() + o.getRadius() / 2, o.getY() + o.getRadius() / 2, ((Tower) o).getTarget().getX() + ((Tower) o).getTarget().getRadius() / 2, ((Tower) o).getTarget().getY() + ((Tower) o).getTarget().getRadius() / 2);
                        float oX = blockCoordToViewCoordX(i);
                        float oY = blockCoordToViewCoordY(j);
                        float oW = w2/world.getBlocks().length;
                        float oH = h2/world.getBlocks()[i].length;
                        batch.draw(ViewManager.mgTurret, oX, oY, oW ,oH , oW/2,oH/2, angle, 1f,1f,1f,1f);
                    }
                }
            }
        }
        //batch.draw(ViewManager.world1,originWidth * 7 / 8 / 2- w2 / 2, 0, w2, h2);
        /**
         * Alles Was mit dem Towersetzen zu tun hat
         */
        for(int i = 0; i < towerButtons.length; i++){
            towerButtons[i].draw(batch,h2/towerButtons.length *i + (originHeight-h2)/2);
        }

        if(setTower != null) {
            batch.draw(ViewManager.mgTurret, Mouse.getX() - setTower.getRadius() / 2, originHeight - Mouse.getY() - setTower.getRadius() / 2, setTower.getRadius(), setTower.getRadius());
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

    public Vector2 getBlockIDOfMouse(float mouseX,float mouseY){

        float x = ((originWidth * 7 / 8) / 2 - w2 / 2);
        float y = 0;
        float w = x + w2;
        float h = y + h2;
        if(mouseX > x && mouseY > y && mouseX < w &&mouseY < h) {
            //System.out.println((mouseX - (originWidth * 7 / 8 - w2) / 2 )*world.getBlocks().length/ w2 +" "+ (mouseY - (originHeight  - h2) / 2 )*world.getBlocks().length/ h2);
            return  new Vector2((mouseX - (originWidth * 7 / 8 - w2) / 2 )*world.getBlocks().length/ w2,(mouseY - (originHeight  - h2) / 2 )*world.getBlocks().length/ h2);
        }
        return null;
    }

    private float blockCoordToViewCoordX(float coord){
        return w2/world.getBlocks().length * coord+ (originWidth*7/8-w2) / 2;
    }

    private float blockCoordToViewCoordY(float coord){
        return h2/world.getBlocks().length * coord+ (originHeight-h2) / 2;
    }

    @Override
    public List<ViewComponent> getComponents() {
        return super.getComponents();
    }

    @Override
    public void onMouseDown(int button, int mouseX, int mouseY) {
        super.onMouseDown(button, mouseX, mouseY);

        if(setTower != null) {
            if (button == 1) {
                viewManager.getPostProcessingManager().disableEffect(PostProcessingManager.Effect.RadialBlur);
                setTower = null;
            }
        }
        if (getBlockIDOfMouse(mouseX, mouseY) != null) {

            if (button == 0){
                if (world.CanSetTowerInBlocks((int) getBlockIDOfMouse(mouseX, mouseY).getCoords()[0], (int) getBlockIDOfMouse(mouseX, mouseY).getCoords()[1], (setTower))) {
                    viewManager.getPostProcessingManager().disableEffect(PostProcessingManager.Effect.RadialBlur);
                    setTower = null;
                }
            }
        }
        for(int i = 0; i < towerButtons.length; i++) {
            if (setTower == null) {
                if (towerButtons[i].buttonPressed()) {
                    System.out.println("pressed");
                    viewManager.getPostProcessingManager().enableEffect(PostProcessingManager.Effect.RadialBlur);
                    setTower = new Tower(towerButtons[i].getTowerType(), 0, "Tower" + i, mouseX, mouseY, h2 / world.getBlocks().length);
                }
            }
        }
    }

    @Override
    public void onKeyDown(int key, char c) {
        if (key == Keyboard.KEY_1) {
            this.getViewManager().getPostProcessingManager().enableEffect(PostProcessingManager.Effect.RadialBlur);
        }
        if (key == Keyboard.KEY_2) {
            this.getViewManager().getPostProcessingManager().disableEffect(PostProcessingManager.Effect.RadialBlur);
        }
        if (key == Keyboard.KEY_3) {
            this.getViewManager().getPostProcessingManager().enableEffect(PostProcessingManager.Effect.Drunk);
        }
        if (key == Keyboard.KEY_4) {
            this.getViewManager().getPostProcessingManager().disableEffect(PostProcessingManager.Effect.Drunk);
        }
    }
}
