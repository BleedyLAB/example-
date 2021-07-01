package ru.isin.security.endpoint.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.isin.security.domain.dto.RegistrationDTO;
import ru.isin.security.domain.dto.responce.BaseResponse;
import ru.isin.security.domain.dto.responce.ErrorResponse;
import ru.isin.security.domain.dto.responce.RegistrationResponse;
import ru.isin.security.endpoint.utils.RestControllersUtil;
import ru.isin.security.service.jwt.JwtTokenProvider;
import ru.isin.security.service.user.SecurityUserService;
import ru.isin.security.service.email.EmailConfirmService;


/**
 * Controller for register new user.
 *
 * @author Bleedy (elderrat23@gmail.com) (28.11.2020)
 */
@RestController
@RequestMapping(RegisterController.BASE_URL)
@RequiredArgsConstructor
public class RegisterController implements AbstractRest {
    public static final String BASE_URL = AbstractRest.BASE_URL;

    private final SecurityUserService securityUserService;
    private final JwtTokenProvider tokenProvider;
    private final EmailConfirmService emailConfirmService;

    /**
     * Регистрация пользователя.
     *
     * @param user использует request для получения данных пользователя
     * @return возвращает ответ в виде HttpStatus.
     */
    @PostMapping("/register")
    public ResponseEntity<? extends BaseResponse> register(@RequestBody RegistrationDTO user) {
        try {
            securityUserService.saveUser(user);
            emailConfirmService.sendEmailConfirmation(user.getName(), user.getEmail(),
                    BASE_URL + "/activate");

            var successResponseEntity = new RegistrationResponse(
                    "User successfully registered, confirm your email",
                    HttpStatus.ACCEPTED);
            return new ResponseEntity<>(successResponseEntity, HttpStatus.ACCEPTED);
        } catch (DataIntegrityViolationException e) {
            var errorResponseEntity = new ErrorResponse(
                    e.getMostSpecificCause().getMessage(),
                    HttpStatus.BAD_REQUEST,
                    BASE_URL + "/register");
            return new ResponseEntity<>(errorResponseEntity, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var errorResponseEntity = new ErrorResponse(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST,
                    BASE_URL + "/register"
            );

            return new ResponseEntity<>(errorResponseEntity, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Изменение роли на "администратор".
     *
     * @param user использует request для получения данных пользователя
     * @return возвращает ответ в виде HttpStatus.
     */
    @PostMapping("/setAdmin")
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity makeAdmin(@RequestBody String user) {
        String userName;
        try {
            userName = securityUserService.makeAdminRole(user.toLowerCase()).getName();
        } catch (UsernameNotFoundException e) {

            var errorResponseEntity = new ErrorResponse(
                    "User not found",
                    HttpStatus.NOT_FOUND,
                    BASE_URL + "/setAdmin"
            );

            return new ResponseEntity<>(errorResponseEntity, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok("Success set admin on " + userName);
    }

    /**
     * Изменение данных пользователей.
     *
     * @param user  использует request для получения данных пользователя
     * @param token принимает токен для последующего парса и доступа к пользователю
     * @return возвращает ответ в виде HttpStatus.
     */
    @PostMapping("/updateUser")
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity updateUserDetails(@RequestBody RegistrationDTO user,
                                            @RequestHeader("Authorization") String token) {
        securityUserService.updateUser(tokenProvider.getTokenSubject(token), user);
        return ResponseEntity.ok("User successfully updated");
    }

    /**
     * Контроллер который обрабатывает запрос активации пользователя.
     * Вызывает функцию activateUserAccountByToken из сервиса активации пользователя.
     * Если возникают ошибки, то возвращается json с описанием ошибки.
     *
     * @param token jwt токен активации почты
     * @return json с сообщением об удачной активации почты либо с сообщением об ошибке
     */
    @GetMapping("/activate/{token}")
    public ResponseEntity activateUser(@PathVariable String token) {
        var baseResponse = RestControllersUtil.handleJwtException(
                () -> emailConfirmService.activateUserAccountByToken(token),
                BASE_URL + "/activate/{token}"
        );
        return ResponseEntity.ok(baseResponse);
    }
}
