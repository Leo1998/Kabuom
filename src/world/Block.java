package world;

import enemy.Enemy;
import tower.Tower;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by 204g10 on 15.06.2016.
 */
public class Block implements Iterable<Enemy> {

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

    public void addEnemy(Enemy enemy){
        enemies.add(enemy);
    }

    public boolean removeEnemy(Enemy enemy){
        return enemies.remove(enemy);
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public BlockIterator iterator(){
        return new BlockIterator(enemies,this);
    }

    private class BlockIterator implements Iterator<Enemy> {
        private ArrayList<Enemy> enemies;
        private Block block;
        private int index;


        private BlockIterator(ArrayList<Enemy> enemies, Block block) {
            this.enemies = enemies;
            this.block = block;
            index = 0;
            cleanUp();
        }

        @Override
        public boolean hasNext() {
            return index < enemies.size();
        }

        @Override
        public Enemy next() {
            if(hasNext()){
                Enemy result = enemies.get(index);
                index++;
                cleanUp();
                return result;
            }else{
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            if(hasNext()){
                enemies.remove(index);
            }else{
                throw new IllegalStateException();
            }
        }

        private void cleanUp(){
            while (hasNext() && (enemies.get(index).getHp() <= 0 || enemies.get(index).getBlock() != block)){
                enemies.remove(index);
            }
        }
    }
}
