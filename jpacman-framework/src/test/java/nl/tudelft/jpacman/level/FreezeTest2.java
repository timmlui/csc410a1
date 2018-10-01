package nl.tudelft.jpacman.level;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Smoke test launching the full game,
 * and attempting to make a number of typical moves.
 *
 * This is <strong>not</strong> a <em>unit</em> test -- it is an end-to-end test
 * trying to execute a large portion of the system's behavior directly from the
 * user interface. It uses the actual sprites and monster AI, and hence
 * has little control over what is happening in the game.
 *
 * Because it is an end-to-end test, it is somewhat longer
 * and has more assert statements than what would be good
 * for a small and focused <em>unit</em> test.
 *
 * @author Arie van Deursen, March 2014.
 */
public class FreezeTest2 {

    private Launcher launcher;
    private Game game;
    private Player player;

    /**
     * Launch the user interface.
     */
    @BeforeEach
    void setUpPacman() {
        launcher = new Launcher();
        launcher.launch();
        game = launcher.getGame();
        player = game.getPlayers().get(0);
    }

    /**
     * Quit the user interface when we're done.
     */
    @AfterEach
    void tearDown() {
        launcher.dispose();
    }

    /**
     * Validates that the state of the game is still in progress.
     */
    @Test
    void inProgress() {
        game.start();
        game.freeze();
        assertThat(game.isInProgress()).isTrue();
    }

    /**
     * Validates that the player can still move.
     */
    @Test
    void playerMovement() {
        game.start();
        game.freeze();
        Square currentSquare = player.getSquare();
        game.move(player, Direction.EAST);
        Square afterSquare = player.getSquare();
        assertThat(currentSquare.equals(afterSquare)).isFalse();
    }

    /**
     * Validates that the score still updates.
     */
    @Test
    void score() {
        
    }

    /**
     * Validates that the NPCs cannot move.
     */
    @Test
    void npcNoMovement() {

    }

    /**
     * Validates the state of the game can still end if the player and ghost collides.
     */
    @Test
    void gameEnd() {

    }
}
