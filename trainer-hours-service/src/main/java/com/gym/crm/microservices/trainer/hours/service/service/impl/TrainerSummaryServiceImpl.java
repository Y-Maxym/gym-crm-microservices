package com.gym.crm.microservices.trainer.hours.service.service.impl;

import com.gym.crm.microservices.trainer.hours.service.entity.MonthlySummary;
import com.gym.crm.microservices.trainer.hours.service.entity.TrainerSummary;
import com.gym.crm.microservices.trainer.hours.service.entity.YearlySummary;
import com.gym.crm.microservices.trainer.hours.service.exception.DataNotFoundException;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerSummaryRequest;
import com.gym.crm.microservices.trainer.hours.service.model.TrainerWorkloadResponse;
import com.gym.crm.microservices.trainer.hours.service.repository.TrainerSummaryRepository;
import com.gym.crm.microservices.trainer.hours.service.service.TrainerSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerSummaryServiceImpl implements TrainerSummaryService {

    private static final String USER_INFORMATION_NOT_FOUND = "User information not found";

    private final TrainerSummaryRepository repository;

    @Override
    public void sumTrainerSummary(TrainerSummaryRequest request) {
        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();
        int trainingDuration = request.getTrainingDuration();
        TrainerSummaryRequest.ActionTypeEnum actionType = request.getActionType();

        TrainerSummary trainerSummary = getOrCreateTrainerSummary(request);
        YearlySummary yearlySummary = getOrCreateYearlySummary(trainerSummary, year);
        MonthlySummary monthlySummary = getOrCreateMonthlySummary(yearlySummary, month);

        updateMonthlySummary(monthlySummary, trainingDuration, actionType);

        repository.save(trainerSummary);
    }

    @Override
    public TrainerWorkloadResponse getTrainerWorkload(String username, Integer year, Integer month) {
        List<TrainerSummary> summaryList = repository.findByParams(username, year, month);

        int workload = summaryList.stream()
                .flatMap(trainerSummary -> trainerSummary.getYearlySummaries().stream())
                .flatMap(yearlySummary -> yearlySummary.getMonthlySummaries().stream())
                .mapToInt(MonthlySummary::getTotalTrainingDuration)
                .sum();


        if (workload == 0) {
            throw new DataNotFoundException(USER_INFORMATION_NOT_FOUND);
        }

        return new TrainerWorkloadResponse()
                .workload(workload);
    }

    private TrainerSummary getOrCreateTrainerSummary(TrainerSummaryRequest request) {
        return repository
                .findByUsername(request.getUsername())
                .orElseGet(() -> createNewTrainerSummary(request));
    }

    private YearlySummary getOrCreateYearlySummary(TrainerSummary trainerSummary, int year) {
        return trainerSummary.getYearlySummaries().stream()
                .filter(yearSummary -> yearSummary.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> createNewYearlySummary(trainerSummary, year));
    }

    private MonthlySummary getOrCreateMonthlySummary(YearlySummary yearlySummary, int month) {
        return yearlySummary.getMonthlySummaries().stream()
                .filter(monthSummary -> monthSummary.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> createNewMonthlySummary(yearlySummary, month));
    }

    private void updateMonthlySummary(MonthlySummary monthlySummary, int additionalDuration, TrainerSummaryRequest.ActionTypeEnum actionType) {
        int summedTotalDuration = switch (actionType) {
            case ADD -> monthlySummary.getTotalTrainingDuration() + additionalDuration;
            case DELETE -> monthlySummary.getTotalTrainingDuration() - additionalDuration;
        };

        monthlySummary.setTotalTrainingDuration(summedTotalDuration);
    }

    private TrainerSummary createNewTrainerSummary(TrainerSummaryRequest request) {
        return TrainerSummary.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(request.getIsActive())
                .build();
    }

    private YearlySummary createNewYearlySummary(TrainerSummary trainerSummary, int year) {
        YearlySummary yearlySummary = YearlySummary.builder()
                .year(year)
                .build();

        trainerSummary.getYearlySummaries().add(yearlySummary);
        return yearlySummary;
    }

    private MonthlySummary createNewMonthlySummary(YearlySummary yearlySummary, int month) {
        MonthlySummary monthlySummary = MonthlySummary.builder()
                .month(month)
                .totalTrainingDuration(0)
                .build();

        yearlySummary.getMonthlySummaries().add(monthlySummary);
        return monthlySummary;
    }
}
