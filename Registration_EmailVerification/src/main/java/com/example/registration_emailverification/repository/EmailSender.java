package com.example.registration_emailverification.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface EmailSender {
    void send(String to , String email);
}
