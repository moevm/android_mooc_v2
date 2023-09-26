FROM --platform=linux/amd64 ubuntu:22.04

LABEL maintainer="messeb"
    
ENV ANDROID_SDK_TOOLS_VERSION 9477386
ENV ANDROID_SDK_TOOLS_CHECKSUM bd1aa17c7ef10066949c88dc6c9c8d536be27f992a1f3b5a584f9bd2ba5646a0

ENV ANDROID_HOME "/opt/android-sdk-linux"
ENV ANDROID_SDK_ROOT $ANDROID_HOME
ENV ANDROID_AVD_HOME $ANDROID_HOME/.android/avd
ENV PATH $PATH:$ANDROID_HOME/cmdline-tools:$ANDROID_HOME/cmdline-tools/bin:$ANDROID_HOME/platform-tools

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG en_US.UTF-8

# Add base environment
RUN apt-get -qq update \
    && apt-get -qqy --no-install-recommends install \
    apt-utils=2.4.10 \
    openjdk-18-jdk=18.0.2+9-2~22.04 \
    openjdk-18-jre-headless=18.0.2+9-2~22.04 \
    software-properties-common=0.99.22.7 \
    build-essential=12.9ubuntu3 \
    libstdc++6=12.3.0-1ubuntu1~22.04 \
    libpulse0=1:15.99.1+dfsg1-1ubuntu2.1 \
    libglu1-mesa=9.0.2-1 \
    openssh-server=1:8.9p1-3ubuntu0.3 \
    unzip=6.0-26ubuntu3.1 \
    curl=7.81.0-1ubuntu1.13 \
    lldb=1:14.0-55~exp2 \
    git=1:2.34.1-1ubuntu1.10 > /dev/null \
    vim=2:8.2.3995-1ubuntu2.11 \
    tmux=3.2a-4ubuntu0.2 \
    sudo=1.9.9-1ubuntu2.4
   
# Add virtualization for emulator
RUN apt-get install -y \
	qemu-block-extra=1:6.2+dfsg-2ubuntu6.12 \
	qemu-system-common=1:6.2+dfsg-2ubuntu6.12 \
	qemu-system-data=1:6.2+dfsg-2ubuntu6.12 \
	qemu-system-x86=1:6.2+dfsg-2ubuntu6.12 \
	qemu-utils=1:6.2+dfsg-2ubuntu6.12 \
	libvirt-clients=8.0.0-1ubuntu7.6 \
	libvirt-daemon-system=8.0.0-1ubuntu7.6 \
	bridge-utils=1.7-1ubuntu3

# Clean cache
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Download and unzip Android SDK Tools
RUN curl -s https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS_VERSION}_latest.zip > /tools.zip \
    && echo "$ANDROID_SDK_TOOLS_CHECKSUM ./tools.zip" | sha256sum -c \
    && unzip -qq /tools.zip -d $ANDROID_HOME \
    && rm -v /tools.zip

# Accept licenses
RUN mkdir -p $ANDROID_HOME/licenses/ \
    && echo "8933bad161af4178b1185d1a37fbf41ea5269c55\nd56f5187479451eabf01fb78af6dfcb131a6481e\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > $ANDROID_HOME/licenses/android-sdk-license \
    && echo "84831b9409646a918e30573bab4c9c91346d8abd\n504667f4c0de7af1a06de9f4b1727b84351f2910" > $ANDROID_HOME/licenses/android-sdk-preview-license --licenses \
    && yes | $ANDROID_HOME/cmdline-tools/bin/sdkmanager --licenses --sdk_root=${ANDROID_SDK_ROOT}

# Add non-root and root users
RUN groupadd -r mobiledevops \
    && useradd --no-log-init -r -g mobiledevops mobiledevops \
    && echo "mobiledevops:root" | chpasswd \
    && adduser mobiledevops sudo \
    && adduser mobiledevops libvirt \
    && adduser mobiledevops kvm \
    && mkdir -p /home/mobiledevops/.android \
    && mkdir -p /home/mobiledevops/app \
    && touch /home/mobiledevops/.android/repositories.cfg \
    && chown --recursive mobiledevops:mobiledevops /home/mobiledevops \
    && chown --recursive mobiledevops:mobiledevops /home/mobiledevops/app \
    && chown --recursive mobiledevops:mobiledevops $ANDROID_HOME

# Set non-root user as default      
ENV HOME /home/mobiledevops
USER mobiledevops
WORKDIR $HOME/app

# Install Android packages
ADD packages.txt $HOME
RUN $ANDROID_HOME/cmdline-tools/bin/sdkmanager --update  --sdk_root=${ANDROID_SDK_ROOT} \
    && while read -r pkg; do PKGS="${PKGS}${pkg} "; done < $HOME/packages.txt \
    && $ANDROID_HOME/cmdline-tools/bin/sdkmanager $PKGS > /dev/null --sdk_root=${ANDROID_SDK_ROOT} \
    && rm $HOME/packages.txt
    
    
# Download android-29 system-image
RUN mkdir $ANDROID_HOME/cmdline-tools-temp \
	 && mv $ANDROID_HOME/cmdline-tools $ANDROID_HOME/cmdline-tools-temp \
	 && mv $ANDROID_HOME/cmdline-tools-temp/cmdline-tools $ANDROID_HOME/cmdline-tools-temp/latest \
	 && mv $ANDROID_HOME/cmdline-tools-temp $ANDROID_HOME/cmdline-tools
RUN $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "system-images;android-29;google_apis;x86"

