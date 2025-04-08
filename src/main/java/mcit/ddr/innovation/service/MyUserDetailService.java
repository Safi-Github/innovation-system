package mcit.ddr.innovation.service;

import mcit.ddr.innovation.dto.ResetPasswordOTPRequest;
import mcit.ddr.innovation.dto.ResetPasswordRequest;
import mcit.ddr.innovation.entity.ForgotPassword;
import mcit.ddr.innovation.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.repository.MyUserRepository;

@Slf4j
@Service
//connecting the spring security to user table
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private MyUserRepository repository;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("This is an INFO level log message",username);
        Optional<MyUser> user = repository.findByUsername(username);

        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(userObj.getRole().name())))
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    private String getRoles(MyUser user) {
        if (user.getRole() == null) {
            // return new String[]{"USER"};
        }
        return user.getRole().name();
    }
}