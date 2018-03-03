package view;

import controller.Config;
import controller.Controller;
import org.lwjgl.opengl.Display;
import view.components.Button;
import view.components.ButtonListener;
import view.components.Slider;

public class OptionsView extends BaseMenuView {

    private Button backButton;
    private Button graphicsButton;
    private Slider difficultySlider;

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

                String mode = config.getGraphicMode().name();
                graphicsButton.setButtontext("Graphics: " + mode);

                Controller.instance.getViewManager().getPostProcessingManager().resize(Display.getWidth(), Display.getHeight());
            }
        });

        int difficulty = Controller.instance.getConfig().getDifficulty();
        difficultySlider = new Slider(0.25f,0.55f,0.5f,0.2f,this, "Difficulty", true, 1, 21, difficulty);
        this.components.add(difficultySlider);

        difficultySlider.setListener(value -> Controller.instance.getConfig().setDifficulty(value));

    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }

    public int getDifficulty(){
        return difficultySlider.getValue();
    }

}
