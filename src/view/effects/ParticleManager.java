package view.effects;

import view.rendering.Batch;

import java.util.ArrayList;

public class ParticleManager {

    private ArrayList<Particle> particles;

    public ParticleManager(int maxParticles) {
        this.particles = new ArrayList<>();
    }

    public void emit(ParticleEffect effect) {
        for (int i = 0; i < effect.getCount(); i++) {
            Particle p = new Particle(effect.getX(), effect.getY(), effect.getDir(), effect.getSpeed(), effect.getSize(), effect.getLifeTime(), effect.getColor(), effect.getRandomizer());

            particles.add(p);
        }
    }

    public void render(Batch batch, float deltaTime) {
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);

            if (p.isAlive()) {
                p.update(deltaTime);

                batch.draw(null, p.getX(), p.getY(), p.getSize(), p.getSize(), p.getR(), p.getG(), p.getB(), p.getA());
            } else {
                particles.remove(p);
            }
        }
    }

    public void clearParticles() {
        particles.clear();
    }

}
