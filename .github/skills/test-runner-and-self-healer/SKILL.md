---
name: test-runner-and-self-healer
description: "Run Maven Selenium TestNG test cases, analyze pass/fail status, diagnose failures, and propose or apply fixes only after asking the user for approval. Use when the user asks to run tests, execute a specific TestNG class or method, check pass/fail results, diagnose Selenium failures, inspect Surefire reports, or fix test problems in this project."
---

# Test Runner and Self Healer

Run and analyze the project's Java, Selenium, and TestNG tests when the user mentions a test name, test class, or test file. When a failure is found, identify the likely root cause and propose the smallest fix, but never change project files until the user explicitly approves the proposed change.

## Project Context

- Build tool: Maven
- Test framework: TestNG
- Browser automation: Selenium WebDriver
- Java release: 17
- Test suite: `src/test/resources/testng.xml`
- Test source root: `src/test/java`
- Reports: `target/surefire-reports`

## Workflow

1. Confirm the current working directory is the Maven project root containing `pom.xml`.
2. Detect the user's requested target:
   - A method name such as `loginPageOpens`: search the test source tree, identify the owning class, package, and method.
   - A class name such as `LoginPage`: search for the Java class and read its package declaration.
   - A file path such as `src/test/java/TestClasses/LoginPage.java`: read the file and extract its package and test methods.
3. If the target is ambiguous, report the matching files/classes and ask which one to run. Do not guess.
4. Choose the narrowest command that satisfies the request:

   Full configured TestNG suite:

   ```powershell
   mvn test
   ```

   Specific TestNG class:

   ```powershell
   mvn test "-Dtest=fully.qualified.TestClass"
   ```

   Specific TestNG method:

   ```powershell
   mvn test "-Dtest=fully.qualified.TestClass#methodName"
   ```

   Visible Chrome:

   ```powershell
   mvn test "-Dtest=fully.qualified.TestClass" "-Dheadless=false"
   ```

5. Preserve the complete Maven exit code and console output.
6. Read the relevant files under `target/surefire-reports`, especially `testng-results.xml` and the class report, when the command fails or reports zero tests.
7. Summarize the result with the command, counts, failures, root-cause category, key exception, and smallest recommended next action.
8. State clearly whether the result is `PASS`, `FAIL`, `NO TESTS DISCOVERED`, or `BLOCKED`.
9. If the result is `FAIL` and the cause is actionable, do not finish after reporting the failure. You must provide a concrete proposed fix with the exact file and intended edit, then ask the user for explicit yes/no approval.

The skill must execute the resolved test command itself. Do not only provide a command to the user unless execution is blocked by a missing prerequisite or the user explicitly asks for command-only instructions.

## Approval-Gated Repair Workflow

When the test fails:

1. Inspect the current source, Maven configuration, TestNG suite, and relevant Surefire report.
2. Determine whether the failure is caused by test code, framework configuration, environment, or application code.
3. Prepare a concise repair proposal containing the file(s), exact problem, smallest intended change, and validation command.
4. Your response must include the repair proposal and then stop to ask for explicit approval before editing. Never return only the exception, failure location, or root-cause category when an actionable fix can be proposed:

   ```text
   Proposed fix: <exact change and file>
   Validation: <command to run afterward>
   Apply this fix? (yes/no)
   ```

   Accept approval only when the user clearly agrees, such as `yes`, `approve`, or `apply the fix`. Do not treat `run`, `execute`, `analyze`, or `fix the failure` in the original request as approval to edit.
5. If approval is granted, make only the proposed changes, rerun the same focused test, and report the new result.
6. If approval is not granted, make no edits and report the recommended manual fix.

For the current LoginPage example, a valid proposal would identify that `By.id("aname")` is absent from the target page and propose changing it to the confirmed field locator, such as `By.id("name")`, before asking `Apply this fix? (yes/no)`.

Never infer approval from a request to run or analyze tests. Running tests is allowed; editing files requires a separate explicit approval.

Do not repeatedly change code after a failed repair. After at most two repair attempts, stop and report the remaining failure and evidence.

## Failure Classification

Use these categories:

- **Compilation**: `COMPILATION ERROR`, `cannot be resolved`, syntax errors, or test compilation failures.
- **Discovery**: zero tests, wrong package, wrong class name, invalid TestNG XML, or excluded groups.
- **Fixture/setup**: failures in `@BeforeMethod`, `@BeforeClass`, driver creation, or a null WebDriver caused by lifecycle misuse.
- **Browser/environment**: ChromeDriver, browser startup, network, timeout, or unavailable remote endpoint errors.
- **Assertion**: `AssertionError` or TestNG assertion mismatch.
- **Locator/interaction**: `NoSuchElementException`, stale elements, click interception, invalid selector, or input interaction failures.
- **Unknown**: preserve the original exception and recommend inspecting the first relevant stack-trace frame.

Do not call a test `PASS` solely because Maven exits successfully: `Tests run: 0` is `NO TESTS DISCOVERED`.

## TestNG/Selenium Checks

When a Selenium test extends a base fixture:

- Verify setup and teardown use TestNG annotations such as `@BeforeMethod` and `@AfterMethod`.
- Do not recommend manually calling `setUp()` from the test unless the project intentionally uses that design.
- Check that the test class has the correct package declaration and is under `src/test/java`.
- Check that the TestNG suite package or class includes the requested test.
- Treat CDP version warnings as warnings unless the browser session actually fails.
- Distinguish missing imports and compilation problems from runtime locator failures.

## Output Format

Use this concise format:

```text
Status: PASS | FAIL | NO TESTS DISCOVERED | BLOCKED
Command: <command>
Results: <total> total, <passed> passed, <failed> failed, <skipped> skipped

Failures:
- <test method> (<file and line if available>): <category> - <root cause>

Proposed fix: <exact file and code/config change, or "none" if the failure is environmental or needs user information>
Validation: <command to run afterward>
Apply this fix? (yes/no)
```

For a passing run, omit the `Failures` section. For a blocked run, include the missing prerequisite, such as Maven, JDK, Chrome, or network access.
