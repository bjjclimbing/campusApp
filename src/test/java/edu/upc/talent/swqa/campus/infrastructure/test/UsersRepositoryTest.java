package edu.upc.talent.swqa.campus.infrastructure.test;

import edu.upc.talent.swqa.campus.domain.User;
import edu.upc.talent.swqa.campus.domain.UsersRepository;
import edu.upc.talent.swqa.campus.test.utils.TestFixtures;
import edu.upc.talent.swqa.campus.test.utils.UsersRepositoryState;
import static edu.upc.talent.swqa.test.utils.Asserts.assertEquals;
import static edu.upc.talent.swqa.util.Utils.plus;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;

public interface UsersRepositoryTest {

  UsersRepository getRepository(final UsersRepositoryState initialState);

  UsersRepositoryState getFinalState();


  default void assertExpectedFinalState(final UsersRepositoryState expectedFinalState) {
    assertEquals(expectedFinalState, getFinalState());
  }

  UsersRepositoryState defaultInitialState = TestFixtures.defaultInitialState.usersRepositoryState();

  @Test
  default void testGetUsersByGroup() {
    final var repository = getRepository(defaultInitialState);
    final var actual = repository.getUsersByGroup("swqa");
    assertEquals(defaultInitialState.users(), new HashSet<>(actual));
    assertExpectedFinalState(defaultInitialState);
  }

  @Test
  default void testCreateUser() {
    final var repository = getRepository(defaultInitialState);
    final var id = "0";
    final var name = "Jack";
    final var surname = "Doe";
    final var email = "jack.doe@example.com";
    final var role = "student";
    final var groupName = "swqa";
    final var now = Instant.now();
    final var expectedNewUser = new User(id, name, surname, email, role, groupName, now);
    final var expected =
          new UsersRepositoryState(plus(defaultInitialState.users(), expectedNewUser), defaultInitialState.groups());
    repository.createUser(id, name, surname, email, role, groupName);
    assertExpectedFinalState(expected);
  }

  @Test
  @Disabled
  default void testCreateUserFailsIfGroupDoesNotExist() {
    final var repository = getRepository(defaultInitialState);
    final var groupName = "non-existent";
    final var exception = assertThrows(RuntimeException.class, () ->
          repository.createUser("0", "a", "b", "a.b@example.com", "student", groupName)
    );
    assertEquals("Group " + groupName + " does not exist", exception.getMessage());
    assertExpectedFinalState(defaultInitialState);
  }
}



