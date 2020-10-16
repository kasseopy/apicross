package apicross.java;

import org.junit.Assert;
import org.junit.Test;

public class DefaultMethodNameBuilderTest {
    @Test
    public void whenNoOperationPayloads_thenMethodNameIsOperationId() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .build();
        Assert.assertEquals("getResource", methodName);
    }

    @Test
    public void whenOperationProducesContent_thenMethodNameIsOperationIdAndProduceMadiaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType("text/plain")
                .build();
        Assert.assertEquals("getResourceProducePlainText", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType("application/vnd.content.v1+json")
                .build();
        Assert.assertEquals("getResourceProduceVndContentV1Json", methodName);
    }

    @Test
    public void whenOperationConsumesContent_thenMethodNameIsOperationIdAndConsumeMediaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .consumingsMediaType("text/plain")
                .build();
        Assert.assertEquals("getResourceConsumePlainText", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("application/vnd.content.v1+json")
                .build();
        Assert.assertEquals("putResourceConsumeVndContentV1Json", methodName);
    }

    @Test
    public void whenOperationConsumesAndProducesContent_thenMethodNameIsOperationIdAndConsumeProduceMadiaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("search")
                .consumingsMediaType("application/x.query.v1+json")
                .producingMediaType("application/x.content.v2+json")
                .build();
        Assert.assertEquals("searchConsumeXQueryV1JsonProduceXContentV2Json", methodName);
    }

    @Test
    public void masksIsNotAllowedInProducingMediaType() {
        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("application/*")
                    .build();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }

        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("*/json")
                    .build();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }

        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("*/*")
                    .build();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }
    }

    @Test
    public void masksIsAllowedInConsumingMediaType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("application/*")
                .build();
        Assert.assertEquals("putResourceConsumeApplicationAnySubType", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("*/json")
                .producingMediaType("application/json")
                .build();
        Assert.assertEquals("putResourceConsumeAnyTypeJsonProduceJson", methodName);

        methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("*/*")
                .producingMediaType("application/json")
                .build();
        Assert.assertEquals("putResourceProduceJson", methodName);
    }
}
