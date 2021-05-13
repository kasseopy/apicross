package apicross.java;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpringMvcCodeGeneratorOptions extends JavaCodeGeneratorOptions {
    private boolean enableApicrossJavaBeanValidationSupport = false;
    private boolean enableDataModelReadInterfaces = false;
    private boolean enableSpringSecurityAuthPrincipal = false;
    private boolean useQueryStringParametersObject = true;
    private String apiModelReadInterfacesPackage;
    private List<String> alternativeTemplatesPath;
}
