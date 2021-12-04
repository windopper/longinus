package userdata;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class UserQuestManager {
	
	/* 0 시작안함
	 * 1 완료함
	 *
	 * 2~ 퀘스트 단계
	 * 
	 * 
	 * 
	 */
	
	private static UserQuestManager UserQuestManager;
	
	private UserQuestManager() {
		
	}
	
	public static UserQuestManager getinstance() {
		if(UserQuestManager == null) UserQuestManager = new UserQuestManager();
		return UserQuestManager;
	}
	
	
	
	

}
