package projectile;

import enemy.Enemy;
import tower.TowerType;
import utility.*;
import world.World;

import java.util.ArrayList;
import java.util.Random;

public class ProjectileHandler {

    private World world;

    public ProjectileHandler(World world) {
        this.world = world;
    }

    // Projektile bewegen & Kollisionen mit Gegner überprüfen -> schaden
    public void handleProjectiles(float dt, ArrayList<Projectile> projectiles, ArrayList<Enemy> enemies) {

        for(Projectile projectile:projectiles){
            //System.out.println(p.getX() + "  " + p.getY());
            //weite die das Projektil geflogen ist wird aktualisiert
            if(projectile.getDistance() < projectile.getRange()){
                projectile.setDistance(projectile.getDistance() + projectile.getSpeed() * dt);
            }
            //wenn die distanz größer als die reichweite ist wird das projektil entfernt
            if(projectile.getDistance() >= projectile.getRange()) {
                world.removeGameObject(projectile);
            }else {
                //projektil fliegt in richtung des zieles
                projectile.setX(projectile.getX() + projectile.getDir().getCoords()[0] * projectile.getSpeed() * dt);
                projectile.setY(projectile.getY() + projectile.getDir().getCoords()[1] * projectile.getSpeed() * dt);

                for(Enemy enemy:enemies){
                    //falls projektil mit gegner kollidiert und der gegner noch nicht getroffen ist
                    if(Utility.gameObjectIsCollidingWithGameObject(projectile,enemy) && !projectile.getHitEnemies().contains(enemy)){
                        //verringere die hp des gegners, verringere die hp des projektils, füge gegner zu getroffenen hinzu
                        enemy.addHp(-projectile.getImpactDamage());
                        projectile.addHp(-1);
                        projectile.addToHitEnemies(enemy);
                        //falls hp des projektils == 0 ist
                        if(projectile.getHp()==0){
                            //lösche das projektil
                            //wenn projektil gift, dann fette giftwolke
                            if(projectile.getProjectileType() == ProjectileType.POISON){
                                Random random = new Random();
                                for(int w = 0; w < 15 ; w++){
                                    float alpha = random.nextFloat()*(float)(Math.PI * 2);
                                    float distance = random.nextFloat()*1.5f;
                                    float y = projectile.getY()-(float)(Math.sin(alpha) * distance);
                                    float x = projectile.getX()-(float)(Math.cos(alpha) * distance);
                                    world.spawnProjectile(new Projectile(ProjectileType.POISONTRAIL,projectile.getLevel(),x,y,projectile.getDir()));
                                }
                            }
                            world.removeGameObject(projectile);

                        }
                    }
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

}

