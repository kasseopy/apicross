package apicross.java;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DefaultMethodNameBuilderTest {
    @Test
    public void maskIsNotAllowedInProduces() {
        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("application/*")
                    .build();
            fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }

        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("*/json")
                    .build();
            fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }

        try {
            new DefaultMethodNameBuilder()
                    .operationId("getResource")
                    .producingMediaType("*/*")
                    .build();
            fail();
        } catch (IllegalArgumentException e) {
            // it's ok
        }
    }

    @Test
    public void worksWhenPayloadTypesNotDefined() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .build();
        assertEquals("getResource", methodName);
    }
    @Test
    public void worksForTextPlainRequests() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType("text/plain")
                .build();
        assertEquals("getResourceProducePlainText", methodName);
    }

    @Test
    public void worksForTextPlainResponses() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .consumingsMediaType("text/plain")
                .build();
        assertEquals("getResourceConsumePlainText", methodName);
    }

    @Test
    // operationId: getResource, Accept: application/vnd.content.v1+json
    public void worksForResponseContentTypeWithParts() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType("application/vnd.content.v1+json")
                .build();
        assertEquals("getResourceProduceVndContentV1Json", methodName);
    }

    @Test
    // operationId: getResource, Accept: application/xml
    public void worksForResponseContentTypeWithoutParts() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("getResource")
                .producingMediaType("application/xml")
                .build();
        assertEquals("getResourceProduceXml", methodName);
    }

    @Test
    // operationId: putResource, consume: application/vnd-content-v1+json
    public void worksForRequestContentTypeWithParts() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("application/vnd.content.v1+json")
                .build();
        assertEquals("putResourceConsumeVndContentV1Json", methodName);
    }

    @Test
    // operationId: putResource, consume: application/xml
    public void worksForRequestContentTypeWithoutParts() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("application/xml")
                .build();
        assertEquals("putResourceConsumeXml", methodName);
    }

    @Test
    // operationId: search, consume: application/x.query.v1+json, Accept: application/x.content.v1+json
    public void worksForRequestResponseContentType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("search")
                .consumingsMediaType("application/x.query.v1+json")
                .producingMediaType("application/x.content.v1+json")
                .build();
        assertEquals("searchConsumeXQueryV1JsonProduceXContentV1Json", methodName);
    }

    @Test
    // operationId: putResource, consume: application/*
    public void worksWithMaskedRequestType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("application/*")
                .build();
        assertEquals("putResourceConsumeApplicationAnySubType", methodName);
    }

    @Test
    // operationId: putResource, consume: */json, produce: application/*
    public void worksForMaskedResponseContentTypeAndMaskedRequestType() {
        String methodName = new DefaultMethodNameBuilder()
                .operationId("putResource")
                .consumingsMediaType("*/json")
                .producingMediaType("application/json")
                .build();
        assertEquals("putResourceConsumeAnyTypeJsonProduceJson", methodName);
    }
}
