package MA.Openings;

import MA.util.Move;
import MA.util.Movetree;

/**
 * Die Zugfolge f�r die Slawische Er�ffnung
 * @author Lukas
 *
 */
public class Slav extends Opening{
	public Slav()
	{
		Start = new Movetree( new Move(0,0,0));
		Start.addChild(new Movetree( new Move(10, 34, 54)));//1.d4
		helpTree = Start.children.get(0);
		helpTree.addChild(new Movetree( new Move (20,84,64)));//1.d5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (10,33,53)));//2.c4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (20,83,73)));//2.c6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (12,27,46)));//3.Nf3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move(22,97,76)));//3.Nf6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move(12,22,43)));//4.Nc3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move(20,85,75)));//4.e6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move(13,23,67)));//5.Bg5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move(20,88,78)));//5.h6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move(13,67,58)));//6.Bh4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move(20,64,53)));//6.dc4:
		helpTree = Start;
		}
}
