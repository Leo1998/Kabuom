package view;

import controller.Config;
import controller.Controller;
import org.lwjgl.opengl.Display;
import view.components.Button;
import view.components.ButtonListener;

public class OptionsView extends BaseMenuView {

    private Button backButton;
    private Button graphicsButton;
    private Button difficultyButton;

    public OptionsView(float width, float height, final ViewManager viewManager) {
        super(width, height, viewManager);
        backButton = new Button(0.05f, 0.05f, 0.15f, 0.1f, this, "Back");
        this.components.add(backButton);

        backButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                viewManager.setCurrentView(new MenuView(originWidth, originHeight, viewManager));
            }
        });

        String mode = Controller.instance.getConfig().getGraphicMode().name();
        graphicsButton = new Button(0.25f, 0.3f, 0.5f, 0.2f, this, "Graphics: " + mode);
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
            }
        });

        String difficulty = Integer.toString(Controller.instance.getConfig().getDifficulty());
        difficultyButton = new Button(0.25f, 0.55f, 0.5f, 0.2f, this, "Difficulty " + difficulty);

        this.components.add(difficultyButton);

        difficultyButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                Config config = Controller.instance.getConfig();
                int difficultyInt = Controller.instance.getConfig().getDifficulty();
                if (difficultyInt == 10) {
                    config.setDifficulty(1);
                } else {
                    config.setDifficulty(++difficultyInt);
                }
                difficultyButton.setButtontext("Difficulty: " + config.getDifficulty());
            }
        });

    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }

}
