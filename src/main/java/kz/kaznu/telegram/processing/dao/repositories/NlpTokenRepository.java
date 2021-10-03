package kz.kaznu.telegram.processing.dao.repositories;

import kz.kaznu.telegram.processing.dao.entities.NlpToken;
import kz.kaznu.telegram.processing.dao.rowmappers.NlpTokenRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by yerzhan on 10/19/19.
 */
@Repository
public class NlpTokenRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CommonRepository commonRepository;

    @Autowired
    public NlpTokenRepository(JdbcTemplate jdbcTemplate, CommonRepository commonRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.commonRepository = commonRepository;
    }

    public NlpToken findByToken(String token) {
        try {
            final String query = "select * from nlp_token where token = ?";
            return jdbcTemplate.queryForObject(query, new Object[]{token}, new NlpTokenRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long insert(NlpToken nlpToken) {
        final String query = "insert into nlp_token (id, token) values ( ?, ?)";
        final Long id = commonRepository.getNextValueForTokenSequence();
        jdbcTemplate.update(query, id, nlpToken.getToken());
        return id;
    }

}
