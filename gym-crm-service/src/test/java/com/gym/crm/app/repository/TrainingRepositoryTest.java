package com.gym.crm.app.repository;

import com.gym.crm.app.entity.Training;
import com.gym.crm.app.service.search.TraineeTrainingSearchFilter;
import com.gym.crm.app.service.search.TrainerTrainingSearchFilter;
import com.gym.crm.app.service.spectification.TraineeTrainingSpecification;
import com.gym.crm.app.service.spectification.TrainerTrainingSpecification;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class TrainingRepositoryTest extends AbstractTestRepository<TrainingRepository> {

    @Test
    @DisplayName("Test find training by id functionality")
    public void givenId_whenFindById_thenTrainingIsReturned() {
        // given
        Training expected = EntityTestData.getTransientTrainingEmilyDavis();
        entityManager.persist(expected);

        // when
        Optional<Training> actual = repository.findById(expected.getId());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find training by incorrect id functionality")
    public void givenIncorrectId_whenFindById_thenNullIsReturned() {
        // given
        Training expected = EntityTestData.getPersistedTrainingDavidBrown();

        // when
        Optional<Training> actual = repository.findById(expected.getId());

        // then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Test find all training functionality")
    public void givenTrainings_whenFindAll_thenTrainingsIsReturned() {
        // given
        Training training1 = EntityTestData.getTransientTrainingEmilyDavis();
        Training training2 = EntityTestData.getTransientTrainingDavidBrown();

        entityManager.persist(training1);
        entityManager.persist(training2);

        // when
        List<Training> actual = repository.findAll();

        // then
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).containsAll(List.of(training1, training2));
    }

    @Test
    @DisplayName("Test save training without id functionality")
    public void givenTraining_whenSaveTraining_thenTrainingIsSaved() {
        // given
        Training training = EntityTestData.getTransientTrainingEmilyDavis();

        // when
        Training actual = repository.save(training);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test update training functionality")
    public void givenTraining_whenUpdateTraining_thenTrainingIsUpdated() {
        // given
        Training training = EntityTestData.getTransientTrainingEmilyDavis();
        entityManager.persist(training);

        // when
        Training actual = repository.save(training);

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Test delete training by id functionality")
    public void givenId_whenDeleteById_thenTrainingIsDeleted() {
        // given
        Training training = EntityTestData.getTransientTrainingEmilyDavis();
        entityManager.persist(training);
        entityManager.clear();

        // when
        repository.deleteById(training.getId());

        // then
        Training actual = entityManager.find(Training.class, training.getId());

        assertThat(actual).isNull();
    }


    @Test
    @DisplayName("Test find trainee trainings by criteria functionality")
    public void givenValidTraineeCriteria_whenFindTrainings_thenReturnTrainings() {
        // given
        List<Training> trainings = addTraineeTrainingList();
        Training training = trainings.get(1);

        String username = training.getTrainee().getUser().getUsername();
        LocalDate from = training.getTrainingDate();
        LocalDate to = training.getTrainingDate();
        String trainerName = training.getTrainer().getUser().getFirstName();
        String trainingType = training.getTrainingType().getTrainingTypeName();

        TraineeTrainingSearchFilter searchFilter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .from(from)
                .to(to)
                .profileName(trainerName)
                .trainingType(trainingType)
                .build();
        Specification<Training> specification = TraineeTrainingSpecification.findByCriteria(searchFilter);

        // when
        List<Training> actual = repository.findAll(specification);

        // then
        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual).contains(training);
    }

    @Test
    @DisplayName("Test find trainee trainings by null criteria functionality")
    public void givenNullTraineeCriteria_whenFindTrainings_thenReturnTrainings() {
        // given
        List<Training> trainingList = addTraineeTrainingList();

        String username = trainingList.get(0).getTrainee().getUser().getUsername();
        TraineeTrainingSearchFilter searchFilter = TraineeTrainingSearchFilter.builder().username(username).build();
        Specification<Training> specification = TraineeTrainingSpecification.findByCriteria(searchFilter);

        // when
        List<Training> trainings = repository.findAll(specification);

        // then
        assertThat(trainings.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test find trainee trainings by blank criteria functionality")
    public void givenBlankTraineeCriteria_whenFindTrainings_thenReturnTrainings() {
        // given
        List<Training> trainingList = addTraineeTrainingList();

        String username = trainingList.get(0).getTrainee().getUser().getUsername();
        TraineeTrainingSearchFilter searchFilter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .profileName("")
                .trainingType("")
                .build();
        Specification<Training> specification = TraineeTrainingSpecification.findByCriteria(searchFilter);

        // when
        List<Training> trainings = repository.findAll(specification);

        // then
        assertThat(trainings.size()).isEqualTo(1);
    }


    @Test
    @DisplayName("Test find trainer trainings by criteria functionality")
    public void givenValidTrainerCriteria_whenFindTrainings_thenReturnTrainings() {
        // given
        List<Training> trainings = addTrainerTrainingList();
        Training training = trainings.get(1);

        String username = training.getTrainer().getUser().getUsername();
        LocalDate from = training.getTrainingDate();
        LocalDate to = training.getTrainingDate();
        String traineeName = training.getTrainee().getUser().getFirstName();

        TrainerTrainingSearchFilter searchFilter = TrainerTrainingSearchFilter.builder()
                .username(username)
                .from(from)
                .to(to)
                .profileName(traineeName)
                .build();
        Specification<Training> specification = TrainerTrainingSpecification.findByCriteria(searchFilter);

        // when
        List<Training> actual = repository.findAll(specification);

        // then
        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual).contains(training);
    }

    @Test
    @DisplayName("Test find trainings by null criteria functionality")
    public void givenNullTrainerCriteria_whenFindTrainings_thenReturnTrainings() {
        // given
        List<Training> trainingList = addTrainerTrainingList();

        String username = trainingList.get(0).getTrainer().getUser().getUsername();
        TrainerTrainingSearchFilter searchFilter = TrainerTrainingSearchFilter.builder().username(username).build();
        Specification<Training> specification = TrainerTrainingSpecification.findByCriteria(searchFilter);

        // when
        List<Training> trainings = repository.findAll(specification);

        // then
        assertThat(trainings.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test find trainings by blank criteria functionality")
    public void givenBlankTrainerCriteria_whenFindTrainings_thenReturnTrainings() {
        // given
        List<Training> trainingList = addTrainerTrainingList();

        String username = trainingList.get(0).getTrainer().getUser().getUsername();
        TrainerTrainingSearchFilter searchFilter = TrainerTrainingSearchFilter.builder()
                .username(username)
                .profileName("")
                .build();
        Specification<Training> specification = TrainerTrainingSpecification.findByCriteria(searchFilter);

        // when
        List<Training> trainings = repository.findAll(specification);

        // then
        assertThat(trainings.size()).isEqualTo(1);
    }

    private List<Training> addTraineeTrainingList() {
        Training training1 = EntityTestData.getTransientTrainingEmilyDavis();
        Training training2 = EntityTestData.getTransientTrainingDavidBrown();

        entityManager.persist(training1);
        entityManager.persist(training2);

        return List.of(training1, training2);
    }

    private List<Training> addTrainerTrainingList() {
        Training training1 = EntityTestData.getTransientTrainingEmilyDavis();
        Training training2 = EntityTestData.getTransientTrainingDavidBrown();

        entityManager.persist(training1);
        entityManager.persist(training2);

        return List.of(training1, training2);
    }
}
