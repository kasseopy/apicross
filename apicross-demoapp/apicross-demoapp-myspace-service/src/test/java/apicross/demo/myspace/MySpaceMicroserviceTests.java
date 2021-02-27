package apicross.demo.myspace;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MySpaceMicroserviceTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestToNonExistingResourceGives404() throws Exception {
        mockMvc.perform(get("/unknown").with(httpBasic("user1", "user1Pass")))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void requestWithValidBodyGives2xx() throws Exception {
        mockMvc.perform(post("/my/competitions").with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":null," +
                        "\"participantReqs\":{\"minAge\": 5}" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(201));
    }

    @Test
    public void requestWithMissedRequiredPropertiesGives422() throws Exception {
        mockMvc.perform(post("/my/competitions").with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":null" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void requestWithPropertiesNumberLessThanMinGives422() throws Exception {
        mockMvc.perform(post("/my/competitions").with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":null," +
                        "\"participantReqs\":{}" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void validRequestsGives201() throws Exception {
        mockMvc.perform(post("/my/competitions").with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                        "ex ea commodo consequat. " +
                        "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore " +
                        "eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
                        "sunt in culpa qui officia deserunt mollit anim id est laborum.\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(header().exists("ETag"));
    }

    @Test
    public void conditionalRequestWithInvalidMatchGives412() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/my/competitions").with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                        "ex ea commodo consequat. " +
                        "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore " +
                        "eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
                        "sunt in culpa qui officia deserunt mollit anim id est laborum.\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andDo(print())
                .andReturn();

        String newResourceLocation = mvcResult.getResponse().getHeader("Location");
        assertNotNull(newResourceLocation);
        String eTag = mvcResult.getResponse().getHeader("ETag");
        assertNotNull(eTag);

        String incorrectEtag = "\"" + RandomStringUtils.randomAlphabetic(20) + "\"";
        mockMvc.perform(patch(newResourceLocation).with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .header("If-Match", incorrectEtag)
                .content("{" +
                        "\"participantReqs\":{\"minAge\":5}" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(412));
    }

    @Test
    public void conditionalRequestWithValidMatchGives204() throws Exception {
        MvcResult registerCompetitionResult = mockMvc.perform(post("/my/competitions").with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .content("{" +
                        "\"title\":\"Demo Competition\"," +
                        "\"votingType\":\"ClapsVoting\"," +
                        "\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                        "ex ea commodo consequat. " +
                        "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore " +
                        "eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
                        "sunt in culpa qui officia deserunt mollit anim id est laborum.\"," +
                        "\"participantReqs\":{\"minAge\":10}" +
                        "}"))
                .andDo(print())
                .andReturn();

        String newCompetitionResourceURI = registerCompetitionResult.getResponse().getHeader("Location");
        assertNotNull(newCompetitionResourceURI);

        String eTag = registerCompetitionResult.getResponse().getHeader("ETag");
        assertNotNull(eTag);

        MvcResult patchCompetitionResult = mockMvc.perform(patch(newCompetitionResourceURI).with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .header("If-Match", eTag)
                .content("{" +
                        "\"participantReqs\":{\"minAge\":5}" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(204))
                .andReturn();

        String eTagAfterPatch = patchCompetitionResult.getResponse().getHeader("ETag");
        assertNotNull(eTag);

        mockMvc.perform(patch(newCompetitionResourceURI).with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .header("If-Match", eTag)
                .content("{" +
                        "\"participantReqs\":{\"minAge\":15}" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(412));

        mockMvc.perform(patch(newCompetitionResourceURI).with(httpBasic("user1", "user1Pass"))
                .contentType("application/vnd.demoapp.v1+json")
                .header("If-Match", eTagAfterPatch)
                .content("{" +
                        "\"participantReqs\":{\"minAge\":15}" +
                        "}"))
                .andDo(print())
                .andExpect(status().is(204));
    }
}
