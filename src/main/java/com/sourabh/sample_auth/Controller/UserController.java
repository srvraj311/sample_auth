package com.sourabh.sample_auth.Controller;


import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sourabh.sample_auth.Entity.User;
import com.sourabh.sample_auth.Service.UserService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")

public class UserController {
	
	private UserService userService;
	
	@GetMapping("/")
	public String testApplication() {
		return "The Application is working";
	}
	
	@PostMapping("/signup")
	public ResponseEntity<Object> signup (@RequestBody User user) throws BadRequestException {
		return ResponseEntity.ok(userService.signup(user));
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login (@RequestBody User user) throws Exception {
		return ResponseEntity.ok(userService.login(user));
	}

	/**
	 *
	 * @return user details for logged-in user, only with admin roles
	 * @throws Exception when user details are invalid or role is not admin
	 */
	@PreAuthorize("hasAuthority('ROLE_USER') || hasAuthority('ROLE_ADMIN')")
	@GetMapping("/details")
	public ResponseEntity<Object> getUserDetails () throws Exception {
		return ResponseEntity.ok(userService.getUserDetails());
	}
}
