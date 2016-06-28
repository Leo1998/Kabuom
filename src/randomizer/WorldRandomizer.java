package randomizer;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import tower.Tower;
import tower.TowerType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Daniel on 28.06.2016.
 */
public class WorldRandomizer {

    /**
     * Erstellt einen zufälligen Graphen und gibt diesen zurück
     * @param blocks zweidimensionales Array aus Vertices, legt Größe des Graphen fest und wird mit Vertices gefüllt
     * @return
     */
    public Graph createRandomGraph(Vertex[][] blocks){
        Graph graph = new Graph();

        //-------------Bereite Schleife vor-----------
        Random random = new Random();
        int currX = blocks.length/2;
        int currY = blocks[0].length/2;
        boolean[][] discovered = new boolean[blocks.length][blocks[0].length];
        int[][] timesVisited = new int[blocks.length][blocks[0].length];
        int unvisited = blocks.length * blocks[0].length - 1;
        final int vertexCount = blocks.length * blocks[0].length;
        boolean alwaysAddEdge = false;
        ArrayList<Vertex>[][] neighbours = new ArrayList[blocks.length][blocks[0].length];
        discovered[currX][currY] = true;
        int timeSinceLastAdd = 0;

        //Fülle Graphen mit Vertices
        for(int i = 0;i < blocks.length;i++){
            for(int j = 0;j < blocks[i].length;j++){
                Tower dummyTower = new Tower(TowerType.DUMMY,0,i+" "+j,i,j,0);
                Vertex<Tower> currVertex = new Vertex<>(i+" "+j);
                currVertex.setContent(dummyTower);
                graph.addVertex(currVertex);
                blocks[i][j] = currVertex;
                timesVisited[i][j] = 0;
                neighbours[i][j] = new ArrayList<>();
            }
        }
        //------------Schleife des Todes und der finstern Vernichtung----------------

        //Diese Schleife kann sehr lange brauchen, wird aber nie unendlich lange brauchen
        while (unvisited > 0){  //Wiederhole, solange nicht alle Vertices verbunden
            timesVisited[currY][currX]++;
            
            //Erstelle immer eine Kante zu unentdeckten Knoten, wenn maximal 10% unentdeckt
            if(!alwaysAddEdge){
                if (((double) unvisited) / ((double) vertexCount) < 0.1) {
                    alwaysAddEdge = true;
                }
            }
            
            Vertex currVertex = blocks[currX][currY];

            //Hole alle Nachbarn und bestimme Grad
            int grad = neighbours[currX][currY].size();
            boolean[][] isNeighbour = new boolean[blocks.length][blocks[0].length];
            for(Vertex vertex:neighbours[currX][currY]){
                Tower tower = (Tower) vertex.getContent(); 
                isNeighbour[(int) tower.getX()][(int) tower.getY()] = true;
            }

            //Bestimme maximalen Grad
            int maxGrad = 8;
            if(currX == 0||currX == blocks.length){
                maxGrad =- 2;
            }
            if(currY == 0||currY == blocks[0].length){
                maxGrad =-2;
            }
            maxGrad = (maxGrad == 4) ? 3 : maxGrad;

            boolean addedEdge = false;
            int smallestX = -1;
            int smallestY = -1;
            //Traversiere über alle potenziellen Nachbarn von currVertex
            //Erstellt Nachbarn (Teilweise Random)
            for(int i = Math.max(0,currX-1); i < Math.min(blocks.length,currX+1);i++){
                for(int j = Math.max(0,currY-1); j < Math.min(blocks[i].length,currY+1);j++){
                    if(i != currX || j != currY){
                        if(!isNeighbour[i][j]) {    //Überprüfe, ob blocks[i][j] schon ein Nachbar von currVertex ist
                            //Mache blocks[i][j] zu Nachbarn, wenn alwaysAddEdge und blocks[i][j] unentdeckt oder Random in Abhängigkeit zum Verhätlnis von Grad und maximalen Grad oder wenn seit 3 Schleifendurchläufen keine neue Kante hinzugefügt wurde
                            if((alwaysAddEdge && !discovered[i][j])||(timeSinceLastAdd >= 3)||(random.nextFloat() >= grad/maxGrad)){
                                addedEdge = true;
                                timeSinceLastAdd = 0;
                                if(!discovered[i][j]) {
                                    discovered[i][j] = true;
                                    unvisited--;
                                }
                                isNeighbour[i][j] = true;
                                neighbours[i][j].add(blocks[currX][currY]);
                                neighbours[currX][currY].add(blocks[i][j]);
                                double weight = (i == currX || j == currY) ? 1 : Math.sqrt(2);
                                graph.addEdge(new Edge(blocks[i][j],blocks[currX][currY],weight));
                                //Finde am seltensten besuchten Nachbarn
                                if (smallestX == -1||smallestY == -1||timesVisited[smallestX][smallestY] < timesVisited[i][j]){
                                    smallestX = i;
                                    smallestY = j;
                                }
                            }
                        }else{
                            //Finde am seltensten besuchten Nachbarn
                            if (smallestX == -1||smallestY == -1||timesVisited[smallestX][smallestY] < timesVisited[i][j]){
                                smallestX = i;
                                smallestY = j;
                            }
                        }
                    }
                }
            }
            
            //Bereite nächsten Schleifendurchlauf vor
            if(!addedEdge){
                timeSinceLastAdd++;
            }
            currX = smallestX;
            currY = smallestY;
        }
        return graph;
    }
}
