package world;

import entity.model.Entity;
import entity.model.MoveEntity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by 204g10 on 15.06.2016.
 */
public class Block implements Iterable<Entity> {

    private Entity tower;
    private LinkedList<Entity> entities;
    public final int x,y;

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        this.tower = null;
        entities = new LinkedList<>();
    }

    public Entity getTower(){
        return tower;
    }

    public void addEntity(Entity entity){
        entities.add(entity);

        if(!(entity instanceof MoveEntity)){
            tower = entity;
        }
    }

    public boolean contains(Entity entity){
        return entities.contains(entity);
    }

    public int countEntites(){
        return entities.size();
    }

    @Override
    public String toString() {
        return "Block{" +
                "tower=" + (tower == null ? "null":tower) +
                ", entities=" + entities +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public BlockIterator iterator(){
        return new BlockIterator();
    }

    private class BlockIterator implements Iterator<Entity> {
        private Iterator<Entity> iterator;
        private Entity next;

        private BlockIterator() {
            this.iterator = entities.iterator();
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

        private void cleanUp(){
            if(iterator.hasNext()) {
                boolean finished = false;
                do {
                    next = iterator.next();
                    if(next == null || next.getHp() <= 0 || next.getBlock() != Block.this){
                        iterator.remove();
                        if(next == tower){
                            tower = null;
                        }
                    } else {
                        finished = true;
                    }
                } while (iterator.hasNext() && !finished);

                if(!finished){
                    next = null;
                }
            }else{
                next = null;
            }
        }
    }
}
