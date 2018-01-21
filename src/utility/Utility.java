package utility;

import enemy.Enemy;
import entity.model.Entity;
import model.GameObject;
import model.Position;
import projectile.Projectile;
import projectile.ProjectileType;
import tower.Tower;
import tower.TowerType;
import view.components.ViewComponent;
import world.Block;
import world.World;

import java.util.Random;


public class Utility {

    public static Random random = new Random();

    public static float calculateAngleBetweenTwoPoints(float x1, float y1, float x2, float y2) {
        double deltaX, deltaY;
        int tempCoord;

        if (x2 - x1 != 0) {
            deltaX = x2 - x1;
        } else {
            deltaX = 0.00000001;
        }
        if (y2 - y1 != 0) {
            deltaY = y2 - y1;
        } else {
            deltaY = 0.00000001;
        }

        if (x2 < x1) {
            if (y2 < y1) {
                tempCoord = 3;
            } else {
                tempCoord = 2;
            }
        } else {
            if (y2 < y1) {
                tempCoord = 4;
            } else {
                tempCoord = 1;
            }
        }

        double angle = Math.atan(deltaX / deltaY);
        if (tempCoord == 1) {
            angle = (float) (Math.atan((deltaX) / (-deltaY)));
        } else if (tempCoord == 2) {
            angle = (float) (Math.atan((deltaX) / (-deltaY)));
        } else if (tempCoord == 3) {
            angle = (float) (Math.atan((deltaX) / (-deltaY))) + Math.PI;
        } else if (tempCoord == 4) {
            angle = (float) (Math.atan((deltaX) / (-deltaY))) + Math.PI;
        }

        return (float) (angle);
    }

    public static float getAngle(GameObject o1, GameObject o2) {
        double theta = Math.atan2(o1.getY() - o2.getY(), o1.getX() - o2.getX());

        theta += Math.PI / 2.0;

        if (theta < 0) {
            theta += Math.PI * 2.0;
        }

        return (float) theta;
    }

    public static boolean viewComponentIsCollidingWithMouse(ViewComponent o1, int mouseX, int mouseY) {
        if (o1 != null) {
            return mouseX >= (o1.getX()) &&
                    mouseX <= (o1.getX()) + (o1.getWidth()) &&
                    mouseY >= (o1.getY()) &&
                    mouseY <= (o1.getY()) + (o1.getHeight());
        } else {
            return false;
        }
    }

    public static Entity findCollidingEntity(Entity source, Block[][] blocks){
        float x = source.getX(), y = source.getY(), radius = source.entityType.getRadius();
        Entity closest = null;
        for (int i = Math.max(0,(int) (Math.floor(x - 1))); i < Math.min(blocks.length,Math.ceil(x + 2)); i++) {
            for (int j = Math.max(0, (int) (Math.floor(y - 1))); j < Math.min(blocks[i].length, Math.ceil(y + 2)); j++) {
                /* Comment because Block does not yet support Entities
                for(Entity entity : blocks[i][j].getEnemies()){
                    if(!source.allyOf(entity)){
                        if (closest == null || getDist(source, entity) - entity.entityType.getRadius() < getDist(source, closest) - closest.entityType.getRadius()) {
                            closest = entity;
                        }
                    }
                }//*/
            }
        }

        if(closest != null && getDist(source,closest) > closest.entityType.getRadius() + radius){
            closest = null;
        }
        return closest;
    }

    public static Enemy shootEnemy(GameObject source, TowerType shootable, World world){
        Enemy target = getTargetEnemy(source,shootable.getAimRadius(),world);

        if(target != null) {
            Vector2 aiming = aim(source, target, target.getMovement(), shootable.getProjectileType().speed);

            for (int i = 0; i < shootable.getShots(); i++) {
                Vector2 copy = aiming.clone();

                Projectile projectile = createProjectile(source, copy, shootable.getAccuracy(), shootable.getProjectileType());

                world.spawnProjectile(projectile);
            }
        }

        return target;
    }

    public static Tower shootTower(GameObject source, TowerType shootable, World world){
        Tower target = getTargetTower(source,shootable.getAimRadius(), world);

        if(target != null){
            Vector2 aiming = aim(source,target);

            for(int i = 0; i < shootable.getShots(); i++){
                Vector2 copy = aiming.clone();

                Projectile projectile = createProjectile(source, copy, shootable.getAccuracy(), shootable.getProjectileType());

                world.spawnProjectile(projectile);
            }
        }

        return target;
    }

