package chemon.opening;

import chemon.util.*;

public class Sicilian extends Opening{
	
	public Sicilian()
	{
		Start = new MoveTree( new Move(0,0,0));
		Start.addChild(new MoveTree( new Move(10, 35, 55)));//1.e4
		helpTree = Start.children.get(0);
		helpTree.addChild(new MoveTree( new Move(20,85,65)));//1.c5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(14,24,68)));//2.Nf3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(25,95,85)));
		helpTree.addChild(new MoveTree( new Move(20,83,63)));//1.c5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(12,27,46)));//2.Nf3
		helpTree.addChild(new MoveTree( new Move(10,33,43)));//2.c3
		Branch = helpTree;
		helpTree = helpTree.children.get(1);
		helpTree.addChild(new MoveTree( new Move(20,84,64)));//2.d5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(10,55,64)));//3.ed5:
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(24,94,64)));//3.Qd5:
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(10,34,54)));//4.d4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(22,92,73)));//4.Nc6
		helpTree = Branch.children.get(0);
		helpTree.addChild(new MoveTree( new Move(20,84,74)));//2.d6
		helpTree.addChild(new MoveTree( new Move(22,92,73)));//2.Nc6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(10,34,54)));//3.d4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(20,63,54)));//3.cd4:
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(12,46,54)));//4.Nd4:
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(22,97,76)));//4.Nf6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(12,22,43)));//5.Nc3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(22,92,73)));//5.Nc6
		helpTree = Start;
		}
	
}
