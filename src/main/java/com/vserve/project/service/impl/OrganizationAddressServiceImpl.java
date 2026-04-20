package com.vserve.project.service.impl;

import com.vserve.project.dto.organization.OrganizationAddressRequestDto;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.OrganizationAddress;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OrganizationAddressRepository;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.service.OrganizationAddressService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
public class OrganizationAddressServiceImpl implements OrganizationAddressService {
    final OrganizationAddressRepository organizationAddressRepository;
    final OrganizationRepository organizationRepository;

    public OrganizationAddressServiceImpl(OrganizationAddressRepository organizationAddressRepository, OrganizationRepository organizationRepository) {
        this.organizationAddressRepository = organizationAddressRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public String registerAddress(OrganizationAddressRequestDto dto){
        Organization organization = organizationRepository.findById(dto.organization_Id()).orElse(null);
        if(organization == null){
            throw new ResourceAccessException("Organization not found");
        }
        OrganizationAddress organizationAddress=mapToOrganizationAddress(dto);
        organizationAddress.setOrganization(organization);
        organizationAddressRepository.save(organizationAddress);

//        organization.setStatus(AccountStatus.ACTIVE);
//        organizationRepository.save(organization);

        return ("Organization Address Registered Successfully");
    }

    @Override
    public String updateOrganizationAddress( OrganizationAddressRequestDto dto){
        OrganizationAddress existingAddress =
                organizationAddressRepository.findByOrganizationId(dto.organization_Id());

        if (existingAddress == null) {
            throw new BusinessException("Organization address doesn't exist");
        }

        updateFromDto(existingAddress, dto);

        organizationAddressRepository.save(existingAddress);

        return "Organization address updated successfully";
    }

    @Override
    public OrganizationAddressRequestDto getOrganizationDetails(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId).orElse(null);
        if(organization==null)
            throw new BusinessException("Organization Id not Found");

        OrganizationAddress address = organizationAddressRepository.findByOrganizationId(organizationId);

        if (address == null) {
            throw new BusinessException("Address is not present"); // frontend will show form
        }
         return mapToGetOrganizationAddres(address);

    }

    private OrganizationAddressRequestDto mapToGetOrganizationAddres(OrganizationAddress address) {
        return new OrganizationAddressRequestDto(
                address.getOrganization().getId(),
                address.getState(),
                address.getCity()
        );
    }

    private void updateFromDto(OrganizationAddress existingAddress,
                               OrganizationAddressRequestDto dto) {

        if (dto.state() != null)
            existingAddress.setState(dto.state());


        if (dto.city() != null)
            existingAddress.setCity(dto.city());


    }



    private OrganizationAddress mapToOrganizationAddress(OrganizationAddressRequestDto dto) {
        OrganizationAddress orgAddress =new OrganizationAddress();
        orgAddress.setState(dto.state());
        orgAddress.setCity(dto.city());

        return orgAddress;
    }


}
