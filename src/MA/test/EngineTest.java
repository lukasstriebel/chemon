package MA.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import MA.Chemon.Engine;
import MA.util.MoveTree;
import MA.util.Position;

class EngineTest {

	@Test
	void testCheck() {
		int[] board = new int[] {
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				11, 12, 13, 14, 15, 13, 12, 11, -1, -1,
				10, 10, 10,  0,  0, 10, 10, 10, -1, -1,
				0, 	0,  0,   0, 10,  0,  0,  0, -1, -1,
				0,  23, 0,  10,  0,  0,  0,  0, -1, -1,
				0,  0,  0,  20,  0,  0,  0,  0, -1, -1,
				0,  0,  0,   0, 20, 22,  0,  0, -1, -1,
				20, 20, 20,  0,  0,  20, 20, 20, -1, -1,
				21, 22, 23, 24, 25, 0, 0, 21, -1, -1, -1,
				-1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, };

		Position position = new Position(board);
		position.setBlackKing(95);
		position.setWhiteKing(25);
		Engine engine = initializeEngine(board);
		boolean isChecked = engine.whiteIsChecked(position);
		assertTrue(isChecked);
		isChecked = engine.blackIsChecked(position);
		assertFalse(isChecked);
	}
	
	@Test
	void testKnightMoves() {
		int[] board = new int[120];
		board[55] = 12;
		Engine engine = initializeEngine(board);
		List<MoveTree> moves = engine.movesWhite(new Position(board), null);
		assertEquals(8, moves.size());
	}

	
	@Test
	void testKingMoves() {
		int[] board = new int[120];
		board[55] = 15;
		Engine engine = initializeEngine(board);
		List<MoveTree> moves = engine.movesWhite(new Position(board), null);
		assertEquals(8, moves.size());
	}
	
	@Test
	void testPawnMoves() {
		int[] board = new int[120];
		board[25] = 10;
		Engine engine = initializeEngine(board);
		List<MoveTree> moves = engine.movesWhite(new Position(board), null);
		assertEquals(2, moves.size());
	}
	
	@Test
	void testBishopMoves() {
		int[] board = new int[] {
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				0, 0, 0, 0, 0, 0, 0, 0, -1, -1,
				0, 0, 0,  0,  0, 0, 0, 0, -1, -1,
				0, 	0,  0,   0, 0,  0,  0,  0, -1, -1,
				0, 0, 0,  13,  0,  0,  0,  0, -1, -1,
				0,  0,  0,  0,  0,  0,  0,  0, -1, -1,
				0,  0,  0,   0, 0, 0,  0,  0, -1, -1,
				0, 0, 0,  0,  0,  0, 0, 0, -1, -1,
				0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1,
				-1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, };
		Engine engine = initializeEngine(board);
		List<MoveTree> moves = engine.movesWhite(new Position(board), null);
		assertEquals(13, moves.size());
	}
	
	
	@Test
	void testRookMoves() {
		int[] board = new int[] {
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				0, 0, 0, 0, 0, 0, 0, 0, -1, -1,
				0, 0, 0,  0,  0, 0, 0, 0, -1, -1,
				0, 	0,  0,   0, 0,  0,  0,  0, -1, -1,
				0, 0, 0,  11,  0,  0,  0,  0, -1, -1,
				0,  0,  0,  0,  0,  0,  0,  0, -1, -1,
				0,  0,  0,   0, 0, 0,  0,  0, -1, -1,
				0, 0, 0,  0,  0,  0, 0, 0, -1, -1,
				0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1,
				-1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, };
		Engine engine = initializeEngine(board);
		List<MoveTree> moves = engine.movesWhite(new Position(board), null);
		assertEquals(14, moves.size());
	}
	
	
	@Test
	void testQueenMoves() {
		int[] board = new int[] {
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				0, 0, 0, 0, 0, 0, 0, 0, -1, -1,
				0, 0, 0,  0,  0, 0, 0, 0, -1, -1,
				0, 	0,  0,   0, 0,  0,  0,  0, -1, -1,
				0, 0, 0,  14,  0,  0,  0,  0, -1, -1,
				0,  0,  0,  0,  0,  0,  0,  0, -1, -1,
				0,  0,  0,   0, 0, 0,  0,  0, -1, -1,
				0, 0, 0,  0,  0,  0, 0, 0, -1, -1,
				0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1,
				-1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, };
		Engine engine = initializeEngine(board);
		List<MoveTree> moves = engine.movesWhite(new Position(board), null);
		assertEquals(27, moves.size());
	}
	
	
	private Engine initializeEngine(int[] board) {	
		Position position = new Position(board);
		return new Engine(position);
		
	}
}
