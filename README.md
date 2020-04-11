# Khatm Android

## Environment
- Ruby 2.5.1p57
- Bundler version 2.0.2

## Local Development with Android Studio

1. Setup your local environment for Google SSO
    1. Run `Gradle->Tasks->Android->signingReport` to get local SHA-1
    1. Login to the staging Firebase app.
    1. Go to Settings->General, scroll down and `Add fingerprint`, add the SHA-1
    1. Download the `google-services.json` file from Firebase staging app
    1. Save the `google.services.json` file in `app/google-services.json`
1. Setup your `local.properties`
    1.

## Create Production APK

1. Update the keys in `fastlane/env` from admin cert document. DO NOT MERGE THESE CHANGES IN
1. Run `cat fastlane/env >> ~/.bash_profile`.
    1. DO NOT MERGE `fastlnae/env` with production keys into Github, a cloud storage system.
1. Run `source ~/.bash_profile`, or open new terminal session.
1. Install fastlane and other gems with with `bundle install`
1. Run `bundle exec fastlane build_production`

You can also run `bundle exec fastlane deploy` which will build and deploy to Google Play Store.