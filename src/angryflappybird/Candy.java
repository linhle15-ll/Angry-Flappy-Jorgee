package angryflappybird;

import javafx.scene.image.Image;

/**
 * @author Linh Ngoc Le
 */
public class Candy extends Sprite {
    boolean isRainbowCandy;

    /**
     * @param pX
     * @param pY
     * @param image
     * @param isRainbowCandy 
     */
    public Candy(double pX, double pY, Image image, boolean isRainbowCandy) {
        super(pX, pY, image);
        this.isRainbowCandy = true;
        
    }

    /**
     * Check if the candy is a rainbow candy or not
     * @return isRainbowCandy
     */
    public boolean isRainbowCandy() {
        return isRainbowCandy;
    }
    
    /**
     * set new boolean value for the candy
     * @param isRainbowCandy
     */
    public void setRainbowCandy(boolean isRainbowCandy) {
        this.isRainbowCandy = isRainbowCandy;
        
    }
}
