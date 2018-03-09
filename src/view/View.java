package view;

import org.lwjgl.input.Mouse;
import view.components.ViewComponent;
import view.effects.PostProcessingEffect;
import view.effects.PostProcessingManager;
import view.math.Camera;
import view.math.Vector2;
import view.rendering.Batch;

import java.util.ArrayList;
import java.util.List;

public abstract class View {

    protected List<ViewComponent> components;
    protected int originWidth, originHeight;

    private PostProcessingManager postProcessingManager;
    private final List<PostProcessingEffect> postProcessingEffects = new ArrayList<>();
    final ViewManager viewManager;

    private Camera uiCamera;
    private Camera sceneCamera;

    public View(int width, int height, ViewManager viewManager) {
        components = new ArrayList<>();
        originHeight = height;
        originWidth = width;
        this.viewManager = viewManager;

        this.sceneCamera = new Camera(width, height);
        this.uiCamera = new Camera(width, height);
        layout(width, height);
    }

    public final void render(float deltaTime, Batch batch) {
        if (!postProcessingEffects.isEmpty() && postProcessingManager == null) {
            postProcessingManager = new PostProcessingManager(batch);
            postProcessingManager.init(originWidth, originHeight, postProcessingEffects);
        }

        if (postProcessingManager != null)
            postProcessingManager.begin(deltaTime);

        batch.begin(sceneCamera);
        renderScene(deltaTime, batch);
        batch.end();

        if (postProcessingManager != null)
            postProcessingManager.end(sceneCamera);

        batch.begin(uiCamera);
        renderUI(batch);
        batch.end();
    }

    public void renderUI(Batch batch) {
        for (ViewComponent v : components) {
            v.draw(batch, originWidth, originHeight);
        }
    }

    public void renderScene(float deltaTime, Batch batch) {
    }

    public void layout(int width, int height) {
        originHeight = height;
        originWidth = width;

        this.sceneCamera.resize(width, height);
        this.sceneCamera.setTranslation(new Vector2(width / 2, height / 2));
        this.uiCamera.resize(width, height);
        this.uiCamera.setTranslation(new Vector2(width / 2, height / 2));

        if (postProcessingManager != null)
            postProcessingManager.init(width, height, postProcessingEffects);
    }

    public void addEffect(PostProcessingEffect effect) {
        postProcessingEffects.add(effect);

        if (postProcessingManager != null)
            postProcessingManager.init(originWidth, originHeight, postProcessingEffects);
    }

    public void removeEffect(PostProcessingEffect effect) {
        postProcessingEffects.remove(effect);

        if (postProcessingManager != null)
            postProcessingManager.init(originWidth, originHeight, postProcessingEffects);
    }

    public void onStart() {

    }

    public void onStop() {
        for(PostProcessingEffect e : postProcessingEffects) {
            e.dispose();
        }
        postProcessingEffects.clear();

        if (postProcessingManager != null)
            postProcessingManager.init(originWidth, originHeight, postProcessingEffects);
    }

    public List<ViewComponent> getComponents() {
        return components;
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    public Camera getUiCamera() {
        return uiCamera;
    }

    public Camera getSceneCamera() {
        return sceneCamera;
    }

    public void onKeyDown(int key, char c) {
        for (ViewComponent v : components) {
            v.onKeyDown(key, c);
        }
    }

    public void onKeyUp(int key, char c) {
        for (ViewComponent v : components) {
            v.onKeyUp(key, c);
        }
    }

    public void onMouseDown(int button, int mouseX, int mouseY) {
        for (ViewComponent v : components) {
            v.onMouseDown(button, getMouseX(), getMouseY());
        }
    }

    public void onMouseUp(int button, int mouseX, int mouseY) {
        for (ViewComponent v : components) {
            v.onMouseUp(button, getMouseX(), getMouseY());
        }
    }

    public float getMouseX() {
        return Mouse.getX() / (float)originWidth;
    }

    public float getMouseY() {
        return 1 - Mouse.getY() / (float)originHeight;
    }
}
