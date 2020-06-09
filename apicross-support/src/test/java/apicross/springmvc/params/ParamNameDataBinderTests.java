package apicross.springmvc.params;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TestController.class)
public class ParamNameDataBinderTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void simpleParameterBindingWorks() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test?param-1=123"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"param1\":\"123\"}"));
    }

    @Test
    public void stdParameterBindingWorks() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test?param1=123"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"param1\":\"123\"}"));
    }

    @Test
    public void arrayParameterBindingWorks() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test?param-2=123,456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"param2\":[123,456]}"));
    }
}
