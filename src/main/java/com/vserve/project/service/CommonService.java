package com.vserve.project.service;

import com.vserve.project.dto.common.HomeContactDto;

public interface CommonService {
    String sendContactMessage(HomeContactDto homeContactDto);
}
