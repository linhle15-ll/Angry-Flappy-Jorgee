package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    private int scores;
    private Text livesText;
    private int lives;
    private Text snoozeText;
    private Text gameOverText;
    private Text getReadyText;
    
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
    	resetGameScene();  // resets the gameScene
    	
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
    	    resetGameScene();
        }
    	else if (GAME_START){
            clickTime = System.nanoTime();   
        }
    	GAME_START = true;
        CLICKED = true;
        
        gameScene.getChildren().removeIf(node -> node instanceof Text && ((Text) node).getText().equals("Get Ready"));
    }
    
    /**
     * Generate a random height value for the pipes
     * @param maxH: max height of a pipe
     * @param minH: min height of a pipe
     * @return randomHeight: random height value
     * @author: Linh Ngoc Le
     */
    public int generateRandomHeight(int maxH, int minH) {
        int randomHeight = (int)(Math.random() * (maxH - minH)) + minH + 3;
        return randomHeight;
    }
    
    private void resetGameScene() {
        // Clear the gameScene
        if (gameScene == null) {
            gameScene = new Group();
        } else {
            gameScene.getChildren().clear();
        }
        
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            ImageView dayBackground = DEF.IMVIEW.get("day_background");
            
            gameScene.getChildren().addAll(dayBackground, canvas);
            
            // display text "Get Ready" until start game is clicked (later replaced with better graphic) - Linh Ngoc Le
            getReadyText = new Text(50,250, "Get Ready");    
            getReadyText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
            getReadyText.setStroke(Color.BLACK);
            getReadyText.setFill(Color.RED);
            
            // display text "Game Over" when GAME_OVER = True
            gameOverText = new Text(50, 250, "");
            gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
            gameOverText.setStroke(Color.BLACK);
            gameOverText.setFill(Color.RED);
            
            gameScene.getChildren().addAll(getReadyText, gameOverText);
            
            /**
             * create score and lives and snooze text
             * @author Linh Ngoc Le
             */
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
     * initialize candies with positions on random lowerPipes
     * @author Linh Ngoc Le
     * @param random 
     */
    private void initializeCandies(double pipeWidthCandyPosX, double pipeWidthCandyPosY, double random) {
        Candy rainbowCandy;
        Candy normalCandy;

        if (0.4 >= random && random >= 0.35) {
         // create rainbowCandy and normalCandy
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
            
        } else if (0.8 <= random && random <= 0.9) {
                
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
        Pipe lowerPipe = new Pipe(400, DEF.SCENE_HEIGHT -  generateRandomHeight(DEF.PIPE_MAX_HEIGHT, DEF.PIPE_MIN_HEIGHT), DEF.IMAGE.get("lower_pipe"), true, false); 
        Pipe upperPipe = new Pipe(400, generateRandomHeight(0, -40), DEF.IMAGE.get("upper_pipe"), false, false);
       
        lowerPipe.setVelocity(DEF.PIPE_VEL_EASY, 0);
        upperPipe.setVelocity(DEF.PIPE_VEL_EASY, 0);
        
        pipes.add(upperPipe);
        pipes.add(lowerPipe);
        
        // initialize candies on lower pipes
       
        double randomCandy = Math.random();  //random coefficient for candies
        double lowerPipeWidthCandyPosX = lowerPipe.getPositionX();
        double lowerPipeWidthCandyPosY = lowerPipe.getPositionY();
                
        if (lowerPipeWidthCandyPosX >= DEF.SCENE_WIDTH) {
            initializeCandies(lowerPipeWidthCandyPosX, lowerPipeWidthCandyPosY, randomCandy);
        }            
       
        // initialize dragons from upper pipes
        double randomDragon = Math.random();
        double upperPipeWithDragonPosX = upperPipe.getPositionX();
        
        initializeDragons(upperPipeWithDragonPosX, randomDragon);
        
    }

    /**
     * Initialize dragons
     * Dragon drops randomly from the sky (from some random upperPipe)
     * @author Linh Ngoc Le
     */
    private void initializeDragons(double upperPipeWithDragonPosX, double random) {
        if (random >= 0.3 && random <= 0.4) {
            Dragon dragon = new Dragon(
                    upperPipeWithDragonPosX, 
                    -DEF.DRAGON_HEIGHT,
                    DEF.IMAGE.get("dragon")
            );
            
            dragon.setHeight(DEF.DRAGON_HEIGHT);
            dragon.setWidth(DEF.DRAGON_WIDTH);
            
            dragon.setVelocity(0, DEF.DRAGON_DROP_VEL);
            dragons.add(dragon);
        }
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
    	    	 
    	    	 // step3: update pipes, candies, dragons - Linh Ngoc Le
                 movePipes();
                 moveCandies();
                 moveDragons();
                               
                 // Check for collision in between objects
    	    	 checkCollision();
    	    	 checkPassingPipe();
    	    	 
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
                 dragon.render(gc);
                 dragon.update(DEF.SCENE_SHIFT_TIME);
             }
         }
         
         /**
          * Check if the goose pass the pipe
          * true if posX of goose > posX of pipe and pipe is not checked yet
          * only increase the total scores if goose does not collide with candy on the pipe
          * @author Linh Ngoc Le
          */
         private void checkPassingPipe() {
             for (Pipe pipe: pipes) {
                 if (!pipe.isScored() && pipe.getPositionX() + pipe.getWidth() < goose.getPositionX() + goose.getWidth()) {
                     pipe.setScored(true);
                     
                     // only add point if the goose does not intersect with the candy
                     boolean candyCollision = false; // set CandyCollision state for every pipe passed by
                     for (Candy candy: candies) {
                         if (goose.intersectsSprite(candy)) {
                             candyCollision = true;
                             break;
                         }
                     }
                     
                     if (!candyCollision) {
                         DEF.TOTAL_SCORES += 1;
                         scores = DEF.TOTAL_SCORES;
                         scoreText.setText(Integer.toString(scores));
                     }
                 }
             }
         }
         
         // CHECK FOR COLLISIONS
         /**
          * Check for collisions
          * @author Linh Ngoc Le
          */
    	 private void checkCollision() {
    	     
    		// check collision: Goose - Floor 
			for (Floor floor: floors) {
				if (goose.intersectsSprite(floor)) {
				    GAME_OVER = true;
				};
				
			}
			// check collision: Goose - Pipe
			for (Pipe pipe: pipes) {
			    if (goose.intersectsSprite(pipe)) {
			        
			        // update total lives (one life is taken)
			        DEF.TOTAL_LIVES --; 
			        lives = DEF.TOTAL_LIVES; 
			        livesText.setText(Integer.toString(lives) + " lives left"); 
			        
			        // bounce back
		            goose.setVelocity(-50, 90);
		            
		            GAME_START = false;
		            
		            // schedule bouncing back to the start position after 2 second
		   
		            
		            Timeline bounceBackTimeline = new Timeline(
		                    new KeyFrame(Duration.seconds(1), event -> {
		                        GAME_START = true;
		                        goose.setPositionXY(20, DEF.SCENE_HEIGHT/2);
		                        pipes = new ArrayList<>();
		                        initializePipes();
		   
		                        movePipes();
		                        moveCandies();
		                        //// PROBLEMMMMMM
		                        
		                    })
		            );
		            
		            bounceBackTimeline.play();
		                    
			        // check remaining total live
			        if (DEF.TOTAL_LIVES == 0) {
	                    GAME_OVER = true;
	                    System.out.println("OUT OF LIVES");
	                    return;
	        
	                }
			        
			        // reset the game with updates in lives

			    }
			    
			}
			
			// Check collision: Goose - Candy
			if (candies.size()!= 0) {
			    for (Candy candy: candies) {
			        // goose - rainbowCandy: turn on autopilot 6 secs 
	                if (goose.intersectsSprite(candy) && candy.isRainbowCandy()) {
	                    System.out.println("INTERSECT RAINBOW CANDY");
	                    candies.remove(candy);
	                    
	                    goose.setImage(DEF.IMAGE.get("auto"));
	                    System.out.println("TO AUTO MODE: " + goose.getIMAGE_DIR());
	                        // snooze time + auto not hit the pipes
	                    
	                }
	                    // Goose - normalCandy: add 1 bonus point
	                else if (goose.intersectsSprite(candy) && !candy.isRainbowCandy) {
	                    System.out.println("INTERSECT NORMAL CANDY");
	                    candies.remove(candy);
	                    DEF.TOTAL_SCORES += 1;
	                    scores = DEF.TOTAL_SCORES;
	                    scoreText.setText(Integer.toString(scores));
	                }
	            }
			} 
			// Check collision: Dragon - Goose
			// Game over (reset all scores
			// check collision: Goose - Floor 
            for (Dragon dragon: dragons) {
                // bounce down
                goose.setVelocity(50, 0);
                if (dragon.intersectsSprite(goose)) {
                    GAME_OVER = true;
                }
            }
			
			// Check collision: Dragon - Candy
			// collect the egg right beneath it and
			// lead to points lost if the egg is not collected by the bird first.
			for (Dragon dragon: dragons) {
			    for (Candy candy: candies) {
			        if (dragon.intersectsSprite(candy)) {
			            DEF.TOTAL_SCORES --;
			            scores = DEF.TOTAL_SCORES; 
			            scoreText.setText(Integer.toString(scores));
			        }
			    }
			}
			
			// end the game 
			if (GAME_OVER) {
				showHitEffect(); 
				for (Floor floor: floors) {
					floor.setVelocity(0, 0);
				}
				for (Pipe pipe: pipes) {
				    pipe.setVelocity(0, 0);
				}
				timer.stop();
				gameOverText.setText("Game Over");
			
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

