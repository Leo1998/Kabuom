package view.effects;

import view.rendering.Batch;
import view.texture.ITexture;

public abstract class PostProcessingEffect {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }

    public abstract void render(ITexture sceneTexture, Batch batch, float totalTime);

    public abstract void dispose();

}
