package edu.upc.talent.swqa.campus.domain.test;

import edu.upc.talent.swqa.campus.domain.CampusApp;
import edu.upc.talent.swqa.campus.domain.UsersRepository;
import edu.upc.talent.swqa.campus.infrastructure.PostgresQlUsersRepository;
import edu.upc.talent.swqa.campus.infrastructure.UsersDb;
import edu.upc.talent.swqa.jdbc.Database;
import static edu.upc.talent.swqa.jdbc.HikariCP.getDataSource;
import edu.upc.talent.swqa.jdbc.test.utils.DatabaseBackedTest;
import static edu.upc.talent.swqa.test.utils.Asserts.assertEquals;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.bouncycastle.asn1.dvcs.Data;

import java.util.Set;

public class CampusAppTest extends DatabaseBackedTest {

  final InMemoryEmailService emailService = new InMemoryEmailService();
  final UsersRepository usersRepository = new PostgresQlUsersRepository(db);
  final CampusApp app = new CampusApp(usersRepository, emailService);

  @Test
  public void testSendMailToGroup() {
    // Arrange
    db.update(UsersDb.groupsTableDml);
    db.update(UsersDb.usersTableDml);
    db.update("delete from users");
    db.update("delete from groups");
    app.createGroup("g1", "swqa");
    app.createGroup("g2", "electronics");
    app.createUser("1", "John", "Doe", "john.doe@example.com", "student", "swqa");
    app.createUser("2", "Jane", "Doe", "jane.doe@example.com", "student", "electronics");
    app.createUser("3", "Mariah", "Renfield", "mariah.renfield@example.com", "student", "swqa");

    // Act
    app.sendMailToGroup("swqa", "Hi!", "How are you doing?");

    // Assert
    // Consultar emails contra un servidor POP / IMAP
    final var sentEmails = emailService.getSentEmails();
    final var expected = Set.of(
          new SentEmail("john.doe@example.com", "Hi!", "How are you doing?"),
          new SentEmail("mariah.renfield@example.com", "Hi!", "How are you doing?")
    );
    assertEquals(expected, sentEmails);
  }
}
