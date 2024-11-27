package com.gym.crm.microservices.trainer.hours.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "month_summary", schema = "public")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false, unique = true, insertable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "year_summary_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_month_year_summary")
    )
    private YearlySummary yearSummary;

    @Column(name = "training_month", nullable = false)
    private Integer month;

    @Column(name = "total_training_duration", nullable = false)
    private Integer totalTrainingDuration;
}
