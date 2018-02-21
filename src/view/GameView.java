package view;

import com.sun.org.apache.bcel.internal.generic.MONITORENTER;
import entity.model.Entity;
import entity.model.EntityType;
import entity.model.MoveEntity;
import model.GameObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import projectile.Projectile;
import utility.Utility;
import utility.Vector2;
import view.components.Button;
import view.components.ButtonListener;
import view.components.TowerButton;
import view.components.ViewComponent;
import view.rendering.Batch;
import view.rendering.ITexture;
import view.rendering.PostProcessingManager;
import world.World;

import java.util.List;
import java.util.Scanner;

public class GameView extends View {

    private final float tBWidth = 0.125f;

    private World world;
    private TowerButton[] towerButtons;
    private Entity setTower;
    private float scale;
    private int offsetX, offsetY;
    private ITexture blockTexture, towerButtonBackgroundTexture;
    private Button startButton;
    private boolean shiftdown;


    public GameView(float width, float height, final ViewManager viewManager, final World world) {
        super(width, height, viewManager);

        blockTexture = ViewManager.getTexture("block1.png");
        towerButtonBackgroundTexture = ViewManager.getTexture("viewTextures/mainButton.png");

        this.world = world;

        towerButtons = new TowerButton[EntityType.mainTowerIndex];
        float buttonWidth = tBWidth;
        float buttonHeight = 0.8f/towerButtons.length;
        float buttonStartX = 1 - buttonWidth;
        float buttonStartY = 0;
        for(int i = 0; i < towerButtons.length; i++){
            final TowerButton towerButton = new TowerButton(buttonStartX, buttonStartY + buttonHeight*i , buttonWidth, buttonHeight, this, null, EntityType.values()[i]);

            towerButtons[i] = towerButton;
            this.components.add(towerButton);

            towerButton.setListener(new ButtonListener() {
                @Override
                public void onClick() {
                    if (setTower == null) {
                        int cost = towerButton.getEntityType().cost;
                        if (world.getCoins() >= cost) {
                            viewManager.getPostProcessingManager().enableEffect(PostProcessingManager.Effect.RadialBlur);

                            setTower = new Entity(towerButton.getEntityType(), 0, 0, 0, -1, null, false);
                        }
                    }
                }
            });
        }

        startButton = new Button(buttonStartX, 0.9f, buttonWidth, 0.1f, this, "Start");
        startButton.setListener(() -> world.startWave());
        components.add(startButton);
    }

    private void calcultateOffset(float width, float height, int sizeX, int sizeY){
        float scaleX = width * (1 - tBWidth) / sizeX;
        float scaleY = height / sizeY;

        if (scaleX < scaleY){
            offsetX = 0;
            offsetY = Math.round((height - sizeY * scaleX) / 2);
            scale = scaleX;
        } else {
            offsetX = Math.round((width * (1 - tBWidth) - sizeX * scaleY) / 2);
            offsetY = 0;
            scale = scaleY;
        }
    }

    private void drawEntity(Entity entity, Batch batch){
        if(entity.entityType.baseTexture != null) {
            float angle = 0;
            if (entity instanceof MoveEntity) {
                MoveEntity mEntity = (MoveEntity) entity;
                if (!mEntity.getMovement().nullVector()) {
                    angle = Utility.calculateAngleBetweenTwoPoints(0, 0, mEntity.getMovement().getCoords()[0], mEntity.getMovement().getCoords()[1]);
                }
                float diameter = entity.entityType.radius * 2;
                float width = scale * diameter;
                float height = scale * diameter;
                float percentage = Math.max(0, entity.getHp() / entity.entityType.getMaxHP());

                batch.draw(null, blockToViewX(entity.getX(), diameter), blockToViewY(entity.getY(), diameter) + height * 1.1f, width, height * 0.1f, 0.6f, 0.6f, 0.6f, 1.0f);
                batch.draw(null, blockToViewX(entity.getX(), diameter), blockToViewY(entity.getY(), diameter) + height * 1.1f, width * percentage, height * 0.1f, 0.0f, 1.0f, 0.0f, 1.0f);
            }

            drawGameObject(entity, entity.entityType.baseTexture, angle, batch, 1);
        }

        if (entity.entityType.turretTexture != null) {
            float angle = 0;
            if (entity.getTarget() != null) {
                angle = Utility.calculateAngleBetweenTwoPoints(entity.getTarget().getX(), entity.getTarget().getY(), entity.getX(), entity.getY());
            }

            drawGameObject(entity, entity.entityType.turretTexture, angle, batch, 0.9f);
        }
    }

