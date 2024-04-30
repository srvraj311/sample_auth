package com.sourabh.sample_auth.Service;

import java.util.HashMap;
import java.util.Optional;

import com.sourabh.sample_auth.Config.JWTTokenUtil;
import com.sourabh.sample_auth.Repository.UserRepositiory;
import com.sourabh.sample_auth.Utils.ApiResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sourabh.sample_auth.Entity.User;

import javax.security.auth.login.CredentialNotFoundException;
import javax.security.sasl.AuthenticationException;

@Service
public class UserService {

	@Autowired
	final PasswordEncoder passwordEncoder;

	@Autowired
	final JWTTokenUtil jwtTokenUtil;

	@Autowired
	final UserRepositiory userRepositiory;

	@Autowired
	final AuthenticationManager authenticationManager;

    public UserService(PasswordEncoder passwordEncoder, JWTTokenUtil jwtTokenUtil, UserRepositiory userRepositiory, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepositiory = userRepositiory;
        this.authenticationManager = authenticationManager;
    }

    public ApiResponse signup(User user) throws BadRequestException {
		if (user.getUsername() == null) {
			throw new BadRequestException("Username is missing from request");
		}

		if (user.getPassword() == null) {
			throw new BadRequestException("Password is required");
		}

		if (user.getRole() == null || user.getRole().isEmpty()) {
			throw new BadRequestException("Role is required");
		}

		if (userRepositiory.findOneByUserName(user.getUsername()).isPresent()) {
			throw new BadRequestException("User already exists for given username");
		}

		try {
			// Set username to lowercase
			user.setUsername(user.getUsername().toLowerCase());
			// Encode password for saving
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			// Create a jwt token
			String token = jwtTokenUtil.generateToken(user);

			HashMap<String, Object> map = new HashMap<>();
			map.put("token" , token);
			map.put("user", user);
			map.put("message" , "User created successfully");

			userRepositiory.save(user);

			return new ApiResponse("OK", null , map);
		} catch (Exception e) {
			throw new RuntimeException("Error occurred while signing up");
		}
	}

	public ApiResponse login(User user) throws Exception {
		if (user.getUsername() == null) {
			throw new BadRequestException("Username is missing from request");
		}

		if (user.getPassword() == null) {
			throw new BadRequestException("Password is required");
		}

		if (userRepositiory.findOneByUserName(user.getUsername()).isEmpty()) {
			throw new BadRequestException("User does not exists");
		}

		String encodedPassword = passwordEncoder.encode(user.getPassword());
		Optional<User> dbUser = userRepositiory.findOneByUserName(user.getUsername());
		if (dbUser.isPresent()) {
			System.out.println(encodedPassword);
			System.out.println(dbUser.get().getPassword());
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

			if (authentication.isAuthenticated()) {
				String token = jwtTokenUtil.generateToken(user);
				HashMap<String, Object> map = new HashMap<>();
				map.put("token", token);
				map.put("user", dbUser.get());
				return new ApiResponse( "OK", null, map);
			} else {
				throw new AuthenticationException("Password is incorrect");
			}
		} else {
			throw new CredentialNotFoundException("No user exists for this username");
		}
	}

	public ApiResponse getUserDetails() throws Exception {
		ApiResponse apiResponse = new ApiResponse( "OK", null, null);
		HashMap<String, Object> response = new HashMap<>();
		try {
			if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
				if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails userDetails) {
					User user = userRepositiory.findOneByUserName(userDetails.getUsername()).isPresent() ? userRepositiory.findOneByUserName(userDetails.getUsername()).get() : null;
					if (user != null && !user.getRole().contains("ROLE_ADMIN")) {
						user.setCreated_at(null);
						user.setUpdated_at(null);
						user.setDeleted_at(null);
						response.put("message", "Resource limited for this user");
					}
					response.put("user", user);
					apiResponse.setBody(response);
					return apiResponse;
				} else {
					throw new BadRequestException("User Details inValid");
				}
			}
			throw new UsernameNotFoundException("User not authorized");
		} catch (Exception e) {
			throw new RuntimeException("An server error occurred");
		}
	}
}
