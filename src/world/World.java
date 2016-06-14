package world;

import enemy.Enemy;
import enemy.EnemyHandler;
import graph.*;
import model.GameObject;
import projectile.Projectile;
import projectile.ProjectileHandler;
import tower.*;

/**
 * Created by Daniel on 09.06.2016.
 */
public class World {
    private List<GameObject> objects;
    private Graph graph;
    private Vertex[][] blocks;
    private int width,height,difficulty;
    private float timePassed;
    private EnemyHandler eH;
    private ProjectileHandler pH;
    private TowerHandler tH;

    public World(int width, int height, int difficulty) {
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
        timePassed = 0;
    }

    /**
     * hat eine instanz von jedem handler
     * muss von jedem handler updaten
     * @param dt
     */
    public void update(float dt){

        List<Enemy> enemyList = new List<Enemy>();
        List<Projectile> projectileList = new List<Projectile>();
        List<Tower> towerList = new List<Tower>();

        objects.toFirst();
        while(objects.hasAccess()){
            if (objects.getContent().getClass().equals(Enemy.class)){
                enemyList.append((Enemy)objects.getContent());
            } else if(objects.getContent().getClass().equals(Projectile.class)){
                projectileList.append((Projectile) objects.getContent());
            } else if(objects.getContent().getClass().equals(Tower.class)){
                towerList.append((Tower) objects.getContent());
            }
            objects.next();
        }
        eH.handleEnemies(dt,enemyList,towerList);
        pH.handleProjectiles(dt,projectileList,enemyList);
        tH.handleTowers(dt,towerList,enemyList);
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
    public Vertex[][] getBlocks(){
        return blocks;
    }
}
