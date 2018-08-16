package MA.util;

import java.util.ArrayList;

public class Movetree implements Comparable<Movetree> {
	public String name;
	public Move move;
	public Position position;
	public Movetree parent;
	public int value = 0;
	public int piece, from, to, addition = 0;
	public ArrayList<Movetree> children;

	public Movetree(Move pMove, Movetree parent) {
		move = pMove;
		setParent(parent);
		position = parent.position;
	}

	public Movetree(Move pMove) {
		move = pMove;
	}

	public Movetree(Movetree parent, int... args) {
		this(args);
		this.parent = parent;
		if (parent.position != null)
			position = parent.position;
	}

	public Movetree(int value, Movetree parent, int... args) {
		this(parent, args);
		this.value = value;

	}

	public Movetree(int... args) {
		piece = args[0];
		from = args[1];
		to = args[2];
		if (args.length > 3)
			addition = args[3];
		move = new Move(piece, from, to, addition);
	}

	public void setParent(Movetree parent) {
		this.parent = parent;
		parent.addChild(this);
	}

	public Movetree getParent() {
		return this.parent;
	}

	public void addChild(Movetree Kind) {
		if (children == null)
			children = new ArrayList<Movetree>();
		children.add(Kind);
	}

	public void setChildren(ArrayList<Movetree> list) {
		children = list;
	}

	public String toString() {
		if (move == null)
			move = new Move(piece, from, to, addition);
		return move.toString(false);
	}

	@Override
	public int compareTo(Movetree o) {
		if (value > o.value)
			return 1;
		else if (value == o.value)
			return 0;
		else
			return -1;
	}

}