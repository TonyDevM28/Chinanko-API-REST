package chinanko.chinanko.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chinanko.chinanko.dto.ReportSuggestedPointRequest;
import chinanko.chinanko.dto.ReportSuggestedPointResponse;
import chinanko.chinanko.mapper.ReportSuggestedPointMapper;
import chinanko.chinanko.model.ReportSuggestedPoint;
import chinanko.chinanko.model.SuggestedPoint;
import chinanko.chinanko.model.TypeOfReportPoint;
import chinanko.chinanko.model.ProfileUser;
import chinanko.chinanko.repository.ReportSuggestedPointRepository;
import chinanko.chinanko.repository.SuggestedPointRepository;
import chinanko.chinanko.repository.TypeOfReportPointRepository;
import chinanko.chinanko.repository.ProfileUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportSuggestedPointServiceImpl implements ReportSuggestedPointService {

    private final ReportSuggestedPointRepository repository;
    private final SuggestedPointRepository suggestedPointRepository;
    private final ProfileUserRepository userRepository;
    private final TypeOfReportPointRepository typeRepository;

    @Override
    @Transactional
    public ReportSuggestedPointResponse create(ReportSuggestedPointRequest request) {
        // Validaciones de existencia
        SuggestedPoint point = suggestedPointRepository.findById(request.getSuggestedPointId())
                .orElseThrow(() -> new EntityNotFoundException("Suggested Point not found"));
        
        ProfileUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        TypeOfReportPoint type = typeRepository.findById(request.getTypeOfReportPointId())
                .orElseThrow(() -> new EntityNotFoundException("Type of Report not found"));

        // Construcción de la entidad
        ReportSuggestedPoint report = ReportSuggestedPoint.builder()
                .description(request.getDescription())
                .dateReport(LocalDateTime.now())
                .suggestedPoint(point)
                .user(user)
                .typeOfReportPoint(type)
                .build();

        return ReportSuggestedPointMapper.toResponse(repository.save(report));
    }

    @Override
    @Transactional
    public ReportSuggestedPointResponse update(Integer id, ReportSuggestedPointRequest request) {
        ReportSuggestedPoint existingReport = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + id));

        // Validar nuevas referencias si cambiaron
        if (!existingReport.getSuggestedPoint().getIdSuggestedPoint().equals(request.getSuggestedPointId())) {
             suggestedPointRepository.findById(request.getSuggestedPointId())
                .orElseThrow(() -> new EntityNotFoundException("Suggested Point not found"));
        }
        
        // Actualizar campos
        ReportSuggestedPointMapper.copyToEntity(existingReport, request);
        
        return ReportSuggestedPointMapper.toResponse(repository.save(existingReport));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Report not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportSuggestedPointResponse getById(Integer id) {
        ReportSuggestedPoint entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + id));
        return ReportSuggestedPointMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportSuggestedPointResponse> getBySuggestedPoint(Integer suggestedPointId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReportSuggestedPoint> result = repository.findBySuggestedPoint_IdSuggestedPoint(suggestedPointId, pageable);
        
        return result.stream()
                .map(ReportSuggestedPointMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportSuggestedPointResponse> getByUser(Integer userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReportSuggestedPoint> result = repository.findByUser_IdUser(userId, pageable);
        
        return result.stream()
                .map(ReportSuggestedPointMapper::toResponse)
                .collect(Collectors.toList());
    }
}