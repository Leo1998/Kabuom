package view.components;

import utility.Utility;
import view.ITexture;
import view.Texture;
import view.View;


public class Button extends ViewComponent{

    private String buttontext;
    private Texture mainTexture,pressedTexture, texture;

    public Button(float x, float y, float width, float height, View v, String buttontext) {
        super(x, y, width, height, v);
        this.buttontext = buttontext;
        mainTexture = new Texture(getClass().getResource("/textures/viewTextures/mainButton"));
        pressedTexture = new Texture(getClass().getResource("/textures/viewTextures/pressedButton"));
        texture = mainTexture;
    }

    public boolean buttonPressed(){
        if(new Utility().viewComponentIsCollidingWithMouse(this)){
            texture= pressedTexture;
            return true;
        }else{
            texture = mainTexture;
            return false;
        }
    }

    public Texture getTexture(){
        return texture;
    }

    public String getButtontext(){
        return buttontext;
    }

    public void setButtontext(String buttontext){
        this.buttontext = buttontext;
    }
}
