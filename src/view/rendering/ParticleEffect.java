package view.rendering;

import java.awt.*;

public class ParticleEffect {

    private int count;
    private int x;
    private int y;
    private float dir;
    private float speed;
    private float size;
    private float lifeTime;
    private float[] color;
    private Particle.ParticleRandomizer randomizer;

    public ParticleEffect(int count, int x, int y, float dir, float speed, float size, float lifeTime, float[] color, Particle.ParticleRandomizer randomizer) {
        this.count = count;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.speed = speed;
        this.size = size;
        this.lifeTime = lifeTime;
        this.color = color;
        this.randomizer = randomizer;
    }

    public int getCount() {
        return count;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getDir() {
        return dir;
    }

    public float getSpeed() {
        return speed;
    }

    public float getSize() {
        return size;
    }

    public float getLifeTime() {
        return lifeTime;
    }

    public float[] getColor() {
        return color;
    }

    public Particle.ParticleRandomizer getRandomizer() {
        return randomizer;
    }
}
