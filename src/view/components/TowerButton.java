package view.components;

import entity.model.EntityType;
import view.View;
import view.ViewManager;
import view.rendering.Batch;
import view.texture.ITexture;

public class TowerButton extends Button {

    private EntityType entityType;
    private ITexture icon1, icon2;
    private String description;

    public TowerButton(float x, float y, float width, float height, View v, String buttontext, EntityType entityType) {
        super(x, y, width, height, v, buttontext);
        this.entityType = entityType;
        this.icon1 = ViewManager.getTexture(entityType.baseTexture);
        this.icon2 = ViewManager.getTexture(entityType.turretTexture);
        this.description = entityType.getName() + " (" + entityType.cost + ")";
    }

    @Override
    public void draw(Batch batch, float originWidth, float originHeight) {

        float x = getX() * originWidth;
        float width = getWidth() * originWidth;
        float y = getY() * originHeight;
        float height = getHeight() * originHeight;

        batch.draw(getTexture(), x, y, width, height, 0, 1f, 1f, 1f, 1f);

        float iconSize = height - ViewManager.font.getLineHeight();

        if (icon1 != null) {
            batch.draw(icon1, (x + (width / 2) - (iconSize / 2)), y, iconSize, iconSize, 0, 1f, 1f, 1f, 1f);
        }
        if (icon2 != null) {
            batch.draw(icon2, (x + (width / 2) - (iconSize / 2)), y, iconSize, iconSize, 0, 1f, 1f, 1f, 1f);
        }

        ViewManager.font.drawText(batch, description, (int) (x + width / 2 - ViewManager.font.getWidth(description) / 2), (int) (y + iconSize));
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
