package com.vserve.project.controller;

import com.vserve.project.dto.common.HomeContactDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.CommonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class CommonController {

    private CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/home/contact")
    public ApiResponse<String> contactMessage(@RequestBody HomeContactDto homeContactDto) {
        return ApiResponse.ok("Message sent successfully", commonService.sendContactMessage(homeContactDto));
    }

}
