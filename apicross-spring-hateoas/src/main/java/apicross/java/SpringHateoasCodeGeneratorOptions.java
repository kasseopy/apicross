package apicross.java;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SpringHateoasCodeGeneratorOptions extends JavaCodeGeneratorOptions {
    private boolean enableApicrossJavaBeanValidationSupport;
    private Set<String> entityModels = new HashSet<>();
    private Set<String> collectionModels = new HashSet<>();
    private Set<String> pagedModels = new HashSet<>();
}
