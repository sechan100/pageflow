package org.pageflow.email.port;


import lombok.Value;

@Value
public class MailRequest {
  String to;
  String from;
  String fromName;
  String subject;
  EmailContent emailContent;

  public static BuildStep0 builder() {
    return new MailRequest.Builder();
  }


  // Build Steps
  public interface BuildStep0 {
    BuildStep1 to(String to);

  }

  public interface BuildStep1 {
    BuildStep2 from(String fromAddress);

    /**
     * @param fromAddress
     * @param fromName 사용자에게 fromName이 표시됨.
     * @return
     */
    BuildStep2 from(String fromAddress, String fromName);
  }

  public interface BuildStep2 {
    /**
     * @param subject 제목
     * @return
     */
    BuildStep3 subject(String subject);
  }

  public interface BuildStep3 {
    BuildStep4 content(EmailContent content);
  }

  public interface BuildStep4 {
    MailRequest build();
  }

  public static class Builder implements BuildStep0, BuildStep1, BuildStep2, BuildStep3, BuildStep4 {
    private String to;
    private String from;
    private String fromName;
    private String subject;
    private EmailContent emailContent;


    @Override
    public BuildStep1 to(String to) {
      this.to = to;
      return this;
    }

    @Override
    public BuildStep2 from(String fromAddress) {
      return from(fromAddress, null);
    }

    @Override
    public BuildStep2 from(String fromAddress, String fromName) {
      this.from = fromAddress;
      this.fromName = fromName;
      return this;
    }

    @Override
    public BuildStep3 subject(String subject) {
      this.subject = subject;
      return this;
    }

    @Override
    public BuildStep4 content(EmailContent emailContent) {
      this.emailContent = emailContent;
      return this;
    }


    @Override
    public MailRequest build() {
      return new MailRequest(
        to,
        from,
        fromName,
        subject,
        emailContent
      );
    }
  }


}