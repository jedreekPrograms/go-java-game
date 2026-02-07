package pl.edu.go.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoardBuilderTest {

    @Test
    public void build_defaultSize_is9() {
        Board board = new BoardBuilder().build();

        assertNotNull(board);
        assertEquals(9, board.getSize());
    }

    @Test
    public void build_customSize_isApplied() {
        Board board = new BoardBuilder()
                .size(9)
                .build();

        assertEquals(9, board.getSize());
    }

    @Test
    public void size_method_isFluent() {
        BoardBuilder builder = new BoardBuilder();

        BoardBuilder returned = builder.size(13);

        assertSame(builder, returned);
    }

    @Test
    public void multipleSizeCalls_lastOneWins() {
        Board board = new BoardBuilder()
                .size(9)
                .size(13)
                .build();

        assertEquals(13, board.getSize());
    }

    @Test
    public void build_createsNewBoardInstanceEachTime() {
        BoardBuilder builder = new BoardBuilder();

        Board b1 = builder.size(9).build();
        Board b2 = builder.size(9).build();

        assertNotSame(b1, b2);
        assertEquals(b1.getSize(), b2.getSize());
    }
}
