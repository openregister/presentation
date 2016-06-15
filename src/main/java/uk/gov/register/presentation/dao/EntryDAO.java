package uk.gov.register.presentation.dao;

import io.dropwizard.java8.jdbi.args.InstantMapper;
import org.skife.jdbi.v2.ResultIterator;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.FetchSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface EntryDAO extends Transactional<EntryDAO> {
    @SqlQuery("select * from entry where entry_number=:entry_number")
    @RegisterMapper(EntryMapper.class)
    @SingleValueResult(Entry.class)
    Optional<Entry> findByEntryNumber(@Bind("entry_number") int entryNumber);

    // FIXME: "lower" and "upper" names are rubbish
    @SqlQuery("select * from entry WHERE entry_number >= :lower AND entry_number < :upper ORDER BY entry_number")
    @RegisterMapper(EntryMapper.class)
    @FetchSize(262144) // Has to be non-zero to enable cursor mode https://jdbc.postgresql.org/documentation/head/query.html#query-with-cursor
    ResultIterator<Entry> entriesIterator(@Bind("lower") long lower, @Bind("upper") long upper);

    @SqlQuery("select * from entry ORDER BY entry_number desc limit :limit offset :offset")
    @RegisterMapper(EntryMapper.class)
    List<Entry> getEntries(@Bind("limit") long maxNumberToFetch, @Bind("offset") long offset);

    @RegisterMapper(InstantMapper.class)
    @SqlQuery("SELECT timestamp FROM entry ORDER BY entry_number DESC LIMIT 1")
    Instant getLastUpdatedTime();

    @SqlQuery("SELECT count FROM total_entries")
    int getTotalEntries();

    //Note: This is fine for small data registers like country
    @RegisterMapper(EntryMapper.class)
    @SqlQuery("SELECT * from entry order by entry_number desc")
    Collection<Entry> getAllEntriesNoPagination();

}

