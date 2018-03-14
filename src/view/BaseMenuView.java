package view;

import controller.Controller;
import utility.Utility;
import view.effects.Particle;
import view.effects.ParticleEffect;
import view.effects.ParticleManager;
import view.rendering.Batch;

public abstract class BaseMenuView extends View {

    private float timer = 0f;

    public BaseMenuView(int width, int height, ViewManager viewManager) {
        super(width, height, viewManager);
    }

    @Override
    public void renderScene(float deltaTime, Batch batch) {
        if (Controller.instance.getConfig().isMenuConfetti()) {
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
}
