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
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
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
     * An NPC on this level.
     */
    private final Ghost ghost = mock(Ghost.class);

    /**
     * Starting position 1.
     */
    private final Square square1 = mock(Square.class);

    /**
     * Starting position 2.
     */
    private final Square square2 = mock(Square.class);

    /**
     * The board for this level.
     */
    private final Board board = mock(Board.class);

    /**
     * The collision map.
     */
    private final CollisionMap collisions = mock(CollisionMap.class);

    /**
     * The default player interaction map.
     */
    private final DefaultPlayerInteractionMap defaultPlayerInteractions = new DefaultPlayerInteractionMap();


    /**
     * Sets up the level with the default board, a single NPC and a starting
     * square.
     */
    @BeforeEach
    void setUp() {
        final long defaultInterval = 100L;
        level = new Level(board, Lists.newArrayList(ghost), Lists.newArrayList(
            square1, square2), collisions);
        when(ghost.getInterval()).thenReturn(defaultInterval);
    }

    /**
     * Validates that the state of the game is still in progress.
     */
    @Test
    void inPrgoress() {
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Validates that the player can still move.
     */
    @Test
    void playerMovement() {
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Validates that the score still updates.
     */
    @Test
    void score() {
        level.stop();
        assertThat(level.isInProgress()).isFalse();
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
