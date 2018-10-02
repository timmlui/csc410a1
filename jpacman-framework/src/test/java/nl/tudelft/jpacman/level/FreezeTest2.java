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
 * Tests various aspects of freeze.
 *
 * @author Timothy Lui and Ivan Shen
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
        move(game, Direction.EAST, 1);
        Square afterSquare = player.getSquare();
        assertThat(currentSquare.equals(afterSquare)).isFalse();
    }

    /**
     * Validates that the score still updates.
     */
    @Test
    void score() {
        game.start();
        game.freeze();
        int currentPellets = game.getLevel().remainingPellets();
        int currentPoints = player.getScore();
        assertThat(player.getScore()).isZero();

        move(game, Direction.EAST, 1);
        int afterPellets = game.getLevel().remainingPellets();
        int afterPoints = player.getScore();
        assertThat(afterPellets == currentPellets).isFalse();
        assertThat(afterPoints == currentPoints).isFalse();
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
        game.start();
        game.freeze();
        // head towards npc
        move(game, Direction.EAST, 1);
        move(game, Direction.NORTH, 2);
        move(game, Direction.EAST, 3);
        move(game, Direction.NORTH, 6);
        move(game, Direction.WEST, 4);
        // should have collided with ghost (Inky is at starting position)
        assertThat(player.isAlive()).isFalse();
        assertThat(game.getLevel().isAnyPlayerAlive()).isFalse();
        assertThat(game.isInProgress()).isFalse();
    }

    /**
     * Validates the state of the game is unfrozen (npc can move) after a freeze -> start sequence.
     */
    @Test
    void freezeStart() {
        game.start();
        assertThat(game.isInProgress()).isTrue();
        game.freeze();
        assertThat(game.isInProgress()).isTrue();
        game.start();
        assertThat(game.isInProgress()).isTrue();
    }

    /**
     * Validates the state of the game is unfrozen (npc can move) after unfreezing (clicking freeze again).
     */
    @Test
    void freezeUnfreeze() {
        game.start();
        assertThat(game.isInProgress()).isTrue();
        game.freeze();
        assertThat(game.isInProgress()).isTrue();
        game.freeze();
        assertThat(game.isInProgress()).isTrue();
    }

    /**
     * Validates the state of the game is stopped after a freeze -> stop sequence.
     */
    @Test
    void freezeStop() {
        game.start();
        assertThat(game.isInProgress()).isTrue();
        game.freeze();
        assertThat(game.isInProgress()).isTrue();
        game.stop();
        assertThat(game.isInProgress()).isFalse();
    }

    /**
     * Validates the state of the game is still stopped after a stop -> freeze sequence.
     */
    @Test
    void stopFreeze() {
        game.start();
        assertThat(game.isInProgress()).isTrue();
        game.stop();
        assertThat(game.isInProgress()).isFalse();
        game.freeze();
        assertThat(game.isInProgress()).isFalse();

    }

    /**
     * Make number of moves in given direction.
     *
     * @param game The game we're playing
     * @param dir The direction to be taken
     * @param numSteps The number of steps to take
     */
    public static void move(Game game, Direction dir, int numSteps) {
        Player player = game.getPlayers().get(0);
        for (int i = 0; i < numSteps; i++) {
            game.move(player, dir);
        }
    }
}
