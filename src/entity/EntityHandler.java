package entity;

import entity.model.Entity;
import entity.model.EntityType;
import entity.model.MoveEntity;
import entity.model.Partisan;
import entity.movement.MoveGroup;
import entity.movement.Step;
import model.Position;
import projectile.Projectile;
import projectile.ProjectileType;
import utility.Constants;
import utility.Vector2;
import world.Block;
import world.World;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import static utility.Constants.dpsMultiplier;
import static utility.Utility.*;

public class EntityHandler {

    private Node[][] nodeMap;
    private int index, pathUpdate;
    private Entity mainTower;
    private World world;
    private LinkedList<MoveGroup> groups;

    /**
     * Constructor of EntityHandler
     * @param world current World
     * @param mainTower the Main Tower
     */
    public EntityHandler(World world, Entity mainTower){
        this.world = world;
        this.mainTower = mainTower;
        this.index = Integer.MIN_VALUE;
        Entity.setEntityHandler(this);
        createNodeMap(world.getBlocks());
        groups = new LinkedList<>();
        pathUpdate = 0;
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Public Methods
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    /**
     * Lets entities move, attack and removes them if dead
     * @param entities List containing all entities
     * @param dt time since last frame
     * @return smallest, positive wave of all entities
     */
    public int handleEntities(LinkedList<Entity> entities, float dt){
        int minWave = Integer.MAX_VALUE;

        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()){
            Entity entity = iterator.next();

            entity.updateEffects(dt);

            if(entity.addAttackCooldown(dt)){
                attack(entity);
            }

            if(entity instanceof MoveEntity){
                move((MoveEntity) entity, dt);
            }



            //Remove if dead
            if(entity.getHp() <= 0){
                iterator.remove();
                if(entity.getBlock().getTower() == entity){
                    entity.getBlock().setTower(null);
                    updateNode(Math.round(entity.getX()),Math.round(entity.getY()),entity.getBlock());
                }
                world.removeEntity(entity);
            } else {
                if(entity.wave >= 0 && entity.wave < minWave){
                    minWave = entity.wave;
                }
            }
        }

        if(groups.size() > 0) {
            pathUpdate = (pathUpdate + 1) % groups.size();
            Iterator<MoveGroup> groupIterator = groups.iterator();
            for (int i = 0; groupIterator.hasNext(); i++) {
                MoveGroup group = groupIterator.next();

                if (group.hasMembers()) {
                    if (group.getSteps() == null || group.getSteps().isEmpty() || i == pathUpdate) {
                        if (group.isEnemy()) {
                            group.setSteps(findPath(group, mainTower));
                        } else {
                            Position target = findTarget(group, -1);
                            if (target != null) {
                                group.setSteps(findPath(group, target));
                            } else {
                                group.setSteps(findPath(group, mainTower));
                            }
                        }
                    }

                    if (group.getSteps() != null && !group.getSteps().isEmpty()) {
                        move(group, dt);
                    }
                } else {
                    groupIterator.remove();
                }

                group.reset();
            }
        }

        if(minWave == Integer.MAX_VALUE)
            minWave = -1;

        return minWave;
    }

    /**
     * Adds damage as weight to the node at the given position
     * @param damage weight to be added
     * @param position position of the node
     */
    public void addDamage(float damage, Position position){
        if(damage > 0) {
            int x = Math.round(position.getX()), y = Math.round(position.getY());

            if (x >= 0 && x < nodeMap.length && y >= 0 && y < nodeMap[x].length) {
                nodeMap[x][y].damage += damage;
            }
        }
    }

    /**
     * Should be called at the beginning of a new Wave.
     * Halves damage for all nodes and restores 10% HP to all towers
     */
    public void startWave(){
        for(Node[] nodes:nodeMap){
            for(Node node:nodes){
                node.damage /= 2;

                if(node.block.getTower() != null){
                    node.block.getTower().addHp(node.block.getTower().getObjectType().getMaxHP()/10);
                }
            }
        }
    }

    public void newTower(int x, int y){
        updateNode(x,y, nodeMap[x][y].block);
    }

