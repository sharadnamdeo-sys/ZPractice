# Test Runner and Self Healer

This document explains how the project was configured so Copilot can run Selenium/TestNG tests, analyze failures, and repair code only after receiving explicit approval.

## 1. Reload VS Code

After adding or changing a skill or custom agent, reload the VS Code window so Copilot discovers the project customization.

1. Press `Ctrl+Shift+P`.
2. Type `Developer: Reload Window`.
3. Select **Developer: Reload Window** and press `Enter`.
4. Wait for VS Code to reopen the workspace.

A Maven reload may also be needed after changing `pom.xml`:

1. Press `Ctrl+Shift+P`.
2. Run `Maven: Reload Projects`.

## 2. Project Structure

The project uses Maven, Java 17, Selenium WebDriver, and TestNG.

```text
.github/
  agents/
    test-runner-and-self-healer.agent.md
  skills/
    test-runner-and-self-healer/
      SKILL.md
src/
  test/
    java/
    resources/
      testng.xml
```

Tests belong below `src/test/java`, and their Java package must match the directory path. For example, `TestClasses.LoginPage` belongs in `src/test/java/TestClasses/LoginPage.java` and starts with:

```java
package TestClasses;
```

## 3. Create the Workspace Skill

A skill is an on-demand workflow stored in a `SKILL.md` file. It is not normally shown in the Agent dropdown; Copilot loads it when the request matches its description.

### How This Skill Was Created

The skill was created through an iterative Copilot Chat conversation rather than by writing the final workflow in one step:

1. The initial prompt described the desired behavior:

  ```text
  I want to create a skill that will execute my testcases and analyze pass/fail status.
  ```

2. The first version of the skill was created for this Maven, Selenium, Java, and TestNG project. It defined commands for running the full suite, a class, or a method, and instructions for reading Surefire reports.

3. The requirement was refined to include automatic repair:

  ```text
  I want a single skill that will execute the test, analyse possible failures and try to fix it on its own, but I want the skill to ask me before making any changes.
  ```

4. The skill was refined again so it could resolve and run a test from any of these inputs:

  ```text
  Run loginPageOpens.
  Run LoginPage.java.
  Run src/test/java/TestClasses/LoginPage.java.
  ```

5. The approval rule was clarified after a failure was only reported instead of receiving a repair proposal. The final behavior requires the skill to show the proposed file/code change and ask:

  ```text
  Proposed fix: <exact file and code/config change>
  Validation: <command to run afterward>
  Apply this fix? (yes/no)
  ```

6. The skill name was refined to **Test Runner and Self Healer**. The internal folder and identifier use hyphens because the skill is stored as `test-runner-and-self-healer`.

7. An extra `test-framework` skill was briefly created while organizing the project conventions. It was later removed, leaving one active project skill: `test-runner-and-self-healer`.

8. The workflow was validated by running `LoginPage` in headed mode, diagnosing an invalid locator, asking for approval, applying the approved locator change, and rerunning the focused test successfully.

Create:

```text
.github/skills/test-runner-and-self-healer/SKILL.md
```

The skill frontmatter identifies the skill and provides discovery keywords:

```yaml
---
name: test-runner-and-self-healer
description: "Run Maven Selenium TestNG test cases, analyze pass/fail status, diagnose failures, and propose or apply fixes only after asking the user for approval."
---
```

The workflow in this project skill does the following:

1. Resolves a test method, class, or Java file named by the user.
2. Reads the package and test method names before running.
3. Chooses the narrowest Maven command.
4. Executes the test itself.
5. Reads Maven/Surefire results when the test fails or discovers zero tests.
6. Classifies failures as compilation, discovery, fixture/setup, browser/environment, assertion, or locator/interaction failures.
7. Proposes a concrete fix when the failure is actionable.
8. Asks for explicit approval before changing any file.
9. Applies only the approved change and reruns the focused test.

The skill must never infer permission to edit from words such as `run`, `execute`, `analyze`, or `fix` in the original request.

## 4. Create the Custom Agent

A custom agent is the selectable entry in the Agent dropdown. Create:

```text
.github/agents/test-runner-and-self-healer.agent.md
```

Its frontmatter gives the dropdown entry its display name and description:

```yaml
---
name: Test Runner and Self Healer
description: Run Maven Selenium TestNG tests, analyze failures, and propose fixes after asking for approval.
argument-hint: Specify a test class, method, or Java test file to run.
---
```

The agent delegates the detailed behavior to the workspace skill:

```text
Use the workspace skill at:
.github/skills/test-runner-and-self-healer/SKILL.md

Follow that skill exactly.
```

This separation keeps the dropdown agent short while keeping the test workflow reusable and detailed.

## 5. Use the Agent

After reloading VS Code:

1. Open Copilot Chat.
2. Open the Agent dropdown.
3. Select **Test Runner and Self Healer**.
4. Name a test method, class, or file.

Examples:

```text
Run loginPageOpens1.
```

```text
Run LoginPage.java in headed mode.
```

```text
Run src/test/java/TestClasses/LoginPage.java and analyze any failures.
```

The agent resolves the current class and package. It should not guess if multiple files match the requested name.

## 6. Maven Commands Used by the Agent

Run the configured TestNG suite:

```powershell
mvn test
```

Run one class:

```powershell
mvn test "-Dtest=TestClasses.LoginPage"
```

Run one method:

```powershell
mvn test "-Dtest=TestClasses.LoginPage#loginPageOpens1"
```

Run with a visible Chrome browser:

```powershell
mvn test "-Dtest=TestClasses.LoginPage" "-Dheadless=false"
```

## 7. Approval-Gated Repair

When a test fails, the agent must first provide a result and a concrete proposal:

```text
Status: FAIL
Command: <command>
Results: <counts>

Failures:
- <test method>: <category> - <root cause>

Proposed fix: <exact file and code/config change>
Validation: <command to run afterward>
Apply this fix? (yes/no)
```

No file changes should happen until the user replies with explicit approval, for example:

```text
yes
```

```text
apply the fix
```

After approval, the agent makes only the proposed change, reruns the same focused test, and reports the new status. If the user says no, the agent leaves files unchanged.

## 8. Example From This Project

The `LoginPage` test originally used a locator for an element that was not present:

```java
By.id("aname")
```

The live page contained the field with id `name`, so the agent proposed changing it to:

```java
By.id("name")
```

Only after the user approved did the agent apply the change and rerun the headed test. The final result was one test passed with zero failures.

## 9. Troubleshooting Discovery

If the agent does not appear in the Agent dropdown:

1. Confirm the file ends in `.agent.md`.
2. Confirm it is under `.github/agents`.
3. Confirm the YAML frontmatter is between `---` markers.
4. Confirm `name`, `description`, and `argument-hint` are present.
5. Run `Developer: Reload Window`.

If a skill is not being used:

1. Confirm `SKILL.md` is under `.github/skills/<skill-name>/`.
2. Confirm the folder name matches the `name` value.
3. Use the skill's trigger terms in the request, such as `run test`, `analyze failure`, `LoginPage.java`, or `TestNG`.
4. Reload VS Code.

If Maven reports zero tests, check the test source path, Java package, TestNG annotations, and `src/test/resources/testng.xml` package/class selection.
