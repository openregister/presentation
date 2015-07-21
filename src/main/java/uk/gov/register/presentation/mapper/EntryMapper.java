package uk.gov.register.presentation.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import io.dropwizard.jackson.Jackson;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.gov.register.presentation.Entry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntryMapper implements ResultSetMapper<Entry> {
    private final ObjectMapper objectMapper;

    public EntryMapper() {
        objectMapper = Jackson.newObjectMapper();
    }

    @Override
    public Entry map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        try {
            return objectMapper.readValue(r.getBytes("entry"), Entry.class);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
