package com.nexaride.user.exception;

import com.nexaride.user.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){

        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        return new ResponseEntity<>(
                new ErrorResponse(errorMessage,400),
                HttpStatus.BAD_REQUEST
        );

    }
    //User Already Exists
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex){
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(), 409),
                HttpStatus.CONFLICT
        );
    }
    // WRONG PASSWORD → 401
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredential(InvalidCredentialsException ex){
        return new ResponseEntity<>(
                new ErrorResponse(ex.getMessage(),401),
                HttpStatus.UNAUTHORIZED
        );
    }

    //  USER NOT FOUND → 404
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex){
        return new ResponseEntity<>(
                new ErrorResponse("User not found",404),
                HttpStatus.NOT_FOUND
        );
    }

    //Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex){
        ex.printStackTrace();
        return new ResponseEntity<>(
                new ErrorResponse("Something went wrong",500),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
