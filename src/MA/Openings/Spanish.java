package MA.Openings;

import MA.util.Move;
import MA.util.MoveTree;

/**
 * Die Zugfolge für die Spanische Eröffnung
 * @author Lukas
 *
 */
public class Spanish extends Opening {
	
	public Spanish(boolean correct) {
		Start = new MoveTree( new Move(0, 0, 0));
		Start.addChild(new MoveTree( new Move(10, 35, 55)));// 1.e4
		helpTree = Start.children.get(0);
		helpTree.addChild(new MoveTree( new Move(20, 85, 65)));// 1.e5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(12, 27, 46)));// 2.Nf3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(22, 92, 73)));// 2.Nc6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(13, 26, 62)));// 3.Bb5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(20, 81, 71)));// 3.a6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(13, 62, 51)));// 4.Ba4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(22, 97, 76)));// 4.Nf6
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(15, 25, 27, 3)));// 5.0-0
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(23, 96, 85)));// 5.Be7
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(11, 26, 25)));// 6.Re1
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(20, 82, 62)));// 6.b5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(13, 51, 42)));// 7.Bb3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(25, 95, 97, -3)));// 7.0-0
		helpTree = Start;
	}
	public Spanish() {
		Start = new MoveTree( new Move(0, 0, 0));
		Start.addChild(new MoveTree( new Move(10, 35, 55)));// 1.e4
		helpTree = Start.children.get(0);
		helpTree.addChild(new MoveTree( new Move(20, 85, 65)));// 1.e5
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(15, 25, 35)));// 2.Ke2
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(24, 94, 58)));// 2.Dh4
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(10, 31, 41)));// 3.a3
		helpTree = helpTree.children.get(0);
		helpTree.addChild(new MoveTree( new Move(24, 58, 55)));// 3.a6
		helpTree = Start;
	}
}
