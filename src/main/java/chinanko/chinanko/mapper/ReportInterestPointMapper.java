package chinanko.chinanko.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import chinanko.chinanko.dto.ReportInterestPointRequest;
import chinanko.chinanko.dto.ReportInterestPointResponse;
import chinanko.chinanko.model.InterestPoint;
import chinanko.chinanko.model.ProfileUser; // Asumido que existe
import chinanko.chinanko.model.ReportInterestPoint;
import chinanko.chinanko.model.TypeOfReportPoint; // Asumido que se llama User o ProfileUser

@Component
public class ReportInterestPointMapper {

    public static ReportInterestPoint toEntity(ReportInterestPointRequest r) {
        if (r == null) return null;

        return ReportInterestPoint.builder()
                .description(r.getDescription())
                .dateReport(LocalDateTime.now()) // Fecha automática al crear
                // Relaciones (Solo IDs para referencia, el Service valida existencia)
                .typeOfReportPoint(TypeOfReportPoint.builder().idTypeOfReportPoint(r.getTypeOfReportPointId()).build()) // Ajustar nombre ID según tu entidad TypeOfReportPoint
                .interestPoint(InterestPoint.builder().idInterestPoint(r.getInterestPointId()).build())
                .user(ProfileUser.builder().idProfileUser(r.getUserId()).build()) // Ajustar nombre ID según tu entidad User
                .build();
    }

    public static ReportInterestPointResponse toResponse(ReportInterestPoint e) {
        if (e == null) return null;

        return ReportInterestPointResponse.builder()
                .idReportInterestPoint(e.getIdReportInterestPoint())
                .description(e.getDescription())
                .dateReport(e.getDateReport())
                // Obtención segura de nombres para la respuesta
                .typeOfReportPointName(e.getTypeOfReportPoint() != null ? e.getTypeOfReportPoint().getType() : "Unknown") // Ajustar getter
                .interestPointName(e.getInterestPoint() != null ? e.getInterestPoint().getNameInterestPoint() : "Unknown")
                .userName(e.getUser() != null ? e.getUser().getFirstName() + " " + e.getUser().getLastName(): "Unknown") // Ajustar getter (email o nombre)
                .build();
    }
}