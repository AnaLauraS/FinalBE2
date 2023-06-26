package com.dh.msusers.service;

import com.dh.msusers.model.User;
import com.dh.msusers.repository.KeyCloakUserRepository;
import com.dh.msusers.repository.feign.BillsFeignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
        private final KeyCloakUserRepository userRepository;
        private final BillsFeignRepository billsFeignRepository;

        public User getAllBill(String customerId) {
            User usuario = userRepository.findById(customerId);
            usuario.setBills(billsFeignRepository.findByCustomerId(customerId));
            return usuario;
        }
}
