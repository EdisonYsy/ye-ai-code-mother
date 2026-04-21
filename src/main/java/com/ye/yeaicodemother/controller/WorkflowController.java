package com.ye.yeaicodemother.controller;

import com.ye.yeaicodemother.langgraph4j.CodeGenWorkflow;
import com.ye.yeaicodemother.langgraph4j.state.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/workflow")
@Slf4j
public class WorkflowController {

    @PostMapping("/execute")
    public WorkflowContext executeWorkflow(@RequestParam String prompt){
        log.info("收到同步工作流执行请求:{}",prompt);
        return new CodeGenWorkflow().executeWorkflow(prompt);

    }

    @PostMapping("/execute-flux")
    public Flux<String> executeWorkflowWithFlux(@RequestParam String prompt){
        log.info("收到Flux工作流执行请求:{}",prompt);
        return new CodeGenWorkflow().executeWorkflowWithFlux(prompt);

    }

    /**
     * SSE 流式执行工作流
     */
    @GetMapping(value = "/execute-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter executeWorkflowWithSse(@RequestParam String prompt) {
        log.info("收到 SSE 工作流执行请求: {}", prompt);
        return new CodeGenWorkflow().executeWorkflowWithSse(prompt);
    }
}
