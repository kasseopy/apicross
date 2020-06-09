package apicross.java;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpringMvcCodeGeneratorOptions extends JavaCodeGeneratorOptions {
    private boolean enableApicrossJavaBeanValidationSupport;
}
