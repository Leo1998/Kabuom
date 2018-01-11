package world;

import enemy.Enemy;
import graph.Vertex;
import tower.Tower;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by 204g10 on 15.06.2016.
 */
public class Block {

    private Tower tower;
    private ArrayList<Enemy> enemies;

    public Block() {
        this.tower = null;
        enemies = new ArrayList<>();
    }

    public Tower getTower() {
        return tower;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void addEnemy(Enemy enemy){
        enemies.add(enemy);
    }

    public boolean removeEnemy(Enemy enemy){
        return enemies.remove(enemy);
    }

    public void test(){
        for(Enemy enemy:enemies){
            System.out.print(enemy.getBlock() == this);
        }
        if(enemies.size() > 0) System.out.println();
    }
}
