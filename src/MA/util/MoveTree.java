package MA.util;

import java.util.ArrayList;

public class MoveTree implements Comparable<MoveTree> {
	public String name;
	public Move move;
	public Position position;
	public MoveTree parent;
	public int value = 0;
	public int piece, from, to, addition = 0;
	public ArrayList<MoveTree> children;

	public MoveTree(Move pMove, MoveTree parent) {
		move = pMove;
		setParent(parent);
		position = parent.position;
	}

	public MoveTree(Move pMove) {
		move = pMove;
	}

	public MoveTree(MoveTree parent, int... args) {
		this(args);
		this.parent = parent;
		if (parent != null && parent.position != null)
			position = parent.position;
	}

	public MoveTree(int value, MoveTree parent, int... args) {
		this(parent, args);
		this.value = value;

	}

	public MoveTree(int... args) {
		piece = args[0];
		from = args[1];
		to = args[2];
		if (args.length > 3)
			addition = args[3];
		move = new Move(piece, from, to, addition);
	}

	public void setParent(MoveTree parent) {
		this.parent = parent;
		parent.addChild(this);
	}

	public MoveTree getParent() {
		return this.parent;
	}

	public void addChild(MoveTree Kind) {
		if (children == null)
			children = new ArrayList<MoveTree>();
		children.add(Kind);
	}

	public void setChildren(ArrayList<MoveTree> list) {
		children = list;
	}

	public String toString() {
		if (move == null)
			move = new Move(piece, from, to, addition);
		return move.toString(false);
	}

	@Override
	public int compareTo(MoveTree o) {
		if (value > o.value)
			return 1;
		else if (value == o.value)
			return 0;
		else
			return -1;
	}

}