package entity;

import entity.model.*;
import model.Position;
import projectile.Projectile;
import projectile.ProjectileType;
import utility.Constants;
import utility.Vector2;
import world.Block;
import world.World;

import java.util.*;

import static utility.Constants.dpsMultiplier;
import static utility.Utility.*;

public class EntityHandler {

    private Node[][] nodeMap;
    private int difficulty, index, updateIndex;
    private Entity mainTower;
    private World world;

    private int ranged;

    /**
     * Constructor of EntityHandler
     * @param world current World
     * @param mainTower the Main Tower
     */
    public EntityHandler(World world, Entity mainTower, int difficulty){
        this.world = world;
        this.mainTower = mainTower;
        this.difficulty = difficulty;
        this.index = Integer.MIN_VALUE;
        updateIndex = 0;
        Entity.setEntityHandler(this);
        createNodeMap(world.getBlocks());
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
        ranged = 0;

        if(entities.size() > 0) {
            updateIndex = (updateIndex + 1) % entities.size();
            boolean[][] findPath = new boolean[nodeMap.length][nodeMap[0].length];

            Iterator<Entity> iterator = entities.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                Entity entity = iterator.next();
                if (entity.getHp() <= 0) {
                    iterator.remove();
                    if (entity.getBlock().getTower() == entity) {
                        entity.getBlock().removeTower();
                        updateNode(Math.round(entity.getX()), Math.round(entity.getY()), entity.getBlock());
                    }
                    world.removeEntity(entity);
                } else {
                    if (entity.isEnemy() && entity.getWave() < minWave) {
                        minWave = entity.getWave();
                    }
                    if(entity.isRanged()){
                        ranged++;
                    }

                    entity.updateEffects(dt);

                    if (entity.addAttackCooldown(dt)) {
                        attack(entity);
                    }

                    if (i == updateIndex) {
                        findPath[Math.round(entity.getX())][Math.round(entity.getY())] = true;
                    }

                    if (entity instanceof MoveEntity) {
                        MoveEntity mEntity = (MoveEntity) entity;
                        if (mEntity.getSteps().isEmpty()) {
                            findPath[Math.round(mEntity.getX())][Math.round(mEntity.getY())] = true;
                        } else {
                            move((MoveEntity) entity, dt);
                        }
                    }
                }
            }

            providePaths(findPath);
        }

