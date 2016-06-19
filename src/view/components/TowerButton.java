package view.components;

import tower.TowerType;
import view.View;
import view.rendering.Batch;
import view.rendering.ITexture;

/**
 * Created by Hinke on 17.06.2016.
 */
public class TowerButton extends Button{

    private TowerType towerType;
    public TowerButton(float x, float y, float width, float height, View v, String buttontext, ITexture mainTexture, ITexture pressedTexture, TowerType towerType) {
        super(x, y, width, height, v, buttontext, mainTexture, pressedTexture);
        this.towerType = towerType;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }

    @Override
    public boolean buttonPressed() {
        return super.buttonPressed();
    }

    public TowerType getTowerType(){
        return towerType;
    }
}
