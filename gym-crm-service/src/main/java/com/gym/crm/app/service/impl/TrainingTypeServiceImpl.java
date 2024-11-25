package com.gym.crm.app.service.impl;

import com.gym.crm.app.entity.TrainingType;
import com.gym.crm.app.repository.TrainingTypeRepository;
import com.gym.crm.app.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> findAll() {
        return repository.findAll();
    }
}
