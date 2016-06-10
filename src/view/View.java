package view;

import view.components.ViewComponent;
import view.rendering.Batch;

import java.util.List;

public abstract class View {

    protected List<ViewComponent> components;

    public View(){}
    public void render(float deltaTime, Batch batch){}

    public void layout(float width, float height){}

    public List<ViewComponent> getComponents(){return components;}

    public void onKeyDown(int key,char c){

    }

}
