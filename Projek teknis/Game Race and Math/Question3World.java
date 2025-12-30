import greenfoot.*;

public class Question3World extends World {
    private boolean buttonClicked = false;
    private Button_Next buttonNext;
    private Button_TryAgain buttonTryAgain;

    public Question3World() {    
        super(800, 600, 1); 
         SoundManager.playMusic("Yamko Rambe Yamko.mp3");
        prepare();
    }

    private void prepare() {
        Soal3 soal3 = new Soal3();
        addObject(soal3, 409, 255);

        Button_A buttonA = new Button_A(this);
        addObject(buttonA, 191, 354);

        Button_B buttonB = new Button_B(this);
        addObject(buttonB, 191, 405);

        Button_C buttonC = new Button_C(this);
        addObject(buttonC, 191, 457);

        Level3 level3 = new Level3();
        addObject(level3, 355, 34);

        buttonNext = new Button_Next();
        addObject(buttonNext, 695, 546);
        buttonNext.getImage().setTransparency(0);

        buttonTryAgain = new Button_TryAgain();
        addObject(buttonTryAgain, 123, 546);
        buttonTryAgain.getImage().setTransparency(0);
    }

    public boolean isButtonClicked() {
        return buttonClicked;
    }

    public void setButtonClicked(boolean clicked) {
        this.buttonClicked = clicked;
    }

    public void showNextButton() {
        buttonNext.getImage().setTransparency(255);
    }

    public void showTryAgainButton() {
        buttonTryAgain.getImage().setTransparency(255);
    }
}
