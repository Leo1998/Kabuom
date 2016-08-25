package view;

import controller.Config;
import controller.Controller;
import org.lwjgl.opengl.Display;
import view.components.Button;
import view.components.ButtonListener;

public class OptionsView extends BaseMenuView {

    private Button backButton;
    private Button graphicsButton;

    public OptionsView(float width, float height, final ViewManager viewManager) {
        super(width, height, viewManager);
        backButton = new Button(25, 25, width / 8, height / 10, this, "Back");
        this.components.add(backButton);

        backButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                viewManager.setCurrentView(new MenuView(originWidth,originHeight, viewManager));
            }});

        String mode = Controller.instance.getConfig().getGraphicMode().name();
        graphicsButton = new Button((width / 2) - (width / 6 / 2), (height / 2) - (height / 8 / 2) - (height / 6), width / 4, height / 8, this, "Graphics: " + mode);
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

                //hotfix
                Controller.instance.getViewManager().getPostProcessingManager().resize(Display.getWidth(), Display.getHeight());

                String mode = config.getGraphicMode().name();
                graphicsButton.setButtontext("Graphics: " + mode);
            }});

    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);

        backButton.setX(25);
        backButton.setY(25);
        backButton.setWidth(width / 8);
        backButton.setHeight(height / 10);

        graphicsButton.setX((width / 2) - (width / 6 / 2));
        graphicsButton.setY((height / 2) - (height / 8 / 2) - (height / 6));
        graphicsButton.setWidth(width / 4);
        graphicsButton.setHeight(height / 8);
    }

}
