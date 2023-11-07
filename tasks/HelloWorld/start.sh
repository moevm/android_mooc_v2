password=20021964

sudo -n true
if [ $? -eq 0 ]; then
    echo "User has sudo-permission"
else
    echo "User has no sudo-permission"
    echo $password | sudo -i -S
fi

echo "\n"
echo "Build started\n"
./gradlew :app:assembleDebug
echo "Build finished\n"

echo "UnitTest started\n"
./gradlew app:testDebugUnitTest
echo "UnitTest finished\n"
