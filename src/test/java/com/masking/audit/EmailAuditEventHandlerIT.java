package com.masking.audit;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.*;

import javax.mail.internet.MimeMessage;
import static org.junit.jupiter.api.Assertions.*;

class EmailAuditEventHandlerIT {
    private GreenMail greenMail;

    @BeforeEach
    void setup() {
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();
    }

    @AfterEach
    void teardown() {
        greenMail.stop();
    }

    @Test
    void handle_shouldSendEmail() throws Exception {
        EmailAuditEventHandler handler = new EmailAuditEventHandler(
                "localhost", 3025,
                "from@test.com", "to@test.com",
                "", ""
        );
        handler.handle("email", "a", "b");

        MimeMessage[] msgs = greenMail.getReceivedMessages();
        assertEquals(1, msgs.length);
        assertEquals("[AUDIT] field=email", msgs[0].getSubject());
        assertTrue(((String)msgs[0].getContent()).contains("before: a"));
    }
}
