package uk.gov.register.presentation.view;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.jena.rdf.model.Model;
import uk.gov.organisation.client.GovukOrganisation;
import uk.gov.register.presentation.ItemConverter;
import uk.gov.register.presentation.FieldValue;
import uk.gov.register.presentation.config.PublicBody;
import uk.gov.register.presentation.dao.Item;
import uk.gov.register.presentation.representations.CsvRepresentation;
import uk.gov.register.presentation.representations.RdfModeller;
import uk.gov.register.presentation.representations.RepresentationView;
import uk.gov.register.presentation.resource.RequestContext;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemView extends AttributionView implements RepresentationView {

    private ItemConverter itemConverter;
    private Item item;

    public ItemView(RequestContext requestContext, PublicBody custodian, Optional<GovukOrganisation.Details> branding, ItemConverter itemConverter, Item item) {
        super(requestContext, custodian, branding, "item.html");
        this.itemConverter = itemConverter;
        this.item = item;
    }

    @JsonValue
    public Map<String, FieldValue> getContent() {
        return item.getFieldsStream().collect(Collectors.toMap(Map.Entry::getKey, itemConverter::convert));
    }

    public String getSha256hex() {
        return item.getSha256hex();
    }

    @Override
    public CsvRepresentation<Map> csvRepresentation() {
        return new CsvRepresentation<>(Item.csvSchema(getRegister().getFields()), getContent());
    }

    @Override
    public Model turtleRepresentation() {
        RdfModeller rdfModeller = new RdfModeller(requestContext);
        return rdfModeller.rdfModel(this);
    }
}

