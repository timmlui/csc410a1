package nl.tudelft.jpacman.level;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;
import nl.tudelft.jpacman.level.Level.LevelObserver;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.Blinky;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests various aspects of level.
 *
 * @author Jeroen Roosen 
 */
// The four suppress warnings ignore the same rule, which results in 4 same string literals
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyStaticImports"})
class LevelTest {

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
     * The launcher for test collision
     */
    private final Launcher launcher = new Launcher();

    /**
     * The sprite and factories for the units.
     */
    private final PacManSprites spriteStore = new PacManSprites();
    private final GhostFactory ghostFactory = new GhostFactory(spriteStore);
    private final LevelFactory levelFactory = new LevelFactory(spriteStore, ghostFactory);


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
     * Validates the state of the level when it isn't started yet.
     */
    @Test
    void noStart() {
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Validates the state of the level when it is stopped without starting.
     */
    @Test
    void stop() {
        level.stop();
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Validates the state of the level when it is started.
     */
    @Test
    void start() {
        level.start();
        assertThat(level.isInProgress()).isTrue();
    }

    /**
     * Validates the state of the level when it is started then stopped.
     */
    @Test
    void startStop() {
        level.start();
        level.stop();
        assertThat(level.isInProgress()).isFalse();
    }

    /**
     * Verifies registering a player puts the player on the correct starting
     * square.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void registerPlayer() {
        Player p = mock(Player.class);
        level.registerPlayer(p);
        verify(p).occupy(square1);
    }

    /**
     * Verifies registering a player twice does not do anything.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void registerPlayerTwice() {
        Player p = mock(Player.class);
        level.registerPlayer(p);
        level.registerPlayer(p);
        verify(p, times(1)).occupy(square1);
    }

    /**
     * Verifies registering a second player puts that player on the correct
     * starting square.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void registerSecondPlayer() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        level.registerPlayer(p1);
        level.registerPlayer(p2);
        verify(p2).occupy(square2);
    }

    /**
     * Verifies registering a third player puts the player on the correct
     * starting square.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void registerThirdPlayer() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);
        level.registerPlayer(p1);
        level.registerPlayer(p2);
        level.registerPlayer(p3);
        verify(p3).occupy(square1);
    }

    /**
     * Verifies that unit collision produces the correct outcome of the status of the game.
     * Only Player-Ghost interactions should end and anything else should not affect it.
     */
    @Test
    void testCollision() {
        launcher.launch();
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        game.start();
        game.getLevel().addObserver(game);
        
        Ghost ghost = levelFactory.createGhost();
        Pellet pellet = levelFactory.createPellet();
        List<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.NORTH);
        directions.add(Direction.WEST);
        directions.add(Direction.SOUTH);
        directions.add(Direction.EAST);

        List<Unit> unitList = new ArrayList<Unit>();
        unitList.add(player);
        unitList.add(ghost);
        unitList.add(pellet);
        Random blackBoxRandom = new Random();
        Unit colider = unitList.get(blackBoxRandom.nextInt(unitList.size()));
        if (colider.getClass().equals(Pellet.class)) {
            unitList.remove(colider);
        }
        Unit colidee = unitList.get(blackBoxRandom.nextInt(unitList.size()));

        defaultPlayerInteractions.collide(colider, colidee);
        if ((colider.getClass().equals(Player.class) && colidee.getClass().equals(Blinky.class)) || 
            (colider.getClass().equals(Blinky.class) && colidee.getClass().equals(Player.class))) {
            
            // call the UpdateObservers() indirectly to check whether has ended or not
            game.getLevel().notfiyToUpdateObservers();
            assertThat(player.isAlive()).isFalse();
            assertThat(game.getLevel().isAnyPlayerAlive()).isFalse();
            assertThat(game.getLevel().isInProgress()).isFalse();
        }
        else {
            assertThat(player.isAlive()).isTrue();
            assertThat(game.getLevel().isAnyPlayerAlive()).isTrue();
            assertThat(game.getLevel().isInProgress()).isTrue();
        }

        launcher.dispose();
    }
}
