# Purpose
APICROSS is a tool to generate source code from OpenAPI 3.0 API specification.

# Features
- Generates API Models
- Generates API Requests Handlers (SpringMVC, SpringCloud Feign)
- Maven plugin

# Dependencies
* [OpenAPI model](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-models/src/main/java/io/swagger/v3/oas/models)
* Spring MVC
* Spring Cloud Feign
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
                        <id>generate-storefront-api-classes</id>
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
## Optional data model fields
By default OpenAPI specification states all model fields are not nullable (`nullable: false`). 
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
But for public getters `java.utils.Optional` type is used. For example, for the schema above generated Java class will be:
```java
public class MyModel {
    private JsonNullable<String> a = JsonNullable.undefined();

    @JsonIgnore
    public Optional<String> getA() {
        return a.isPresent() ? Optional.of(this.a.get()) : Optional.empty();
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
## Min/Max/Required properties validation
Validating such features within java code is tricky because JSON and Java are different.
OpenAPI specification states:
- minProperties - minimum number of fields must present in JSON document
- maxProperties - maximun number of fields must present in JSON document
- required - such fields must present in JSON document (it doesn't matter what value fields have, nulls or not) 

But after JSON deserialization into Java object there is no information about field's presence in the JSON document.
APICROSS has simple toolkit to handle that. Every generated Java class has a setter to keep populated fields 
into collection. For example:
```java
public class MyModel implements HasPopulatedProperties {
    private final Set<String> $populatedProperties = new HashSet<>();

    private JsonNullable<String> a = JsonNullable.undefined();

    @JsonIgnore
    public Optional<String> getA() {
        return a.isPresent() ? Optional.of(this.a.get()) : Optional.empty();
    }

    @JsonGetter("a")
    protected JsonNullable<String> aJson() {
        return this.a;
    }

    @JsonSetter("a")
    public void setA(String a) {
        this.$populatedProperties.add("a");
        this.a = JsonNullable.of(a);
    }
    
    @Override
    public Set<String> get$populatedProperties() {
        return Collections.unmodifiableSet(this.$populatedProperties);
    }
}
```
So it can be used to perform validation of objects of such class (min/maxProperties/required). 
APICROSS toolkit has JSR380 validators to handle that. Take a look at the `apicross.beanvalidation.*` 
classes within `apicross-support` module.

## API handler
API Handler is an object handling API requests. For SpringWebMVC - handlers are `@Controller`-s.
APICROSS generated Java interfaces with spring MVC metadata. So application's `@Controller`-s have to implement these.
For example, generated interface:
```java
public interface MyApiHandler  {
    @RequestMapping(path = "/my", method = RequestMethod.POST, consumes = "application/json")
    ResponseEntity<Void> create(@RequestBody(required = true) MyModel model, @RequestHeader HttpHeaders headers);

    @RequestMapping(path = "/my/{id}", method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<MyModelRepresentation> get(@PathVariable("id") String id, @RequestHeader HttpHeaders headers);
}
```

and Spring MVC controller (manually developed):
```java
@RestController
public class MyApiHandlerController implements MyApiHandler {
    private final MyAppService myAppService;
    
    @Autowired
    MyApiHandlerController(MyAppService myAppService) {
        this.myAppService = myAppService;
    }

    @Override
    public ResponseEntity<Void> create(@RequestBody MyModel model, HttpHeaders headers) {
        CreateMyModelResult result = myAppService.create(new CreateModelCommand(model));
        return created(result);
    }

    @Override
    public ResponseEntity<MyModelRepresentation> get(String id, HttpHeaders headers) {
        ModelObject model = myAppService.get(id);
        return ResponseEntity.ok(new MyModelRepresentation(model));
    }
}
```
