package com.livingalone.springboot.domain.profile.valid.exception;

import jakarta.persistence.EntityNotFoundException;

public class ProfileNotFoundException extends EntityNotFoundException {
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
