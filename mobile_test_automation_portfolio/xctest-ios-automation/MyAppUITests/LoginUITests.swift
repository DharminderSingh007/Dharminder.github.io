import XCTest

class LoginUITests: XCTestCase {

    let app = XCUIApplication()

    // MARK: - Setup & Teardown

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app.launch()
    }

    override func tearDown() {
        app.terminate()
        super.tearDown()
    }

    // MARK: - Basic Tests

    func testLogin() {
        let usernameTextField = app.textFields["username"]
        usernameTextField.tap()
        usernameTextField.typeText("testUser")

        let passwordSecureTextField = app.secureTextFields["password"]
        passwordSecureTextField.tap()
        passwordSecureTextField.typeText("password123")

        app.buttons["login"].tap()
        XCTAssertTrue(app.staticTexts["Welcome, testUser"].exists)
    }

    // MARK: - Advanced Locator Strategies

    func testAdvancedLocatorStrategies() {
        let forgotPasswordLink = app.links.element(matching: NSPredicate(format: "label CONTAINS 'Forgot'"))
        XCTAssertTrue(forgotPasswordLink.exists)

        let signUpButton = app.buttons.matching(NSPredicate(format: "label == %@ AND isEnabled == YES", "Sign Up")).element
        XCTAssertTrue(signUpButton.exists)

        let googleButton = app.otherElements["social-login-section"].buttons["google"]
        XCTAssertTrue(googleButton.exists)

        app.images["help_icon"].tap()
        XCTAssertTrue(app.sheets.element.waitForExistence(timeout: 3))
    }

    // MARK: - Data-Driven Tests

    func testDataDrivenLoginAttempts() {
        let testCases = [
            ("", "", "Username and password required"),
            ("invalid@user", "pass", "Invalid credentials"),
            ("locked_user", "correct", "Account locked")
        ]

        for (username, password, errorMessage) in testCases {
            let usernameField = app.textFields["username"]
            let passwordField = app.secureTextFields["password"]

            usernameField.tap()
            usernameField.clearText()
            usernameField.typeText(username)

            passwordField.tap()
            passwordField.clearText()
            passwordField.typeText(password)

            app.buttons["login"].tap()

            let errorMessage = app.staticTexts["error_message"]
            XCTAssertTrue(errorMessage.waitForExistence(timeout: 2))
            XCTAssertEqual(errorMessage.label, testCases.first(where: { $0.0 == username })?.2)
        }
    }

    // MARK: - Gesture & Interaction Tests

    func testAdvancedGestures() {
        app.buttons["terms_and_conditions"].tap()
        let termsTextView = app.textViews["terms_content"]
        XCTAssertTrue(termsTextView.exists)

        (1...3).forEach { _ in termsTextView.swipeUp(); sleep(1) }
        termsTextView.pinch(withScale: 1.5, velocity: 1)
        termsTextView.press(forDuration: 2.0)
        XCTAssertTrue(app.menuItems["Copy"].waitForExistence(timeout: 2))
        app.coordinate(withNormalizedOffset: CGVector(dx: 0.1, dy: 0.1)).tap()
        termsTextView.doubleTap()
        app.navigationBars.buttons.element(boundBy: 0).tap()
    }

    // MARK: - Performance Tests

    func testLoginPerformance() {
        measure {
            app.launch()
            app.textFields["username"].tap()
            app.textFields["username"].typeText("performanceUser")
            app.secureTextFields["password"].tap()
            app.secureTextFields["password"].typeText("perfTest123")
            app.buttons["login"].tap()
            XCTAssertTrue(app.tabBars["main_tab_bar"].waitForExistence(timeout: 5))
        }
    }

    // MARK: - Accessibility Tests

    func testAccessibilityFeatures() {
        let elements = [
            app.textFields["username"],
            app.secureTextFields["password"],
            app.buttons["login"]
        ]
        elements.forEach { element in
            XCTAssertTrue(element.exists)
            XCTAssertFalse(element.label.isEmpty)
        }
    }

    // MARK: - Helpers & Extensions

    private func measureDashboardLoadTime() -> TimeInterval {
        let startTime = Date()
        app.buttons["dashboard_refresh"].tap()
        XCTAssertTrue(app.staticTexts["data_timestamp"].waitForExistence(timeout: 10))
        return Date().timeIntervalSince(startTime)
    }
}

extension XCUIElement {
    func clearText() {
        guard let stringValue = self.value as? String else { return }
        tap()
        typeText(String(repeating: XCUIKeyboardKey.delete.rawValue, count: stringValue.count))
    }
}
