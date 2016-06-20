package view.rendering;

public abstract class PostProcessingEffect {

    public abstract void render(ITexture sceneTexture, Batch batch, float totalTime);

    public abstract void dispose();

}
