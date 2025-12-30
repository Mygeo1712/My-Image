import greenfoot.GreenfootSound;

public class SoundManager {
    private static GreenfootSound currentMusic = null;
    private static String currentMusicFile = null; // Menyimpan nama file musik yang sedang diputar

    // Method untuk memutar musik
    public static void playMusic(String musicFile) {
        stopMusic(); // Hentikan musik yang sedang diputar
        currentMusic = new GreenfootSound(musicFile);
        currentMusicFile = musicFile; // Simpan nama file
        currentMusic.playLoop(); // Mainkan musik secara terus-menerus
    }

    // Method untuk menghentikan musik
    public static void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }

    // Method untuk melanjutkan musik yang terakhir diputar
    public static void resumeMusic() {
        if (currentMusicFile != null) { // Pastikan ada musik yang sebelumnya disimpan
            if (currentMusic == null) {
                currentMusic = new GreenfootSound(currentMusicFile);
            }
            if (!currentMusic.isPlaying()) {
                currentMusic.playLoop(); // Lanjutkan musik jika tidak sedang diputar
            }
        }
    }
}
