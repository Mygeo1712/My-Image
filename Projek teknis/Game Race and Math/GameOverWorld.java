import greenfoot.*;

public class GameOverWorld extends World {
    public GameOverWorld() {

        super(800, 600, 1); // Atur ukuran dunia

        prepare();
    }


    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        Button_Retry button_Retry = new Button_Retry();
        addObject(button_Retry,140,496);
        

        Button_Exit button_Exit = new Button_Exit();
        addObject(button_Exit,644,496);

        GameOver gameOver = new GameOver();
        addObject(gameOver,461,91);
    }

}
