package projectile;

import enemy.Enemy;
import utility.Constants;
import utility.Vector2;
import world.World;

import java.util.ArrayList;
import java.util.Random;

import static utility.Utility.random;

public class ProjectileHandler {

    private World world;

    public ProjectileHandler(World world) {
        this.world = world;
    }

    // Projektile bewegen & Kollisionen mit Gegner überprüfen -> schaden
    public void handleProjectiles(float dt, ArrayList<Projectile> projectiles) {

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);


            //System.out.println(p.getX() + "  " + p.getY());
            //weite die das Projektil geflogen ist wird aktualisiert
            projectile.setDistance(projectile.getDistance() + projectile.projectileType.speed * dt);
            //wenn die distanz größer als die reichweite ist wird das projektil entfernt
            //projektil fliegt in richtung des zieles
            projectile.setX(projectile.getX() + projectile.getDir().getCoords()[0] * projectile.projectileType.speed * dt);
            projectile.setY(projectile.getY() + projectile.getDir().getCoords()[1] * projectile.projectileType.speed * dt);

            for (Enemy enemy : findCollidingEnemies(projectile)) {
                if(!projectile.hasHitEnemy(enemy)) {
                    enemy.addHp(-projectile.projectileType.impactDamage);
                    projectile.addToHitEnemies(enemy);
                    spawnEffects(projectile);

                    if (projectile.projectileType.effectType != null) {
                        enemy.addEffect(projectile.projectileType.effectType);
                    } else if (projectile.projectileType == ProjectileType.LIGHTNING) {
                        projectile.setDistance(projectile.getDistance()/2);
                        projectile.getDir().rotate((float)(random.nextDouble()*Math.PI*2));
                    }

                    projectile.addHp(-1);
                    if (projectile.getHp() <= 0) {
                        break;
                    }
                }
            }

            if(!world.inWorld(projectile) || projectile.getHp() <= 0 || projectile.getDistance() >= projectile.projectileType.range){
                spawnEffects(projectile);
                world.removeProjectile(projectile);
                i--;
            }
        }
    }

    private void spawnEffects(Projectile projectile){
        if(projectile.getHp() > 0) {
            switch (projectile.projectileType) {
                case POISON:
                    spawnPoisonCloud(projectile.getX(), projectile.getY(), projectile.getLevel(), Constants.poisonCloudAmount, projectile.getDir());
                    break;
                case FRAGGRENADE:
                    world.spawnProjectile(new Projectile(ProjectileType.EXPLOSION, projectile.getLevel(), projectile.getX(), projectile.getY(), projectile.getDir()));
                    break;
            }
        }
    }

    private ArrayList<Enemy> findCollidingEnemies(Projectile projectile){
        ArrayList<Enemy> enemies = new ArrayList<>();
        float radius = projectile.projectileType.radius;
        for (int i = Math.max(0,(int) Math.floor(projectile.getX() - radius)); i < Math.min(world.getBlocks().length,Math.ceil(projectile.getX()+radius) + 1); i++) {
            for (int j = Math.max(0,(int) Math.floor(projectile.getY()-radius)); j < Math.min(world.getBlocks()[i].length,Math.ceil(projectile.getY()+radius) + 1); j++) {
                for(Enemy enemy:world.getBlocks()[i][j]){
                    float distance = (float)(Math.sqrt(Math.pow(projectile.getX()-enemy.getX(),2) + Math.pow(projectile.getY()-enemy.getY(),2)));
                    if(distance <= enemy.enemyType.getRadius() + radius){
                        enemies.add(enemy);
                    }
                }
            }
        }

        return enemies;
    }

    private void spawnPoisonCloud(float xPos, float yPos, int level, int amount, Vector2 direction) {
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            float alpha = random.nextFloat() * (float) (Math.PI * 2);
            float distance = random.nextFloat() * 1.5f;
            float x = xPos + (float) (Math.cos(alpha) * distance);
            float y = yPos + (float) (Math.sin(alpha) * distance);
            world.spawnProjectile(new Projectile(ProjectileType.POISONTRAIL, level, x, y, direction));
        }
    }

}

