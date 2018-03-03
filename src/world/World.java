package world;

import controller.Controller;
import entity.EntityHandler;
import entity.model.Entity;
import entity.model.EntityType;
import entity.model.MoveEntity;
import model.GameObject;
import projectile.Projectile;
import projectile.ProjectileHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedList;

import static utility.Utility.random;

public class World {

    private LinkedList<Projectile> projectileList;
    private LinkedList<Entity> entityList;
    private LinkedList<Projectile> newProjectiles;
    private LinkedList<Entity> newEntities;

    private Block[][] blocks;
    private int width, height;

    private int wave;
    private boolean spawnWave = false, inWave = false, ended = false;

    private int coins;

    private EntityHandler entityHandler;
    private ProjectileHandler projectileHandler;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.coins = 1000;
        this.wave = 0;

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

        Entity mainTower = new Entity(EntityType.MAINTOWER, 0, mainTowerCoordX, mainTowerCoordY, 0, blocks[mainTowerCoordX][mainTowerCoordY], false);
        blocks[mainTowerCoordX][mainTowerCoordY].addEntity(mainTower);
        entityList.add(mainTower);

        projectileHandler = new ProjectileHandler(this);
        entityHandler = new EntityHandler(this,mainTower,Controller.instance.getConfig().getDifficulty());
    }

    public World(JSONObject object){
        this.width = object.getInt("width");
        this.height = object.getInt("height");
        this.coins = object.getInt("coins");
        this.wave = object.getInt("wave");

        blocks = new Block[width][height];
        for(int i = 0; i < blocks.length; i++){
            for(int j = 0; j < blocks[i].length; j++){
                blocks[i][j] = new Block(i,j);
            }
        }

        projectileList = new LinkedList<>();
        entityList = new LinkedList<>();
        newProjectiles = new LinkedList<>();
        newEntities = new LinkedList<>();

        Entity mainTower = null;

        JSONArray entities = object.getJSONArray("entities");

        for(int i = 0; i < entities.length(); i++){
            JSONObject entityObj = entities.getJSONObject(i);
            Entity entity;
            if(entityObj.getBoolean("isMove")){
                entity = new MoveEntity(entityObj,blocks);
            } else {
                entity = new Entity(entityObj, blocks);
            }

            if(entity.isMaintower()){
                mainTower = entity;
            }

            entityList.add(entity);
        }

        if(mainTower == null){
            throw new IllegalArgumentException();
        } else {
            projectileHandler = new ProjectileHandler(this);
            entityHandler = new EntityHandler(this,mainTower,Controller.instance.getConfig().getDifficulty());
        }
    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();

        obj.put("width", width);
        obj.put("height", height);
        obj.put("wave", wave);
        obj.put("coins", coins);

        JSONArray entities = new JSONArray();
        for(Entity entity : entityList){
            entities.put(entity.toJSON());
        }
        obj.put("entities",entities);

        return obj;
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

    public boolean upgradeTower(int x, int y){
        if(x >= 0 && x < blocks.length && y >= 0 && y < blocks[x].length){
            Entity tower = blocks[x][y].getTower();
            if(tower != null){
                if(tower.getCost() <= coins){
                    coins -= tower.getCost();
                    tower.upgrade();

                    entityHandler.newTower(x,y);

                    return true;
                }
            }
        }
        return false;
    }

    public boolean setTower(Entity tower){
        int xPos = (int)(tower.getX());
        int yPos = (int)(tower.getY());
        if (xPos >= 0 && xPos < blocks.length && yPos >= 0 && yPos < blocks[xPos].length && blocks[xPos][yPos].getTower() == null) {
            tower.setX(xPos);
            tower.setY(yPos);
            tower.setWave(wave);
            tower.setBlock(blocks[xPos][yPos]);
            blocks[xPos][yPos].addEntity(tower);
            newEntities.add(tower);
            entityHandler.newTower(xPos,yPos);

            if(!tower.isMaintower()) {
                coins -= tower.getCost();
            }

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
            coins += (entity.getLevel()+1) * 1000/(Math.pow(1+wave,2)/10 + 5);
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

        if(!ended) {

            if (spawnWave) {
                spawnWave();
            }

            int minWave = entityHandler.handleEntities(entityList, dt);

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
        Controller.instance.saveGame();

        entityHandler.startWave();

        int amount = (int)(Math.pow(1+wave,2)/10 + 5);

        while (amount > 0){
            int x = random.nextInt(width);
            int y = 0;
            int entityIndex = random.nextInt(EntityType.values().length - EntityType.firstEnemyIndex) + EntityType.firstEnemyIndex;
            int level = (int)(random.nextFloat()*0.25f*amount);
            amount -= EntityType.values()[entityIndex].cost + level;
            Entity entity;
            if (EntityType.values()[entityIndex].speed > 0) {
                entity = new MoveEntity(EntityType.values()[entityIndex], level, x, y, wave, blocks[x][y], true);
            } else {
                entity = new Entity(EntityType.values()[entityIndex], level, x, y, wave, blocks[x][y], true);
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
