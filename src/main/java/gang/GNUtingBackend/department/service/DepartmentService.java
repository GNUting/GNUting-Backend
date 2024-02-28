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

    /**
     * name이 이름에 포함된 학과를 모두 찾는다.
     * @param name
     * @return
     */
    public List<Department> searchDepartments(String name) {
        return departmentRepository.findByNameContaining(name);
    }
}
