package com.gym.crm.microservices.trainer.hours.service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "year_summary", schema = "public")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class YearlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false, unique = true, insertable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "trainer_summary_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_year_trainer_summary")
    )
    private TrainerSummary trainerSummary;

    @Column(name = "training_year", nullable = false)
    private Integer year;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "yearSummary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonthlySummary> monthlySummaries = new ArrayList<>();
}