        if (minWave == Integer.MAX_VALUE)
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
                nodeMap[x][y].damage += damage/difficulty;
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
                    node.block.getTower().addEffect(EffectType.HEALING);
                }
            }
        }
    }

    /**
     * Should be called when a tower is set or destroyed
     * @param x X-Coordinate of tower
     * @param y Y-Coordinate of tower
     */
    public void newTower(int x, int y){
        updateNode(x,y, nodeMap[x][y].block);
    }

    /**
     * Gives the amount of ranged entities
     * @return amount of ranged entities
     */
    public int getRanged(){
        return ranged;
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Attacking
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private void attack(Entity entity){
        if (provideTarget(entity)) {
            if (entity.isRanged()) {
                if(entity instanceof MoveEntity){
                    MoveEntity mEntity = (MoveEntity) entity;

                    if(mEntity.getSteps().isEmpty() || mEntity.getSteps().peek().stepType != Step.StepType.StayInRange){
                        mEntity.getSteps().push(new Step(Step.StepType.StayInRange, 0, 0));
                    }
                }


                if(entity.getProjectile() instanceof ProjectileType){
                    Vector2 aiming = aim(entity, entity.getTarget());

                    for (int i = 0; i < entity.getAttack(); i++) {
                        Vector2 copy = aiming.clone();

                        Projectile projectile = createProjectile(entity, copy);

                        world.spawnProjectile(projectile);
                    }
                } else {
                    for(int i = 0; i < entity.getAttack(); i++){
                        Entity spawn = createEntity(entity);

                        world.spawnEntity(spawn, Math.round(entity.getX()), Math.round(entity.getY()));
                    }
                }
            } else {
                if(random.nextFloat() > entity.getAccuracy()) {
                    entity.getTarget().addHp(-entity.getAttack(), entity.getName());
                }
            }
        }
    }

    private boolean provideTarget(Entity entity){
        if(entity.getTarget() != null && entity.getTarget().getHp() > 0 && entity.attacks(entity.getTarget()) && isColliding(entity,entity.getTarget()) <= entity.getRange()){
            return true;
        } else {
            if(entity instanceof MoveEntity){
                entity.setTarget(findTarget(entity,-1));
                if(entity.getTarget() == null){
                    if(entity.isEnemy()) {
                        entity.setTarget(mainTower);
                        return entity.attacks(mainTower) && isColliding(entity, mainTower) <= entity.getRange();
                    } else {
                        entity.setHp(-1);
                        return false;
                    }
                } else {
                    return isColliding(entity,entity.getTarget()) <= entity.getRange();
                }
            } else {
                entity.setTarget(findTarget(entity, entity.getRange()+1));
                if(entity.getTarget() == null){
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private Entity findTarget(Entity source, float range){
        int x = Math.round(source.getX()), y = Math.round(source.getY());

        Entity closest = null;

        int maxDist = Math.max(Math.max(x, world.getBlocks().length-x),Math.max(y, world.getBlocks()[x].length-y));
        if(range > 0){
            maxDist = Math.min(Math.round(range),maxDist);
        } else {
            range = Integer.MAX_VALUE;
        }
        for(int dist = 0; closest == null && dist <= maxDist; dist++) {
            for (int i = Math.max(0, x - dist); i < Math.min(world.getBlocks().length, x + dist + 1); i++) {
                boolean full = (i == x - dist || i == x + dist);
                for (int j = full ? Math.max(0, y - dist) : (y - dist < 0) ? y + dist : y - dist; j < Math.min(world.getBlocks()[i].length, y + dist + 1); j += full ? 1 : 2 * dist) {
                    for(Entity entity:nodeMap[i][j].block){
                        if(entity != source && source.attacks(entity)){
                            if(closest == null || getDist(source,entity) < getDist(source,closest)){
                                closest = entity;
                            }
                        }
                    }
                }
            }
        }


        if(closest != null && getDist(source,closest) > closest.getRadius() + range){
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

            float s = ((ProjectileType)source.getProjectile()).speed;

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
            float aimX = target.getX() - source.getX();
            float aimY = target.getY() - source.getY();

            vec = new Vector2(aimX,aimY);
        }

        vec.normalize();

        return vec;
    }

    private Projectile createProjectile(Entity source, Vector2 vec){
        if (source.getAccuracy() > 0) {
            vec.rotate((float) ((random.nextDouble() - 0.5) * source.getAccuracy()));
        }

        Projectile p = new Projectile((ProjectileType)source.getProjectile(), source.getLevel(), source.getX(), source.getY(), vec, source.isEnemy(), source.getName());

        p.setX(p.getX() + p.getDir().getCoords()[0] * source.getRadius());
        p.setY(p.getY() + p.getDir().getCoords()[1] * source.getRadius());

        return p;
    }

    private Entity createEntity(Entity source){
        return new MoveEntity((EntityType)source.getProjectile(),source.getLevel(),source.getX(),source.getY(),source.getWave(),source.getBlock(),source.isEnemy());
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Movement
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private void move(MoveEntity entity, float dt){
        Entity collidingEntity = findCollidingEntity(entity);
        if(collidingEntity == null || collidingEntity.getHp() <= 0) {
            switch (entity.getSteps().peek().stepType) {
                case GoTo:
                    goTo(entity,dt);
                break;
                case MoveInRange:
                    moveInRange(entity,dt);
                    break;
                case StayInRange:
                    stayInRange(entity,dt);
                    break;
            }
        } else {
            entity.setTarget(collidingEntity);
        }
    }

    private void goTo(MoveEntity entity, float dt){
        float totalDist, movableDist;
        Stack<Step> steps = entity.getSteps();
        movableDist = entity.getSpeed() * dt;
        Step step = steps.peek();
        totalDist = getDist(step, entity);
        if(totalDist > 0) {

            while (totalDist < movableDist + Math.min(steps.size() - 1, 3) && !steps.isEmpty() && steps.peek().stepType == Step.StepType.GoTo) {
                steps.pop();
                if (!steps.isEmpty() && steps.peek().stepType == Step.StepType.GoTo) {
                    step = steps.peek();
                    totalDist = getDist(step, entity);
                }
            }

            float q = movableDist / totalDist;

            Vector2 vec = new Vector2((step.getX() - entity.getX()) * q, (step.getY() - entity.getY()) * q);
            moveByVector(entity, vec, dt);
        }
    }

    private void moveInRange(MoveEntity entity, float dt){
        if(entity.getTarget() == null){
            entity.getSteps().pop();
        } else {
            Entity target = entity.getTarget();

            float dist = getDist(entity,target);

            if(dist > entity.getRange()/4 && dist < entity.getRange() + entity.getRadius() + target.getRadius()){
                float q = (entity.getSpeed()*dt)/dist;
                Vector2 vec = new Vector2((entity.getTarget().getX() - entity.getX()) * q, (entity.getTarget().getY() - entity.getY()) * q);

                moveByVector(entity,vec,dt);
            } else {
                entity.getSteps().pop();
            }
        }
    }

    private void stayInRange(MoveEntity entity, float dt){
        if(entity.getTarget() == null){
            entity.getSteps().pop();
        } else {
            Entity target = entity.getTarget();

            float dist = getDist(entity,target);

            if(dist > 0 && dist < 3* entity.getRange()/4){
                float q = (entity.getSpeed() * dt) / dist;

                float xCoord = (entity.getTarget().getX() - entity.getX()) * q;
                float yCoord = (entity.getTarget().getY() - entity.getY()) * q;

                float dist1 = getDist(yCoord, -xCoord, entity.getMovement().getCoords()[0], entity.getMovement().getCoords()[1]);
                float dist2 = getDist(-yCoord, xCoord, entity.getMovement().getCoords()[0], entity.getMovement().getCoords()[1]);

                Vector2 vec;
                if (dist1 < dist2) {
                    vec = new Vector2(yCoord, -xCoord);
                } else {
                    vec = new Vector2(-yCoord, xCoord);
                }

                moveByVector(entity,vec,dt);
            } else if(dist < entity.getRange() + entity.getRadius() + target.getRadius()) {
                entity.getSteps().push(new Step(Step.StepType.MoveInRange,0,0));
            } else {
                entity.getSteps().pop();
            }
        }
    }

    private void moveByVector(MoveEntity entity, Vector2 vector, float dt){
        float targetX = entity.getX() + vector.getCoords()[0], targetY = entity.getY() + vector.getCoords()[1];

        if(targetX < -0.4f || targetX > nodeMap.length - 0.6f || targetY < -0.4f || targetY > nodeMap[Math.round(targetX)].length - 0.6f){
            vector.multiply(-1);
            targetX = entity.getX() + vector.getCoords()[0];
            targetY = entity.getY() + vector.getCoords()[1];
            if(targetX < -0.4f){
                targetX = -0.4f;
            } else if(targetX > nodeMap.length - 0.6f) {
                targetX = nodeMap.length-0.6f;
            }
            if(targetY < -0.4f){
                targetY = 0.4f;
            } else if(targetY > nodeMap[Math.round(targetX)].length - 0.6f){
                targetY = nodeMap[Math.round(targetX)].length - 0.6f;
            }
        }

        if (entity.getBlock() != nodeMap[Math.round(targetX)][Math.round(targetY)].block) {
            entity.setBlock(nodeMap[Math.round(targetX)][Math.round(targetY)].block);
            nodeMap[Math.round(targetX)][Math.round(targetY)].block.addEntity(entity);
        }

        Vector2 movement = new Vector2((targetX - entity.getX()) / dt, (targetY - entity.getY()) / dt);
        movement.multiply(0.09f);
        entity.setMovement(movement);
        entity.setX(targetX);
        entity.setY(targetY);
    }

    private Entity findCollidingEntity(Entity source){
        float x = source.getX(), y = source.getY();
        int radius = (int) source.getRadius();
        Entity closest = null;
        for (int i = Math.max(0,(int) (Math.floor(x - radius))); i < Math.min(nodeMap.length,Math.ceil(x + radius + 1)); i++) {
            for (int j = Math.max(0, (int) (Math.floor(y - radius))); j < Math.min(nodeMap[i].length, Math.ceil(y + radius + 1)); j++) {
                for(Entity entity : nodeMap[i][j].block){
                    if(!source.allyOf(entity)){
                        if (closest == null || isColliding(source,entity) < isColliding(source, closest)) {
                            closest = entity;
                        }
                    }
                }
            }
        }

        if(closest != null && isColliding(source,closest) > 0){
            closest = null;
        }
        return closest;
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Pathfinding
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private void providePaths(boolean[][] findPath){
        for(int i = 0; i < findPath.length; i++){
            for(int j = 0; j < findPath[i].length; j++){
                if(findPath[i][j]){
                    HashMap<Byte, Stack<Step>> paths = new HashMap<>();

                    for(Entity entity : nodeMap[i][j].block){
                        if(entity instanceof MoveEntity){
                            byte key = ((MoveEntity)entity).getKey();

                            if(!paths.containsKey(key)){
                                if(entity.getTarget() != null){
                                    paths.put(key,findPath(entity,entity.getTarget()));
                                } else if(entity.isEnemy()){
                                    paths.put(key,findPath(entity,mainTower));
                                }
                            }

                            if(paths.get(key) != null) {
                                MoveEntity mEntity = (MoveEntity) entity;
                                Stack<Step> steps = (Stack<Step>) paths.get(key).clone();
                                if(!mEntity.getSteps().isEmpty() && mEntity.getSteps().peek().stepType != Step.StepType.GoTo){
                                    steps.push(mEntity.getSteps().peek());
                                }
                                mEntity.setSteps(steps);
                            }
                        }
                    }
                }
            }
        }
    }

    private Stack<Step> findPath(Partisan source, Position target){
        boolean evade = source.isEnemy();
        boolean collision = !evade;

        if(aStar(source,target,evade,collision) || (collision && aStar(source,target,evade,false))){
            return backtracking(target,source);
        } else {
            return new Stack<>();
        }
    }

    private boolean aStar(Position start, Position target, boolean evade, boolean collision){
        int startX = Math.round(start.getX()), startY = Math.round(start.getY()), targetX = Math.round(target.getX()), targetY = Math.round(target.getY());

        if (startX < 0 || startY < 0 || targetX < 0 || targetY < 0) return false;

        if (startX >= nodeMap.length || startY >= nodeMap[0].length || targetX >= nodeMap.length || targetY >= nodeMap[0].length) return false;

        index++;
        if(index == Integer.MAX_VALUE){
            resetIndex();
        }

        Node sNode = nodeMap[startX][startY];
        sNode.index = index;
        sNode.fromStart = 0;
        sNode.preX = -1;
        sNode.preY = -1;

        PriorityQueue<Node> queue = new PriorityQueue<>();

        Node curr = nodeMap[startX][startY];

        while (curr != null && !(curr.block.x == targetX && curr.block.y == targetY)) {
            updateNeighbours(curr, evade, collision, queue);
            if(!queue.isEmpty()){
                curr = queue.remove();
            } else {
                curr = null;
            }
        }

        return curr != null;
    }

    private Stack<Step> backtracking(Position start, Position end){
        Stack<Step> result = new Stack<>();
        int currX = Math.round(start.getX()), currY = Math.round(start.getY()), endX = Math.round(end.getX()), endY = Math.round(end.getY());
        while (!(currX == endX && currY == endY)) {
            int newX = nodeMap[currX][currY].preX;
            int newY = nodeMap[currX][currY].preY;
            result.add(new Step(Step.StepType.GoTo, currX, currY));
            currX = newX;
            currY = newY;
        }

        return result;
    }

    private void updateNeighbours(Node source, boolean evade, boolean collision, PriorityQueue<Node> queue) {
        int x = source.block.x, y = source.block.y;
        for (int i = Math.max(0, x - 1); i < Math.min(x + 2, nodeMap.length); i++) {
            for (int j = Math.max(0, y - 1); j < Math.min(y + 2, nodeMap[i].length); j++) {
                Node current = nodeMap[i][j];

                if(!collision || current.block.getTower() != null) {
                    float addDist = (x == i || y == j) ? 1 : (float) Math.sqrt(2);
                    if(evade) {
                        addDist = addo(addDist, ((current.dpsInRange + source.dpsInRange) / 2) * Constants.dpsMultiplier);
                        addDist = addo(addDist, (current.damage + source.damage) / 2);
                    }
                    addDist = addo(addDist, source.fromStart);

                    if (index != current.index || current.fromStart > addDist) {
                        if(current.index != index){
                            current.index = index;
                            queue.add(current);
                        }
                        current.fromStart = addDist;
                        current.preX = x;
                        current.preY = y;
                    }
                }
            }
        }
    }

    private void resetIndex(){
        index = Integer.MIN_VALUE;
        for(Node[] nodes : nodeMap) {
            for (Node node : nodes) {
                node.index = Integer.MAX_VALUE;
            }
        }
    }

    /*
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     *                      Utility Methods
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    private float getDist(Position p1, Position p2){
        return getDist(p1.getX(),p1.getY(),p2.getX(),p2.getY());
    }

    private float getDist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }

    private float isColliding(Entity p1, Entity p2){
        return getDist(p1,p2) - p1.getRadius() - p2.getRadius();
    }

    private float addo(float old, float add){
        if(old < 0 || add < 0){
            throw new IllegalArgumentException();
        }else if(old+add < 0){
            return Float.MAX_VALUE;
        }else{
            return old+add;
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

    private void updateAllNodes() {
        for(Node[] nodes:nodeMap){
            for(Node node:nodes){
                node.dpsInRange = 0;
            }
        }

        for (int i = 0; i < nodeMap.length; i++) {
            for (int j = 0; j < nodeMap[i].length; j++) {
                updateNodesInRange(i, j);
            }
        }
    }

    private void createNodeMap(Block[][] blocks) {
        nodeMap = new Node[blocks.length][blocks[0].length];

        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                nodeMap[i][j] = new Node(blocks[i][j], i, j, mainTower.getX(), mainTower.getY());
            }
        }

        updateAllNodes();
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

    private class Node implements Comparable<Node> {
        private float dps, attackRange, dpsInRange, fromStart, damage;
        private final float toEnd;
        private int preX, preY, index;
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
            index = Integer.MAX_VALUE;
        }

        /**
         * Setzt dps, attackRange abhängig des übergebenen Turmes
         */
        private void getFromEntity(Entity entity) {
            dps = 0;
            attackRange = 0;
            if (entity != null && !entity.isEnemy() && entity.getAttack() > 0) {
                if(entity.getProjectile() instanceof ProjectileType) {
                    dps = ((ProjectileType)entity.getProjectile()).impactDamage * entity.getAttack() / entity.getFrequency() * dpsMultiplier * difficulty;
                    attackRange = entity.getRange();
                }else if(!entity.isRanged()){
                    dps = entity.getAttack() / entity.getFrequency() * dpsMultiplier * difficulty;
                    attackRange = entity.getRange();
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

        @Override
        public int compareTo(Node o) {
            return Math.round(this.getDistance() - o.getDistance());
        }
    }
}
