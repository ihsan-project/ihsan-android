# Ihsan Android

- [The Ihsan Project](https://ihsanproject.com/)
- [The Android Architecture](https://github.com/ihsan-project/ihsan-android/wiki/Architecture)

## Environment
- Ruby 2.5.1p57 - As per `.ruby-version`
- Bundler version 2.0.2

## Local Development with Android Studio

1. Run `Gradle->Tasks->Android->signingReport` to get local SHA-1.
1. Give this SHA-1 to the Ihsan Admin and wait for them to add your fingerprint.
1. Once the Admin has added you, they will provide you with the following:
    - AWS_ACCESS_KEY_ID
    - AWS_SECRET_ACCESS_KEY
    - AWS_CERT_BUCKET
    - AWS_BUCKET_REGION
1. Make sure you have fastlane installed with `bundle install`
1. Run the fastlane command `AWS_ACCESS_KEY_ID=[ENTER VALUE] AWS_SECRET_ACCESS_KEY=[ENTER VALUE] AWS_CERT_BUCKET=[ENTER VALUE] AWS_BUCKET_REGION=[ENTER VALUE] bundle exec fastlane setup_development`

### ngrok to use local instance of api server
[ngrok](https://ngrok.com/) allows you to create a public tunnel to your local computer. This way if you are running the [Ihsan Hapi API](https://github.com/ihsan-project/ihsan-api-hapi/wiki/Architecture) server on your computer and it's listening on `http://localhost:3000`, you can create a public tunnel using `ngrok http 3000` and ngrok will provide you a public HTTPS URL that forwards any requests directly to your local server.

1. Open this project in Android Studio and let it sync.
1. Once complete, you should see a `/local.properties` file in the root of this project
1. Append the following line to the end `api_url="[Enter HTTPS ngrok url]"` (The quotes `"` are important or it won't be interpreted as a string)

## Create Production APK

1. Update the keys in `fastlane/env` from admin cert document and save.
1. Run `cat fastlane/env >> ~/.bash_profile` or `cat fastlane/env >> ~/.zshrc`.
    1. DO NOT MERGE `fastlane/env` with production keys, undo the changes made.
1. Run `source ~/.bash_profile`, or open new terminal session so updated bash settings are applied.
1. Install fastlane and other gems with with `bundle install`
1. Run `bundle exec fastlane build_production`

You can also run `bundle exec fastlane deploy` which will build and deploy to Google Play Store.
