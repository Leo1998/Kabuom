package view.components;

import utility.Utility;
import view.View;
import view.ViewManager;
import view.rendering.Batch;
import view.rendering.ITexture;


public class Button extends ViewComponent {

    private ButtonListener listener;
    protected String buttontext;
    protected ITexture texture, buttonMainTexture, buttonPressedTexture;
    private boolean down;

    public Button(float x, float y, float width, float height, View v, String buttontext) {
        this(x, y, width, height, v, buttontext, ViewManager.getTexture("viewTextures/mainButton.png"), ViewManager.getTexture("viewTextures/pressedButton.png"));
    }

    public Button(float x, float y, float width, float height, View v, String buttontext, ITexture mainTexture, ITexture pressedTexture) {
        super(x, y, width, height, v);
        this.buttontext = buttontext;
        buttonMainTexture = mainTexture;
        buttonPressedTexture = pressedTexture;
        texture = buttonMainTexture;
        down = false;
    }

    @Override
    public void onMouseDown(int button, float mouseX, float mouseY) {
        super.onMouseDown(button, mouseX, mouseY);
        if (button == 0 && Utility.viewComponentIsCollidingWithMouse(this, mouseX, mouseY)) {
            down = true;
            texture = buttonPressedTexture;
        } else {
            texture = buttonMainTexture;
            down = false;
        }
    }

    @Override
    public void onMouseUp(int button, float mouseX, float mouseY) {
        super.onMouseUp(button, mouseX, mouseY);

        if (button == 0 && down) {
            texture = buttonMainTexture;
            down = false;

            if (listener != null && Utility.viewComponentIsCollidingWithMouse(this, mouseX, mouseY)) {
                ViewManager.clickSound.start();
                listener.onClick();
            }
        }
    }

    @Override
    public void draw(Batch batch, float originWidth, float originHeight) {
        float x = getX()*originWidth;
        float width = getWidth()*originWidth;
        float y = getY()*originHeight;
        float height = getHeight()*originHeight;

        batch.draw(getTexture(), x, y, width, height, 0, 1f, 1f, 1f, 1f);
        if (buttontext != null)
            ViewManager.font.drawText(batch, buttontext, (int) (x + width / 2 - ViewManager.font.getWidth(buttontext) / 2), (int) (y + height / 2 - ViewManager.font.getLineHeight() / 2));
    }

    public ButtonListener getListener() {
        return listener;
    }

    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }

    public ITexture getTexture() {
        return texture;
    }

    public String getButtontext() {
        return buttontext;
    }

    public void setButtontext(String buttontext) {
        this.buttontext = buttontext;
    }

    public boolean isDown() {
        return down;
    }
}
