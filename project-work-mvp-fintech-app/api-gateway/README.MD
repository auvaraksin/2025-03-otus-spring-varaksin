#!/bin/bash

# СЦЕНАРИЙ ТЕСТИРОВАНИЯ USER-SERVICE, ACCOUNT-SERVICE ЧЕРЕЗ API-GATEWAY С ИСПОЛЬЗОВАНИЕМ CURL
# Перед началом тестирования необходимо выполнить:
# 1. Запустить образ user-service в Docker для загрузки БД PostgreSQL и Redis
# 2. Запустить образ account-service в Docker для загрузки БД PostgreSQL
# 3. Запустить user-service в Idea
# 4. Запустить account-service в Idea
# 5. Запустить API-gateway в Idea

# 1. Проверка, зарегистрирован ли пользователь
echo ""
echo "🔍 1. Проверка регистрации пользователя..."
curl -X POST http://localhost:8080/api/users/public/check-registration \
-H "Content-Type: application/json" \
-d '{"mobilePhone": "79114649999"}' \
-w "\n📊 Status: %{http_code}\n"

# 2. Регистрация пользователя с конкретными данными
echo ""
echo "📝 2. Регистрация нового пользователя..."
REGISTRATION_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users/public/registration \
-H "Content-Type: application/json" \
-d '{
"firstName": "Алексей",
"lastName": "Вараксин",
"middleName": "Николаевич",
"mobilePhone": "79114649999",
"email": "alex123@example.com",
"password": "SecurePass123!",
"passportNumber": "0987654321",
"issuedBy": "ОУФМС России по г. Москве",
"issueDate": "2018-04-25",
"departmentCode": "770098",
"birthDate": "1985-12-15",
"country": "Россия",
"city": "Москва",
"street": "Ленинский проспект",
"house": "45",
"hull": "2",
"flat": "78",
"postCode": "119334"
}')

echo "📄 Ответ регистрации: $REGISTRATION_RESPONSE"

# Извлечь clientId из ответа
CLIENT_ID=$(echo $REGISTRATION_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "🆔 Client ID: $CLIENT_ID"

# 3. Авторизация пользователя
echo ""
echo "🔑 3. Авторизация пользователя..."
AUTH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users/public/authorization \
-H "Content-Type: application/json" \
-d '{
"mobilePhone": "79114649999",
"password": "SecurePass123!"
}')

echo "📄 Ответ авторизации: $AUTH_RESPONSE"

# Извлечь токены из ответа
ACCESS_TOKEN=$(echo $AUTH_RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
REFRESH_TOKEN=$(echo $AUTH_RESPONSE | grep -o '"refreshToken":"[^"]*' | cut -d'"' -f4)
RESPONSE_CLIENT_ID=$(echo $AUTH_RESPONSE | grep -o '"clientId":"[^"]*' | cut -d'"' -f4)

echo "🔐 Access Token: ${ACCESS_TOKEN:0:50}..."
echo "🔄 Refresh Token: ${REFRESH_TOKEN:0:50}..."
echo "🆔 Client ID из ответа: $RESPONSE_CLIENT_ID"

# Сохранить токен в переменную окружения для последующих запросов
export ACCESS_TOKEN=$ACCESS_TOKEN

# 4. Создание OTP кода (защищенный эндпоинт)
echo ""
echo "📱 4. Создание OTP кода (проверка защищенного эндпоинта)..."
curl -X POST http://localhost:8080/api/users/auth/otp/creation \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{"mobilePhone": "79114649999"}' \
-w "\n📊 Status: %{http_code}\n"

echo ""
echo "=== ПРОВЕРКА ЛОГОВ GATEWAY ==="
echo "📋 В логах API Gateway ищите следующие сообщения:"
echo "   - '🔍 Extracted clientId from token: ...'"
echo "   - '👤 Added X-User-Id header: ...'"
echo "   - В ObservedRequestHttpHeadersFilter должен быть 'X-User-Id: ...'"

# 5. Проверка OTP кода
# ВНИМАНИЕ! В поле otpCode заменить значние "123456" на значение из вывода в консоли для user-service
echo ""
echo "🔢 5. Проверка OTP кода..."
echo "⚠️  ВНИМАНИЕ! Замените '123456' на реальный OTP код из логов user-service"
curl -X POST http://localhost:8080/api/users/auth/otp/verification \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{
"mobilePhone": "79114649999",
"otpCode": "123456"
}' \
-w "\n📊 Status: %{http_code}\n"

