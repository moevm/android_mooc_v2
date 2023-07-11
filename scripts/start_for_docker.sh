# Sudo password
password=root

# Check sudo
sudo -n true
if [ $? -eq 0 ]; then
    echo "User has sudo-permission"
else
    echo "User has no sudo-permission"
    echo $password | sudo -i -S
fi

# Start building
echo "\n"
echo "Build started\n"
sudo ./gradlew :app:assembleDebug
echo "Build finished\n"

# Start testing
echo "UnitTest started\n"
sudo ./gradlew :app:testDebugUnitTest
echo "UnitTest finished\n"

