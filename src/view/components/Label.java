package view.components;

import view.View;


public class Label extends ViewComponent{

    private String text;

    public Label(float x, float y, float width, float height, View v, String text) {
        super(x, y, width, height, v);
        this.text = text;

    }


}
