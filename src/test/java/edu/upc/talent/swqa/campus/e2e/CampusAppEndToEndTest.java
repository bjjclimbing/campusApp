package edu.upc.talent.swqa.campus.e2e;

import edu.upc.talent.swqa.campus.domain.CampusApp;
import edu.upc.talent.swqa.campus.infrastructure.PostgresQlUsersRepository;
import edu.upc.talent.swqa.campus.infrastructure.SmtpEmailService;
import edu.upc.talent.swqa.campus.infrastructure.UsersDb;
import edu.upc.talent.swqa.jdbc.test.utils.DatabaseBackedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class CampusAppEndToEndTest extends DatabaseBackedTest {

  private CampusApp app;

  @BeforeEach
  public void setUpDatabaseSchema() {
    db.update(UsersDb.groupsTableDml);
    db.update(UsersDb.usersTableDml);
    db.update("delete from users");
    db.update("delete from groups");
    final var repo = new PostgresQlUsersRepository(db);
    repo.createGroup("0", "swqa");
    repo.createUser("1", "John", "Doe", "john.doe@example.com", "student", "swqa");
    repo.createUser("2", "Jane", "Doe", "jane.doe@example.com", "student", "swqa");
    repo.createUser("3", "Mariah", "Harris", "mariah.hairam@example.com", "teacher", "swqa");
    this.app = new CampusApp(repo, new SmtpEmailService());
  }

  @Test
  public void testCreateGroup() {
    app.createGroup("1", "bigdata");

  }

  @Test
  public void testSendEmailToGroup() {
    app.sendMailToGroup("swqa", "New campus!", "Hello everyone! We just created a new virtual campus!");
  }

  @Test
  public void testSendEmailToGroupRole() {
    app.sendMailToGroupRole("swqa", "teacher", "Hey! Teacher!", "Let them students alone!!");
  }

}
