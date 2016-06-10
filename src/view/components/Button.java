package view.components;

import utility.Utility;
import view.ITexture;
import view.Texture;
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
        if(new Utility().viewComponentIsCollidingWithMouse(this)){
            texture= ViewManager.buttonPressedTexture;
            return true;
        }else{
            texture = ViewManager.buttonMainTexture;
            return false;
        }
    }

    public ITexture getTexture(){
        return texture;
    }

    public String getButtontext(){
        return buttontext;
    }

    public void setButtontext(String buttontext){
        this.buttontext = buttontext;
    }
}
