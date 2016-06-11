package enemy;

import graph.*;
import projectile.ProjectileType;
import tower.*;

/**
 * Created by Daniel on 09.06.2016.
*/
public class EnemyHandler {
    private Graph adoptedGraph;

    public EnemyHandler(Graph graph) {
        calcAdoptedGraph(graph);
    }

    public void handleEnemies(float dt, List<Enemy> enemies, List<Tower> towers){

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
            currVertex.getContent().dpsInRange = currVertex.getContent().dpsInRange + dps;
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
        while (!vQueue.isEmpty()){
            Vertex<Tower> towerVertex = vQueue.front();
            vQueue.dequeue();
            Vertex<VertexData> dataVertex = vQueue.front();
            vQueue.dequeue();
            changedTower(dataVertex,towerVertex.getTowerType());
        }
    }

    private class VertexData{
        private int dps,attackRange,projectileRange,dpsInRange;
        private float x,y;

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
        }
    }

}
