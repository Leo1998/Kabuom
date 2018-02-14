package view;

import controller.Controller;
import view.components.Button;
import view.components.ButtonListener;
import view.components.Label;
import view.components.ViewComponent;

public class MenuView extends BaseMenuView {

    private enum ButtonType{
        CONTINUE("Continue"),
        NEWGAME("New Game"),
        OPTIONS("Options"),
        EXIT("Exit");

        final String label;

        ButtonType(String label){
            this.label = label;
        }
    }

    private Label[] labels;
    private Button[] buttons;

    public MenuView(float width, float height, final ViewManager viewManager) {
        super(width, height, viewManager);

        labels = new Label[2];

        labels[0] = createLabel(0.25f,0.01f,0.5f,0.2f,"KABUOM! Tower Defense");
        labels[1] = createLabel(0.25f,0.15f,0.5f,0.15f,"Max Wave: "+Controller.instance.getConfig().getMaxWave());

        buttons = new Button[ButtonType.values().length];
        float buttonWidth = 0.2f;
        float buttonHeight = 0.5f/buttons.length;
        float buttonStartX = 0.4f;
        float buttonStartY = 0.3f;
        for(int i = 0; i < buttons.length; i++){
            buttons[i] = createButton(buttonStartX,buttonStartY+buttonHeight*i, buttonWidth,buttonHeight*0.75f, ButtonType.values()[i]);
        }

        for(Label label:labels){
            components.add(label);
        }

        for(Button button:buttons){
            components.add(button);
        }
    }

    private Label createLabel(float x, float y, float width, float height, String text){
        return new Label(x,y,width,height, this, text);
    }

    private Button createButton(float x ,float y, float width, float height, ButtonType buttonType){
        Button button = new Button(x,y,width,height, this, buttonType.label);

        switch (buttonType){
            case CONTINUE:
                button.setListener(new ButtonListener() {
                    @Override
                    public void onClick() {
                        viewManager.getCtrl().continueGame();

                        viewManager.setCurrentView(new GameView(originWidth, originHeight, viewManager, viewManager.getCtrl().getWorld()));
                    }
                });
                break;
            case NEWGAME:
                button.setListener(new ButtonListener() {
                    @Override
                    public void onClick() {
                        viewManager.getCtrl().startGame();

                        viewManager.setCurrentView(new GameView(originWidth, originHeight, viewManager, viewManager.getCtrl().getWorld()));
                    }
                });
                break;
            case OPTIONS:
                button.setListener(new ButtonListener() {
                    @Override
                    public void onClick() {
                        viewManager.setCurrentView(new OptionsView(originWidth, originHeight, viewManager));
                    }
                });
                break;
            case EXIT:
                button.setListener(new ButtonListener() {
                    @Override
                    public void onClick() {
                        System.exit(0);
                    }
                });
                break;
        }

        return button;
    }

    @Override
    public void layout(float width, float height) {
        super.layout(width, height);
    }

}
