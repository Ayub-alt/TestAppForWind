package com.ayub.testApp.Controller;

import com.ayub.testApp.Service.TestAppService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@EnableAsync
@RestController
@RequestMapping("/tasks")
public class TestAppController {

    TestAppService testAppService;

    public TestAppController(TestAppService testAppService){
        this.testAppService = testAppService;
    }

    @PostMapping("/start")
    public String startTask(@RequestParam int min, @RequestParam int max, @RequestParam int count) {
        return testAppService.startTask(min, max, count).toString();
    }

    @GetMapping("/result/{taskId}")
    public List<Integer> getResult(@PathVariable String taskId) {
        return testAppService.getResult(taskId);
    }
}
