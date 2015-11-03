package uk.gov.register.presentation.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;
import uk.gov.register.presentation.DbEntry;
import uk.gov.register.presentation.mapper.EntryMapper;

import java.util.List;
import java.util.Optional;

@RegisterMapper(EntryMapper.class)
public interface RecentEntryIndexQueryDAO {

    @SqlQuery("select serial_number,entry from ordered_entry_index order by serial_number desc limit :limit offset :offset")
    List<DbEntry> getAllEntries(@Bind("limit") long maxNumberToFetch, @Bind("offset") long offset);

    @SqlQuery("select serial_number,entry from ordered_entry_index where serial_number = (select serial_number from current_keys where key = :key)")
    @SingleValueResult(DbEntry.class)
    Optional<DbEntry> findByPrimaryKey(@Bind("key") String key);

    @SqlQuery("select serial_number,entry from ordered_entry_index where (entry #>> array['entry',:key]) = :value order by serial_number desc")
    List<DbEntry> findAllByKeyValue(@Bind("key") String key, @Bind("value") String value);

    @SqlQuery("select serial_number,entry from ordered_entry_index where (entry #>> array['hash']) = :hash")
    @SingleValueResult(DbEntry.class)
    Optional<DbEntry> findByHash(@Bind("hash") String hash);

    @SqlQuery("select serial_number,entry from ordered_entry_index where serial_number = :serial")
    @SingleValueResult(DbEntry.class)
    Optional<DbEntry> findBySerial(@Bind("serial") long serial);

    @SqlQuery("select serial_number,entry from ordered_entry_index where serial_number in(select serial_number from current_keys order by serial_number desc limit :limit) order by serial_number desc")
    List<DbEntry> getLatestEntriesOfRecords(@Bind("limit") long maxNumberToFetch);

    @SqlQuery("select count from register_entries_count")
    int getTotalEntriesCount();
}
