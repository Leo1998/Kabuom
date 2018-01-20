package entity;

import entity.model.Entity;
import entity.model.MoveEntity;
import entity.model.Partisan;
import entity.movement.MoveGroup;
import projectile.Projectile;
import utility.Vector2;
import world.Block;
import world.World;

import java.util.ArrayList;

import static utility.Constants.dpsMultiplier;
import static utility.Utility.getDist;
import static utility.Utility.random;
import static utility.Utility.saveAdd;

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
            boolean moved = false;

            //Movement
            if(entity instanceof MoveEntity){
                //Movement, set moved = true if entity moved
                MoveEntity moveEntity = (MoveEntity) entity;

                if(moveEntity.getGroup() != null){
                    MoveGroup group = moveEntity.getGroup();
                    if(getDist(moveEntity,group) < 2){
                        // Move according to Group
                    } else {
                        if(group.removeMember()){
                            groups.remove(group);
                            moveEntity.setGroup(null);
                        }
                    }
                } else {
                    // Find/Create group for Entity to join
                }
            }

            //Attack
            if(!moved){
                if(entity.addAttackCooldown(dt)){
                    //Find Target
                    if(entity instanceof MoveEntity && ((MoveEntity)entity).getGroup().getTarget() != null && getDist(((MoveEntity)entity).getGroup().getTarget(),entity) < entity.entityType.range){
                        entity.setTarget(((MoveEntity) entity).getGroup().getTarget());
                    }else{
                        if(entity.getTarget() == null || getDist(entity,entity.getTarget()) > entity.entityType.range) {
                            entity.setTarget(findTarget(entity));
                        }
                    }

                    //Attack Target
                    if(entity.getTarget() != null) {
                        if (entity.entityType.isRanged()) {
                            shotEntity(entity,entity.getTarget());
                        } else {
                            attackEntity(entity,entity.getTarget());
                        }
                    }
                }
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
     *                  Methods for Attacking
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private Entity findTarget(Entity source){
        return findTarget(source,source.entityType.range);
    }

    private Entity findTarget(Partisan source, float range){
        float x = source.getX(), y = source.getY();
        Entity closest = null;
        for (int i = Math.max(0,(int) (Math.floor(x - range))); i < Math.min(world.getBlocks().length,Math.ceil(x + range) + 1); i++) {
            for (int j = Math.max(0, (int) (Math.floor(y - range))); j < Math.min(world.getBlocks()[i].length, Math.ceil(y + range) + 1); j++) {
                /* Comment because Block does not yet support Entities
                for(Entity entity : nodeMap[i][j].block.getEnemies()){
                    if(!source.allyOf(entity)){
                        if (closest == null || getDist(source, entity) < getDist(source, closest)) {
                            closest = entity;
                        }
                    }
                }*/
            }
        }

        if(closest != null && getDist(source,closest) > range){
            closest = null;
        }
        return closest;
    }

    private void attackEntity(Entity source, Entity target){
        if(random.nextFloat() > source.entityType.accuracy) {
            target.addHp(-source.entityType.attack);
        }
    }

    private void shotEntity(Entity source, Entity target){
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
     *             Methods for Movement/Pathfinding
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
