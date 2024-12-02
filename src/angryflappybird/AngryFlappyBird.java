package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 */
//The Application layer
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;
    
    // game components - Linh Ngoc Le
    private Goose goose;
    private ArrayList<Floor> floors;
    private ArrayList<Pipe> pipes;
    private ArrayList<Candy> candies;
    private ArrayList<Dragon> dragons;
    
    // Score texts
    private Text scoreText;
    private int score;
    private Text livesTexts;
    private int lives;
    
    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private GraphicsContext gc;		
    
	// the mandatory main method 
    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
       
    // the start method sets the Stage layer
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	// initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
    	resetGameScene(true);  // resets the gameScene
    	
        HBox root = new HBox();
		HBox.setMargin(gameScene, new Insets(0,0,0,15));
		root.getChildren().add(gameScene);
		root.getChildren().add(gameControl);
		
		// add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);
        
        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    // the getContent method sets the Scene layer - Linh Ngoc Le
//    Set the gameControl UI (left side of the gameScene)
    private void resetGameControl() {
        
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
       // DEF.levelSelection.setOnAction(this::onLevelChange);
        gameControl = new VBox(20);
        gameControl.setPadding(new Insets(10,10,10,10));
        gameControl.getChildren().addAll(DEF.startButton, DEF.levelSelection, DEF.instruction);
    }
    
    private void mouseClickHandler(MouseEvent e) {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	else if (GAME_START){
            clickTime = System.nanoTime();   
        }
    	GAME_START = true;
        CLICKED = true;
    }
    
    /**
     * Generate a random height value for the pipes
     * @param maxH: max height of a pipe
     * @param minH: min height of a pipe
     * @return randomHeight: random height value
     * @author: Linh Ngoc Le
     */
    public int generateRandomHeight(int maxH, int minH) {
        int randomHeight = (int)(Math.random() * (maxH - minH + 1));
        return randomHeight;
    }
    
    private void resetGameScene(boolean firstEntry) {
    	
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        
        // Create arrays to hold Sprite objects
        floors = new ArrayList<>();
        pipes = new ArrayList<>();
        dragons = new ArrayList<>();
        candies = new ArrayList<>();
        
    	if(firstEntry) {
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            ImageView dayBackground = DEF.IMVIEW.get("day_background");
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(dayBackground, canvas);
    	}
    	
    	// initialize floor
    	for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    		
    		int posX = i * DEF.FLOOR_WIDTH;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    		Floor floor = new Floor(posX, posY, DEF.IMAGE.get("floor"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);
    		
    		floors.add(floor);
    	}
        
        // initialize goose
        goose = new Goose(DEF.GOOSE_POS_X, DEF.GOOSE_POS_Y,DEF.IMAGE.get("goose0"));
        goose.render(gc);
        
        // initialize pipes - Linh Ngoc Le
        initializePipes();
        
        // initialize timer
        startTime = System.nanoTime();
//      backgroundTime = 0;
        timer = new MyTimer();
        timer.start();
    }

    private void initializePipes() {
        Pipe lowerPipe = new Pipe(100, DEF.SCENE_HEIGHT -  generateRandomHeight(DEF.PIPE_MAX_HEIGHT, DEF.PIPE_MIN_HEIGHT), DEF.IMAGE.get("lower_pipe")); 
        Pipe upperPipe = new Pipe(100, generateRandomHeight(0, -50), DEF.IMAGE.get("upper_pipe"));
       
        lowerPipe.setVelocity(DEF.PIPE_VEL_EASY, 0);
        upperPipe.setVelocity(DEF.PIPE_VEL_EASY, 0);
        
        pipes.add(upperPipe);
        pipes.add(lowerPipe);
        
        // pipes are rendered at the start upperPipe.render(gc);
        lowerPipe.render(gc);
        upperPipe.render(gc);
    }

    //timer stuff
    class MyTimer extends AnimationTimer {
    	
    	int counter = 0;
    	
    	 @Override
    	 public void handle(long now) {   		 
    		 // time keeping
    	     elapsedTime = now - startTime;
    	     
    	     startTime = now;
    	     
    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

    	     if (GAME_START) {
    	    	 // step1: update floor
    	    	 moveFloor();
    	    	 
    	    	 // step2: update blob
    	    	 moveBlob();
    	    	 
    	    	 // step3: update pipes - Linh Ngoc Le
                 movePipes();
                               
    	    	 checkCollision();
    	     }
    	 }
    	 
    	 // step1: update floor
    	 private void moveFloor() {
    		for (int i=0; i<DEF.FLOOR_COUNT; i++) {
    			if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
    				double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
    	        	double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    	        	floors.get(i).setPositionXY(nextX, nextY);
    			}
    			floors.get(i).render(gc);
    			floors.get(i).update(DEF.SCENE_SHIFT_TIME);
    		}
    	 }
    	 
    	 // step2: update blob
    	 private void moveBlob() {
			long diffTime = System.nanoTime() - clickTime;
			
			// blob flies upward with animation
			if (CLICKED && diffTime <= DEF.GOOSE_DROP_TIME) {
				
				int imageIndex = Math.floorDiv(counter++, DEF.GOOSE_IMG_PERIOD);
				imageIndex = Math.floorMod(imageIndex, DEF.GOOSE_IMG_LEN);
				goose.setImage(DEF.IMAGE.get("goose"+String.valueOf(imageIndex)));
				goose.setVelocity(0, DEF.GOOSE_FLY_VEL);
			}
			// blob drops after a period of time without button click
			else {
			    goose.setVelocity(0, DEF.GOOSE_DROP_VEL); 
			    CLICKED = false;
			}

			// render blob on GUI
			goose.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			goose.render(gc);
    	 }
    	 
    	 // Step3: Move pipes
 
    	 private void movePipes() {
             for (Pipe pipe : pipes) {
                 if (pipes.size() != 0) {
                     int lastPipe = pipes.size() - 1;
                     
                     if (pipes.get(lastPipe).getPositionX() == DEF.SCENE_WIDTH / 2 - 10) {
                         initializePipes();
                     } else if (pipes.get(lastPipe).getPositionX() <= -DEF.FLOOR_WIDTH ) {
                         pipes.remove(0);
                         pipes.remove(0);
                     }
                 } else { 
                     initializePipes();
                 }
                 pipe.render(gc);
                 pipe.update(DEF.SCENE_SHIFT_TIME);
                 
             }
         }
    	 
    	 void checkCollision() {
    		 
    		// check collision  
			for (Floor floor: floors) {
				GAME_OVER = GAME_OVER || goose.intersectsSprite(floor);
			}
			
			// end the game when blob hit stuff
			if (GAME_OVER) {
				showHitEffect(); 
				for (Floor floor: floors) {
					floor.setVelocity(0, 0);
				}
				timer.stop();
			}
			
    	 }
    	 
	     private void showHitEffect() {
	        ParallelTransition parallelTransition = new ParallelTransition();
	        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
	        fadeTransition.setToValue(0);
	        fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
	        fadeTransition.setAutoReverse(true);
	        parallelTransition.getChildren().add(fadeTransition);
	        parallelTransition.play();
	     }
    	 
    } // End of MyTimer class

} // End of AngryFlappyBird Class

