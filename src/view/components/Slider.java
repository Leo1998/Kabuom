package view.components;

import org.lwjgl.input.Mouse;
import utility.Utility;
import view.View;
import view.ViewManager;
import view.rendering.Batch;
import view.rendering.ITexture;

public class Slider extends ViewComponent {
    private ITexture backgroundTexture,sliderTexture;
    private int min, max, value;
    private String sliderText;
    private boolean horizontal;
    private SliderListener listener;

    public Slider(float x, float y, float width, float height, View v, String sliderText, boolean horizontal, int min, int max, int value){
        this(x,y,width,height,v,ViewManager.getTexture("viewTextures/mainButton.png"),ViewManager.getTexture("viewTextures/pressedButton.png"),sliderText,horizontal,min,max,value);
    }

    public Slider(float x, float y, float width, float height, View v, ITexture backgroundTexture, ITexture sliderTexture, String sliderText, boolean horizontal, int min, int max, int value) {
        super(x, y, width, height, v);
        this.backgroundTexture = backgroundTexture;
        this.sliderTexture = sliderTexture;
        this.sliderText = sliderText;
        this.horizontal = horizontal;
        this.min = min;
        this.max = max;
        this.value = value;
    }

    @Override
    public void draw(Batch batch, float originWidth, float originHeight) {
        if(Mouse.isButtonDown(0)){
            float mouseX = view.getMouseX();
            float mouseY = view.getMouseY();
            if(Utility.viewComponentIsCollidingWithMouse(this,mouseX,mouseY)){
                int old = value;

                if(horizontal){
                    value = min + (int)((mouseX - getX())/(getWidth()/(max-min)));
                } else {
                    value = min + (int)((mouseY - getY())/(getHeight()/(max-min)));
                }

                if(value >= max){
                    value = max-1;
                } else if(value < min){
                    value = min;
                }

                if(old != value && listener != null){
                    listener.onChange(value);
                }
            }
        }

        float x = getX()*originWidth;
        float width = getWidth()*originWidth;
        float y = getY()*originHeight;
        float height = getHeight()*originHeight;

        batch.draw(backgroundTexture, x, y, width, height, 0, 1f, 1f, 1f, 1f);

        if(horizontal){
            int scaling = Math.round(width/(max-min));
            batch.draw(sliderTexture, x+scaling*(value-min),y,scaling,height, 1f,1f,1f,1f);
        } else {
            int scaling = Math.round(height/(max-min));
            batch.draw(sliderTexture, x,y+scaling*(value-min),width,scaling, 1f,1f,1f,1f);
        }

        String v = Integer.toString(value);
        if(sliderText == null){
            ViewManager.font.drawText(batch, v, (int) (x + width / 2 - ViewManager.font.getWidth(v) / 2), (int) (y + height / 2 - ViewManager.font.getLineHeight() / 2));
        } else {
            ViewManager.font.drawText(batch, v, (int) (x + width / 2 - ViewManager.font.getWidth(v) / 2), (int) (y + height / 2));
            ViewManager.font.drawText(batch, sliderText, (int) (x + width / 2 - ViewManager.font.getWidth(sliderText) / 2), (int) (y + height / 2 - ViewManager.font.getLineHeight()));
        }

    }

    public void setListener(SliderListener listener){
        this.listener = listener;
    }

    public int getValue(){
        return value;
    }
}
