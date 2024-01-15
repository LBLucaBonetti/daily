package it.lbsoftware.daily.emails;

import java.util.Map;

public interface EmailService {

  void send(EmailInfo emailInfo, Map<String, Object> context);
}
