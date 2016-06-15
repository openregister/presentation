package uk.gov.register.presentation.dao;

import org.skife.jdbi.v2.ResultIterator;

public class EntryIteratorDAO {


    private final EntryDAO entryDAO;
    private final int fetchSize;
    private int nextValidEntryNumber = -1;
    private ResultIterator<Entry> iterator;

    public EntryIteratorDAO(EntryDAO entryDAO, int fetchSize){
        this.entryDAO = entryDAO;
        this.fetchSize = fetchSize;
        this.nextValidEntryNumber = -1;
    }

    public Entry findByEntryNumber(int entryNumber) {

        if ((iterator == null) || (entryNumber != nextValidEntryNumber)){
            if(iterator != null){
                iterator.close();
            }
            iterator = entryDAO.entriesIterator(entryNumber, fetchSize);
            nextValidEntryNumber = entryNumber;
        }

        nextValidEntryNumber++;
        return iterator.next();
    }

}
