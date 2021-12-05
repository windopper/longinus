package QuestFunctions;

public enum QuestList {
	
	튜토리얼(1),
	종자_전달하기(2),
	일손_도움(3),
	TestQuest(1);

	private final int LevelReq;
	
	QuestList(int LevelReq) {
		this.LevelReq = LevelReq;
	}
	
	public int getLevelReq() {
		return LevelReq;
	}



}
