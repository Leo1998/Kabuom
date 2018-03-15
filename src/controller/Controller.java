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
    private File worldFile = new File("save/world1.json");

    public void mainLoop() {
        new File("save").mkdir();

        this.config = new Config(new File("save/config.json"));
        System.out.println(this.config.toString());

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (world != null)
                    saveGame();

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
        int lastFPS = 0;
        while (!viewManager.isCloseRequested()) {
            long currentTime = System.nanoTime();
            deltaTime = (float) ((currentTime - startTime) / 1000000000D);
            startTime = currentTime;
            frames++;
            if (currentTime - totalTime > 1000000000) {
                if(world != null){
                    System.out.println("fps: " + frames + " entities: " + world.countEntities());
                } else {
                    System.out.println("fps: " + frames);
                }
                lastFPS = frames;
                frames = 0;
                totalTime = currentTime;
            }

            if (world != null) {
                world.update(deltaTime);
            }

            viewManager.render(deltaTime, lastFPS);
        }

        viewManager.dispose();
    }

    public void startGame() {
        this.world = new World(config.getWidth(),config.getHeight());
    }

    public void continueGame(){
        if(worldFile.exists()){
            try{
                this.world = new World(worldFile);
            } catch (Exception e){
                e.printStackTrace();
                startGame();
            }
        } else {
            startGame();
        }
    }

    public void saveGame(){
        if(world != null) {
            try {
                world.write(worldFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void endGame(boolean gameOver) {
        if (!gameOver)
            saveGame();
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
