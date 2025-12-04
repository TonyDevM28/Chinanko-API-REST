package chinanko.chinanko.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REPORTS_INTEREST_POINTS")
public class ReportInterestPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_report_interest_point")
    private Integer idReportInterestPoint;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date_report")
    private LocalDateTime dateReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_type_of_report_point", nullable = false)
    private TypeOfReportPoint typeOfReportPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_interest_point", nullable = false)
    private InterestPoint interestPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_user", nullable = false)
    private ProfileUser user;
}