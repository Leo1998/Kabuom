package enemy;

import graph.Vertex;
import tower.Tower;
import tower.TowerType;
import utility.Vector2;
import world.Block;
import world.World;
import enemy.step.*;

import java.util.*;


public class EnemyHandler {

    private Node[][] nodeMap;
    private boolean changed;
    private World world;
    private float dpsMultiplier = 1;
    private final int mainX, mainY;
    private Random random;

    /**
     * Konstruktur von Enemyhandler
     * @param world die World
     */
    public EnemyHandler(World world, int mainX, int mainY) {
        this.mainX = mainX;
        this.mainY = mainY;
        createNodeMap(world.getBlocks());
        changed = true;
        this.world = world;
        this.random = new Random();
    }


    /**
     * Berechnet die Handlungen aller Gegner und führt diese aus
     * @param dt Zeit seit dem letztem Frame
     * @param enemies Liste aller Gegner
     * @param recalculate Ist true, wenn in dem Frame ein Turm geädnert oder Drunk aktiviert wurde (einmalig)
     * @param drunk Ist true, wenn Drunk aktiv ist
     */
    public void handleEnemies(float dt, ArrayList<Enemy> enemies, boolean recalculate,boolean drunk){
        if(!drunk) {
            if (recalculate) {
                createNodeMap(world.getBlocks());
            }
            calcAllPaths(enemies);
        }else {
            for(Enemy enemy : enemies){
                if(enemy.getPath().isEmpty() || recalculate){
                    Queue<Step> newPath =  new LinkedList<>();
                    newPath.add(getRandomMove(Math.round(enemy.getX()),Math.round(enemy.getY()),random));
                    enemy.setPath(newPath);
                }
            }
            changed = true;
        }
        for(Enemy currEnemy:enemies){
            handleEnemy(dt, currEnemy,drunk,random);
        }
    }

    private MoveStep getRandomMove(int x, int y, Random random){
        boolean moveX = random.nextBoolean();
        boolean moveY = random.nextBoolean();
        if(!moveX && !moveY){
            moveX = random.nextBoolean();
            moveY = !moveX;
        }
        int toX = x, toY = y;
        if(moveX){
            toX += (random.nextBoolean())? -1:1;
        }
        if(moveY){
            toY += (random.nextBoolean())? -1:1;
        }

        return new MoveStep(StepType.Move,toX,toY);
    }

    private void calcAllPaths(ArrayList<Enemy> enemies){

        //Group Enemies at similar places together to reduce total amount of findPath calls
        ArrayList<Enemy>[][] groupMap = new ArrayList[nodeMap.length][nodeMap[0].length];
        for(Enemy enemy: enemies){
            if(changed || enemy.getPath().isEmpty()){
                int i = Math.max(Math.min(Math.round(enemy.getX()),groupMap.length),0);
                int j = Math.max(Math.min(Math.round(enemy.getY()),groupMap[i].length),0);
                if(groupMap[i][j] == null){
                    groupMap[i][j] = new ArrayList<>();
                }
                groupMap[i][j].add(enemy);
            }
        }

        //Call findPath for every enemy-Group
        for(int i = 0; i < groupMap.length; i++){
            for(int  j = 0; j < groupMap[i].length; j++){
                ArrayList<Enemy> group = groupMap[i][j];
                if(group != null){
                    Queue<Step> path = findPath(i,j);
                    Queue<Step>[] paths = createQueueClones(path,group.size());
                    if(paths != null) {
                        for (int k = 0; k < group.size(); k++) {
                            group.get(k).setPath(paths[k]);
                        }
                    }
                }
            }
        }

        changed = false;
    }

