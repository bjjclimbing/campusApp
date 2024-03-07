package edu.upc.talent.swqa.campus.test.utils;

import edu.upc.talent.swqa.campus.domain.User;
import edu.upc.talent.swqa.campus.test.utils.CampusAppState;
import edu.upc.talent.swqa.campus.test.utils.Group;
import edu.upc.talent.swqa.campus.test.utils.UsersRepositoryState;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public final class TestFixtures {

  private TestFixtures() {}

  public static final CampusAppState defaultInitialState = new CampusAppState(
        new UsersRepositoryState(
              Set.of(
                    new User("1", "John", "Doe", "john.doe@example.com", "student", "swqa", Instant.now().minus(200, ChronoUnit.DAYS)),
                    new User("2", "Jane", "Doe", "jane.doe@example.com", "student", "swqa", Instant.now().minus(100, ChronoUnit.DAYS)),
                    new User("3", "Mariah", "Harris", "mariah.hairam@example.com", "teacher", "swqa", Instant.now().minus(300, ChronoUnit.DAYS))
              ),
              Set.of(new Group("1", "swqa"))
        ),
        Set.of()
  );


}