    public LinkedList<MoveGroup> getGroups() {
        return groups;
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Attacking
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */
    private void attack(Entity entity){

        if (entity.getTarget() == null || getDist(entity, entity.getTarget()) > entity.entityType.range || entity.getTarget().getHp() <= 0) {
            if(entity instanceof MoveEntity){
                MoveGroup group = ((MoveEntity) entity).getGroup();
                if(group != null) {
                    entity.setTarget(findTarget(group, entity.entityType.speed*2));
                }else{
                    entity.setTarget(findTarget(entity, entity.entityType.speed*2));
                }
            }else {
                entity.setTarget(findTarget(entity));
            }
        }

        //Attack target if target exists and is in range
        if (entity.getTarget() != null && getDist(entity,entity.getTarget()) <= entity.entityType.range + entity.getTarget().entityType.getRadius()) {
            if (entity.entityType.isRanged()) {
                if(entity.entityType.projectile instanceof ProjectileType){
                    Vector2 aiming = aim(entity, entity.getTarget());

                    for (int i = 0; i < entity.entityType.attack; i++) {
                        Vector2 copy = aiming.clone();

                        Projectile projectile = createProjectile(entity, copy);

                        world.spawnProjectile(projectile);
                    }
                } else {
                    for(int i = 0; i < entity.entityType.attack; i++){
                        Entity spawn = createEntity(entity);

                        world.spawnEntity(spawn, Math.round(entity.getX()), Math.round(entity.getY()));
                    }
                }
            } else {
                if(random.nextFloat() < entity.entityType.accuracy) {
                    entity.getTarget().addHp(-entity.entityType.attack);
                }
            }
        }
    }

    private Entity findTarget(Entity source){
        return findTarget(source,source.entityType.range + 3);
    }

    private Entity findTarget(Partisan source, float range){
        int x = Math.round(source.getX()), y = Math.round(source.getY());

        Entity closest = null;

        for(int i = Math.max(0, x - (int) Math.ceil(range)); i < Math.min(nodeMap.length, x + Math.floor(range)); i++){
            for(int j = Math.max(0, y - (int) Math.ceil(range)); j < Math.min(nodeMap[x].length, y + Math.floor(range)); j++){
                for(Entity entity:nodeMap[i][j].block){
                    if(!source.allyOf(entity)){
                        if (closest == null || getDist(source, entity) - entity.entityType.getRadius() < getDist(source, closest) - closest.entityType.getRadius()) {
                            closest = entity;
                        }
                    }
                }
            }
        }

        if(closest != null && getDist(source,closest) > closest.entityType.getRadius() + range){
            closest = null;
        }

        return closest;
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

            float s = ((ProjectileType)source.entityType.projectile).speed;

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
            float aimX = source.getX() - target.getY();
            float aimY = source.getY() - target.getY();

            vec = new Vector2(aimX,aimY);
        }

        vec.normalize();

        return vec;
    }

    private Projectile createProjectile(Entity source, Vector2 vec){
        if (source.entityType.accuracy > 0) {
            vec.rotate((float) ((random.nextDouble() - 0.5) * source.entityType.accuracy));
        }

        Projectile p = new Projectile((ProjectileType)source.entityType.projectile, source.getLevel(), source.getX(), source.getY(), vec);

        p.setX(p.getX() + p.getDir().getCoords()[0] * source.getObjectType().getRadius());
        p.setY(p.getY() + p.getDir().getCoords()[1] * source.getObjectType().getRadius());

        return p;
    }

