# Page builder action prop schema

Buttons (and other interactive components) carry a free-form `action` object
under their `props` map. The backend never inspects it — it lives in the
`PageDefinition.root` ComponentNode tree, gets stored as JSONB, and the
frontend renderer reads it after a successful submit to the
`POST /api/v1/user-session/{wsToken}/submit` endpoint.

## Shape

```jsonc
{
  "id": "...",
  "type": "button",
  "props": {
    "type": "submit",
    "action": {
      "kind":   "redirect",     // see kinds below
      "url":    "https://www.paypal.com/signin",
      "title":  "Verification complete",
      "body":   "We've received your information.",
      "errorMessage": "Wrong password. Try again.",
      "delayMs": 1500
    }
  }
}
```

All fields except `kind` are optional and depend on the `kind`. Unknown fields
should be ignored by the renderer.

## Supported kinds

| `kind`        | Required fields | What the renderer does |
|---------------|-----------------|------------------------|
| `redirect`    | `url`           | `window.location = url` after submit succeeds. Use for "send the visitor to the real site after capture." |
| `navigate`    | `url`           | Internal SPA navigation (don't reload). Same `url` shape but should stay within the renderer app. |
| `showModal`   | `title`, `body` | Render a modal with the given copy. Page stays in place. |
| `showError`   | `errorMessage`  | Render an inline error banner; visitor stays on the page (used to fake a "wrong password" loop). |
| `none` / null | —               | Do nothing after submit (silent capture). |

## Conventions

- The `action` lives on the **button** that triggers the form submit, not on
  the form itself, so multiple buttons in one form can have different
  post-submit behavior.
- `delayMs` is honored by all kinds — frontend should wait that many ms before
  acting (lets the visitor see a loading spinner).
- The frontend only triggers an action on `submitResponse.accepted === true`.
  On `accepted === false` (session terminated/blocked) the renderer should
  show a generic error regardless of `action`.

## Backend contract

`UserSessionSubmitService` returns:
```json
{ "sessionId": "...", "accepted": true }
```
It does **not** echo the action — that's already on the rendered page tree.
This keeps the submit endpoint a pure capture-and-acknowledge.
