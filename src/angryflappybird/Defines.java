package angryflappybird;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Define initial values
 */
public class Defines {
    
	// dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to the goose
    
    final int GOOSE_WIDTH = 80;
    final int GOOSE_HEIGHT = 80;
    final int GOOSE_AUTO_WIDTH = 130;
    final int GOOSE_AUTO_HEIGHT = 90;
    final int GOOSE_POS_X = 70;
    final int GOOSE_POS_Y = 200;
    final int GOOSE_DROP_TIME = 200000000;  	// the elapsed time threshold before the goose starts dropping
    final int GOOSE_DROP_VEL = 350;    		// the goose drop velocity
    int GOOSE_FLY_VEL = -40;
    final int GOOSE_IMG_LEN = 4;
    final int GOOSE_IMG_PERIOD = 5;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 80;
    final int FLOOR_COUNT = 2;
    
    // coefficients related to the pipes - Linh Ngoc Le
    final int PIPES_COUNT = 2;
    final int PIPE_WIDTH = 80;
    final int UPPER_PIPE_HEIGHT = 210;
    final int LOWER_PIPE_HEIGHT = 210;
   
    final int PIPE_MIN_HEIGHT = 120;
    final int PIPE_MAX_HEIGHT = 235;
    
    int PIPES_GAP = 200; 
    
    // velocity at which pipes move - Linh Ngoc Le
    double PIPE_VEL= -0.5;

    // coefficients related to raibow candy - Linh Ngoc Le
    final int RAINBOW_CANDY_WIDTH = 60;
    final int RAINBOW_CANDY_HEIGHT = 60;
    
    // coefficients related to normal candy - Linh Ngoc Le
    final int NORMAL_CANDY_WIDTH = 60;
    final int NORMAL_CANDY_HEIGHT = 60;
    final int CANDY_COUNT = 30;
    double MIN_NORMAL_CANDY_RATE = 0.5;
    double MAX_NORMAL_CANDY_RATE = 0.8;
    
    double MIN_RAINBOW_CANDY_RATE = 0.2;
    double MAX_RAINBOW_CANDY_RATE = 0.4;
    
    // coefficients related to dragon - Linh Ngoc Le
    final int DRAGON_HEIGHT = 60;
    final int DRAGON_WIDTH = 60;
    double DRAGON_DROP_VEL = 0.05;
    double MIN_DRAGON_RATE = 0.3;
    double MAX_DRAGON_RATE = 0.5;
    
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    double BACKGROUND_TIME = 0;
    
    // coefficients related to scores and lives
    int TOTAL_LIVES = 3;
    int TOTAL_SCORES = 0;
    int SNOOZE_TIME = 6;
    
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird - George Version";
    private final String IMAGE_DIR = "../resources/images/";
    
    final String[] IMAGE_FILES = {"day_background", "night_background", "goose0", "goose1", "goose2", "goose3", "floor", "lower_pipe", "upper_pipe", "rainbow_candy", "normal_candy", "dragon", "auto"};  
    
    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    final ArrayList<String> levels = new ArrayList<>(Arrays.asList("Easy", "Medium", "Hard"));

    // Coefficients related to sounds
    
    
    
    // nodes on the scene graph - start game, level selection, instruction box
    Button startButton;
    ComboBox<String> levelSelection;
    VBox instruction;
    
    // constructor
    // Linh Ngoc Le
	Defines() {
		
		// initialize images
		for(int i=0; i<IMAGE_FILES.length; i++) {
			Image img;
			if (i == 0 | i == 1) {
                img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
            }
			else if (i == 2 || i == 3 || i == 4 || i == 5){
				img = new Image(pathImage(IMAGE_FILES[i]), GOOSE_WIDTH, GOOSE_HEIGHT, false, false);
			}
			else if (i == 6) {
                img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
            }
			else if (i == 7) {
			    img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, LOWER_PIPE_HEIGHT, false, false);
			}
			else if (i == 8) {
			    img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, UPPER_PIPE_HEIGHT, false, false);
            }
			else if (i == 9) {
			    img = new Image(pathImage(IMAGE_FILES[i]), RAINBOW_CANDY_WIDTH, RAINBOW_CANDY_HEIGHT, false, false);
			}
			else if (i == 10) {
			    img = new Image(pathImage(IMAGE_FILES[i]), NORMAL_CANDY_WIDTH, NORMAL_CANDY_HEIGHT, false, false);
			} 
			else if (i == 12) {
			    img = new Image(pathImage(IMAGE_FILES[i]), GOOSE_AUTO_WIDTH, GOOSE_AUTO_HEIGHT, false, false);
			}
			else {
			    img = new Image(pathImage(IMAGE_FILES[i]), DRAGON_WIDTH, DRAGON_HEIGHT, false, false);
			}
    		IMAGE.put(IMAGE_FILES[i],img);
    	}
		
		// initialize image views
		for(int i = 0; i < IMAGE_FILES.length; i++) {
    		ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
    		IMVIEW.put(IMAGE_FILES[i],imgView);
    	}
		
		// initialize scene nodes - Linh Ngoc Le
		startButton = new Button("Start game!");
		levelSelection = new ComboBox<String>();
		levelSelection.getItems().addAll(levels);
		levelSelection.getSelectionModel().selectFirst(); 

		
		instruction = new VBox(10);
