package com.jaoafa.MyMaid3.Lib;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

public class PlayerVoteData_Monocraft {
    OfflinePlayer offplayer;

    /**
     * 指定したプレイヤーの投票データを取得します。
     *
     * @param player プレイヤー
     * @author mine_book000
     */
    public PlayerVoteData_Monocraft(Player player) {
        if (player == null)
            throw new NullPointerException("We could not get the player.");
        this.offplayer = player;

        changePlayerName();
    }

    /**
     * 指定したオフラインプレイヤーの投票データを取得します。
     *
     * @param offplayer オフラインプレイヤー
     * @author mine_book000
     */
    public PlayerVoteData_Monocraft(OfflinePlayer offplayer) {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        this.offplayer = offplayer;

        changePlayerName();
    }

    /**
     * 指定したプレイヤーネームの投票データを取得します。
     *
     * @param name プレイヤーネーム
     * @throws NullPointerException プレイヤーが取得できなかったとき
     * @author mine_book000
     * @deprecated プレイヤー名で検索するため、思い通りのプレイヤーを取得できない場合があります。
     */
    @Deprecated
    public PlayerVoteData_Monocraft(String name) throws NullPointerException {
        OfflinePlayer offplayer = Bukkit.getOfflinePlayer(name);
        this.offplayer = offplayer;

        changePlayerName();
    }

