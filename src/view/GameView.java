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
import view.components.ButtonListener;
import view.components.TowerButton;
import view.components.ViewComponent;
import view.rendering.Batch;
import view.rendering.ITexture;
import view.rendering.PostProcessingManager;
import world.World;

import java.util.List;

public class GameView extends View{

    private Utility u;

    private World world;
    private TowerButton[] towerButtons;
    private Tower setTower;
    private float w2,h2;
    private ITexture blockTexture,towerButtonBackgroundTexture;

    public GameView(float width, float height, final ViewManager viewManager, final World world){
        super(width,height, viewManager);

        blockTexture = ViewManager.getTexture("block1.png");
        towerButtonBackgroundTexture = ViewManager.getTexture("viewTextures/mainButton.png");

        float towerButtonX = width * 7/8;
        float towerButtonHeight = height/TowerType.values().length;
        float towerButtonWidth = width * 1/8;

        this.world = world;
        towerButtons = new TowerButton[TowerType.values().length];
        for(int i= 0 ; i < towerButtons.length; i++){
            if (TowerType.values()[i] == TowerType.MAINTOWER) {
                continue;
            }

            final TowerButton towerButton = new TowerButton(towerButtonX, i * towerButtonHeight, towerButtonWidth, towerButtonHeight, this, null, TowerType.values()[i]);

            towerButtons[i] = towerButton;
            this.components.add(towerButton);

            towerButton.setListener(new ButtonListener() {
                @Override
                public void onClick() {
                    if (setTower == null) {
                        viewManager.getPostProcessingManager().enableEffect(PostProcessingManager.Effect.RadialBlur);

                        int cost = towerButton.getTowerType().getCost();
                        if (world.getCoins() > cost) {
                            setTower = new Tower(towerButton.getTowerType(), 0, 0, 0, 0);
                        }
                    }
                }
            });
        }
        u = new Utility();
    }

    public void drawGameObject(GameObject o,Batch batch){
         if(o instanceof Enemy){
             Enemy e = (Enemy) o;
             
             float percentage = (float) e.getMaxHp() / (float) e.getHp();

             batch.draw(ViewManager.getTexture(e.getEnemyType().getTextureID()), blockCoordToViewCoordX(e.getX()),blockCoordToViewCoordY(e.getY()),(h2/world.getBlocks().length),h2/world.getBlocks().length);

             batch.draw(null, blockCoordToViewCoordX(e.getX()),blockCoordToViewCoordY(e.getY()) + h2/world.getBlocks().length * 1.2f,(h2/world.getBlocks().length),h2/world.getBlocks().length * 0.2f,0.6f,0.6f,0.6f,1.0f);
             batch.draw(null, blockCoordToViewCoordX(e.getX()),blockCoordToViewCoordY(e.getY()) + h2/world.getBlocks().length * 1.2f,(h2/world.getBlocks().length * percentage),h2/world.getBlocks().length * 0.2f,0.0f,1.0f,0.0f,1.0f);
         } else if(o instanceof Projectile){
             Projectile p = (Projectile) o;

             float angle = Utility.calculateAngleBetweenTwoPoints(p.getX(), p.getY(), p.getX() - p.getDir().getCoords()[0], p.getY() - p.getDir().getCoords()[1]);

             batch.draw(ViewManager.getTexture(p.getProjectileType().getTextureID()), blockCoordToViewCoordX(p.getX()), blockCoordToViewCoordY(p.getY()),h2/world.getBlocks().length,h2/world.getBlocks().length,h2/world.getBlocks().length/2,h2/world.getBlocks().length/2,angle,1,1,1,1);
        }
    }


