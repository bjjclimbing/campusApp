package edu.upc.talent.swqa.campus.domain;

import edu.upc.talent.swqa.jdbc.Database;
import static edu.upc.talent.swqa.jdbc.HikariCP.getDataSource;
import static edu.upc.talent.swqa.jdbc.Param.p;

public final class CampusApp {

  private final Database db = new Database(getDataSource("jdbc:postgresql:///", "postgres", "postgres"));

  public void sendMailToGroup(final String groupName, final String subject, final String body) {
    final var users = db.select(
          """
          select u.id, u.name, u.surname, u.email, u.role
          from users u join groups g on u.group_id = g.id
          where u.active and g.name = ?""",
          (rs) -> new User(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                groupName
          ),
          p(groupName)
    );
    users.forEach(u ->
                        System.out.println("--------\n" +
                                           "to: " + u.email() + "\n" +
                                           "subject: " + subject + "\n" +
                                           "body:\n" + body + "\n" +
                                           "--------\n")
    );
  }


  record BirthdayEmailData(String email, String name, String surname, String createdAt) {}

  public void sendBirthdayEmails() {
    db.select(
          "select email, name, surname, created_at from users where (now() - created_at) > '1 days' and  to_char(created_at, 'MM-dd') = to_char(now(), 'MM-dd')",
          (rs) -> new BirthdayEmailData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4))
    ).forEach(u ->
                    System.out.println("--------\n" +
                                       "to: " + u.email() + "\n" +
                                       "subject: Happy Campus Birthday!\n" +
                                       "body:\n" + "Happy campus birthday " + u.name() + " " + u.surname() + "!\n" +
                                       "You have been with us since " + u.createdAt() + "\n" +
                                       "--------\n")
    );
  }

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

  public void createGroup(final String id, final String name) {
    db.update("insert into groups (id, name) values (?, ?)", p(id), p(name));
  }


}
