package view.components;

import utility.Utility;
import view.View;


public class Button extends ViewComponent{

    public Button(float x, float y, float width, float height, View v) {
        super(x, y, width, height, v);
    }

    public boolean buttonPressed(){
        if(new Utility().viewCompenentIsCollidingWithMouse(this)){
            return true;
        }else{
            return false;
        }
    }
}
