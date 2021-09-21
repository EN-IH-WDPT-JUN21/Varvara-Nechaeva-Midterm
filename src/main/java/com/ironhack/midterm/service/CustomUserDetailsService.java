package com.ironhack.midterm.service;

import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.repository.AdminRepository;
import com.ironhack.midterm.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired private AdminRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Admin> user = userRepository.findByName(username);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User not found with username " + username);
    }
    return new CustomUserDetails(user.get());
  }
}
