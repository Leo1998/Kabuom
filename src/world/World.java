package world;

import controller.Controller;
import entity.EntityHandler;
import entity.model.Entity;
import entity.model.EntityType;
import entity.model.MoveEntity;
import model.GameObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import projectile.Projectile;
import projectile.ProjectileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import static utility.Utility.random;

public class World {

    /*
    public static World createWorld(File worldFile, int difficulty) {
        if (worldFile.exists()) {
            try {
                JSONObject obj = new JSONObject(new JSONTokener(new FileInputStream(worldFile)));

                int width = obj.getInt("width");
                int height = obj.getInt("height");
                int wave = obj.getInt("wave");
                int coins = obj.getInt("coins");

                World world = new World(width, height, difficulty, worldFile);
                world.wave = wave;
                world.coins = coins;

                JSONArray blocksX = obj.getJSONArray("blocks");
                for (int x = 0; x < width; x++) {
                    JSONArray blocksY = blocksX.getJSONArray(x);

                    for (int y = 0; y < height; y++) {
                        JSONObject o = blocksY.getJSONObject(y);

                        if (o.has("towerType")) {
                            float hp = (float) o.getDouble("hp");
                            int level = o.getInt("level");

                            TowerType type = TowerType.valueOf(o.getString("towerType"));
                            Tower tower = new Tower(type, level, x, y);
                            tower.setHp(hp);

                            world.spawnTower(tower);
                        }
                    }
                }

                return world;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                worldFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return new World(19, 19, difficulty, worldFile);
        }
    }

    public static void saveWorld(World world) {
        File worldFile = world.getWorldFile();

        try {
            JSONObject obj = new JSONObject();

            obj.put("width", world.getWidth());
            obj.put("height", world.getHeight());
            obj.put("wave", world.getWave());
            obj.put("coins", world.getCoins());

            JSONArray blocksX = new JSONArray();
            for (int x = 0; x < world.getWidth(); x++) {
                JSONArray blocksY = new JSONArray();

                for (int y = 0; y < world.getHeight(); y++) {
                    Tower t = world.getBlocks()[x][y].getTower();

                    if (t != null) {
                        JSONObject o = new JSONObject();
                        o.put("towerType", t.towerType.name());
                        o.put("hp", (double) t.getHp());
                        o.put("level", t.getLevel());

                        blocksY.put(o);
                    } else {
                        blocksY.put(new JSONObject());
                    }
                }

                blocksX.put(blocksY);
            }

            obj.put("blocks", blocksX);

            String json = obj.toString();

            BufferedWriter writer = new BufferedWriter(new FileWriter(worldFile));
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    private File worldFile;

    private LinkedList<Projectile> projectileList;
    private LinkedList<Entity> entityList;

    private Block[][] blocks;
    private int width, height;
    private float timePassed;

    private int wave = 0;
    private boolean spawnWave = false, inWave = false, ended = false;
    private float gameTime;

    private int coins = 1000;

    private EntityHandler entityHandler;
    private ProjectileHandler projectileHandler;

    /**
     * Attribute für den EnemyHandler
     */
    private boolean newTower;
    private boolean isDrunk;

    public World(int width, int height, int difficulty, File worldFile) {
        this.width = width;
        this.height = height;
        timePassed = 0;
        this.worldFile = worldFile;

        int mainTowerCoordX = width / 2;
        int mainTowerCoordY = height - 2;

        projectileList = new LinkedList<>();
        entityList = new LinkedList<>();
        blocks = new Block[width][height];

        for(int i = 0; i < blocks.length; i++){
            for(int j = 0; j < blocks.length; j++){
                blocks[i][j] = new Block();
            }
        }

        Entity mainTower = new Entity(EntityType.MAINTOWER, 1, mainTowerCoordX, mainTowerCoordY, -1, blocks[mainTowerCoordX][mainTowerCoordY]);
        this.setTower(mainTower);

        newTower = false;

        projectileHandler = new ProjectileHandler(this);
        entityHandler = new EntityHandler(this,mainTower);
    }

