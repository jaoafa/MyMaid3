package com.jaoafa.MyMaid3.DiscordEvent;

import com.jaoafa.MyMaid3.Lib.MyMaidConfig;
import com.jaoafa.jaoSuperAchievement2.API.AchievementAPI;
import com.jaoafa.jaoSuperAchievement2.API.Achievementjao;
import com.jaoafa.jaoSuperAchievement2.Lib.AchievementType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ServerChatListCmd {
    @SubscribeEvent
    public void onListCommand(MessageReceivedEvent event) {
        JDA jda = event.getJDA();
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        if(event.isWebhookMessage()){
            return;
        }
        Guild guild = event.getGuild();
        if(guild.getIdLong() != 597378876556967936L){
            return;
        }
        MessageChannel channel = event.getChannel();
        if(channel.getIdLong() != MyMaidConfig.getServerChatChannel().getIdLong()){
            return;
        }
        Member member = event.getMember();
        if(member == null){
            return;
        }
        Message message = event.getMessage();
        String text = message.getContentRaw();

        if(!text.equalsIgnoreCase("/list")){
            return;
        }

        try {
            Connection conn = MyMaidConfig.getMySQLDBManager().getConnection();
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM discordlink WHERE disid = ? AND disabled = ?");
            statement.setString(1, member.getId());
            statement.setBoolean(2, false);
            ResultSet res = statement.executeQuery();
            if(!res.next()){
                return;
            }

            String uuid = res.getString("uuid");
            OfflinePlayer offplayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            Achievementjao.getAchievement(offplayer, new AchievementType(61)); // 誰かいるかな？ 鯖茶から/listコマンドを実行する

            res.close();
            statement.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
