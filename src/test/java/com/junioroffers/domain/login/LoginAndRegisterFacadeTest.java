package com.junioroffers.domain.login;

import com.junioroffers.domain.login.dto.RegisterUserDto;
import com.junioroffers.domain.login.dto.RegistrationResultDto;
import com.junioroffers.domain.login.dto.UserDto;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;

import static org.assertj.core.api.AbstractSoftAssertions.assertAll;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

public class LoginAndRegisterFacadeTest {

    LoginAndRegisterFacade loginFacade = new LoginAndRegisterFacade(
            new InMemoryLoginRepository()
    );

    @Test
    public void shouldRegisterUser() {
        //given
        RegisterUserDto registerUserDto = new RegisterUserDto("username", "password");

        //when
        RegistrationResultDto register = loginFacade.register(registerUserDto);

        //then
        assertThat(register.created()).isTrue();
        assertThat(register.username()).isEqualTo("username");
    }
    @Test
    public void shouldFindUserByUsername() {
        //given
        RegisterUserDto registerUserDto = new RegisterUserDto("username", "password");
        RegistrationResultDto register = loginFacade.register(registerUserDto);

        //when
        UserDto userByName = loginFacade.findByUsername(register.username());

        //then
        assertThat(userByName).isEqualTo(new UserDto(register.id(), "password", "username"));
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        //given
        String username = "user";

        //when
        Throwable thrown = catchThrowable(() -> loginFacade.findByUsername(username));

        //then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
