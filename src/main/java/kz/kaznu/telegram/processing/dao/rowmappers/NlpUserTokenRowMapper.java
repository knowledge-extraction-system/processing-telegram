package kz.kaznu.telegram.processing.dao.rowmappers;

import kz.kaznu.telegram.processing.dao.entities.NlpToken;
import kz.kaznu.telegram.processing.dao.entities.NlpUserToken;
import kz.kaznu.telegram.processing.dao.entities.TelegramUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NlpUserTokenRowMapper implements RowMapper<NlpUserToken> {

    @Override
    public NlpUserToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        NlpUserToken nlpUserToken = new NlpUserToken();
        nlpUserToken.setTelegramUser(new TelegramUser(rs.getLong("telegram_user_id")));
        nlpUserToken.setNlpToken(new NlpToken(rs.getLong("nlp_token_id")));
        nlpUserToken.setCount(rs.getLong("count"));

        return nlpUserToken;
    }
}
