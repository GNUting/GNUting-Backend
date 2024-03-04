package gang.GNUtingBackend.department.controller;

import gang.GNUtingBackend.department.domain.Department;
import gang.GNUtingBackend.department.service.DepartmentService;
import gang.GNUtingBackend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
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
    @Operation(summary = "회원가입 시 학과 검색 API", description = "회원가입 시 자신의 학과를 검색합니다.")
    public ResponseEntity<ApiResponse<List<Department>>> searchDepartments(@RequestParam("name") @Parameter(description = "학과 이름") String name) {
        List<Department> departments = departmentService.searchDepartments(name);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(departments));
    }
}
