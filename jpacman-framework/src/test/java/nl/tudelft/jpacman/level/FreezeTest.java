package nl.tudelft.jpacman.level;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.Navigation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests various aspects of freeze.
 *
 * @author Timothy Lui and Ivan Shen
 */
public class FreezeTest {

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
        List<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.NORTH);
        directions.add(Direction.WEST);
        directions.add(Direction.SOUTH);
        directions.add(Direction.EAST);

        Random random = new Random();
        // random steps of 1 to 5
		int steps = random.nextInt(5) + 1;

        game.start();
        game.freeze();
        Square currentSquare = player.getSquare();
        Square afterSquare = player.getSquare();

        while (currentSquare.equals(afterSquare)){
            Direction direction = directions.get(random.nextInt(directions.size()));
            directions.remove(direction);
            move(game, direction, steps);
            afterSquare = player.getSquare();
        }
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
    void npcNoMovement() throws InterruptedException {
        game.start();
        List<Ghost> ghostList = findGhostsInBoard(game.getLevel().getBoard());
        assert(ghostList.size() > 0);
        Map<Ghost, Square> occupiedMapBefore = new HashMap<Ghost, Square>();
        Map<Ghost, Square> occupiedMapAfter = new HashMap<Ghost, Square>();

        for (Ghost g : ghostList) {
            occupiedMapBefore.put(g, g.getSquare());
        }
        game.freeze();

        // Sleeping in tests is generally a bad idea.
        // Here we do it just to let the monsters try and move.
        // (Shouldn't actually move as it is in freeze mode)
        Thread.sleep(500L);

        for (Ghost g : ghostList) {
            occupiedMapAfter.put(g, g.getSquare());
        }
        assertThat(occupiedMapBefore.equals(occupiedMapAfter)).isTrue();
    }

    /**
     * Validates the state of the game can still end if the player and ghost collides.
     */
    @Test
    void gameEnd() {
        game.start();
        game.freeze();
        List<Ghost> ghostList = findGhostsInBoard(game.getLevel().getBoard());
        assert(ghostList.size() > 0);
        DefaultPlayerInteractionMap defaultPlayerInteractions = new DefaultPlayerInteractionMap();
        defaultPlayerInteractions.collide(ghostList.get(0), player);
        // move pacman once to update observer
        game.getLevel().move(player, Direction.EAST); 
        // should have collided with ghost (Inky is at starting position)
        assertThat(player.isAlive()).isFalse();
        assertThat(game.getLevel().isAnyPlayerAlive()).isFalse();
        assertThat(game.isInProgress()).isFalse();
    }

    /**
     * Validates the state of the game can still win if the player collects all the pellets.
     */
    @Test
    void gameWin() {
        game.start();
        assertThat(game.isInProgress()).isTrue();
        game.freeze();
        assertThat(game.isInProgress()).isTrue();
        // simulate a winning scenario where all the pellets are taken
        removePellets();
        // move once to trigger win detection
        move(game, Direction.EAST, 1); 
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
     * Borrowed from LauncherSmokeTest.java.
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
    /**
     * Remove all the pellets from the board.
     * Borrowed from Level.java remainingPellets method.
     */
    public void removePellets() {
        Board board = game.getLevel().getBoard();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                for (Unit unit : board.squareAt(x, y).getOccupants()) {
                    if (unit instanceof Pellet) {
                        unit.leaveSquare();
                    }
                }
            }
        }
    }

    /**
     * Finds all the ghosts in a level.
     * Borrowed from Navigation.java findUnitInBoard method.
     * 
     * @param board
     * @return ghostList
     */
    public static List<Ghost> findGhostsInBoard(Board board) {
        List<Ghost> ghostList = new ArrayList<Ghost>();
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                final Ghost ghost = Navigation.findUnit(Ghost.class, board.squareAt(x, y));
                if (ghost != null) {
                    ghostList.add(ghost);
                }
            }
        }
        return ghostList;
    }
}