echo ""
echo "=== ПРОВЕРКА ЛОГОВ USER SERVICE ==="
echo "📋 В логах User Service ищите:"
echo "   - Заголовок 'X-User-Id: 553f994f-e2ab-471f-a273-574c32dfe7f9'"
echo "   - Или добавьте в User Service контроллер логирование:"
echo "     log.info('Received X-User-Id: {}', request.getHeader('X-User-Id'));"

# 6. Тестирование безопасности
echo ""
echo "🔒 6. Тестирование безопасности JWT фильтра..."

echo ""
echo "🚫 6.1. Тестирование с неверным токеном..."
curl -X POST http://localhost:8080/api/users/auth/otp/creation \
-H "Content-Type: application/json" \
-H "Authorization: Bearer invalid_token_12345" \
-d '{"mobilePhone": "79114649999"}' \
-w "\n📊 Status: %{http_code}\n"

echo ""
echo "🚫 6.2. Тестирование без токена..."
curl -X POST http://localhost:8080/api/users/auth/otp/creation \
-H "Content-Type: application/json" \
-d '{"mobilePhone": "79114649999"}' \
-w "\n📊 Status: %{http_code}\n"

# 7. Проверка публичных эндпоинтов
echo ""
echo "🌐 7. Проверка публичных эндпоинтов (должны работать без токена)..."

echo ""
echo "🔍 7.1. Проверка регистрации..."
curl -X POST http://localhost:8080/api/users/public/check-registration \
-H "Content-Type: application/json" \
-d '{"mobilePhone": "79114649999"}' \
-w "\n📊 Status: %{http_code}\n"

echo ""
echo "🏥 7.2. Проверка health-check эндпоинтов..."
curl -X GET http://localhost:8080/actuator/health \
-w "\n📊 Status: %{http_code}\n"

# 8. ТЕСТИРОВАНИЕ ACCOUNT-SERVICE ЧЕРЕЗ API-GATEWAY
echo ""
echo "=== ТЕСТИРОВАНИЕ ACCOUNT-SERVICE ==="

# Использовать тестовые данные из скриптов миграции
TEST_CLIENT_ID="a1b2c3d4-5e6f-4a8b-9c0d-a1b2c3d4e5f6"
TEST_ACCOUNT_ID="311e4567-e89b-12d3-a456-426614174001"
TEST_CONDITIONS_ID="111e4567-e89b-12d3-a456-426614174001"

# Авторизация с тестовым пользователем из миграции
echo ""
echo "🔑 8.1. Авторизация тестового пользователя из миграции..."
TEST_AUTH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users/public/authorization \
-H "Content-Type: application/json" \
-d '{
"mobilePhone": "79101234567",
"password": "SecurePass123!"
}')

echo "📄 Ответ авторизации: $TEST_AUTH_RESPONSE"

# Извлечь токен для тестового пользователя
TEST_ACCESS_TOKEN=$(echo $TEST_AUTH_RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
echo "🔐 Test Access Token: ${TEST_ACCESS_TOKEN:0:50}..."

export TEST_ACCESS_TOKEN=$TEST_ACCESS_TOKEN

# 8.2. Получение списка счетов пользователя ✅ СООТВЕТСТВУЕТ
echo ""
echo "📋 8.2. Получение списка счетов пользователя..."
curl -X GET http://localhost:8080/api/accounts/auth/accounts \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TEST_ACCESS_TOKEN" \
-H "clientId: $TEST_CLIENT_ID" \
-w "\n📊 Status: %{http_code}\n"

# 8.3. Создание нового счета ❌ ИСПРАВЛЕНО
echo ""
echo "➕ 8.3. Создание нового счета..."
curl -X POST http://localhost:8080/api/accounts/auth/accounts \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TEST_ACCESS_TOKEN" \
-H "clientId: $TEST_CLIENT_ID" \
-d '{
"type": "CURRENT",
"currency": "RUB",
"conditionsId": "'$TEST_CONDITIONS_ID'"
}' \
-w "\n📊 Status: %{http_code}\n"