    public static Enemy getTargetEnemy(GameObject source, float range, World world){
        int x = Math.round(source.getX()), y = Math.round(source.getY());
        Enemy closest = null;

        int maxDist = Math.min(Math.round(range),Math.max(Math.max(x, world.getBlocks().length-x),Math.max(y, world.getBlocks()[x].length-y)));
        for(int dist = 0; closest == null && dist <= maxDist; dist++) {
            for (int i = Math.max(0, x - dist); i < Math.min(world.getBlocks().length, x + dist + 1); i++) {
                boolean full = (i == x - dist || i == x + dist);
                for (int j = full ? Math.max(0, y - dist) : (y - dist < 0) ? y + dist : y - dist; j < Math.min(world.getBlocks()[i].length, y + dist + 1); j += full ? 1 : 2 * dist) {
                    Block block = world.getBlocks()[i][j];
                    for(Enemy enemy:block){
                        if(closest == null || getDist(enemy,source) < getDist(enemy,closest)){
                            closest = enemy;
                        }
                    }
                }
            }
        }

        if(closest != null && getDist(source,closest) > range){
            closest = null;
        }
        return closest;
    }

    public static Tower getTargetTower(GameObject source, float range, World world){
        int x = Math.round(source.getX()), y = Math.round(source.getY());
        Tower closest = null;
        int maxDist = Math.min(Math.round(range),Math.max(Math.max(x, world.getBlocks().length-x),Math.max(y, world.getBlocks()[x].length-y)));
        for(int dist = 0; closest == null && dist <= maxDist; dist++) {
            for (int i = Math.max(0, x - dist); i < Math.min(world.getBlocks().length, x + dist + 1); i++) {
                boolean full = (i == x - dist || i == x + dist);
                for (int j = full ? Math.max(0, y - dist) : (y - dist < 0) ? y + dist : y - dist; j < Math.min(world.getBlocks()[i].length, y + dist + 1); j += full ? 1 : 2 * dist) {
                    Block block = world.getBlocks()[i][j];
                    if(block.getTower() != null && (closest == null || getDist(closest,source) > getDist(block.getTower(),source))){
                        closest = block.getTower();
                    }
                }
            }
        }

        if(closest != null && getDist(source,closest) > range){
            closest = null;
        }
        return closest;
    }

    public static Vector2 aim(GameObject source, GameObject target, Vector2 targetMovement, float projectileSpeed){
        float sX = source.getX();
        float sY = source.getY();

        float tX = target.getX();
        float tY = target.getY();

        float tmx = targetMovement.getCoords()[0];
        float tmy = targetMovement.getCoords()[1];
        float s = projectileSpeed;

        float a = tmx * tmx + tmy * tmy - s * s;
        float b = 2 * ((tX - sX) * tmx + (tY - sY) * tmy);
        float c = (tX - sX) * (tX - sX) + (tY - sY) * (tY - sY);
        float d = b * b - 4 * a * c;

        Vector2 vec;
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

        vec.normalize();

        return vec;
    }

    public static Vector2 aim(GameObject source, GameObject target){
        float aimX = target.getX() - source.getY();
        float aimY = target.getY() - source.getY();

        Vector2 vec = new Vector2(aimX,aimY);

        vec.normalize();

        return vec;
    }

    public static Projectile createProjectile(GameObject source, Vector2 vec, float accuracy, ProjectileType projectileType){
        if (accuracy > 0) {
            vec.rotate((float) ((random.nextDouble() - 0.5) * accuracy));
        }

        Projectile p = new Projectile(projectileType, source.getLevel(), source.getX(), source.getY(), vec);

        p.setX(p.getX() + p.getDir().getCoords()[0] * source.getObjectType().getRadius());
        p.setY(p.getY() + p.getDir().getCoords()[1] * source.getObjectType().getRadius());

        return p;
    }

    public static float getDist(Position p1, Position p2){
        return getDist(p1.getX(),p1.getY(),p2.getX(),p2.getY());
    }

    public static float getDist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }

    public static float saveAdd(float old, float add){
        if(old < 0 || add < 0){
            throw new IllegalArgumentException();
        }else if(old+add < 0){
            return Float.MAX_VALUE;
        }else{
            return old+add;
        }
    }

}
