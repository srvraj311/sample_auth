package com.sourabh.sample_auth.Utils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL )
public class ApiResponse {
    private String status;
    private ErrorBody error;
    private Object body;
}
