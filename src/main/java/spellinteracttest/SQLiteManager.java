package spellinteracttest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SQLiteManager {
	
	private Connection alarmconnect() {
		String url = "jdbc:sqlite:\\"+Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder().getAbsolutePath()+"\\alarm.db";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url);
		}catch(SQLException e) {
			Bukkit.broadcastMessage(e.getMessage());
		}
		
		return connection;
	}
	
	
	public void alarmsetup(Player player) {
		
		String uuid = player.getUniqueId().toString();
		
		String sql = "create table if not exists \""+uuid+"\"(content text, type text, number, int)";
		
		Connection conn = this.alarmconnect();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		

		
	}
	
	public void addalarm(Player p, String contents, String type) {
		
		String uuid = p.getUniqueId().toString();
		
		String sql = "INSERT AND REPLACE INTO \""+uuid+"\"(content, type) VALUES(\""+contents+"\",\""+ type+"\")";
		
		Connection conn = this.alarmconnect();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void removeoldonealarm(Player p) {
		
		String uuid = p.getUniqueId().toString();
		
	}
}
