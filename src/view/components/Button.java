package view.components;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import utility.Utility;
import view.rendering.Batch;
import view.rendering.ITexture;
import view.View;
import view.ViewManager;


public class Button extends ViewComponent{

    private String buttontext;
    private ITexture texture;

    public Button(float x, float y, float width, float height, View v, String buttontext) {
        super(x, y, width, height, v);
        this.buttontext = buttontext;
        texture = ViewManager.buttonMainTexture;

    }

    public boolean buttonPressed(){
        if(new Utility().viewComponentIsCollidingWithMouse(this, Mouse.getX(), Display.getHeight() - Mouse.getY()) && Mouse.isButtonDown(0)){
            texture= ViewManager.buttonPressedTexture;
            return true;
        }else{
            texture = ViewManager.buttonMainTexture;
            return false;
        }
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        batch.draw(getTexture(),getX(),getY(),getWidth(),getHeight());
        ViewManager.font.drawText(batch, buttontext , (int)getX(), (int)(getY() + getHeight()/4));
    }

    public ITexture getTexture() {return texture;}

    public String getButtontext(){
        return buttontext;
    }

    public void setButtontext(String buttontext){
        this.buttontext = buttontext;
    }
}
