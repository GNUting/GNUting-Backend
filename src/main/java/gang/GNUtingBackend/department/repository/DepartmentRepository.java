package gang.GNUtingBackend.department.repository;

import gang.GNUtingBackend.department.domain.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * name이 포함된 학과를 리스트로 찾는다.
     * @param name
     * @return
     */
    List<Department> findByNameContaining(String name);
}
