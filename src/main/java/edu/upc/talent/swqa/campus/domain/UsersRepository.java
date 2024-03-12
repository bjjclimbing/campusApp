package edu.upc.talent.swqa.campus.domain;

import java.util.List;

public interface UsersRepository {
  List<User> getUsersByGroup(final String groupName);
  List<BirthdayEmailData> getUsersInBirthday();

  List<User> getUserById(final String id);

  void createUser(final String id, final String name, final String surname, final String email, final String role, final String groupName);

  void createGroup(final String id, final String name);
}
