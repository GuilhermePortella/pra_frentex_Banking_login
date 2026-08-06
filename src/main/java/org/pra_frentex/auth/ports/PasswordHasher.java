package org.pra_frentex.auth.ports;

public interface PasswordHasher {

    String hash(char[] password);
    boolean matches(char[] rawPassword, String encodedPassword);

}
