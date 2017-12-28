package enemy;

import graph.*;
import graph.List;
import graph.Queue;
import tower.Tower;
import tower.TowerType;
import utility.Vector2;
import world.World;

import java.util.*;


public class EnemyHandler {

    private Graph adoptedGraph;
    private boolean changed;
    private World world;
    private float dpsMultiplier = 1;
    private int maxCalc, calced;

    /**
     * Konstruktur von Enemyhandler
     * Ruft calcAdoptedGraph auf
     * @param graph der Graph von World
     * @param blocks zweidimensionales Array aus allen Vertices des Graphen
     * @param world die World
     */
    public EnemyHandler(Graph graph,Vertex<Tower>[][] blocks,World world) {
        calcAdoptedGraph(graph,blocks);
        changed = true;
        this.world = world;
        maxCalc = 1;
        calced = 0;
    }


    /**
     * Berechnet die Handlungen aller Gegner und führt diese aus
     * @param dt Zeit seit dem letztem Frame
     * @param enemies Liste aller Gegner
     * @param targetID ID des Ziel-Vertex
     * @param graph Der Graph
     * @param recalculate Ist true, wenn in dem Frame ein Turm geädnert oder Drunk aktiviert wurde (einmalig)
     * @param drunk Ist true, wenn Drunk aktiv ist
     */
    public void handleEnemies(float dt, ArrayList<Enemy> enemies,String targetID, Graph graph, boolean recalculate,boolean drunk){
        calced = 0;
        Random random = new Random();
        if(!drunk) {
            if (recalculate) {
                setGraph(graph);
            }
            calcAllPaths(enemies, targetID);
        }else {
            for(int i = 0; i < enemies.size(); i++){
                Enemy currEnemy = enemies.get(i);
                if(currEnemy.getPath().isEmpty() || recalculate){
                    Queue<Vertex> newPath =  new Queue<>();
                    newPath.enqueue(getRandomNeighbour(currEnemy.getPos(),random));
                    currEnemy.setPath(newPath);
                }
            }
            changed = true;
        }
        for(Enemy currEnemy:enemies){
            handleEnemy(dt, graph, currEnemy,drunk,random);
        }
    }

    /**
     * Gibt einen zufälligen Nachbarn von currVertex zurück
     */
    private Vertex getRandomNeighbour(Vertex currVertex,Random random){
        List<Vertex> neighbours = adoptedGraph.getNeighbours(currVertex);
        int length = 0;
        neighbours.toFirst();
        while (neighbours.hasAccess()){
            length++;
            neighbours.next();
        }
        int pos = random.nextInt(length + 1)-1;
        neighbours.toFirst();
        for(int i = 0;i < pos;i++){
            neighbours.next();
        }
        return neighbours.getContent();
    }

    /**
     * Entscheidet, ob ein Gegner sich bewegen oder angreifen soll.
     * Führt dies aus
     * @param dt Zeit seit dem letztem Frame
     * @param graph Der Graph
     * @param enemy Gegner, für den dies entschieden werden soll
     */
    private void handleEnemy(float dt,Graph graph,Enemy enemy,boolean drunk,Random random){
        if(enemy.getHp()<=0){
            world.removeGameObject(enemy);
        }else {
            enemy.setAttackCooldown(enemy.getAttackCooldown() + dt);
            Tower tower = checkCollision(enemy, graph);
            if (tower != null && (!drunk || random.nextDouble() < 0.3 || tower.getType() == TowerType.BARRICADE)) {  //Attack
                enemy.setMovement(new Vector2(0,0));
                if (enemy.getAttackCooldown() > enemy.getAttackSpeed()) {
                    tower.setHp(tower.getHp() - enemy.getDamage());
                    enemy.setAttackCooldown(0);
                    if(tower.getType() == TowerType.BARRICADE){
                        enemy.setHp(Math.round(enemy.getHp()-(enemy.getDamage()*0.5f)));
                    }
                }
            } else {              //Move
                move(enemy, enemy.getSpeed() * dt, graph);
            }
        }
    }

