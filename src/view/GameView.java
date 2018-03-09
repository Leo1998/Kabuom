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
import view.components.*;
import view.math.Vector2;
import view.components.Button;
import view.components.TowerButton;
import view.components.ViewComponent;
import view.effects.DrunkEffect;
import view.effects.RadialBlurEffect;
import view.rendering.Batch;
import view.texture.ITexture;
import world.World;

import java.util.List;

public class GameView extends View {

    private final float tBWidth = 0.125f;

    private World world;
    private Entity setTower;
    private float scale;
    private int offsetX, offsetY, circleLimit = 64;
    private ITexture blockTexture;
    private boolean shiftdown, mousedown0, mousedown1, showRange, showLevel;
    private Vector2 mouse0, mouse1;

    private RadialBlurEffect radialBlurEffect;
    private DrunkEffect drunkEffect;

    public GameView(int width, int height, final ViewManager viewManager, final World world) {
        super(width, height, viewManager);

        this.blockTexture = ViewManager.getTexture("block1.png");

        this.radialBlurEffect = new RadialBlurEffect();
        this.addEffect(radialBlurEffect);
        this.radialBlurEffect.setEnabled(false);

        this.drunkEffect = new DrunkEffect();
        this.addEffect(drunkEffect);
        this.drunkEffect.setEnabled(false);

        this.world = world;

        Button[] towerButtons = new TowerButton[EntityType.mainTowerIndex];
        float buttonWidth = tBWidth;
        float buttonHeight = 0.8f / towerButtons.length;
        float buttonStartX = 1 - buttonWidth;
        float buttonStartY = 0;
        for (int i = 0; i < towerButtons.length; i++) {
            final TowerButton towerButton = new TowerButton(buttonStartX, buttonStartY + buttonHeight * i, buttonWidth, buttonHeight, this, null, EntityType.values()[i]);

            towerButtons[i] = towerButton;
            this.components.add(towerButton);

            towerButton.setListener(() -> {
                if (setTower == null) {
                    int cost = towerButton.getEntityType().cost;
                    if (world.getCoins() >= cost) {
                        radialBlurEffect.setEnabled(true);

                        setTower = new Entity(towerButton.getEntityType(), 0, 0, 0, -1, null, false);
                    }
                }
            });
        }

        IndexedButton startButton = new IndexedButton(buttonStartX, 0.9f, buttonWidth, 0.1f, this, "Next Wave:", world.getStrength());
        startButton.setListener(()->{
            world.startWave();
            startButton.setIndex(world.getStrength());
        });
        components.add(startButton);
    }

    private void calcultateOffset(float width, float height, int sizeX, int sizeY) {
        float scaleX = width * (1 - tBWidth) / sizeX;
        float scaleY = height / sizeY;

        if (scaleX < scaleY) {
            offsetX = 0;
            offsetY = Math.round((height - sizeY * scaleX) / 2);
            scale = scaleX;
        } else {
            offsetX = Math.round((width * (1 - tBWidth) - sizeX * scaleY) / 2);
            offsetY = 0;
            scale = scaleY;
        }
    }

    private void drawEntity(Entity entity, Batch batch) {

        if (entity.getBaseTexture() != null) {
            float angle = 0;
            if (entity instanceof MoveEntity) {
                MoveEntity mEntity = (MoveEntity) entity;
                if (!mEntity.getMovement().nullVector()) {
                    angle = mEntity.getMovement().getAngle();
                }
                float diameter = entity.getRadius() * 2;
                float width = scale * diameter;
                float height = scale * diameter;
                float percentage = Math.max(0, entity.getHp() / entity.getMaxHp());

                batch.draw(null, blockToViewX(entity.getX(), diameter), blockToViewY(entity.getY(), diameter) + height * 1.1f, width, height * 0.1f, 0.6f, 0.6f, 0.6f, 1.0f);
                batch.draw(null, blockToViewX(entity.getX(), diameter), blockToViewY(entity.getY(), diameter) + height * 1.1f, width * percentage, height * 0.1f, 0.0f, 1.0f, 0.0f, 1.0f);
            }

            drawGameObject(entity, entity.getBaseTexture(), angle, batch, 1);
        }

        if (entity.getTurretTexture() != null) {
            float angle = 0;
            if (entity.getTarget() != null) {
                angle = Utility.getAngle(entity.getTarget(), entity);
            }

            drawGameObject(entity, entity.getTurretTexture(), angle, batch, 0.9f);
        }

        if (showRange && entity.isRanged() && !entity.isSpawner()) {
            float x = blockToViewX(entity.getX() + 0.5f);
            float y = blockToViewY(entity.getY() + 0.5f);

            float r, g, b;
            r = g = b = 0;
            if (entity.isEnemy()) {
                r = 1;
            } else {
                b = 1;
            }

            float radius = entity.getRange() * scale;
            batch.limitedCircle(x, y, radius, r, g, b, 0.0625f, offsetX, offsetY, offsetX + Math.round(scale * world.getWidth()), offsetY + Math.round(scale * world.getHeight()));
        }

        if (showLevel) {
            float x = blockToViewX(entity.getX() + 0.5f);
            float y = blockToViewY(entity.getY() + 0.5f);

            String s = Integer.toString(entity.getLevel() + 1);
            float oX = ViewManager.font.getWidth(s) / 2;
            float oY = ViewManager.font.getLineHeight() / 2;
            ViewManager.font.drawText(batch, s, Math.round(x - oX), Math.round(y - oY));
        }
    }

