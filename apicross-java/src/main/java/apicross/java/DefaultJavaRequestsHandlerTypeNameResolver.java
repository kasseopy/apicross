package apicross.java;

import apicross.core.handler.model.HttpOperationsGroup;
import apicross.core.handler.RequestsHandlerTypeNameResolver;
import org.apache.commons.lang3.StringUtils;

public class DefaultJavaRequestsHandlerTypeNameResolver implements RequestsHandlerTypeNameResolver {
    @Override
    public String resolve(HttpOperationsGroup httpOperationsGroup) {
        return StringUtils.capitalize(toJavaIdentifier(httpOperationsGroup.getName())) + "RequestsHandler";
    }

    protected static String toJavaIdentifier(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if ((i == 0 && Character.isJavaIdentifierStart(str.charAt(i))) || (i > 0 && Character.isJavaIdentifierPart(str.charAt(i)))) {
                sb.append(str.charAt(i));
            } else {
                sb.append((int) str.charAt(i));
            }
        }
        return sb.toString();
    }
}
