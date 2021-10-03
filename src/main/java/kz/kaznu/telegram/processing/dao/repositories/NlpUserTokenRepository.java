package kz.kaznu.telegram.processing.dao.repositories;

import kz.kaznu.telegram.processing.dao.entities.NlpUserToken;
import kz.kaznu.telegram.processing.dao.rowmappers.NlpUserTokenRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by yerzhan on 10/19/19.
 */
@Repository
public class NlpUserTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NlpUserTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public NlpUserToken findByUserAndToken(Long nlpUserTokenId, Long nlpTokenId) {
        try {
            final String query = "select * from nlp_user_token where telegram_user_id = ? and nlp_token_id = ?";
            return jdbcTemplate.queryForObject(query, new Object[]{nlpUserTokenId, nlpTokenId}, new NlpUserTokenRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void insert(NlpUserToken nlpUserToken) {
        final String query = "insert into nlp_user_token (telegram_user_id, nlp_token_id, count) values ( ?, ?, ?)";
        jdbcTemplate.update(query, nlpUserToken.getTelegramUser().getId(), nlpUserToken.getNlpToken().getId(), nlpUserToken.getCount());
    }

    public void update(NlpUserToken nlpUserToken) {
        final String query = "update nlp_user_token set count = ? where telegram_user_id = ? and nlp_token_id = ?";
        jdbcTemplate.update(query, nlpUserToken.getCount(), nlpUserToken.getTelegramUser().getId(), nlpUserToken.getNlpToken().getId());
    }

}
