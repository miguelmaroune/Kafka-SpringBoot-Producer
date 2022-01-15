package com.learnKafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnKafka.domain.Book;
import com.learnKafka.domain.LibraryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)//We are going to use mockito to mock the kafka send call
public class LibraryEventProducerUnitTest {

    @Mock
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Spy//i use spy because i dont want to change its behavior.
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducer eventProducer;


    @Test
    void sendLibraryEvent_Approach2_failure() throws JsonProcessingException, ExecutionException, InterruptedException {

        //given
        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        //we created a ListenableFuture and added an exception.
        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Exception calling kafka"));
        //When sendLibraryEvent_Approach2 wants to call kafkaTemplate.send  return the future object that we created that contains the error.
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        //when
        assertThrows(Exception.class,() -> eventProducer.sendLibraryEvent_Approach2(libraryEvent).get());
        //then
    }

    @Test
    void sendLibraryEvent_Approach2_success() throws JsonProcessingException, ExecutionException, InterruptedException {

        //given
        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        String record = objectMapper.writeValueAsString(libraryEvent);
        //
        SettableListenableFuture future = new SettableListenableFuture();

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord("library-events",libraryEvent.getLibraryEventId(),record);
        RecordMetadata recordMetadataTest = new RecordMetadata(new TopicPartition("library-events",1),1,1,342,System.currentTimeMillis(),1,2);
        SendResult<Integer,String> sendResult = new SendResult<Integer,String>(producerRecord,recordMetadataTest);
//Creating a sendResult object to simulate a succesful call to the kafka server.
        future.set(sendResult);
        //When sendLibraryEvent_Approach2 wants to call kafkaTemplate.send  return the future object that we created that contains the SendResult we created .
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        //when
        ListenableFuture<SendResult<Integer,String>> listenableFuture =   eventProducer.sendLibraryEvent_Approach2(libraryEvent);
        //then

        SendResult<Integer,String> sendResult1 = listenableFuture.get();

        assert sendResult1.getRecordMetadata().partition()==1;
    }

}
