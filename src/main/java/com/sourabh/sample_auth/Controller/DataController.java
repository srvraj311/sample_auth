package com.sourabh.sample_auth.Controller;

import com.sourabh.sample_auth.Entity.Employee;
import com.sourabh.sample_auth.Service.EmployeeService;
import jakarta.el.MethodNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/employee")
public class DataController {

    @Autowired
    final EmployeeService employeeService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> addEmployee (@RequestBody Employee employee) throws Exception {
        return ResponseEntity.ok(employeeService.addEmp(employee));
    }


    @GetMapping("/")
    @PreAuthorize("hasAuthority('ROLE_USER') || hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getEmployee(@Param("employee_id") int employee_id) throws Exception {
        return ResponseEntity.ok(employeeService.getEmployeeForUser(employee_id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getAllEmployee() throws Exception {
        return ResponseEntity.ok(employeeService.getAllEmployee());
    }
}
