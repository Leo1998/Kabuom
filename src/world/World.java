package world;

import enemy.Enemy;
import enemy.EnemyHandler;
import enemy.EnemyType;
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
    private Tower mainTower;

    public World(int width, int height, int difficulty) {
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
        timePassed = 0;

        this.graph = new Graph();

        this.objects = new ArrayList<>();
        this.blocks = new Vertex[width][height];
        for(int i = 0 ; i< this.blocks.length ; i++ ){
            for(int j = 0 ; j< this.blocks[i].length ; j++ ) {
                blocks[i][j]= new Vertex<Tower>(i+" "+j);
                blocks[i][j].setContent(new Tower(TowerType.DUMMY,12,"Leer",i,j,0));

                graph.addVertex(blocks[i][j]);
            }
        }

        //connectAll(graph);

        this.eH = new EnemyHandler(graph);
        this.pH = new ProjectileHandler();
        this.tH = new TowerHandler();

        this.mainTower = new Tower(TowerType.MAINTOWER, 1, "Main Tower", 10, 10, 8);
        this.setTowerInBlocks(10, 10, mainTower);

        this.spawnEnemy(2, 2, EnemyType.Tank);
    }

    public void spawnEnemy(float x, float y, EnemyType type) {
        this.objects.add(new Enemy(type, 1, x, y, blocks[(int) x][(int) y]));
    }

    private void connectAll(Graph graph) {
        List<Vertex> l1 = graph.getVertices();
        List<Vertex> l2 = graph.getVertices();

        l1.toFirst();
        while(l1.hasAccess()) {
            l2.toFirst();
            while(l2.hasAccess()) {
                graph.addEdge(new Edge(l1.getContent(), l2.getContent(), 1000));
                l2.next();
            }
            l1.next();
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

        //eH.handleEnemies(dt,enemyList,blocks[10][10].getID(),graph,true,true);//TODO: ZielID->ID des Zielvektors , recalculate(false)->true,wenn Tower platziert oder drunk aktiviert wurde , drunk(true)->true wenn drunk aktiv ist
        pH.handleProjectiles(dt, projectileList, enemyList);
        tH.handleTowers(dt, towerList, enemyList, this.mainTower);
    }

    /**
     * Setzt einen Tower in den angegebenen Vertex, wenn dieser frei ist
     * Gibt zürück, ob dies möglich war oder nicht
     */
    public boolean setTowerInBlocks(int i, int j, Tower setTower){
        if(!isTowerAtCoords(i,j)&& setTower != null){
            blocks[i][j].setContent(setTower);
            objects.add(setTower);
            return true;
        }
            return false;
    }

    /**
     * Prüft, ob an den Blockkoordinaten i und j ein Tower vorhanden ist
     */
    public boolean isTowerAtCoords(int i, int j){
        return blocks[i][j].getContent().getType() != TowerType.DUMMY;
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
