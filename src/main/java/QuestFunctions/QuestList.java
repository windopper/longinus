package QuestFunctions;

import java.util.ArrayList;
import java.util.List;

public enum QuestList {
	
	튜토리얼(1),
	첫번째임무(1),
	일손_도움(3),
	TestQuest(1);

	private final int LevelReq;
	private final List<String> Reward = new ArrayList<>();
	
	QuestList(int LevelReq) {
		this.LevelReq = LevelReq;
	}
	
	public int getLevelReq() {
		return LevelReq;
	}



}
