package com.hhplus.concert.application;

import com.hhplus.concert.domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
class ConcertFacadeTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private SeatService seatService;


}