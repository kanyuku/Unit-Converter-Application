# Play Store release checklist

## Release build and signing

The app targets and compiles against API 36. Each release must use a unique,
increasing `versionCode`; the next configured release is version code 2,
version name 1.1.

Use Google Play App Signing and a dedicated **upload key**. Keep the keystore
and its passwords outside the repository, maintain a secure backup, and never
send them in chat or commit them to Git. The app's Gradle configuration reads
these environment variables:

```text
UNIT_CONVERTER_UPLOAD_STORE_FILE
UNIT_CONVERTER_UPLOAD_STORE_PASSWORD
UNIT_CONVERTER_UPLOAD_KEY_ALIAS
UNIT_CONVERTER_UPLOAD_KEY_PASSWORD
```

Set all four variables in the local build environment, then create the signed
Android App Bundle:

```sh
./gradlew :app:bundleRelease
```

The bundle is written to `app/build/outputs/bundle/release/app-release.aab`.
Release tasks deliberately fail if signing is absent or only partly configured.
Do not upload an unsigned build. If you do not have an upload key yet, create one
locally with Android Studio or `keytool`, register it with Play Console, and
store its credentials in your secret manager or CI secret store.

Before the first Play upload, replace `com.example.unitconverter` in
`app/build.gradle` with the final unique application ID. An application ID
cannot be changed for an existing Play listing.

## Draft store listing

- **App name:** Unit Converter
- **Short description:** Convert everyday length, weight and volume units offline.
- **Full description:**

  Convert measurements quickly and clearly. Unit Converter handles everyday
  length, weight and volume conversions in one simple screen. Choose a
  measurement type, enter a value, select the units, and tap Convert.

  **Length:** millimetres, centimetres, metres, kilometres, inches, feet, yards
  and miles.

  **Weight:** milligrams, grams, kilograms, ounces and pounds.

  **Volume:** millilitres, litres, teaspoons, tablespoons, US cups and US gallons.

  Conversions run on your device. No account or internet connection is needed.

The listing and icon are drafts; review the final branding, localization, and
claims before publishing. The repository includes two 1080 × 2160 phone
screenshots (`play-store/assets/phone-length.png` and
`play-store/assets/phone-volume.png`) and a 512 × 512 PNG store icon at
`play-store/assets/app-icon-512.png`. Add them to the Play listing, and add a
feature graphic if you want one.

## Privacy policy and Play Console declarations

The app has no network permission, third-party SDKs, ads, account system, or
data collection code. Its conversion inputs are processed locally and not
stored by the app. Based on the current source, the Data safety draft is:

- Data collected: **No**
- Data shared: **No**
- Ads: **No**
- App access: no restricted access or account required
- Permissions: none declared by the app

Verify these answers against the exact artifact and all SDKs before submitting;
the developer is responsible for the declarations. Complete the content rating
questionnaire and select the target audience in Play Console as well.

Google Play requires a privacy policy URL on the store listing and a privacy
policy accessible from within the app, even when no user data is collected. The
app's Privacy policy link currently shows the policy in-app. Before release:

1. Replace the publisher/contact placeholder in the in-app policy and in
   `app/src/main/res/values/strings.xml` with real, monitored contact details.
2. Publish the matching policy on a publicly accessible HTTPS webpage (not a
   PDF and not behind a login).
3. Enter that URL in the Play Console privacy-policy declaration and listing.

The privacy-policy draft must not be published with placeholder contact details.

## Final checks

- Verify the signed AAB and test it using a Play internal testing track.
- Check the app on supported Android versions, screen sizes, and accessibility
  settings.
- Finish Play Console app-content declarations, store listing, screenshots,
  content rating, and release review.
