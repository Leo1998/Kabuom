package view.components;

import tower.TowerType;
import view.View;
import view.ViewManager;
import view.rendering.Batch;
import view.rendering.ITexture;

public class TowerButton extends Button {

    private TowerType towerType;
    private ITexture icon;
    private String description;

    public TowerButton(float x, float y, float width, float height, View v, String buttontext, TowerType towerType) {
        super(x, y, width, height, v, buttontext);
        this.towerType = towerType;
        this.icon = ViewManager.getTexture(towerType.getTextureID());
        this.description = towerType.getName() + " (" + towerType.getCost() + ")";
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        float iconSize = (getHeight()) - ViewManager.font.getLineHeight();

        batch.draw(icon, (getX() + (getWidth() / 2) - (iconSize / 2)), (getY()), iconSize, iconSize, iconSize / 2, iconSize / 2, (float) Math.toRadians(0), 1f, 1f, 1f, 1f);

        ViewManager.font.drawText(batch, description, (int) ((getX()) + (getWidth()) / 2 - ViewManager.font.getWidth(description) / 2), (int) ((getY()) + iconSize));
    }

    public TowerType getTowerType() {
        return towerType;
    }
}
