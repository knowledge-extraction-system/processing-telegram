package kz.kaznu.telegram.processing.dao.rowmappers;

import kz.kaznu.telegram.processing.models.TelegramMessageElasticSearch;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelegramMessageElasticSearchListRowMapper implements RowMapper<TelegramMessageElasticSearch> {

    @Override
    public TelegramMessageElasticSearch mapRow(ResultSet rs, int rowNum) throws SQLException {
        TelegramMessageElasticSearch telegramMessage = new TelegramMessageElasticSearch();
        telegramMessage.setTelegramMessageId(rs.getLong("id"));
        telegramMessage.setDate(rs.getTimestamp("date").getTime());
        telegramMessage.setMessage(rs.getString("message"));
        telegramMessage.setTelegramUserId(rs.getLong("telegram_user_id"));
        telegramMessage.setTelegramChatId(rs.getLong("telegram_chat_id"));
        telegramMessage.setThreadId(rs.getLong("message_thread_id"));
        telegramMessage.setThreadCount(rs.getLong("thread_count"));
        telegramMessage.setUserChatCount(rs.getLong("user_chat_count"));

        return telegramMessage;
    }
}
