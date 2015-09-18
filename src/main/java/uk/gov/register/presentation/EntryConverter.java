package uk.gov.register.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import org.jvnet.hk2.annotations.Service;
import uk.gov.register.presentation.config.FieldsConfiguration;
import uk.gov.register.presentation.config.RegisterDomainConfiguration;
import uk.gov.register.presentation.resource.RequestContext;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class EntryConverter {
    private final FieldsConfiguration fieldsConfiguration;
    private final RequestContext requestContext;
    private final RegisterDomainConfiguration registerDomainConfiguration;

    @Inject
    public EntryConverter(FieldsConfiguration fieldsConfiguration, RequestContext requestContext, RegisterDomainConfiguration registerDomainConfiguration) {
        this.fieldsConfiguration = fieldsConfiguration;
        this.requestContext = requestContext;
        this.registerDomainConfiguration = registerDomainConfiguration;
    }

    public EntryView convert(DbEntry dbEntry) {
        Iterable<Map.Entry<String, JsonNode>> fields = () -> dbEntry.getContent().getContent().fields();
        Stream<Map.Entry<String, JsonNode>> fieldStream = StreamSupport.stream(fields.spliterator(), false);
        return new EntryView(
                dbEntry.getSerialNumber(),
                dbEntry.getContent().getHash(),
                requestContext.getRegisterPrimaryKey(),
                fieldStream.collect(Collectors.toMap(Map.Entry::getKey, this::convert))
        );
    }


    private FieldValue convert(Map.Entry<String, JsonNode> entry) {
        Optional<String> register = fieldsConfiguration.getField(entry.getKey()).getRegister();
        return register.isPresent() ?
                new LinkValue(register.get(), entry.getValue().textValue(), registerDomainConfiguration.getRegisterDomain()) :
                new StringValue(entry.getValue().textValue());
    }
}
