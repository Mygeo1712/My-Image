import greenfoot.*;

public class Question2World extends World {
    private boolean buttonClicked = false;
    private Button_Next buttonNext;
    private Button_TryAgain buttonTryAgain;

    public Question2World() {    
        super(800, 600, 1); 
        SoundManager.playMusic("Bolelebo-Anak Kambing Saya.mp3");
        prepare();
    }

    private void prepare() {
        Level2 level2 = new Level2();
        addObject(level2, 396, 47);

        Soal2 soal2 = new Soal2();
        addObject(soal2, 401, 271);

        Button_A buttonA = new Button_A(this);
        addObject(buttonA, 205, 340);

        Button_B buttonB = new Button_B(this);
        addObject(buttonB, 205, 393);

        Button_C buttonC = new Button_C(this);
        addObject(buttonC, 205, 445);

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
