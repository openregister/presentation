package uk.gov.register.presentation.view;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dropwizard.jackson.Jackson;
import uk.gov.organisation.client.GovukOrganisation;
import uk.gov.register.presentation.ItemConverter;
import uk.gov.register.presentation.FieldValue;
import uk.gov.register.presentation.config.PublicBody;
import uk.gov.register.presentation.dao.Record;
import uk.gov.register.presentation.representations.CsvRepresentation;
import uk.gov.register.presentation.resource.RequestContext;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecordView extends CsvRepresentationView {
    private ItemConverter itemConverter;
    private final Record record;

    public RecordView(RequestContext requestContext, PublicBody custodian, Optional<GovukOrganisation.Details> custodianBranding, ItemConverter itemConverter, Record record) {
        super(requestContext, custodian, custodianBranding, "record.html");
        this.itemConverter = itemConverter;
        this.record = record;
    }

    public String getPrimaryKey() {
        return record.item.content.get(requestContext.getRegisterPrimaryKey()).textValue();
    }

    @SuppressWarnings("unused, used to create the json representation of this class")
    @JsonValue
    public ObjectNode getRecordJson() {
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        ObjectNode jsonNodes = objectMapper.convertValue(record.entry, ObjectNode.class);
        jsonNodes.setAll((ObjectNode) record.item.content.deepCopy());
        return jsonNodes;
    }

    public Map<String, FieldValue> getContent() {
        return record.item.getFieldsStream().collect(Collectors.toMap(Map.Entry::getKey, itemConverter::convert));
    }

    @SuppressWarnings("unused, used from html templates")
    public Optional<FieldValue> getField(String fieldName) {
        return Optional.ofNullable(getContent().get(fieldName));
    }

    public Record getRecord() {
        return record;
    }

    @Override
    public CsvRepresentation<ObjectNode> csvRepresentation() {
        return new CsvRepresentation<>(Record.csvSchema(getRegister().getFields()), getRecordJson());
    }
}
