package randomizer;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import tower.Tower;

import java.util.Random;

/**
 * Created by Daniel on 28.06.2016.
 */
public class GraphRandomizer {

    /**
     * Erstellt einen zufälligen, zusammenhängenden Graphen und gibt diesen zurück
     *
     * @param blocks zweidimensionales Array aus Vertices, legt Größe des Graphen fest und wird mit Vertices gefüllt
     * @return Erstellter Graph
     */
    public Graph createRandomGraph(Vertex[][] blocks) {
        Graph graph = new Graph();

        //-------------Bereite Schleife vor-----------
        Random random = new Random();
        int currX = blocks.length / 2;
        int currY = blocks[0].length / 2;
        int unvisited = blocks.length * blocks[0].length - 1;
        final int vertexCount = blocks.length * blocks[0].length;
        boolean alwaysAddEdge = false;
        data[][] datas = new data[blocks.length][blocks[0].length];
        int timeSinceLastAdd = 0;

        //Fülle Graphen mit Vertices
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                Vertex<Tower> currVertex = new Vertex<>(i + " " + j);
                currVertex.setContent(null);
                graph.addVertex(currVertex);
                blocks[i][j] = currVertex;
                int maxGrad = 8;
                if (i == 0 || i == blocks.length) {
                    maxGrad = -2;
                }
                if (j == 0 || j == blocks[0].length) {
                    maxGrad = -2;
                }
                maxGrad = (maxGrad == 4) ? 3 : maxGrad;
                datas[i][j] = new data(currVertex, maxGrad);
            }
        }

        //------------Schleife des Todes und der finsteren Vernichtung----------------

        while (unvisited > 0) {  //Wiederhole, solange nicht alle Vertices verbunden
            data currData = datas[currX][currY];
            currData.timesVisited++;

            //Erstelle immer eine Kante zu unentdeckten Knoten, wenn maximal 10% unentdeckt
            if (!alwaysAddEdge) {
                if (((double) unvisited) / ((double) vertexCount) < 0.1) {
                    alwaysAddEdge = true;
                }
            }

            Vertex currVertex = blocks[currX][currY];

            boolean addedEdge = false;
            data smallest = null;
            int smallestX = -1;
            int smallestY = -1;
            //Traversiere über alle potenziellen Nachbarn von currVertex
            //Erstellt Nachbarn (Teilweise Random)
            for (int i = Math.max(0, currX - 1); i < Math.min(blocks.length, currX + 1); i++) {
                for (int j = Math.max(0, currY - 1); j < Math.min(blocks[i].length, currY + 1); j++) {
                    if (i != currX || j != currY) {
                        //Setze Data des Nachbarn in nData
                        data nData = datas[i][j];
                        if (!currData.isNeighbour[i - currX + 1][j - currY + 1]) {    //Überprüfe, ob blocks[i][j] schon ein Nachbar von currVertex ist
                            //Mache blocks[i][j] zu Nachbarn, wenn alwaysAddEdge und blocks[i][j] unentdeckt oder Random in Abhängigkeit zum Verhätlnis von Grad und maximalen Grad oder wenn seit 3 Schleifendurchläufen keine neue Kante hinzugefügt wurde
                            if ((alwaysAddEdge && nData.discovered) || (timeSinceLastAdd >= 3) || (random.nextFloat() >= currData.grad / currData.maxGrad)) {
                                addedEdge = true;
                                timeSinceLastAdd = 0;
                                if (!nData.discovered) {
                                    nData.discovered = true;
                                    unvisited--;
                                }
                                currData.isNeighbour[i - currX + 1][j - currY + 1] = true;
                                nData.isNeighbour[currX - i + 1][currY - j + 1] = true;
                                currData.grad++;
                                nData.grad++;

                                double weight = (i == currX || j == currY) ? 1 : Math.sqrt(2);
                                graph.addEdge(new Edge(currData.content, nData.content, weight));
                                //Finde am seltensten besuchten Nachbarn
                                if (smallest == null || smallest.timesVisited < nData.timesVisited) {
                                    smallest = nData;
                                    smallestX = i;
                                    smallestY = j;
                                }
                            }
                        } else {
                            //Finde am seltensten besuchten Nachbarn
                            if (smallest == null || smallest.timesVisited < nData.timesVisited) {
                                smallest = nData;
                                smallestX = i;
                                smallestY = j;
                            }
                        }
                    }
                }
            }

            //Bereite nächsten Schleifendurchlauf vor
            if (!addedEdge) {
                timeSinceLastAdd++;
            }
            currX = smallestX;
            currY = smallestY;
        }
        return graph;
    }

    private class data {
        boolean discovered;
        int timesVisited, grad, maxGrad;
        boolean[][] isNeighbour;
        Vertex content;

        public data(Vertex content, int maxGrad) {
            discovered = false;
            timesVisited = 0;
            grad = 0;
            this.maxGrad = maxGrad;
            isNeighbour = new boolean[3][3];
            this.content = content;
        }
    }
}
