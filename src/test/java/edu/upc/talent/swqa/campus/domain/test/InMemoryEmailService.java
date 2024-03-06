package edu.upc.talent.swqa.campus.domain.test;

import edu.upc.talent.swqa.campus.domain.EmailService;
import edu.upc.talent.swqa.campus.domain.User;

import java.util.HashSet;
import java.util.Set;

public class InMemoryEmailService implements EmailService {

  private final Set<SentEmail> sentEmails = new HashSet<>();

  @Override public void sendEmail(final User to, final String subject, final String body) {
    sentEmails.add(new SentEmail(to.email(), subject, body));
  }

  public Set<SentEmail> getSentEmails() {
    return Set.copyOf(sentEmails);
  }

}
