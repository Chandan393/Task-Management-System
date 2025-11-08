package com.example.taskmanager;

import com.example.taskmanager.model.TaskStatus;
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
// Ensure the in-memory repository is reset after each test method to avoid state leakage
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void createGetUpdateDeleteFlow() throws Exception {
        // Step 1: Prepare request payload
        String due = LocalDate.now().plusDays(2).toString();
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "My Test Task");
        payload.put("description", "Testing controller");
        payload.put("status", TaskStatus.PENDING.name());
        payload.put("dueDate", due);

        // Step 2: Create a new task (POST /tasks)
        String response = mvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andDo(print()) // prints request + response for debugging
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        // Extract id safely using ObjectMapper
        JsonNode node = mapper.readTree(response);
        String id = node.get("id").asText();

        // Step 3: Get task by ID (GET /tasks/{id})
        mvc.perform(get("/tasks/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My Test Task"))
                .andExpect(jsonPath("$.description").value("Testing controller"));

        // Step 4: Update task (PUT /tasks/{id})
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

        // Step 5: List tasks (GET /tasks)
        mvc.perform(get("/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Step 6: Delete the task (DELETE /tasks/{id})
        mvc.perform(delete("/tasks/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Step 7: Verify deletion (GET /tasks/{id} -> 404)
        mvc.perform(get("/tasks/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
