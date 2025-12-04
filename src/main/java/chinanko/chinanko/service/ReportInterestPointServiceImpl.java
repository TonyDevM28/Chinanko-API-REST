package chinanko.chinanko.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chinanko.chinanko.dto.ReportInterestPointRequest;
import chinanko.chinanko.dto.ReportInterestPointResponse;
import chinanko.chinanko.mapper.ReportInterestPointMapper;
import chinanko.chinanko.model.ReportInterestPoint;
import chinanko.chinanko.model.InterestPoint;
import chinanko.chinanko.model.TypeOfReportPoint;
import chinanko.chinanko.model.ProfileUser;
import chinanko.chinanko.repository.ReportInterestPointRepository;
import chinanko.chinanko.repository.InterestPointRepository;
import chinanko.chinanko.repository.ProfileUserRepository; // Ajustar si es ProfileUserRepository
import chinanko.chinanko.repository.TypeOfReportPointRepository; // Asumido
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportInterestPointServiceImpl implements ReportInterestPointService {

    private final ReportInterestPointRepository repository;
    private final InterestPointRepository interestPointRepository;
    private final ProfileUserRepository userRepository;
    private final TypeOfReportPointRepository typeRepository;

    @Override
    @Transactional
    public ReportInterestPointResponse create(ReportInterestPointRequest request) {
        // 1. Validar existencias
        InterestPoint point = interestPointRepository.findById(request.getInterestPointId())
                .orElseThrow(() -> new EntityNotFoundException("Interest Point not found"));
        
        ProfileUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        TypeOfReportPoint type = typeRepository.findById(request.getTypeOfReportPointId())
                .orElseThrow(() -> new EntityNotFoundException("Type of Report not found"));

        // 2. Crear Entidad
        ReportInterestPoint report = ReportInterestPoint.builder()
                .description(request.getDescription())
                .dateReport(LocalDateTime.now())
                .interestPoint(point)
                .user(user)
                .typeOfReportPoint(type)
                .build();

        return ReportInterestPointMapper.toResponse(repository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public ReportInterestPointResponse getById(Integer id) {
        ReportInterestPoint entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + id));
        return ReportInterestPointMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportInterestPointResponse> getByInterestPoint(Integer interestPointId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReportInterestPoint> result = repository.findByInterestPoint_IdInterestPoint(interestPointId, pageable);
        
        return result.stream()
                .map(ReportInterestPointMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportInterestPointResponse> getByUser(Integer userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ReportInterestPoint> result = repository.findByUser_IdUser(userId, pageable);
        
        return result.stream()
                .map(ReportInterestPointMapper::toResponse)
                .collect(Collectors.toList());
    }
}