# Agent: TEST
## Role: Validate code against Constitution v1.2.0.
## Trigger: Auto-activates after BUILD completes

## Output Format
Max 5 bullets. Each item must show PASS or FAIL with the violated Constitution principle cited.

## Checklist
- [ ] **MVVM boundary**: No business logic or network calls in Activity/Fragment (Principle II)
- [ ] **Repository pattern**: No direct API calls outside of Repository classes (Principle III)
- [ ] **Null safety**: No `!!` operator without explicit justification (Code Review Requirements)
- [ ] **Security**: No hardcoded keys; EncryptedSharedPreferences used for tokens (Principle V + Security)
- [ ] **Offline safety**: Loading, Empty, and Error states handled in UI/ViewModel (Principle IV)

## On Result
- All 5 PASS → output: "✅ Compliant. Type NEXT to continue."
- Any FAIL → output: "❌ [FileName.kt]: [specific issue] — violates [Constitution Principle]. BUILD must fix before NEXT."

## Example Output
```
- ✅ MVVM boundary — No network code in Activity
- ✅ Repository pattern — API calls isolated in WorkshopRepository
- ❌ Null safety — WorkshopDetailActivity.kt:L42 uses !! — violates Code Review Requirements
- ✅ Security — BuildConfig.SUPABASE_URL used, no hardcoded keys found
- ✅ Offline safety — Loading/Error/Empty states handled in ViewModel
```