//		row 1: Bonus point (normal candy)
		VBox row1 = new VBox(10);
		Label labelBonusPoints = new Label("Bonus points");
		row1.getChildren().addAll(IMVIEW.get(IMAGE_FILES[10]), labelBonusPoints);
		
//		row 2: Snooze (rainbow candy)
		VBox row2 = new VBox(10);
        Label labelSnooze = new Label("Let you snooze");
        row2.getChildren().addAll(IMVIEW.get(IMAGE_FILES[9]), labelSnooze);
		
//      row3: Avoid dragons
        VBox row3 = new VBox(10);
        Label labelDragon = new Label("Avoid dragons");
        row3.getChildren().addAll(IMVIEW.get(IMAGE_FILES[11]),labelDragon);
		
        instruction.getChildren().addAll(row1, row2, row3);
	}
	
	/**
	 * @param filepath
	 * @return fullpath
	 */
	public String pathImage(String filepath) {
    	String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
    	return fullpath;
    }
	
	/**
	 * @param filepath
	 * @param width
	 * @param height
	 * @return resized image
	 */
	public Image resizeImage(String filepath, int width, int height) {
    	IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
    	return IMAGE.get(filepath);
    }
	
	
	
//	COEFFICIENTS FOR DIFFERENT LEVELS 
	/**
     * Reset sprites coefficients for easy level
     * @author Linh Ngoc Le
     */
	public void easyLevel() {
	    this.GOOSE_FLY_VEL = -40;
	    this.PIPE_VEL= -0.4;
	    this.PIPES_GAP = 200; 
	    this.MIN_NORMAL_CANDY_RATE = 0.5;
	    this.MAX_NORMAL_CANDY_RATE = 0.8;
	    this.MIN_RAINBOW_CANDY_RATE = 0.1;
	    this.MAX_RAINBOW_CANDY_RATE = 0.4;
	    this.DRAGON_DROP_VEL = 3;
	    this.MIN_DRAGON_RATE = 0.3;
	    this.MAX_DRAGON_RATE = 0.7;
	}
	
	/**
	 * Reset sprites coefficients for easy level
     * @author Linh Ngoc Le
	 */
	public void mediumLevel() {
	    this.GOOSE_FLY_VEL = -45;
	    this.PIPE_VEL= -0.7;
	    this.PIPES_GAP = 190; 
	    this.MIN_NORMAL_CANDY_RATE = 0.5;
        this.MAX_NORMAL_CANDY_RATE = 0.8;
        this.MIN_RAINBOW_CANDY_RATE = 0.2;
        this.MAX_RAINBOW_CANDY_RATE = 0.4;
        this.DRAGON_DROP_VEL = 1;
        this.MIN_DRAGON_RATE = 0.3;
        this.MAX_DRAGON_RATE = 0.8;
	}
	
	/**
	 * Reset sprites coefficients for easy level
     * @author Linh Ngoc Le
	 */
	public void hardLevel() {
	    this.GOOSE_FLY_VEL = -50;
	    this.PIPE_VEL= -0.8;
	    this.PIPES_GAP = 200;
	    this.MIN_NORMAL_CANDY_RATE = 0.6;
        this.MAX_NORMAL_CANDY_RATE = 0.8;
        this.MIN_RAINBOW_CANDY_RATE = 0.3;
        this.MAX_RAINBOW_CANDY_RATE = 0.4;
        this.DRAGON_DROP_VEL = 1;
        this.MIN_DRAGON_RATE = 0.3;
        this.MAX_DRAGON_RATE = 0.9;
	}
}
