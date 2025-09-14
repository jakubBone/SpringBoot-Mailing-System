package com.jakubbone.unit;

import com.jakubbone.repository.MessageRepository;
import com.jakubbone.service.KeycloakUserService;
import com.jakubbone.service.MessageService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @Mock
    MessageRepository messageRepository;

    @Mock
    KeycloakUserService keycloakUserService;

    @InjectMocks
    MessageService messageService;
}
