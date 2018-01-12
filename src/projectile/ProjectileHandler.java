package projectile;

import enemy.Enemy;
import enemy.effect.EffectType;
import utility.Constants;
import utility.Utility;
import utility.Vector2;
import world.Block;
import world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class ProjectileHandler {

    private World world;

    private Random random;

    public ProjectileHandler(World world) {
        this.world = world;
        random = new Random();
    }

    // Projektile bewegen & Kollisionen mit Gegner überprüfen -> schaden
    public void handleProjectiles(float dt, ArrayList<Projectile> projectiles) {

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);

            if(projectile.getHp() <= 0 || projectile.getDistance() >= projectile.projectileType.speed){
                if(projectile.projectileType == ProjectileType.POISON){
                    spawnPoisonCloud(projectile.getX(),projectile.getY(),projectile.getLevel(), projectile.getDir());
                }
                world.removeProjectile(projectile);
                i--;
            }
            //System.out.println(p.getX() + "  " + p.getY());
            //weite die das Projektil geflogen ist wird aktualisiert
            projectile.setDistance(projectile.getDistance() + projectile.projectileType.speed * dt);
            //wenn die distanz größer als die reichweite ist wird das projektil entfernt
            //projektil fliegt in richtung des zieles
            projectile.setX(projectile.getX() + projectile.getDir().getCoords()[0] * projectile.projectileType.speed * dt);
            projectile.setY(projectile.getY() + projectile.getDir().getCoords()[1] * projectile.projectileType.speed * dt);

            for (Enemy enemy : findCollidingEnemies(projectile)) {
                enemy.addHp(-projectile.projectileType.impactDamage);
                projectile.addHp(-1);
                projectile.addToHitEnemies(enemy);
                switch (projectile.projectileType) {
                    case POISON:
                        spawnPoisonCloud(projectile.getX(), projectile.getY(), projectile.getLevel(), projectile.getDir());
                        break;
                    case ICE:
                        if (Constants.fireBreaksSlow) {
                            enemy.removeEffect(EffectType.Burning);
                        }
                        enemy.addEffect(EffectType.Slow);
                        break;
                    case FLAME:
                    case FRAGGRENADE:
                        if (Constants.fireBreaksSlow) {
                            enemy.removeEffect(EffectType.Slow);
                        }
                        enemy.addEffect(EffectType.Burning);
                        break;
                    case BULLET:
                    case PIERCINGBULLET:
                        enemy.addEffect(EffectType.Bleeding);
                        break;
                    case LIGHTNING:
                        randomRotation(projectile);
                        break;
                }

                if (projectile.getHp() <= 0) {
                    break;
                }
            }
        }


        // Projektile werden bewegt.
        /*
        for(int i = 0;i < projectiles.size();i++) {
            if (projectiles.get(i).getTargetX() - projectiles.get(i).getX() != 0 && projectiles.get(i).getTargetY() - projectiles.get(i).getY() != 0) {
                float temp = (float)Math.sqrt((Math.pow(projectiles.get(i).getTargetX() - projectiles.get(i).getX(),2)+Math.pow(projectiles.get(i).getTargetY() - projectiles.get(i).getY(),2)))/projectiles.get(i).getSpeed();
                projectiles.get(i).setX(projectiles.get(i).getX() + (projectiles.get(i).getTargetX() - projectiles.get(i).getX()) / temp);
                projectiles.get(i).setY(projectiles.get(i).getY() + (projectiles.get(i).getTargetY() - projectiles.get(i).getY()) / temp);

                   // AB HIER WIRD GEPRÜFT OB DER PROJECTILE SICH NUR IN X ODER Y RICHTUNG BEWEGEN MUSS!

            }else if(projectiles.get(i).getTargetX() - projectiles.get(i).getX() != 0 && projectiles.get(i).getTargetY() - projectiles.get(i).getY() == 0){
                if(projectiles.get(i).getX() < projectiles.get(i).getTargetX()) {
                    projectiles.get(i).setX(projectiles.get(i).getX() + projectiles.get(i).getSpeed());
                }else{
                    projectiles.get(i).setX(projectiles.get(i).getX() - projectiles.get(i).getSpeed());
                }
            }else if(projectiles.get(i).getTargetX() - projectiles.get(i).getX() == 0 && projectiles.get(i).getTargetY() - projectiles.get(i).getY() != 0){
                if(projectiles.get(i).getY() < projectiles.get(i).getTargetY()) {
                    projectiles.get(i).setY(projectiles.get(i).getY() + projectiles.get(i).getSpeed());
                }else{
                    projectiles.get(i).setY(projectiles.get(i).getY() - projectiles.get(i).getSpeed());
                }
            }
            //geflogene distanz des projektils wird vergrößert
            if(projectiles.get(i).getDistance() < projectiles.get(i).getRange()){
                projectiles.get(i).setDistance(projectiles.get(i).getDistance() + projectiles.get(i).getSpeed());
            }
            //wenn die distanz größer als die reichweite ist wird das projektil entfernt
            if(projectiles.get(i).getDistance() >= projectiles.get(i).getRange()) {
                projectiles.remove(i);
            }
        }*/

        /*// Kollisionen werden überprüft.
        for(int i = 0;i < projectiles.size();i++){
            for(int j = 0;j < enemies.size();i++){
                //falls projektil mit gegner kollidiert und der gegner noch nicht getroffen ist
                if(utility.gameObjectIsCollidingWithGameObject(projectiles.get(i),enemies.get(j)) && !projectiles.get(i).getHitEnemies().contains(enemies.get(j))){
                    //verringere die hp des gegners, verringere die hp des projektils, füge gegner zu getroffenen hinzu
                    enemies.get(j).setHp(enemies.get(j).getHp() - projectiles.get(i).getImpactDamage());
                    projectiles.get(i).setHp(projectiles.get(i).getHp()-1);
                    projectiles.get(i).addToHitEnemies(enemies.get(j));
                    //falls hp des projektils == 0 ist
                    if(projectiles.get(i).getHp()==0){
                        //lösche das projektil
                        projectiles.remove(i);
                    }
                }
            }
        }*/
    }

    private ArrayList<Enemy> findCollidingEnemies(Projectile projectile){
        ArrayList<Enemy> enemies = new ArrayList<>();

        for (int i = Math.max(0,(int) Math.floor(projectile.getX())); i < Math.min(world.getBlocks().length,Math.ceil(projectile.getX()) + 1); i++) {
            for (int j = Math.max(0,(int) Math.floor(projectile.getY())); j < Math.min(world.getBlocks()[i].length,Math.ceil(projectile.getY()) + 1); j++) {
                for(Enemy enemy:world.getBlocks()[i][j].getEnemies()){
                    float distance = (float)(Math.sqrt(Math.pow(projectile.getX()-enemy.getX(),2) + Math.pow(projectile.getY()-enemy.getY(),2)));
                    if(distance <= enemy.enemyType.getRadius()){
                        enemies.add(enemy);
                    }
                }
            }
        }

        return enemies;
    }

    private void spawnPoisonCloud(float xPos, float yPos, int level, Vector2 direction) {
        Random random = new Random();
        for (int i = 0; i < Constants.poisonCloudAmount; i++) {
            float alpha = random.nextFloat() * (float) (Math.PI * 2);
            float distance = random.nextFloat() * 1.5f;
            float x = xPos + (float) (Math.cos(alpha) * distance);
            float y = yPos + (float) (Math.sin(alpha) * distance);
            world.spawnProjectile(new Projectile(ProjectileType.POISONTRAIL, level, x, y, direction));
        }
    }

    private void randomRotation(Projectile projectile){
        float oldX = projectile.getDir().getCoords()[0];
        float oldY = projectile.getDir().getCoords()[1];
        double rand = random.nextDouble() * (float) (Math.PI*2);
        projectile.getDir().getCoords()[0] = (float)(Math.cos(rand)*oldX - Math.sin(rand)*oldY);
        projectile.getDir().getCoords()[1] = (float)(Math.sin(rand)*oldX + Math.cos(rand)*oldY);
    }

}

