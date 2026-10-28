package com.IIITV.EMS.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.IIITV.EMS.Model.ActivityLog;

public interface ActivityLogRepository
        extends MongoRepository<ActivityLog, String> {

    List<ActivityLog> findAllByOrderByOccurredAtDesc(Pageable pageable);
}
