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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

// for adding sound
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

/**
 * 
 */
//The Application layer
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private long bounceBackTime;
    private AnimationTimer timer;
    long bounceDurationNano = (long) (1 * 1e9);
    
    long endTime;
    
    // game components - Linh Ngoc Le
    private Goose goose;
    private ArrayList<Floor> floors;
    private ArrayList<Pipe> pipes;
    private ArrayList<Candy> candies;
    private ArrayList<Dragon> dragons;
    
    // Score texts
    private Text scoreText;
    private Text livesText;
    private Text snoozeText;
    private Text gameOverText;
    private Text getReadyText;
    
    // Background state and transition timer - Melita Madhurza
    private boolean isDay = true; // Track whether it's currently day
    private long lastBackgroundSwitchTime = 0; // Time when the last switch occurred
    private final long BACKGROUND_SWITCH_INTERVAL = 5_000_000_000L; // 3 seconds in nanoseconds

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    private boolean hasCollidedWithPipe;
    private boolean isAutoPilot;
    private boolean inBounceBackMode;
    private boolean FRESH_ENTRY; // if Entering game from fresh start, or set true after GAME_OVER = true (lives is 0)  
    
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
    	    resetGameScene(true);
        }
    	else if (GAME_START){
            clickTime = System.nanoTime();   
        }
    	GAME_START = true;
        CLICKED = true;
        
        gameScene.getChildren().removeIf(node -> node instanceof Text && ((Text) node).getText().equals("Get Ready"));
    }
   
    private void resetGameScene(boolean isFreshEntry) {
        FRESH_ENTRY = isFreshEntry;
        
        // Clear the gameScene
        if (gameScene == null) {
            gameScene = new Group();
        } else {
            gameScene.getChildren().clear();
        }
        
    	// reset variables
        GAME_OVER = false;
       
        hasCollidedWithPipe = false;
        isAutoPilot = false;
        inBounceBackMode = false;
        FRESH_ENTRY = true; 
        
        
        // display text "Get Ready" until start game is clicked 
        // only for first entry/ entry after game over (later replaced with better graphic) - Linh Ngoc Le
        getReadyText = new Text(50,250, "");    
        getReadyText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        getReadyText.setStroke(Color.BLACK);
        getReadyText.setFill(Color.RED);
        
        // if reset game scene from the start or after game over
        if (isFreshEntry) {
            GAME_START = false;
            CLICKED = false;
            isDay = true;
            getReadyText.setText("Get Ready");
            // score
            DEF.TOTAL_SCORES = 0;
            
            scoreText = new Text(25, 60, Integer.toString(DEF.TOTAL_SCORES));
            scoreText.setFont(Font.font("Arial", FontWeight.NORMAL,50));
            scoreText.setFill(Color.YELLOW);
            scoreText.setStroke(Color.BLACK);
            
            // lives to go
            DEF.TOTAL_LIVES = 3;
            livesText = new Text(210, 540, Integer.toString(DEF.TOTAL_LIVES) + " lives left");
            livesText.setFont(Font.font("Arial", FontWeight.BOLD,30));
            livesText.setFill(Color.RED);
        } 
        
        // if not a FRESH_TRY (reset game scene after bumping into the pipe BUT score > 0
        else if (!isFreshEntry){ // condition for !FRESH_ENTRY is scores = 0 --> FRESH_ENTRY = false
            GAME_START = true;
            CLICKED = true;
            scoreText = new Text(25, 60, Integer.toString(DEF.TOTAL_SCORES));
            scoreText.setFont(Font.font("Arial", FontWeight.NORMAL,50));
            scoreText.setFill(Color.YELLOW);
            scoreText.setStroke(Color.BLACK);
            
            // lives to go
            livesText = new Text(210, 540, Integer.toString(DEF.TOTAL_LIVES) + " lives left");
            livesText.setFont(Font.font("Arial", FontWeight.BOLD,30));
            livesText.setFill(Color.RED);
        }
           // Reset the background switch timer -- Melita Madhurza
           lastBackgroundSwitchTime = System.nanoTime(); // Ensure the timer starts fresh

    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();
            
            // Dynamically set the background based on the current time of day - Melita
            ImageView background = isDay ? DEF.IMVIEW.get("day_background") : DEF.IMVIEW.get("night_background");
            
            gameScene.getChildren().addAll(background, canvas);
            
            // display text "Game Over" when GAME_OVER = True
            gameOverText = new Text(50, 250, "");
            gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
            gameOverText.setStroke(Color.BLACK);
            gameOverText.setFill(Color.RED);
            
            gameScene.getChildren().addAll(getReadyText, gameOverText);
            
            // snooze time on autopilot
            snoozeText = new Text(20, 95, "" + ""); // SET SNOOZE TIME ON AUTOPILOT
            snoozeText.setFont(Font.font("Arial", FontWeight.NORMAL, 30));
            scoreText.setFill(Color.YELLOW);
            
            // Add the text to the pane
            gameScene.getChildren().addAll(scoreText, livesText, snoozeText);
           
    	
            // Clear and reinitialize dynamic objects
            if (floors == null) floors = new ArrayList<>();
            floors.clear();
            
            for (int i = 0; i < DEF.FLOOR_COUNT; i++) {
                int posX = i * DEF.FLOOR_WIDTH;
                int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                Floor floor = new Floor(posX, posY, DEF.IMAGE.get("floor"));
                floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
                floors.add(floor);
            }

            if (pipes == null) pipes = new ArrayList<>();
            pipes.clear();
            initializePipes();

            if (candies == null) candies = new ArrayList<>();
            candies.clear();

            if (dragons == null) dragons = new ArrayList<>();
            dragons.clear();

            // Initialize goose
            if (goose == null) {
                goose = new Goose(DEF.GOOSE_POS_X, DEF.GOOSE_POS_Y, DEF.IMAGE.get("goose0"));
            }
            goose.setPositionXY(DEF.GOOSE_POS_X, DEF.GOOSE_POS_Y);
            
        // initialize timer
        startTime = System.nanoTime();
        
        // ensure timer is stopped before starting a new one
        if (timer != null) {
            timer.stop();
        }
        timer = new MyTimer();
        timer.start();
    }
    
    /**
     * initialize switchBackground
     * toggle between day and night
     * @author Melita Madhurza
     */
    private void switchBackground() {

        isDay = !isDay; // Toggle day/night
        
        ImageView newBackground = isDay ? DEF.IMVIEW.get("day_background") : DEF.IMVIEW.get("night_background");
        gameScene.getChildren().set(0, newBackground); // Replace the current background
    }

    /**
     * Generate a random height value for the pipes
     * @param maxH: max height of a pipe
     * @param minH: min height of a pipe
     * @return randomHeight: random height value
     * @author: Linh Ngoc Le
     */
    @SuppressWarnings("static-method")
    public int generateRandomHeight(int maxH, int minH) {
        int randomHeight = (int)(Math.random() * (maxH - minH)) + minH + 3;
        return randomHeight;
    }
    
    /**
     * initialize candies with positions on random lowerPipes
     * @author Linh Ngoc Le
     * @param random 
     */
    private void initializeCandies(double pipeWidthCandyPosX, double pipeWidthCandyPosY) {
        
        Candy rainbowCandy;
        Candy normalCandy;
        
        double random = Math.random();  //random coefficient for candies 
        
        if (0.45 >= random && random > 0.1) {
            rainbowCandy = new Candy(
                    pipeWidthCandyPosX, 
                    pipeWidthCandyPosY - DEF.RAINBOW_CANDY_HEIGHT, 
                    DEF.IMAGE.get("rainbow_candy"), 
                    true
            );
            
            rainbowCandy.setHeight(DEF.RAINBOW_CANDY_HEIGHT);
            rainbowCandy.setWidth(DEF.RAINBOW_CANDY_WIDTH);
            rainbowCandy.setVelocity(DEF.PIPE_VEL_EASY, 0);
            rainbowCandy.render(gc);
            candies.add(rainbowCandy);
            
        } else if (0.45 < random && random <= 0.9) {
                
            normalCandy = new Candy(
                    pipeWidthCandyPosX, 
                    pipeWidthCandyPosY - DEF.NORMAL_CANDY_HEIGHT, 
                    DEF.IMAGE.get("normal_candy"), 
                    false
            );
            normalCandy.setHeight(DEF.NORMAL_CANDY_HEIGHT);
            normalCandy.setWidth(DEF.NORMAL_CANDY_WIDTH);
            normalCandy.setVelocity(DEF.PIPE_VEL_EASY, 0);
            normalCandy.render(gc);
            candies.add(normalCandy);
        }
       
    }
    
    /**
     * initialize pipes
     * @author Linh Ngoc Le
     */
    private void initializePipes() {
        Pipe lowerPipe = new Pipe(400, DEF.SCENE_HEIGHT -  generateRandomHeight(DEF.PIPE_MAX_HEIGHT, DEF.PIPE_MIN_HEIGHT), DEF.IMAGE.get("lower_pipe"), true); 
        Pipe upperPipe = new Pipe(400, generateRandomHeight(0, -40), DEF.IMAGE.get("upper_pipe"), false);
       
        lowerPipe.setVelocity(DEF.PIPE_VEL_EASY, 0);
        upperPipe.setVelocity(DEF.PIPE_VEL_EASY, 0);
        
        pipes.add(upperPipe);
        pipes.add(lowerPipe);
        
        // initialize candies on lower pipes
       
        
        double lowerPipeWidthCandyPosX = lowerPipe.getPositionX();
        double lowerPipeWidthCandyPosY = lowerPipe.getPositionY();
                
        if (lowerPipeWidthCandyPosX >= DEF.SCENE_WIDTH) {
            initializeCandies(lowerPipeWidthCandyPosX, lowerPipeWidthCandyPosY);
        }            
       
        // initialize dragons from upper pipes
        double upperPipeWithDragonPosX = upperPipe.getPositionX();
        
        if (upperPipeWithDragonPosX >= DEF.SCENE_WIDTH) {
            initializeDragons(upperPipeWithDragonPosX);
        }

    }

    /**
     * Initialize dragons
     * Dragon drops randomly from the sky (from some random upperPipe)
     * @author Linh Ngoc Le
     */
    private void initializeDragons(double upperPipeWithDragonPosX) { //////////////////////////////////
        Random random = new Random();
        if (random.nextDouble() < 0.3) { // Adjust drop rate
            Dragon dragon = new Dragon(
                upperPipeWithDragonPosX, 
                -DEF.DRAGON_DROP_VEL, 
                DEF.IMAGE.get("dragon")
            );
            dragon.setHeight(DEF.DRAGON_HEIGHT);
            dragon.setWidth(DEF.DRAGON_WIDTH);
            dragon.setVelocity(upperPipeWithDragonPosX, -DEF.DRAGON_DROP_VEL);
            dragons.add(dragon);
            
            System.out.println("PRINT DRAGON COEFFICIENTS");
            System.out.println(dragon.getPositionX());
            System.out.println(dragon.getPositionY());
        }
    }
    
    //timer stuff
    class MyTimer extends AnimationTimer {
    	
    	int counter = 0;
    	
    	 @Override
    	 public void handle(long now) {
    	     // update the score and lives text
    	     scoreText.setText(Integer.toString(DEF.TOTAL_SCORES));
    	     livesText.setText(Integer.toString(DEF.TOTAL_LIVES) + " lives left.");
    	     
    	     if (now - lastBackgroundSwitchTime >= BACKGROUND_SWITCH_INTERVAL) {
    	         lastBackgroundSwitchTime = now; // Update the last switch time
    	         if (GAME_START && !GAME_OVER) {
    	             switchBackground(); // Toggle the background
    	         }
    	     }

    		 // time keeping
    	     elapsedTime = now - startTime;
    	     startTime = now;
    	     bounceBackTime = now;
    	     
    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
    	     
    	  // if bounceBackMode is on and we have finished the time of boucing back
             if (inBounceBackMode && bounceBackTime > endTime) {
                 inBounceBackMode = false; // turn bounceBackMode off
                  
                 resetGameScene(false);
                 GAME_START = true;
             }

    	     if (GAME_START) {
    	    	 // step1: update floor
    	    	 moveFloor();
    	    	 
    	    	 // step2: update blob
    	    	 moveBlob();
    	    	 
    	    	 // step3: update pipes, candies, dragons - Linh Ngoc Le
                 movePipes();
                 moveCandies();
                 moveDragons();
                               
                 // step 4: Check for collision in between objects - Linh Ngoc Le
    	    	 checkCollision();
    	     } 
    	         
    	     if (isAutoPilot) { 
    	         autoPilot();
    	         isAutoPilot = false;
    	     }
    	    
    	 }
    	 
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
    	 
    	 private void moveBlob() {
			long diffTime = System.nanoTime() - clickTime;
			
			// blob flies upward with animation
			if (CLICKED && diffTime <= DEF.GOOSE_DROP_TIME && !inBounceBackMode) {
				
				int imageIndex = Math.floorDiv(counter++, DEF.GOOSE_IMG_PERIOD);
				imageIndex = Math.floorMod(imageIndex, DEF.GOOSE_IMG_LEN);
				goose.setImage(DEF.IMAGE.get("goose"+String.valueOf(imageIndex)));
				goose.setVelocity(0, DEF.GOOSE_FLY_VEL);
			}
			// blob drops after a period of time without button click
			else if (CLICKED && diffTime > DEF.GOOSE_DROP_TIME && !inBounceBackMode) {
			    goose.setVelocity(0, DEF.GOOSE_DROP_VEL); 
			    CLICKED = false;
			}

			// render blob on GUI
			goose.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			goose.render(gc);
    	 }
    	
    	 /**
    	  * Move pipes
    	  * remove pipes if out of bound
    	  * remove candies and dragons if the pipes are out of bound
    	  * @author Linh Ngoc Le
    	  */
    	 private void movePipes() {
             for (Pipe pipe : pipes) {
                 
                 if (pipes.size() != 0) {
                     int lastPipe = pipes.size() - 1;
                     // int lastCandies = candies.size() - 1;
                     if (pipes.get(lastPipe).getPositionX() == DEF.SCENE_WIDTH / 2 - 20) {
                         initializePipes();
                         
                     } else if (pipes.get(lastPipe).getPositionX() <= -DEF.SCENE_WIDTH + 10) {
                         pipes.remove(0);
                     }
                     
                 } else { 
                     initializePipes();
                 }
                 
                 pipe.render(gc);
                 pipe.update(DEF.SCENE_SHIFT_TIME);
             }
         }
    	 
    	 /**
          * Move candies
          * @author Linh Ngoc Le
          */
         private void moveCandies() {
             for (Candy candy: candies) {
                 candy.render(gc);
                 candy.update(DEF.SCENE_SHIFT_TIME);
             }
         }
         
         /**
          *  move dragons
          *  drop from above the scene height to below the floor
          */
         private void moveDragons() {
             for (Dragon dragon: dragons) {
                 dragon.update(DEF.SCENE_SHIFT_TIME);
                 dragon.render(gc);
                  
             }
         }
         
         /**
          * if isAutoPilot mode is on, then enter autoPilot
          * snooze for 6 seconds without hindrance 
          * @author Linh Ngoc Le
          */
         private void autoPilot() { ///////////// CHECKKK
             goose.setImage(DEF.IMAGE.get("auto"));
             goose.setVelocity(0, 0);  // Stop any current movement for autopilot control

             // Check the position of pipes ahead to avoid collisions
             for (Pipe pipe : pipes) {
                 // Calculate the vertical position where the goose needs to be to avoid this pipe
                 double targetPositionY = pipe.getPositionY() + pipe.getHeight() + DEF.PIPES_GAP / 2;
                 
                 // If the goose is within range of this pipe, adjust the Y velocity to move towards the target position
                 if (goose.getPositionX() + goose.getWidth() > pipe.getPositionX() && 
                     goose.getPositionX() < pipe.getPositionX() + pipe.getWidth()) {
                     double distanceToTarget = targetPositionY - goose.getPositionY();
                     if (Math.abs(distanceToTarget) > 1) {
                         // Adjust the velocity to move the goose towards the target position
                         goose.setVelocity(0, distanceToTarget > 0 ? 1 : -1);
                     }
                     break; // Only adjust once per pipe to avoid excessive changes
                 }
             }
         }
         
         /**
          * Bounce back the goose diagonally downward and backward when it hits the pipe or the dragon.
          * The animation lasts 2 seconds.
          * @author Linh Ngoc Le
          */
         public void bounceBack() {
             inBounceBackMode = true;
             CLICKED = false;

             long startTimeBounceBack = System.nanoTime();
             bounceBackTime = System.nanoTime();
             endTime = bounceBackTime + bounceDurationNano;
             
             // update bounceElapsedTime?
             if (bounceBackTime > startTimeBounceBack && bounceBackTime < endTime) {
                 
                 goose.setVelocity(-goose.getPositionX(), goose.getPositionY());
                 
                 for (Pipe pipe: pipes) {
                     pipe.setVelocity(0, 0);
                 }
                 for (Floor floor: floors) {
                     floor.setVelocity(0, 0);
                 }
                 for (Dragon dragon: dragons) {
                     dragon.setVelocity(0, 0);
                 }
                 for (Candy candy: candies) {
                     candy.setVelocity(0, 0);
                 }
                 System.out.println("WITHIN BOUNCEBACK MODE");       
             } 
         }

         // CHECK FOR COLLISIONS
         /**
          * Check for collisions
          * @author Linh Ngoc Le
          */
        // Flag to prevent multiple deductions for the same collision

         private void checkCollision() {
             
             // Check collision: Goose - Floor 
             for (Floor floor: floors) {
                 if (goose.intersectsSprite(floor)) {
                     if (!hasCollidedWithPipe && !inBounceBackMode) {
                         GAME_OVER = true;
                     }
                     else if (hasCollidedWithPipe && DEF.TOTAL_LIVES > 0) {
                         GAME_OVER = false;
                     }
                 }
             }
             
             // Check collision: Goose - Dragon 
             // Keep track with the colission flags
             for (Dragon dragon: dragons) {
                 if (goose.intersectsSprite(dragon)) {
                     bounceBack();
                     GAME_OVER = true;
                 }
             }
             
             // Check collision: Goose - Pipe
             // Track how many pipes the goose is colliding with in the current frame
             // this solves the problem of incrementing the score by 2 each time hitting just a pipe
             
             for (Pipe pipe: pipes) {
                 if (goose.intersectsSprite(pipe) && !hasCollidedWithPipe) {
                     bounceBack();

                    
                     // Decrease lives and update the text
                     DEF.TOTAL_LIVES -= 1;
                         
                     // Mark that a collision has occurred
                     hasCollidedWithPipe = true;

                     // If no lives left, end the game
                     if (DEF.TOTAL_LIVES == 0) {
                         GAME_OVER = true;
                        
                     }
                     
                 } else if (!goose.intersectsSprite(pipe)) { 
                  // Check if the pipe has not been scored and the goose has passed it
                     if (!pipe.isScored() && goose.getPositionX() > pipe.getPositionX() + pipe.getWidth()) {
                         pipe.setScored(true); // Mark this pipe as scored

                         // Check if the goose has collided with a candy
                         boolean candyCollision = false;
                         for (Candy candy : candies) {
                             if (goose.intersectsSprite(candy)) {
                                 candyCollision = true;
                                 break;
                             }
                         }

                         // Increment score only if no candy collision
                         if (!candyCollision) {
                             DEF.TOTAL_SCORES ++;
                         }
                     }
                 }
                 
             }

             // Check for collision: Goose - Candies
             if (candies.size() != 0) {
                 for (Candy candy : candies) {
                     if (goose.intersectsSprite(candy)) {
                         System.out.println("BUMP INTO RAINBOW CANDY");
                         if (candy.isRainbowCandy()) {
                             candies.remove(candy);
                             isAutoPilot = true;
                         } else {
                             System.out.print("BUMP INTO NORMAL CANDY");
                             candies.remove(candy);
                             // Add bonus points for normal candy
                             DEF.TOTAL_SCORES += 5;
                         }
                     }
                 }
             }

             // Check collision: Dragon - Candy
             // Track how many candies the dragon is colliding with in the current frame
            
             for (Dragon dragon: dragons) {
                 for (Candy candy: candies) {
                     if (candy.intersectsSprite(dragon)) {
                         // Only reduce lives once when the candy collides with dragon
                         candies.remove(candy);
                         // Decrease lives and update the text
                         DEF.TOTAL_LIVES -= 1;

                         // If no lives left, end the game
                         if (DEF.TOTAL_LIVES == 0) {
                             GAME_OVER = true;
                         }
                     }
                 }
             }
                 

             // after colission:
             if (GAME_OVER) {
                 showHitEffect();
                 FRESH_ENTRY = true;
                 
                 for (Floor floor: floors) {
                     floor.setVelocity(0, 0);
                 }
                 for (Pipe pipe: pipes) {
                     pipe.setVelocity(0, 0);
                 }
                 for (Dragon dragon: dragons) {
                     dragon.setVelocity(0,0);
                 }
                 goose.setVelocity(0,0);
                 timer.stop();
                 gameOverText.setText("Game Over");
                 
             } else {
                 FRESH_ENTRY = false;
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

