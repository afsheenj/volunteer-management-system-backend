package com.vserve.project.scheduler;

import com.vserve.project.entity.OrganizationParticipation;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.enums.RequestStatus;
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
    @Scheduled(fixedRate = 60000)
    public void changeStatus() {
        try {

            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();

            // STEP 1: HANDLE START TIME LOGIC
            List<ServiceRequest> startingServices =
                    serviceRequestRepository.findServicesToStart(today, now);

            for (ServiceRequest service : startingServices) {

                if (service.getRegisteredCount() >= service.getMinVolunteers()) {

                    //  Enough volunteers → IN_PROGRESS
                    service.setStatus(RequestStatus.IN_PROGRESS);
                    organizationParticipationRepository.RejectRequestedParticipants(service.getId());
                    userParticipationRepository.RejectRequestedParticipants(service.getId());

                } else {

                    //  Not enough → NOT_FILLED
                    service.setStatus(RequestStatus.NOT_FILLED);

                    // cancel all participants
                    userParticipationRepository.cancelByServiceId(service.getId());
                    organizationParticipationRepository.cancelByServiceId(service.getId());
                }

                serviceRequestRepository.save(service);
            }


            //  STEP 2: HANDLE END TIME LOGIC
            List<ServiceRequest> completedServices =
                    serviceRequestRepository.findServicesToComplete(today, now);

            for (ServiceRequest service : completedServices) {
                service.setStatus(RequestStatus.COMPLETED);
                serviceRequestRepository.save(service);
            }

        } catch (Exception e) {
            System.out.println("Scheduled task failed: " + e.getMessage());
        }
    }
}
