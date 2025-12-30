import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Button_Music extends Actor {
    private GreenfootImage MusicImage = new GreenfootImage("Button_Music.png");
    private GreenfootImage NoMusicImage = new GreenfootImage("Button_Nomusic.png");
    private boolean isMusicOn = true; // Status apakah musik aktif

    public Button_Music() {
        setImage(MusicImage); // Set gambar awal ke MusicImage
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            toggleMusic(); // Panggil metode untuk mengatur ulang status musik
        }
    }

    private void toggleMusic() {
        if (isMusicOn) {
            setImage(NoMusicImage); // Ganti gambar ke NoMusicImage
            SoundManager.stopMusic(); // Hentikan semua musik
        } else {
            setImage(MusicImage); // Ganti gambar kembali ke MusicImage
            resumeBackgroundMusic(); // Memutar ulang musik sesuai dengan world saat ini
        }
        isMusicOn = !isMusicOn; // Balik status musik
    }

    private void resumeBackgroundMusic() {
        // Cek world saat ini dan putar musik sesuai urutan
        World currentWorld = getWorld();
        if (currentWorld instanceof HomeWorld) {
            SoundManager.playMusic("HomeMusic.mp3");
        } else if (currentWorld instanceof Level1World || currentWorld instanceof Question1World) {
            SoundManager.playMusic("Rasa Sayange.mp3");
        } else if (currentWorld instanceof Level2World || currentWorld instanceof Question2World) {
            SoundManager.playMusic("Bolelebo-Anak Kambing Saya.mp3");
        } else if (currentWorld instanceof Level3World || currentWorld instanceof Question3World) {
            SoundManager.playMusic("Yamko Rambe Yamko.mp3");
        } else if (currentWorld instanceof EndWorld) {
            SoundManager.playMusic("Finish.mp3");
        }
    }
}
