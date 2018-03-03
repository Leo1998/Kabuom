package view;

import utility.Utility;
import view.rendering.Batch;
import view.rendering.Particle;
import view.rendering.ParticleEffect;
import view.rendering.ParticleManager;

public abstract class BaseMenuView extends View {

    private float timer = 0f;

    public BaseMenuView(float width, float height, ViewManager viewManager) {
        super(width, height, viewManager);
    }

    @Override
    public void render(float deltaTime, Batch batch) {
        super.render(deltaTime, batch);

        timer += deltaTime;

        while (timer > 0.1f) {
            timer -= 0.1f;

            ParticleManager manager = this.getViewManager().getParticleManager();

            for (int i = 0; i <= 10; i++) {
                Particle.ParticleRandomizer randomizer = new Particle.ParticleRandomizer() {
                    @Override
                    public float randomizeDir(float dir) {
                        return (float) Utility.random.nextInt(90) + 270 - 45;
                    }

                    @Override
                    public float randomizeSpeed(float speed) {
                        return speed + Utility.random.nextFloat() * 10f - 5f;
                    }

                    @Override
                    public float randomizeSize(float size) {
                        return size;
                    }

                    @Override
                    public float randomizeLifeTime(float lifeTime) {
                        return lifeTime + Utility.random.nextFloat() * 3f - 1f;
                    }

                    @Override
                    public float[] randomizeColor(float[] color) {
                        return new float[]{Utility.random.nextFloat(), Utility.random.nextFloat(), Utility.random.nextFloat(), 0.55f};
                    }
                };

                float x = i * (originWidth / 10);
                float y = originHeight;
                ParticleEffect effect = new ParticleEffect(15, x, y, 0f, 50f, 4f, 6.0f, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, randomizer);

                manager.emit(effect);
            }
        }
    }

}
