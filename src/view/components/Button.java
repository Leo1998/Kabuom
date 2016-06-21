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
    private ITexture texture,buttonMainTexture,buttonPressedTexture;
    private boolean gotClicked;
    public Button(float x, float y, float width, float height, View v, String buttontext,ITexture mainTexture, ITexture pressedTexture) {
        super(x, y, width, height, v);
        this.buttontext = buttontext;
        buttonMainTexture = mainTexture;
        buttonPressedTexture =  pressedTexture;
        texture = buttonMainTexture;
        gotClicked = false;

    }

    public boolean buttonPressed(){
        if( Utility.viewComponentIsCollidingWithMouse(this, Mouse.getX(), Display.getHeight() - Mouse.getY()) ){

            if (Mouse.isButtonDown(0)) {
                texture= buttonPressedTexture;
                gotClicked = true;
                return false;
            }else {
                if (gotClicked) {
                    gotClicked = false;
                    texture = buttonMainTexture;
                    return true;
                } else {

                }
            }
            return false;
        }else{

            texture = buttonMainTexture;
            gotClicked = false;
            return false;
        }

    }

    @Override
    public void draw(Batch batch) {
        batch.draw(getTexture(),(getX()),(getY()),(getWidth()),(getHeight()),getWidth()/2,getHeight()/2,(float) Math.toRadians(270),1f,1f,1f,1f);
        if(buttontext != null)
        ViewManager.font.drawText(batch, buttontext , (int)((getX())+ (getWidth())/2 - ViewManager.font.getWidth(buttontext)/2),(int) ((getY()) + (getHeight())/2- ViewManager.font.getLineHeight()/2));
    }

    public ITexture getTexture() {return texture;}

    public String getButtontext(){
        return buttontext;
    }

    public void setButtontext(String buttontext){
        this.buttontext = buttontext;
    }


}
