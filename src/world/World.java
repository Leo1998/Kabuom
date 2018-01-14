package world;

import controller.Controller;
import enemy.Enemy;
import enemy.EnemyHandler;
import enemy.EnemyType;
import projectile.Projectile;
import projectile.ProjectileHandler;
import tower.Tower;
import tower.TowerHandler;
import tower.TowerType;

import java.util.ArrayList;
import java.util.Random;

public class World {

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

    public World(int width, int height, int difficulty) {
        this.width = width;
        this.height = height;
        timePassed = 0;

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
            Controller.instance.endGame();
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
            Random random = new Random();
            for (int i = 0; i < Math.pow(1 + wave, 3) / 100 + 5; i++) {
                this.spawnEnemy(new Enemy(EnemyType.values()[random.nextInt(EnemyType.values().length - 1)], 1, random.nextInt(width), 0, enemyHandler, wave));
            }
            spawnWave = false;
        }

        int minWave = enemyHandler.handleEnemies(dt, enemyList, newTower, isDrunk);
        if (minWave != -1 && minWave-1 > Controller.instance.getConfig().getMaxWave()) {
            Controller.instance.getConfig().setMaxWave(minWave-1);
        }
        projectileHandler.handleProjectiles(dt, projectileList);
        if(!enemyList.isEmpty()) {
            towerHandler.handleTowers(dt, towerList);
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

    public void startWave() {
        this.spawnWave = true;
        this.wave++;
    }

    public void setDifficulty(int difficulty) {
        enemyHandler.setDpsMultiplier(((float) difficulty - 1) / 10);
    }
}
