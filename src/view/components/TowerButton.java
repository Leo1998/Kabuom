package view.components;

import tower.TowerType;
import view.View;
import view.ViewManager;
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

    public void draw(Batch batch,float height){
        batch.draw(getTexture(),(getX()),(height),(getWidth()),(getHeight()),getWidth()/2,getHeight()/2,(float) Math.toRadians(270),1f,1f,1f,1f);
    }

    public TowerType getTowerType(){
        return towerType;
    }
}