    private void drawProjectile(Projectile projectile, Batch batch) {
        float angle = projectile.getDir().getAngle();

        drawGameObject(projectile, projectile.getTexture(), angle, batch, 1);
    }

    private void drawGameObject(GameObject gameObject, String textureId, float rotation, Batch batch, float size) {
        float diameter = gameObject.getRadius() * 2 * size;
        float width = scale * diameter;
        float height = scale * diameter;

        batch.draw(ViewManager.getTexture(textureId), blockToViewX(gameObject.getX(), diameter), blockToViewY(gameObject.getY(), diameter), width, height, rotation, 1, 1, 1, 1);
    }

    @Override
    public void renderScene(float deltaTime, Batch batch) {
        calcultateOffset(originWidth, originHeight, world.getWidth(), world.getHeight());


        //Zeichnet die Welt (Die einzelnen Blöcke)
        for (int i = 0; i < world.getBlocks().length; i++) {
            for (int j = 0; j < world.getBlocks()[i].length; j++) {
                batch.draw(blockTexture, blockToViewX(i), blockToViewY(j), scale, scale);
            }
        }

        if (showRange && world.getRanged() > circleLimit) {
            showRange = false;
        }

        for (Entity entity : world.getEntityList()) {
            drawEntity(entity, batch);
        }

        for (Projectile projectile : world.getProjectileList()) {
            drawProjectile(projectile, batch);
        }


        //Zeichnet die Coin & Wave Zähler an den unteren rand
        String coinsMessage = "Coins: " + Utility.niceNumber(world.getCoins());
        ViewManager.font.drawText(batch, coinsMessage, (int) (originWidth - ViewManager.font.getWidth(coinsMessage)), (int) (originHeight - ViewManager.font.getLineHeight() * 2 - (originHeight / 10)));

        String waveMessage = "Wave: " + world.getWave();
        ViewManager.font.drawText(batch, waveMessage, (int) (originWidth - ViewManager.font.getWidth(waveMessage)), (int) (originHeight - ViewManager.font.getLineHeight() - (originHeight / 10)));


        //Alles was mit der Maus zu tun hat
        Vector2 block = getBlockIDOfMouse();
        if (block != null) {

            //Zeichnet die Info über den überfahrenden Tower an den Cursor
            batch.draw(null, blockToViewX((int) block.getX()), blockToViewY((int) block.getY()), scale, scale, 0, 1f, 1f, 1f, 0.45f);

            if (setTower == null) {
                Entity t = world.getBlocks()[(int) block.getX()][(int) block.getY()].getTower();

                if (t != null) {

                    if (t.isRanged() && !t.isSpawner() && !showRange) {
                        float radius = t.getRange() * scale;
                        float x1 = blockToViewX(t.getX() + 0.5f);
                        float y1 = blockToViewY(t.getY() + 0.5f);
                        batch.limitedCircle(x1, y1, radius, 1, 1, 1, 0.0625f, offsetX, offsetY, offsetX + Math.round(scale * world.getWidth()), offsetY + Math.round(scale * world.getHeight()));
                    }

                    int x0 = Mouse.getX();
                    int y0 = Math.round(originHeight) - Mouse.getY();

                    String l1 = t.getName();
                    String l2 = "Health: " + Utility.niceNumber(Math.round(t.getHp()));
                    String l3 = "Upgrade: " + Utility.niceNumber(t.getCost()) + " Coins";

                    int w = Math.max(Math.max(ViewManager.font.getWidth(l1), ViewManager.font.getWidth(l2)), ViewManager.font.getWidth(l3));
                    int h = ViewManager.font.getLineHeight() * 3;

                    x0 = Math.max(0, Math.min(Math.round(originWidth) - w, x0));
                    y0 = Math.max(0, Math.min(Math.round(originHeight) - h, y0));

                    batch.draw(ViewManager.getTexture("viewTextures/mainButton.png"), x0, y0, w, h);
                    ViewManager.font.drawText(batch, l1, x0, y0);
                    ViewManager.font.drawText(batch, l2, x0, y0 + (h / 3));
                    ViewManager.font.drawText(batch, l3, x0, y0 + (h * 2 / 3));
                }
            }

            if (mousedown0) {
                if (mouse0 == null || (int) mouse0.getX() != (int) block.getX() || (int) mouse0.getY() != (int) block.getY()) {
                    leftClick((int) block.getX(), (int) block.getY());
                }

                mouse0 = block;
            }
            if (mousedown1) {
                if (mouse1 == null || (int) mouse1.getX() != (int) block.getX() || (int) mouse1.getY() != (int) block.getY()) {
                    rightClick((int) block.getX(), (int) block.getY());
                }

                mouse1 = block;
            }
        } else if (mousedown1 && setTower != null) {
            radialBlurEffect.setEnabled(false);
            setTower = null;
        }

        //Alles Was mit dem Towersetzen zu tun hat
        if (setTower != null) {
            float diameter = setTower.getRadius() * 2;
            float width = scale * diameter;
            float height = scale * diameter;

            if (block != null) {
                setTower.setX(blockToViewX((int) block.getX(), diameter));
                setTower.setY(blockToViewY((int) block.getY(), diameter));
            } else {
                setTower.setX(Mouse.getX() - width / 2);
                setTower.setY(originHeight - Mouse.getY() - height / 2);
            }

            if (setTower.getBaseTexture() != null) {
                batch.draw(ViewManager.getTexture(setTower.getBaseTexture()), setTower.getX(), setTower.getY(), width, height);
            }
            if (setTower.getTurretTexture() != null) {
                batch.draw(ViewManager.getTexture(setTower.getTurretTexture()), setTower.getX(), setTower.getY(), width, height);
            }
            if (setTower.isRanged() && !setTower.isSpawner()) {
                float x = setTower.getX() + width / 2;
                float y = setTower.getY() + height / 2;
                float r = setTower.getRange() * scale;
                batch.limitedCircle(x, y, r, 1, 1, 1, 0.1f, offsetX, offsetY, offsetX + Math.round(scale * world.getWidth()), offsetY + Math.round(scale * world.getHeight()));
            }
        }
    }


