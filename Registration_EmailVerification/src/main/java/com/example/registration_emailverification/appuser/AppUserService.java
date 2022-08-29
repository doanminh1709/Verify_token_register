package com.example.registration_emailverification.appuser;

import com.example.registration_emailverification.repository.AppUserRepo;
import com.example.registration_emailverification.repository.ConfirmationTokenRepository;
import com.example.registration_emailverification.resistration.token.ConfirmationToken;
import com.example.registration_emailverification.resistration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepo appUserRepo;
    private final static String USER_NOT_FOUND = "user with email %s not found";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepo.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
    }

    //Register user return link user confirm
    @Transactional
    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepo
                .findByEmail(appUser.getEmail())
                .isPresent();
        if (userExists && appUser.getEnabled()) {
            //TODO check of attributes are the same and
            //TODO if email not confirm send confirmation email
            throw new IllegalStateException("Email already taken");
        } else if (userExists && !appUser.getEnabled()) {
            confirmationTokenRepository.deleteConfirmation(appUser.getId());
        } else if (!(userExists && appUser.getEnabled()))
            appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
        //  TODO : Send confirmation token
        appUserRepo.save(appUser);
        String token = UUID.randomUUID().toString();
        //Send confirmation token
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        //TODO : SEND EMAIL
        return token;
    }


    public int enableAppUser(String email) {
        return appUserRepo.enableAppUser(email);
    }
}
