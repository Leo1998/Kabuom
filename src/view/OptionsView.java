package view;

import controller.Config;
import controller.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import view.components.*;
import view.rendering.Batch;

import java.util.LinkedList;
import java.util.List;

public class OptionsView extends BaseMenuView {

    List<ViewComponent> options1,options2;
    Switch typeSwitch;

    public OptionsView(int width, int height, final ViewManager viewManager) {
        super(width, height, viewManager);
        Button backButton = new Button(0.05f, 0.05f, 0.15f, 0.1f, this, "Back");
        this.components.add(backButton);

        backButton.setListener(() -> viewManager.setCurrentView(new MenuView(originWidth, originHeight, viewManager)));

        typeSwitch = new Switch(0.8f,0.05f,0.15f,0.1f,this,null);
        this.components.add(typeSwitch);

        options1 = createOptions1();
        options2 = createOptions2();
    }

    private final List<ViewComponent> createOptions1(){
        List<ViewComponent> result = new LinkedList<>();

        int difficulty = Controller.instance.getConfig().getDifficulty();
        Slider difficultySlider = new Slider(0.15f, 0.25f, 0.7f, 0.1f, this, "Difficulty", true, 1, 33, difficulty);
        result.add(difficultySlider);

        difficultySlider.setListener(value -> Controller.instance.getConfig().setDifficulty(value));

        int aiDifficulty = Controller.instance.getConfig().getAiDifficulty();
        Slider aiSlider = new Slider(0.15f, 0.4f, 0.7f, 0.1f, this, "AI Difficulty", true, 1, 33, aiDifficulty);
        result.add(aiSlider);

        aiSlider.setListener(value -> Controller.instance.getConfig().setAiDifficulty(value));

        int gameWidth = Controller.instance.getConfig().getWidth();
        Slider widthSlider = new Slider(0.15f, 0.55f, 0.7f, 0.1f, this, "Game Width", true, 5, 50, gameWidth);
        result.add(widthSlider);

        widthSlider.setListener(value -> Controller.instance.getConfig().setWidth(value));

        int gameHeight = Controller.instance.getConfig().getHeight();
        Slider heightSlider = new Slider(0.15f, 0.7f, 0.7f, 0.1f, this, "Game Height", true, 5, 50, gameHeight);
        result.add(heightSlider);

        heightSlider.setListener(value -> Controller.instance.getConfig().setHeight(value));

        return result;
    }

    private final List<ViewComponent> createOptions2(){
        List<ViewComponent> result = new LinkedList<>();

        Switch graphicsButton = new Switch(0.25f, 0.25f, 0.5f, 0.1f, this, "High Graphics");
        graphicsButton.setDown(Controller.instance.getConfig().getGraphicMode() == Config.GraphicMode.High);
        result.add(graphicsButton);

        graphicsButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                Config config = Controller.instance.getConfig();
                if(graphicsButton.isDown()){
                    config.setGraphicMode(Config.GraphicMode.High);
                } else {
                    config.setGraphicMode(Config.GraphicMode.Low);
                }

                Controller.instance.getViewManager().onGraphicsConfigurationChanged(config.getGraphicMode());
            }
        });


        Switch soundSwitch = new Switch(0.25f, 0.4f, 0.5f, 0.1f, this, "Sound");
        soundSwitch.setDown(Controller.instance.getConfig().isSound());
        result.add(soundSwitch);

        soundSwitch.setListener(()->Controller.instance.getConfig().setSound(soundSwitch.isDown()));

        Switch fpsSwitch = new Switch(0.25f, 0.55f, 0.5f, 0.1f, this, "Show FPS");
        fpsSwitch.setDown(Controller.instance.getConfig().isShowFPS());
        result.add(fpsSwitch);

        fpsSwitch.setListener(()->Controller.instance.getConfig().setShowFPS(fpsSwitch.isDown()));

        Switch menuConfetti = new Switch(0.25f, 0.7f, 0.5f, 0.1f, this, "Menu Confetti");
        menuConfetti.setDown(Controller.instance.getConfig().isMenuConfetti());
        result.add(menuConfetti);

        menuConfetti.setListener(()->Controller.instance.getConfig().setMenuConfetti(menuConfetti.isDown()));

        return result;
    }

    @Override
    public void renderUI(Batch batch) {
        super.renderUI(batch);
        if(typeSwitch.isDown()){
            for(ViewComponent component : options1){
                component.draw(batch,originWidth,originHeight);
            }
        } else {
            for(ViewComponent component : options2){
                component.draw(batch,originWidth,originHeight);
            }
        }
    }

    @Override
    public void layout(int width, int height) {
        super.layout(width, height);
    }

    @Override
    public void onKeyDown(int key, char c) {
        super.onKeyDown(key, c);
        if (key == Keyboard.KEY_ESCAPE) {
            viewManager.setCurrentView(new MenuView(originWidth, originHeight, viewManager));
        }
    }

    @Override
    public void onMouseDown(int button, int mouseX, int mouseY) {
        super.onMouseDown(button, mouseX, mouseY);
        if(typeSwitch.isDown()){
            for(ViewComponent component : options1){
                component.onMouseDown(button,getMouseX(),getMouseY());
            }
        } else {
            for(ViewComponent component : options2){
                component.onMouseDown(button,getMouseX(),getMouseY());
            }
        }
    }

    @Override
    public void onMouseUp(int button, int mouseX, int mouseY) {
        super.onMouseUp(button, mouseX, mouseY);
        if(typeSwitch.isDown()){
            for(ViewComponent component : options1){
                component.onMouseUp(button,getMouseX(),getMouseY());
            }
        } else {
            for(ViewComponent component : options2){
                component.onMouseUp(button,getMouseX(),getMouseY());
            }
        }
    }
}
