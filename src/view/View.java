package view;

import org.lwjgl.opengl.Display;
import view.components.ViewComponent;
import view.rendering.Batch;


import java.util.ArrayList;
import java.util.List;

public abstract class View {

    protected List<ViewComponent> components;
    protected float originWidth,originHeight;

    public View(float width,float height) {
        components = new ArrayList<ViewComponent>();
        originWidth = Display.getWidth();
        originHeight = Display.getHeight();
    }

    public void render(float deltaTime, Batch batch){
        for(int i = 0; i < components.size(); i++){
            ViewComponent temp = components.get(i);
            components.get(i).draw(batch);
        }
    }


    public void layout(float width, float height){
        originHeight = height;
        originWidth = width;
    }
    public List<ViewComponent> getComponents(){return components;}

    public abstract void onKeyDown(int key,char c);
    public void onMouseDown(int button, int mouseX, int mouseY){

    }


}
