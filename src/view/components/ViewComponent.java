package view.components;


import view.View;
import view.rendering.Batch;
import view.rendering.ITexture;

public abstract class ViewComponent {

    private View view;
    private float x,y,width,height;
    private ITexture texture;



    public ViewComponent(float x, float y, float width,float height, View v){
        view = v;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Batch batch){}

    public ITexture getTexture() {return texture;}

    public View getView() {return view;}

    public float getX() {return x;}

    public float getY() {return y;}

    public float getWidth() {return width;}

    public float getHeight() {return height;}

}
