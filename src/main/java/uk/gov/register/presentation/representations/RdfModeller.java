package uk.gov.register.presentation.representations;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import uk.gov.register.presentation.FieldValue;
import uk.gov.register.presentation.LinkValue;
import uk.gov.register.presentation.ListValue;
import uk.gov.register.presentation.resource.RequestContext;
import uk.gov.register.presentation.view.ItemView;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

public class RdfModeller {
    private final RequestContext requestContext;

    public RdfModeller(RequestContext requestContext) {

        this.requestContext = requestContext;
    }

    public static final String ITEM_PREFIX = "//%1$s.%2$s/item/";
    static final String ITEM_FIELD_PREFIX = "//field.%s/record/";

    public Model rdfModel(ItemView itemView) {
        String itemFieldPrefix = fieldUri().toString();

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(itemUri(itemView).toString());

        for (Map.Entry<String, FieldValue> field : itemView.getContent().entrySet()) {
            RdfModeller.FieldRenderer fieldRenderer = new RdfModeller.FieldRenderer(model.createProperty(itemFieldPrefix + field.getKey()));
            fieldRenderer.render(field.getValue(), resource);
        }
        model.setNsPrefix("field", itemFieldPrefix);
        return model;
    }

    private URI fieldUri() {
        String path = String.format(RdfModeller.ITEM_FIELD_PREFIX, requestContext.getRegisterDomain());
        return uriWithScheme(path).build();
    }

    private URI itemUri(ItemView itemView) {
        String path = String.format(RdfModeller.ITEM_PREFIX, requestContext.getRegisterPrimaryKey(), requestContext.getRegisterDomain());
        return uriWithScheme(path).path(itemView.getSha256hex()).build();
    }

    private UriBuilder uriWithScheme(String path) {
        return UriBuilder.fromPath(path).scheme(requestContext.getScheme());
    }

    private static class FieldRenderer {
        private final Property fieldProperty;

        public FieldRenderer(Property fieldProperty) {
            this.fieldProperty = fieldProperty;
        }

        public void render(FieldValue fieldO, Resource resource) {
            renderField(fieldO, resource);
        }

        private void renderField(FieldValue value, Resource resource) {
            if (value.isList()) {
                renderList((ListValue) value, resource);
            }
            else {
                renderScalar(value, resource);
            }
        }

        private void renderList(ListValue listValue, Resource resource) {
            for (FieldValue value : listValue) {
                renderScalar(value, resource);
            }
        }

        private void renderScalar(FieldValue value, Resource resource) {
            if (value.isLink()) {
                resource.addProperty(fieldProperty, resource.getModel().createResource(((LinkValue) value).link()));
            } else {
                resource.addProperty(fieldProperty, value.getValue());
            }
        }
    }
}
