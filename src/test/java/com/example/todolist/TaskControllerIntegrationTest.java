package com.example.todolist;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        taskRepository.deleteAll();
    }

    @Test
    public void shouldCreateTask() throws Exception {
        String taskJson = "{\"description\":\"Task 1\", \"completed\":false}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Task 1"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    public void shouldGetAllTasks() throws Exception {
        Task task1 = new Task();
        task1.setDescription("Task 1");
        task1.setCompleted(false);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setDescription("Task 2");
        task2.setCompleted(true);
        taskRepository.save(task2);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("Task 1")))
                .andExpect(jsonPath("$[1].description", is("Task 2")));
    }

    @Test
    public void shouldGetTaskById() throws Exception {
        Task task = new Task();
        task.setDescription("Task 1");
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        mockMvc.perform(get("/tasks/" + savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTask.getId()))
                .andExpect(jsonPath("$.description").value("Task 1"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    public void shouldUpdateTask() throws Exception {
        Task task = new Task();
        task.setDescription("Task 1");
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        String updatedTaskJson = "{\"description\":\"Updated Task\", \"completed\":true}";

        mockMvc.perform(put("/tasks/" + savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTask.getId()))
                .andExpect(jsonPath("$.description").value("Updated Task"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    public void shouldDeleteTask() throws Exception {
        Task task = new Task();
        task.setDescription("Task 1");
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/" + savedTask.getId()))
                .andExpect(status().isOk());

        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());
        assertTrue(deletedTask.isEmpty());
    }
}
