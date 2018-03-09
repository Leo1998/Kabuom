package view;

import org.lwjgl.input.Mouse;
import view.components.ViewComponent;
import view.effects.PostProcessingEffect;
import view.rendering.Batch;

import java.util.ArrayList;
import java.util.List;

public abstract class View {

    protected List<ViewComponent> components;
    protected float originWidth, originHeight;
    protected final List<PostProcessingEffect> postProcessingEffects = new ArrayList<>();
    protected final ViewManager viewManager;

    public View(float width, float height, ViewManager viewManager) {
        components = new ArrayList<>();
        originHeight = height;
        originWidth = width;
        this.viewManager = viewManager;
    }

    /**
     * zeichnet alle ViewComponents
     */
    public void render(float deltaTime, Batch batch) {
        for (ViewComponent v : components) {
            v.draw(batch, originWidth, originHeight);
        }
    }


    public void layout(float width, float height) {
        originHeight = height;
        originWidth = width;
    }

    public void addEffect(PostProcessingEffect effect) {
        postProcessingEffects.add(effect);
        viewManager.postProcessingManager.addEffect(effect);
    }

    public void removeEffect(PostProcessingEffect effect) {
        postProcessingEffects.remove(effect);
        viewManager.postProcessingManager.removeEffect(effect);
    }

    public void onStart() {

    }

    public void onStop() {
        for (PostProcessingEffect e : postProcessingEffects) {
            viewManager.postProcessingManager.removeEffect(e);
        }
        postProcessingEffects.clear();
    }

    public List<ViewComponent> getComponents() {
        return components;
    }

    public ViewManager getViewManager() {
        return viewManager;
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
        return Mouse.getX() / originWidth;
    }

    public float getMouseY() {
        return 1 - Mouse.getY() / originHeight;
    }
}
