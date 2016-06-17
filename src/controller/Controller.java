package controller;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import view.GameView;
import view.View;
import view.ViewManager;
import world.World;

public class Controller {

    public static void main(String[] args) {
        new Controller().mainLoop();
    }

    private ViewManager viewManager;
    private World world;

    public void mainLoop() {
        viewManager = new ViewManager();

        createWorld();

        float deltaTime;
        long startTime = System.currentTimeMillis();
        long totalTime = startTime;
        int frames = 0;
        while (!viewManager.isCloseRequested()) {
            long currentTime = System.currentTimeMillis();
            deltaTime = (float) ((currentTime - startTime) / 1000D);
            startTime = currentTime;
            frames++;
            if (currentTime - totalTime > 1000) {
                System.out.println("fps: " + frames);
                frames = 0;

                totalTime = currentTime;
            }

            viewManager.render(deltaTime);
        }

        viewManager.dispose();
    }

    private void createWorld(){
        world = new World(20,20,9000);

        View view = new GameView(Display.getWidth(),Display.getHeight(), viewManager, world);
        viewManager.setCurrentView(view);
    }

}
