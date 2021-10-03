package kz.kaznu.telegram.processing.dao.repositories;

import kz.kaznu.telegram.processing.dao.entities.NlpChatToken;
import kz.kaznu.telegram.processing.dao.extended.NlpChatTokenExtended;
import kz.kaznu.telegram.processing.dao.rowmappers.NlpChatTokenExtendedRowMapper;
import kz.kaznu.telegram.processing.dao.rowmappers.NlpChatTokenRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yerzhan on 10/19/19.
 */
@Repository
public class NlpChatTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NlpChatTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public NlpChatToken findByChatAndToken(Long nlpChatTokenId, Long nlpTokenId) {
        try {
            final String query = "select * from nlp_chat_token where telegram_chat_id = ? and nlp_token_id = ?";
            return jdbcTemplate.queryForObject(query, new Object[]{nlpChatTokenId, nlpTokenId}, new NlpChatTokenRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<NlpChatTokenExtended> findAllWithToken() {
        try {
            final String query = "select nt.token, SUM(nct.count) as sum_count from nlp_chat_token nct " +
                    "inner join nlp_token nt on nt.id=nct.nlp_token_id " +
                    "group by nt.token";
            return jdbcTemplate.query(query, new Object[]{}, new NlpChatTokenExtendedRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void insert(NlpChatToken nlpChatToken) {
        final String query = "insert into nlp_chat_token (telegram_chat_id, nlp_token_id, count) values ( ?, ?, ?)";
        jdbcTemplate.update(query, nlpChatToken.getTelegramChat().getId(), nlpChatToken.getNlpToken().getId(), nlpChatToken.getCount());
    }

    public void update(NlpChatToken nlpChatToken) {
        final String query = "update nlp_chat_token set count = ? where telegram_chat_id = ? and nlp_token_id = ?";
        jdbcTemplate.update(query, nlpChatToken.getCount(), nlpChatToken.getTelegramChat().getId(), nlpChatToken.getNlpToken().getId());
    }

}
