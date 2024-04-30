package com.sourabh.sample_auth.Service;

import com.sourabh.sample_auth.Entity.Employee;
import com.sourabh.sample_auth.Entity.User;
import com.sourabh.sample_auth.Repository.EmployeeRepository;
import com.sourabh.sample_auth.Repository.UserRepositiory;
import com.sourabh.sample_auth.Utils.ApiResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.security.auth.login.CredentialNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeService {


    @Autowired
    final EmployeeRepository employeeRepository;

    @Autowired
    final UserRepositiory userRepositiory;

    public ApiResponse addEmp(Employee employee) throws Exception {
        if (employee.getName() == null || employee.getName().isEmpty()) {
            throw new BadRequestException("Employee Name is required");
        }
        if (employee.getRole() == null || employee.getRole().isEmpty()) {
            throw new BadRequestException("Employee Role is required");
        }

        employeeRepository.save(employee);
        HashMap<String, Object> res = new HashMap<>();

        res.put("employee" , employee);
        res.put("message", "Employee saved successfully");

        return new ApiResponse("OK", null, res);
    }

    public ApiResponse getEmployeeForUser(int employeeId) throws Exception {
        if (employeeId == 0) {
            throw new BadRequestException("Employee ID is 0");
        }
        Optional<Employee> dbEmp = employeeRepository.findById(employeeId);
        if (dbEmp.isEmpty()) {
            throw new BadRequestException("No employee with ID present");
        }
        Employee emp = dbEmp.get();

        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails userDetails) {
                User user = userRepositiory.findOneByUserName(userDetails.getUsername()).isPresent() ? userRepositiory.findOneByUserName(userDetails.getUsername()).get() : null;
                if (user != null && user.getRole().contains("ROLE_USER")) {
                    emp.setAnnual_rating(null);
                    emp.setQuarterly_rating(null);
                    emp.setWork_manager(null);
                    emp.setLine_manager(null);
                } else if (user != null && user.getRole().contains("ROLE_ADMIN")){

                }

                HashMap<String, Object> res = new HashMap<>();
                res.put("employee" , emp);
                res.put("message", "Employee details fetched successfully");
                return new ApiResponse("OK", null, res);
            } else {
                throw new RuntimeException("An errror occured while validating user");
            }
        }

        throw new CredentialNotFoundException("User not authenticated");
    }


    public ApiResponse getEmployeeForAdmin(int employeeId) throws BadRequestException {
        if (employeeId == 0) {
            throw new BadRequestException("Employee ID is 0");
        }

        Optional<Employee> dbEmp = employeeRepository.findById(employeeId);
        if (dbEmp.isEmpty()) {
            throw new BadRequestException("No employee with ID present");
        }

        Employee emp = dbEmp.get();

        HashMap<String, Object> res = new HashMap<>();
        res.put("employee" , emp);
        res.put("message", "Employee details fetched successfully");

        return new ApiResponse("OK", null, res);
    }

    public ApiResponse getAllEmployee() {
        List<Employee> employeeList = (List<Employee>) employeeRepository.findAll();

        HashMap<String, Object> res = new HashMap<>();
        res.put("employees" , employeeList);
        res.put("message", "Employee details fetched successfully");

        return new ApiResponse("OK", null, res);
    }
}
