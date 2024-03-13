package edu.upc.talent.swqa.campus.infrastructure;

import edu.upc.talent.swqa.campus.domain.BirthdayEmailData;
import edu.upc.talent.swqa.campus.domain.User;
import edu.upc.talent.swqa.campus.domain.UserNotFoundException;
import edu.upc.talent.swqa.campus.domain.UsersRepository;
import edu.upc.talent.swqa.jdbc.Database;
import static edu.upc.talent.swqa.jdbc.Param.p;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

public class PostgreSqlUsersRepository implements UsersRepository {

  private final Database db;

  public PostgreSqlUsersRepository(final Database db) {
    this.db = db;
  }

  public List<User> getUsersByGroup(final String groupName) {
    return db.select(
            """
                    select u.id, u.name, u.surname, u.email, u.role, u.created_at
                    from users u join groups g on u.group_id = g.id
                    where u.active and g.name = ?""",
            (rs) -> new User(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    groupName,
                    rs.getInstant(6)
            ),
            p(groupName)
    );
  }


  @Override
  public List<BirthdayEmailData> getUsersInBirthday() {
    return db.select(
            "select email, name, surname, created_at from users where (now() - created_at) > '1 days' and  to_char(created_at, 'MM-dd') = to_char(now(), 'MM-dd')",
            (rs) -> new BirthdayEmailData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4))
    );
  }

  @Override
  public void createUser(
          final String id,
          final String name,
          final String surname,
          final String email,
          final String role,
          final String groupName
  ) {
    db.update(
            "insert into users (id, name, surname, email, role, group_id) " +
                    "values (?, ?, ?, ?, ?, (select id from groups where name = ?))",
            p(id),
            p(name),
            p(surname),
            p(email),
            p(role),
            p(groupName)
    );
  }

  @Override
  public void createGroup(final String id, final String name) {
    db.update("insert into groups (id, name) values (?, ?)", p(id), p(name));
  }

  @Override
  public List<User> getUserById(final String id) throws UserNotFoundException {

    List<User> User = db.select(
            """
                    select u.id, u.name, u.surname, u.email, u.role, u.group_id, u.created_at
                    from users u
                    where  u.id = ?""",
            (rs) -> new User(
                    id,
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getInstant(7)
            ),
            p(id)
    );
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
