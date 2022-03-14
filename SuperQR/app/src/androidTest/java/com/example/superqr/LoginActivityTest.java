package com.example.superqr;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LogInActivity> rule =
            new ActivityTestRule<>(LogInActivity.class, true, true);


    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Check if user is properly register
     */
    @Test
    public void checkRegisterUser() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);

        solo.clickOnButton("New User");

        //assertEquals(8, solo.getView(R.id.newUserButton).getVisibility());
        //assertEquals(8, solo.getView(R.id.existingUserButton).getVisibility());

        solo.enterText((EditText) solo.getView(R.id.userNameEditText), "IntentTest");
        solo.enterText((EditText) solo.getView(R.id.emailEditText), "IntentTest@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.phoneEditText), "0000000000");
        solo.clickOnButton("SIGN UP");

        assertTrue(solo.waitForActivity("ActivityTest", 5000));
        //Activity a = solo.getCurrentActivity();  // This works - a is "ActivityTest"
        solo.sleep(5000);

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        MainActivity mainActivity = (MainActivity) solo.getCurrentActivity();
        assertTrue(solo.waitForText("IntentTest", 1, 2000));
        assertTrue(solo.waitForText("IntentTest@gmail.com", 1, 2000));
        assertTrue(solo.waitForText("0000000000", 1, 2000));
    }


    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
