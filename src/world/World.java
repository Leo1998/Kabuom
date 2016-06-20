package world;

import enemy.Enemy;
import enemy.EnemyHandler;
import graph.*;
import model.GameObject;
import projectile.Projectile;
import projectile.ProjectileHandler;
import tower.*;

import java.util.ArrayList;

public class World {
    private ArrayList<GameObject> objects;
    private Graph graph;
    private Vertex<Tower>[][] blocks;
    private int width, height, difficulty;
    private float timePassed;
    private EnemyHandler eH;
    private ProjectileHandler pH;
    private TowerHandler tH;

    public World(int width, int height, int difficulty) {
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
        timePassed = 0;

        this.objects = new ArrayList<>();
        this.blocks = new Vertex[width][height];
        for(int i = 0 ; i< this.blocks.length ; i++ ){
            for(int j = 0 ; j< this.blocks[i].length ; j++ ) {
                blocks[i][j]= new Vertex<Tower>(i+" "+j);
                blocks[i][j].setContent(new Tower(TowerType.DUMMY,12,"Leer",i,j,0));
            }
        }
    }

    /**
     * hat eine instanz von jedem handler
     * muss von jedem handler updaten
     * @param dt
     */
    public void update(float dt){
        ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
        ArrayList<Projectile> projectileList = new ArrayList<Projectile>();
        ArrayList<Tower> towerList = new ArrayList<Tower>();

        for(int i = 0; i < objects.size(); i++){
            GameObject object = objects.get(i);

            if (object instanceof Enemy){
                enemyList.add((Enemy) object);
            } else if(object instanceof Projectile){
                projectileList.add((Projectile) object);
            } else if(object instanceof Tower){
                towerList.add((Tower) object);
            }
        }
        //eH.handleEnemies(dt,enemyList,towerList);
        pH.handleProjectiles(dt, projectileList, enemyList);
        tH.handleTowers(dt, towerList, enemyList, null);//TODO: mainTower???????
    }
    /**
     * Die Anfrage liefert die Schwierigkeit der Welt als Integer.
     */
    public int getDifficulty(){
        return difficulty;
    }
    /**
     * Die Anfrage liefert die vergangene Zeit der Welt als float.
     */
    public float getTimePassed(){
        return timePassed;
    }
    /**
     * Die Anfrage liefert den Graphen der Welt als Graph.
     */
    public Graph getGraph(){
        return graph;
    }
    /**
     * Die Anfrage liefert die Blöcke der Welt als Zweidimensionales Array der Klasse Vertex.
     */
    public Vertex<Tower>[][] getBlocks(){
        return blocks;
    }

    public ArrayList<GameObject> getObjects() {
        return objects;
    }
}
