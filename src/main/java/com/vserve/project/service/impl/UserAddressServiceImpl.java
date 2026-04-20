package com.vserve.project.service.impl;

import com.vserve.project.dto.user.UserAddressDto;
import com.vserve.project.entity.User;
import com.vserve.project.entity.UserAddress;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.UserAddressRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.UserAddressService;
import org.springframework.stereotype.Service;


@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    public UserAddressServiceImpl(UserRepository userRepository, UserAddressRepository userAddressRepository) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public String userAddress(UserAddressDto dto){
        User existingUser = userRepository.findById(dto.userId()).orElse(null);
        if (existingUser==null)
            throw new BusinessException("User doesn't exist");

        if (userAddressRepository.existsByUser(existingUser)) {
            throw new BusinessException("User address already exists");
        }

        UserAddress address = mapToUserAddress(existingUser, dto);
        userAddressRepository.save(address);

//        existingUser.setAddressVerified(true);
//        userRepository.save(existingUser);
        return "User address was added successfully";
    }


    @Override
    public UserAddressDto getUserAddress(Long userId) {

        UserAddress address = userAddressRepository
                .findByUserId(userId);
        if(address==null)
            throw new BusinessException("Address doesn't exist");

        return new UserAddressDto(
                address.getUser().getId(),
                address.getState(),
                address.getDistrict(),
                address.getPincode(),
                address.getCity(),
                address.getStreet(),
                address.getDoorNo()
        );
    }

    @Override
    public String updateUserAddress(UserAddressDto dto) {
        UserAddress existingAddress = userAddressRepository.findByUserId(dto.userId());

        if (existingAddress == null) {
            // Address does not exist, create new
            User user = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new BusinessException("User doesn't exist"));

            UserAddress newAddress = mapToUserAddress(user, dto);
            userAddressRepository.save(newAddress);

            return "User address created successfully";
        }

        updateUserAddressFromDto(existingAddress, dto);
        userAddressRepository.save(existingAddress);

        return "User address updated successfully";
    }

    private void updateUserAddressFromDto(UserAddress existingAddress, UserAddressDto dto) {

        existingAddress.setState(dto.state());
        existingAddress.setDistrict(dto.district());
        existingAddress.setCity(dto.city());
        existingAddress.setStreet(dto.street());
        existingAddress.setDoorNo(dto.doorNo());
        existingAddress.setPincode(dto.pincode());

    }


    public UserAddress mapToUserAddress(User existingUser, UserAddressDto dto){

        UserAddress userAddress = new UserAddress();
        userAddress.setUser(existingUser);
        userAddress.setCity(dto.city());
        userAddress.setDistrict(dto.district());
        userAddress.setPincode(dto.pincode());
        userAddress.setDoorNo(dto.doorNo());
        userAddress.setState(dto.state());
        userAddress.setStreet(dto.street());
        return userAddress;
    }
}
