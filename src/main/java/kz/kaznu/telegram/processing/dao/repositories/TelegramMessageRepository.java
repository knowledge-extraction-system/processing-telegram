package kz.kaznu.telegram.processing.dao.repositories;

import kz.kaznu.telegram.processing.dao.entities.TelegramMessage;
import kz.kaznu.telegram.processing.models.TelegramMessageElasticSearch;
import kz.kaznu.telegram.processing.dao.rowmappers.TelegramMessageElasticSearchListRowMapper;
import kz.kaznu.telegram.processing.dao.rowmappers.TelegramMessageListRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yerzhan on 10/21/19.
 */
@Repository
public class TelegramMessageRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final JdbcTemplate jdbcTemplate;
    private final CommonRepository commonRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public TelegramMessageRepository(JdbcTemplate jdbcTemplate, CommonRepository commonRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.commonRepository = commonRepository;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public LocalDateTime findDateById(Long id) {
        final String query = "select date from telegram_message where id = :id";

        return namedParameterJdbcTemplate.queryForObject(query,
                new MapSqlParameterSource().addValue("id", id), LocalDateTime.class);
    }

    public List<TelegramMessage> findByChatIdAndBetweenDates(List<Long> chatIds, LocalDateTime dateStart, LocalDateTime dateStop) {
        final String query = "select * from telegram_message where telegram_chat_id in (:chatIds) and message_date between :dateStart and :dateStop";

        try {
            return namedParameterJdbcTemplate.queryForObject(query,
                    new MapSqlParameterSource()
                            .addValue("chatIds", chatIds)
                            .addValue("dateStart", dateStart)
                            .addValue("dateStop", dateStop)
                    , new TelegramMessageListRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<TelegramMessage> findByDateBetween(LocalDateTime dateStart, LocalDateTime dateStop) {
        final String query = "select * from telegram_message where date between :dateStart and :dateStop";

        try {
            return namedParameterJdbcTemplate.queryForObject(query,
                    new MapSqlParameterSource().addValue("dateStop", dateStop)
                            .addValue("dateStart", dateStart), new TelegramMessageListRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<TelegramMessageElasticSearch> extractDataForElasticSearch(LocalDateTime dateStart, LocalDateTime dateStop) {
        final String query = "SELECT id, date, message, telegram_user_id, telegram_chat_id, message_thread_id, COUNT(*) over (partition by message_thread_id) as thread_count, " +
                "(select COUNT(*) from chat_member cm where cm.telegram_chat_id = tm.telegram_chat_id) as user_chat_count " +
                "from telegram_message tm where date between ? and ? and deleted = false";

        try {
            return jdbcTemplate.query(query,new Object[]{ dateStart, dateStop}, new TelegramMessageElasticSearchListRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.error("Empty result set during sql search between dates: " + dateStart + " - " + dateStop, ex);
            return new ArrayList<>();
        }
    }
}
