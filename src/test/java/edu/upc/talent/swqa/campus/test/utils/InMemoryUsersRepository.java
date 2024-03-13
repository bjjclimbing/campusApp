package edu.upc.talent.swqa.campus.test.utils;

import edu.upc.talent.swqa.campus.domain.BirthdayEmailData;
import edu.upc.talent.swqa.campus.domain.User;
import edu.upc.talent.swqa.campus.domain.UserNotFoundException;
import edu.upc.talent.swqa.campus.domain.UsersRepository;
import static edu.upc.talent.swqa.util.Utils.now;

import java.time.LocalDate;
import java.util.List;

public record InMemoryUsersRepository(UsersRepositoryState state) implements UsersRepository {

  @Override
  public void createUser(
        final String id,
        final String name,
        final String surname,
        final String email,
        final String role,
        final String groupName
  ) {
    final var user = new User(id, name, surname, email, role, groupName, now());
    state.users().add(user);
  }

  @Override
  public void createGroup(final String id, final String name) {
    state.groups().add(new Group(id, name));
  }


  @Override
  public List<User> getUsersByGroup(final String groupName) {
    return state.users().stream()
                .filter(user -> user.groupName().equals(groupName))
                .toList();
  }

  @Override public List<BirthdayEmailData> getUsersInBirthday() {
    final var today = LocalDate.now();
    return state.users().stream()
                .filter(user -> {
                  final var createdDate = user.createdDate();
                  return createdDate.getDayOfMonth() == today.getDayOfMonth() &&
                         createdDate.getMonth() == today.getMonth();
                })
                .map(user -> new BirthdayEmailData(
                      user.email(),
                      user.name(),
                      user.surname(),
                      user.createdAt().toString()
                ))
                .toList();
  }

  @Override
  public List<User> getUserById(final String id) throws UserNotFoundException {
    List<User> User = state.users().stream()
            .filter(user -> user.id().equals(id))
            .toList();
    if (User.isEmpty() ) {
      final String msg = "User "+id+" does not exist";
      throw new UserNotFoundException(msg);
    }
    return User;
  }

  @Override
  public void getIsaTeacher(User user) throws UserNotFoundException {
    if (!user.role().equals("teacher")) {
      final String msg = "User " + user.id() + " is not a teacher";
      throw new UserNotFoundException(msg);
    }
  }
}
