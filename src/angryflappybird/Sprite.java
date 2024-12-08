package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * 
 */
public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private String IMAGE_DIR = "../resources/images/";
//     private String IMAGE_DIR = "/angryflappybird-lmao/src/images/";

    /**
     * 
     */
    public Sprite() {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /**
     * @param pX
     * @param pY
     * @param image
     */
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    /**
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * @param positionX
     * @param positionY
     */
    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * @return positionX: position at x-axis of an object
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * @return positionY: position at y-axis of an object
     */
    public double getPositionY() {
        return positionY;
    }
    
    /**
     * @author Ngoc Linh Le
     * @param positionY 
     */
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }
    
    /**
     * @param velocityX
     * @param velocityY
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * @param x
     * @param y
     */
    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }

    /**
     * @return
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * @return
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * @return
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * @param width: new width
     * @return
     */
    public void setWidth(double width) {
        this.width = width;
        
    }
    
    /**
     * @return
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * @param height
     * @return
     */
    public void setHeight(double height) {
        this.height = height;
        
    } 
    /**
     * @param gc
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    /**
     * @return
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    /**
     * @param s
     * @return
     */
    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    /**
     * @param time
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    /**
     * @return
     */
    public String getIMAGE_DIR() {
        return IMAGE_DIR;
    }

    /**
     * @param iMAGE_DIR
     */
    public void setIMAGE_DIR(String iMAGE_DIR) {
        IMAGE_DIR = iMAGE_DIR;
    }
}
