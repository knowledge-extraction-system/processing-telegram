package kz.kaznu.telegram.processing.dao.rowmappers;

import kz.kaznu.telegram.processing.dao.entities.NlpToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NlpTokenRowMapper implements RowMapper<NlpToken> {

    @Override
    public NlpToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        NlpToken nlpToken = new NlpToken();
        nlpToken.setId(rs.getLong("id"));
        nlpToken.setToken(rs.getString("token"));

        return nlpToken;
    }
}
