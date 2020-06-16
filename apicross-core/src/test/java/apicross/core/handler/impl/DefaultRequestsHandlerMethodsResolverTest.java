package apicross.core.handler.impl;

import apicross.core.data.DataModel;
import apicross.core.data.DataModelResolver;
import apicross.core.handler.HttpOperation;
import apicross.core.handler.ParameterNameResolver;
import apicross.core.handler.RequestsHandlerMethod;
import apicross.core.handler.RequestsHandlerMethodNameResolver;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRequestsHandlerMethodsResolverTest {
    private DefaultRequestsHandlerMethodsResolver resolver;

    @Mock
    private DefaultOperationRequestAndResponseResolver operationRequestAndResponseResolver;
    @Mock
    private RequestsHandlerMethodNameResolver methodNameResolver;
    @Mock
    private DataModelResolver dataModelResolver;

    @Before
    public void setUp() {
        when(methodNameResolver.resolve(any(Operation.class), anyString(), anyString(), anyString()))
                .thenAnswer(invocationOnMock -> ((Operation) invocationOnMock.getArgument(0)).getOperationId());

        resolver = new DefaultRequestsHandlerMethodsResolver(operationRequestAndResponseResolver, dataModelResolver, PARAMETER_NAME_RESOLVER);
    }

    @Test
    public void resolveMethods() {
        Operation operation = new Operation().operationId("test")
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse()));

        OperationRequestAndResponse operationRequestAndResponse = new OperationRequestAndResponse()
                .setRequestMediaType("application/json")
                .setRequestContentSchema(new ObjectSchema().name("TestRequest").$ref("TestRequest"))
                .setResponseMediaType("application/xml")
                .setResponseContentSchema(new ObjectSchema().name("TestResponse").$ref("TestResponse")); //

        when(operationRequestAndResponseResolver.resolve(any(Operation.class)))
                .thenReturn(Collections.singletonList(operationRequestAndResponse));

        when(dataModelResolver.resolve(any(Schema.class)))
                .thenAnswer((Answer<DataModel>) invocationOnMock -> {
                    ObjectSchema objectSchema = invocationOnMock.getArgument(0);
                    return DataModel.newObjectType(objectSchema, objectSchema.getName());
                });

        List<RequestsHandlerMethod> methods = resolver.resolve(new HttpOperation("/test", "GET", operation), methodNameResolver);

        assertEquals(1, methods.size());
        RequestsHandlerMethod requestsHandlerMethod = methods.get(0);
        assertEquals("test", requestsHandlerMethod.getMethodName());
        assertEquals("GET", requestsHandlerMethod.getHttpMethod());
        assertEquals("application/xml", requestsHandlerMethod.getResponseBody().getMediaType());
        assertEquals("application/json", requestsHandlerMethod.getRequestBody().getMediaType());
        assertEquals("TestRequest", requestsHandlerMethod.getRequestBody().getContent().getTypeName());
        assertEquals("TestResponse", requestsHandlerMethod.getResponseBody().getContent().getTypeName());
    }

    private static final ParameterNameResolver PARAMETER_NAME_RESOLVER = new ParameterNameResolver() {
        @Nonnull
        @Override
        public String resolveParameterName(@Nonnull Schema<?> parameterSchema, @Nonnull String apiParameterName) {
            return apiParameterName;
        }
    };
}