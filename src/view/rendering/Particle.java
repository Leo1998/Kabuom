package view.rendering;

import java.awt.*;
import java.util.Random;

public class Particle {

    public interface ParticleRandomizer {
        public float randomizeDir(float dir);

        public float randomizeSpeed(float speed);

        public float randomizeSize(float size);

        public float randomizeLifeTime(float lifeTime);

        public Color randomizeColor(Color color);
    }

    public static final ParticleRandomizer identityRandomizer = new ParticleRandomizer() {
        @Override
        public float randomizeDir(float dir) {
            return dir;
        }

        @Override
        public float randomizeSpeed(float speed) {
            return speed;
        }

        @Override
        public float randomizeSize(float size) {
            return size;
        }

        @Override
        public float randomizeLifeTime(float lifeTime) {
            return lifeTime;
        }

        @Override
        public Color randomizeColor(Color color) {
            return color;
        }
    };

    private static final Random random = new Random();

    private float x;
    private float y;
    private float dir;
    private float speed;
    private float size;
    private Color color;
    private float life = 0;
    private float lifeTime;
    private ParticleRandomizer randomizer;

    public Particle(float x, float y, float dir, float speed, float size, float lifeTime, Color color, ParticleRandomizer randomizer) {
        this.x = x;
        this.y = y;
        this.dir = randomizer.randomizeDir(dir);
        this.speed = randomizer.randomizeSpeed(speed);
        this.size = randomizer.randomizeSize(size);
        this.lifeTime = randomizer.randomizeLifeTime(lifeTime);
        this.color = randomizer.randomizeColor(color);
        this.randomizer = randomizer;
    }

    public void update(float deltaTime) {
        this.life += deltaTime;

        float moveX = (float) Math.cos(Math.toRadians(dir));
        float moveY = (float) Math.sin(Math.toRadians(dir));

        this.x += moveX * deltaTime * speed;
        this.y += moveY * deltaTime * speed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLifeTime() {
        return lifeTime;
    }

    public boolean isAlive() {
        return life < lifeTime;
    }

    public float getR() {
        return color.getRed();
    }

    public float getG() {
        return color.getGreen();
    }

    public float getB() {
        return color.getBlue();
    }

    public float getA() {
        return color.getAlpha();
    }

    public float getSize() {
        return size;
    }
}