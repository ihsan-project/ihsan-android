# Khatm Android

## Environment
- Ruby 2.5.1p57 - As per `.ruby-version`
- Bundler version 2.0.2

## Local Development with Android Studio

1. Setup your local environment for Google SSO
    1. Run `Gradle->Tasks->Android->signingReport` to get local SHA-1.
    1. Give this SHA-1 to the Khatm Admin and wait for them to add your fingerprint.
    1. Once the Admin has added you, they will provide you with the following:
        - AWS_ACCESS_KEY_ID
        - AWS_SECRET_ACCESS_KEY
        - AWS_CERT_BUCKET
        - AWS_BUCKET_REGION
    1. Make sure you have fastlane installed with `bundle install`
    1. Run the fastlane command `AWS_ACCESS_KEY_ID=[ENTER VALUE] AWS_SECRET_ACCESS_KEY=[ENTER VALUE] AWS_CERT_BUCKET=[ENTER VALUE] AWS_BUCKET_REGION=[ENTER VALUE] bundle exec fastlane setup_development`
1. Setup your `local.properties`
    1.

## Create Production APK

1. Update the keys in `fastlane/env` from admin cert document and save.
1. Run `cat fastlane/env >> ~/.bash_profile`.
    1. DO NOT MERGE `fastlnae/env` with production keys. Undo the changes made.
1. Run `source ~/.bash_profile`, or open new terminal session.
1. Install fastlane and other gems with with `bundle install`
1. Run `bundle exec fastlane build_production`

You can also run `bundle exec fastlane deploy` which will build and deploy to Google Play Store.