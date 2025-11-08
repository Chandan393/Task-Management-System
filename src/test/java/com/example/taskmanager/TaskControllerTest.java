package com.example.taskmanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void createGetUpdateDeleteFlow() throws Exception {
        String due = LocalDate.now().plusDays(2).toString();

        // Create Task payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "My Test Task");
        payload.put("description", "Testing controller");
        payload.put("status", ""); // empty string to test default PENDING
        payload.put("dueDate", due);

        // Create Task
        String response = mvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING")) // default applied
                .andReturn().getResponse().getContentAsString();

        String id = mapper.readTree(response).get("id").asText();

        // Get Task by ID
        mvc.perform(get("/tasks/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Update Task
        Map<String, Object> update = new HashMap<>();
        update.put("title", "Updated Task Title");
        update.put("description", "Updated Desc");

        mvc.perform(put("/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title"))
                .andExpect(jsonPath("$.description").value("Updated Desc"));

        // List Tasks
        mvc.perform(get("/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(id));

        // Delete Task
        mvc.perform(delete("/tasks/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify Deletion
        mvc.perform(get("/tasks/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
