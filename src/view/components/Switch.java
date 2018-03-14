package view.components;

import utility.Utility;
import view.View;
import view.ViewManager;
import view.rendering.Batch;
import view.texture.ITexture;

public class Switch extends Button {

    public Switch(float x, float y, float width, float height, View v, String buttontext) {
        this(x, y, width, height, v, buttontext, ViewManager.getTexture("viewTextures/mainButton.png"), ViewManager.getTexture("viewTextures/pressedButton.png"));
    }

    public Switch(float x, float y, float width, float height, View v, String buttontext, ITexture mainTexture, ITexture pressedTexture) {
        super(x, y, width, height, v, buttontext, mainTexture, pressedTexture);
    }

    public void setDown(boolean down){
        this.down = down;
    }

    @Override
    public void onMouseDown(int button, float mouseX, float mouseY) {
        if (button == 0 && Utility.viewComponentIsCollidingWithMouse(this, mouseX, mouseY)) {
            down = !down;
            ViewManager.playSound(ViewManager.SoundID.CLICK);
            if(getListener() != null) {
                getListener().onClick();
            }
        }
    }

    @Override
    public void onMouseUp(int button, float mouseX, float mouseY) {
    }

    @Override
    public void draw(Batch batch, float originWidth, float originHeight) {
        float x = getX() * originWidth;
        float width = getWidth() * originWidth;
        float y = getY() * originHeight;
        float height = getHeight() * originHeight;

        batch.draw(buttonMainTexture, x, y, width, height, 0, 1f, 1f, 1f, 1f);

        batch.draw(buttonPressedTexture,x + (down ? width*0.5f:0f),y,width*0.5f,height,0,1f,1f,1f,1f);

        if (buttontext != null) {
            String s = buttontext + ": " + (down ? "ON":"OFF");
            ViewManager.font.drawText(batch, s, (int) (x + width / 2 - ViewManager.font.getWidth(s) / 2), (int) (y + height / 2 - ViewManager.font.getLineHeight() / 2));
        }
    }
}
