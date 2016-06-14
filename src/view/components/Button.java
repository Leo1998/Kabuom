package view.components;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import utility.Utility;
import view.rendering.Batch;
import view.rendering.BitmapFont;
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

        batch.draw(getTexture(),Utility.layoutX(getX()),Utility.layoutY(getY()),Utility.layoutX(getWidth()),Utility.layoutY(getHeight()));
        ViewManager.font.drawText(batch, buttontext , (int)(Utility.layoutX(getX())+ Utility.layoutX(getWidth())/2 - ViewManager.font.getWidth(buttontext)/2),(int) (Utility.layoutY(getY()) + Utility.layoutY(getHeight())/2- ViewManager.font.getLineHeight()/2));
    }

    public ITexture getTexture() {return texture;}

    public String getButtontext(){
        return buttontext;
    }

    public void setButtontext(String buttontext){
        this.buttontext = buttontext;
    }
}
