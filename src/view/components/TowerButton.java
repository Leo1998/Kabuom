package view.components;

import view.View;
import view.rendering.Batch;
import view.rendering.ITexture;

/**
 * Created by Hinke on 17.06.2016.
 */
public class TowerButton extends Button{

    public TowerButton(float x, float y, float width, float height, View v, String buttontext, ITexture mainTexture, ITexture pressedTexture) {
        super(x, y, width, height, v, buttontext, mainTexture, pressedTexture);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }

    @Override
    public boolean buttonPressed() {
        return super.buttonPressed();
    }
}
