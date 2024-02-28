package gang.GNUtingBackend.department.service;

import gang.GNUtingBackend.department.domain.Department;
import gang.GNUtingBackend.department.repository.DepartmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> searchDepartments(String name) {
        return departmentRepository.findByNameContaining(name);
    }
}
