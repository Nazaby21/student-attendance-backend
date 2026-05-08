package com.example.demo.security;

import com.example.demo.modal.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final com.example.demo.repository.ClassEntityRepository classRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        Long classId = null;
        if (user.getRole() == com.example.demo.enumeration.Role.TEACHER) {
            classId = classRepository.findByTeachersId(user.getId()).stream()
                    .findFirst()
                    .map(com.example.demo.modal.ClassEntity::getId)
                    .orElse(null);
        }

        return UserDetailsImpl.build(user, classId);
    }
}
