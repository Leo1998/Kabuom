package world;

import controller.Controller;
import entity.EntityHandler;
import entity.model.Entity;
import entity.model.EntityType;
import entity.model.MoveEntity;
import model.GameObject;
import projectile.Projectile;
import projectile.ProjectileHandler;

import java.io.File;
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
    private LinkedList<Projectile> newProjectiles;
    private LinkedList<Entity> newEntities;

    private Block[][] blocks;
    private int width, height;
    private float timePassed;

    private int wave = 0;
    private boolean spawnWave = false, inWave = false, ended = false, print = false;
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
        newProjectiles = new LinkedList<>();
        newEntities = new LinkedList<>();
        blocks = new Block[width][height];

        for(int i = 0; i < blocks.length; i++){
            for(int j = 0; j < blocks[i].length; j++){
                blocks[i][j] = new Block(i,j);
            }
        }

        Entity mainTower = new Entity(EntityType.MAINTOWER, 0, mainTowerCoordX, mainTowerCoordY, -1, blocks[mainTowerCoordX][mainTowerCoordY], false);

        projectileHandler = new ProjectileHandler(this);
        entityHandler = new EntityHandler(this,mainTower);

        this.setTower(mainTower);

        newTower = false;
    }

    public void printEntities(int x, int y){
        System.out.println(blocks[x][y]);
    }

    public void spawnProjectile(Projectile projectile) {
        newProjectiles.add(projectile);
    }

    public void spawnEntity(Entity entity, int x, int y){
        newEntities.add(entity);
        blocks[x][y].addEntity(entity);
    }

    public boolean setTower(Entity tower){
        int xPos = (int)(tower.getX());
        int yPos = (int)(tower.getY());
        if (xPos >= 0 && xPos < blocks.length && yPos >= 0 && yPos < blocks[xPos].length && blocks[xPos][yPos].getTower() == null) {
            tower.setX(xPos);
            tower.setY(yPos);
            tower.setWave(wave);
            newTower = true;
            blocks[xPos][yPos].setTower(tower);
            tower.setBlock(blocks[xPos][yPos]);
            newEntities.add(tower);
            entityHandler.newTower(xPos,yPos);

            coins -= tower.getCost();

            return true;
        }
        return false;
    }

    public void sellTower(int x, int y){
        Entity entity = blocks[x][y].getTower();
        if (entity != null && !entity.isMaintower()) {
            coins += entity.getCost() * (entity.getHp() / entity.getMaxHp());
            removeEntity(entity);
        }
    }

    public void removeEntity(Entity entity){
        entity.setHp(-1);
        if(entity.isEnemy()){
            coins += entity.getCost()*25;
        } else if(entity.isMaintower()){
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
                spawnWave();
            }

            int minWave = entityHandler.handleEntities(entityList, dt, isDrunk);

            if (minWave == -1) {
                inWave = false;
            } else {
                inWave = true;
                if (minWave > Controller.instance.getConfig().getMaxWave()) {
                    Controller.instance.getConfig().setMaxWave(minWave - 1);
                }
            }
            projectileHandler.handleProjectiles(dt, projectileList);

            if(!newProjectiles.isEmpty()) {
                projectileList.addAll(newProjectiles);

                newProjectiles.clear();
            }

            if(!newEntities.isEmpty()) {
                entityList.addAll(newEntities);

                newEntities.clear();
            }
        }
    }

    private void spawnWave(){
        entityHandler.startWave();
        for (int i = 0; i < wave*2+4;) {
            int x = random.nextInt(width);
            int y = 0;
            int entityIndex = random.nextInt(EntityType.values().length - EntityType.firstEnemyIndex) + EntityType.firstEnemyIndex;
            //int entityIndex = EntityType.firstEnemyIndex+6;
            i+= EntityType.values()[entityIndex].cost;
            Entity entity;
            if (EntityType.values()[entityIndex].speed > 0) {
                entity = new MoveEntity(EntityType.values()[entityIndex], 0, x, y, wave, blocks[x][y], true);
            } else {
                entity = new Entity(EntityType.values()[entityIndex], 0, x, y, wave, blocks[x][y], true);
            }
            spawnEntity(entity, x, y);
        }
        spawnWave = false;
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

    public int countEntities(){
        return (entityList == null ? 0 : entityList.size());
    }

    public void startWave() {
        this.spawnWave = true;
        this.wave++;
    }

    public boolean inWorld(GameObject gameObject){
        return !(gameObject.getX() < -0.5f) && !(gameObject.getX() > width-0.5f) && !(gameObject.getY() < -0.5f) && !(gameObject.getY() > height-0.5f);
    }

    public void end(){
        ended = true;
    }
}
