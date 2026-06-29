package com.social_media.common.exception;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s với định danh '%s' không tồn tại.", entityName, id));
    }
    
    public EntityNotFoundException(String message) {
        super(message);
    }
}
