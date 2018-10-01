package nl.tudelft.jpacman.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test various aspects of board.
 *
 * @author Jeroen Roosen 
 */
class BoardTest {

    private static final int MAX_WIDTH = 3;
    private static final int MAX_HEIGHT = 3;

    private final Square[][] grid = {
        { mock(Square.class), mock(Square.class), mock(Square.class) },
        { mock(Square.class), mock(Square.class), mock(Square.class) },
        { mock(Square.class), mock(Square.class), mock(Square.class) }
    };
    private final Board board = new Board(grid);

    private final Square[][] illegalGrid = {
        { mock(Square.class), mock(Square.class), mock(Square.class) },
        { mock(Square.class), null, mock(Square.class) }
    };
    private final Board illegalBoard = new Board(illegalGrid);

    /**
     * Verifies the board has the correct width.
     */
    @Test
    void verifyWidth() {
        assertThat(board.getWidth()).isEqualTo(MAX_WIDTH);
    }

    /**
     * Verifies the board has the correct height.
     */
    @Test
    void verifyHeight() {
        assertThat(board.getHeight()).isEqualTo(MAX_HEIGHT);
    }

    /**
     * Verify that squares at key positions are properly set.
     * @param x Horizontal coordinate of relevant cell.
     * @param y Vertical coordinate of relevant cell.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "1, 2",
        "0, 1"
    })
    void testSquareAt(int x, int y) {
        assertThat(board.squareAt(x, y)).isEqualTo(grid[x][y]);
    }

     /**
     * Verifies that illegal boards can be detected.
     */
    @Test
    void testIllegalBoard() {
        assertThat(illegalBoard.invariant()).isFalse();
    }

    /**
     * Verifies that coordinates can be located as within 
     * or outside the border.
     */
    @Test
    void testOnPointX() {
        assertThat(board.withinBorders(0, 1)).isTrue();
        assertThat(board.withinBorders(3, 1)).isFalse();
    }

    /**
     * Verifies that coordinates can be located as within 
     * or outside the border.
     */
    @Test
    void testInPointXandY() {
        assertThat(board.withinBorders(1, 1)).isTrue();
    }
    /**
     * Verifies that coordinates can be located as within 
     * or outside the border.
     */
    @Test
    void testOffPointX() {
        assertThat(board.withinBorders(-1, 1)).isFalse();
        assertThat(board.withinBorders(5, 1)).isFalse();
    }

    /**
     * Verifies that coordinates can be located as within 
     * or outside the border.
     */
    @Test
    void testOnPointY() {
        assertThat(board.withinBorders(1, 0)).isTrue();
        assertThat(board.withinBorders(1, 3)).isFalse();
    }
    /**
     * Verifies that coordinates can be located as within 
     * or outside the border.
     */
    @Test
    void testOffPointY() {
        assertThat(board.withinBorders(1, -1)).isFalse();
        assertThat(board.withinBorders(1, 5)).isFalse();
    }
}
