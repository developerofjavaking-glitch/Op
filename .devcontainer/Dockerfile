FROM mcr.microsoft.com/devcontainers/java:17-bullseye

# Android SDK Environment Variables
ENV ANDROID_SDK_ROOT=/usr/local/android-sdk
ENV ANDROID_HOME=$ANDROID_SDK_ROOT
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools

# Dependencies & Android Command-line Tools Install
RUN apt-get update && apt-get install -y wget unzip curl git && \
    mkdir -p $ANDROID_SDK_ROOT/cmdline-tools && \
    cd $ANDROID_SDK_ROOT/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O commandlinetools.zip && \
    unzip commandlinetools.zip && \
    rm commandlinetools.zip && \
    mv cmdline-tools latest

# Accept Licenses & Install Build Tools / Platforms
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
