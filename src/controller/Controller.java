package controller;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.lwjgl.opengl.Display;
import view.MenuView;
import view.View;
import view.ViewManager;
import world.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

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
        this.world = new World(config.getWidth(),config.getHeight());
    }

    public void continueGame(){
        if(worldFile.exists()){
            try{
                JSONObject object = new JSONObject(new JSONTokener(new FileInputStream(worldFile)));
                this.world = new World(object);
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
                BufferedWriter writer = new BufferedWriter(new FileWriter(worldFile));
                writer.write(world.toJSON().toString());
                writer.close();
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
