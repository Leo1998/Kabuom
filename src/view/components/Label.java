package view.components;

import view.View;
import view.ViewManager;
import view.rendering.Batch;


public class Label extends ViewComponent {

    private String text;

    public Label(float x, float y, float width, float height, View v, String text) {
        super(x, y, width, height, v);
        this.text = text;
    }

    @Override
    public void draw(Batch batch, float originWidth, float originHeight) {
        float x = getX()*originWidth;
        float width = getWidth()*originWidth;
        float y = getY()*originHeight;
        float height = getHeight()*originHeight;

        ViewManager.font.drawText(batch, text, (int) (x + width / 2 - ViewManager.font.getWidth(text) / 2), (int) (y + height / 2 - ViewManager.font.getLineHeight() / 2));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
