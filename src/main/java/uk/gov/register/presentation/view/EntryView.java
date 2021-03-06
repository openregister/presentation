package uk.gov.register.presentation.view;

import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.organisation.client.GovukOrganisation;
import uk.gov.register.presentation.config.PublicBody;
import uk.gov.register.presentation.dao.Entry;
import uk.gov.register.presentation.representations.CsvRepresentation;
import uk.gov.register.presentation.resource.RequestContext;

import java.util.Optional;

public class EntryView extends CsvRepresentationView<Entry> {
    private Entry entry;

    public EntryView(RequestContext requestContext, PublicBody custodian, Optional<GovukOrganisation.Details> custodianBranding, Entry entry) {
        super(requestContext, custodian, custodianBranding, "entry.html");
        this.entry = entry;
    }

    @JsonValue
    public Entry getEntry() {
        return entry;
    }

    @Override
    public CsvRepresentation<Entry> csvRepresentation() {
        return new CsvRepresentation<>(Entry.csvSchema(), getEntry());
    }
}
