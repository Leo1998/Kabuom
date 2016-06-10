package view;

import view.components.ViewComponent;
import view.rendering.Batch;


import java.util.ArrayList;
import java.util.List;

public abstract class View {

    protected List<ViewComponent> components;

    public View(){
        components = new ArrayList<ViewComponent>() ;
    }

    public void render(float deltaTime, Batch batch){
        for(int i = 0; i < components.size(); i++){
            ViewComponent temp = components.get(i);
            components.get(i).draw(batch);
        }
    }

    public void layout(float width, float height){}

    public List<ViewComponent> getComponents(){return components;}

    public void onKeyDown(int key,char c){

    }

}
