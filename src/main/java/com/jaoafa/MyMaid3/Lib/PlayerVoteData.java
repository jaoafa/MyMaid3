package com.jaoafa.MyMaid3.Lib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.jaoafa.MyMaid3.Main;

public class PlayerVoteData extends MyMaidLibrary {
	OfflinePlayer offplayer;

	/**
	 * 指定したプレイヤーの投票データを取得します。
	 * @param player プレイヤー
	 * @author mine_book000
	 */
	public PlayerVoteData(Player player) {
		if (player == null)
			throw new NullPointerException("We could not get the player.");
		this.offplayer = player;

		changePlayerName();
	}

	/**
	 * 指定したオフラインプレイヤーの投票データを取得します。
	 * @param offplayer オフラインプレイヤー
	 * @author mine_book000
	 */
	public PlayerVoteData(OfflinePlayer offplayer) {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		this.offplayer = offplayer;

		changePlayerName();
	}

	/**
	 * 指定したプレイヤーネームの投票データを取得します。
	 * @param name プレイヤーネーム
	 * @deprecated プレイヤー名で検索するため、思い通りのプレイヤーを取得できない場合があります。
	 * @exception NullPointerException プレイヤーが取得できなかったとき
	 * @author mine_book000
	 */
	@Deprecated
	public PlayerVoteData(String name) throws NullPointerException {
		OfflinePlayer offplayer = Bukkit.getOfflinePlayer(name);
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		this.offplayer = offplayer;

		changePlayerName();
	}

