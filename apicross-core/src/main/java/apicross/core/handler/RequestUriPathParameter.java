package apicross.core.handler;

import apicross.core.NamedDatum;
import apicross.core.data.DataModel;

public class RequestUriPathParameter extends NamedDatum {
    public RequestUriPathParameter(String name, String resolvedName, String description, DataModel type, boolean required, boolean deprecated) {
        super(name, resolvedName, description, type, required, deprecated);
    }
}
