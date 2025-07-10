package com.banco.bancodemo.services.impl;

import com.banco.bancodemo.entity.TitularCuenta;
import com.banco.bancodemo.repository.TitularCuentaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TitularCuentaRepository titularRepository;

    public CustomUserDetailsService(TitularCuentaRepository titularRepository) {
        this.titularRepository = titularRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        TitularCuenta titular = titularRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return User.builder()
                .username(titular.getEmail())
                .password(titular.getPassword())
                .roles("USER") // O los roles que necesites
                .build();
    }
}
