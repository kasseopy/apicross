package apicross;

import apicross.core.handler.impl.DefaultOperationRequestAndResponseResolverTest;
import apicross.core.handler.impl.DefaultRequestsHandlerMethodsResolverTest;
import apicross.core.handler.impl.OperationFirstTagHttpOperationsGroupsResolverTest;
import apicross.core.data.DataModelResolverHandlesSimpleCasesTests;
import apicross.core.handler.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DefaultOperationRequestAndResponseResolverTest.class,
        DefaultRequestsHandlerMethodsResolverTest.class,
        DataModelResolverHandlesSimpleCasesTests.class,
        OperationFirstTagHttpOperationsGroupsResolverTest.class,
        RequestsHandlersResolverTest.class
})
public class AllTests {
}
