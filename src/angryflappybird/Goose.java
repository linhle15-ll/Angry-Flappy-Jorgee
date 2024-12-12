package angryflappybird;

import javafx.scene.image.Image;

/**
 * @param pX
 * @param pY
 * @param image
 * @author Linh Ngoc Le
 */
public class Goose extends Sprite{
    /**
     * @param pX
     * @param pY
     * @param image
     */
    public Goose(double pX, double pY, Image image) {
        super(pX, pY, image);
    }
    
    /**
     * @param pipe
     * @return
     */
    public boolean isPassingPipe(Pipe pipe) {
        // Check if the goose has passed the pipe
        return this.getPositionX() > pipe.getPositionX() + pipe.getWidth();
    }
    

}