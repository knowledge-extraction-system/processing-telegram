package kz.kaznu.telegram.processing.dao.repositories;


import kz.kaznu.telegram.processing.dao.entities.TelegramChat;
import kz.kaznu.telegram.processing.dao.rowmappers.TelegramChatElasticSearchRowMapper;
import kz.kaznu.telegram.processing.dao.rowmappers.TelegramChatShortRowMapper;
import kz.kaznu.telegram.processing.models.TelegramChatElasticSearch;
import kz.kaznu.telegram.processing.models.enums.ChatTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yerzhan on 10/19/19.
 */
@Repository
public class TelegramChatRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TelegramChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TelegramChat findById(Long id) {
        try {
            final String query = "select * from telegram_chat where id=?";
            return jdbcTemplate.queryForObject(query, new Object[]{id}, new TelegramChatShortRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<TelegramChatElasticSearch> findAllSuperGroupsActive() {
        try {
            final String query = "select id, title, description, member_count, is_channel from telegram_chat where is_active = true and \"type\" =  ?";
            return jdbcTemplate.query(query, new Object[]{ChatTypeEnum.ChatTypeSupergroup.name()}, new TelegramChatElasticSearchRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
