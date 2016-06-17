package view.components;

import utility.Utility;
import view.View;
import view.ViewManager;
import view.rendering.Batch;


public class Label extends ViewComponent{

    private String text;

    public Label(float x, float y, float width, float height, View v, String text) {
        super(x, y, width, height, v);
        this.text = text;
    }

    @Override
    public void draw(Batch batch) {
        ViewManager.font.drawText(batch, text , (int)(Utility.layoutX(getX())+ Utility.layoutX(getWidth())/2 - ViewManager.font.getWidth(text)/2),(int) (Utility.layoutY(getY()) + Utility.layoutY(getHeight())/2- ViewManager.font.getLineHeight()/2));
    }

}
