import greenfoot.*;
import java.util.ArrayList;

public class TopScoreWorld extends World {
    public TopScoreWorld() {
        super(800, 600, 1); // Ukuran dunia
        prepare();
    }

    private void prepare() {
        ArrayList<PlayerData> players = GameManager.getInstance().getPlayers();

        int y = 50; // Posisi awal untuk teks
        for (PlayerData player : players) {
            // Tampilkan nama pemain
            addObject(new Label("Nama: " + player.getName(), 24), getWidth() / 2, y);
            y += 30;

            // Tampilkan skor untuk Level 1
            addObject(new Label("Level 1 - Top Time: " + player.getTopTime(1) + "s", 18), getWidth() / 2, y);
            y += 20;

            // Tampilkan skor untuk Level 2
            addObject(new Label("Level 2 - Top Time: " + player.getTopTime(2) + "s", 18), getWidth() / 2, y);
            y += 20;

            // Tampilkan skor untuk Level 3
            addObject(new Label("Level 3 - Top Time: " + player.getTopTime(3) + "s", 18), getWidth() / 2, y);
            y += 40; // Tambahkan jarak antar pemain
        }

        TopScore_Word topScore_Word = new TopScore_Word();
        addObject(topScore_Word,416,279);

        Button_Exit button_Exit = new Button_Exit();
        addObject(button_Exit,715,535);
       
        
    }
}
