package chemon.opening;

import chemon.util.Move;
import chemon.util.MoveTree;

/**
 * Die Zugfolge für die Englische Eröffnung
 * @author Lukas
 *
 */
public class English extends Opening{
	public English()
	{
		Start = new MoveTree(new Move(0,0,0));
		Start.addChild(new MoveTree(new Move(10, 33, 53)));//1.c4
		helpTree = Start.children.get(0);
		helpTree.addChild(new MoveTree (new Move (20,85,65)));//1.e5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (12,22,43)));//2.Nc3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (22,97,76)));//2.Nf6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (12,27,46)));//3.Nf3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (22,92,73)));//3.Nc6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (10,35,45)));//4.e3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (23,96,52)));//4.Bb4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (14,24,33)));//5.Qc2
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (23,52,43)));//5.Bc3:
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (14,33,43)));//6.Qc3:
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree (new Move (24,94,85)));//6.Qe7
		helpTree = Start;
		}
}
