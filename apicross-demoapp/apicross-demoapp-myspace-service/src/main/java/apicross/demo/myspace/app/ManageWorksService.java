package apicross.demo.myspace.app;

import apicross.demo.common.utils.ValidationStages;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated({ValidationStages.class})
public class ManageWorksService {
}
