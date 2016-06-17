package enemy;

import graph.*;
import projectile.ProjectileType;
import tower.*;

/**
public class EnemyHandler {

    private Graph adoptedGraph;
    private boolean changed;

    public EnemyHandler(Graph graph) {
        calcAdoptedGraph(graph);
        changed = true;
    }

    public void handleEnemies(float dt, List<Enemy> enemies,String targetID, Graph graph, boolean placedTower){
        if(placedTower){
            setGraph(graph);
        }
        calcAllPaths(enemies,targetID);
        moveEnemies(dt,graph,enemies);
    }

    private void moveEnemies(float dt,Graph graph,List<Enemy> enemies){
        enemies.toFirst();
        while (enemies.hasAccess()){
            handleEnemy(dt,graph,enemies.getContent());
            enemies.next();
        }
    }

    private void handleEnemy(float dt,Graph graph,Enemy enemy){
        enemy.setAttackCooldown(enemy.getAttackCooldown()+dt);
        Tower tower = checkCollision(enemy);
        if(tower != null){
            if(enemy.getAttackCooldown() > enemy.getAttackSpeed()){
                tower.setHp(tower.getHp()-enemy.getDamage());
            }
        }else{

        }
    }

    private void move(Enemy enemy, Graph graph){
        Edge pos = enemy.getPos();
        String id1 = pos.getVertices()[0].getID();
        String id2 = pos.getVertices()[1].getID();
    }

    private Tower checkCollision(Enemy enemy){
        Vertex<Tower> v1 = enemy.getPos().getVertices()[0];
        Vertex<Tower> v2 = enemy.getPos().getVertices()[1];
        if(v1.getContent().getHp() > 0){
            Tower tower = v1.getContent();
            if(calcDist(tower.getX(),tower.getY(),enemy.getX(),enemy.getY()) < tower.getRadius() + enemy.getRadius()){
                return tower;
            }
        }
        if(v2.getContent().getHp() > 0){
            Tower tower = v2.getContent();
            if(calcDist(tower.getX(),tower.getY(),enemy.getX(),enemy.getY()) < tower.getRadius() + enemy.getRadius()){
                return tower;
            }
        }
        return null;
    }

    private void calcAllPaths(List<Enemy> enemies,String targetID) {
        List<Queue<Enemy>> qEList = new List<>();
        enemies.toFirst();
        while (enemies.hasAccess()){
            Enemy currEnemy = enemies.getContent();
            if(currEnemy.getPath() == null || changed){
                addToList(qEList,currEnemy);
            }
            enemies.next();
        }

        qEList.toFirst();
        while (qEList.hasAccess()){
            Queue<Enemy> currQueue = qEList.getContent();
            Queue<Vertex> path = dijkstraAlgorithm(adoptedGraph.getVertex(currQueue.front().getPos().getVertices()[0].getID()),adoptedGraph.getVertex(targetID));
            while (!currQueue.isEmpty()){
                currQueue.front().setPath(path);
                currQueue.dequeue();
            }
        }
    }

    private List<Queue<Enemy>> addToList(List<Queue<Enemy>> pList,Enemy enemy){
        boolean added = false;
        pList.toFirst();
        while (pList.hasAccess()){
            Enemy currEnemy = pList.getContent().front();
            if(currEnemy.getPos() == enemy.getPos()){
                pList.getContent().enqueue(enemy);
                pList.toLast();
                added = true;
            }
            pList.next();
        }
        if(!added){
            Queue<Enemy> eQueue = new Queue<>();
            eQueue.enqueue(enemy);
            pList.append(eQueue);
        }
        return pList;
    }

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
        Queue<Vertex> retList = new Queue<Vertex>();
        retList.enqueue(pVertex);
        while (pVertex.getContent().prev != null) {
            Vertex nextVertex = (pVertex == pVertex.getContent().prev.getVertices()[0]) ? pVertex.getContent().prev.getVertices()[1] : pVertex.getContent().prev.getVertices()[0];
            retList.enqueue(nextVertex);
            pVertex = nextVertex;
        }
        return retList;
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
                v.getContent().dist = ((int)(source.getContent().dist+edgeList.getContent().getWeight()));
                v.getContent().prev = (edgeList.getContent());
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

    private void changedTower(Vertex<VertexData> pos,TowerType towerType){
        VertexData content = pos.getContent();
        int addDPS;
        int dps = towerType.getProjectileType.getDamage()*towerType.getFrequency();
        if(content == null){
            addDPS = dps;
        }else{
            addDPS = dps - pos.getContent().dps;
        }
        pos.getContent().getFromTowerType(towerType);
        DFS(pos,pos.getContent().x,pos.getContent().y,pos.getContent().attackRange,addDPS);
    }

    private void DFS(Vertex<VertexData> currVertex,float startX,float startY,float range,int dps){
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

    private void calcAdoptedGraph(Graph graph){
        Queue<Vertex> vQueue = new Queue<>();
        adoptedGraph = new Graph();
        List<Vertex> vList = graph.getVertices();
        vList.toFirst();    //Kopiere Vertices
        while (vList.hasAccess()){
            Vertex<Tower> currVertex = vList.getContent();
            Vertex<VertexData> nVertex = new Vertex(currVertex.getID());
            nVertex.setContent(new VertexData(currVertex.getContent().getTowerType()), currVertex.getContent().getX(), currVertex.getContent().getY());
            if(nVertex.getContent().dps > 0){
                vQueue.enqueue(nVertex);
                vQueue.enqueue(currVertex);
            }
            adoptedGraph.addVertex(nVertex);
            vList.next();
        }
        List<Edge> eList = graph.getEdges();
        eList.toFirst();    //Kopiere Edges
        while (eList.hasAccess()){
            Edge currEdge = eList.getContent();
            Vertex v1 = adoptedGraph.getVertex(currEdge.getVertices()[0].getID());
            Vertex v2 = adoptedGraph.getVertex(currEdge.getVertices()[1].getID());
            Edge nEdge = new Edge(v1,v2,currEdge.getWeight());
            adoptedGraph.addEdge(nEdge);
        }
        updateData(vQueue);
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
                vQueue.enqueue(oVList.getContent());
                vQueue.enqueue(nVList.getContent());
            }
            nVList.next();
            oVList.next();
        }
        while (oVList.hasAccess()){
            adoptedGraph.removeVertex(oVList.getContent());
            oVList.remove();
        }
        updateData(vQueue);
    }

    private void updateData(Queue<Vertex> vQueue){
        while (!vQueue.isEmpty()){
            Vertex<Tower> towerVertex = vQueue.front();
            vQueue.dequeue();
            Vertex<VertexData> dataVertex = vQueue.front();
            vQueue.dequeue();
            changedTower(dataVertex,towerVertex.getTowerType());
        }
    }

    private class VertexData{
        private String name;
        private int dps,attackRange,projectileRange,dpsInRange;
        private float x,y;
        private double dist;
        private Edge prev;

        public VertexData(TowerType towerType,float x,float y){
            getFromTowerType(towerType);
            this.x =x;
            this.y = y;
            dpsInRange = 0;
        }

        private void getFromTowerType(TowerType towerType){
            ProjectileType projectileType = towerType.getProjectileType;
            dps = projectileType.getDamage()*towerType.getFrequency();
            attackRange = towerType.getAttackRange();
            projectileRange = projectileType.getRange();
            name = towerType.getName();
        }
    }
}
 */