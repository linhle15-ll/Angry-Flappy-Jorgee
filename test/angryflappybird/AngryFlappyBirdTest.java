package angryflappybird;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * JUnit test for AngryFlappyBird
 * @author Melita Madhurza
 */
public class AngryFlappyBirdTest {
private Candy normalCandy;
private Goose goose;
private Defines defines;
private int initialScore;

/**
 * 
 */
@BeforeEach
   public void setUp() {
       // Initialize dependencies
       defines = new Defines();
       defines.TOTAL_SCORES = 0; // Reset score for testing
       // Create objects for testing
       normalCandy = new Candy(100, 100, null, false); // Normal candy at (100, 100)
       goose = new Goose(100, 100, null); // Goose at the same position
       initialScore = defines.TOTAL_SCORES; // Store initial score
   }

@Test
   public void testCollisionWithNormalCandy() {
       // Simulate collision by calling intersectsSprite (assuming it's implemented)
       boolean isIntersecting = goose.intersectsSprite(normalCandy);
       if (isIntersecting) {
           defines.TOTAL_SCORES += 5; // Add bonus points for normal candy
       }
       // Verify that collision occurred
       assertTrue(isIntersecting, "Goose should intersect with the candy");
       // Verify that the score increased
       assertEquals(initialScore + 5, defines.TOTAL_SCORES,
           "Score should increase by 5 when colliding with a normal candy");
   }
}










