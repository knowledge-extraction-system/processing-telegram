package kz.kaznu.telegram.processing.dao.rowmappers;

import kz.kaznu.telegram.processing.dao.entities.NlpChatToken;
import kz.kaznu.telegram.processing.dao.entities.NlpToken;
import kz.kaznu.telegram.processing.dao.entities.TelegramChat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NlpChatTokenRowMapper implements RowMapper<NlpChatToken> {

    @Override
    public NlpChatToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        NlpChatToken nlpChatToken = new NlpChatToken();
        nlpChatToken.setTelegramChat(new TelegramChat(rs.getLong("telegram_chat_id")));
        nlpChatToken.setNlpToken(new NlpToken(rs.getLong("nlp_token_id")));
        nlpChatToken.setCount(rs.getLong("count"));

        return nlpChatToken;
    }
}