    /**
     * Bewegt einen Gegner entlang des berechneten Weges path
     * @param enemy Der zu bewegene Gegner
     * @param moveableDist Die Strecke, die der Gegner in diesem Frame noch zurücklegen kann
     * @param graph Der Graph
     */
    private void move(Enemy enemy, float moveableDist, Graph graph){
        Tower collidingTower = checkCollision(enemy,graph);
        if(collidingTower == null && enemy.getPath() != null && !enemy.getPath().isEmpty()) {
            VertexData vd = (VertexData) enemy.getPath().front().getContent();
            float targetX = vd.x;
            float targetY = vd.y;
            float dist = (float)(Math.sqrt(Math.pow(enemy.getX() - targetX, 2) + Math.pow(enemy.getY() - targetY, 2)));
            while (dist < moveableDist) {
                enemy.setPos(graph.getVertex(enemy.getPath().front().getID()));
                enemy.getPath().dequeue();
                vd = (VertexData) enemy.getPath().front().getContent();
                targetX = vd.x;
                targetY = vd.y;
                dist = (float)(Math.sqrt(Math.pow(enemy.getX() - targetX, 2) + Math.pow(enemy.getY() - targetY, 2)));
            }
            float q = moveableDist / dist;
            float nX = enemy.getX() + ((targetX - enemy.getX()) * q);
            float nY = enemy.getY() + ((targetY - enemy.getY()) * q);
            enemy.setX(nX);
            enemy.setY(nY);
            Vector2 vec = new Vector2(targetX-enemy.getX(), targetY-enemy.getY());
            vec.normalize();
            vec.multiply(enemy.getEnemyType().getSpeed());
            enemy.setMovement(vec);
            enemy.setPos(graph.getVertex(enemy.getPath().front().getID()));
        }
    }

    /**
     * Überprüft, ob der Gegner mit einem Turm kollidiert.
     * Überprüft den Turm, von dem der Gegner kommt und den Turm, auf den sich der Gegner zubewegt
     * @param enemy Der Gegner, für den die Kollision überprüft werden soll
     * @return Ist null, wenn der Gegner mit keinem Turm kollidiert. Gibt sonst den Turm mit den der Gegner kollidiert zurück
     */
    private Tower checkCollision(Enemy enemy,Graph graph){
        List<Vertex> checkVertex = graph.getNeighbours(enemy.getPos());
        checkVertex.append(enemy.getPos());
        checkVertex.toFirst();
        while (checkVertex.hasAccess()){
            Tower checkTower = (Tower)checkVertex.getContent().getContent();
            if(checkTower != null){
                float dist = calcDist(enemy.getX(),enemy.getY(),checkTower.getX(),checkTower.getY());
                if(dist < new Double(Math.sqrt(2)).floatValue()){
                    return checkTower;
                }
            }
            checkVertex.next();
        }
        return null;
    }

