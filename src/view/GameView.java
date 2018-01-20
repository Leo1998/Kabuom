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

    private Utility u;

    private World world;
    private TowerButton[] towerButtons;
    private Tower setTower;
    private float w2, h2, scale;
    private ITexture blockTexture, towerButtonBackgroundTexture;
    private Button startButton;
    private boolean shiftdown;


    public GameView(float width, float height, final ViewManager viewManager, final World world) {
        super(width, height, viewManager);

        blockTexture = ViewManager.getTexture("block1.png");
        towerButtonBackgroundTexture = ViewManager.getTexture("viewTextures/mainButton.png");

        float towerButtonX = width * 7 / 8;
        float towerButtonHeight = (height - ViewManager.font.getLineHeight() - (originHeight / 10)) / TowerType.values().length;
        float towerButtonWidth = width * 1 / 8;


        this.world = world;
        towerButtons = new TowerButton[TowerType.values().length];
        for (int i = 0; i < towerButtons.length; i++) {
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
                        int cost = towerButton.getTowerType().cost;
                        if (world.getCoins() >= cost) {
                            viewManager.getPostProcessingManager().enableEffect(PostProcessingManager.Effect.RadialBlur);

                            setTower = new Tower(towerButton.getTowerType(), 0, 0, 0);
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

        u = new Utility();
    }

    private void drawEnemy(Enemy enemy, Batch batch){
        float angle = 0;
        if(enemy.getMovement().getCoords()[0] != 0 || enemy.getMovement().getCoords()[1] != 0) {
            angle = Utility.calculateAngleBetweenTwoPoints(0, 0, enemy.getMovement().getCoords()[0], enemy.getMovement().getCoords()[1]);
        }


        drawGameObject(enemy,angle,batch);

        float diameter = enemy.enemyType.radius*2;
        float width = scale * diameter;
        float height = scale * diameter;
        float percentage = Math.max(0,enemy.getHp() / enemy.enemyType.getMaxHP());

        batch.draw(null, blockCoordToViewCoordX(enemy.getX(),diameter), blockCoordToViewCoordY(enemy.getY(),diameter) + height * 1.1f, width, height * 0.1f, 0.6f, 0.6f, 0.6f, 1.0f);
        batch.draw(null, blockCoordToViewCoordX(enemy.getX(),diameter), blockCoordToViewCoordY(enemy.getY(),diameter) + height * 1.1f, width * percentage, height * 0.1f, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    private void drawProjectile(Projectile projectile, Batch batch){
        float angle = Utility.calculateAngleBetweenTwoPoints(projectile.getDir().getCoords()[0], projectile.getDir().getCoords()[1], 0, 0);

        drawGameObject(projectile,angle,batch);
    }

    private void drawTower(Tower tower, Batch batch){

        float diameter = tower.towerType.radius*2;

        float xCoord = blockCoordToViewCoordX(tower.getX(),diameter);
        float yCoord = blockCoordToViewCoordY(tower.getY(),diameter);

        float angle = 0;
        if (tower.getTarget() != null && tower.towerType.canShoot) {
            angle = Utility.calculateAngleBetweenTwoPoints(blockCoordToViewCoordX(tower.getTarget().getX()), blockCoordToViewCoordY(tower.getTarget().getY()), xCoord, yCoord);
            //System.out.println(Utility.calculateAngleBetweenTwoPoints(xCoord, yCoord, blockCoordToViewCoordX(tower.getTarget().getX()), blockCoordToViewCoordY(tower.getTarget().getY())));
        }
        drawGameObject(tower,angle,batch);
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

        for(Tower tower: world.getTowerList()){
            drawTower(tower,batch);
        }

        for(Enemy enemy: world.getEnemyList()){
            drawEnemy(enemy,batch);
        }

        for(Projectile projectile: world.getProjectileList()){
            drawProjectile(projectile,batch);
        }

        super.render(deltaTime, batch);


        /**
         * Zeichnet die Coin & Wave Zähler an den unteren rand
         */
        String coins = Integer.toString(world.getCoins());
        for(int i = coins.length()-3; i > 0; i-=3){
            coins = coins.substring(0,i) + "," + coins.substring(i);
        }
        String coinsMessage = "Coins: " + coins;
        ViewManager.font.drawText(batch, coinsMessage, (int) (originWidth - ViewManager.font.getWidth(coinsMessage)), (int) (originHeight - ViewManager.font.getLineHeight() * 2 - (originHeight / 10)));

        String waveMessage = "Wave: " + world.getWave();
        ViewManager.font.drawText(batch, waveMessage, (int) (originWidth - ViewManager.font.getWidth(waveMessage)), (int) (originHeight - ViewManager.font.getLineHeight() - (originHeight / 10)));


        /**
         * Zeichnet die Info über den überfahrenden Tower an den Cursor
         */
        Vector2 block = getBlockIDOfMouse(Mouse.getX(), Mouse.getY());
        if (block != null) {
            batch.draw(null, blockCoordToViewCoordX((int) block.getCoords()[0]), blockCoordToViewCoordY((int) (world.getBlocks()[0].length - block.getCoords()[1])), w2 / world.getBlocks().length, h2 / world.getBlocks()[0].length, 0, 1f, 1f, 1f, 0.45f);

            Tower t = world.getBlocks()[(int) block.getCoords()[0]][(int) (world.getBlocks()[0].length - block.getCoords()[1])].getTower();

            //world.getBlocks()[(int) block.getCoords()[0]][(int) (world.getBlocks()[0].length - block.getCoords()[1])].test();

            if (t != null) {
                int x0 = (int) blockCoordToViewCoordX(block.getCoords()[0]);
                int y0 = (int) blockCoordToViewCoordY((world.getBlocks()[0].length - block.getCoords()[1]));

                String l1 = t.towerType.getName();
                String l2 = "Health: " + t.getHp();

                int w = Math.max(ViewManager.font.getWidth(l1), ViewManager.font.getWidth(l2));
                int h = ViewManager.font.getLineHeight() * 2;

                batch.draw(ViewManager.getTexture("viewTextures/mainButton.png"), x0, y0, w, h);
                ViewManager.font.drawText(batch, l1, x0, y0);
                ViewManager.font.drawText(batch, l2, x0, y0 + (h / 2));
            }
        }

        /**
         * Alles Was mit dem Towersetzen zu tun hat
         */
        if (setTower != null) {
            setTower.setX(Mouse.getX() - scale / 2);
            setTower.setY(originHeight - Mouse.getY() - scale / 2);
            float diameter = setTower.towerType.radius*2;
            float width = scale * diameter;
            float height = scale * diameter;
            batch.draw(ViewManager.getTexture(setTower.towerType.textureID), setTower.getX(), setTower.getY(), width, height);
        }
    }


    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
        for (int i = 0; i < towerButtons.length - 1; i++) {
            towerButtons[i].setX(width * 7 / 8);
            towerButtons[i].setY(i * ((height - ViewManager.font.getLineHeight() - (originHeight / 10)) / TowerType.values().length));
            towerButtons[i].setWidth(width * 1 / 8);
            towerButtons[i].setHeight((height - ViewManager.font.getLineHeight() - (originHeight / 10)) / TowerType.values().length);
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

    /*
    private float viewCoordToBlockCoordX(float vx) {
        return w2 / world.getBlocks().length / (vx - (originWidth * 7 / 8 - w2) / 2);
    }

    private float viewCoordToBlockCoordY(float vy) {
        return h2 / world.getBlocks()[0].length / (vy - (originHeight - h2) / 2);
    }
    */

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
                    if (world.spawnTower(setTower)) {
                        world.setCoins(world.getCoins() - setTower.towerType.cost);

                        if (shiftdown && world.getCoins() - setTower.towerType.cost >= 0) {
                            setTower = new Tower(setTower.towerType, 0, 0, 0);
                        } else {
                            setTower = null;
                            viewManager.getPostProcessingManager().disableEffect(PostProcessingManager.Effect.RadialBlur);
                        }
                    }
                }
            }
        } else {
            Vector2 block = getBlockIDOfMouse(Mouse.getX(), Mouse.getY());
            if (block != null) {
                Tower t = world.getBlocks()[(int) block.getCoords()[0]][(int) (world.getBlocks()[0].length - block.getCoords()[1])].getTower();
                if (t != null && t.towerType != TowerType.MAINTOWER) {
                    if (button == 1) {
                        world.setCoins(world.getCoins() + t.towerType.cost / 2);
                        world.removeTower(t);
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
        if (key == Keyboard.KEY_LSHIFT) {
            shiftdown = true;
        }

        if (key == Keyboard.KEY_ESCAPE && world.getEnemyList().isEmpty()) {
            getViewManager().getCtrl().endGame(false);
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
