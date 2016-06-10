package view.components;


import view.View;

public abstract class ViewComponent {

    private View view;
    private float x,y,width,height;



    public ViewComponent(float x, float y, float width,float height, View v){
        view = v;
    }

    public View getView() {return view;}

    public float getX() {return x;}

    public float getY() {return y;}

    public float getWidth() {return width;}

    public float getHeight() {return height;}

}
