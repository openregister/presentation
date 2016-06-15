package uk.gov.register.presentation.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Iterators;
import org.apache.commons.lang3.StringUtils;
import uk.gov.register.presentation.dao.Entry;
import uk.gov.register.presentation.dao.EntryDAO;
import uk.gov.verifiablelog.merkletree.MerkleTree;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/")
public class VerifiableLogResource {

    private final EntryDAO entryDAO;

    @Inject
    public VerifiableLogResource(EntryDAO entryDAO) throws NoSuchAlgorithmException {
        this.entryDAO = entryDAO;
    }

    @GET
    @Path("/mth")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMth() throws NoSuchAlgorithmException {
        try {
            // need to use a transaction to enable use of a cursor
            entryDAO.begin();
            MerkleTree merkleTree = merkleTree();
            return bytesToString(merkleTree.currentRoot());
        } finally {
            entryDAO.rollback();
        }
    }

    @GET
    @Path("/audit/{snapshot}/{entry-number}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAuditPath(@PathParam("entry-number") int entryNumber, @PathParam("snapshot") int snapshot) throws NoSuchAlgorithmException {
        try {
            entryDAO.begin();
            MerkleTree merkleTree = merkleTree();
            List<String> path = bytesToString(merkleTree.pathToRootAtSnapshot(entryNumber - 1, snapshot));
            return StringUtils.join(path, ",");
        } finally {
            entryDAO.rollback();
        }
    }

    @GET
    @Path("/consistency/{m}/{n}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConsistencySet(@PathParam("m") int m, @PathParam("n") int n) throws NoSuchAlgorithmException {
        try {
            entryDAO.begin();
            MerkleTree merkleTree = merkleTree();
            List<String> path = bytesToString(merkleTree.snapshotConsistency(m, n));
            return StringUtils.join(path, ",");
        } finally {
            entryDAO.rollback();
        }
    }

    private MerkleTree merkleTree() throws NoSuchAlgorithmException {
        return new MerkleTree(MessageDigest.getInstance("SHA-256"),
                i -> bytesFromEntry(entryDAO.findByEntryNumber(i + 1).get()),
                entryDAO::getTotalEntries);
    }

    private byte[] bytesFromEntry(Entry value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

            return mapper.writeValueAsString(value).getBytes();
        } catch (JsonProcessingException e) {
            // FIXME swallow for now and return null byte
            return new byte[]{0x00};
        }
    }

    private List<String> bytesToString(List<byte[]> listOfByteArrays) {
        return listOfByteArrays.stream().map(this::bytesToString).collect(toList());
    }

    private String bytesToString(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }
}
