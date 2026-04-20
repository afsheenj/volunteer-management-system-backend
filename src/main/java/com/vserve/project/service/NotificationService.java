package com.vserve.project.service;

import com.vserve.project.entity.ServiceRequest;

public interface NotificationService {
    public void sendNotificationEmails(ServiceRequest request);
}
