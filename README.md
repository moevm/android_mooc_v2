# android_mooc_v2

## Структура репозитория

### [base_docker_override](./base_docker_override)
Папка с изменённым файлом Dockerfile, который ранее должен был использоваться для создания докер-образа на основе [mobiledevops/android-sdk-image](https://hub.docker.com/r/mobiledevops/android-sdk-image)

### [plugin](./plugin)
Папка с будущим плагином для Android Studio, который будет позволять загружать курс "Разработка под Android" и автоматически проверять в нём задачи. Работает для версий от AI-223 до AI-232.*. 
[Инструкция к плагину](./plugin/README.md)


### [scripts](./scripts)
Папка со скриптом для запуска тестов внутри докера

### [tasks](./tasks)
Папка с задачами для тестирования работы плагина

### [wiki](./wiki)
Папка с информацией, касающейся разработки или предметной области
