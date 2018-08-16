package MA.Openings;

import MA.util.Move;
import MA.util.Movetree;

/**
 * Die Zugfolge für die Königsindische Eröffnung
 * @author Lukas
 *
 */
public class Kings_Indian extends Opening{
	public Kings_Indian()
	{
		Start = new Movetree( new Move(0,0,0));
		Start.addChild(new Movetree( new Move(10, 34, 54)));//1.d4
		helpTree = Start.children.get(0);
		helpTree.addChild(new Movetree( new Move (22,97,76)));//1.Nf6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (10,33,53)));//2.c4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (20,87,77)));//2.g6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (12,22,43)));//3.Nc3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (23,96,87)));//3.Bg7
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (10,35,55)));//4.e4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (20,84,74)));//4.d6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (12,27,46)));//5.Nf3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (25,95,97,-3)));//5.0-0
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (13,26,35)));//6.Be2
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (22,92,73)));//6.Nc6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (15,25,27,3)));//7.0-0
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (20,85,65)));//7.e5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (10,54,64)));//8.d5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new Movetree( new Move (22,73,85)));//8.Ne7
		helpTree = Start;
		}
}
