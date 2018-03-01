package projectile;

import entity.model.Entity;
import entity.model.EntityType;
import utility.Constants;
import utility.Vector2;
import world.World;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import static utility.Utility.random;

public class ProjectileHandler {

    private World world;

    public ProjectileHandler(World world) {
        this.world = world;
    }

    // Projektile bewegen & Kollisionen mit Gegner überprüfen -> schaden
    public void handleProjectiles(float dt, LinkedList<Projectile> projectiles) {

        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();

            float moveBy = Math.min(projectile.getRadius()*2,projectile.getSpeed()*dt);
            float total = 0;
            float max = projectile.getSpeed()*dt;

            while (total < max) {
                if(total + moveBy >= max){
                    moveBy = max - total;
                    total = max;
                } else {
                    total += moveBy;
                }

                projectile.setX(projectile.getX() + projectile.getDir().getCoords()[0] * moveBy);
                projectile.setY(projectile.getY() + projectile.getDir().getCoords()[1] * moveBy);

                for (Entity entity : findCollidingEnemies(projectile)) {
                    if (!projectile.addToHitEntities(entity)) {
                        entity.addHp(-projectile.getDamage(), projectile.source);

                        if (projectile.getEffect() != null) {
                            entity.addEffect(projectile.getEffect());
                        }

                        if (projectile.getAbility() == ProjectileType.Ability.RANDOMROTATION) {
                            projectile.setDistance(projectile.getDistance() / 2);
                            projectile.getDir().rotate((float) (random.nextGaussian() * Math.PI));
                        } else if(projectile.getAbility() != ProjectileType.Ability.NULL) {
                            projectile.setHp(-1);
                        }

                        projectile.addHp(-1);
                        if (projectile.getHp() <= 0) {
                            break;
                        }
                    }
                }
            }

            projectile.setDistance(projectile.getDistance() + projectile.getSpeed() * dt);
            if(!world.inWorld(projectile) || projectile.getHp() <= 0 || projectile.getDistance() >= projectile.getRange()){
                iterator.remove();
                spawnEffects(projectile);
            }
        }
    }

    private void spawnEffects(Projectile projectile){
        switch (projectile.getAbility()) {
            case POISONCLOUD:
                spawnPoisonCloud(projectile.getX(), projectile.getY(), projectile.getLevel(), Math.round(projectile.getMaxHp()), projectile.getDir(), projectile.isEnemy(), projectile.source);
                break;
            case EXPLOSION:
                world.spawnProjectile(new Projectile(ProjectileType.EXPLOSION, projectile.getLevel(), projectile.getX(), projectile.getY(), projectile.getDir(), projectile.isEnemy(), projectile.source));
                break;
        }
    }

    private LinkedList<Entity> findCollidingEnemies(Projectile projectile){
        LinkedList<Entity> entities = new LinkedList<>();
        float radius = projectile.getRadius();
        for (int i = Math.max(0,(int) Math.floor(projectile.getX() - radius)); i < Math.min(world.getBlocks().length,Math.ceil(projectile.getX()+radius) + 1); i++) {
            for (int j = Math.max(0,(int) Math.floor(projectile.getY()-radius)); j < Math.min(world.getBlocks()[i].length,Math.ceil(projectile.getY()+radius) + 1); j++) {
                for(Entity entity:world.getBlocks()[i][j]){
                    if(projectile.hits(entity)) {
                        float distance = (float) (Math.sqrt(Math.pow(projectile.getX() - entity.getX(), 2) + Math.pow(projectile.getY() - entity.getY(), 2)));
                        if (distance <= entity.getRadius() + radius) {
                            entities.add(entity);
                        }
                    }
                }
            }
        }

        return entities;
    }

    private void spawnPoisonCloud(float xPos, float yPos, int level, int amount, Vector2 direction, boolean isEnemy, String source) {
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            float alpha = random.nextFloat() * (float) (Math.PI * 2);
            float distance = random.nextFloat() * 1.5f;
            float x = xPos + (float) (Math.cos(alpha) * distance);
            float y = yPos + (float) (Math.sin(alpha) * distance);
            world.spawnProjectile(new Projectile(ProjectileType.POISONTRAIL, level, x, y, direction, isEnemy, source));
        }
    }

}

