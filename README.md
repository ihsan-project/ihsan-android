# Khatm Android

## Environment
- Ruby 2.6.1 (as defined in `.ruby-version`)

### Setup

1. Follow instructions in `local.properties.copy` to setup local environments
1. Run Gradle->Tasks->Android->signingReport to get local SHA-1
1. Add the SHA-1 to Firebase. Go to Settings->General, scroll down and `Add fingerprint`
1. Install fastlane and other gems with with `bundle install`

### Building with fastlane

1. `bundle exec fastlane deploy`