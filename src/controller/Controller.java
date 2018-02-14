package controller;

import org.lwjgl.opengl.Display;
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
                //if (world != null)
                //    World.saveWorld(world);

                config.save();
            }
        }));

        viewManager = new ViewManager(this);

        View view = new MenuView(Display.getWidth(), Display.getHeight(), viewManager);
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
            if (currentTime - totalTime > 1000000000) {
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

    public void startGame() {
        this.world = new World(19,19,config.getDifficulty(),new File("save/world1.json"));
    }

    public void continueGame(){
        //this.world = World.createWorld(new File("save/world1.json"), config.getDifficulty());
        this.world = new World(19,19,config.getDifficulty(),new File("save/world1.json"));
    }

    public void endGame(boolean gameOver) {
        if (gameOver)
            world.getWorldFile().delete();
        else
            //World.saveWorld(world);
        world.end();

        world = null;

        viewManager.setCurrentView(new MenuView(Display.getWidth(), Display.getHeight(), viewManager));
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
