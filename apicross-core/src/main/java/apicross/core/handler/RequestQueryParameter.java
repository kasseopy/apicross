package apicross.core.handler;

import apicross.core.NamedDatum;
import apicross.core.data.DataModel;

public class RequestQueryParameter extends NamedDatum {
    public RequestQueryParameter(String name, String resolvedName, String description, DataModel type, boolean required, boolean deprecated) {
        super(name, resolvedName, description, type, required, deprecated);
    }
}
