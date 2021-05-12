[![Build Status](https://travis-ci.com/kasseopy/apicross.svg?branch=master)](https://travis-ci.com/kasseopy/apicross)

# Purpose
APICROSS is a tool to generate source code from OpenAPI 3.0 API specification.

# Features
- Generates API Models
- Generates API Requests Handlers (SpringMVC)
- Maven plugin

# Dependencies
* [OpenAPI model](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-models/src/main/java/io/swagger/v3/oas/models)
* Spring MVC
* Jackson JSON
* [JsonNullable Jackson module](https://github.com/OpenAPITools/jackson-databind-nullable)

# Minimal Maven Plugin setup
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>itroadlabs.toolkits</groupId>
                <artifactId>apicross-maven-plugin</artifactId>
                <version>${project.version}</version>
                <executions>
                    <execution>
                        <id>generate-api-classes</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate-code</goal>
                        </goals>
                        <configuration>
                            <specUrl>file:///${project.basedir}/../api-specifications/api.yaml</specUrl>
                            <generatorClassName>apicross.java.SpringMvcCodeGenerator</generatorClassName>
                            <generatorOptions implementation="apicross.java.SpringMvcCodeGeneratorOptions">
                                <apiHandlerPackage>com.myapp.web.handlers</apiHandlerPackage>
                                <apiModelPackage>com.myapp.web.models</apiModelPackage>
                                <writeSourcesTo>${project.build.directory}/generated-sources/java</writeSourcesTo>
                            </generatorOptions>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>itroadlabs.toolkits</groupId>
                        <artifactId>apicross-springmvc</artifactId>
                        <version>${project.version}</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```

# OpenAPI Specification processing features
## Optional fields in data model
By default, OpenAPI specification states all model fields are not nullable (`nullable: false`). 
Required fields are fields must be present in a valid JSON  document representing API data models (request/response payloads).
So field may be not required to be in JSON document, but it's value must not be null. For example, consider following schema
```yaml
MyModel:
    type: object
    properties:
        a: string
```
Then JSON document like
```json
{
  "a": null
}
```
is not vallid, but following JSON document is valid:
```json
{}
```
And following JSON is valid:
```json
{
  "a": "abc"
}
```

Optional fields (which are not required) become `JsonNullable<Type>` fields for internal data model and Jackson serialization/deserialization.
For example, for the schema above generated Java class will be:
```java
public class MyModel {
    private JsonNullable<String> a = JsonNullable.undefined();

    @JsonIgnore
    public String getA() throws NoSuchElementException {
        return this.a.get();
    }

    @JsonGetter("a")
    protected JsonNullable<String> aJson() {
        return this.a;
    }

    @JsonSetter("a")
    public void setA(String a) {
        this.a = JsonNullable.of(a);
    }
}
```

It is possible to disable `JsonNullable` usage by maven plugin configuration option:
```xml
    <generatorOptions implementation="apicross.java.SpringMvcCodeGeneratorOptions">
        ...
        <useJsonNullable>false</useJsonNullable>
        ...
    </generatorOptions>
```

## Min/Max/Required properties validation
Validating such features within java code is tricky because JSON and Java are different.
OpenAPI specification states:
- minProperties - minimum number of fields must present in JSON document,
- maxProperties - maximum number of fields must present in JSON document,
- required - such fields must present in JSON document (it doesn't matter what value fields have, nulls or not).

After JSON deserialization into Java object there is no information about field's presence in the JSON document.
APICROSS by default represents optional schema fields as `JsonNullable`. 
To validate such data models there is a simple JSR-380 toolkit. 
Take a look at the `apicross.beanvalidation.*` classes within `apicross-support` module.

Here is an example of generated code below:
```java
@MinProperties(
        value = 2,
        groups = {BeanPropertiesValidationGroup.class})
@MaxProperties(
        value = 3,
        groups = {BeanPropertiesValidationGroup.class})
@RequiredProperties(
        value = {"a"},
        groups = {BeanPropertiesValidationGroup.class})
public class MyModel implements HasPopulatedProperties {
    private final Set<String> $populatedProperties = new HashSet<>();

    private JsonNullable<String> a = JsonNullable.undefined();
    private JsonNullable<String> b = JsonNullable.undefined();
    private JsonNullable<String> c = JsonNullable.undefined();
    private JsonNullable<String> d = JsonNullable.undefined();
    ...

    @JsonSetter("a")
    public void setA(String a) {
        this.$populatedProperties.add("a");
        this.a = JsonNullable.of(a);
    }
    
    ...
    
    @Override
    public Set<String> get$populatedProperties() {
        return Collections.unmodifiableSet(this.$populatedProperties);
    }
}
```
Validation group above can be used for validation sequence needs.

To enable/disable this feature use following configuration option:
```xml
    <generatorOptions implementation="apicross.java.SpringMvcCodeGeneratorOptions">
        ...
        <enableApicrossJavaBeanValidationSupport>true</enableApicrossJavaBeanValidationSupport>
        ...
    </generatorOptions>
```
By default this feature is enabled.

## Additional properties
Take a look at following schema:
```yaml
MyModel:
    type: object
    additionalProperties:
      type: string
```
Java class representing that model will be:
```java
public class MyModel {
    private final Map<String, String> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> getAdditionalProperties() {
        return this.additionalProperties;
    }
    
    @JsonAnySetter
    public void setAdditionalProperty(String name, String value) {
        this.additionalProperties.put(name, value);
    }
}
```
## IRead* interfaces feature
This feature is for Hexagonal architecture purists.

Generated java classes for API models (mainly request/response models) actually belong to adapters. 
It is not possible to use such models inside application "core", because adapters can use ports from application "core", 
but not vise-versa.

With APICROSS it is possible to generate interface (a-la `IRead*` interface) for API models, 
and locate these interfaces inside application "core" packages. Take a look at example:
```java
package com.myapp.ports.adapters.web;

...
@javax.annotation.Generated(value = "apicross.java.SpringMvcCodeGenerator")
public class CreateMyResourceRepresentation implements IReadCreateMyResourceRepresentation {
    ...
}
```
```java
package com.myapp.application;
...
@javax.annotation.Generated(value = "apicross.java.SpringMvcCodeGenerator")
public interface IReadCreateMyResourceRepresentation {
    String getSomeProperty();
    ...
}
```
```java
package com.myapp.ports.adapters.web;
...
@javax.annotation.Generated(value = "apicross.java.SpringMvcCodeGenerator")
public interface MyApiHandler {
    @RequestMapping(path = "/my-resource", method = RequestMethod.POST, consumes = "application/json")
    ResponseEntity<?> createMyResource(@RequestBody(required = true) CreateMyResourceRepresentation model,
                             @RequestHeader HttpHeaders headers) throws Exception;
}
```

```java
package com.myapp.ports.adapters.web;
...
@RestController
public class MyApiHandlerController implements MyApyHandler {
    private final MyService myService;
    ...
    @Override
    public ResponseEntity<?> createMyResource(CreateMyResourceRepresentation model, HttpHeaders headers) throws Exception {
        myService.create(model);
        return ResponseEntity.status(204).build();
    }
}
```

```java
package com.myapp.application;
...
@Service
@Validated
public class MyService {
    public void create(@Valid IReadCreateMyResourceRepresentation model) {
        ...
    }
}
```

To enable this feature use following configuration:
```xml
    <generatorOptions implementation="apicross.java.SpringMvcCodeGeneratorOptions">
        ...
        <apiHandlerPackage>com.myapp.ports.adapters.web</apiHandlerPackage>
        <apiModelPackage>com.myapp.ports.adapters.web</apiModelPackage>
        <enableDataModelReadInterfaces>true</enableDataModelReadInterfaces>
        <apiModelReadInterfacesPackage>com.myapp.application</apiModelReadInterfacesPackage>
        ...
    </generatorOptions>
```

## API handler
API Handler is an object handling API requests. For SpringWebMVC - handlers are `@Controller`s.
APICROSS generated Java interfaces with spring MVC metadata. So application's `@Controller`s have to implement these interfaces.
Consider API specification fragment bellow:
```yaml
  '/my':
    post:
      operationId: create
      tags:
        - MyApi
      requestBody:
        required: true
        content:
          'application/json':
            schema:
              $ref: '#/components/schemas/CreateMyModelRepresentation'
      responses:
        '201':
          description: Success. Resource created.
  '/my/{id}':
    get:
      operationId: get
      tags:
        - MyApi
      parameters:
        - $ref: '#/components/parameters/IdPathParameter'
      responses:
        '200':
          description: Success. Resource representation in the response body.
          content:
            'application/json':
              schema:
                $ref: '#/components/schemas/MyModelRepresentation'          
          
```
Generated interface will looks like:
```java
public interface MyApiHandler  {
    @RequestMapping(path = "/my", method = RequestMethod.POST, consumes = "application/json")
    ResponseEntity<Void> create(@RequestBody(required = true) CreateMyModelRepresentation model, 
                                                        @RequestHeader HttpHeaders headers) throws Exception;

    @RequestMapping(path = "/my/{id}", method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<MyModelRepresentation> get(@PathVariable("id") String id, 
                                                        @RequestHeader HttpHeaders headers) throws Exception;
}
```

and Spring MVC controller (manually coded):
```java
@RestController
public class MyApiHandlerController implements MyApiHandler {
    private final MyAppService myAppService;
    
    @Autowired
    MyApiHandlerController(MyAppService myAppService) {
        this.myAppService = myAppService;
    }

    @Override
    public ResponseEntity<Void> create(CreateMyModelRepresentation model, HttpHeaders headers) {
        CreateMyModelResult result = myAppService.create(new CreateModelCommand(model));
        return created(result);
    }

    @Override
    public ResponseEntity<MyModelRepresentation> get(String id, HttpHeaders headers) {
        ModelObject model = myAppService.get(id);
        return ResponseEntity.ok(MyModelRepresentationFactory.create(model));
    }
}
```
### Spring Security Authentication
When API operation definition contains security option, then it is possible to add `Authentication` parameter to the 
API Handler interface. For example, take a look at following specification:
```yaml
  '/my-resource':
    patch:
      operationId: updateMyResource
      requestBody:
        required: true
        content:
          'application/json':
            schema:
              $ref: '#/components/schemas/MyModel'
      security:
        - Basic: [ ]
```
Then API Handler interface will look like:
```java
  ...
  @RequestMapping(
      method = RequestMethod.PATCH,
      path = "/my-resource",
      consumes = "application/json")
  ResponseEntity<?> updateMyResource(
      @RequestHeader HttpHeaders headers,
      @CurrentSecurityContext(expression = "authentication") Authentication authentication,
      @RequestBody(required = true) MyModel requestBody) throws Exception;
```
To enable this option use following configuration option:
```xml
    <generatorOptions implementation="apicross.java.SpringMvcCodeGeneratorOptions">
        ...
        <enableSpringSecurityAuthPrincipal>true</enableSpringSecurityAuthPrincipal>
        ...
    </generatorOptions>
```