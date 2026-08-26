---
name: fill-locators
description: "Inspect Java Selenium tests, discover missing or invalid WebDriver locators from the target page, propose locator changes, and edit only after explicit user approval. Use when the user asks to fill locators, find locators, complete WebElement declarations, or repair Selenium selectors."
---

# Fill Selenium Locators

Inspect the requested Java Selenium test and fill missing, placeholder, or invalid locator expressions. Never modify project files until the user explicitly approves the complete locator proposal.

## Workflow

1. Resolve the requested Java test from its method name, class name, or file path.
2. Read the complete test and list every missing, placeholder, or failing locator.
3. Resolve the page URL from the base fixture, configuration, or test code.
4. Inspect the live page using the browser or page HTML. Do not invent locator values from variable names alone.
5. Match each requested element to a real page element and prefer stable locators in this order: unique `id`, stable `name`, unique CSS selector, then XPath.
6. Verify that each proposed locator matches the intended element and does not match multiple elements.
7. Present every proposed change and stop for approval:

   ```text
   Proposed locator fixes:
   - name: By.id("name")
   - email: By.id("email")
   - phone: By.id("phone")

   File: <test file>
   Validation: mvn test "-Dtest=<fully.qualified.TestClass#method>"
   Apply these locator fixes? (yes/no)
   ```

8. Accept only explicit approval such as `yes`, `approve`, or `apply the fix`. Do not infer approval from the request to inspect or fill locators.
9. After approval, add required Selenium imports and replace only the incomplete or incorrect locator expressions. Preserve existing assertions and actions.
10. Run the focused Maven/TestNG test and report which locator fixes passed or failed.

## Locator Rules

- Use `By.id("value")` when the target has a unique stable `id`.
- Use `By.name("value")` when `name` is stable and unique.
- Use `By.cssSelector("selector")` for a stable CSS relationship.
- Use XPath only when the other locator types are unavailable or insufficient.
- Do not add hard-coded ChromeDriver paths.
- Do not remove assertions or rewrite unrelated test logic.
- If the page cannot be inspected, ask for the URL, HTML, or screenshot instead of guessing.

## Output

Report the target test, discovered elements, proposed locators, approval status, changed file, validation command, and final test result.