    public void spawnProjectile(Projectile projectile) {
        this.projectileList.add(projectile);
    }

    public void spawnEntity(Entity entity, int x, int y){
        entityList.add(entity);
        blocks[x][y].addEntity(entity);
    }

    public boolean setTower(Entity tower){
        int xPos = (int)(tower.getX());
        int yPos = (int)(tower.getY());
        if (xPos >= 0 && xPos < blocks.length && yPos >= 0 && yPos < blocks[xPos].length && blocks[xPos][yPos].getTower() == null) {
            tower.setX(xPos);
            tower.setY(yPos);
            newTower = true;
            blocks[xPos][yPos].setTower(tower);
            tower.setBlock(blocks[xPos][yPos]);
            entityList.add(tower);
            return true;
        }
        return false;
    }

    public void sellTower(int x, int y){
        Entity entity = blocks[x][y].getTower();
        if (entity != null && entity.entityType != EntityType.MAINTOWER) {
            coins += entity.entityType.cost / 2;
            removeEntity(entity);
        }
    }

    public void removeEntity(Entity entity){
        entity.setHp(0);
        if(entity.isEnemy()){
            coins += (25 - (25 - 1 - (5) * Math.pow(Math.E, ((-1f / 6f) * (entity.wave - 15f)))));
        } else if(entity.entityType == EntityType.MAINTOWER){
            Controller.instance.endGame(true);
        }
    }

    /**
     * hat eine instanz von jedem handler
     * muss von jedem handler updaten
     *
     * @param dt
     */
    public void update(float dt) {
        timePassed = timePassed + dt;

        gameTime = gameTime + dt;

        if(!ended) {

            if (spawnWave) {
                entityHandler.startWave();
                for (int i = 0; i < Math.pow(1 + wave, 3) / 100 + 5; i++) {
                    int x = random.nextInt(width);
                    int y = 0;
                    int entityIndex = random.nextInt(EntityType.values().length - EntityType.firstEnemyIndex) + EntityType.firstEnemyIndex;
                    Entity entity;
                    if (EntityType.values()[entityIndex].speed > 0) {
                        entity = new MoveEntity(EntityType.values()[entityIndex], 1, x, y, wave, blocks[x][y]);
                    } else {
                        entity = new Entity(EntityType.values()[entityIndex], 1, x, y, wave, blocks[x][y]);
                    }
                    spawnEntity(entity, x, y);
                }
                spawnWave = false;
            }

            int minWave = entityHandler.handleEntities(entityList, dt);

            if (minWave == -1) {
                inWave = false;
            } else if (minWave > Controller.instance.getConfig().getMaxWave()) {
                inWave = true;
                Controller.instance.getConfig().setMaxWave(minWave - 1);
            }
            projectileHandler.handleProjectiles(dt, projectileList);
        }
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getWave() {
        return wave;
    }

    public boolean isInWave() {
        return inWave;
    }

    /**
     * Die Anfrage liefert die vergangene Zeit der Welt als float.
     */
    public float getTimePassed() {
        return timePassed;
    }
    /**
     * Die Anfrage liefert die Blöcke der Welt als Zweidimensionales Array der Klasse Vertex.
     */
    public Block[][] getBlocks() {
        return blocks;
    }

    public LinkedList<Projectile> getProjectileList() {
        return projectileList;
    }

    public LinkedList<Entity> getEntityList() {

        return entityList;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public File getWorldFile() {
        return worldFile;
    }

    public void startWave() {
        this.spawnWave = true;
        this.wave++;
    }

    public boolean inWorld(GameObject gameObject){
        if(gameObject.getX() < -0.5f)
            return false;
        if(gameObject.getX() > width-0.5f)
            return false;
        if(gameObject.getY() < -0.5f)
            return false;
        if(gameObject.getY() > height-0.5f)
            return false;

        return true;
    }

    public void end(){
        ended = true;
    }
}
