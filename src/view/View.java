package view;

import org.lwjgl.input.Keyboard;
import view.components.ViewComponent;

public abstract class View {

    private ViewComponent[] components;

    public View(){

    }
    public void render(float deltaTime, Batch batch){}

    public void layout(float width, float height){}

    public ViewComponent[] getComponents(){return components;}

    public void onKeyDown(int key,char c){

    }

}
