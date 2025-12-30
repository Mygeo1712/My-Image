import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Level2World extends World
{
    private Life lifePanel;
    private ScorePanel scorePanel;
    private long startTime;  
    
    
    public Level2World()
    {  
        super(800, 600, 1);
         SoundManager.playMusic("Ampar Ampar Pisang.mp3");
        GameManager.getInstance().setCurrentLevel(2); // Simpan level saat ini

        // Mengatur urutan objek, Cloud dan Cloud2 berada di belakang semua objek lainnya.
        setPaintOrder(
            Button_Pouse.class,Cover1.class, Cover2.class, Jamur.class, Grass.class, 
            Plane2.class,Finish.class, Wall2.class, Rocket2.class, 
            Level2.class, Life.class, ScorePanel.class, 
            Cloud2.class, Cloud.class
        );

        Button_Pouse button_Pouse = new Button_Pouse();
        addObject(button_Pouse,774,22);

        ScorePanel scorePanel = new ScorePanel();
        addObject(scorePanel, 71, 42);

        prepare();  
    }
    
   
    public void endLevel() {
        long endTime = System.currentTimeMillis();
        long elapsedTime = (endTime - startTime) / 1000;  // Waktu dalam detik
        GameManager.getInstance().getCurrentPlayer().setTopTime(2, elapsedTime);  // Simpan waktu level 1
        Greenfoot.setWorld(new Level3World());  // Lanjut ke Level 2
    }
    
    public void saveScore(long timeAlive) {
        GameManager.getInstance().setTopScore(timeAlive, 2);  // Simpan top score untuk level 1
    }

    private void resetGame() {
        removeObjects(getObjects(Plane2.class));
        removeObjects(getObjects(Life.class));
        removeObjects(getObjects(ScorePanel.class));
    }
    
    private void prepare()
    {
        GameManager manager = GameManager.getInstance();

        Life life = new Life();  
        addObject(life, 203, 45);  // Menambahkan Life ke dunia

        Plane2 plane2 = new Plane2();
        addObject(plane2, 48, 317); // Menambahkan Plane2 ke dunia
        plane2.setLife(life);

        Level2 level2 = new Level2();
        addObject(level2,420, 44);

        Sun sun = new Sun();
        addObject(sun,682,36);

        Rocket2 rocket2 = new Rocket2();
        addObject(rocket2,307,482);

        Rocket2 rocket22 = new Rocket2();
        addObject(rocket22,474,132);

        Cloud cloud = new Cloud();
        addObject(cloud,738,69);

        Cloud cloud2 = new Cloud();
        addObject(cloud2,613,71);

        Cloud cloud3 = new Cloud();
        addObject(cloud3,415,114);

        Cloud cloud4 = new Cloud();
        addObject(cloud4,200,72);

        Cloud2 cloud22 = new Cloud2();
        addObject(cloud22,612,318);

        Cloud2 cloud23 = new Cloud2();
        addObject(cloud23,232,205);

        Cloud2 cloud24 = new Cloud2();
        addObject(cloud24,20,483);

        Cloud cloud5 = new Cloud();
        addObject(cloud5,775,484);

        Cloud cloud6 = new Cloud();
        addObject(cloud6,786,180);

        Cloud cloud7 = new Cloud();
        addObject(cloud7,500,584);

        Wall2 wall2 = new Wall2();
        addObject(wall2,234,130);

        Wall2 wall22 = new Wall2();
        addObject(wall22,234,188);

        Wall2 wall23 = new Wall2();
        addObject(wall23,234,495);

        Wall2 wall24 = new Wall2();
        addObject(wall24,234,422);

        Wall2 wall25 = new Wall2();
        addObject(wall25,380,548);

        Wall2 wall26 = new Wall2();
        addObject(wall26,380,479);

        Wall2 wall27 = new Wall2();
        addObject(wall27,402,281);

        Wall2 wall213 = new Wall2();
        addObject(wall213,402,212);

        Wall2 wall29 = new Wall2();
        addObject(wall29,592,87);

        Wall2 wall210 = new Wall2();
        addObject(wall210,592,140);

        Wall2 wall211 = new Wall2();
        addObject(wall211,548,357);

        Wall2 wall212 = new Wall2();
        addObject(wall212,548,414);

        Finish finish = new Finish();
        addObject(finish,709,352);

        Grass grass = new Grass();
        addObject(grass,787,580);

        cloud24.setLocation(18,580);
        Grass grass2 = new Grass();

        addObject(grass2,18,580);
        grass.setLocation(792,592);

        grass2.setLocation(7,591);
        Jamur jamur = new Jamur();

        addObject(jamur,21,572);
        Jamur jamur2 = new Jamur();

        addObject(jamur2,790,574);
        Cover1 cover1 = new Cover1();

        addObject(cover1,376,577);

        Cover1 cover12 = new Cover1();
        addObject(cover12,548,447);

        Cover1 cover13 = new Cover1();
        addObject(cover13,228,524);

        Cover2 cover2 = new Cover2();
        addObject(cover2,584,52);

        Cover2 cover22 = new Cover2();
        addObject(cover22,399,172);

        Cover2 cover23 = new Cover2();
        addObject(cover23,230,83);

        Cover1 cover14 = new Cover1();
        addObject(cover14,588,170);

        Cover1 cover15 = new Cover1();
        addObject(cover15,398,318);

        Cover1 cover16 = new Cover1();
        addObject(cover16,232,222);

        Cover2 cover24 = new Cover2();
        addObject(cover24,230,375);

        Cover2 cover25 = new Cover2();
        addObject(cover25,375,435);

        Cover2 cover26 = new Cover2();   
        addObject(cover26,545,311);

    }

    public Life getLifePanel() {
        return lifePanel;
    }

    public ScorePanel getScorePanel() {
        return scorePanel;
    }
}
