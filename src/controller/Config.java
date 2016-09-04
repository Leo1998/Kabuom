package controller;

import org.json.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class Config {

    public enum GraphicMode {
        Low, High
    }

    private GraphicMode graphicMode = GraphicMode.High;
    private int fboSamples = 1;
    private int maxWave = 0;



    private int difficulty = 0;

    private File configFile;

    public Config(File configFile) {
        this.configFile = configFile;

        if (configFile.exists()) {
            try {
                JSONObject obj = new JSONObject(new JSONTokener(new FileInputStream(configFile)));

                this.graphicMode = GraphicMode.valueOf(obj.getString("graphicMode"));
                this.fboSamples = obj.getInt("fboSamples");
                this.maxWave = obj.getInt("maxWave");
                this.difficulty = obj.getInt("difficulty");
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
            obj.put("fboSamples", this.fboSamples);
            obj.put("maxWave", this.maxWave);
            obj.put("difficulty", this.difficulty);

            String json = obj.toString();

            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));

            writer.write(json);

            writer.close();
        } catch(Exception e) {
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
        return fboSamples;
    }

    public void setFboSamples(int fboSamples) {
        this.fboSamples = fboSamples;
    }

    public int getMaxWave() {
        return maxWave;
    }

    public void setMaxWave(int maxWave) {
        this.maxWave = maxWave;
    }

    public int getDifficulty() {return difficulty;}

    public void setDifficulty(int difficulty) {this.difficulty = difficulty;}

    @Override
    public String toString() {
        return "Config{" +
                "graphicMode=" + graphicMode +
                ", fboSamples=" + fboSamples +
                ", maxWave=" + maxWave +
                '}';
    }
}
