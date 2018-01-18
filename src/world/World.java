package world;

import controller.Controller;
import enemy.Enemy;
import enemy.EnemyHandler;
import enemy.EnemyType;
import model.GameObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import projectile.Projectile;
import projectile.ProjectileHandler;
import tower.Tower;
import tower.TowerHandler;
import tower.TowerType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;

import static utility.Utility.random;

public class World {

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

    private File worldFile;

    private ArrayList<Enemy> enemyList;
    private ArrayList<Projectile> projectileList;
    private ArrayList<Tower> towerList;

    private Block[][] blocks;
    private int width, height;
    private float timePassed;

    private int wave = 0;
    private boolean spawnWave = false;
    private float gameTime;

    private int coins = 1000;

    private EnemyHandler enemyHandler;
    private ProjectileHandler projectileHandler;
    private TowerHandler towerHandler;

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

        enemyList = new ArrayList<>();
        projectileList = new ArrayList<>();
        towerList = new ArrayList<>();
        blocks = new Block[width][height];

        for(int i = 0; i < blocks.length; i++){
            for(int j = 0; j < blocks.length; j++){
                blocks[i][j] = new Block();
            }
        }

        this.spawnTower(new Tower(TowerType.MAINTOWER, 1, mainTowerCoordX, mainTowerCoordY));

        newTower = false;

        enemyHandler = new EnemyHandler(this, mainTowerCoordX, mainTowerCoordY);
        projectileHandler = new ProjectileHandler(this);
        towerHandler = new TowerHandler(this);

        setDifficulty(difficulty);

         /**for(int i = 0; i < width;i++){
         for(int j = 0; j < height;j++){
         if(i != mainTowerCoordX || j != mainTowerCoordY){
         this.spawnTower(i,j,new Tower(TowerType.FLAMETHROWER,1,i,j,8));
         }
         }
         }*/
    }

    public void spawnEnemy(Enemy enemy) {
        this.enemyList.add(enemy);
        int xPos = Math.round(enemy.getX());
        int yPos = Math.round(enemy.getY());
        blocks[xPos][yPos].addEnemy(enemy);
    }

    public void spawnProjectile(Projectile projectile) {
        this.projectileList.add(projectile);
    }

    /**
     * Setzt einen Tower in den angegebenen Vertex, wenn dieser frei ist
     * Gibt zürück, ob dies möglich war oder nicht
     */
    public boolean spawnTower(Tower tower) {
        int xPos = (int)(tower.getX());
        int yPos = (int)(tower.getY());
        if (xPos >= 0 && xPos < blocks.length && yPos >= 0 && yPos < blocks[xPos].length && blocks[xPos][yPos].getTower() == null) {
            tower.setX(xPos);
            tower.setY(yPos);
            newTower = true;
            blocks[xPos][yPos].setTower(tower);
            towerList.add(tower);
            return true;
        }
        return false;
    }

    public void removeEnemy(Enemy enemy){
        coins += (25 - (25 - 1 - (5) * Math.pow(Math.E, ((-1f / 6f) * (enemy.wave - 15f)))));
        enemy.getBlock().removeEnemy(enemy);
        enemyList.remove(enemy);
    }

    public void removeTower(Tower tower){
        if (tower.towerType == TowerType.MAINTOWER) {
            towerList.clear();
            Controller.instance.endGame(true);
        } else {
            newTower = true;
            blocks[Math.round(tower.getX())][Math.round(tower.getY())].setTower(null);
            towerList.remove(tower);
        }
    }

    public void removeProjectile(Projectile projectile){
        projectileList.remove(projectile);
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

        if (spawnWave) {
            towerHandler.regenerateTowers(towerList);
            enemyHandler.newWave();
            //this.spawnEnemy(10,0,EnemyType.Cheat);
            for (int i = 0; i < Math.pow(1 + wave, 3) / 100 + 5; i++) {
                this.spawnEnemy(new Enemy(EnemyType.values()[random.nextInt(EnemyType.values().length - 1)], 1, random.nextInt(width), 0, enemyHandler, wave));
            }
            spawnWave = false;
        }


        int minWave = 0;
        if(!enemyList.isEmpty())
            enemyHandler.handleEnemies(dt, enemyList, newTower, isDrunk);

        if (minWave != -1 && minWave-1 > Controller.instance.getConfig().getMaxWave()) {
            Controller.instance.getConfig().setMaxWave(minWave-1);
        }
        projectileHandler.handleProjectiles(dt, projectileList);
        if(!enemyList.isEmpty()) {
            towerHandler.handleTowers(dt, towerList);
        }else{
            towerHandler.noWave(towerList,dt);
        }

        if(newTower) newTower = false;

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

    /**
     * Die Anfrage liefert die Schwierigkeit der Welt als Integer.
     */
    public float getDifficulty() {
        return (enemyHandler.getDpsMultiplier() + 1) * 10;
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

    public ArrayList<Projectile> getProjectileList() {
        return projectileList;
    }

    public ArrayList<Tower> getTowerList() {
        return towerList;
    }

    public ArrayList<Enemy> getEnemyList() {

        return enemyList;
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

    public void setDifficulty(int difficulty) {
        enemyHandler.setDpsMultiplier(((float) difficulty - 1) / 10);
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
}
