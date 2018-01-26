package view.components;

import entity.model.EntityType;
import view.View;
import view.ViewManager;
import view.rendering.Batch;
import view.rendering.ITexture;

public class TowerButton extends Button {

    private EntityType entityType;
    private ITexture icon;
    private String description;

    public TowerButton(float x, float y, float width, float height, View v, String buttontext, EntityType entityType) {
        super(x, y, width, height, v, buttontext);
        this.entityType = entityType;
        this.icon = ViewManager.getTexture(entityType.getTextureId());
        this.description = entityType.getName() + " (" + entityType.cost + ")";
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        float iconSize = (getHeight()) - ViewManager.font.getLineHeight();

        batch.draw(icon, (getX() + (getWidth() / 2) - (iconSize / 2)), (getY()), iconSize, iconSize, (float) Math.toRadians(0), 1f, 1f, 1f, 1f);

        ViewManager.font.drawText(batch, description, (int) ((getX()) + (getWidth()) / 2 - ViewManager.font.getWidth(description) / 2), (int) ((getY()) + iconSize));
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
