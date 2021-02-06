package apicross.java;

import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultJavaPropertyAndParameterNameResolverTest {
    private DefaultJavaPropertyAndParameterNameResolver resolver;

    @Before
    public void setup() {
        resolver = new DefaultJavaPropertyAndParameterNameResolver();
    }

    @Test
    public void validJavaIdentifiersResolvedForProperties() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolvePropertyName(stringProperty, "myProperty123");
        assertThat(resolvePropertyName, is("myProperty123"));
    }

    @Test
    public void underscoreResolvedForProperties() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolvePropertyName(stringProperty, "my_property_123");
        assertThat(resolvePropertyName, is("myProperty123"));
    }

    @Test
    public void noneJavaIdentifiersResolvedForProperties() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolvePropertyName(stringProperty, "123ab4~`!@#$%^&*()_+:;'\"|\\/<>");
        assertThat(resolvePropertyName, is("ab4$"));
    }

    @Test
    public void validJavaIdentifiersResolvedForParameters() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolveParameterName(stringProperty, "myProperty123");
        assertThat(resolvePropertyName, is("myProperty123"));
    }

    @Test
    public void underscoreResolvedForParameters() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolveParameterName(stringProperty, "my_property_123");
        assertThat(resolvePropertyName, is("myProperty123"));
    }

    @Test
    public void noneJavaIdentifiersResolvedForParameters() {
        StringSchema stringProperty = new StringSchema();
        String resolvePropertyName = resolver.resolveParameterName(stringProperty, "123ab4~`!@#$%^&*()_+:;'\"|\\/<>");
        assertThat(resolvePropertyName, is("ab4$"));
    }
}