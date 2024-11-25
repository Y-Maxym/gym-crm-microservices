package com.gym.crm.app.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(
        name = "training",
        schema = "public",
        indexes = @Index(name = "training_pkey", unique = true, columnList = "id")
)
@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
public final class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true, insertable = false)
    private final Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(
            name = "trainee_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_training_trainee")
    )
    private final Trainee trainee;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(
            name = "trainer_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_training_trainer")
    )
    private final Trainer trainer;

    @Column(name = "training_name", nullable = false, length = 100)
    private final String trainingName;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "training_type_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_training_training_type")
    )
    private final TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    private final LocalDate trainingDate;

    @Column(name = "training_duration", nullable = false)
    private final Integer trainingDuration;
}
