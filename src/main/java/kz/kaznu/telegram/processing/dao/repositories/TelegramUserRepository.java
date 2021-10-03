package kz.kaznu.telegram.processing.dao.repositories;

import kz.kaznu.telegram.processing.dao.entities.TelegramUser;
import kz.kaznu.telegram.processing.dao.rowmappers.TelegramUserShortRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by yerzhan on 10/10/19.
 */
@Repository
public class TelegramUserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TelegramUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TelegramUser findById(Long id) {
        try {
            final String query = "select * from telegram_user where id=?";
            return jdbcTemplate.queryForObject(query, new Object[]{id}, new TelegramUserShortRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
