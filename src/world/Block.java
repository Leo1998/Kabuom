package world;

import entity.model.Entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by 204g10 on 15.06.2016.
 */
public class Block implements Iterable<Entity> {

    //private Tower tower;
    //private ArrayList<Enemy> enemies;
    private Entity tower;
    private LinkedList<Entity> entities;

    public Block() {
        this.tower = null;
        //enemies = new ArrayList<>();
        entities = new LinkedList<>();
    }

    /*
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
    */

    public Entity getTower(){
        return tower;
    }

    public void setTower(Entity entity){
        tower = entity;
        if(!entities.contains(tower)){
            entities.add(tower);
        }
    }

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public LinkedList<Entity> getEntities() {
        return entities;
    }

    public BlockIterator iterator(){
        return new BlockIterator(this);
    }

    private class BlockIterator implements Iterator<Entity> {
        private Iterator<Entity> iterator;
        private Block block;
        private Entity next;

        private BlockIterator(Block block) {
            this.iterator = entities.iterator();
            this.block = block;
            cleanUp();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Entity next() {
            if(next != null) {
                Entity result = next;
                cleanUp();
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            iterator.remove();
            cleanUp();
        }

        private void cleanUp(){
            if(iterator.hasNext()) {
                next = iterator.next();
                while (next != null && (next.getBlock() != block || next.getHp() <= 0)){
                    iterator.remove();
                    if(iterator.hasNext()) {
                        next = iterator.next();
                    } else {
                        next = null;
                    }
                }
            } else {
                next = null;
            }
        }
    }
}
