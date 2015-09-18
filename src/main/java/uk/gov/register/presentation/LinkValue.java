package uk.gov.register.presentation;

public class LinkValue implements FieldValue {
    private static final String template = "http://%s/%s/%s";
    private final String value;
    private final String link;

    public LinkValue(String registerName, String value, String registerDomain) {
        this.value = value;
        this.link = String.format(template, registerName + "." + registerDomain, registerName, value);
    }

    @Override
    public boolean isLink() {
        return true;
    }

    @Override
    public String value() {
        return value;
    }

    public String link() {
        return link;
    }
}
