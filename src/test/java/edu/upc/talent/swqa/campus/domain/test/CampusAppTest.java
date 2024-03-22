package edu.upc.talent.swqa.campus.domain.test;

import edu.upc.talent.swqa.campus.domain.CampusApp;
import edu.upc.talent.swqa.campus.domain.UserNotFoundException;
import edu.upc.talent.swqa.campus.infrastructure.PostgreSqlUsersRepository;
import edu.upc.talent.swqa.campus.test.utils.CampusAppState;
import edu.upc.talent.swqa.campus.test.utils.Group;
import edu.upc.talent.swqa.campus.test.utils.InMemoryUsersRepository;
import edu.upc.talent.swqa.campus.test.utils.SentEmail;
import static edu.upc.talent.swqa.campus.test.utils.TestFixtures.defaultInitialState;
import edu.upc.talent.swqa.campus.test.utils.UsersRepositoryState;
import static edu.upc.talent.swqa.test.utils.Asserts.assertEquals;
import static edu.upc.talent.swqa.util.Utils.union;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Set;

public final class CampusAppTest {

  private CampusAppState state;

  private CampusApp getApp(final CampusAppState initialState) {
    state = initialState.copy();
    return new CampusApp(
          new InMemoryUsersRepository(state.usersRepositoryState()),
          new InMemoryEmailService(state.sentEmails())
    );
  }

  private CampusAppState getFinalState() {
    return state;
  }

  @Test
  public void testCreateGroup() {
    final var app = getApp(defaultInitialState);
    final var newGroup = new Group("0", "bigdata");
    app.createGroup(newGroup.id(),newGroup.name());
    final var expectedFinalState = new CampusAppState(
          new UsersRepositoryState(
                defaultInitialState.usersRepositoryState().users(),
                union(defaultInitialState.usersRepositoryState().groups(), Set.of(newGroup))
          ),
          Set.of()
    );
    assertEquals(expectedFinalState, getFinalState());
  }

  @Test
  public void testSendEmailToGroup() {
    final var app = getApp(defaultInitialState);
    final var subject = "New campus!";
    final var body = "Hello everyone! We just created a new virtual campus!";
    app.sendMailToGroup("swqa", subject, body);
    final var expectedFinalState = new CampusAppState(
          defaultInitialState.usersRepositoryState(),
          Set.of(
                new SentEmail("john.doe@example.com", subject, body),
                new SentEmail("jane.doe@example.com", subject, body),
                new SentEmail("mariah.hairam@example.com", subject, body)
          )
    );
    assertEquals(expectedFinalState, getFinalState());
  }

  @Test
  public void testSendEmailToGroupRole() {
    final var app = getApp(defaultInitialState);
    final var subject = "Hey! Teacher!";
    final var body = "Let them students alone!!";
    app.sendMailToGroupRole("swqa", "teacher", subject, body);
    final var expectedFinalState = new CampusAppState(
          defaultInitialState.usersRepositoryState(),
          Set.of(new SentEmail("mariah.hairam@example.com", subject, body))
    );
    assertEquals(expectedFinalState, getFinalState());
  }

  //Test WorkShop 1
  @Test
  public void testSendEmailById() throws UserNotFoundException {
    final var app = getApp(defaultInitialState);
    final var subject = "Hey! Teacher!";
    final var body = "Let them students alone!!";
    final var id="3";
    final var confirm=true;
    app.sendMailToTeacher(id,  subject, body,confirm);
    final var expectedFinalState = new CampusAppState(
            defaultInitialState.usersRepositoryState(),
            Set.of(new SentEmail("mariah.hairam@example.com", subject, body))
    );
    assertEquals(expectedFinalState, getFinalState());
  }


  //Test Mejora 1
  @Test
  public void testisSubjectNotNull() throws UserNotFoundException{
    final var app = getApp(defaultInitialState);
    final var subject = "";
    final var exception = assertThrows(edu.upc.talent.swqa.campus.domain.UserNotFoundException.class, () -> {
              try {
                app.isSubjectNotNull(subject);
              } catch (UserNotFoundException e) {
                throw new UserNotFoundException(e.getMessage());
              }
            }
    );
    assertEquals("The email subject is mandatory", exception.getMessage());
  }

  //Test Mejora 2 - confirm false
  @Test
  public void testBodyNotNull() throws UserNotFoundException{
    final var app = getApp(defaultInitialState);

    final var body="";
    final var confirm=false;
    final var exception = assertThrows(edu.upc.talent.swqa.campus.domain.UserNotFoundException.class, () -> {
              try {
                app.isBodyNotNull(body,confirm);
              } catch (UserNotFoundException e) {
                throw new UserNotFoundException(e.getMessage());
              }
            }
    );
    assertEquals("No se ha indicado el cuerpo del mensaje. Infórmelo o marque la casilla 'Confirmar'", exception.getMessage());
  }



}
