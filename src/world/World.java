package world;

import graph.*;
import model.GameObject;

/**
 * Created by Daniel on 09.06.2016.
 */
public class World {
    private List<GameObject> objects;
    private Graph graph;
    private Vertex[][] blocks;
    private int width,height,difficulty;
    private float timePassed;

    public World(int width, int height, int difficulty) {
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
        timePassed = 0;
    }

    public void update(float dt){

    }
}
