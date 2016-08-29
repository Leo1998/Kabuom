package enemy;

import graph.*;
import graph.List;
import graph.Queue;
import tower.Tower;
import tower.TowerType;
import world.World;

import java.util.*;


public class EnemyHandler {

    private Graph adoptedGraph;
    private boolean changed;
    private World world;

    public EnemyHandler(Graph graph,Vertex<Tower>[][] blocks,World world) {
        calcAdoptedGraph(graph,blocks);
        changed = true;
        this.world = world;
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
        for(int i = 0; i < enemies.size(); i++){
            Enemy currEnemy = enemies.get(i);
            if (currEnemy.getHp() <= 0){
                enemies.remove(currEnemy);
            }
        }
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
                    currEnemy.getPath().enqueue(getRandomNeighbour(currEnemy.getPos(),random));
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
        }
        enemy.setAttackCooldown(enemy.getAttackCooldown()+dt);
        Tower tower = checkCollision(enemy,graph);
        float[] test = enemy.getMovement();
        //System.out.println("Movement: "+test[0]+" "+test[1]);
        if(tower != null && (!drunk || random.nextDouble()<0.3 || tower.getType() == TowerType.BARRICADE)){  //Attack
            float[] move = {0,0};
            enemy.setMovement(move);
            if(enemy.getAttackCooldown() > enemy.getAttackSpeed()){
                tower.setHp(tower.getHp()-enemy.getDamage());
            }
        }else{              //Move
            move(enemy,enemy.getSpeed()*dt,graph,dt);
        }
    }

    /**
     * Bewegt einen Gegner entlang des berechneten Weges path
     * @param enemy Der zu bewegene Gegner
     * @param moveableDist Die Strecke, die der Gegner in diesem Frame noch zurücklegen kann
     * @param graph Der Graph
     */
    private void move(Enemy enemy, float moveableDist, Graph graph,float dt){
        Tower collidingTower = checkCollision(enemy,graph);
        if(collidingTower == null && !enemy.getPath().isEmpty()) {
            float[] pos = {enemy.getX(), enemy.getY()};
            VertexData vd = (VertexData) enemy.getPath().front().getContent();
            float[] target = {vd.x, vd.y};
            float dist = (new Double(Math.sqrt(Math.pow(pos[0] - target[0], 2) + Math.pow(pos[1] - target[1], 2)))).floatValue();
            if (dist < moveableDist) {
                enemy.setX(target[0]);
                enemy.setY(target[1]);
                moveableDist = moveableDist - dist;
                enemy.setPos(graph.getVertex(enemy.getPath().front().getID()));
                enemy.getPath().dequeue();
                if (checkCollision(enemy, graph) != null) {
                    move(enemy, moveableDist, graph, dt);
                }
            } else {
                float q = moveableDist / dist;
                float nX = pos[0] + ((target[0] - pos[0]) * q);
                float nY = pos[1] + ((target[1] - pos[1]) * q);
                enemy.setX(nX);
                enemy.setY(nY);
                float[] move = {nX / dt, nY / dt};
                enemy.setMovement(move);
                enemy.setPos(graph.getVertex(enemy.getPath().front().getID()));
            }
        }
    }

    /**
     * Überprüft, ob der Gegner mit einem Turm kollidiert.
     * Überprüft den Turm, von dem der Gegner kommt und den Turm, auf den sich der Gegner zubewegt
     * @param enemy Der Gegner, für den die Kollision überprüft werden soll
     * @return Ist null, wenn der Gegner mit keinem Turm kollidiert. Gibt sonst den Turm mit den der Gegner kollidiert zurück
     */
    private Tower checkCollision(Enemy enemy,Graph graph){
        Tower collidingTower = null;
        Tower currTower = (Tower)enemy.getPos().getContent();
        if(currTower != null && currTower.getHP() > 0){
            if(calcDist(currTower.getX(),currTower.getY(),enemy.getX(),enemy.getY()) < currTower.getRadius() + enemy.getRadius()){
                collidingTower = currTower;
            }
        }
        if(enemy.getPath().front()!= null) {
            currTower = (Tower) graph.getVertex(enemy.getPath().front().getID()).getContent();
            if (currTower != null && currTower.getHP() > 0) {
                if (calcDist(currTower.getX(), currTower.getY(), enemy.getX(), enemy.getY()) < currTower.getRadius() + enemy.getRadius()) {
                    collidingTower = currTower;
                }
            }
        }
        return collidingTower;
    }

    /**
     * Berechnet für alle Gegner, für die dies nötig ist, den besten Weg
     * @param enemies Liste aller Gegner
     * @param targetID ID des Zielvertex
     */
    private void calcAllPaths(ArrayList<Enemy> enemies,String targetID) {
        ArrayList<Queue<Enemy>> qEList = new ArrayList<>();
        for(Enemy currEnemy:enemies){
            if(currEnemy.getPath() == null || changed){
                addToList(qEList,currEnemy);
            }
        }

        if(changed){
            changed = false;
        }

        for(Queue<Enemy> currQueue:qEList){
            Queue<Vertex> path = dijkstraAlgorithm(adoptedGraph.getVertex(currQueue.front().getPos().getID()),adoptedGraph.getVertex(targetID));
            ArrayList<Vertex> pathList = new ArrayList<>();
            while (!path.isEmpty()){
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
            Queue<Enemy> eQueue = new Queue<>();
            eQueue.enqueue(enemy);
            pList.add(eQueue);
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

    private boolean isInList(Vertex v,List<Vertex> vertexList){
        vertexList.toFirst();
        while (vertexList.hasAccess()){
            if(vertexList.getContent() == v) return true;
            vertexList.next();
        }
        return false;
    }

    private void changedTower(Vertex<VertexData> pos,Tower tower){
        VertexData content = pos.getContent();
        float addDPS;
        float dps = tower.getProjectile().getImpactDamage() * tower.getFrequency();
        if(content == null){
            addDPS = dps;
        }else{
            addDPS = dps - pos.getContent().dps;
        }
        pos.getContent().getFromTower(tower);
        DFS(pos,pos.getContent().x,pos.getContent().y,pos.getContent().attackRange,addDPS);
    }

    private void DFS(Vertex<VertexData> currVertex,float startX,float startY,float range,float dps){
        float currX = currVertex.getContent().x;
        float currY = currVertex.getContent().y;
        if(calcDist(startX,startY,currX,currY) <= range){
            List<Vertex> neighbours = adoptedGraph.getNeighbours(currVertex);
            neighbours.toFirst();
            while (neighbours.hasAccess()){
                Vertex curr = neighbours.getContent();
                if(curr.isMarked()){
                    neighbours.remove();
                }else{
                    curr.setMark(true);
                    ((VertexData)(curr.getContent())).dpsInRange=+dps;
                    DFS(curr,startX,startY,range,dps);
                    neighbours.next();
                }
            }
        }
    }

    private void calcEdge(Edge nEdge,Edge oEdge){
        Vertex<VertexData> v1 = nEdge.getVertices()[0];
        Vertex<VertexData> v2 = nEdge.getVertices()[1];
        double dpsInRange = Math.max(v1.getContent().dpsInRange,v2.getContent().dpsInRange);
        double weight = oEdge.getWeight();
        nEdge.setWeight(dpsInRange+weight);
    }

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
            }
        }
    }

    private float calcDist(float x1,float y1,float x2,float y2){
        return (new Double(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)))).floatValue();
    }

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

    private void setGraph(Graph graph){
        changed = true;
        List<Vertex> nVList = graph.getVertices();
        List<Vertex> oVList = adoptedGraph.getVertices();
        Queue<Vertex> vQueue = new Queue<>();
        nVList.toFirst();
        oVList.toFirst();
        while (nVList.hasAccess()){
            while (nVList.getContent().getID() != oVList.getContent().getID()){
                adoptedGraph.removeVertex(oVList.getContent());
                oVList.remove();
            }
            Tower currTower = (Tower) nVList.getContent().getContent();
            VertexData currData = (VertexData) oVList.getContent().getContent();
            if(!currData.name.equals(currTower.getName())){
                vQueue.enqueue(nVList.getContent());
                vQueue.enqueue(oVList.getContent());
            }
            oVList.next();
            nVList.next();
        }
        while (oVList.hasAccess()){
            adoptedGraph.removeVertex(oVList.getContent());
            oVList.remove();
        }
        updateData(vQueue,graph);
    }

    private void updateData(Queue<Vertex> vQueue,Graph graph){
        while (!vQueue.isEmpty()){
            Vertex<Tower> towerVertex = vQueue.front();
            vQueue.dequeue();
            Vertex<VertexData> dataVertex = vQueue.front();
            vQueue.dequeue();
            changedTower(dataVertex,towerVertex.getContent());
        }
        calcEdges(vQueue,graph);
    }

    private class VertexData{
        private String name;
        private float dps,attackRange,dpsInRange;
        private float x,y;
        private double dist;
        private Vertex<VertexData> prev;

        public VertexData(Tower tower,int x,int y){
            this.x = x;
            this.y = y;
            getFromTower(tower);
            dpsInRange = 0;
        }

        private void getFromTower(Tower tower){
            if(tower != null) {
                dps = tower.getProjectile().getImpactDamage() / tower.getFrequency();
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