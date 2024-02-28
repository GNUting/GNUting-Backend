package gang.GNUtingBackend.department.controller;

import gang.GNUtingBackend.department.domain.Department;
import gang.GNUtingBackend.department.service.DepartmentService;
import gang.GNUtingBackend.response.ApiResponse;
import java.util.List;
import javax.naming.Name;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 파라미터가 포함된 모든 학과를 리스트로 나타낸다.
     * @param name
     * @return
     */
    @GetMapping("/search-department")
    public ResponseEntity<ApiResponse<List<Department>>> searchDepartments(@RequestParam("name") String name) {
        List<Department> departments = departmentService.searchDepartments(name);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(departments));
    }
}
