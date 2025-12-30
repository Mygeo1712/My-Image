import greenfoot.*;

public class Question1World extends World {
    private Button_Next buttonNext;
    private Button_TryAgain buttonTryAgain;
    private boolean buttonClicked = false;

    public Question1World() {    
        super(800, 600, 1);
        SoundManager.playMusic("Rasa Sayange.mp3");
        prepare();
    }

    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare() {
        Soal1 soal1 = new Soal1();
        addObject(soal1, 400, 259);

        Level1 level1 = new Level1();
        addObject(level1, 396, 30);

        Button_A button_A = new Button_A(this);
        addObject(button_A, 205, 339);

        Button_B button_B = new Button_B(this);
        addObject(button_B, 205, 394);

        Button_C button_C = new Button_C(this);
        addObject(button_C, 205, 448);

        // Inisialisasi buttonNext menggunakan variabel instans
        buttonNext = new Button_Next();
        addObject(buttonNext, 695, 546);
        if (buttonNext.getImage() != null) {
            buttonNext.getImage().setTransparency(0); // Sembunyikan tombol Next
        }

        // Inisialisasi buttonTryAgain menggunakan variabel instans
        buttonTryAgain = new Button_TryAgain();
        addObject(buttonTryAgain, 123, 546);
        if (buttonTryAgain.getImage() != null) {
            buttonTryAgain.getImage().setTransparency(0); // Sembunyikan tombol TryAgain
        }
    }

    public boolean isButtonClicked() {
        return buttonClicked;
    }

    public void setButtonClicked(boolean value) {
        this.buttonClicked = value;
    }

    public void showNextButton() {
        if (buttonNext != null && buttonNext.getImage() != null) {
            buttonNext.getImage().setTransparency(255); // Tampilkan tombol Next
        }
    }

    public void showTryAgainButton() {
        if (buttonTryAgain != null && buttonTryAgain.getImage() != null) {
            buttonTryAgain.getImage().setTransparency(255); // Tampilkan tombol TryAgain
        }
    }
}