    /**
     * その日のうちに誰も投票していないかどうか調べる（その日初めての投票かどうか）
     *
     * @return 誰も投票してなければtrue
     */
    public static boolean isTodayFirstVote() {
        // 仮
        try {
            MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
            Connection conn = sqlmanager.getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM vote_monocraft");
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                long last = res.getTimestamp("last").getTime() / 1000L;
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                long today0 = cal.getTimeInMillis() / 1000L;

                if (today0 < last && last < today0 + 86400) {
                    res.close();
                    statement.close();
                    return false;
                }
            }
            res.close();
            statement.close();
        } catch (UnsupportedOperationException | NullPointerException | NumberFormatException | SQLException e) {
            e.printStackTrace();
            return false; // エラー発生したらその日の初めての投票ではないとみなす。ただしエラー通知はする
        }
        return true; // だれも投票してなかったら、trueを返す
    }

    /**
     * プレイヤーの投票数を取得します。
     *
     * @return プレイヤーの投票数
     * @throws SQLException                  内部でSQLExceptionが発生した場合
     * @throws UnsupportedOperationException 投票数が取得できなかったとき
     * @throws NullPointerException          プレイヤーが取得できなかったとき
     */
    public int get() throws SQLException, UnsupportedOperationException, NullPointerException {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        if (!exists())
            return 0;
        MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
        Connection conn = sqlmanager.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM vote_monocraft WHERE id = ?");
        statement.setInt(1, getID());
        ResultSet res = statement.executeQuery();
        if (res.next()) {
            int count = res.getInt("count");
            res.close();
            statement.close();
            return count;
        } else {
            res.close();
            statement.close();
            throw new UnsupportedOperationException("Could not get VoteCount.");
        }
    }

    /**
     * プレイヤーの最終投票日時をunixtimeで取得します。
     *
     * @return プレイヤーの最終投票のunixtime
     * @throws SQLException                  内部でSQLExceptionが発生した場合
     * @throws UnsupportedOperationException 投票数が取得できなかったとき
     * @throws NullPointerException          プレイヤーが取得できなかったとき
     * @throws NumberFormatException         最終投票日時が正常に取得できなかったとき
     */
    public Long getLastVoteMilliseconds() throws SQLException, UnsupportedOperationException,
            NullPointerException, NumberFormatException {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        if (!exists())
            return -1L;
        MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
        Connection conn = sqlmanager.getConnection();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM vote_monocraft WHERE id = ?");
        statement.setInt(1, getID());
        ResultSet res = statement.executeQuery();
        if (res.next()) {
            long unixtime = res.getTimestamp("last").getTime();
            res.close();
            statement.close();
            return unixtime;
        } else {
            res.close();
            statement.close();
            throw new UnsupportedOperationException("Could not get Vote LastTime.");
        }
    }

    public boolean isVoted() {
        try {
            Long lasttime = getLastVoteMilliseconds();
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            long today0 = cal.getTimeInMillis() / 1000L;

            cal.add(Calendar.DAY_OF_MONTH, -1);
            long yesterday0 = cal.getTimeInMillis() / 1000L;

            long now = System.currentTimeMillis() / 1000L;

            boolean checktype = today0 <= now; // true: 今日の0時 / false: 昨日の0時

            if (checktype) {
                if (lasttime < today0) {
                    return false;
                }
            } else {
                if (lasttime < yesterday0) {
                    return false;
                }
            }
        } catch (UnsupportedOperationException | NullPointerException | NumberFormatException | SQLException e) {
            return false; // エラー発生したら投票してないものとみなす
        }
        return true; // どれもひっかからなかったら投票したものとみなす
    }

    /**
     * プレイヤーの投票数データを作成する<br>
     * ※初めての投票時に作成すること！
     *
     * @return 作成できたかどうか
     * @throws SQLException         内部でSQLExceptionが発生した場合
     * @throws NullPointerException 内部でNullPointerExceptionが発生した場合
     */
    public boolean create() throws SQLException, NullPointerException {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        if (exists())
            return false;
        MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
        Connection conn = sqlmanager.getConnection();
        PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO vote_monocraft (player, uuid, count, first, last) VALUES (?, ?, ?, ?, ?);");
        statement.setString(1, offplayer.getName()); // player
        statement.setString(2, offplayer.getUniqueId().toString()); // uuid
        statement.setInt(3, 1);
        statement.setTimestamp(4, Timestamp.from(Instant.now()));
        statement.setTimestamp(5, Timestamp.from(Instant.now()));
        int count = statement.executeUpdate();
        statement.close();
        return count != 0;
    }

    /**
     * プレイヤーの投票数データが存在するかどうかを確認します。
     *
     * @return 存在するかどうか
     * @throws SQLException                  内部でSQLExceptionが発生した場合
     * @throws NullPointerException          内部でNullPointerExceptionが発生した場合
     * @throws UnsupportedOperationException 内部でUnsupportedOperationExceptionが発生した場合
     * @author mine_book000
     */
    public boolean exists()
            throws SQLException, NullPointerException, UnsupportedOperationException {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
        Connection conn = sqlmanager.getConnection();
        PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM vote_monocraft WHERE uuid = ? ORDER BY id DESC");
        statement.setString(1, offplayer.getUniqueId().toString()); // uuid
        ResultSet res = statement.executeQuery();
        if (res.next()) {
            res.close();
            statement.close();
            return true;
        } else {
            res.close();
            statement.close();
            return false;
        }
    }


    /**
     * プレイヤーの投票数に1つ追加します。
     *
     * @return 実行できたかどうか
     * @throws SQLException         内部でSQLExceptionが発生した場合
     * @throws NullPointerException プレイヤーが取得できなかったとき
     */
    public boolean add() throws SQLException, NullPointerException {
        return add(System.currentTimeMillis());
    }

    /**
     * プレイヤーの投票数に1つ追加します。
     *
     * @param millisecond MilliSeconds
     * @return 実行できたかどうか
     * @throws SQLException         内部でSQLExceptionが発生した場合
     * @throws NullPointerException プレイヤーが取得できなかったとき
     */
    public boolean add(long millisecond) throws SQLException, NullPointerException {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        if (!exists()) {
            create();
            return true;
        }
        int next = get() + 1;
        MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
        Connection conn = sqlmanager.getConnection();
        PreparedStatement statement = conn
                .prepareStatement("UPDATE vote_monocraft SET count = ?, last = ? WHERE id = ?");
        statement.setInt(1, next);
        statement.setTimestamp(2, Timestamp.from(Instant.ofEpochMilli(millisecond)));
        statement.setInt(3, getID());
        int upcount = statement.executeUpdate();
        statement.close();
        addLog(next);
        return upcount != 0;
    }

    private void addLog(int count) throws SQLException {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
        Connection conn = sqlmanager.getConnection();
        PreparedStatement statement = conn
                .prepareStatement("INSERT INTO votelog_success_mono (player, uuid, count, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP);");
        statement.setString(1, offplayer.getName());
        statement.setString(2, offplayer.getUniqueId().toString());
        statement.setInt(3, count);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * プレイヤーのIDを取得します。
     *
     * @return 所持しているjaoポイント
     * @throws SQLException                  内部でSQLExceptionが発生した場合
     * @throws NullPointerException          内部でNullPointerExceptionが発生した場合
     * @throws UnsupportedOperationException 内部でUnsupportedOperationExceptionが発生した場合
     * @author mine_book000
     */
    public int getID()
            throws SQLException, NullPointerException, UnsupportedOperationException {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
        Connection conn = sqlmanager.getConnection();
        PreparedStatement statement = conn
                .prepareStatement("SELECT * FROM vote_monocraft WHERE uuid = ? ORDER BY id DESC");
        statement.setString(1, offplayer.getUniqueId().toString()); // uuid
        ResultSet res = statement.executeQuery();
        if (res.next()) {
            int id = res.getInt("id");
            res.close();
            statement.close();
            return id;
        } else {
            res.close();
            statement.close();
            throw new UnsupportedOperationException("Could not get ID.");
        }
    }

    /**
     * プレイヤー名を更新します。
     *
     * @author mine_book000
     */
    public void changePlayerName() {
        if (offplayer == null)
            throw new NullPointerException("We could not get the player.");
        try {
            if (!exists())
                return;

            MySQLDBManager sqlmanager = MyMaidConfig.getMySQLDBManager();
            Connection conn = sqlmanager.getConnection();
            PreparedStatement statement = conn.prepareStatement("UPDATE vote_monocraft SET player = ? WHERE uuid = ?");
            statement.setString(1, offplayer.getName());
            statement.setString(2, offplayer.getUniqueId().toString());// uuid
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
