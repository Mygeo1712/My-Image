import greenfoot.*; // Paket Greenfoot

public class GameWorld extends World {
    public static int currentLevel = 1; // Default: mulai dari level 1

    public GameWorld(int width, int height, int cellSize) {
        super(width, height, cellSize);
    }

    public static void setCurrentLevel(int level) {
        currentLevel = level;
    }

    public static int getCurrentLevel() {
        return currentLevel;
    }
}
