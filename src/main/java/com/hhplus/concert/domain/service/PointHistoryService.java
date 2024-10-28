package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.PointHistory;
import com.hhplus.concert.domain.repository.PointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;

    @Autowired
    public PointHistoryService(PointHistoryRepository pointHistoryRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public List<PointHistory> getUserHistory(Long userId){
        return pointHistoryRepository.findByUserId(userId);
    }
}