    private void drawProjectile(Projectile projectile, Batch batch){
        float angle = Utility.calculateAngleBetweenTwoPoints(projectile.getDir().getCoords()[0], projectile.getDir().getCoords()[1], 0, 0);

        drawGameObject(projectile, projectile.projectileType.textureID, angle, batch, 1);
    }

    private void drawGameObject(GameObject gameObject, String textureId, float rotation, Batch batch, float size){
        float diameter = gameObject.getObjectType().getRadius()*2*size;
        float width = scale * diameter;
        float height = scale * diameter;

        batch.draw(ViewManager.getTexture(textureId), blockToViewX(gameObject.getX(),diameter), blockToViewY(gameObject.getY(),diameter), width, height, rotation, 1, 1, 1, 1);
    }

    @Override
    public void render(float deltaTime, Batch batch) {
        super.render(deltaTime,batch);

        calcultateOffset(originWidth, originHeight, world.getWidth(), world.getHeight());

        /**
         * Zeichnet die Welt (Die einzelnen Blöcke)
         */
        for (int i = 0; i < world.getBlocks().length; i++) {
            for (int j = 0; j < world.getBlocks()[i].length; j++) {
                batch.draw(blockTexture, blockToViewX(i), blockToViewY(j), scale, scale);
            }
        }

        for(Entity entity: world.getEntityList()){
            drawEntity(entity,batch);
        }

        for(Projectile projectile: world.getProjectileList()){
            drawProjectile(projectile,batch);
        }


        /**
         * Zeichnet die Coin & Wave Zähler an den unteren rand
         */
        String coinsMessage = "Coins: " + niceNumber(world.getCoins());
        ViewManager.font.drawText(batch, coinsMessage, (int) (originWidth - ViewManager.font.getWidth(coinsMessage)), (int) (originHeight - ViewManager.font.getLineHeight() * 2 - (originHeight / 10)));

        String waveMessage = "Wave: " + world.getWave();
        ViewManager.font.drawText(batch, waveMessage, (int) (originWidth - ViewManager.font.getWidth(waveMessage)), (int) (originHeight - ViewManager.font.getLineHeight() - (originHeight / 10)));


        /**
         * Zeichnet die Info über den überfahrenden Tower an den Cursor
         */
        Vector2 block = getBlockIDOfMouse();
        if (block != null) {
            batch.draw(null, blockToViewX((int)block.getCoords()[0]), blockToViewY((int)block.getCoords()[1]), scale, scale, 0, 1f, 1f, 1f, 0.45f);

            Entity t = world.getBlocks()[(int) block.getCoords()[0]][(int) block.getCoords()[1]].getTower();

            //world.getBlocks()[(int) block.getCoords()[0]][(int) (world.getBlocks()[0].length - block.getCoords()[1])].test();

            if (t != null) {
                int x0 = Mouse.getX();
                int y0 = Math.round(originHeight) - Mouse.getY();

                String l1 = t.entityType.getName();
                String l2 = "Health: " + niceNumber(Math.round(t.getHp()));
                String l3 = "X:"+Math.round(t.getX())+" Y:"+Math.round(t.getY());

                int w = Math.max(Math.max(ViewManager.font.getWidth(l1), ViewManager.font.getWidth(l2)), ViewManager.font.getWidth(l3));
                int h = ViewManager.font.getLineHeight() * 3;

                batch.draw(ViewManager.getTexture("viewTextures/mainButton.png"), x0, y0, w, h);
                ViewManager.font.drawText(batch, l1, x0, y0);
                ViewManager.font.drawText(batch, l2, x0, y0 + (h / 3));
                ViewManager.font.drawText(batch, l3, x0, y0 + (h * 2 / 3));
            }
        }

        /**
         * Alles Was mit dem Towersetzen zu tun hat
         */
        if (setTower != null) {
            setTower.setX(Mouse.getX() - scale / 2);
            setTower.setY(originHeight - Mouse.getY() - scale / 2);
            float diameter = setTower.entityType.getRadius()*2;
            float width = scale * diameter;
            float height = scale * diameter;
            if(setTower.entityType.baseTexture != null){
                batch.draw(ViewManager.getTexture(setTower.entityType.baseTexture), setTower.getX(), setTower.getY(), width, height);
            }
            if(setTower.entityType.turretTexture != null){
                batch.draw(ViewManager.getTexture(setTower.entityType.turretTexture), setTower.getX(), setTower.getY(), width, height);
            }
        }
    }


    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }

    /**
     * Rechnet aus den MouseKoordinaten die ID des Blockes aus
     */
    public Vector2 getBlockIDOfMouse() {
        float x = viewToBlockX(Mouse.getX());
        float y = viewToBlockY(originHeight - Mouse.getY());

        if(x >= 0 && y >= 0 && x < world.getWidth() && y < world.getHeight()){
            return new Vector2(x,y);
        } else {
            return null;
        }
    }

    private float viewToBlockX(float x){
        return (x - offsetX) / scale;
    }

    private float viewToBlockY(float y){
        return (y - offsetY) / scale;
    }

    /**
     * Rechnet die Block Koordinate in eine View Koordinate um
     */
    private float blockToViewX(float x, float width){
        return blockToViewX(x + 0.5f - width/2);
    }

    private float blockToViewY(float y, float height){
        return blockToViewY(y + 0.5f - height/2);
    }

    private float blockToViewX(float x) {
        return (x*scale) + offsetX;
    }

    private float blockToViewY(float y) {
        return (y*scale) + offsetY;
    }

    private String niceNumber(int number){
        String result = Integer.toString(number);
        for(int i = result.length()-3; i > 0; i-=3){
            result = result.substring(0,i) + "," + result.substring(i);
        }
        return result;
    }

    @Override
    public List<ViewComponent> getComponents() {
        return super.getComponents();
    }

    @Override
    public void onMouseDown(int button, int mouseX, int mouseY) {
        super.onMouseDown(button, mouseX, mouseY);


        if (setTower != null) {
            if (button == 1) {
                viewManager.getPostProcessingManager().disableEffect(PostProcessingManager.Effect.RadialBlur);
                setTower = null;
            }
            Vector2 mouse = getBlockIDOfMouse();
            if (mouse != null) {
                if (button == 0) {
                    setTower.setX((int)mouse.getCoords()[0]);
                    setTower.setY((int)mouse.getCoords()[1]);
                    if (world.setTower(setTower)) {
                        world.setCoins(world.getCoins() - setTower.entityType.cost);

                        if (shiftdown && world.getCoins() - setTower.entityType.cost >= 0) {
                            setTower = new Entity(setTower.entityType, 0, 0, 0, -1, null, false);
                        } else {
                            setTower = null;
                            viewManager.getPostProcessingManager().disableEffect(PostProcessingManager.Effect.RadialBlur);
                        }
                    }
                }
            }
        } else {
            if(button == 1){
                Vector2 block = getBlockIDOfMouse();
                if(block != null) {
                    world.sellTower((int) block.getCoords()[0], (int) block.getCoords()[1]);
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
        if (key == Keyboard.KEY_LSHIFT) {
            shiftdown = true;
        }

        if (key == Keyboard.KEY_ESCAPE && !world.isInWave()) {
            getViewManager().getCtrl().endGame(false);
        }
        if(key == Keyboard.KEY_TAB) {
            Vector2 block = getBlockIDOfMouse();
            if (block != null) {
                world.printEntities((int)block.getCoords()[0],(int) (world.getBlocks()[0].length - block.getCoords()[1]));
            }
        }
    }

    @Override
    public void onKeyUp(int key, char c) {
        super.onKeyUp(key, c);
        if (key == Keyboard.KEY_LSHIFT) {
            shiftdown = false;
        }
    }
}
