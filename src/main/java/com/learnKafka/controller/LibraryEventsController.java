package com.learnKafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnKafka.domain.LibraryEvent;
import com.learnKafka.domain.LibraryEventType;
import com.learnKafka.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryEvent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {
        //invoke the kafka producer
        log.info("Before : sendLibraryEvent ");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        log.info("After : sendLibraryEvent ");

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

}