    @Override
    public void render(float deltaTime, Batch batch) {
        //Hintergrund für die TowerButtons am Rechten Rand
        batch.draw(towerButtonBackgroundTexture,originWidth*7/8,(originHeight-h2)/2,originWidth*1/8,h2);

        super.render(deltaTime, batch);

        if(originHeight < originWidth * 7/8) {
            h2 = originHeight;
            w2 = h2;
        }else{
            w2 = originWidth*7/8;
            h2 = w2;
        }

        /**
         * Zeichnet die Welt (Die einzelnen Blöcke)
         */
        for(int i = 0; i < world.getBlocks().length; i++){
            for(int j = 0; j < world.getBlocks()[i].length; j++){
                batch.draw(blockTexture,blockCoordToViewCoordX(i), blockCoordToViewCoordY(j), w2/world.getBlocks().length, h2/world.getBlocks()[i].length);
            }
        }

        /**
         * Zeichnet die Enemies und Projectiles
         */
            for (int i = 0; i < world.getObjects().size(); i++) {
                drawGameObject(world.getObjects().get(i), batch);
            }
            for(int i = 0; i < world.getBlocks().length; i++){
                for(int j = 0 ; j< world.getBlocks()[i].length ; j++ ) {
                    Tower tower = world.getBlocks()[i][j].getContent();
                    if (tower != null) {
                        float oX = blockCoordToViewCoordX(i);
                        float oY = blockCoordToViewCoordY(j);
                        float oW = w2 / world.getBlocks().length;
                        float oH = h2 / world.getBlocks()[i].length;

                        float angle = (float)Math.PI;
                        if (tower.getTarget() != null && tower.getType().canShoot()) {
                            angle = Utility.calculateAngleBetweenTwoPoints(oX, oY, blockCoordToViewCoordX(tower.getTarget().getX()), blockCoordToViewCoordY(tower.getTarget().getY()));
                            //System.out.println(Utility.calculateAngleBetweenTwoPoints(oX, oY, blockCoordToViewCoordX(tower.getTarget().getX()), blockCoordToViewCoordY(tower.getTarget().getY())));
                        }
                        batch.draw(ViewManager.getTexture(tower.getType().getTextureID()), oX, oY, oW, oH, oW / 2, oH / 2, (float) (angle + Math.PI), 1f, 1f, 1f, 1f);
                    }
                }
            }

        String message = "Coins: " + world.getCoins();
        ViewManager.font.drawText(batch, message, (int) (originWidth - ViewManager.font.getWidth(message)), (int) (originHeight - ViewManager.font.getLineHeight()));

        Vector2 block = getBlockIDOfMouse(Mouse.getX(), Mouse.getY());
        if (block != null) {
            batch.draw(null,blockCoordToViewCoordX((int)block.getCoords()[0]), blockCoordToViewCoordY((int)(world.getBlocks()[0].length - block.getCoords()[1])), w2/world.getBlocks().length, h2/world.getBlocks()[0].length, 0, 0, 0, 1f, 1f, 1f, 0.45f);

            Tower t = world.getBlocks()[(int)block.getCoords()[0]][(int)(world.getBlocks()[0].length - block.getCoords()[1])].getContent();

            if (t != null) {
                int x0 = (int) blockCoordToViewCoordX( block.getCoords()[0]);
                int y0 = (int) blockCoordToViewCoordY((world.getBlocks()[0].length - block.getCoords()[1]));

                String l1 = t.getName();
                String l2 = "Health: " + t.getHp();

                int w = Math.max(ViewManager.font.getWidth(l1), ViewManager.font.getWidth(l2));
                int h = ViewManager.font.getLineHeight() * 2;

                batch.draw(ViewManager.getTexture("viewTextures/mainButton.png"), x0, y0, w, h);
                ViewManager.font.drawText(batch, l1, x0, y0);
                ViewManager.font.drawText(batch, l2, x0, y0 + (h / 2));
            }
        }

        for (int i = 0; i < towerButtons.length; i++) {
            TowerButton b = towerButtons[i];

            if (Utility.viewComponentIsCollidingWithMouse(b, Mouse.getX(), (int) (originHeight - Mouse.getY()))) {
                TowerType t = b.getTowerType();

                String l1 = t.getName();
                String l2 = "Cost: " + t.getCost();

                int w = Math.max(ViewManager.font.getWidth(l1), ViewManager.font.getWidth(l2));
                int h = ViewManager.font.getLineHeight() * 2;

                int x0 = Mouse.getX() - (w / 2);
                int y0 = (int) (originHeight - Mouse.getY() - (h / 2));

                batch.draw(ViewManager.getTexture("viewTextures/mainButton.png"), x0, y0, w, h);
                ViewManager.font.drawText(batch, l1, x0, y0);
                ViewManager.font.drawText(batch, l2, x0, y0 + (h / 2));
            }
        }

        /**
         * Alles Was mit dem Towersetzen zu tun hat
         */
        if(setTower != null) {
            setTower.setX(Mouse.getX() - setTower.getRadius() / 2);
            setTower.setY(originHeight - Mouse.getY() - setTower.getRadius() / 2);
            setTower.setRadius(h2/world.getBlocks().length);
            batch.draw(ViewManager.getTexture(setTower.getType().getTextureID()), setTower.getX(),setTower.getY(), setTower.getRadius(), setTower.getRadius());
        }
    }



    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
        for (int i = 0; i <towerButtons.length -1 ; i++){
            towerButtons[i].setX(width * 7/8);
            towerButtons[i].setY( i * height/towerButtons.length);
            towerButtons[i].setWidth(width* 1/8);
            towerButtons[i].setHeight(height/towerButtons.length);
        }
    }

    /**
     * Rechnet aus den MouseKoordinaten die ID des Blockes aus
     */
    public Vector2 getBlockIDOfMouse(float mouseX,float mouseY){
        float x = ((originWidth * 7 / 8) / 2 - w2 / 2);
        float y = 0;
        float w = x + w2;
        float h = y + h2;

        if(mouseX > x && mouseY > y && mouseX < w && mouseY < h) {
            return  new Vector2((mouseX - x) * world.getBlocks().length / w2,(mouseY - (originHeight  - h2) / 2 ) * world.getBlocks().length / h2);
        }
        return null;
    }

    /**
     * Rechnet die Block Koordinate in eine View Koordinate um
     */
    private float blockCoordToViewCoordX(float coord){
        return w2/world.getBlocks().length * coord+ ((originWidth*7f/8f)-w2) / 2f;
    }

    private float blockCoordToViewCoordY(float coord){
        return h2/world.getBlocks()[0].length * coord+ (originHeight-h2) / 2f;
    }

    private float viewCoordToBlockCoordX(float vx){
        return w2 / world.getBlocks().length / (vx - (originWidth * 7/8 - w2) / 2);
    }

    private float viewCoordToBlockCoordY(float vy){
        return h2 / world.getBlocks()[0].length / (vy - (originHeight - h2) / 2);
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
            Vector2 mouse = getBlockIDOfMouse(mouseX, mouseY);
            if (mouse != null) {
                if (button == 0) {
                    world.setCoins(world.getCoins() - setTower.getCost());
                    if (world.setTowerInBlocks((int) mouse.getCoords()[0], (int) mouse.getCoords()[1], (setTower))) {
                        viewManager.getPostProcessingManager().disableEffect(PostProcessingManager.Effect.RadialBlur);
                        setTower = null;
                    }
                }
            }
        }
    }

    @Override
    public void onKeyDown(int key, char c) {
        super.onKeyDown(key, c);
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
