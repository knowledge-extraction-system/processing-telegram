package kz.kaznu.telegram.processing.dao.rowmappers;

import kz.kaznu.telegram.processing.models.TelegramChatElasticSearch;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TelegramChatElasticSearchRowMapper implements RowMapper<TelegramChatElasticSearch> {

    @Override
    public TelegramChatElasticSearch mapRow(ResultSet rs, int rowNum) throws SQLException {
        TelegramChatElasticSearch telegramChatElasticSearch = new TelegramChatElasticSearch();
        telegramChatElasticSearch.setTelegramChatId(rs.getLong("id"));
        telegramChatElasticSearch.setTitle(rs.getString("title"));
        telegramChatElasticSearch.setDescription(rs.getString("description"));
        telegramChatElasticSearch.setMemberCount(rs.getInt("member_count"));
        telegramChatElasticSearch.setChannel(rs.getBoolean("is_channel"));

        return telegramChatElasticSearch;
    }
}
