package nl.tudelft.jpacman.level;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.Blinky;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests various aspects of freeze.
 *
 * @author Timothy Lui and Ivan Shen
 */
// The four suppress warnings ignore the same rule, which results in 4 same string literals
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyStaticImports"})
class FreezeTest {

    /**
     * The level under test.
     */
    private Level level;

    /**
     * The game under test.
     */
    private Game game;

    /**
     * An NPC on this level.
     */
    private final Ghost ghost = mock(Ghost.class);

    /**
     * Creating 4 squares
     */
    private final Square square1 = mock(Square.class);
    private final Square square2 = mock(Square.class);
    private final Square square3 = mock(Square.class);
    private final Square square4 = mock(Square.class);

    /**
     * The collision map.
     */
    private final CollisionMap collisions = mock(CollisionMap.class);

    /**
     * The default player interaction map.
     */
    private final DefaultPlayerInteractionMap defaultPlayerInteractions = new DefaultPlayerInteractionMap();

    private final Square[][] grid = {
        { square1, square2 },
        { square3, square4 }
    };

    PacManSprites spriteStore = new PacManSprites();
    BoardFactory boardFactory = new BoardFactory(spriteStore);
    Board board = boardFactory.createBoard(grid);
    PlayerFactory playerFactory = new PlayerFactory(spriteStore);
    GameFactory gameFactory = new GameFactory(playerFactory);


    /**
     * Sets up the level with the default board, a single NPC and a starting
     * square.
     */
    @BeforeEach
    void setUp() {
        final long defaultInterval = 100L;
        level = new Level(board, Lists.newArrayList(ghost), Lists.newArrayList(
            square1, square2, square3, square4), collisions);
        when(ghost.getInterval()).thenReturn(defaultInterval);
        game = gameFactory.createSinglePlayerGame(level);
    }

    /**
     * Validates that the state of the game is still in progress.
     */
    @Test
    void inProgress() {
        //System.out.println("HEIGHT: " + board.getHeight() + " AND WIDTH: " + board.getWidth());
        game.start();
        game.freeze();
        assertThat(level.isInProgress()).isTrue();
    }

    /**
     * Validates that the player can still move.
     */
    @Test
    void playerMovement() {
        System.out.println("BOARD: " + board.getHeight() + "x" + board.getWidth());
        game.start();
        game.freeze();
        Player player = new PlayerFactory(spriteStore).createPacMan();
        level.registerPlayer(player);
        player.occupy(square1);
        System.out.println("===DOES SQ1 HV PLAYER?: " + square1.getOccupants().contains(player));
        game.move(player, Direction.SOUTH);
        assertThat(square3.getOccupants().contains(player));
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
        level.start();
        assertThat(level.isInProgress()).isTrue();
    }

    /**
     * Validates the state of the game can still end if the player and ghost collides.
     */
    @Test
    void gameEnd() {
        level.start();
        level.stop();
        assertThat(level.isInProgress()).isFalse();
    }
}
