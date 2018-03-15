package world;

import controller.Controller;
import entity.EntityHandler;
import entity.model.Entity;
import entity.model.EntityType;
import entity.model.Minion;
import entity.model.MoveEntity;
import model.GameObject;
import projectile.Projectile;
import projectile.ProjectileHandler;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import static utility.Utility.random;

public class World {

    private LinkedList<Projectile> projectileList;
    private LinkedList<Entity> entityList;
    private LinkedList<Projectile> newProjectiles;
    private LinkedList<Entity> newEntities;

    private Block[][] blocks;
    private int width, height;

    private int wave, coins, difficulty;
    private boolean spawnWave = false, inWave = false, ended = false;

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
        entityHandler = new EntityHandler(this,mainTower,Controller.instance.getConfig().getAiDifficulty());
        difficulty = Controller.instance.getConfig().getDifficulty();
    }

    public World(File file) throws IOException{
        InputStream is = new FileInputStream(file);

        long length = file.length();

        if(length < byteSize()){
            throw new IOException("File " + file.getName() + " could not be read. It was to short (" + length + " min size: " + byteSize() + ")");
        }

        ByteBuffer buf = ByteBuffer.allocateDirect(byteSize());
        byte[] bytes = new byte[byteSize()];
        is.read(bytes, 0, bytes.length);
        buf.put(bytes);

        buf.flip();

        this.width = buf.getInt();
        this.height = buf.getInt();
        this.wave = buf.getInt();
        this.coins = buf.getInt();

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

        boolean reading = true;
        while(reading){
            byte b = (byte)is.read();

            if(b != -1) {

                boolean isMove, isMinion;
                isMove = (b & Entity.byteMask.isMove.mask) == Entity.byteMask.isMove.mask;
                isMinion = (b & Entity.byteMask.isMinion.mask) == Entity.byteMask.isMinion.mask;

                Entity entity = null;
                if (isMove) {
                    if (isMinion) {
                        bytes = new byte[Minion.byteSize()];
                        bytes[0] = b;

                        buf = ByteBuffer.allocateDirect(bytes.length);

                        if (is.read(bytes, 1, bytes.length - 1) == bytes.length - 1) {
                            buf.put(bytes);
                            buf.flip();

                            entity = new Minion(buf, blocks);
                        } else {
                            reading = false;
                        }
                    } else {
                        bytes = new byte[MoveEntity.byteSize()];
                        bytes[0] = b;

                        buf = ByteBuffer.allocateDirect(bytes.length);

                        if (is.read(bytes, 1, bytes.length - 1) == bytes.length - 1) {
                            buf.put(bytes);
                            buf.flip();

                            entity = new MoveEntity(buf, blocks);
                        } else {
                            reading = false;
                        }
                    }
                } else {
                    bytes = new byte[Entity.byteSize()];
                    bytes[0] = b;

                    buf = ByteBuffer.allocateDirect(bytes.length);

                    if (is.read(bytes, 1, bytes.length - 1) == bytes.length - 1) {
                        buf.put(bytes);
                        buf.flip();

                        entity = new Entity(buf, blocks);
                    } else {
                        reading = false;
                    }
                }
                if (reading) {
                    if (entity.isType(EntityType.MAINTOWER))
                        mainTower = entity;

                    entityList.add(entity);

                    System.out.println(entity);
                }
            } else {
                reading = false;
            }
        }

        if(mainTower == null){
            throw new IOException("File " + file.getName() + " could not be read. No maintower was found.");
        } else {
            projectileHandler = new ProjectileHandler(this);
            entityHandler = new EntityHandler(this,mainTower,Controller.instance.getConfig().getAiDifficulty());
            difficulty = Controller.instance.getConfig().getDifficulty();
        }

        is.close();
    }

    public void write(File file) throws IOException{
        BufferedOutputStream bos = null;

        try{
            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);

            byte[] bytes = new byte[byteSize()];
            ByteBuffer buf = ByteBuffer.allocateDirect(byteSize());
            buf.putInt(width);
            buf.putInt(height);
            buf.putInt(wave);
            buf.putInt(coins);

            buf.flip();
            buf.get(bytes,0,bytes.length);

            bos.write(bytes);

            for(Entity entity : entityList){
                bytes = new byte[entity.byteSize()];
                buf = ByteBuffer.allocateDirect(bytes.length);

                entity.write(buf);

                buf.flip();
                buf.get(bytes,0,bytes.length);

                bos.write(bytes);
            }
        } finally {
            if(bos != null){
                try{
                    bos.flush();
                    bos.close();
                } catch(Exception e){}
            }
        }
    }

    public static int byteSize(){
        return 4*4;
    }

    public int getRanged(){
        return entityHandler.getRanged();
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

            if(!tower.isType(EntityType.MAINTOWER)) {
                coins -= tower.getCost();
            }

            return true;
        }
        return false;
    }

    public void sellTower(int x, int y){
        Entity entity = blocks[x][y].getTower();
        if (entity != null && !entity.isType(EntityType.MAINTOWER)) {
            coins += entity.getCost() * (entity.getHp() / entity.getMaxHp());
            removeEntity(entity);
        }
    }

    public void removeEntity(Entity entity){
        entity.setHp(-1);
        if(entity.isEnemy()){
            coins += entity.getReward() * 1000/ getAmount(entity.getWave());
        } else if(entity.isType(EntityType.MAINTOWER)){
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

        int amount = getAmount(wave);

        while (amount > 0){
            int x = random.nextInt(width);
            int y = 0;
            int entityIndex = random.nextInt(EntityType.values().length - EntityType.firstEnemyIndex) + EntityType.firstEnemyIndex;
            int level = (int)(random.nextFloat()*0.25f*amount/EntityType.values()[entityIndex].cost);
            Entity entity;
            if (EntityType.values()[entityIndex].speed > 0) {
                entity = new MoveEntity(EntityType.values()[entityIndex], level, x, y, wave, blocks[x][y], true);
            } else {
                entity = new Entity(EntityType.values()[entityIndex], level, x, y, wave, blocks[x][y], true);
            }
            amount -= entity.getReward();
            spawnEntity(entity, x, y);
        }

        spawnWave = false;
    }

    private int getAmount(int wave){
        return (int)(Math.pow(1 + ((double)difficulty/50d) * (double)wave,2) + 5);
    }

    public int getStrength(){
        return getAmount(wave+1);
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
