package view;

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

public class GameView extends View {

    private World world;
    private TowerButton[] towerButtons;
    private Entity setTower;
    private float w2, h2, scale;
    private ITexture blockTexture, towerButtonBackgroundTexture;
    private Button startButton;
    private boolean shiftdown;


    public GameView(float width, float height, final ViewManager viewManager, final World world) {
        super(width, height, viewManager);

        blockTexture = ViewManager.getTexture("block1.png");
        towerButtonBackgroundTexture = ViewManager.getTexture("viewTextures/mainButton.png");

        float towerButtonX = width * 7 / 8;
        float towerButtonHeight = (height - ViewManager.font.getLineHeight() - (originHeight / 10)) / EntityType.firstEnemyIndex;
        float towerButtonWidth = width * 1 / 8;


        this.world = world;
        towerButtons = new TowerButton[EntityType.mainTowerIndex];
        for (int i = 0; i < towerButtons.length; i++) {

            final TowerButton towerButton = new TowerButton(towerButtonX, i * towerButtonHeight, towerButtonWidth, towerButtonHeight, this, null, EntityType.values()[i]);

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
        startButton = new Button(width * 7 / 8, height - height / 10, width * 1 / 8, height / 10, this, "Start");
        startButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                world.startWave();
            }
        });
        components.add(startButton);
    }

    private void drawEntity(Entity entity, Batch batch){
        float angle = 0;
        if(entity instanceof MoveEntity){
            MoveEntity mEntity = (MoveEntity) entity;
            if(!mEntity.getMovement().nullVector()) {
                angle = Utility.calculateAngleBetweenTwoPoints(0, 0, mEntity.getMovement().getCoords()[0], mEntity.getMovement().getCoords()[1]);
            }
            float diameter = entity.entityType.radius*2;
            float width = scale * diameter;
            float height = scale * diameter;
            float percentage = Math.max(0,entity.getHp() / entity.entityType.getMaxHP());

            batch.draw(null, blockCoordToViewCoordX(entity.getX(),diameter), blockCoordToViewCoordY(entity.getY(),diameter) + height * 1.1f, width, height * 0.1f, 0.6f, 0.6f, 0.6f, 1.0f);
            batch.draw(null, blockCoordToViewCoordX(entity.getX(),diameter), blockCoordToViewCoordY(entity.getY(),diameter) + height * 1.1f, width * percentage, height * 0.1f, 0.0f, 1.0f, 0.0f, 1.0f);
        } else {
            if (entity.getTarget() != null && entity.entityType.isRanged()) {
                angle = Utility.calculateAngleBetweenTwoPoints(entity.getTarget().getX(), entity.getTarget().getY(), entity.getX(), entity.getY());
            }
        }

        drawGameObject(entity,angle,batch);
    }

    private void drawProjectile(Projectile projectile, Batch batch){
        float angle = Utility.calculateAngleBetweenTwoPoints(projectile.getDir().getCoords()[0], projectile.getDir().getCoords()[1], 0, 0);

        drawGameObject(projectile,angle,batch);
    }

    private void drawGameObject(GameObject gameObject, float rotation, Batch batch){
        float diameter = gameObject.getObjectType().getRadius()*2;
        float width = scale * diameter;
        float height = scale * diameter;

        batch.draw(ViewManager.getTexture(gameObject.getObjectType().getTextureId()), blockCoordToViewCoordX(gameObject.getX(),diameter), blockCoordToViewCoordY(gameObject.getY(),diameter), width, height, rotation, 1, 1, 1, 1);
    }

    @Override
    public void render(float deltaTime, Batch batch) {
        if (originHeight < originWidth * 7 / 8) {
            h2 = originHeight;
            w2 = h2;
        } else {
            w2 = originWidth * 7 / 8;
            h2 = w2;
        }

        scale = h2/world.getBlocks().length;

        /**
         * Zeichnet die Welt (Die einzelnen Blöcke)
         */
        for (int i = 0; i < world.getBlocks().length; i++) {
            for (int j = 0; j < world.getBlocks()[i].length; j++) {
                batch.draw(blockTexture, blockCoordToViewCoordX(i), blockCoordToViewCoordY(j), w2 / world.getBlocks().length, h2 / world.getBlocks()[i].length);
            }
        }


        for(Entity entity: world.getEntityList()){
            drawEntity(entity,batch);
        }

        for(Projectile projectile: world.getProjectileList()){
            drawProjectile(projectile,batch);
        }

        super.render(deltaTime, batch);


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
        Vector2 block = getBlockIDOfMouse(Mouse.getX(), Mouse.getY());
        if (block != null) {
            batch.draw(null, blockCoordToViewCoordX((int) block.getCoords()[0]), blockCoordToViewCoordY((int) (world.getBlocks()[0].length - block.getCoords()[1])), w2 / world.getBlocks().length, h2 / world.getBlocks()[0].length, 0, 1f, 1f, 1f, 0.45f);

            Entity t = world.getBlocks()[(int) block.getCoords()[0]][(int) (world.getBlocks()[0].length - block.getCoords()[1])].getTower();

            //world.getBlocks()[(int) block.getCoords()[0]][(int) (world.getBlocks()[0].length - block.getCoords()[1])].test();

            if (t != null) {
                int x0 = (int) blockCoordToViewCoordX(block.getCoords()[0]);
                int y0 = (int) blockCoordToViewCoordY((world.getBlocks()[0].length - block.getCoords()[1]));

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
            batch.draw(ViewManager.getTexture(setTower.entityType.getTextureId()), setTower.getX(), setTower.getY(), width, height);
        }
    }


    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
        for (int i = 0; i < towerButtons.length; i++) {
            towerButtons[i].setX(width * 7 / 8);
            towerButtons[i].setY(i * ((height - ViewManager.font.getLineHeight() - (originHeight / 10)) / EntityType.firstEnemyIndex));
            towerButtons[i].setWidth(width * 1 / 8);
            towerButtons[i].setHeight((height - ViewManager.font.getLineHeight() - (originHeight / 10)) / EntityType.firstEnemyIndex);
        }

        startButton.setX(width * 7 / 8);
        startButton.setY(height - height / 10);
        startButton.setWidth(width * 1 / 8);
        startButton.setHeight(height / 10);
    }

    /**
     * Rechnet aus den MouseKoordinaten die ID des Blockes aus
     */
    public Vector2 getBlockIDOfMouse(float mouseX, float mouseY) {
        float x = ((originWidth * 7 / 8) / 2 - w2 / 2);
        float y = 0;
        float w = x + w2;
        float h = y + h2;

        if (mouseX > x && mouseY > y && mouseX < w && mouseY < h) {
            return new Vector2((mouseX - x) * world.getBlocks().length / w2, (mouseY - (originHeight - h2) / 2) * world.getBlocks().length / h2);
        }
        return null;
    }

    /**
     * Rechnet die Block Koordinate in eine View Koordinate um
     */
    private float blockCoordToViewCoordX(float coord, float width){
        return blockCoordToViewCoordX(coord + 0.5f - width/2);
    }

    private float blockCoordToViewCoordY(float coord, float height){
        return blockCoordToViewCoordY(coord + 0.5f - height/2);
    }

    private float blockCoordToViewCoordX(float coord) {
        return w2 / world.getBlocks().length * coord + ((originWidth * 7f / 8f) - w2) / 2f;
    }

    private float blockCoordToViewCoordY(float coord) {
        return h2 / world.getBlocks()[0].length * coord + (originHeight - h2) / 2f;
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
            Vector2 mouse = getBlockIDOfMouse(mouseX, mouseY);
            if (mouse != null) {
                if (button == 0) {
                    Vector2 blockId = getBlockIDOfMouse(mouseX,mouseY);
                    setTower.setX(blockId.getCoords()[0]);
                    setTower.setY(blockId.getCoords()[1]);
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
                Vector2 block = getBlockIDOfMouse(mouseX, mouseY);
                if(block != null) {
                    world.sellTower((int) block.getCoords()[0], (int) (world.getBlocks()[0].length - block.getCoords()[1]));
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
            Vector2 block = getBlockIDOfMouse(Mouse.getX(), Mouse.getY());
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
