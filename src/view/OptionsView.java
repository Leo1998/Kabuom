package view;

import controller.Config;
import controller.Controller;
import org.lwjgl.opengl.Display;
import view.components.Button;
import view.components.ButtonListener;
import view.components.Slider;

public class OptionsView extends BaseMenuView {

    public OptionsView(float width, float height, final ViewManager viewManager) {
        super(width, height, viewManager);
        Button backButton = new Button(0.05f, 0.05f, 0.15f, 0.1f, this, "Back");
        this.components.add(backButton);

        backButton.setListener(() -> viewManager.setCurrentView(new MenuView(originWidth,originHeight,viewManager)));

        String mode = Controller.instance.getConfig().getGraphicMode().name();
        Button graphicsButton = new Button(0.25f, 0.2f, 0.5f, 0.15f, this, "Graphics: " + mode);
        this.components.add(graphicsButton);

        graphicsButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                Config config = Controller.instance.getConfig();
                if (config.getGraphicMode() == Config.GraphicMode.High) {
                    config.setGraphicMode(Config.GraphicMode.Low);
                } else if (config.getGraphicMode() == Config.GraphicMode.Low) {
                    config.setGraphicMode(Config.GraphicMode.High);
                }

                String mode = config.getGraphicMode().name();
                graphicsButton.setButtontext("Graphics: " + mode);

                Controller.instance.getViewManager().getPostProcessingManager().resize(Display.getWidth(), Display.getHeight());
            }
        });

        int difficulty = Controller.instance.getConfig().getDifficulty();
        Slider difficultySlider = new Slider(0.15f,0.4f,0.7f,0.1f,this, "Difficulty", true, 1, 21, difficulty);
        this.components.add(difficultySlider);

        difficultySlider.setListener(value -> Controller.instance.getConfig().setDifficulty(value));

        int gameWidth = Controller.instance.getConfig().getWidth();
        Slider widthSlider = new Slider(0.15f, 0.55f, 0.7f, 0.1f, this, "Game Width", true, 5, 50, gameWidth);
        this.components.add(widthSlider);

        widthSlider.setListener(value -> Controller.instance.getConfig().setWidth(value));

        int gameHeight = Controller.instance.getConfig().getHeight();
        Slider heightSlider = new Slider(0.15f, 0.7f, 0.7f, 0.1f, this, "Game Height", true, 5, 50, gameHeight);
        this.components.add(heightSlider);

        heightSlider.setListener(value -> Controller.instance.getConfig().setHeight(value));
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }
}
