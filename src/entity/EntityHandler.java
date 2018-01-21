package entity;

import entity.model.Entity;
import entity.model.MoveEntity;
import entity.model.Partisan;
import entity.movement.MoveGroup;
import model.Position;
import projectile.Projectile;
import utility.Vector2;
import world.Block;
import world.World;

import java.util.ArrayList;

import static utility.Constants.dpsMultiplier;
import static utility.Utility.*;

public class EntityHandler {

    private Node[][] nodeMap;
    private int index;
    private final int mainX,mainY;
    private World world;

    private ArrayList<MoveGroup> groups;

    public EntityHandler(World world, int mainX, int mainY){
        this.world = world;
        this.mainX = mainX;
        this.mainY = mainY;
    }

    public int handleEntities(ArrayList<Entity> entities, float dt){
        int minWave = 0;

        for(int i = 0; i < entities.size(); i++){
            Entity entity = entities.get(i);

            entity.updateEffects(dt);

            if(entity.addAttackCooldown(dt)){
                attack(entity);
            }

            if(entity instanceof MoveEntity){
                move((MoveEntity) entity, dt);
            }

            //Remove if dead
            if(entity.getHp() <= 0){
                //Remove Entity
            }
        }

        for(MoveGroup group:groups){
            if(group.getSteps().isEmpty()){
                // Pathfinding
            } else {
                // Move Group
            }
        }

        return minWave;
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Attacking
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    private void attack(Entity entity){
        //*
        if (entity.getTarget() == null || getDist(entity, entity.getTarget()) > entity.entityType.range) {
            if(entity instanceof MoveEntity){
                entity.setTarget(findTarget(entity,-1));
            }else {
                entity.setTarget(findTarget(entity));
            }
        }

        if (entity.getTarget() != null && getDist(entity,entity.getTarget()) < entity.entityType.range) {
            if (entity.entityType.isRanged()) {
                shootEntity(entity, entity.getTarget());
            } else {
                attackEntity(entity, entity.getTarget());
            }
        }//*/
    }

    private Entity findTarget(Entity source){
        return findTarget(source,source.entityType.range);
    }

    private Entity findTarget(Partisan source, float range){
        int x = Math.round(source.getX()), y = Math.round(source.getY());

        Entity closest = null;

        int maxDist = Math.min(Math.round(range),Math.max(Math.max(x, world.getBlocks().length-x),Math.max(y, world.getBlocks()[x].length-y)));
        for(int dist = 0; closest == null && dist <= maxDist; dist++) {
            for (int i = Math.max(0, x - dist); i < Math.min(world.getBlocks().length, x + dist + 1); i++) {
                boolean full = (i == x - dist || i == x + dist);
                for (int j = full ? Math.max(0, y - dist) : (y - dist < 0) ? y + dist : y - dist; j < Math.min(world.getBlocks()[i].length, y + dist + 1); j += full ? 1 : 2 * dist) {
                    Block block = world.getBlocks()[i][j];
                    /*
                    for(Entity entity:block){
                        if(closest == null || getDist(entity,source) < getDist(entity,closest)){
                            closest = entity;
                        }
                    }//*/
                }
            }
        }

        return closest;
    }


    private void attackEntity(Entity source, Entity target){
        if(random.nextFloat() > source.entityType.accuracy) {
            target.addHp(-source.entityType.attack);
        }
    }

    private void shootEntity(Entity source, Entity target){
        Vector2 aiming = aim(source, target);

        for (int i = 0; i < source.entityType.attack; i++) {
            Vector2 copy = aiming.clone();

            Projectile projectile = createProjectile(source, copy);

            world.spawnProjectile(projectile);
        }
    }

    private Vector2 aim(Entity source, Entity target){
        Vector2 vec;
        if(target instanceof MoveEntity) {
            float sX = source.getX();
            float sY = source.getY();

            float tX = target.getX();
            float tY = target.getY();

            MoveEntity moveTarget = (MoveEntity)target;

            float tmx = moveTarget.getMovement().getCoords()[0];
            float tmy = moveTarget.getMovement().getCoords()[1];
            float s = source.entityType.projectileType.speed;

            float a = tmx * tmx + tmy * tmy - s * s;
            float b = 2 * ((tX - sX) * tmx + (tY - sY) * tmy);
            float c = (tX - sX) * (tX - sX) + (tY - sY) * (tY - sY);
            float d = b * b - 4 * a * c;

            if (d >= 0) {
                float sqrtD = (float) Math.sqrt(d);
                float t1 = (-b + sqrtD) / (2 * a);
                float t2 = (-b - sqrtD) / (2 * a);

                float aimX = tX - sX + Math.min(t1, t2) * tmx;
                float aimY = tY - sY + Math.min(t1, t2) * tmy;

                vec = new Vector2(aimX, aimY);

            } else {
                vec = new Vector2(tX - sX, tY - sY);
            }
        }else{
            float aimX = target.getX() - source.getY();
            float aimY = target.getY() - source.getY();

            vec = new Vector2(aimX,aimY);
        }

        vec.normalize();

        return vec;
    }

    private Projectile createProjectile(Entity source, Vector2 vec){
        if (source.entityType.accuracy > 0) {
            vec.rotate((float) ((random.nextDouble() - 0.5) * source.entityType.accuracy));
        }

        Projectile p = new Projectile(source.entityType.projectileType, source.getLevel(), source.getX(), source.getY(), vec);

        p.setX(p.getX() + p.getDir().getCoords()[0] * source.getObjectType().getRadius());
        p.setY(p.getY() + p.getDir().getCoords()[1] * source.getObjectType().getRadius());

        return p;
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Movement
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private void move(MoveEntity entity, float dt){
        if(entity.getGroup() != null){
            Entity collidingEntity = findCollidingEntity(entity, world.getBlocks());
            if(collidingEntity != null){
                MoveGroup group = entity.getGroup();
                float dist = getDist(group,entity);

                if(dist > 2){
                    goTo(entity,group,dt);
                } else {

                }
            }else{
                entity.setTarget(collidingEntity);
                if(!entity.getMovement().nullVector()) {
                    entity.setMovement(new Vector2(0, 0));
                }
            }
        }else{
            if(!entity.getMovement().nullVector()) {
                entity.setMovement(new Vector2(0, 0));
            }
        }
    }

    private void goTo(MoveEntity entity, Position position, float dt) {
        float q = (entity.entityType.speed*dt) / getDist(entity,position);

        float x = entity.getX() + ((position.getX() - entity.getX()) * q);
        float y = entity.getY() + ((position.getY() - entity.getY()) * q);

        if(Math.round(entity.getX()) != Math.round(x) || Math.round(entity.getY()) != Math.round(y)) {
            //nodeMap[Math.round(entity.getX())][Math.round(entity.getY())].block.removeEnemy(enemy);
            entity.setBlock(nodeMap[Math.round(x)][Math.round(y)].block);
            //nodeMap[Math.round(x)][Math.round(y)].block.addEnemy(entity);
        }

        Vector2 movement = new Vector2((x - entity.getX()) / dt, (y - entity.getY()) / dt);
        movement.multiply(0.09f);
        entity.setMovement(movement);
        entity.setX(x);
        entity.setY(y);
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Pathfinding
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    /*
     * Internal Class Node, needed for Pathfinding
     */
    private class Node {
        private float dps, attackRange, dpsInRange, fromStart, damage;
        private final float toEnd;
        private int preX, preY, index;
        private boolean visited;
        private Block block;

        public Node(Block block, int xPos, int yPos, int endX, int endY) {
            this.preX = -1;
            this.preY = -1;
            damage = 0;
            this.block = block;
            int minDif = Math.min(Math.max(xPos, endX) - Math.min(xPos, endX), Math.max(yPos, endY) - Math.min(yPos, endY));
            int maxDif = Math.max(Math.max(xPos, endX) - Math.min(xPos, endX), Math.max(yPos, endY) - Math.min(yPos, endY));
            this.toEnd = (float) (Math.sqrt(2) * minDif) + maxDif - minDif;
            this.fromStart = Float.MAX_VALUE;
            //getFromEntity(block.getTower());
            dpsInRange = 0;
            visited = false;
            index = Integer.MIN_VALUE;
        }

        /**
         * Setzt dps, attackRange abhängig des übergebenen Turmes
         */
        private void getFromEntity(Entity entity) {
            if (entity != null && !entity.isEnemy() && entity.entityType.attack > 0) {
                if(entity.entityType.projectileType != null) {
                    dps = ((entity.entityType.projectileType.impactDamage * entity.entityType.attack) / entity.entityType.frequency) * dpsMultiplier;
                }else{
                    dps = (entity.entityType.attack / entity.entityType.frequency) * dpsMultiplier;
                }
                attackRange = entity.entityType.range;
            } else {
                dps = 0;
                attackRange = 0;
            }
        }

        private void getFromBlock(Block block){
            this.block = block;
            //getFromEntity(block.getTower());
            dpsInRange = 0;
        }

        private float getDistance() {
            return saveAdd(fromStart,toEnd);
        }
    }
}
