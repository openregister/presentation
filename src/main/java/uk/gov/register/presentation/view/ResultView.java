package uk.gov.register.presentation.view;

import uk.gov.register.presentation.mapper.JsonObjectMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultView extends AbstractView {
    private Object object;

    public ResultView(String templateName, Object object) {
        super(templateName);
        this.object = object;
    }

    @Override
    public Object getObject() {

        return object;
    }

    @Override
    public ResultView flatten() {
        final Set<HashMap.Entry> sets = new HashSet<>();
        if (object != null) {
            Map map = JsonObjectMapper.convert(object, Map.class);
            toSets(map, sets);
        }
        return new ResultView(getTemplateName(), sets);
    }


    private void toSets(Map m, Set<HashMap.Entry> sets) {
        m.entrySet().stream()
                .forEachOrdered(e -> {
                    final HashMap.Entry entry = (HashMap.Entry) e;
                    if (entry.getValue() instanceof Map) {
                        toSets((Map) entry.getValue(), sets);
                    } else {
                        sets.add(entry);
                    }
                });
    }
}
