package enemy;

import enemy.effect.EffectType;
import enemy.step.MoveStep;
import enemy.step.Step;
import tower.Tower;
import tower.TowerType;
import utility.Constants;
import utility.Vector2;
import world.Block;
import world.World;

import java.util.*;


public class EnemyHandler {

    private Node[][] nodeMap;
    private boolean changed;
    private World world;
    private float dpsMultiplier = 1;
    private final int mainX, mainY;
    private Random random;

    /**
     *
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

    public void addDamage(float damage, float xPos, float yPos){
        for(int i = Math.max(Math.round(xPos)-2,0); i < Math.min(Math.round(xPos)+2,nodeMap.length); i++){
            for(int j = Math.max(Math.round(yPos)-2,0); j < Math.min(Math.round(yPos)+2,nodeMap[i].length); j++){
                nodeMap[Math.round(xPos)][Math.round(yPos)].damage += damage;
            }
        }
    }


    /**
     * Berechnet die Handlungen aller Gegner und führt diese aus
     *
     * @param dt          Zeit seit dem letztem Frame
     * @param enemies     Liste aller Gegner
     * @param recalculate Ist true, wenn in dem Frame ein Turm geädnert oder Drunk aktiviert wurde (einmalig)
     * @param drunk       Ist true, wenn Drunk aktiv ist
     */
    public int handleEnemies(float dt, ArrayList<Enemy> enemies, boolean recalculate, boolean drunk) {
        int minWave = -1;
        if (!drunk) {
            if (recalculate) {
                updateNodeMap(world.getBlocks());
            }
            findPaths(enemies);
        } else {
            for (Enemy enemy : enemies) {
                if (enemy.getPath().isEmpty() || recalculate) {
                    Stack<Step> newPath = new Stack<>();
                    newPath.push(getRandomMove(Math.round(enemy.getX()), Math.round(enemy.getY()), random));
                    enemy.setPath(newPath);
                }
            }
            changed = true;
        }
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            if(enemy.getHp() <= 0){
                world.removeEnemy(enemy);
                i--;
            }else{
                if(enemy.wave < minWave || minWave == -1){
                    minWave = enemy.wave;
                }
            }

            handleEnemy(dt, enemy, drunk, random);
        }
        return minWave;
    }

    private MoveStep getRandomMove(int x, int y, Random random) {
        boolean moveX = random.nextBoolean();
        boolean moveY = random.nextBoolean();
        if (!moveX && !moveY) {
            moveX = random.nextBoolean();
            moveY = !moveX;
        }
        int toX = x, toY = y;
        if (moveX) {
            toX += (random.nextBoolean()) ? -1 : 1;
        }
        if (moveY) {
            toY += (random.nextBoolean()) ? -1 : 1;
        }

        return new MoveStep(toX, toY);
    }

    private void findPaths(ArrayList<Enemy> enemies) {

        //Group Enemies at similar places together to reduce total amount of findPath calls
        ArrayList<Enemy>[][] groupMap = new ArrayList[nodeMap.length][nodeMap[0].length];
        for (Enemy enemy : enemies) {
            if (changed || enemy.getPath().isEmpty()) {
                int i = Math.max(Math.min(Math.round(enemy.getX()), groupMap.length), 0);
                int j = Math.max(Math.min(Math.round(enemy.getY()), groupMap[i].length), 0);
                if (groupMap[i][j] == null) {
                    groupMap[i][j] = new ArrayList<>();
                }
                groupMap[i][j].add(enemy);
            }
        }

        //Call findPath for every enemy-Group
        for (int i = 0; i < groupMap.length; i++) {
            for (int j = 0; j < groupMap[i].length; j++) {
                ArrayList<Enemy> group = groupMap[i][j];
                if (group != null) {
                    Queue<Step> path = findPath(i, j);
                    Stack<Step>[] paths = createStackClones(path, group.size());
                    if (paths != null) {
                        for (int k = 0; k < group.size(); k++) {
                            group.get(k).setPath(paths[k]);
                        }
                    }
                }
            }
        }

        changed = false;
    }

    private Stack<Step>[] createStackClones(Queue<Step> steps, int amount) {
        if (amount > 0 && steps != null && !steps.isEmpty()) {
            Stack<Step>[] result = new Stack[amount];
            for (int i = 0; i < result.length; i++) {
                result[i] = new Stack<>();
            }
            for (Step step : steps) {
                for (Stack<Step> stack : result) {
                    try {
                        stack.push(step.clone());
                    } catch (java.lang.CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Entscheidet, ob ein Gegner sich bewegen oder angreifen soll.
     * Führt dies aus
     *
     * @param dt    Zeit seit dem letztem Frame
     * @param enemy Gegner, für den dies entschieden werden soll
     */
    private void handleEnemy(float dt, Enemy enemy, boolean drunk, Random random) {
        enemy.addAttackCooldown(dt);
        enemy.addEffectDuration(-dt);
        if (enemy.hasEffect(EffectType.Burning)) {
            enemy.addHp(Math.round(EffectType.Burning.strength * dt));
        }
        Tower tower = getCollidingTower(enemy);
        if (tower != null && (!drunk || random.nextDouble() < 0.3 || tower.towerType == TowerType.BARRICADE)) {  //Attack
            enemy.setMovement(new Vector2(0, 0));
            if (enemy.getAttackCooldown() > enemy.getAttackSpeed()) {
                tower.addHp(-enemy.getDamage());
                enemy.setAttackCooldown(0);
                if (tower.towerType == TowerType.BARRICADE) {
                    enemy.setHp(Math.round(enemy.getHp() - (enemy.getDamage() * 0.5f)));
                }
            }
        } else {              //Move
            move(enemy, enemy.getSpeed() * dt, dt);
        }
    }

    private void move(Enemy enemy, float moveableDist, float dt) {
        Tower collidingTower = getCollidingTower(enemy);
        if (collidingTower == null && enemy.getPath() != null && !enemy.getPath().isEmpty()) {
            Step step = enemy.getPath().peek();
            if (step instanceof MoveStep) {
                float targetX = ((MoveStep) step).x;
                float targetY = ((MoveStep) step).y;
                float dist = (float) (Math.sqrt(Math.pow(enemy.getX() - targetX, 2) + Math.pow(enemy.getY() - targetY, 2)));
                boolean qMove = true;
                while (dist < moveableDist) {
                    enemy.getPath().pop();
                    if(!enemy.getPath().isEmpty()) {
                        step = enemy.getPath().peek();
                        if (step instanceof MoveStep) {

                            targetX = ((MoveStep) step).x;
                            targetY = ((MoveStep) step).y;

                            dist = (float) (Math.sqrt(Math.pow(enemy.getX() - targetX, 2) + Math.pow(enemy.getY() - targetY, 2)));
                        } else {
                            goTo(enemy, targetX, targetY, dt);
                            qMove = false;
                        }
                    } else{
                        qMove = false;
                    }
                }
                if(qMove) {
                    float q = moveableDist / dist;
                    float nX = enemy.getX() + ((targetX - enemy.getX()) * q);
                    float nY = enemy.getY() + ((targetY - enemy.getY()) * q);
                    goTo(enemy, nX, nY, dt);
                }
            }
        }
    }

    private void goTo(Enemy enemy, float x, float y, float dt) {
        if(Math.round(enemy.getX()) != Math.round(x) || Math.round(enemy.getY()) != Math.round(y)) {
            nodeMap[Math.round(enemy.getX())][Math.round(enemy.getY())].block.removeEnemy(enemy);
            enemy.setBlock(nodeMap[Math.round(x)][Math.round(y)].block);
            nodeMap[Math.round(x)][Math.round(y)].block.addEnemy(enemy);
        }

        enemy.setMovement(new Vector2((x - enemy.getX()) / dt, (y - enemy.getY()) / dt));
        enemy.setX(x);
        enemy.setY(y);
    }

    private Tower getCollidingTower(Enemy enemy) {
        Tower result = null;
        for (int i = Math.max(0,(int) Math.floor(enemy.getX())); i < Math.min(nodeMap.length,Math.ceil(enemy.getX()) + 1) && result == null; i++) {
            for (int j = Math.max(0,(int) Math.floor(enemy.getY())); j < Math.min(nodeMap[i].length,Math.ceil(enemy.getY()) + 1) && result == null; j++) {
                Tower tower = nodeMap[i][j].block.getTower();
                if (tower != null) {
                    if (getDist(tower.getX(), tower.getY(), enemy.getX(), enemy.getY()) < 2) {
                        result = tower;
                    }
                }
            }
        }
        return result;
    }

    private Queue<Step> findPath(int startX, int startY) {
        //Exceptions
        //Map empty
        if (nodeMap == null || nodeMap.length == 0 || nodeMap[0] == null || nodeMap[0].length == 0) return null;
        //Coordinates to small
        if (startX < 0 || startY < 0 || mainX < 0 || mainY < 0) return null;
        //Coordinates to large
        if (startX >= nodeMap.length || startY >= nodeMap[0].length || mainX >= nodeMap.length || mainY >= nodeMap[0].length)
            return null;

        prepareNodeMap(startX, startY);
        int currX = startX, currY = startY;
        boolean unreachable = false;

        while (!unreachable && !(currX == mainX && currY == mainY)) {
            nodeMap[currX][currY].visited = true;
            updateNeighbours(currX, currY);
            int[] newCurr = getMinNode();
            if (newCurr == null) {
                unreachable = true;
            } else {
                currX = newCurr[0];
                currY = newCurr[1];
            }
        }

        if (unreachable) {
            return null;
        } else {
            return goBack(currX, currY, startX, startY);
        }
    }

    private Queue<Step> goBack(int currX, int currY, int startX, int startY) {
        Queue<Step> result = new LinkedList<>();
        while (!(currX == startX && currY == startY)) {
            int newX = nodeMap[currX][currY].preX;
            int newY = nodeMap[currX][currY].preY;
            result.add(new MoveStep(currX, currY));
            currX = newX;
            currY = newY;
        }

        return result;
    }

    private void updateNeighbours(int x, int y) {
        if (x >= 0 && y >= 0 && x < nodeMap.length && y < nodeMap[x].length) {
            for (int i = Math.max(0, x - 1); i < Math.min(x + 2, nodeMap.length); i++) {
                for (int j = Math.max(0, y - 1); j < Math.min(y + 2, nodeMap[i].length); j++) {
                    updateNeighbour(x, y, i, j);
                }
            }
        }
    }

    private void updateNeighbour(int srcX, int srcY, int xPos, int yPos) {
        if (xPos >= 0 && yPos >= 0 && xPos < nodeMap.length && yPos < nodeMap[xPos].length) {
            Node source = nodeMap[srcX][srcY], current = nodeMap[xPos][yPos];
            float addDist = (srcX == xPos || srcY == yPos) ? 1 : (float) Math.sqrt(2);
            addDist += ((current.dpsInRange + source.dpsInRange) / 2)* Constants.dpsMultiplier;
            addDist += (current.damage + source.damage) / 2;
            if ((current.fromStart > source.fromStart + addDist) || (current.fromStart == source.fromStart + addDist)) {
                current.fromStart = source.fromStart + addDist;
                current.preX = srcX;
                current.preY = srcY;
            }

        }
    }

    private int[] getMinNode() {
        int minX = -1, minY = -1;
        for (int i = 0; i < nodeMap.length; i++) {
            for (int j = 0; j < nodeMap[i].length; j++) {
                if (!nodeMap[i][j].visited && (minX == -1 || nodeMap[i][j].getDistance() < nodeMap[minX][minY].getDistance())) {
                    minX = i;
                    minY = j;
                }
            }
        }
        if (minX == -1 || nodeMap[minX][minY].fromStart == Float.MAX_VALUE) {
            return null;
        } else {
            return new int[]{minX, minY};
        }
    }

    private void prepareNodeMap(int startX, int startY) {
        for (int i = 0; i < nodeMap.length; i++) {
            for (int j = 0; j < nodeMap.length; j++) {
                Node current = nodeMap[i][j];
                current.preX = -1;
                current.preY = -1;
                current.visited = false;
                if (i == startX && j == startY) {
                    current.fromStart = 0;
                } else {
                    current.fromStart = Float.MAX_VALUE;
                }
            }
        }
    }

    private float getDist(int x1, int y1, int x2, int y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private float getDist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void updateNodesInRange(int xPos, int yPos) {
        if (nodeMap[xPos][yPos].dps > 0) {
            float dps = nodeMap[xPos][yPos].dps;
            float attackRange = nodeMap[xPos][yPos].attackRange;

            for (int i = (int) Math.max(0, xPos - attackRange); i < (int) Math.min(nodeMap.length, xPos + attackRange + 1); i++) {
                for (int j = (int) Math.max(0, yPos - attackRange); j < (int) Math.min(nodeMap[i].length, yPos + attackRange + 1); j++) {
                    float distance = getDist(xPos,yPos,i,j);
                    if (distance <= attackRange) {
                        nodeMap[i][j].dpsInRange += dps;
                    }
                }
            }
        }
    }

    private void updateAllNodes() {
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
                nodeMap[i][j] = new Node(blocks[i][j], i, j, mainX, mainY);
            }
        }

        updateAllNodes();
    }

    private void updateNodeMap(Block[][] blocks){
        for(int i = 0; i < blocks.length && i < nodeMap.length; i++){
            for(int j = 0; j < blocks[i].length && i < nodeMap[i].length; j++){
                nodeMap[i][j].getFromBlock(blocks[i][j]);
            }
        }

        updateAllNodes();
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

    private class Node {
        private float dps, attackRange, dpsInRange, fromStart, damage;
        private final float toEnd;
        private int preX, preY;
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
            getFromTower(block.getTower());
            dpsInRange = 0;
            visited = false;
        }

        /**
         * Setzt dps, attackRange abhängig des übergebenen Turmes
         */
        private void getFromTower(Tower tower) {
            if (tower != null) {
                if (tower.towerType.frequency == 0) {
                    dps = 0;
                } else {
                    dps = (tower.towerType.projectileType.impactDamage / tower.towerType.frequency) * dpsMultiplier;
                }
                attackRange = tower.towerType.attackRadius;
            } else {
                dps = 0;
                attackRange = 0;
            }
        }

        private void getFromBlock(Block block){
            this.block = block;
            getFromTower(block.getTower());
            dpsInRange = 0;
        }

        private float getDistance() {
            if (fromStart == Float.MAX_VALUE) {
                return Float.MAX_VALUE;
            } else {
                return fromStart + toEnd;
            }
        }
    }
}