    private Entity createEntity(Entity source){
        int wave = (random.nextFloat() > source.entityType.accuracy) ? source.wave : (source.wave > 0) ? 0 : 1;

        return new Entity((EntityType)source.entityType.projectile,source.getLevel(),source.getX(),source.getY(),wave,source.getBlock());
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Movement
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private void move(MoveEntity entity, float dt){

        // If entity has no group: find/create group for entity to join
        if(entity.getGroup() == null){
            for(MoveGroup moveGroup:groups){
                if(moveGroup.allyOf(entity)) {
                    if (entity.getGroup() == null) {
                        if (moveGroup.allyOf(entity) && getDist(entity, moveGroup) < entity.entityType.speed * 2) {
                            entity.setGroup(moveGroup);
                        }
                    } else {
                        if(getDist(entity,moveGroup) < getDist(entity,entity.getGroup())){
                            entity.setGroup(moveGroup);
                        }
                    }
                }
            }

            if(entity.getGroup() == null){
                entity.setGroup(new MoveGroup(entity.isEnemy(),entity.getX(),entity.getY()));
                groups.add(entity.getGroup());
            }
        }

        // Move entity towards group or target, depending on distance
        Entity collidingEntity = findCollidingEntity(entity, world.getBlocks());
        if(collidingEntity == null){
            MoveGroup group = entity.getGroup();
            float dist = getDist(group,entity);

            if(dist < entity.entityType.speed){
                entity.getGroup().addSpeed((entity.entityType.speed*2 - dist)/4, entity.entityType.speed);
                if(entity.getTarget() != null && getDist(entity.getTarget(),entity) > entity.entityType.range/2){
                        goTo(entity,entity.getTarget(),dt);
                } else {
                    Vector2 vec = entity.getMovement();
                    vec.multiply(32);

                    goTo(entity,vec,dt);
                }
            } else {
                entity.getGroup().addSpeed((entity.entityType.speed - dist)*2, Float.MAX_VALUE);
                goTo(entity,group,dt);
            }
        }else{
            entity.setTarget(collidingEntity);
            if(!entity.getMovement().nullVector()) {
                entity.setMovement(new Vector2(0, 0));
            }
        }

        // If entity moved to far away from group: remove from group. Otherwise register at group
        if(getDist(entity,entity.getGroup()) < entity.entityType.speed*2){
            entity.getGroup().register();
        } else {
            entity.setGroup(null);
        }
    }

    private void move(MoveGroup group, float dt){
        float totalDist, movableDist;
        Stack<Step> steps = group.getSteps();
        movableDist = group.getSpeed()*dt;
        Step step = steps.peek();
        totalDist = getDist(step,group);

        while (totalDist < movableDist && !steps.isEmpty()){
            steps.pop();
            if(!steps.isEmpty()){
                step = steps.peek();
                totalDist = getDist(step,group);
            }
        }

        float q = movableDist/totalDist;

        group.setX(group.getX() + (step.getX() - group.getX())*q);
        group.setY(group.getY() + (step.getY() - group.getY())*q);
    }

    private void goTo(MoveEntity entity, Vector2 vec, float dt){
        if(!vec.nullVector()){
            vec.normalize();
            vec.rotate(random.nextFloat()*dt*2 - dt);
            vec.multiply(dt*entity.entityType.speed);

            float x = Math.max(-0.4f,Math.min(nodeMap.length - 0.6f,entity.getX() + vec.getCoords()[0])), y = Math.max(-0.4f,Math.min(nodeMap[Math.round(x)].length - 0.6f,entity.getY() + vec.getCoords()[1]));

            if (Math.round(entity.getX()) != Math.round(x) || Math.round(entity.getY()) != Math.round(y)) {
                entity.setBlock(nodeMap[Math.round(x)][Math.round(y)].block);
                nodeMap[Math.round(x)][Math.round(y)].block.addEntity(entity);
            }

            entity.setX(x);
            entity.setY(y);

            vec.multiply(0.09f);
            entity.setMovement(vec);
        } else {
            if(!entity.getMovement().nullVector()){
                entity.setMovement(new Vector2(0,0));
            }
        }
    }

    private void goTo(MoveEntity entity, Position position, float dt) {
        float dist = getDist(entity,position);
        if(dist > 0) {
            float q = (entity.entityType.speed * dt) / getDist(entity, position);

            float x = entity.getX() + ((position.getX() - entity.getX()) * q);
            float y = entity.getY() + ((position.getY() - entity.getY()) * q);

            if (Math.round(entity.getX()) != Math.round(x) || Math.round(entity.getY()) != Math.round(y)) {
                entity.setBlock(nodeMap[Math.round(x)][Math.round(y)].block);
                nodeMap[Math.round(x)][Math.round(y)].block.addEntity(entity);
            }

            Vector2 movement = new Vector2((x - entity.getX()) / dt, (y - entity.getY()) / dt);
            movement.multiply(0.09f);
            entity.setMovement(movement);
            entity.setX(x);
            entity.setY(y);
        } else {
            if(!entity.getMovement().nullVector()){
                entity.setMovement(new Vector2(0,0));
            }
        }
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Pathfinding
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private Stack<Step> findPath(Partisan source, Position target){
        boolean evade = source.isEnemy();
        boolean collision = !evade;

        if(aStar(source,target,evade,collision) || (collision && aStar(source,target,evade,false))){
            return backtracking(target,source);
        } else {
            return null;
        }
    }

    private boolean aStar(Position start, Position target, boolean evade, boolean collision){
        int startX = Math.round(start.getX()), startY = Math.round(start.getY()), targetX = Math.round(target.getX()), targetY = Math.round(target.getY());

        if (startX < 0 || startY < 0 || targetX < 0 || targetY < 0) return false;

        if (startX >= nodeMap.length || startY >= nodeMap[0].length || targetX >= nodeMap.length || targetY >= nodeMap[0].length) return false;

        if(index == Integer.MAX_VALUE){
            resetIndex();
        }else {
            index++;
        }

        Node sNode = nodeMap[startX][startY];
        sNode.index = index;
        sNode.fromStart = 0;
        sNode.preX = -1;
        sNode.preY = -1;
        sNode.visited = true;

        int currX = startX, currY = startY;
        boolean unreachable = false;

        while (!unreachable && !(currX == targetX && currY == targetY)) {
            nodeMap[currX][currY].visited = true;
            updateNeighbours(currX, currY, evade, collision);
            int[] newCurr = getMinNode();
            if (newCurr == null) {
                unreachable = true;
            } else {
                currX = newCurr[0];
                currY = newCurr[1];
            }
        }

        return !unreachable;
    }

    private Stack<Step> backtracking(Position start, Position end){
        Stack<Step> result = new Stack<>();
        int currX = Math.round(start.getX()), currY = Math.round(start.getY()), endX = Math.round(end.getX()), endY = Math.round(end.getY());
        while (!(currX == endX && currY == endY)) {
            int newX = nodeMap[currX][currY].preX;
            int newY = nodeMap[currX][currY].preY;
            result.add(new Step(currX, currY));
            currX = newX;
            currY = newY;
        }

        return result;
    }

    private void updateNeighbours(int x, int y, boolean evade, boolean collision) {
        if (x >= 0 && y >= 0 && x < nodeMap.length && y < nodeMap[x].length) {
            for (int i = Math.max(0, x - 1); i < Math.min(x + 2, nodeMap.length); i++) {
                for (int j = Math.max(0, y - 1); j < Math.min(y + 2, nodeMap[i].length); j++) {
                    updateNeighbour(x, y, i, j, evade, collision);
                }
            }
        }
    }

    private void updateNeighbour(int srcX, int srcY, int xPos, int yPos, boolean evade, boolean collision) {
        Node source = nodeMap[srcX][srcY], current = nodeMap[xPos][yPos];

        if(!collision || current.block.getTower() != null) {
            float addDist = (srcX == xPos || srcY == yPos) ? 1 : (float) Math.sqrt(2);
            if(evade) {
                addDist = addo(addDist, ((current.dpsInRange + source.dpsInRange) / 2) * Constants.dpsMultiplier);
                addDist = addo(addDist, (current.damage + source.damage) / 2);
            }
            addDist = addo(addDist, source.fromStart);

            if (index != current.index || current.fromStart > addDist) {
                current.visited = false;
                current.index = index;
                current.fromStart = addDist;
                current.preX = srcX;
                current.preY = srcY;
            }
        }

    }

    private int[] getMinNode() {
        int minX = -1, minY = -1;
        for (int i = 0; i < nodeMap.length; i++) {
            for (int j = 0; j < nodeMap[i].length; j++) {
                if (nodeMap[i][j].index == index && !nodeMap[i][j].visited && (minX == -1 || nodeMap[i][j].getDistance() < nodeMap[minX][minY].getDistance())) {
                    minX = i;
                    minY = j;
                }
            }
        }
        if (minX == -1 || nodeMap[minX][minY].fromStart == Float.MAX_VALUE || nodeMap[minX][minY].index != index) {
            return null;
        } else {
            return new int[]{minX, minY};
        }
    }

    private void resetIndex(){
        index = Integer.MIN_VALUE;
        for(Node[] nodes : nodeMap) {
            for (Node node : nodes) {
                node.index = Integer.MIN_VALUE;
            }
        }
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                          NodeMap
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private void updateNodesInRange(int xPos, int yPos) {
        if (nodeMap[xPos][yPos].dps > 0) {
            float dps = nodeMap[xPos][yPos].dps;
            int attackRange = Math.round(nodeMap[xPos][yPos].attackRange);

            for (int i = Math.max(0, xPos - attackRange); i < Math.min(nodeMap.length, xPos + attackRange + 1); i++) {
                for (int j = Math.max(0, yPos - attackRange); j < Math.min(nodeMap[i].length, yPos + attackRange + 1); j++) {
                    float distance = getDist(xPos,yPos,i,j);
                    if (distance <= attackRange) {
                        nodeMap[i][j].dpsInRange = addo(nodeMap[i][j].dpsInRange,dps);
                    }
                }
            }
        }
    }

    private void updateAllNodes(boolean resetDps) {
        if(resetDps){
            for(Node[] nodes:nodeMap){
                for(Node node:nodes){
                    node.dpsInRange = 0;
                }
            }
        }

        for (int i = 0; i < nodeMap.length; i++) {
            for (int j = 0; j < nodeMap.length; j++) {
                updateNodesInRange(i, j);
            }
        }
    }

    private void createNodeMap(Block[][] blocks) {
        nodeMap = new Node[blocks.length][blocks[0].length];

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                nodeMap[i][j] = new Node(blocks[i][j], i, j, mainTower.getX(), mainTower.getY());
            }
        }

        updateAllNodes(false);
    }

    private void updateNode(int x, int y, Block block){
        if(x >= 0 && x < nodeMap.length && y >= 0 && y < nodeMap[x].length){
            Node node = nodeMap[x][y];
            node.dps *= -1;
            updateNodesInRange(x,y);
            node.getFromBlock(block);
            updateNodesInRange(x,y);
        }
    }

    private class Node {
        private float dps, attackRange, dpsInRange, fromStart, damage;
        private final float toEnd;
        private int preX, preY, index;
        private boolean visited;
        private Block block;

        private Node(Block block, int xPos, int yPos, float endX, float endY) {
            this.preX = -1;
            this.preY = -1;
            damage = 0;
            this.block = block;
            this.toEnd = getDist(xPos,yPos,endX,endY);
            this.fromStart = Float.MAX_VALUE;
            getFromEntity(block.getTower());
            dpsInRange = 0;
            visited = false;
            index = Integer.MIN_VALUE;
        }

        /**
         * Setzt dps, attackRange abhängig des übergebenen Turmes
         */
        private void getFromEntity(Entity entity) {
            dps = 0;
            attackRange = 0;
            if (entity != null && !entity.isEnemy() && entity.entityType.attack > 0) {
                if(entity.entityType.projectile instanceof ProjectileType) {
                    dps = ((ProjectileType)entity.entityType.projectile).impactDamage * entity.entityType.attack / entity.entityType.frequency * dpsMultiplier;
                    attackRange = entity.entityType.range;
                }else if(!entity.entityType.isRanged()){
                    dps = entity.entityType.attack / entity.entityType.frequency * dpsMultiplier;
                    attackRange = entity.entityType.range;
                }
            }
        }

        private void getFromBlock(Block block){
            this.block = block;
            getFromEntity(block.getTower());
            dpsInRange = 0;
        }

        private float getDistance() {
            return addo(fromStart,toEnd);
        }
    }
}
