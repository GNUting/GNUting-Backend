package gang.GNUtingBackend.department.repository;

import gang.GNUtingBackend.department.domain.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByNameContaining(String name);
}
