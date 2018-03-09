package view.components;

import utility.Utility;
import view.View;
import view.ViewManager;
import view.rendering.Batch;
import view.rendering.ITexture;

public class IndexedButton extends Button {
    private int index;

    public IndexedButton(float x, float y, float width, float height, View v, String buttontext, int index) {
        this(x, y, width, height, v, buttontext, index, ViewManager.getTexture("viewTextures/mainButton.png"), ViewManager.getTexture("viewTextures/pressedButton.png"));
    }

    public IndexedButton(float x, float y, float width, float height, View v, String buttontext, int index, ITexture mainTexture, ITexture pressedTexture) {
        super(x, y, width, height, v, buttontext, mainTexture, pressedTexture);
        this.index = index;
    }

    @Override
    public void draw(Batch batch, float originWidth, float originHeight) {
        float x = getX()*originWidth;
        float width = getWidth()*originWidth;
        float y = getY()*originHeight;
        float height = getHeight()*originHeight;

        batch.draw(getTexture(), x, y, width, height, 0, 1f, 1f, 1f, 1f);

        ViewManager.font.drawText(batch, buttontext, (int) (x + width / 2 - ViewManager.font.getWidth(buttontext) / 2), (int) (y + height / 2 - ViewManager.font.getLineHeight()));
        String iString = Utility.niceNumber(index);
        ViewManager.font.drawText(batch, iString, (int) (x + width / 2 - ViewManager.font.getWidth(iString) / 2), (int) (y + height / 2));
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }
}
