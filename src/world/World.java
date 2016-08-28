package world;

import enemy.Enemy;
import enemy.EnemyHandler;
import enemy.EnemyType;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import model.GameObject;
import projectile.Projectile;
import projectile.ProjectileHandler;
import projectile.ProjectileType;
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

    /**
     * Attribute für den EnemyHandler
     */
    private boolean newTurretSet;
    private boolean isDrunk;

    public World(int width, int height, int difficulty) {
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
        timePassed = 0;

        int mainTowerCoordX = 10;
        int mainTowerCoordY = height -1;

        this.graph = new Graph();

        this.objects = new ArrayList<>();
        this.blocks = new Vertex[width][height];
        for(int i = 0 ; i< this.blocks.length ; i++ ){
            for(int j = 0 ; j< this.blocks[i].length ; j++ ) {
                blocks[i][j]= new Vertex<>(i+" "+j);

                graph.addVertex(blocks[i][j]);
            }
        }

        connectAll(blocks, graph);

        this.eH = new EnemyHandler(graph, blocks);
        this.pH = new ProjectileHandler(this);
        this.tH = new TowerHandler(this);

        this.mainTower = new Tower(TowerType.MAINTOWER, 1, mainTowerCoordX, mainTowerCoordY , 8);
        this.setTowerInBlocks((int) mainTower.getX(),(int) mainTower.getY(), mainTower);

        this.spawnEnemy(2, 2, EnemyType.Tank);
    }

    public void spawnEnemy(float x, float y, EnemyType type) {
        this.objects.add(new Enemy(type, 1, x, y, blocks[(int) x][(int) y]));
    }

    public void spawnProjectile(Projectile p) {
        this.objects.add(p);
    }

    public void removeGameObject(GameObject o) {
        this.objects.remove(o);

        //TODO: handle tower remove
    }

    private void connectAll(Vertex[][] blocks,Graph graph) {
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                createConnection(blocks, graph, i, j, i + 1, j);
                createConnection(blocks, graph, i, j, i, j + 1);
                createConnection(blocks, graph, i, j, i + 1, j + 1);
                createConnection(blocks, graph, i, j, i + 1, j - 1);
            }
        }
    }

    private void createConnection(Vertex[][] blocks,Graph graph,int x1,int y1,int x2,int y2){
        if(x1 >= 0 && y1 >= 0 && x2 >= 0 && y2 >= 0 && x1 < blocks.length && y1 < blocks[x1].length && x2 < blocks.length && y2 < blocks[x2].length) {
            double length = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
            graph.addEdge(new Edge(blocks[x1][y1], blocks[x2][y2], length));
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

        boolean recalculate = false;
        boolean drunk = false ;

        if(newTurretSet){
           recalculate = true;
            newTurretSet = false;
        }
        if(isDrunk){
            drunk = true;
        }
        eH.handleEnemies(dt,enemyList,getIDOfMainTower(),graph, recalculate, drunk);
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
        return blocks[i][j].getContent() != null;
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

    /**
     * Die Anfrage liefert Pizza zu dir nach Hause.
     */
    public String getIDOfMainTower(){
        return blocks[(int)mainTower.getX()][(int)mainTower.getY()].getID();
    }
}
