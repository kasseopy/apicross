package apicross.java;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpringMvcCodeGeneratorOptions extends JavaCodeGeneratorOptions {
    private boolean enableApicrossJavaBeanValidationSupport;
    private boolean enableDataModelReadInterfaces;
    private boolean enableSpringSecurityAuthPrincipal;
    private String apiModelReadInterfacesPackage;
    private List<String> alternativeTemplatesPath;
}