# 8.4. Просмотр информации о конкретном счете ✅ СООТВЕТСТВУЕТ
echo ""
echo "👁️  8.4. Просмотр информации о счете..."
curl -X GET "http://localhost:8080/api/accounts/auth/accounts/$TEST_ACCOUNT_ID" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TEST_ACCESS_TOKEN" \
-w "\n📊 Status: %{http_code}\n"

# 8.5. Просмотр истории операций по счету ✅ СООТВЕТСТВУЕТ
echo ""
echo "📊 8.5. Просмотр истории операций по счету..."
curl -X GET http://localhost:8080/api/accounts/auth/accounts/transactions \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TEST_ACCESS_TOKEN" \
-H "accountId: $TEST_ACCOUNT_ID" \
-w "\n📊 Status: %{http_code}\n"

# 8.6. Тестирование безопасности account-service ✅ СООТВЕТСТВУЕТ
echo ""
echo "🔒 8.6. Тестирование безопасности account-service..."

echo ""
echo "🚫 8.6.1. Попытка доступа без токена..."
curl -X GET http://localhost:8080/api/accounts/auth/accounts \
-H "Content-Type: application/json" \
-w "\n📊 Status: %{http_code}\n"

echo ""
echo "🚫 8.6.2. Попытка доступа с неверным токеном..."
curl -X GET http://localhost:8080/api/accounts/auth/accounts \
-H "Content-Type: application/json" \
-H "Authorization: Bearer invalid_token_12345" \
-H "clientId: $TEST_CLIENT_ID" \
-w "\n📊 Status: %{http_code}\n"

echo ""
echo "🚫 8.6.3. Попытка доступа к чужому счету..."
curl -X GET "http://localhost:8080/api/accounts/auth/accounts/311e4567-e89b-12d3-a456-426614174002" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TEST_ACCESS_TOKEN" \
-w "\n📊 Status: %{http_code}\n"

# 9. Тестирование с другими тестовыми пользователями из миграции
echo ""
echo "👥 9. Тестирование с другими пользователями из миграции..."

# Авторизация второго тестового пользователя
echo ""
echo "🔑 9.1. Авторизация второго тестового пользователя..."
TEST_AUTH_RESPONSE_2=$(curl -s -X POST http://localhost:8080/api/users/public/authorization \
-H "Content-Type: application/json" \
-d '{
"mobilePhone": "79109876543",
"password": "ASDQWEtest123?!"
}')

TEST_ACCESS_TOKEN_2=$(echo $TEST_AUTH_RESPONSE_2 | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
echo "🔐 Test Access Token 2: ${TEST_ACCESS_TOKEN_2:0:50}..."

# Получение счетов второго пользователя
echo ""
echo "📋 9.2. Получение счетов второго пользователя..."
curl -X GET http://localhost:8080/api/accounts/auth/accounts \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TEST_ACCESS_TOKEN_2" \
-H "clientId: b2c3d4e5-6f7a-4b8c-9d0e-b2c3d4e5f6a7" \
-w "\n📊 Status: %{http_code}\n"

echo ""
echo "=== ПРОВЕРКА ЛОГОВ ACCOUNT SERVICE ==="
echo "📋 В логах Account Service ищите:"
echo "   - Заголовок 'clientId: ...'"
echo "   - Логи обработки запросов на создание и получение счетов"
echo "   - Проверку прав доступа к счетам"

echo ""
echo "🎉 ТЕСТИРОВАНИЕ ЗАВЕРШЕНО!"