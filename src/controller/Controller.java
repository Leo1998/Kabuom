package controller;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import view.ViewManager;

public class Controller {

    public static void main(String[] args) {
        new Controller().mainLoop();
    }

    private ViewManager viewManager;

    public void mainLoop() {
        viewManager = new ViewManager();

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

}
