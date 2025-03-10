import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import org.hamcrest.Matchers.not
import android.text.InputType

@RunWith(AndroidJUnit4::class)
class LoginTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testSuccessfulLogin() {
        onView(withId(R.id.username_edit_text)).perform(typeText("testUser"), closeSoftKeyboard())
        onView(withId(R.id.password_edit_text)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())

        onView(withText("Login Success!")).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidUsername() {
        onView(withId(R.id.username_edit_text)).perform(typeText("invalidUser"), closeSoftKeyboard())
        onView(withId(R.id.password_edit_text)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())

        onView(withText("Invalid credentials")).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidPassword() {
        onView(withId(R.id.username_edit_text)).perform(typeText("testUser"), closeSoftKeyboard())
        onView(withId(R.id.password_edit_text)).perform(typeText("wrongPassword"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())

        onView(withText("Invalid credentials")).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginButtonDisabledWhenFieldsAreEmpty() {
        onView(withId(R.id.username_edit_text)).perform(clearText(), closeSoftKeyboard())
        onView(withId(R.id.password_edit_text)).perform(clearText(), closeSoftKeyboard())

        onView(withId(R.id.login_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun testPasswordVisibilityToggle() {
        onView(withId(R.id.password_edit_text)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.toggle_password_visibility)).perform(click())

        onView(withId(R.id.password_edit_text)).check(matches(withInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)))
    }

    @Test
    fun testElementAccessibilityDescriptions() {
        onView(withId(R.id.username_edit_text)).check(matches(withContentDescription("Username Input")))
        onView(withId(R.id.password_edit_text)).check(matches(withContentDescription("Password Input")))
        onView(withId(R.id.login_button)).check(matches(withContentDescription("Login Button")))
    }

    @Test
    fun testClearFieldsOnLogout() {
        onView(withId(R.id.username_edit_text)).perform(typeText("testUser"), closeSoftKeyboard())
        onView(withId(R.id.password_edit_text)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.logout_button)).perform(click())

        onView(withId(R.id.username_edit_text)).check(matches(withText("")))
        onView(withId(R.id.password_edit_text)).check(matches(withText("")))
    }
}
