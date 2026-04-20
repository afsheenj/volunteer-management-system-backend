package com.vserve.project.controller;

import com.vserve.project.service.DummyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@Tag(name = "Dummy", description = "dummy description")
public class DummyController {

    private final DummyService dummyService;

    public DummyController(DummyService dummyService) {
        this.dummyService = dummyService;
    }

    @Operation(description = "it is a sample mail sending API")
    @GetMapping("/sample")
    public void sendSampleMail(@RequestParam String mail) {
        dummyService.sampleMail(mail);
    }

    @PostMapping("/otp")
    public void sendOtp(@RequestParam String mail) {
        dummyService.sendOtp(mail);
    }

    @PostMapping("/otp/verify")
    public String verifyOtp(@RequestParam String mail,
                          @RequestParam String otp) {
        return dummyService.verifyOtp(mail, otp);
    }

}
