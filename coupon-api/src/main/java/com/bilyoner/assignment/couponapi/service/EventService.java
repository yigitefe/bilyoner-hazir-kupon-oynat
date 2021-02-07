package com.bilyoner.assignment.couponapi.service;

import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.model.EventDTO;
import com.bilyoner.assignment.couponapi.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventDTO> getAllEvents() {
        return getEvents();
    }

    public List<EventDTO> getEvents() {
        return eventRepository.findAll().stream().map(item -> EventDTO.mapToEventDTO(item))
                .collect(Collectors.toList());
    }

    public List<EventEntity> getEventsById(List<Long> eventIds) {
        return eventRepository.findAllById(eventIds);
    }

    public EventDTO createEvent(EventDTO eventRequest) {
        final EventEntity createdEventEntity = eventRepository.save(EventEntity.builder()
                .name(eventRequest.getName())
                .mbs(eventRequest.getMbs())
                .type(eventRequest.getType())
                .eventDate(eventRequest.getEventDate())
                .build());

        return EventDTO.mapToEventDTO(createdEventEntity);
    }
}
