package controller;

import utility.Utility;

import java.io.*;
import java.nio.ByteBuffer;

public class Config {

    private boolean highGraphics = false;
    private boolean vSync = true;
    private boolean showFPS = false;
    private boolean sound = true;
    private boolean menuConfetti = true;

    private int maxWave = 0;
    private int difficulty = 1;
    private int aiDifficulty = 1;
    private int width = 19;
    private int height = 19;

    private File configFile;

    public Config(File configFile) {
        this.configFile = configFile;

        if (configFile.exists()) {
            try {
                InputStream is = new FileInputStream(configFile);

                long length = configFile.length();
                if(length < byteSize()){
                    throw new IOException("File " + configFile.getName() + " is to short");
                }

                ByteBuffer buf = ByteBuffer.allocateDirect(byteSize());
                byte[] bytes = new byte[byteSize()];
                if(is.read(bytes,0,bytes.length) != bytes.length){
                    throw new IOException("File " + configFile.getName() + " could not be read");
                }

                buf.put(bytes);
                buf.flip();

                byte b = buf.get();

                this.highGraphics = Utility.isMask(byteMask.GRAPHIC.mask,b);
                this.vSync = Utility.isMask(byteMask.VSYNC.mask,b);
                this.showFPS = Utility.isMask(byteMask.FPS.mask,b);
                this.sound = Utility.isMask(byteMask.SOUND.mask,b);
                this.menuConfetti = Utility.isMask(byteMask.CONFETTI.mask,b);

                this.maxWave = buf.getInt();
                this.difficulty = buf.getInt();
                this.aiDifficulty = buf.getInt();
                this.width = buf.getInt();
                this.height = buf.getInt();
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
        BufferedOutputStream bos = null;

        try {
            FileOutputStream fos = new FileOutputStream(configFile);
            bos = new BufferedOutputStream(fos);

            byte[] bytes = new byte[byteSize()];
            ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);

            byte b = 0x00;

            b = Utility.setMask(byteMask.GRAPHIC.mask,b,this.highGraphics);
            b = Utility.setMask(byteMask.VSYNC.mask,b,this.vSync);
            b = Utility.setMask(byteMask.FPS.mask,b,this.showFPS);
            b = Utility.setMask(byteMask.SOUND.mask,b,this.sound);
            b = Utility.setMask(byteMask.CONFETTI.mask,b,this.menuConfetti);

            buf.put(b);

            buf.putInt(this.maxWave);
            buf.putInt(this.difficulty);
            buf.putInt(this.aiDifficulty);
            buf.putInt(this.width);
            buf.putInt(this.height);

            buf.flip();
            buf.get(bytes,0,bytes.length);

            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(bos != null){
                try {
                    bos.flush();
                    bos.close();
                } catch (Exception e){}
            }
        }
    }

    public static int byteSize(){
        return 1 + 5*4;
    }

    private enum byteMask{
        GRAPHIC((byte)0x01),
        VSYNC((byte)0x02),
        FPS((byte)0x04),
        SOUND((byte)0x08),
        CONFETTI((byte)0x10),
        ;
        final byte mask;

        byteMask(byte mask){
            this.mask = mask;
        }
    }

    public boolean getHighGraphics() {
        return highGraphics;
    }

    public void setHighGraphics(boolean highGraphics) {
        this.highGraphics = highGraphics;
    }

    public boolean isVSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        this.vSync = vSync;
    }

    public boolean isShowFPS() {
        return showFPS;
    }

    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }

    public boolean isSound(){
        return sound;
    }

    public void setSound(boolean sound){
        this.sound = sound;
    }

    public boolean isMenuConfetti() {
        return menuConfetti;
    }

    public void setMenuConfetti(boolean menuConfetti) {
        this.menuConfetti = menuConfetti;
    }

    public int getFboSamples() {
        return highGraphics ? 4 : 1;
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

    public int getAiDifficulty() {
        return aiDifficulty;
    }

    public void setAiDifficulty(int aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
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
                "graphicMode=" + highGraphics +
                ", vSync=" + vSync +
                ", showFPS=" + showFPS +
                ", maxWave=" + maxWave +
                ", difficulty=" + difficulty +
                ", aiDifficulty=" + aiDifficulty +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
