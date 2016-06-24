package view;


import tower.Tower;
import view.components.Button;
import view.components.ButtonListener;
import view.rendering.PostProcessingManager;

public class MenuView extends View{

    Button startButton;
    //TODO : andereButtons
    Button randomButton;
    Button randomButton2;

    public MenuView(float width, float height, final ViewManager viewManager) {
        super(width, height, viewManager);
        startButton = new Button(width / 2 - width / 2 / 2, height * 1 / 3, width / 2, height * 2 / 3-height * 1 / 3, this, "Start Game", ViewManager.buttonMainTexture,ViewManager.buttonPressedTexture);
        this.components.add(startButton);

        startButton.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                viewManager.setCurrentView(new GameView(originWidth,originHeight, viewManager, viewManager.getCtrl().getNewWorld() ));
            }});
    }



    @Override
    public void layout(float width, float height) {
        super.layout(width, height);

        startButton.setX(width / 2 - width / 2 / 2);
        startButton.setY(height * 1 / 3);
        startButton.setWidth(width / 2);
        startButton.setHeight(height * 2 / 3-height * 1 / 3);


    }
}
