    package com.patika.bootcamp.taskmanagement.service_tests;

    import com.patika.bootcamp.taskmanagement.model.User;
    import com.patika.bootcamp.taskmanagement.repository.UserRepository;
    import com.patika.bootcamp.taskmanagement.service.exception.UserNotFoundException;
    import com.patika.bootcamp.taskmanagement.service.impl.UserServiceImpl;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.util.List;
    import java.util.Optional;

    import static org.assertj.core.api.Assertions.assertThat;
    import static org.assertj.core.api.Assertions.assertThatThrownBy;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UserServiceImpl userService;

        private User mockUser;

        @BeforeEach
        void setUp() {
            mockUser = new User();
            mockUser.setId(1L);
            mockUser.setName("John Doe");
            mockUser.setEmail("john@example.com");
            mockUser.setRole("TEAM_MEMBER");
            mockUser.setDeleted(false);
        }

        @Test
        void shouldReturnAllUsers() {
            when(userRepository.findAllByDeletedFalse()).thenReturn(List.of(mockUser));
            List<User> users = userService.getAllUsers();
            assertThat(users).hasSize(1);
            assertThat(users.get(0)).isEqualTo(mockUser);
        }

        @Test
        void shouldReturnUserById() {
            when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));
            User user = userService.findById(1L);
            assertThat(user).isEqualTo(mockUser);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.findById(99L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        void shouldCreateUser() {
            when(userRepository.save(any(User.class))).thenReturn(mockUser);
            User user = userService.create(mockUser);
            assertThat(user).isEqualTo(mockUser);
        }

        @Test
        void shouldUpdateUser() {
            when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));
            when(userRepository.save(any(User.class))).thenReturn(mockUser);
            User updatedUser = userService.update(1L, mockUser);
            assertThat(updatedUser).isEqualTo(mockUser);
        }

        @Test
        void shouldSoftDeleteUser() {
            when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));
            userService.softDelete(1L);
            assertThat(mockUser.isDeleted()).isTrue();
            verify(userRepository, times(1)).save(mockUser);
        }
    }
