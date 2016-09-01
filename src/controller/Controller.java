package controller;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import view.GameView;
import view.MenuView;
import view.View;
import view.ViewManager;
import world.World;

import java.io.File;

public class Controller {

    public static Controller instance;

    public static void main(String[] args) {
        instance = new Controller();
        instance.mainLoop();
    }

    private Config config;
    private ViewManager viewManager;
    private World world;

    public void mainLoop() {
        new File("save").mkdir();

        this.config = new Config(new File("save/config.json"));
        System.out.println(this.config.toString());

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                config.save();
            }
        }));

        viewManager = new ViewManager(this);

        View view = new MenuView(Display.getWidth(),Display.getHeight(), viewManager);
        viewManager.setCurrentView(view);

        float deltaTime;
        long startTime = System.nanoTime();
        long totalTime = startTime;
        int frames = 0;
        while (!viewManager.isCloseRequested()) {
            long currentTime = System.nanoTime();
            deltaTime = (float) ((currentTime - startTime) / 1000000000D);
            startTime = currentTime;
            frames++;
            System.out.println("deltaTime: " + deltaTime);
            if (currentTime - totalTime > 1000) {
                System.out.println("fps: " + frames);
                frames = 0;

                totalTime = currentTime;
            }

            if (world != null) {
                world.update(deltaTime);
            }

            viewManager.render(deltaTime);
        }

        viewManager.dispose();
    }

    public void createNewWorld(){
        this.world = new World(21,21,9000);
    }

    public Config getConfig() {
        return config;
    }

    public ViewManager getViewManager() {
        return viewManager;
    }

    public World getWorld() {
        return world;
    }
}
