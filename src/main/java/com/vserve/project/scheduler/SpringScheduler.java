package com.vserve.project.scheduler;

import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.repository.OrganizationParticipationRepository;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.repository.UserParticipationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class SpringScheduler {

////    @Scheduled(initialDelay = 10000 * 6, fixedDelay = 5000)
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void sample() {
//        System.out.println("Sample message");
//    }

    private ServiceRequestRepository serviceRequestRepository;
    private UserParticipationRepository userParticipationRepository;
    private OrganizationParticipationRepository organizationParticipationRepository;

    public SpringScheduler(ServiceRequestRepository serviceRequestRepository, UserParticipationRepository userParticipationRepository, OrganizationParticipationRepository organizationParticipationRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userParticipationRepository = userParticipationRepository;
        this.organizationParticipationRepository = organizationParticipationRepository;
    }

    @Transactional
    @Scheduled(fixedRate =  60000)
    public void changeStatus() {
        try {
            serviceRequestRepository.updateActiveServices(LocalDate.now(), LocalTime.now());
            serviceRequestRepository.updateCompleteServices(LocalDate.now(), LocalTime.now());
            userParticipationRepository.changeParticipationStatus();
            organizationParticipationRepository.changeParticipationStatus();
        } catch (Exception e) {
            System.out.println("Scheduled task failed: " + e.getMessage());
        }
    }




}
