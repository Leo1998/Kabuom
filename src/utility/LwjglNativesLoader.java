package utility;

import java.io.File;

public class LwjglNativesLoader {

    public static void load() {
        NativeLibraryLoader loader = new NativeLibraryLoader();
        File nativesDir;
        try {
            if (OS.isWindows) {
                nativesDir = loader.extractFile(OS.is64Bit ? "natives/lwjgl64.dll" : "natives/lwjgl.dll", null).getParentFile();
            } else if(OS.isLinux) {
                nativesDir = loader.extractFile(OS.is64Bit ? "natives/liblwjgl64.so" : "natives/liblwjgl.so", null).getParentFile();
            } else {
                throw new UnsupportedOperationException("Unsupported os!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to extract LWJGL natives.");
        }

        System.setProperty("org.lwjgl.librarypath", nativesDir.getAbsolutePath());
    }

}
