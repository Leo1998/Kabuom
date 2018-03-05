package controller;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class Config {

    public enum GraphicMode {
        Low, High
    }

    private GraphicMode graphicMode = GraphicMode.High;
    private int maxWave = 0;
    private int difficulty = 1;
    private int width = 19;
    private int height = 19;

    private File configFile;

    public Config(File configFile) {
        this.configFile = configFile;

        if (configFile.exists()) {
            try {
                JSONObject obj = new JSONObject(new JSONTokener(new FileInputStream(configFile)));

                this.graphicMode = GraphicMode.valueOf(obj.getString("graphicMode"));
                this.maxWave = obj.getInt("maxWave");
                this.difficulty = obj.getInt("difficulty");
                this.width = obj.getInt("width");
                this.height = obj.getInt("height");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        try {
            JSONObject obj = new JSONObject();

            obj.put("graphicMode", this.graphicMode);
            obj.put("maxWave", this.maxWave);
            obj.put("difficulty", this.difficulty);
            obj.put("width", this.width);
            obj.put("height", this.height);

            String json = obj.toString();

            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

            writer.write(json);

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GraphicMode getGraphicMode() {
        return graphicMode;
    }

    public void setGraphicMode(GraphicMode graphicMode) {
        this.graphicMode = graphicMode;
    }

    public int getFboSamples() {
        return graphicMode == GraphicMode.High ? 4 : 1;
    }

    public int getMaxWave() {
        return maxWave;
    }

    public void setMaxWave(int maxWave) {
        this.maxWave = maxWave;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getWidth(){
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Config{" +
                "graphicMode=" + graphicMode +
                ", maxWave=" + maxWave +
                '}';
    }
}
