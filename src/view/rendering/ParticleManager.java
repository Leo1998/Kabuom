package view.rendering;

import org.lwjgl.opengl.Display;

import java.awt.*;
import java.util.Random;

public class ParticleManager {

    public class Particle {
        private float x;
        private float y;
        private float size;
        private float r, g, b, a;

        public Particle(float x, float y, float size, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.r = color.getRed();
            this.g = color.getGreen();
            this.b = color.getBlue();
            this.a = color.getAlpha();
        }
    }

    private Particle[] particles;

    public ParticleManager(int maxParticles) {
        this.particles = new Particle[maxParticles];

        Random random = new Random();
        for (int i = 0; i < particles.length; i++)
            particles[i] = new Particle(random.nextInt(Display.getWidth()), random.nextInt(Display.getHeight()), 4, Color.GREEN);
    }

    public void render(Batch batch) {
        for (int i = 0; i < particles.length; i++) {
            Particle p = particles[i];

            batch.draw(null, p.x, p.y, p.size, p.size);
        }
    }

}
