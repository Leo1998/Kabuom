package world;

import graph.Vertex;
import tower.Tower;

/**
 * Created by 204g10 on 15.06.2016.
 */
public class Block {

    private Vertex vertex;
    private Tower tower;

    public Block(Vertex vertex, Tower tower){
        this.vertex = vertex;
        this.tower = tower;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public Tower getTower() {
        return tower;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }
}
