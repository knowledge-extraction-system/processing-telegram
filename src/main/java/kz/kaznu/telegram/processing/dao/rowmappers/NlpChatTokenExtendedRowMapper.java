package kz.kaznu.telegram.processing.dao.rowmappers;

import kz.kaznu.telegram.processing.dao.entities.NlpChatToken;
import kz.kaznu.telegram.processing.dao.entities.NlpToken;
import kz.kaznu.telegram.processing.dao.entities.TelegramChat;
import kz.kaznu.telegram.processing.dao.extended.NlpChatTokenExtended;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NlpChatTokenExtendedRowMapper implements RowMapper<NlpChatTokenExtended> {

    @Override
    public NlpChatTokenExtended mapRow(ResultSet rs, int rowNum) throws SQLException {
        NlpChatTokenExtended nlpChatTokenExtended = new NlpChatTokenExtended();
        nlpChatTokenExtended.setToken(rs.getString("token"));
        nlpChatTokenExtended.setChatCount(rs.getLong("sum_count"));

        return nlpChatTokenExtended;
    }
}
