package angryflappybird;

import javafx.scene.image.Image;

/**
 * @author Linh Ngoc Le
 */
public class Pipe extends Sprite {
    boolean isLowerPipe;
    boolean isScored;
    /**
     * @param pX
     * @param pY
     * @param image
     * @param isLowerPipe 
     * @param isScored 
     */
    public Pipe(double pX, double pY, Image image, boolean isLowerPipe) {
        super(pX, pY, image);
        this.isLowerPipe = isLowerPipe;
    }
    

    /**
     * Check if the pipe is lowerPipe or not
     * @return isLowerPipe
     */
    public boolean isLowerPipe() {
        return isLowerPipe;
    }
    
    /**
     * Check if a pipe is scored with goose passing through
     * @return new boolean value for isScored
     */
    public boolean isScored() {
        return isScored;
    }

    /**
     * Set new boolean value for the pipe with goose passing through
     * @param isScored 
     */
    public void setScored(boolean isScored) {
        this.isScored = isScored;
        
    }

}
