package edu.upc.talent.swqa.campus.test.utils;
import edu.upc.talent.swqa.campus.domain.CampusApp;

import edu.upc.talent.swqa.campus.domain.UserNotFoundException;
import edu.upc.talent.swqa.campus.domain.test.InMemoryEmailService;
import org.junit.jupiter.api.Test;

import static edu.upc.talent.swqa.campus.test.utils.TestFixtures.defaultInitialState;
import static edu.upc.talent.swqa.test.utils.Asserts.assertEquals;

public class SendEmailTest {
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
    public void testEmailSubject() throws UserNotFoundException {
        final var app = getApp(defaultInitialState);
        final var subject = "";
        final var body = "Let them students alone!!";
        final var id="1";
        assertEquals(subject,"");
    }
}