	/**
	 * プレイヤーの投票数を取得します。
	 * @return プレイヤーの投票数
	 * @throws ClassNotFoundException 内部でClassNotFoundExceptionが発生した場合
	 * @throws SQLException 内部でSQLExceptionが発生した場合
	 * @throws UnsupportedOperationException 投票数が取得できなかったとき
	 * @throws NullPointerException プレイヤーが取得できなかったとき
	 */
	public int get() throws ClassNotFoundException, SQLException, UnsupportedOperationException, NullPointerException {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		if (!exists())
			return 0;
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return 0;
		}
		Connection conn = MySQLDBManager.getConnection();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM vote WHERE id = ?");
		statement.setInt(1, getID());
		ResultSet res = statement.executeQuery();
		if (res.next()) {
			return res.getInt("count");
		} else {
			throw new UnsupportedOperationException("Could not get VoteCount.");
		}
	}

	/**
	 * その日のうち(前日or当日AM9:00～今)に誰も投票していないかどうか調べる（その日初めての投票かどうか）
	 * @return 誰も投票してなければtrue
	 */
	public static boolean TodayFirstVote() {
		try {
			MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
			if (MySQLDBManager == null) {
				return false;
			}
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM vote");
			ResultSet res = statement.executeQuery();
			while (res.next()) {
				Long lasttime = Long.parseLong(res.getString("lasttime"));
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
				long today9 = cal.getTimeInMillis() / 1000L;

				cal.add(Calendar.DAY_OF_MONTH, -1);
				long yesterday9 = cal.getTimeInMillis() / 1000L;

				long now = System.currentTimeMillis() / 1000L;

				boolean checktype; // true: 今日の9時 / false: 昨日の9時
				if (today9 <= now) {
					checktype = true;
				} else {
					checktype = false;
				}

				if (checktype) {
					if (lasttime > today9) {
						// 投票済み？
						return false;
					}
				} else {
					if (lasttime > yesterday9) {
						// 投票済み？
						return false;
					}
				}
			}
		} catch (UnsupportedOperationException | NullPointerException | NumberFormatException | SQLException e) {
			e.printStackTrace();
			return false; // エラー発生したらその日の初めての投票ではないとみなす。ただしエラー通知はする
		}
		return true; // だれも投票してなかったら、trueを返す
	}

	/**
	 * プレイヤーの最終投票日時をunixtimeで取得します。
	 * @return プレイヤーの最終投票のunixtime
	 * @throws ClassNotFoundException 内部でClassNotFoundExceptionが発生した場合
	 * @throws SQLException 内部でSQLExceptionが発生した場合
	 * @throws UnsupportedOperationException 投票数が取得できなかったとき
	 * @throws NullPointerException プレイヤーが取得できなかったとき
	 * @throws NumberFormatException 最終投票日時が正常に取得できなかったとき
	 */
	public Long getLastVoteUnixTime() throws ClassNotFoundException, SQLException, UnsupportedOperationException,
			NullPointerException, NumberFormatException {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		if (!exists())
			return -1L;
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return -1L;
		}
		Connection conn = MySQLDBManager.getConnection();
		PreparedStatement statement = conn
				.prepareStatement("SELECT * FROM vote WHERE id = ?");
		statement.setInt(1, getID());
		ResultSet res = statement.executeQuery();
		if (res.next()) {
			Long unixtime;
			try {
				unixtime = Long.parseLong(res.getString("lasttime"));
			} catch (NumberFormatException e) {
				throw new NumberFormatException("最終投票日時が正常に取得できませんでした。");
			}
			return unixtime;
		} else {
			throw new UnsupportedOperationException("Could not get Vote LastTime.");
		}
	}

	public boolean isVoted() {
		try {
			Long lasttime = getLastVoteUnixTime();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
			long today9 = cal.getTimeInMillis() / 1000L;

			cal.add(Calendar.DAY_OF_MONTH, -1);
			long yesterday9 = cal.getTimeInMillis() / 1000L;

			long now = System.currentTimeMillis() / 1000L;

			boolean checktype; // true: 今日の9時 / false: 昨日の9時
			if (today9 <= now) {
				checktype = true;
			} else {
				checktype = false;
			}

			if (checktype) {
				if (lasttime < today9) {
					return false;
				}
			} else {
				if (lasttime < yesterday9) {
					return false;
				}
			}
		} catch (UnsupportedOperationException | NullPointerException | NumberFormatException | ClassNotFoundException
				| SQLException e) {
			return false; // エラー発生したら投票してないものとみなす
		}
		return true; // どれもひっかからなかったら投票したものとみなす
	}

	/**
	 * プレイヤーの投票数データを作成する<br>
	 * ※初めての投票時に作成すること！
	 * @return 作成できたかどうか
	 * @throws SQLException 内部でSQLExceptionが発生した場合
	 * @throws ClassNotFoundException 内部でClassNotFoundExceptionが発生した場合
	 * @throws NullPointerException 内部でNullPointerExceptionが発生した場合
	 */
	public boolean create() throws ClassNotFoundException, SQLException, NullPointerException {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		if (exists())
			return false;
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return false;
		}
		Connection conn = MySQLDBManager.getConnection();
		PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO vote (player, uuid, count, first, lasttime, last) VALUES (?, ?, ?, ?, ?, ?);");
		statement.setString(1, offplayer.getName()); // player
		statement.setString(2, offplayer.getUniqueId().toString()); // uuid
		statement.setInt(3, 1);

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date Date = new Date();
		String date = format.format(Date);
		statement.setString(4, date);
		statement.setInt(5, (int) (System.currentTimeMillis() / 1000L));
		statement.setString(6, date);
		int count = statement.executeUpdate();
		if (count != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * プレイヤーの投票数データが存在するかどうかを確認します。
	 * @return 存在するかどうか
	 * @throws SQLException 内部でSQLExceptionが発生した場合
	 * @throws ClassNotFoundException 内部でClassNotFoundExceptionが発生した場合
	 * @throws NullPointerException 内部でNullPointerExceptionが発生した場合
	 * @throws UnsupportedOperationException 内部でUnsupportedOperationExceptionが発生した場合
	 * @author mine_book000
	 */
	public boolean exists()
			throws SQLException, ClassNotFoundException, NullPointerException, UnsupportedOperationException {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return false;
		}
		Connection conn = MySQLDBManager.getConnection();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM vote WHERE uuid = ? ORDER BY id DESC");
		statement.setString(1, offplayer.getUniqueId().toString()); // uuid
		ResultSet res = statement.executeQuery();
		if (res.next()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * プレイヤーの投票数に1つ追加します。
	 * @return 実行できたかどうか
	 * @throws ClassNotFoundException 内部でClassNotFoundExceptionが発生した場合
	 * @throws SQLException 内部でSQLExceptionが発生した場合
	 * @throws NullPointerException プレイヤーが取得できなかったとき
	 */
	public boolean add() throws ClassNotFoundException, SQLException, NullPointerException {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		if (!exists()) {
			create();
			return true;
		}
		int next = get() + 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			return false;
		}
		Connection conn = MySQLDBManager.getConnection();
		PreparedStatement statement = conn
				.prepareStatement("UPDATE vote SET count = ?, lasttime = ?, last = ? WHERE id = ?");
		statement.setInt(1, next);
		statement.setInt(2, (int) (System.currentTimeMillis() / 1000L));
		statement.setString(3, sdf.format(new Date()));
		statement.setInt(4, getID());
		int upcount = statement.executeUpdate();
		if (upcount != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * プレイヤーのIDを取得します。
	 * @return 所持しているjaoポイント
	 * @throws SQLException 内部でSQLExceptionが発生した場合
	 * @throws ClassNotFoundException 内部でClassNotFoundExceptionが発生した場合
	 * @throws NullPointerException 内部でNullPointerExceptionが発生した場合
	 * @throws UnsupportedOperationException 内部でUnsupportedOperationExceptionが発生した場合
	 * @author mine_book000
	 */
	public int getID()
			throws SQLException, ClassNotFoundException, NullPointerException, UnsupportedOperationException {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
		if (MySQLDBManager == null) {
			throw new UnsupportedOperationException("Could not get ID.");
		}
		Connection conn = MySQLDBManager.getConnection();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM vote WHERE uuid = ? ORDER BY id DESC");
		statement.setString(1, offplayer.getUniqueId().toString()); // uuid
		ResultSet res = statement.executeQuery();
		if (res.next()) {
			return res.getInt("id");
		} else {
			throw new UnsupportedOperationException("Could not get ID.");
		}
	}

	/**
	 * プレイヤー名を更新します。
	 * @throws SQLException 内部でSQLExceptionが発生した場合
	 * @author mine_book000
	 */
	public void changePlayerName() {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		try {
			if (!exists())
				return;

			MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
			if (MySQLDBManager == null) {
				return;
			}
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement("UPDATE vote SET player = ? WHERE uuid = ?");
			statement.setString(1, offplayer.getName());
			statement.setString(2, offplayer.getUniqueId().toString());// uuid
			statement.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ChatColor getCustomColor() {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		try {
			if (!exists())
				return null;

			MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
			if (MySQLDBManager == null) {
				return null;
			}
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM vote WHERE uuid = ?");
			statement.setString(1, offplayer.getUniqueId().toString());// uuid
			ResultSet res = statement.executeQuery();
			if (res.next()) {
				String color_str = res.getString("color");
				return ChatColor.valueOf(color_str);
			} else {
				return null;
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setCustomColor(ChatColor color) {
		if (offplayer == null)
			throw new NullPointerException("We could not get the player.");
		try {
			if (!exists())
				return;

			MySQLDBManager MySQLDBManager = Main.MySQLDBManager;
			if (MySQLDBManager == null) {
				return;
			}
			Connection conn = MySQLDBManager.getConnection();
			PreparedStatement statement = conn.prepareStatement("UPDATE vote SET color = ? WHERE uuid = ?");
			statement.setString(1, color.name());
			statement.setString(2, offplayer.getUniqueId().toString());// uuid
			statement.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