    @Override
    public void layout(int width, int height) {
        super.layout(width, height);
    }

    /**
     * Rechnet aus den MouseKoordinaten die ID des Blockes aus
     */
    public Vector2 getBlockIDOfMouse() {
        float x = viewToBlockX(Mouse.getX());
        float y = viewToBlockY(originHeight - Mouse.getY());

        if (x >= 0 && y >= 0 && x < world.getWidth() && y < world.getHeight()) {
            return new Vector2(x, y);
        } else {
            return null;
        }
    }

    private float viewToBlockX(float x) {
        return (x - offsetX) / scale;
    }

    private float viewToBlockY(float y) {
        return (y - offsetY) / scale;
    }

    /**
     * Rechnet die Block Koordinate in eine View Koordinate um
     */
    private float blockToViewX(float x, float width) {
        return blockToViewX(x + 0.5f - width / 2);
    }

    private float blockToViewY(float y, float height) {
        return blockToViewY(y + 0.5f - height / 2);
    }

    private float blockToViewX(float x) {
        return (x * scale) + offsetX;
    }

    private float blockToViewY(float y) {
        return (y * scale) + offsetY;
    }

    @Override
    public List<ViewComponent> getComponents() {
        return super.getComponents();
    }

    @Override
    public void onMouseDown(int button, int mouseX, int mouseY) {
        super.onMouseDown(button, mouseX, mouseY);

        if (button == 0) {
            mousedown0 = true;
        } else if (button == 1) {
            mousedown1 = true;
        }
    }

    @Override
    public void onMouseUp(int button, int mouseX, int mouseY) {
        super.onMouseUp(button, mouseX, mouseY);

        if (button == 0) {
            mousedown0 = false;
            mouse0 = null;
        } else if (button == 1) {
            mousedown1 = false;
            mouse1 = null;
        }
    }

    private void leftClick(int blockX, int blockY) {
        if (setTower != null) {
            setTower.setX(blockX);
            setTower.setY(blockY);
            if (world.setTower(setTower)) {

                if (shiftdown && world.getCoins() - setTower.getCost() >= 0) {
                    setTower = setTower.clone();
                } else {
                    setTower = null;
                    radialBlurEffect.setEnabled(false);
                }
            }
        } else {
            world.upgradeTower(blockX, blockY);
        }
    }

    private void rightClick(int blockX, int blockY) {
        if (setTower == null) {
            world.sellTower(blockX, blockY);
        } else {
            radialBlurEffect.setEnabled(false);
            setTower = null;
        }
    }

    @Override
    public void onKeyDown(int key, char c) {
        super.onKeyDown(key, c);

        if (key == Keyboard.KEY_1) {
            this.radialBlurEffect.toggleEnabled();
        }
        if (key == Keyboard.KEY_2) {
            this.drunkEffect.toggleEnabled();
        }

        if (key == Keyboard.KEY_LSHIFT) {
            shiftdown = true;
        }
        if (key == Keyboard.KEY_ESCAPE) {
            getViewManager().getCtrl().endGame(false);
        }
        if (key == Keyboard.KEY_TAB) {
            if (showLevel) {
                showRange = !showRange;
            }
            showLevel = !showLevel;
        }
        if (key == Keyboard.KEY_P) {
            Vector2 block = getBlockIDOfMouse();
            if (block != null) {
                world.printEntities((int) block.getX(), (int) block.getY());
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
