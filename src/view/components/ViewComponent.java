package view.components;


import view.View;
import view.rendering.Batch;

public abstract class ViewComponent {

    protected View view;
    private float x, y, width, height;

    public ViewComponent(float x, float y, float width, float height, View v) {
        view = v;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Batch batch, float originWidth, float originHeight);

    public View getView() {
        return view;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void onKeyDown(int key, char c) {
    }

    public void onKeyUp(int key, char c) {
    }

    public void onMouseDown(int button, float mouseX, float mouseY) {
    }

    public void onMouseUp(int button, float mouseX, float mouseY) {
    }
}
