package chatApp.utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static chatApp.utilities.Utility.isValidPassword;
import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {
    private Utility utility ;
    @BeforeEach
    void beforeEach(){
        utility = new Utility();
    }

    @Test
    void isValidPassword_checkPssword_validPassword() {assertTrue(isValidPassword("wslkt8Wr94ekl"));}
    @Test
    void isValidPassword_checkPssword_invalidPassword() {
        assertFalse(isValidPassword(null));
    }

    @Test
    void isValidName_checkName_invalidName() {
        assertFalse(Utility.isValidName("dani9"));
    }
    @Test
    void isValidName_checkName_invalidName1() {assertFalse(Utility.isValidName(null));}
    @Test
    void isValidName_checkName_validName() {
        assertTrue(Utility.isValidName("dani"));
    }

    @Test
    void isValidEmail_checkEmail_invalidEmail() {
        assertFalse(Utility.isValidEmail("ee"));
    }
    @Test
    void isValidEmail_checkEmail_invalidEmail1() {
        assertFalse(Utility.isValidEmail(null));
    }
    @Test
    void isValidEmail_checkEmail_validEmail() {assertTrue(Utility.isValidEmail("eli@gmail.com"));}
}