    private float calcDist(float x1,float y1,float x2,float y2){
        return (new Double(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)))).floatValue();
    }

    /**
     * Berechnet für alle Gegner, für die dies nötig ist, den besten Weg
     * @param enemies Liste aller Gegner
     * @param targetID ID des Zielvertex
     */
    private void calcAllPaths(ArrayList<Enemy> enemies,String targetID) {
        ArrayList<Queue<Enemy>> qEList = new ArrayList<>();
        for(Enemy currEnemy:enemies){
            if(currEnemy.getPath() == null || changed || currEnemy.getPath().isEmpty()){
                currEnemy.setPath(null);
                addToList(qEList,currEnemy);
            }
        }

        if(changed){
            changed = false;
        }

        for(Queue<Enemy> currQueue:qEList){
            Queue<Vertex> path = dijkstraAlgorithm(adoptedGraph.getVertex(currQueue.front().getPos().getID()),adoptedGraph.getVertex(targetID));
            ArrayList<Vertex> pathList = new ArrayList<>();
            while (path != null && !path.isEmpty()){
                pathList.add(path.front());
                path.dequeue();
            }
            while (!currQueue.isEmpty()){
                Queue<Vertex> copyPath = new Queue<>();
                for(int i = pathList.size()-1;i >= 0; i--){
                    copyPath.enqueue(pathList.get(i));
                }
                currQueue.front().setPath(copyPath);
                currQueue.dequeue();
            }
        }
    }

    /**
     * Fügt den Gegner an die Stelle der Liste, an der die Gegner in der Queue dieselbe Position haben.
     * Wenn es in den Queues der Liste keine Gegner mit derselben Position wie den übergebenen Gegner gibt, wird eine neue Queue, die diesen enthält ans Ende der Liste gefügt
     * @param pList Liste aus Queues mit Gegnern
     * @param enemy Gegner, der in die richtige Queue der Liste gefügt werden soll
     * @return Liste aus Queues mit Gegnern
     */
    private ArrayList<Queue<Enemy>> addToList(ArrayList<Queue<Enemy>> pList,Enemy enemy){
        boolean added = false;
        for (Queue<Enemy> currQueue:pList){
            Enemy currEnemy = currQueue.front();
            if(currEnemy.getPos() == enemy.getPos()){
                currQueue.enqueue(enemy);
                added = true;
                break;
            }
        }
        if(!added){
            if(calced < maxCalc) {
                calced++;
                Queue<Enemy> eQueue = new Queue<>();
                eQueue.enqueue(enemy);
                pList.add(eQueue);
            }
        }
        return pList;
    }

    /**
     * Führt den Dijkstra-Navigations-Algorithmus auf adoptedGraph aus.
     * @param start Start-Vertex
     * @param target Ziel-Vertex
     * @return Berechneter Pfad
     */
    private Queue<Vertex> dijkstraAlgorithm(Vertex start, Vertex target){
        List<Vertex> vertexList = initiate(start);
        boolean foundTarget = false;
        Vertex currVertex = start;
        do {
            if (currVertex == target) {
                foundTarget = true;
            } else {
                calcDist(currVertex,vertexList);
                currVertex = findSmallest(vertexList);
            }
        } while (!foundTarget && !vertexList.isEmpty());
        if (foundTarget) {
            return goBack(currVertex);
        } else {
            return null;
        }
    }

    /**
     * Nötig für den Dijkstra-Algorithmus
     * Geht vom übergebenen Vertex zurück zum Startvertex
     * Gibt alle Vertices, die auf dem Weg liegen, in einer Queue zurück.
     */
    private Queue<Vertex> goBack(Vertex<VertexData> pVertex){
        Vertex<VertexData> currVertex = pVertex;
        Queue<Vertex> retQueue = new Queue<>();
        retQueue.enqueue(currVertex);
        while (currVertex.getContent().prev != null) {
            Vertex nextVertex = currVertex.getContent().prev;
            retQueue.enqueue(nextVertex);
            currVertex = nextVertex;
        }
        return retQueue;
    }

    /**
     * Nötig für den Dijkstra-Algorithmus
     * Sucht in der übergebenen Liste den Vertex, der als nächstes bearbeitet werden soll und gibt diesen zurück.
     */
    private Vertex findSmallest(List<Vertex> vertexList){
        vertexList.toFirst();
        Vertex<VertexData> v = vertexList.getContent();
        vertexList.next();
        while (vertexList.hasAccess()){
            if(((Vertex<VertexData>)(vertexList.getContent())).getContent().dist < v.getContent().dist){
                v = vertexList.getContent();
            }
            vertexList.next();
        }
        vertexList.toFirst();
        while (vertexList.hasAccess()){
            if(v == vertexList.getContent()){
                vertexList.remove();
                return v;
            }
            vertexList.next();
        }
        return null;
    }

    /**
     * Nötig für den Dijkstra-Algorithmus
     * Berechnet für alle Vertices, die benachbart zum übergebenen Vertex sind, die Distanz vom Start und setzt ggf. diese und den vorherigen Vertex
     */
    private void calcDist(Vertex<VertexData> source,List<Vertex> vertexList){
        List<Edge> edgeList = adoptedGraph.getEdges(source);
        edgeList.toFirst();
        while (edgeList.hasAccess()){
            Vertex<VertexData> v = (source == edgeList.getContent().getVertices()[0]) ? edgeList.getContent().getVertices()[1] : edgeList.getContent().getVertices()[0];
            if(v.getContent().dist > source.getContent().dist + edgeList.getContent().getWeight()){
                v.getContent().dist = source.getContent().dist+edgeList.getContent().getWeight();
                v.getContent().prev = source;
                if(!isInList(v,vertexList)){
                    vertexList.append(v);
                }
            }
            edgeList.next();
        }
    }

    /**
     * Nötig für den Dijkstra-Algorithmus
     * Bereitet alles nötige für den Start des Algorithmus vor.
     */
    private List<Vertex> initiate(Vertex<VertexData> start){
        List<Vertex> vertexList = new List<Vertex>();
        List<Vertex> vList = adoptedGraph.getVertices();
        vList.toFirst();
        while (vList.hasAccess()){
            Vertex<VertexData> v = vList.getContent();
            v.getContent().dist = (Double.MAX_VALUE);
            v.getContent().prev = (null);
            if(v == start){
                v.getContent().dist = 0;
            }
            vList.next();
        }
        return vertexList;
    }

    /**
     * Überprüft, pb der Vertex v in der Liste vertexList ist
     */
    private boolean isInList(Vertex v,List<Vertex> vertexList){
        vertexList.toFirst();
        while (vertexList.hasAccess()){
            if(vertexList.getContent() == v) return true;
            vertexList.next();
        }
        return false;
    }

    /**
     * Berechnet dpsInRange und die Kantengewichter der Vertices um den übergebenen Vertex
     */
    private Queue<Vertex> changedTower(Vertex<VertexData> pos,Tower tower){
        VertexData content = pos.getContent();
        Queue<Vertex> retQueue = new Queue<>();
        if(!(pos.getContent().attackRange == 0)) {
            adoptedGraph.setAllVertexMarks(false);
            DFS(pos, content.x, content.y, pos.getContent().attackRange, -1*pos.getContent().dps, retQueue);
        }
        float dps;
        if(tower == null || tower.getFrequency() == 0){
            dps = 0;
        }else{
            dps = (tower.getProjectile().getImpactDamage() / tower.getFrequency()) * dpsMultiplier;
        }
        content.getFromTower(tower);
        adoptedGraph.setAllVertexMarks(false);
        DFS(pos,content.x,content.y,content.attackRange,dps,retQueue);
        return retQueue;
    }

    /**
     * Setzt dpsInRange mithilfe der Tiefensuche
     */
    private void DFS(Vertex<VertexData> currVertex,float startX,float startY,float range,float dps,Queue<Vertex> vQueue){
        float currX = currVertex.getContent().x;
        float currY = currVertex.getContent().y;
        if(calcDist(startX,startY,currX,currY) <= range){
            vQueue.enqueue(currVertex);
            currVertex.getContent().dpsInRange = currVertex.getContent().dpsInRange+dps;
            List<Vertex> neighbours = adoptedGraph.getNeighbours(currVertex);
            neighbours.toFirst();
            while (neighbours.hasAccess()){
                Vertex curr = neighbours.getContent();
                if(curr.isMarked()){
                    neighbours.remove();
                }else{
                    curr.setMark(true);
                    DFS(curr,startX,startY,range,dps,vQueue);
                    neighbours.next();
                }
            }
        }
    }

    /**
     * Setzt das Gewicht der übergebenen Edge nEdge auf das Gewicht der Edge oEdge + das Maximum von dpsInRange des Knoten, die diese verbindet.
     */
    private void calcEdge(Edge nEdge,Edge oEdge){
        Vertex<VertexData> v1 = nEdge.getVertices()[0];
        Vertex<VertexData> v2 = nEdge.getVertices()[1];
        double dpsInRange = Math.max(v1.getContent().dpsInRange,v2.getContent().dpsInRange);
        double weight = oEdge.getWeight();
        nEdge.setWeight(dpsInRange+weight);
    }

    /**
     * Ruft calcEdge für jede Edge, die an einem Vertice aus der Queue vQueue liegt, auf.
     */
    private void calcEdges(Queue<Vertex> vQueue,Graph oGraph){
        adoptedGraph.setAllEdgeMarks(false);
        while (!vQueue.isEmpty()){
            Vertex currVertex = vQueue.front();
            List<Edge> oEdges = oGraph.getEdges(oGraph.getVertex(currVertex.getID()));
            List<Edge> nEdges = adoptedGraph.getEdges(currVertex);
            oEdges.toFirst();
            nEdges.toFirst();
            while (oEdges.hasAccess() && nEdges.hasAccess()){
                if(!nEdges.getContent().isMarked()){
                    calcEdge(nEdges.getContent(),oEdges.getContent());
                }
                oEdges.next();
                nEdges.next();
            }
            vQueue.dequeue();
        }
    }

    /**
     * Berechnet den adoptedGraph auf Grundlage des Graphe graph
     */
    private void calcAdoptedGraph(Graph graph,Vertex<Tower>[][] blocks){
        Queue<Vertex> vQueue = new Queue<>();
        adoptedGraph = new Graph();
        for(int i = 0;i < blocks.length;i++){
            for(int j = 0;j < blocks[i].length;j++) {
                Vertex<Tower> currVertex = blocks[i][j];
                Vertex<VertexData> nVertex = new Vertex(currVertex.getID());
                nVertex.setContent(new VertexData(currVertex.getContent(),i,j));
                if (nVertex.getContent().dps > 0) {
                    vQueue.enqueue(currVertex);
                    vQueue.enqueue(nVertex);
                }
                adoptedGraph.addVertex(nVertex);
            }
        }
        List<Edge> eList = graph.getEdges();
        eList.toFirst();    //Kopiere Edges
        while (eList.hasAccess()){
            Edge currEdge = eList.getContent();
            Vertex v1 = adoptedGraph.getVertex(currEdge.getVertices()[0].getID());
            Vertex v2 = adoptedGraph.getVertex(currEdge.getVertices()[1].getID());
            Edge nEdge = new Edge(v1,v2,currEdge.getWeight());
            adoptedGraph.addEdge(nEdge);
            eList.next();
        }
        updateData(vQueue,graph);
    }

    /**
     * Passt adoptedGraph dem übergebenen Graphen graph an
     */
    private void setGraph(Graph graph){
        changed = true;
        List<Vertex> nVList = graph.getVertices();
        List<Vertex> oVList = adoptedGraph.getVertices();
        Queue<Vertex> vQueue = new Queue<>();
        nVList.toFirst();
        oVList.toFirst();
        while (nVList.hasAccess()){
            Tower currTower = (Tower) nVList.getContent().getContent();
            VertexData currData = (VertexData) oVList.getContent().getContent();
            if(currTower == null){
                if(!currData.name.equals("dummy")) {
                    vQueue.enqueue(nVList.getContent());
                    vQueue.enqueue(oVList.getContent());
                }
            }else if(!currData.name.equals(currTower.getName())){
                vQueue.enqueue(nVList.getContent());
                vQueue.enqueue(oVList.getContent());
            }
            oVList.next();
            nVList.next();
        }
        updateData(vQueue,graph);
    }

    /**
     * Aktualisiert VertexData der Vertices von vQueue
     * Ruft danach calcEdges auf
     */
    private void updateData(Queue<Vertex> vQueue,Graph graph){
        while (!vQueue.isEmpty()){
            Vertex<Tower> towerVertex = vQueue.front();
            vQueue.dequeue();
            Vertex<VertexData> dataVertex = vQueue.front();
            vQueue.dequeue();
            Queue<Vertex> changedVertices = changedTower(dataVertex,towerVertex.getContent());
            calcEdges(changedVertices,graph);
        }
    }

    /**
     * Gibt dpsMultiplier zurück
     */
    public float getDpsMultiplier() {
        return dpsMultiplier;
    }

    /**
     * Gibt maxCalc zurück
     */
    public int getMaxCalc() {
        return maxCalc;
    }

    /**
     * Setzt maxCalc auf den übergebenen Wert
     */
    public void setMaxCalc(int maxCalc) {
        this.maxCalc = maxCalc;
    }

    /**
     * Setzt dpsMultiplier auf den übergebenen Wert
     */
    public void setDpsMultiplier(float dpsMultiplier) {
        this.dpsMultiplier = dpsMultiplier;
    }

    private class VertexData{
        private String name;
        private float dps,attackRange,dpsInRange;
        private float x,y;
        private double dist;
        private Vertex<VertexData> prev;

        /**
         * Konstruktor von VertexData
         * @param tower Turm, den VertexData repräsentiert
         * @param x X-Position besagten Turmes
         * @param y Y-Position besagten Turmes
         */
        public VertexData(Tower tower,int x,int y){
            this.x = x;
            this.y = y;
            getFromTower(tower);
            dpsInRange = 0;
        }

        /**
         * Setzt dps, attackRange und name abhängig des übergebenen Turmes
         */
        private void getFromTower(Tower tower){
            if(tower != null) {
                if(tower.getFrequency() == 0){
                    dps = 0;
                }else {
                    dps = (tower.getProjectile().getImpactDamage() / tower.getFrequency()) * dpsMultiplier;
                }
                attackRange = tower.getAttackRadius();
                name = tower.getName();
            }else{
                dps = 0;
                attackRange = 0;
                name = "dummy";
            }
        }
    }
}