package apicross.demoapp.storefront;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StorefrontMicroserviceTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestToNonExistingResourceGives404() throws Exception {
        mockMvc.perform(get("/unknown"))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void requestToExistingResourceGives200Ok() throws Exception {
        mockMvc.perform(
                get("/sf/competitions")
                        .accept("application/vnd.demoapp.v1+json")
                        .param("ids", "1,2,3"))
                .andDo(print())
                .andExpect(status().is(200));
    }

    @Test
    public void requestWithInvalidBodyGives422() throws Exception {
        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"ClapsVote\",\"notes\":\"Hop!\"}"))
                .andDo(print())
                .andExpect(status().is(422));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"PointsVote\",\"points\":-1}"))
                .andDo(print())
                .andExpect(status().is(422));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"PointsVote\",\"points\":null}"))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void requestWithInvalidQueryParametersGives422() throws Exception {
        mockMvc.perform(get("/sf/competitions")
                .accept("application/vnd.demoapp.v1+json")
                .param("page", "-1"))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void requestWithValidBodyGives204() throws Exception {
        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"ClapsVote\",\"notes\":\"Very good!\"}"))
                .andDo(print())
                .andExpect(status().is(204));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"PointsVote\",\"points\":6}"))
                .andDo(print())
                .andExpect(status().is(204));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"ClapsVote\"}"))
                .andDo(print())
                .andExpect(status().is(204));
    }

    @Test
    public void requestWithMalformedBodyGives400() throws Exception {
        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content(""))
                .andDo(print())
                .andExpect(status().is(400));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("<vote>Super Good!</vote>"))
                .andDo(print())
                .andExpect(status().is(400));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{}"))
                .andDo(print())
                .andExpect(status().is(400));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":null,\"points\":6}"))
                .andDo(print())
                .andExpect(status().is(400));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"Unknown\",\"points\":6}"))
                .andDo(print())
                .andExpect(status().is(400));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":123,\"points\":6}"))
                .andDo(print())
                .andExpect(status().is(400));

        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"VtVoteRequest\",\"points\":6}"))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void requestWithMissedRequiredPropertiesGives422() throws Exception {
        mockMvc.perform(post("/sf/works/{workId}/votes", "1234")
                .contentType("application/vnd.demoapp.v1+json").content("{\"@type\":\"PointsVote\"}"))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void requestWithPropertiesDefinedLessThanMinGives422() {

    }
}
