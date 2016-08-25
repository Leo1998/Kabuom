package view;

import view.components.Button;
import view.components.ButtonListener;

public class MenuView extends BaseMenuView {

    private Button startButton;
    private Button optionsButton;
    private Button exitButton;

    public MenuView(float width, float height, final ViewManager viewManager) {
        super(width, height, viewManager);
        startButton = new Button((width / 2) - (width / 6 / 2), (height / 2) - (height / 8 / 2) - (height / 6), width / 6, height / 8, this, "Start");
        optionsButton = new Button((width / 2) - (width / 6 / 2), (height / 2) - (height / 8 / 2), width / 6, height / 8, this, "Options");
        exitButton = new Button((width / 2) - (width / 6 / 2), (height / 2) - (height / 8 / 2) + (height / 6), width / 6, height / 8, this, "Exit");
        this.components.add(startButton);
        this.components.add(optionsButton);
        this.components.add(exitButton);

        startButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                viewManager.getCtrl().createNewWorld();

                viewManager.setCurrentView(new GameView(originWidth,originHeight, viewManager, viewManager.getCtrl().getWorld()));
            }});

        optionsButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                viewManager.setCurrentView(new OptionsView(originWidth,originHeight, viewManager));
            }});

        exitButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                System.exit(0);
            }});
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);

        startButton.setX((width / 2) - (width / 6 / 2));
        startButton.setY((height / 2) - (height / 8 / 2) - (height / 6));
        startButton.setWidth(width / 6);
        startButton.setHeight(height / 8);

        optionsButton.setX((width / 2) - (width / 6 / 2));
        optionsButton.setY((height / 2) - (height / 8 / 2));
        optionsButton.setWidth(width / 6);
        optionsButton.setHeight(height / 8);

        exitButton.setX((width / 2) - (width / 6 / 2));
        exitButton.setY((height / 2) - (height / 8 / 2) + (height / 6));
        exitButton.setWidth(width / 6);
        exitButton.setHeight(height / 8);
    }

}
