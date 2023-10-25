package it.marcodemartino.yapga.common.email;

public interface EmailProvider {

    void sendEmail(String toAddress, String subject, String content);

}
