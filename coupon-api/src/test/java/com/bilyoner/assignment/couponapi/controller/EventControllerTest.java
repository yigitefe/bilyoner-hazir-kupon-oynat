package com.bilyoner.assignment.couponapi.controller;


import com.bilyoner.assignment.couponapi.model.EventDTO;
import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import com.bilyoner.assignment.couponapi.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    private EventController eventController;

    @BeforeEach
    public void setUp() {
        eventController = new EventController(eventService);
    }

    @Test
    @DisplayName("Should list all events")
    public void shouldListAllEvents() {
        EventDTO e1 = EventDTO.builder()
                .id(1L)
                .name("Afyon-Gaziantep")
                .mbs(1)
                .type(EventTypeEnum.BASKETBALL)
                .eventDate(LocalDateTime.now())
                .build();
        EventDTO e2 = EventDTO.builder()
                .id(2L)
                .name("Trabzonspor-Konyaspor")
                .mbs(1)
                .type(EventTypeEnum.FOOTBALL)
                .eventDate(LocalDateTime.now())
                .build();
        List<EventDTO> allEvents = Arrays.asList(e1, e2);

        when(eventService.getAllEvents()).thenReturn(allEvents);

        List<EventDTO> returnedEvents = eventController.getAllEvents();

        assertIterableEquals(allEvents, returnedEvents);
    }

}