    private Queue<Step>[] createQueueClones(Queue<Step> steps, int amount){
        if(amount > 0 && steps != null && !steps.isEmpty()) {
            Queue<Step>[] result = new Queue[amount];
            for(int i = 0; i < result.length; i++){
                result[i] = new LinkedList<>();
            }
            for (Step step : steps) {
                for (Queue<Step> queue : result) {
                    try {
                        queue.add(step.clone());
                    } catch (java.lang.CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }else {
            return null;
        }
    }

    /**
     * Entscheidet, ob ein Gegner sich bewegen oder angreifen soll.
     * Führt dies aus
     * @param dt Zeit seit dem letztem Frame
     * @param enemy Gegner, für den dies entschieden werden soll
     */
    private void handleEnemy(float dt,Enemy enemy,boolean drunk,Random random){
        if(enemy.getHp()<=0){
            world.removeGameObject(enemy);
        }else {
            enemy.addAttackCooldown(dt);
            if(enemy.getSlowDuration() > 0) {
                enemy.addSlowDuration(-dt);
            }
            Tower tower = getCollidingTower(enemy);
            if (tower != null && (!drunk || random.nextDouble() < 0.3 || tower.getType() == TowerType.BARRICADE)) {  //Attack
                enemy.setMovement(new Vector2(0,0));
                if (enemy.getAttackCooldown() > enemy.getAttackSpeed()) {
                    tower.setHp(tower.getHp() - enemy.getDamage());
                    enemy.setAttackCooldown(0);
                    if(tower.getType() == TowerType.BARRICADE){
                        enemy.setHp(Math.round(enemy.getHp()-(enemy.getDamage()*0.5f)));
                    }
                }
            } else {              //Move
                move(enemy, enemy.getSpeed() * dt);
            }
        }
    }

    private void move(Enemy enemy, float moveableDist){
        Tower collidingTower = getCollidingTower(enemy);
        if(collidingTower == null && enemy.getPath() != null && !enemy.getPath().isEmpty()) {
            Step step = enemy.getPath().peek();
            if(step instanceof MoveStep) {
                float targetX = ((MoveStep)step).x;
                float targetY = ((MoveStep)step).y;
                float dist = (float) (Math.sqrt(Math.pow(enemy.getX() - targetX, 2) + Math.pow(enemy.getY() - targetY, 2)));
                while (dist < moveableDist) {
                    enemy.getPath().remove();
                    step = enemy.getPath().peek();
                    if(step instanceof MoveStep) {
                        targetX = ((MoveStep) step).x;
                        targetY = ((MoveStep) step).y;
                        dist = (float) (Math.sqrt(Math.pow(enemy.getX() - targetX, 2) + Math.pow(enemy.getY() - targetY, 2)));
                    }else{
                        goTo(enemy,targetX,targetY);
                        return;
                    }
                }
                float q = moveableDist / dist;
                float nX = enemy.getX() + ((targetX - enemy.getX()) * q);
                float nY = enemy.getY() + ((targetY - enemy.getY()) * q);
                goTo(enemy,nX,nY);
            }
        }
    }

    private void goTo(Enemy enemy, float x, float y){
        Vector2 vec = new Vector2(x - enemy.getX(), y - enemy.getY());
        enemy.setX(x);
        enemy.setY(y);
        vec.normalize();
        vec.multiply(enemy.getEnemyType().getSpeed());
        enemy.setMovement(vec);
    }

    private Tower getCollidingTower(Enemy enemy){
        Tower result = null;
        for(int i = (int)Math.floor(enemy.getX()); i < Math.ceil(enemy.getX())+1 && result == null; i++){
            for(int j = (int)Math.floor(enemy.getY()); j < Math.ceil(enemy.getY())+1 && result == null; j++){
                Tower tower = nodeMap[i][j].block.getTower();
                if(tower != null){
                    if(getDist(tower.getX(),tower.getY(),enemy.getX(),enemy.getY()) < 2){
                        result = tower;
                    }
                }
            }
        }
        return result;
    }

    private Queue<Step> findPath(int startX ,int startY){
        prepareNodeMap(startX,startY);
        int xPos = startX;
        int yPos = startY;
        while(xPos != mainX || yPos != mainY){
            nodeMap[xPos][yPos].visited = true;
            updateNeighbours(xPos,yPos);
            int[] next = findSmallest();
            if(next != null){
                xPos = next[0];
                yPos = next[1];
            }else{
                return null;
            }
        }

        return goBack(xPos,yPos);
    }

    private Queue<Step> goBack(int x, int y){
        Queue<Step> path = new LinkedList<>();

        int xPos = x, yPos = y;
        Node current = nodeMap[xPos][yPos];
        while(current.preX != -1 && current.preY != -1){
            path.add(new MoveStep(StepType.Move,xPos,yPos));
            xPos = current.preX;
            yPos = current.preY;
            current = nodeMap[xPos][yPos];
        }
        return path;
    }

    private int[] findSmallest(){
        int[] min = null;
        for(int i = 0; i < nodeMap.length; i++){
            for(int j = 0; j < nodeMap.length; j++){
                Node current  = nodeMap[i][j];
                if(!current.visited && current.getDistance() != Float.MAX_VALUE){
                    if(min == null){
                        min = new int[]{i,j};
                    }else if(nodeMap[min[0]][min[1]].getDistance() > current.getDistance()){
                        min[0] = i;
                        min[1] = j;
                    }
                }
            }
        }
        return min;
    }

    private void updateNeighbours(int xPos, int yPos){
        for(int i = Math.max(0,xPos-1); i < Math.min(nodeMap.length, xPos+2); i++){
            for(int j = Math.max(0,yPos-1); j < Math.min(nodeMap[i].length, yPos+2); j++){
                if(i != xPos || j != yPos){
                    updateNeighbour(xPos,yPos,i,j);
                }
            }
        }
    }

    private void updateNeighbour(int srcX, int srcY, int xPos, int yPos){
        Node current = nodeMap[xPos][yPos];
        if(!current.visited) {
            float newDist = nodeMap[srcX][srcY].fromStart;
            if(newDist != Float.MAX_VALUE) {
                newDist += (nodeMap[srcX][srcY].dpsInRange + current.dpsInRange) / 2;
                if (srcX == xPos || srcY == yPos) {
                    newDist += 1;
                } else {
                    newDist += (float) Math.sqrt(2);
                }

                if (newDist < current.fromStart) {
                    current.fromStart = newDist;
                    current.preX = srcX;
                    current.preY = srcY;
                }
            }
        }
    }

    private void prepareNodeMap(int startX, int startY){
        for(int i = 0; i < nodeMap.length; i++){
            for(int  j = 0; j < nodeMap.length; j++){
                Node current = nodeMap[i][j];
                current.preX = -1;
                current.preY = -1;
                current.visited = false;
                if(i == startX && j == startY){
                    current.fromStart = 0;
                }else{
                    current.fromStart = Float.MAX_VALUE;
                }
            }
        }
    }

    private float getDist(int x1, int y1, int x2, int y2){
        return (float) Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    private float getDist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    private void updateNodesInRange(int xPos, int yPos){
        if(nodeMap[xPos][yPos].dps > 0){
            float dps = nodeMap[xPos][yPos].dps;
            float attackRange = nodeMap[xPos][yPos].attackRange+1;

            for(int i = (int)Math.max(0,xPos-attackRange); i < (int)Math.min(nodeMap.length,xPos+attackRange+1); i++){
                for(int j = (int)Math.max(0,yPos-attackRange); j < (int)Math.min(nodeMap[i].length,yPos+attackRange+1); j++){
                    if(getDist(xPos,yPos,i,j) <= attackRange){
                        nodeMap[i][j].dpsInRange += dps;
                    }
                }
            }
        }
    }

    private void updateAllNodes(){
        for(int  i = 0; i < nodeMap.length; i++){
            for(int  j = 0; j < nodeMap.length; j++){
                updateNodesInRange(i,j);
            }
        }
    }

    private void createNodeMap(Block[][] blocks){
        nodeMap = new Node[blocks.length][blocks[0].length];

        for(int i = 0; i < blocks.length; i++){
            for (int j = 0; j < blocks.length; j++){
                nodeMap[i][j] = new Node(blocks[i][j],i,j,mainX,mainY);
            }
        }

        updateAllNodes();
    }

    private void createNodeMap(Vertex<Tower>[][] blocks){
        nodeMap = new Node[blocks.length][blocks[0].length];

        for(int i = 0; i < blocks.length; i++){
            for (int j = 0; j < blocks.length; j++){
                nodeMap[i][j] = new Node(new Block(blocks[i][j],blocks[i][j].getContent()),i,j,mainX,mainY);
            }
        }

        updateAllNodes();

        changed = true;
    }

    /**
     * Gibt dpsMultiplier zurück
     */
    public float getDpsMultiplier() {
        return dpsMultiplier;
    }

    /**
     * Setzt dpsMultiplier auf den übergebenen Wert
     */
    public void setDpsMultiplier(float dpsMultiplier) {
        this.dpsMultiplier = dpsMultiplier;
    }

    private class Node{
        private float dps,attackRange,dpsInRange;
        private float fromStart;
        private final float toEnd;
        private int xPos, yPos, preX, preY;
        private boolean visited;
        private Block block;

        public Node(Block block, int xPos, int yPos, int endX, int endY){
            this.xPos = xPos;
            this.yPos = yPos;
            this.preX = -1;
            this.preY = -1;
            this.block = block;
            int minDif = Math.min(Math.max(xPos,endX)-Math.min(xPos,endX),Math.max(yPos,endY)-Math.min(yPos,endY));
            int maxDif = Math.max(Math.max(xPos,endX)-Math.min(xPos,endX),Math.max(yPos,endY)-Math.min(yPos,endY));
            this.toEnd = (float) (Math.sqrt(2) * minDif) + maxDif - minDif;
            this.fromStart = Float.MAX_VALUE;
            getFromTower(block.getTower());
            dpsInRange = 0;
            visited = false;
        }

        /**
         * Setzt dps, attackRange abhängig des übergebenen Turmes
         */
        private void getFromTower(Tower tower){
            if(tower != null) {
                if(tower.getFrequency() == 0){
                    dps = 0;
                }else {
                    dps = (tower.getProjectile().getImpactDamage() / tower.getFrequency()) * dpsMultiplier;
                }
                attackRange = tower.getAttackRadius();
            }else{
                dps = 0;
                attackRange = 0;
            }
        }

        private float getDistance(){
            if(fromStart == Float.MAX_VALUE){
                return Float.MAX_VALUE;
            }else {
                return fromStart + toEnd;
            }
        }
    }
}