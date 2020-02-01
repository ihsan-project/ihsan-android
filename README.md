# Khatm Android

## Environment
- Ruby 2.6.1 (as defined in `.ruby-version`)

### Setup

1. Follow instructions in `local.properties.copy` to setup local environments
1. Run Gradle->Tasks->Android->signingReport to get local SHA-1
1. Add the SHA-1 to Firebase. Go to Settings->General, scroll down and `Add fingerprint`

## Building with fastlane

### Setup

1. Download `khatm-keystore` and `khatm-fastlane.json` from admin ENV bucket
1. Update the keys in `fastlane/env` from admin cert document. DO NOT MERGE THESE CHANGES IN
1. Run `cat fastlane/env >> ~/.bash_profile`. Undo changes to `fastlnae/env`
1. Run `source ~/.bash_profile`, or open new terminal session.
1. Install fastlane and other gems with with `bundle install`

### Run

1. Run `bundle exec fastlane deploy`