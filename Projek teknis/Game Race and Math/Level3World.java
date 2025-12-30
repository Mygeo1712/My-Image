import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)


public class Level3World extends World
{
    private Life lifePanel;
    private ScorePanel scorePanel;
    private long startTime;
    
   
    public Level3World()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(800, 600, 1);
         SoundManager.playMusic("Yamko Rambe Yamko.mp3");
        GameManager.getInstance().setCurrentLevel(3); // Simpan level saat ini
        setPaintOrder(
              
            Ground3.class,Ground32.class,Cover1.class, Cover2.class, Wall2.class,
            Plane3.class,Level3.class,  Life.class, ScorePanel.class,  Rocket3.class,
            Button_Pouse.class, Finish3.class,Cloud2.class, Cloud.class
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
        GameManager.getInstance().getCurrentPlayer().setTopTime(3, elapsedTime);  // Simpan waktu level 1
        Greenfoot.setWorld(new EndWorld ());  // Lanjut ke EndWorld
    }
    
    public void saveScore(long timeAlive) {
        GameManager.getInstance().setTopScore(timeAlive, 3);  // Simpan top score untuk level 3
    }

    private void resetGame() {
        removeObjects(getObjects(Plane3.class));
        removeObjects(getObjects(Life.class));
        removeObjects(getObjects(ScorePanel.class));
    }
    
    private void prepare(){
         GameManager manager = GameManager.getInstance();
        
        Life life = new Life();
        addObject(life, 203, 45);
        
        Plane3 plane3 = new Plane3();
        addObject(plane3,32,308);
        plane3.setLife(life);
        
        Level3 level3 = new Level3();
        addObject(level3,420, 44);

        Sun sun = new Sun();
        addObject(sun,705,32);

        Cloud cloud = new Cloud();
        addObject(cloud,754,65);

        Cloud cloud2 = new Cloud();
        addObject(cloud2,616,52);

        cloud2.setLocation(678,54);
        sun.setLocation(696,40);

        Cloud cloud3 = new Cloud();
        addObject(cloud3,227,87);

        Cloud cloud4 = new Cloud();
        addObject(cloud4,444,99);

        Cloud cloud5 = new Cloud();
        addObject(cloud5,585,50);

        Cloud cloud6 = new Cloud();
        addObject(cloud6,35,98);

        Cloud cloud7 = new Cloud();
        addObject(cloud7,433,29);

        Cloud2 cloud22 = new Cloud2();
        addObject(cloud22,433,29);

        Cloud cloud8 = new Cloud();
        addObject(cloud8,453,270);

        Cloud2 cloud23 = new Cloud2();
        addObject(cloud23,682,384);

        Cloud2 cloud24 = new Cloud2();
        addObject(cloud24,728,188);

        Cloud2 cloud25 = new Cloud2();
        addObject(cloud25,181,213);

        Cloud2 cloud26 = new Cloud2();
        addObject(cloud26,51,528);

        Cloud2 cloud27 = new Cloud2();
        addObject(cloud27,497,457);

        Cloud cloud9 = new Cloud();
        addObject(cloud9,773,546);

        

        Wall2 wall2 = new Wall2();
        addObject(wall2,181,152);

        Wall2 wall22 = new Wall2();
        addObject(wall22,181,224);

        Wall2 wall23 = new Wall2();
        addObject(wall23,181,389);

        Wall2 wall24 = new Wall2();
        addObject(wall24,181,464);

        Wall2 wall25 = new Wall2();
        addObject(wall25,296,196);

        Wall2 wall26 = new Wall2();
        addObject(wall26,296,271);

        Wall2 wall27 = new Wall2();
        addObject(wall27,385,432);

        Wall2 wall28 = new Wall2();      
        addObject(wall28,385,505);

        Wall2 wall29 = new Wall2();
        addObject(wall29,486,113);

        Wall2 wall210 = new Wall2();
        addObject(wall210,486,188);

        Wall2 wall211 = new Wall2();
        addObject(wall211,511,347);

        Wall2 wall212 = new Wall2();
        addObject(wall212,511,420);

        Wall2 wall213 = new Wall2();
        addObject(wall213,633,130);

        Wall2 wall214 = new Wall2();
        addObject(wall214,633,204);

        Wall2 wall215 = new Wall2();
        addObject(wall215,647,356);

        Wall2 wall216 = new Wall2();
        addObject(wall216,647,431);

        Rocket3 rocket3 = new Rocket3();
        addObject(rocket3,238,122);

        Rocket3 rocket32 = new Rocket3();
        addObject(rocket32,427,442);

        Rocket3 rocket33 = new Rocket3();
        addObject(rocket33,578,195);

        Cover1 cover1 = new Cover1();
        addObject(cover1,647,464);

        Cover1 cover12 = new Cover1();
        addObject(cover12,510,457);

        Cover1 cover13 = new Cover1();
        addObject(cover13,383,533);

        Cover1 cover14 = new Cover1();
        addObject(cover14,179,498);

        Cover2 cover2 = new Cover2();
        addObject(cover2,630,94);

        Cover2 cover22 = new Cover2();
        addObject(cover22,482,66);

        Cover2 cover23 = new Cover2();
        addObject(cover23,292,149);

        Cover2 cover24 = new Cover2();
        addObject(cover24,177,104);

        Ground3 ground3 = new Ground3();
        addObject(ground3,650,480);

        Ground3 ground32 = new Ground3();      
        addObject(ground32,150,480);

        Ground32 ground322 = new Ground32();
        addObject(ground322,412,480);

        Cloud2 cloud28 = new Cloud2();
        addObject(cloud28,325,114);

        Cloud cloud10 = new Cloud();
        addObject(cloud10,29,363);
        
        Cover1 cover15 = new Cover1();
        addObject(cover15,178,260);
        
        Cover1 cover16 = new Cover1();
        addObject(cover16,293,302);
        
        Cover1 cover17 = new Cover1();
        addObject(cover17,483,224);
        
        Cover1 cover18 = new Cover1();
        addObject(cover18,634,236);
        
        Cover2 cover25 = new Cover2();
        addObject(cover25,643,315);
        
        Cover2 cover26 = new Cover2();
        addObject(cover26,507,303);
        
        Cover2 cover27 = new Cover2();
        addObject(cover27,379,391);
        
        Cover2 cover28 = new Cover2();
        addObject(cover28,175,345);

        Finish3 finish3 = new Finish3();
        addObject(finish3,776,267);
    }

     public Life getLifePanel() {
        return lifePanel;
    }

    public ScorePanel getScorePanel() {
        return scorePanel;
    }
}
