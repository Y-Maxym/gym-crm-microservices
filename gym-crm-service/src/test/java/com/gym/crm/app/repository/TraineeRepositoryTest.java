package com.gym.crm.app.repository;

import com.gym.crm.app.entity.Trainee;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class TraineeRepositoryTest extends AbstractTestRepository<TraineeRepository> {

    @Test
    @DisplayName("Test find trainee by id functionality")
    public void givenId_whenFindById_thenTraineeIsReturned() {
        // given
        Trainee expected = EntityTestData.getTransientTraineeJohnDoe();
        entityManager.persist(expected);

        // when
        Optional<Trainee> actual = repository.findById(expected.getId());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find trainee by incorrect id functionality")
    public void givenIncorrectId_whenFindById_thenNullIsReturned() {
        // given
        Trainee expected = EntityTestData.getPersistedTraineeJohnDoe();

        // when
        Optional<Trainee> actual = repository.findById(expected.getId());

        // then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Test find all trainee functionality")
    public void givenTrainees_whenFindAll_thenTraineesIsReturned() {
        // given
        List<Trainee> trainees = addTraineeList();

        // when
        List<Trainee> actual = repository.findAll();

        // then
        assertThat(actual.size()).isEqualTo(3);
        assertThat(actual).containsAll(trainees);
    }

    @Test
    @DisplayName("Test save trainee without id functionality")
    public void givenTrainee_whenSaveTrainee_thenTraineeIsSaved() {
        // given
        Trainee trainee = EntityTestData.getTransientTraineeJohnDoe();

        // when
        Trainee actual = repository.save(trainee);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test update trainee functionality")
    public void givenTrainee_whenUpdateTrainee_thenTraineeIsUpdated() {
        // given
        Trainee traineeToSave = EntityTestData.getTransientTraineeJohnDoe();
        entityManager.persist(traineeToSave);

        String updatedAddress = "updated address";

        Trainee traineeToUpdate = entityManager.find(Trainee.class, traineeToSave.getId());
        traineeToUpdate = traineeToUpdate.toBuilder().address(updatedAddress).build();

        // when
        Trainee actual = repository.save(traineeToUpdate);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getAddress()).isEqualTo(updatedAddress);
    }

    @Test
    @DisplayName("Test delete trainee by id functionality")
    public void givenId_whenDeleteById_thenTraineeIsDeleted() {
        // given
        Trainee trainee = EntityTestData.getTransientTraineeJohnDoe();
        entityManager.persist(trainee);
        entityManager.clear();

        // when
        repository.deleteById(trainee.getId());

        // then
        Trainee actual = entityManager.find(Trainee.class, trainee.getId());

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("Test find trainee by username functionality")
    public void givenUsername_whenFindByUserUsername_thenTraineeIsFound() {
        // given
        Trainee expected = EntityTestData.getTransientTraineeJohnDoe();
        entityManager.persist(expected);

        // when
        Optional<Trainee> actual = repository.findByUserUsername(expected.getUser().getUsername());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test find trainee by incorrect username functionality")
    public void givenIncorrectUsername_whenFindByUserUsername_thenTraineeIsNotFound() {
        // given
        Trainee expected = EntityTestData.getTransientTraineeJohnDoe();
        entityManager.persist(expected);

        // when
        Optional<Trainee> actual = repository.findByUserUsername("username");

        // then
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Test delete trainee by username functionality")
    public void givenUsername_whenDeleteByUserUsername_thenTraineeIsDeleted() {
        // given
        Trainee trainee = EntityTestData.getTransientTraineeJohnDoe();
        entityManager.persist(trainee);
        entityManager.clear();
        String username = trainee.getUser().getUsername();

        // when
        repository.deleteByUserUsername(username);

        // then
        Trainee actual = entityManager.find(Trainee.class, trainee.getId());

        assertThat(actual).isNull();
    }

    private List<Trainee> addTraineeList() {
        Trainee trainee1 = EntityTestData.getTransientTraineeJohnDoe();
        Trainee trainee2 = EntityTestData.getTransientTraineeJaneSmith();
        Trainee trainee3 = EntityTestData.getTransientTraineeMichaelJohnson();

        entityManager.persist(trainee1);
        entityManager.persist(trainee2);
        entityManager.persist(trainee3);

        return List.of(trainee1, trainee2, trainee3);
    }
}
